"use client";

import { useEffect, useMemo, useRef, useState } from "react";
import { ScenarioDetail } from "@/lib/types";
import { ToggleSwitch } from "@/components/toggle-switch";
import { ScenarioLineCard } from "@/components/scenario-line-card";

type ScenarioDetailViewProps = {
  scenario: ScenarioDetail;
};

type CompletionRecord = {
  scenarioId: number;
};

export function ScenarioDetailView({ scenario }: ScenarioDetailViewProps) {
  const [showPronunciation, setShowPronunciation] = useState(true);
  const [showTranslation, setShowTranslation] = useState(true);
  const [completedScenarioIds, setCompletedScenarioIds] = useState<number[]>([]);
  const [completionReady, setCompletionReady] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);
  const [playingLineId, setPlayingLineId] = useState<number | null>(null);
  const currentAudioRef = useRef<HTMLAudioElement | null>(null);

  useEffect(() => {
    let cancelled = false;

    async function loadCompletions() {
      try {
        const response = await fetch("/api/me/scenario-completions", {
          cache: "no-store"
        });

        if (!response.ok) {
          return;
        }

        const completions = (await response.json()) as CompletionRecord[];
        if (!cancelled) {
          setCompletedScenarioIds(completions.map((completion) => completion.scenarioId));
        }
      } finally {
        if (!cancelled) {
          setCompletionReady(true);
        }
      }
    }

    void loadCompletions();

    return () => {
      cancelled = true;
    };
  }, []);

  useEffect(() => {
    return () => {
      currentAudioRef.current?.pause();
      currentAudioRef.current = null;
    };
  }, []);

  const isCompleted = useMemo(
    () => completedScenarioIds.includes(scenario.id),
    [completedScenarioIds, scenario.id]
  );
  const hasPronunciationGuide = useMemo(
    () => scenario.lines.some((line) => Boolean(line.pronunciationGuide)),
    [scenario.lines]
  );

  async function handleMarkCompleted() {
    setIsSubmitting(true);
    setErrorMessage(null);

    try {
      const response = await fetch("/api/me/scenario-completions", {
        method: "POST",
        headers: {
          "Content-Type": "application/json"
        },
        body: JSON.stringify({ scenarioId: scenario.id })
      });

      if (response.ok || response.status === 409) {
        setCompletedScenarioIds((current) =>
          current.includes(scenario.id) ? current : [...current, scenario.id]
        );
        return;
      }

      const payload = (await response.json()) as { message?: string };
      setErrorMessage(payload.message ?? "Could not mark this scenario as completed.");
    } catch {
      setErrorMessage("Could not mark this scenario as completed.");
    } finally {
      setIsSubmitting(false);
    }
  }

  function handleLineAudioToggle(lineId: number, audioUrl: string) {
    const currentAudio = currentAudioRef.current;

    if (currentAudio) {
      currentAudio.pause();
      currentAudio.currentTime = 0;
      currentAudioRef.current = null;
    }

    if (playingLineId === lineId) {
      setPlayingLineId(null);
      return;
    }

    const nextAudio = new Audio(audioUrl);

    nextAudio.onended = () => {
      currentAudioRef.current = null;
      setPlayingLineId(null);
    };
    nextAudio.onerror = () => {
      currentAudioRef.current = null;
      setPlayingLineId(null);
      console.error(`Could not play audio for scenario line ${lineId}.`);
    };

    currentAudioRef.current = nextAudio;
    setPlayingLineId(lineId);

    void nextAudio.play().catch(() => {
      currentAudioRef.current = null;
      setPlayingLineId(null);
      console.error(`Could not play audio for scenario line ${lineId}.`);
    });
  }

  return (
    <div className="flex flex-col gap-6">
      <section className="rounded-card border border-border bg-white px-5 py-5 shadow-card">
        <div className="flex flex-col gap-4 md:flex-row md:items-center md:justify-between">
          <div className="flex flex-wrap items-center gap-3">
            {hasPronunciationGuide ? (
              <ToggleSwitch
                label="Show pronunciation"
                checked={showPronunciation}
                onChange={setShowPronunciation}
              />
            ) : null}
            <ToggleSwitch
              label="English"
              checked={showTranslation}
              onChange={setShowTranslation}
            />
          </div>

          {scenario.audioUrl ? (
            <audio controls className="w-full md:w-auto">
              <source src={scenario.audioUrl} />
            </audio>
          ) : null}
        </div>
      </section>

      <section className="flex flex-col gap-5">
        {scenario.lines.map((line) => (
          <ScenarioLineCard
            key={line.id}
            line={line}
            showPronunciation={showPronunciation}
            showTranslation={showTranslation}
            isPlaying={playingLineId === line.id}
            onPlayToggle={(selectedLine) => {
              if (!selectedLine.audioUrl) {
                return;
              }

              handleLineAudioToggle(selectedLine.id, selectedLine.audioUrl);
            }}
          />
        ))}
      </section>

      <section className="rounded-card border border-accent/15 bg-soft px-6 py-7 text-center shadow-card">
        <h2 className="text-2xl font-semibold tracking-tight text-ink">
          Finished reading?
        </h2>
        <p className="mt-2 text-sm leading-6 text-muted">
          Mark this scenario as completed to keep track of your practice.
        </p>

        <div className="mt-5 flex flex-col items-center gap-3">
          <button
            type="button"
            onClick={() => void handleMarkCompleted()}
            disabled={isSubmitting || (completionReady && isCompleted)}
            className={`inline-flex items-center justify-center rounded-full px-5 py-3 text-sm font-semibold text-white transition ${
              completionReady && isCompleted
                ? "cursor-default bg-emerald-500"
                : "bg-accent hover:bg-[#274fc0] disabled:cursor-wait disabled:opacity-70"
            }`}
          >
            {completionReady && isCompleted
              ? "✓ Completed"
              : isSubmitting
                ? "Saving..."
                : "Mark as Completed"}
          </button>

          {errorMessage ? (
            <p className="text-sm text-rose-600">{errorMessage}</p>
          ) : null}
        </div>
      </section>
    </div>
  );
}
