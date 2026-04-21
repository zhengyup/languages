"""Minimal Chatterbox multilingual Mandarin TTS experiment.

Install: pip install chatterbox-tts torchaudio
Run: python experiments/chatterbox_mandarin_test.py
Change text: edit TEST_CASES below and keep language_id="zh".
Change length: adjust MAX_NEW_TOKENS. Short dialogue lines usually work well in
the 80-150 range; start around 100-120.
Change speed: adjust PLAYBACK_SPEED. Use 0.9 to make audio 10% slower.

What changed and why:
- Chatterbox's built-in multilingual generate() hardcodes max_new_tokens=1000.
- custom_generate() follows the same path but lets us control max_new_tokens.
- trim_audio() removes the common trailing tail after short generations.
- slow_audio() uses simple waveform stretching and returns the original audio
  unchanged when PLAYBACK_SPEED is 1.0.
"""

from __future__ import annotations

import perth
import torch
import torch.nn.functional as F
import torchaudio
from chatterbox.mtl_tts import SUPPORTED_LANGUAGES, ChatterboxMultilingualTTS, T3Cond, drop_invalid_tokens, punc_norm

DEVICE = "cpu"
DEFAULT_MAX_NEW_TOKENS = 140
CFG_WEIGHT = 0.3
EXAGGERATION = 0.35
TRIM_THRESHOLD = 0.05
TAIL_PADDING_MS = 10
MAX_SECONDS = 5.0
PLAYBACK_SPEED = 1
TEST_CASES = [
    ("mandarin_test_trimmed.wav", "你好，欢迎来到中文情景阅读器。"),
    ("mandarin_test_trimmed_short.wav", "请问，地铁站在哪里？"),
    ("mandarin_test_trimmed_long.wav", "如果你今天晚上有空，我们下班以后一起去吃饭，好吗？"),
]


def recommend_max_new_tokens(text: str) -> int:
    """
    Short Mandarin lines should not get the same token budget as longer lines.
    A smaller budget reduces the chance of trailing breaths, silence, and
    repeated fragments before trimming even runs.
    """
    content_chars = sum(1 for ch in text if ch.strip() and ch not in "，。！？、；：,.!?;:-")
    if content_chars <= 10:
        return 60
    if content_chars <= 16:
        return 80
    if content_chars <= 24:
        return 110
    return DEFAULT_MAX_NEW_TOKENS


def custom_generate(model: ChatterboxMultilingualTTS, text: str, language_id: str = "zh", max_new_tokens: int | None = None, cfg_weight: float = CFG_WEIGHT, exaggeration: float = EXAGGERATION) -> torch.Tensor:
    if language_id.lower() not in SUPPORTED_LANGUAGES:
        raise ValueError(f"Unsupported language_id: {language_id}")
    if max_new_tokens is None:
        max_new_tokens = recommend_max_new_tokens(text)
    if float(exaggeration) != float(model.conds.t3.emotion_adv[0, 0, 0].item()):
        conds: T3Cond = model.conds.t3
        model.conds.t3 = T3Cond(speaker_emb=conds.speaker_emb, clap_emb=conds.clap_emb, cond_prompt_speech_tokens=conds.cond_prompt_speech_tokens, cond_prompt_speech_emb=conds.cond_prompt_speech_emb, emotion_adv=exaggeration * torch.ones(1, 1, 1)).to(device=model.device)
    tokens = model.tokenizer.text_to_tokens(punc_norm(text), language_id=language_id.lower()).to(model.device)
    tokens = torch.cat([tokens, tokens], dim=0)
    tokens = F.pad(tokens, (1, 0), value=model.t3.hp.start_text_token)
    tokens = F.pad(tokens, (0, 1), value=model.t3.hp.stop_text_token)
    with torch.inference_mode():
        speech_tokens = model.t3.inference(t3_cond=model.conds.t3, text_tokens=tokens, max_new_tokens=max_new_tokens, temperature=0.8, cfg_weight=cfg_weight, repetition_penalty=2.0, min_p=0.05, top_p=1.0)[0]
        speech_tokens = drop_invalid_tokens(speech_tokens).to(model.device)
        wav, _ = model.s3gen.inference(speech_tokens=speech_tokens, ref_dict=model.conds.gen)
        wav = wav.squeeze(0).detach().cpu().numpy()
        return torch.from_numpy(model.watermarker.apply_watermark(wav, sample_rate=model.sr)).unsqueeze(0)


def trim_generated_audio(
    wav: torch.Tensor,
    sample_rate: int,
    threshold: float = TRIM_THRESHOLD,
    max_seconds: float = MAX_SECONDS,
    tail_padding_ms: int = TAIL_PADDING_MS,
) -> torch.Tensor:
    """
    Reuse the earlier trim behavior that worked well for short Mandarin TTS:
    trim after the last strong sample, keep a small natural tail, and cap the
    total duration as a final safeguard.
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


def slow_audio(wav: torch.Tensor, sample_rate: int, speed: float = PLAYBACK_SPEED) -> torch.Tensor:
    """
    Slow down playback with simple waveform stretching.
    Use PLAYBACK_SPEED < 1.0 for slower audio, e.g. 0.9 for 10% slower.
    When PLAYBACK_SPEED is 1.0, return the input unchanged.
    """
    if abs(speed - 1.0) < 1e-6:
        return wav

    channel_first_wav = wav if wav.dim() == 2 else wav.unsqueeze(0)
    target_length = max(1, int(channel_first_wav.shape[-1] / speed))
    slowed_wav = F.interpolate(
        channel_first_wav.unsqueeze(0),
        size=target_length,
        mode="linear",
        align_corners=False,
    ).squeeze(0)
    return slowed_wav


def main() -> None:
    if getattr(perth, "PerthImplicitWatermarker", None) is None:
        perth.PerthImplicitWatermarker = perth.DummyWatermarker
    print(f"Loading model on {DEVICE}...")
    model = ChatterboxMultilingualTTS.from_pretrained(device=DEVICE)
    for output_path, text in TEST_CASES:
        max_new_tokens = recommend_max_new_tokens(text)
        print(f"Generating: {text}")
        raw_wav = custom_generate(model, text, max_new_tokens=max_new_tokens, cfg_weight=CFG_WEIGHT, exaggeration=EXAGGERATION)
        trimmed_wav = trim_generated_audio(raw_wav, model.sr)
        slowed_wav = slow_audio(trimmed_wav, model.sr)
        torchaudio.save(output_path, slowed_wav.cpu(), model.sr)
        raw_seconds = raw_wav.shape[1] / model.sr
        trimmed_seconds = trimmed_wav.shape[1] / model.sr
        slowed_seconds = slowed_wav.shape[1] / model.sr
        print(f"Saved {output_path} | raw={raw_seconds:.2f}s trimmed={trimmed_seconds:.2f}s slowed={slowed_seconds:.2f}s | speed={PLAYBACK_SPEED}, max_new_tokens={max_new_tokens}, cfg_weight={CFG_WEIGHT}, exaggeration={EXAGGERATION}")


if __name__ == "__main__":
    main()
