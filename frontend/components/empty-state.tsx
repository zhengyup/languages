type EmptyStateProps = {
  title: string;
  description: string;
};

export function EmptyState({ title, description }: EmptyStateProps) {
  return (
    <div className="rounded-card border border-border bg-white px-6 py-10 text-center shadow-card">
      <h2 className="text-xl font-semibold text-ink">{title}</h2>
      <p className="mt-3 text-sm leading-6 text-muted">{description}</p>
    </div>
  );
}

