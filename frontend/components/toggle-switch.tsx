"use client";

type ToggleSwitchProps = {
  label: string;
  checked: boolean;
  onChange: (next: boolean) => void;
};

export function ToggleSwitch({ label, checked, onChange }: ToggleSwitchProps) {
  return (
    <button
      type="button"
      onClick={() => onChange(!checked)}
      className="inline-flex items-center gap-2 rounded-full border border-border bg-white px-3 py-2 text-sm font-medium text-ink transition hover:border-accent/40"
      aria-pressed={checked}
    >
      <span
        className={`relative h-5 w-9 rounded-full transition ${
          checked ? "bg-accent" : "bg-slate-300"
        }`}
      >
        <span
          className={`absolute top-0.5 h-4 w-4 rounded-full bg-white shadow transition ${
            checked ? "left-4" : "left-0.5"
          }`}
        />
      </span>
      {label}
    </button>
  );
}

