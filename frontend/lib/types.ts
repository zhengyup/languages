export type ScenarioTopic =
  | "RESTAURANT"
  | "DIRECTIONS"
  | "WORK"
  | "TRAVEL"
  | "SHOPPING";

export type DifficultyLevel = "BEGINNER" | "INTERMEDIATE" | "ADVANCED";
export type LearningLanguage = "MANDARIN" | "SPANISH" | "GERMAN";

export type ScenarioResponse = {
  id: number;
  title: string;
  description: string;
  language: LearningLanguage;
  topic: ScenarioTopic;
  difficultyLevel: DifficultyLevel;
  createdAt: string;
};

export type VocabularyItem = {
  id: number;
  expression: string;
  pronunciationGuide?: string | null;
  gloss: string;
  explanation?: string | null;
  startCharIndex: number;
  endCharIndex: number;
  createdAt: string;
};

export type ScenarioLine = {
  id: number;
  lineOrder: number;
  speakerName?: string | null;
  targetText: string;
  pronunciationGuide?: string | null;
  englishTranslation?: string | null;
  audioUrl?: string | null;
  createdAt: string;
  vocabularyItems: VocabularyItem[];
};

export type ScenarioDetail = {
  id: number;
  title: string;
  description: string;
  language: LearningLanguage;
  topic: ScenarioTopic;
  difficultyLevel: DifficultyLevel;
  createdAt: string;
  lines: ScenarioLine[];
  audioUrl?: string | null;
};

export type ScenarioCardData = ScenarioResponse & {
  lineCount: number;
};

export type ScenarioGroup = {
  topic: ScenarioTopic;
  label: string;
  description: string;
  scenarios: ScenarioCardData[];
};

export type User = {
  id: number;
  email: string;
  displayName: string;
  createdAt: string;
  updatedAt: string;
};

export type UserScenarioCompletion = {
  userId: number;
  scenarioId: number;
  completedAt: string;
};
