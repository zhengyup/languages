"use client";

import { useEffect, useMemo, useState } from "react";
import { ScenarioCard } from "@/components/scenario-card";
import { LearningLanguage, ScenarioGroup } from "@/lib/types";
import { formatLanguageLabel } from "@/lib/topic";

type ScenarioListProps = {
  groups: ScenarioGroup[];
};

type CompletionRecord = {
  scenarioId: number;
};

type LanguageFilter = "ALL" | LearningLanguage;
const ACTIVE_LANGUAGE_ORDER: LearningLanguage[] = ["MANDARIN", "KOREAN", "JAPANESE"];

export function ScenarioList({ groups }: ScenarioListProps) {
  const [completedScenarioIds, setCompletedScenarioIds] = useState<number[]>([]);
  const [completionReady, setCompletionReady] = useState(false);
  const [activeLanguage, setActiveLanguage] = useState<LanguageFilter>("ALL");

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

  const completionSet = useMemo(
    () => new Set(completedScenarioIds),
    [completedScenarioIds]
  );
  const filteredGroups = useMemo(
    () =>
      groups
        .map((group) => ({
          ...group,
          scenarios: group.scenarios.filter(
            (scenario) =>
              activeLanguage === "ALL" || scenario.language === activeLanguage
          )
        }))
        .filter((group) => group.scenarios.length > 0),
    [activeLanguage, groups]
  );
  const languageFilters = useMemo<Array<{ value: LanguageFilter; label: string }>>(
    () => [
      { value: "ALL", label: "All" },
      ...ACTIVE_LANGUAGE_ORDER.filter((language) =>
        groups.some((group) =>
          group.scenarios.some((scenario) => scenario.language === language)
        )
      ).map((language) => ({
        value: language,
        label: formatLanguageLabel(language)
      }))
    ],
    [groups]
  );

  return (
    <div className="flex flex-col gap-10">
      <section className="flex flex-wrap gap-2">
        {languageFilters.map((filter) => (
          <button
            key={filter.value}
            type="button"
            onClick={() => setActiveLanguage(filter.value)}
            className={`rounded-full border px-4 py-2 text-sm font-medium transition ${
              activeLanguage === filter.value
                ? "border-accent bg-accent text-white"
                : "border-border bg-white text-ink hover:border-accent/40 hover:text-accent"
            }`}
          >
            {filter.label}
          </button>
        ))}
      </section>

      {filteredGroups.map((group) => (
        <section key={group.topic} className="flex flex-col gap-4">
          <div className="flex items-end justify-between gap-4">
            <div>
              <h2 className="text-2xl font-semibold tracking-tight text-ink">
                {group.label}
              </h2>
              <p className="mt-1 text-sm text-muted">
                {group.description}
              </p>
            </div>
          </div>

          <div className="grid gap-4">
            {group.scenarios.map((scenario) => (
              <ScenarioCard
                key={scenario.id}
                scenario={scenario}
                isCompleted={completionSet.has(scenario.id)}
                completionReady={completionReady}
              />
            ))}
          </div>
        </section>
      ))}

      {filteredGroups.length === 0 ? (
        <section className="rounded-[22px] border border-border bg-white px-5 py-6 text-sm text-muted shadow-card">
          No scenarios are available for this language yet.
        </section>
      ) : null}
    </div>
  );
}
