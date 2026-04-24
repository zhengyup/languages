import Link from "next/link";
import { notFound } from "next/navigation";
import { EmptyState } from "@/components/empty-state";
import { ScenarioDetailView } from "@/components/scenario-detail-view";
import { BackendError, getScenarioDetail } from "@/lib/backend";
import { formatDifficultyLabel, formatLanguageLabel, formatTopicLabel } from "@/lib/topic";

type ScenarioDetailPageProps = {
  params: Promise<{ id: string }>;
};

export default async function ScenarioDetailPage({ params }: ScenarioDetailPageProps) {
  const { id } = await params;

  try {
    const scenario = await getScenarioDetail(Number(id));

    return (
      <main className="min-h-screen px-4 py-6 text-ink sm:px-6 sm:py-8">
        <div className="mx-auto flex max-w-4xl flex-col gap-6">
          <div className="flex items-start gap-4">
            <Link
              href="/"
              className="mt-1 inline-flex h-10 w-10 items-center justify-center rounded-full border border-border bg-white text-lg text-ink transition hover:border-accent hover:text-accent"
            >
              ←
            </Link>
            <div className="min-w-0">
              <p className="text-sm font-semibold uppercase tracking-[0.16em] text-accent">
                {`${formatLanguageLabel(scenario.language)} · ${formatDifficultyLabel(scenario.difficultyLevel)} · ${formatTopicLabel(scenario.topic)}`}
              </p>
              <h1 className="mt-1 text-3xl font-semibold tracking-tight text-ink">
                {scenario.title}
              </h1>
              <p className="mt-2 max-w-2xl text-sm leading-6 text-muted">
                {scenario.description}
              </p>
            </div>
          </div>

          <ScenarioDetailView scenario={scenario} />
        </div>
      </main>
    );
  } catch (error) {
    if (error instanceof BackendError && error.status === 404) {
      notFound();
    }

    const message = error instanceof Error ? error.message : "Unable to load this scenario.";

    return (
      <main className="min-h-screen px-4 py-10 sm:px-6">
        <div className="mx-auto max-w-4xl">
          <Link
            href="/"
            className="inline-flex items-center gap-2 text-sm font-medium text-accent hover:text-ink"
          >
            ← Back to scenarios
          </Link>
          <div className="mt-8">
            <EmptyState
              title="Could not load this scenario"
              description={message}
            />
          </div>
        </div>
      </main>
    );
  }
}
