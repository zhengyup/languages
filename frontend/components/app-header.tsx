export function AppHeader() {
  return (
    <header className="rounded-card border border-white/70 bg-white/90 px-6 py-5 shadow-card backdrop-blur">
      <div className="flex items-center gap-4">
        <div className="flex h-12 w-12 items-center justify-center rounded-2xl bg-accent text-xl text-white shadow-lg shadow-accent/20">
          读
        </div>
        <div>
          <h1 className="text-2xl font-semibold tracking-tight text-ink">
            Mandarin Scenario Reader
          </h1>
          <p className="text-sm text-muted">
            Practice through realistic dialogue.
          </p>
        </div>
      </div>
    </header>
  );
}

