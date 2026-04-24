import Link from "next/link";
import { ScenarioCardData } from "@/lib/types";
import { formatDifficultyLabel, formatLanguageLabel } from "@/lib/topic";

type ScenarioCardProps = {
  scenario: ScenarioCardData;
  isCompleted: boolean;
  completionReady: boolean;
};

export function ScenarioCard({
  scenario,
  isCompleted,
  completionReady
}: ScenarioCardProps) {
  return (
    <Link
      href={`/scenarios/${scenario.id}`}
      className="group flex flex-col gap-4 rounded-[22px] border border-border bg-white px-5 py-5 shadow-card transition duration-200 hover:-translate-y-0.5 hover:border-accent/40 hover:shadow-xl"
    >
      <div className="flex items-start justify-between gap-4">
        <div className="min-w-0">
          <h3 className="text-xl font-semibold tracking-tight text-ink">
            {scenario.title}
          </h3>
          <p className="mt-2 text-sm leading-6 text-muted">
            {scenario.description}
          </p>
        </div>
        <span className="mt-1 text-2xl text-muted transition group-hover:text-accent">
          ›
        </span>
      </div>

      <div className="flex flex-wrap items-center gap-3 text-xs font-medium">
        <span className="text-muted">
          {formatLanguageLabel(scenario.language)}
        </span>
        <span className="rounded-full bg-page px-3 py-1 text-ink">
          {formatDifficultyLabel(scenario.difficultyLevel)}
        </span>
        <span className="text-muted">{scenario.lineCount} lines</span>
        {completionReady && isCompleted ? (
          <span className="ml-auto font-semibold text-accent">✓ Completed</span>
        ) : null}
      </div>
    </Link>
  );
}
