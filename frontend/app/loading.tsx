export default function Loading() {
  return (
    <main className="min-h-screen bg-page px-4 py-10 text-ink sm:px-6">
      <div className="mx-auto flex max-w-5xl flex-col gap-6">
        <div className="h-20 animate-pulse rounded-card border border-border bg-white/80" />
        <div className="h-28 animate-pulse rounded-card border border-border bg-white/80" />
        <div className="h-28 animate-pulse rounded-card border border-border bg-white/80" />
        <div className="h-28 animate-pulse rounded-card border border-border bg-white/80" />
      </div>
    </main>
  );
}

