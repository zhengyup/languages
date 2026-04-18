import Link from "next/link";
import { EmptyState } from "@/components/empty-state";

export default function NotFound() {
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
            title="Scenario not found"
            description="This scenario could not be found. It may have been removed or the backend may not be ready yet."
          />
        </div>
      </div>
    </main>
  );
}
