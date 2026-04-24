"""
Local batch audio generator for Mandarin Scenario Reader.

Install:
    python -m venv .venv
    source .venv/bin/activate
    pip install -r audio-gen/requirements.txt

Configure:
    export DATABASE_URL="postgresql://postgres:postgres@localhost:54321/languages"
    export AUDIO_STORAGE_PATH="/absolute/path/to/backend/generated/audio"  # optional

Run:
    python audio-gen/generate_audio.py

Notes:
- Audio files are stored outside src/main/resources because they are generated
  at runtime, not packaged at build time.
- Stable file names like scenario-line-{scenarioLineId}.wav keep URLs stable
  when a line is regenerated and make it easier to swap local disk for S3 later.
- The script chooses the Chatterbox language code from Scenario.language so the
  same batch flow can support Mandarin, Spanish, and German.
"""

from __future__ import annotations

import hashlib
import os
from dataclasses import dataclass
from pathlib import Path
from typing import Iterable

import perth
import psycopg
import torch
import torch.nn.functional as F
import torchaudio
from chatterbox.mtl_tts import (
    SUPPORTED_LANGUAGES,
    ChatterboxMultilingualTTS,
    T3Cond,
    drop_invalid_tokens,
    punc_norm,
)


REPO_ROOT = Path(__file__).resolve().parents[1]
BACKEND_DIR = REPO_ROOT / "backend"
DEFAULT_AUDIO_DIR = BACKEND_DIR / "generated" / "audio"
DATABASE_URL = os.getenv("DATABASE_URL", "postgresql://postgres:postgres@localhost:54321/languages")
PLAYBACK_SPEED = float(os.getenv("PLAYBACK_SPEED", "1.0"))
DEFAULT_MAX_NEW_TOKENS = int(os.getenv("DEFAULT_MAX_NEW_TOKENS", "140"))
CFG_WEIGHT = float(os.getenv("CFG_WEIGHT", "0.3"))
EXAGGERATION = float(os.getenv("EXAGGERATION", "0.35"))
TRIM_THRESHOLD = float(os.getenv("TRIM_THRESHOLD", "0.05"))
TAIL_PADDING_MS = int(os.getenv("TAIL_PADDING_MS", "10"))
MAX_SECONDS = float(os.getenv("MAX_SECONDS", "5.0"))
DEVICE = "cpu"
LANGUAGE_CONFIG = {
    "MANDARIN": {
        "lang_code": "zh",
        "cfg_weight": CFG_WEIGHT,
        "exaggeration": EXAGGERATION,
        "default_max_new_tokens": DEFAULT_MAX_NEW_TOKENS,
    },
    "SPANISH": {
        "lang_code": "es",
        "cfg_weight": CFG_WEIGHT,
        "exaggeration": EXAGGERATION,
        "default_max_new_tokens": DEFAULT_MAX_NEW_TOKENS,
    },
    "GERMAN": {
        "lang_code": "de",
        "cfg_weight": CFG_WEIGHT,
        "exaggeration": EXAGGERATION,
        "default_max_new_tokens": DEFAULT_MAX_NEW_TOKENS,
    },
}


@dataclass
class ScenarioLineRow:
    id: int
    language: str
    target_text: str


def resolve_audio_storage_path() -> Path:
    configured_path = os.getenv("AUDIO_STORAGE_PATH")
    if not configured_path:
        return DEFAULT_AUDIO_DIR

    normalized_path = configured_path.strip()
    if not normalized_path or normalized_path.startswith("/absolute/path/"):
        return DEFAULT_AUDIO_DIR

    return Path(normalized_path).expanduser().resolve()


def patch_perth_watermarker_if_needed() -> None:
    if getattr(perth, "PerthImplicitWatermarker", None) is None:
        perth.PerthImplicitWatermarker = perth.DummyWatermarker


def get_language_config(language: str) -> dict[str, float | int | str] | None:
    return LANGUAGE_CONFIG.get(language)


def recommend_max_new_tokens(text: str, default_max_new_tokens: int) -> int:
    content_chars = sum(1 for ch in text if ch.strip() and ch not in "，。！？、；：,.!?;:-")
    if content_chars <= 10:
        return 60
    if content_chars <= 16:
        return 80
    if content_chars <= 24:
        return 110
    return default_max_new_tokens


def custom_generate(
    model: ChatterboxMultilingualTTS,
    text: str,
    language_id: str,
    cfg_weight: float,
    exaggeration: float,
    default_max_new_tokens: int,
    max_new_tokens: int | None = None,
) -> torch.Tensor:
    if language_id.lower() not in SUPPORTED_LANGUAGES:
        raise ValueError(f"Unsupported language_id: {language_id}")
    if max_new_tokens is None:
        max_new_tokens = recommend_max_new_tokens(text, default_max_new_tokens)
    if float(exaggeration) != float(model.conds.t3.emotion_adv[0, 0, 0].item()):
        conds: T3Cond = model.conds.t3
        model.conds.t3 = T3Cond(
            speaker_emb=conds.speaker_emb,
            clap_emb=conds.clap_emb,
            cond_prompt_speech_tokens=conds.cond_prompt_speech_tokens,
            cond_prompt_speech_emb=conds.cond_prompt_speech_emb,
            emotion_adv=exaggeration * torch.ones(1, 1, 1),
        ).to(device=model.device)
    tokens = model.tokenizer.text_to_tokens(punc_norm(text), language_id=language_id.lower()).to(model.device)
    tokens = torch.cat([tokens, tokens], dim=0)
    tokens = F.pad(tokens, (1, 0), value=model.t3.hp.start_text_token)
    tokens = F.pad(tokens, (0, 1), value=model.t3.hp.stop_text_token)
    with torch.inference_mode():
        speech_tokens = model.t3.inference(
            t3_cond=model.conds.t3,
            text_tokens=tokens,
            max_new_tokens=max_new_tokens,
            temperature=0.8,
            cfg_weight=cfg_weight,
            repetition_penalty=2.0,
            min_p=0.05,
            top_p=1.0,
        )[0]
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
    return F.interpolate(
        channel_first_wav.unsqueeze(0),
        size=target_length,
        mode="linear",
        align_corners=False,
    ).squeeze(0)


def load_model() -> ChatterboxMultilingualTTS:
    patch_perth_watermarker_if_needed()
    print(f"Loading Chatterbox Multilingual on {DEVICE}...")
    return ChatterboxMultilingualTTS.from_pretrained(device=DEVICE)


def select_lines_for_generation(connection: psycopg.Connection) -> list[ScenarioLineRow]:
    with connection.cursor() as cursor:
        cursor.execute(
            """
            SELECT scenario_lines.id, scenarios.language, scenario_lines.target_text
            FROM scenario_lines
            JOIN scenarios ON scenarios.id = scenario_lines.scenario_id
            WHERE audio_status IN ('NOT_GENERATED', 'PENDING_REGENERATION')
            ORDER BY scenario_lines.id ASC
            """
        )
        return [
            ScenarioLineRow(
                id=row[0],
                language=row[1],
                target_text=row[2],
            )
            for row in cursor.fetchall()
        ]


def filename_for_line(scenario_line_id: int) -> str:
    return f"scenario-line-{scenario_line_id}.wav"


def url_for_filename(filename: str) -> str:
    return f"/audio/{filename}"


def text_hash(text: str) -> str:
    return hashlib.sha256(text.encode("utf-8")).hexdigest()


def mark_generation_success(
    connection: psycopg.Connection,
    scenario_line_id: int,
    audio_url: str,
    source_text_hash: str,
) -> None:
    with connection.cursor() as cursor:
        cursor.execute(
            """
            UPDATE scenario_lines
            SET audio_url = %s,
                audio_status = 'GENERATED',
                audio_generated_at = CURRENT_TIMESTAMP,
                audio_source_text_hash = %s
            WHERE id = %s
            """,
            (audio_url, source_text_hash, scenario_line_id),
        )
    connection.commit()


def mark_generation_failed(connection: psycopg.Connection, scenario_line_id: int) -> None:
    with connection.cursor() as cursor:
        cursor.execute(
            """
            UPDATE scenario_lines
            SET audio_status = 'FAILED'
            WHERE id = %s
            """,
            (scenario_line_id,),
        )
    connection.commit()


def generate_audio_for_line(
    model: ChatterboxMultilingualTTS,
    row: ScenarioLineRow,
    audio_directory: Path,
) -> tuple[Path, str]:
    language_config = get_language_config(row.language)
    if language_config is None:
        raise ValueError(f"Unsupported learning language: {row.language}")

    filename = filename_for_line(row.id)
    output_path = audio_directory / filename
    raw_wav = custom_generate(
        model,
        row.target_text,
        language_id=str(language_config["lang_code"]),
        cfg_weight=float(language_config["cfg_weight"]),
        exaggeration=float(language_config["exaggeration"]),
        default_max_new_tokens=int(language_config["default_max_new_tokens"]),
    )
    trimmed_wav = trim_generated_audio(raw_wav, model.sr)
    final_wav = slow_audio(trimmed_wav)
    torchaudio.save(str(output_path), final_wav.cpu(), model.sr)
    return output_path, url_for_filename(filename)


def process_lines(
    connection: psycopg.Connection,
    model: ChatterboxMultilingualTTS,
    rows: Iterable[ScenarioLineRow],
    audio_directory: Path,
) -> tuple[int, int, int]:
    processed = succeeded = failed = 0
    for row in rows:
        processed += 1
        try:
            if get_language_config(row.language) is None:
                failed += 1
                print(f"[SKIPPED] line={row.id} language={row.language} reason=unsupported-language")
                continue

            print(f"Generating line {row.id} ({row.language})")
            output_path, audio_url = generate_audio_for_line(model, row, audio_directory)
            mark_generation_success(connection, row.id, audio_url, text_hash(row.target_text))
            succeeded += 1
            print(f"[OK] line={row.id} file={output_path.name} url={audio_url}")
        except Exception as exc:  # noqa: BLE001 - batch should continue per row
            connection.rollback()
            mark_generation_failed(connection, row.id)
            failed += 1
            print(f"[FAILED] line={row.id} error={exc}")
    return processed, succeeded, failed


def main() -> None:
    audio_directory = resolve_audio_storage_path()
    audio_directory.mkdir(parents=True, exist_ok=True)

    with psycopg.connect(DATABASE_URL) as connection:
        rows = select_lines_for_generation(connection)
        if not rows:
            print("No scenario lines require audio generation.")
            return

        model = load_model()
        processed, succeeded, failed = process_lines(connection, model, rows, audio_directory)

    print("--- Summary ---")
    print(f"Processed: {processed}")
    print(f"Succeeded: {succeeded}")
    print(f"Failed: {failed}")


if __name__ == "__main__":
    main()
