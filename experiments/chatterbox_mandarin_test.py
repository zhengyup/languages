"""
Small experimental script for Mandarin TTS with Resemble AI's open-source
Chatterbox Multilingual model.

Install dependencies:
    pip install chatterbox-tts torchaudio

How to run:
    python experiments/chatterbox_mandarin_test.py

Recommended Python version:
    Python 3.11 is the safest choice for Chatterbox right now. Newer Python
    versions may work, but they are not the primary tested target in the
    official project.

Where the model is downloaded from:
    ChatterboxMultilingualTTS.from_pretrained(...) downloads the pretrained
    Resemble AI multilingual Chatterbox model from the official hosted weights
    used by the `chatterbox-tts` package and caches them locally.

How to change the text:
    Edit the `TEXT` constant below and keep `language_id="zh"` for Mandarin.
"""

from __future__ import annotations

import sys

import perth
import torch
import torch.nn.functional as F
import torchaudio as ta
from chatterbox.mtl_tts import (
    SUPPORTED_LANGUAGES,
    ChatterboxMultilingualTTS,
    T3Cond,
    drop_invalid_tokens,
    punc_norm,
)


TEXT = "你好，欢迎来到中文情景阅读器。"
OUTPUT_PATH = "mandarin_test.wav"
DEVICE = "cpu"
EXAGGERATION = 0.35
CFG_WEIGHT = 0.3
MAX_NEW_TOKENS = 180
MAX_SECONDS = 5.0
TRIM_THRESHOLD = 0.01
TAIL_PADDING_MS = 250


def patch_perth_watermarker_if_needed() -> None:
    """
    Work around environments where perth.PerthImplicitWatermarker is exposed
    as None. In that case, use the built-in dummy watermarker so the TTS test
    can still run.
    """
    if getattr(perth, "PerthImplicitWatermarker", None) is None:
        print(
            "PerthImplicitWatermarker is unavailable in this environment. "
            "Falling back to DummyWatermarker."
        )
        perth.PerthImplicitWatermarker = perth.DummyWatermarker


def trim_generated_audio(
    wav: torch.Tensor,
    sample_rate: int,
    threshold: float = TRIM_THRESHOLD,
    max_seconds: float = MAX_SECONDS,
    tail_padding_ms: int = TAIL_PADDING_MS,
) -> torch.Tensor:
    """
    Trim the common long trailing tail that sometimes appears with short
    multilingual Chatterbox generations.

    This keeps the script simple:
    - remove mostly-silent audio after the last strong sample
    - keep a small natural tail so the ending is not cut abruptly
    - cap the final duration as a safeguard for obviously overlong output
    """
    mono_wav = wav.squeeze(0)
    significant = torch.nonzero(torch.abs(mono_wav) > threshold, as_tuple=False)

    if significant.numel() > 0:
        last_index = int(significant[-1].item())
        padding_samples = int(sample_rate * tail_padding_ms / 1000)
        mono_wav = mono_wav[: min(last_index + padding_samples, mono_wav.numel())]

    max_samples = int(sample_rate * max_seconds)
    if mono_wav.numel() > max_samples:
        mono_wav = mono_wav[:max_samples]

    return mono_wav.unsqueeze(0)


def generate_short_utterance(
    model: ChatterboxMultilingualTTS,
    text: str,
    language_id: str,
    max_new_tokens: int = MAX_NEW_TOKENS,
    exaggeration: float = EXAGGERATION,
    cfg_weight: float = CFG_WEIGHT,
    temperature: float = 0.8,
    repetition_penalty: float = 2.0,
    min_p: float = 0.05,
    top_p: float = 1.0,
) -> torch.Tensor:
    """
    Chatterbox's built-in multilingual generate() currently uses a hardcoded
    max_new_tokens=1000. That is generous for long passages, but it often
    produces breathing, silence, or a short repeated fragment for very short
    utterances like this Mandarin test sentence.

    This helper follows the same official generation path, but caps the token
    budget to keep short-form synthesis tighter and more natural.
    """
    normalized_language_id = language_id.lower()
    if normalized_language_id not in SUPPORTED_LANGUAGES:
        supported_languages = ", ".join(sorted(SUPPORTED_LANGUAGES))
        raise ValueError(
            f"Unsupported language_id '{language_id}'. "
            f"Supported languages: {supported_languages}"
        )

    if model.conds is None:
        raise ValueError(
            "Model conditionals were not initialized. "
            "Load the model with from_pretrained() before generating."
        )

    if float(exaggeration) != float(model.conds.t3.emotion_adv[0, 0, 0].item()):
        current_conditionals: T3Cond = model.conds.t3
        model.conds.t3 = T3Cond(
            speaker_emb=current_conditionals.speaker_emb,
            cond_prompt_speech_tokens=current_conditionals.cond_prompt_speech_tokens,
            emotion_adv=exaggeration * torch.ones(1, 1, 1),
        ).to(device=model.device)

    normalized_text = punc_norm(text)
    text_tokens = model.tokenizer.text_to_tokens(
        normalized_text,
        language_id=normalized_language_id,
    ).to(model.device)
    text_tokens = torch.cat([text_tokens, text_tokens], dim=0)

    start_token = model.t3.hp.start_text_token
    stop_token = model.t3.hp.stop_text_token
    text_tokens = F.pad(text_tokens, (1, 0), value=start_token)
    text_tokens = F.pad(text_tokens, (0, 1), value=stop_token)

    with torch.inference_mode():
        speech_tokens = model.t3.inference(
            t3_cond=model.conds.t3,
            text_tokens=text_tokens,
            max_new_tokens=max_new_tokens,
            temperature=temperature,
            cfg_weight=cfg_weight,
            repetition_penalty=repetition_penalty,
            min_p=min_p,
            top_p=top_p,
        )
        speech_tokens = speech_tokens[0]
        speech_tokens = drop_invalid_tokens(speech_tokens).to(model.device)

        wav, _ = model.s3gen.inference(
            speech_tokens=speech_tokens,
            ref_dict=model.conds.gen,
        )
        wav = wav.squeeze(0).detach().cpu().numpy()
        watermarked_wav = model.watermarker.apply_watermark(wav, sample_rate=model.sr)

    return torch.from_numpy(watermarked_wav).unsqueeze(0)


def main() -> None:
    """Generate one short Mandarin utterance and save it as a WAV file."""
    patch_perth_watermarker_if_needed()

    print(f"Loading Chatterbox Multilingual on {DEVICE}...")
    model = ChatterboxMultilingualTTS.from_pretrained(device=DEVICE)

    print(f"Generating Mandarin audio for: {TEXT}")
    wav = generate_short_utterance(
        model,
        TEXT,
        language_id="zh",
    )
    wav = trim_generated_audio(wav, model.sr)

    print(f"Saving audio to {OUTPUT_PATH}")
    ta.save(OUTPUT_PATH, wav.cpu(), model.sr)
    print("Done.")


if __name__ == "__main__":
    try:
        main()
    except ImportError as exc:
        print(
            "Missing dependency. Install with: pip install chatterbox-tts torchaudio",
            file=sys.stderr,
        )
        raise exc
