"use client";

import { useEffect, useMemo, useState } from "react";
import { ScenarioCard } from "@/components/scenario-card";
import { ScenarioGroup } from "@/lib/types";

type ScenarioListProps = {
  groups: ScenarioGroup[];
};

type CompletionRecord = {
  scenarioId: number;
};

export function ScenarioList({ groups }: ScenarioListProps) {
  const [completedScenarioIds, setCompletedScenarioIds] = useState<number[]>([]);
  const [completionReady, setCompletionReady] = useState(false);

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

  return (
    <div className="flex flex-col gap-10">
      {groups.map((group) => (
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
    </div>
  );
}

