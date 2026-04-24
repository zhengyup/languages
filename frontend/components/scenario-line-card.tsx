"use client";

import { useMemo, useState } from "react";
import { ScenarioLine, VocabularyItem } from "@/lib/types";

type ScenarioLineCardProps = {
  line: ScenarioLine;
  showPronunciation: boolean;
  showTranslation: boolean;
};

type LineSegment =
  | { type: "text"; value: string; key: string }
  | { type: "vocabulary"; item: VocabularyItem; key: string };

export function ScenarioLineCard({
  line,
  showPronunciation,
  showTranslation
}: ScenarioLineCardProps) {
  const [activeVocabularyId, setActiveVocabularyId] = useState<number | null>(null);

  const activeVocabularyItem = line.vocabularyItems.find(
    (item) => item.id === activeVocabularyId
  );

  const segments = useMemo(() => buildLineSegments(line), [line]);

  return (
    <article className="rounded-[24px] border border-border bg-white px-5 py-5 shadow-card">
      <div className="mb-3 flex items-start justify-between gap-4">
        <span className="text-sm font-semibold text-accent">
          {line.speakerName ?? "Narration"}
        </span>
        <span className="text-sm font-medium text-muted">#{line.lineOrder}</span>
      </div>

      <div className="relative">
        <div className="text-[1.45rem] font-semibold leading-[2.15rem] tracking-tight text-ink">
          {segments.map((segment) =>
            segment.type === "text" ? (
              <span key={segment.key}>{segment.value}</span>
            ) : (
              <button
                key={segment.key}
                type="button"
                onClick={() =>
                  setActiveVocabularyId((current) =>
                    current === segment.item.id ? null : segment.item.id
                  )
                }
                className={`rounded-sm border-b-2 border-dotted transition ${
                  activeVocabularyId === segment.item.id
                    ? "border-accent text-accent"
                    : "border-accent/70 hover:border-accent hover:text-accent"
                }`}
              >
                {segment.item.expression}
              </button>
            )
          )}
        </div>

        {activeVocabularyItem ? (
          <div className="absolute left-0 top-full z-10 mt-4 w-full max-w-xs rounded-[22px] border border-border bg-white px-4 py-4 shadow-2xl">
            <div className="flex items-baseline gap-2">
              <p className="text-3xl font-semibold tracking-tight text-ink">
                {activeVocabularyItem.expression}
              </p>
              {activeVocabularyItem.pronunciationGuide ? (
                <p className="text-base text-muted">
                  {activeVocabularyItem.pronunciationGuide}
                </p>
              ) : null}
            </div>
            <p className="mt-3 text-lg font-medium text-ink">
              {activeVocabularyItem.gloss}
            </p>
            {activeVocabularyItem.explanation ? (
              <p className="mt-2 text-sm leading-6 text-muted">
                {activeVocabularyItem.explanation}
              </p>
            ) : null}
          </div>
        ) : null}
      </div>

      {showPronunciation && line.pronunciationGuide ? (
        <p className="mt-4 text-lg leading-8 text-muted">{line.pronunciationGuide}</p>
      ) : null}

      {showTranslation && line.englishTranslation ? (
        <p className="mt-2 font-serif text-lg italic leading-8 text-slate-600">
          {line.englishTranslation}
        </p>
      ) : null}

      <div className="mt-5 border-t border-border pt-3 text-sm text-muted">
        Hover or tap underlined words for definitions
      </div>
    </article>
  );
}

function buildLineSegments(line: ScenarioLine): LineSegment[] {
  const segments: LineSegment[] = [];
  let cursor = 0;

  line.vocabularyItems.forEach((item) => {
    if (cursor < item.startCharIndex) {
      segments.push({
        type: "text",
        value: line.targetText.slice(cursor, item.startCharIndex),
        key: `text-${cursor}`
      });
    }

    segments.push({
      type: "vocabulary",
      item,
      key: `vocabulary-${item.id}`
    });

    cursor = item.endCharIndex;
  });

  if (cursor < line.targetText.length) {
    segments.push({
      type: "text",
      value: line.targetText.slice(cursor),
      key: `text-${cursor}-tail`
    });
  }

  return segments;
}
