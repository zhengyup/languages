import { getTopicDescription, getTopicOrder, formatTopicLabel } from "@/lib/topic";
import {
  ScenarioCardData,
  ScenarioDetail,
  ScenarioGroup,
  ScenarioResponse,
  ScenarioTopic,
  User,
  UserScenarioCompletion
} from "@/lib/types";

const BACKEND_API_URL = process.env.BACKEND_API_URL ?? "http://localhost:8080";

export class BackendError extends Error {
  status: number;

  constructor(status: number, message: string) {
    super(message);
    this.status = status;
  }
}

export async function getScenarioList(): Promise<ScenarioResponse[]> {
  return backendFetch<ScenarioResponse[]>("/scenarios");
}

export async function getScenarioDetail(id: number): Promise<ScenarioDetail> {
  const detail = await backendFetch<ScenarioDetail>(`/scenarios/${id}`);

  return {
    ...detail,
    audioUrl: toBackendAssetUrl(detail.audioUrl),
    lines: detail.lines.map((line) => ({
      ...line,
      audioUrl: toBackendAssetUrl(line.audioUrl)
    }))
  };
}

export async function getGroupedScenarioCards(): Promise<ScenarioGroup[]> {
  const scenarios = await getScenarioList();
  const detailedScenarios = await Promise.all(
    scenarios.map(async (scenario) => {
      const detail = await getScenarioDetail(scenario.id);

      const scenarioCard: ScenarioCardData = {
        ...scenario,
        lineCount: detail.lines.length
      };

      return scenarioCard;
    })
  );

  const byTopic = detailedScenarios.reduce<Map<ScenarioTopic, ScenarioCardData[]>>((acc, scenario) => {
    const current = acc.get(scenario.topic) ?? [];
    current.push(scenario);
    acc.set(scenario.topic, current);
    return acc;
  }, new Map());

  return Array.from(byTopic.entries())
    .sort(([leftTopic], [rightTopic]) => getTopicOrder(leftTopic) - getTopicOrder(rightTopic))
    .map(([topic, groupedScenarios]) => ({
      topic,
      label: formatTopicLabel(topic),
      description: getTopicDescription(topic),
      scenarios: groupedScenarios.sort((left, right) => left.title.localeCompare(right.title))
    }));
}

export async function createUser(payload: {
  email: string;
  displayName: string;
}): Promise<User> {
  return backendFetch<User>("/users", {
    method: "POST",
    body: JSON.stringify(payload)
  });
}

export async function getUser(id: number): Promise<User> {
  return backendFetch<User>(`/users/${id}`);
}

export async function getScenarioCompletions(userId: number): Promise<UserScenarioCompletion[]> {
  return backendFetch<UserScenarioCompletion[]>(`/users/${userId}/scenario-completions`);
}

export async function markScenarioCompleted(
  userId: number,
  scenarioId: number
): Promise<UserScenarioCompletion> {
  return backendFetch<UserScenarioCompletion>(`/users/${userId}/scenario-completions`, {
    method: "POST",
    body: JSON.stringify({ scenarioId })
  });
}

async function backendFetch<T>(
  path: string,
  init: RequestInit = {}
): Promise<T> {
  const response = await fetch(`${BACKEND_API_URL}${path}`, {
    ...init,
    cache: "no-store",
    headers: {
      "Content-Type": "application/json",
      ...(init.headers ?? {})
    }
  });

  if (!response.ok) {
    throw new BackendError(response.status, await getErrorMessage(response));
  }

  return (await response.json()) as T;
}

async function getErrorMessage(response: Response): Promise<string> {
  try {
    const payload = (await response.json()) as { message?: string };
    return payload.message ?? `Request failed with status ${response.status}`;
  } catch {
    return `Request failed with status ${response.status}`;
  }
}

function toBackendAssetUrl(url?: string | null): string | null {
  if (!url) {
    return null;
  }

  if (url.startsWith("http://") || url.startsWith("https://")) {
    return url;
  }

  if (url.startsWith("/")) {
    return `${BACKEND_API_URL}${url}`;
  }

  return `${BACKEND_API_URL}/${url}`;
}
