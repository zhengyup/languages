import { DifficultyLevel, LearningLanguage, ScenarioTopic } from "@/lib/types";

const TOPIC_META: Record<
  ScenarioTopic,
  { label: string; description: string; order: number }
> = {
  RESTAURANT: {
    label: "Restaurant",
    description: "Food, ordering, and everyday dining interactions.",
    order: 1
  },
  TRAVEL: {
    label: "Travel",
    description: "Getting around, chatting on the move, and exploring new places.",
    order: 2
  },
  WORK: {
    label: "Professional",
    description: "Natural workplace Mandarin for meetings and casual office chat.",
    order: 3
  },
  DIRECTIONS: {
    label: "Directions",
    description: "Finding places and asking for help in the city.",
    order: 4
  },
  SHOPPING: {
    label: "Shopping",
    description: "Everyday buying, browsing, and comparing options.",
    order: 5
  }
};

export function formatTopicLabel(topic: ScenarioTopic): string {
  return TOPIC_META[topic].label;
}

export function getTopicDescription(topic: ScenarioTopic): string {
  return TOPIC_META[topic].description;
}

export function getTopicOrder(topic: ScenarioTopic): number {
  return TOPIC_META[topic].order;
}

export function formatDifficultyLabel(difficulty: DifficultyLevel): string {
  return difficulty.toLowerCase();
}

export function formatLanguageLabel(language: LearningLanguage): string {
  switch (language) {
    case "MANDARIN":
      return "Mandarin";
    case "SPANISH":
      return "Spanish";
    case "GERMAN":
      return "German";
  }
}
