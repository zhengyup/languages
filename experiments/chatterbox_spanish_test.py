"""Minimal Chatterbox multilingual Spanish TTS experiment.

Install: pip install chatterbox-tts torchaudio
Run: python experiments/chatterbox_spanish_test.py
Change text: edit TEST_CASES below and keep language_id="es".
Change length: adjust DEFAULT_MAX_NEW_TOKENS or recommend_max_new_tokens().
Short dialogue lines usually work best in the 60-140 range.
Change speed: adjust PLAYBACK_SPEED. Use 0.9 to make audio 10% slower.

What changed and why:
- This mirrors the validated Mandarin and German experiments but targets Spanish.
- custom_generate() keeps Chatterbox's path while exposing max_new_tokens.
- trim_generated_audio() keeps the working trim behavior for short lines.
- Multiple Spanish sentences make it easier to tune by sentence length.
"""

from __future__ import annotations

import math

import perth
import torch
import torch.nn.functional as F
import torchaudio
from chatterbox.mtl_tts import SUPPORTED_LANGUAGES, ChatterboxMultilingualTTS, T3Cond, drop_invalid_tokens, punc_norm

DEVICE = "cpu"
DEFAULT_MAX_NEW_TOKENS = 120
CFG_WEIGHT = 0.3
EXAGGERATION = 0.35
TRIM_THRESHOLD = 0.15
TAIL_PADDING_MS = 5
MAX_SECONDS = 5.0
PLAYBACK_SPEED = 0.90
AUTO_PITCH_COMPENSATION = True
PITCH_SHIFT_SEMITONES = 0.0
TEST_CASES = [
    ("spanish_test_trimmed.wav", "Buenos días, ¿en qué puedo ayudarle?"),
    ("spanish_test_trimmed_short.wav", "¿Dónde está la estación?"),
    ("spanish_test_trimmed_medium.wav", "Hola, tengo una reservación."),
    ("spanish_test_trimmed_long.wav", "Perdón, quiero comprar un boleto para Madrid mañana por la mañana."),
]


def recommend_max_new_tokens(text: str) -> int:
    content_chars = sum(1 for ch in text if ch.strip() and ch not in ".,!?;:-¿¡")
    if content_chars <= 18:
        return 55
    if content_chars <= 28:
        return 70
    if content_chars <= 42:
        return 110
    return DEFAULT_MAX_NEW_TOKENS


def custom_generate(model: ChatterboxMultilingualTTS, text: str, language_id: str = "es", max_new_tokens: int | None = None, cfg_weight: float = CFG_WEIGHT, exaggeration: float = EXAGGERATION) -> torch.Tensor:
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


def slow_audio(wav: torch.Tensor, speed: float = PLAYBACK_SPEED) -> torch.Tensor:
    if abs(speed - 1.0) < 1e-6:
        return wav
    channel_first_wav = wav if wav.dim() == 2 else wav.unsqueeze(0)
    target_length = max(1, int(channel_first_wav.shape[-1] / speed))
    return F.interpolate(channel_first_wav.unsqueeze(0), size=target_length, mode="linear", align_corners=False).squeeze(0)


def apply_pitch_shift(
    wav: torch.Tensor,
    sample_rate: int,
    speed: float = PLAYBACK_SPEED,
    pitch_shift_semitones: float = PITCH_SHIFT_SEMITONES,
    auto_pitch_compensation: bool = AUTO_PITCH_COMPENSATION,
) -> tuple[torch.Tensor, float]:
    compensation = 0.0
    if auto_pitch_compensation and abs(speed - 1.0) > 1e-6:
        compensation = 12.0 * math.log2(1.0 / speed)

    total_shift = compensation + pitch_shift_semitones
    if abs(total_shift) < 1e-6:
        return wav, total_shift

    shifted_wav = torchaudio.functional.pitch_shift(
        wav,
        sample_rate=sample_rate,
        n_steps=total_shift,
    )
    return shifted_wav, total_shift


def main() -> None:
    if getattr(perth, "PerthImplicitWatermarker", None) is None:
        perth.PerthImplicitWatermarker = perth.DummyWatermarker
    print(f"Loading model on {DEVICE}...")
    model = ChatterboxMultilingualTTS.from_pretrained(device=DEVICE)
    for output_path, text in TEST_CASES:
        max_new_tokens = recommend_max_new_tokens(text)
        print(f"Generating: {text}")
        raw_wav = custom_generate(model, text, language_id="es", max_new_tokens=max_new_tokens, cfg_weight=CFG_WEIGHT, exaggeration=EXAGGERATION)
        trimmed_wav = trim_generated_audio(raw_wav, model.sr)
        slowed_wav = slow_audio(trimmed_wav)
        final_wav, applied_pitch_shift = apply_pitch_shift(slowed_wav, model.sr)
        torchaudio.save(output_path, final_wav.cpu(), model.sr)
        raw_seconds = raw_wav.shape[1] / model.sr
        trimmed_seconds = trimmed_wav.shape[1] / model.sr
        final_seconds = final_wav.shape[1] / model.sr
        print(f"Saved {output_path} | raw={raw_seconds:.2f}s trimmed={trimmed_seconds:.2f}s final={final_seconds:.2f}s | speed={PLAYBACK_SPEED}, pitch_shift={applied_pitch_shift:.2f}, max_new_tokens={max_new_tokens}, cfg_weight={CFG_WEIGHT}, exaggeration={EXAGGERATION}")


if __name__ == "__main__":
    main()
