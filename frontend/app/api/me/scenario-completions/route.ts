import { NextRequest, NextResponse } from "next/server";
import { BackendError, getScenarioCompletions, markScenarioCompleted } from "@/lib/backend";
import { ensureDemoUser } from "@/lib/demo-user";

export async function GET() {
  try {
    const user = await ensureDemoUser();
    const completions = await getScenarioCompletions(user.id);

    return NextResponse.json(completions);
  } catch (error) {
    return toErrorResponse(error);
  }
}

export async function POST(request: NextRequest) {
  try {
    const user = await ensureDemoUser();
    const body = (await request.json()) as { scenarioId?: number };

    if (typeof body.scenarioId !== "number") {
      return NextResponse.json(
        { message: "scenarioId is required" },
        { status: 400 }
      );
    }

    const completion = await markScenarioCompleted(user.id, body.scenarioId);
    return NextResponse.json(completion, { status: 201 });
  } catch (error) {
    return toErrorResponse(error);
  }
}

function toErrorResponse(error: unknown) {
  if (error instanceof BackendError) {
    return NextResponse.json(
      { message: error.message },
      { status: error.status }
    );
  }

  const message = error instanceof Error ? error.message : "Unexpected error";
  return NextResponse.json({ message }, { status: 500 });
}

