import { AppHeader } from "@/components/app-header";
import { EmptyState } from "@/components/empty-state";
import { ScenarioList } from "@/components/scenario-list";
import { getGroupedScenarioCards } from "@/lib/backend";

export default async function HomePage() {
  try {
    const groups = await getGroupedScenarioCards();

    return (
      <main className="min-h-screen px-4 py-6 text-ink sm:px-6 sm:py-8">
        <div className="mx-auto flex max-w-5xl flex-col gap-10">
          <AppHeader />
          <ScenarioList groups={groups} />
        </div>
      </main>
    );
  } catch (error) {
    const message = error instanceof Error ? error.message : "Unable to load scenarios right now.";

    return (
      <main className="min-h-screen px-4 py-10 sm:px-6">
        <div className="mx-auto max-w-5xl">
          <AppHeader />
          <div className="mt-10">
            <EmptyState
              title="Could not load scenarios"
              description={message}
            />
          </div>
        </div>
      </main>
    );
  }
}

