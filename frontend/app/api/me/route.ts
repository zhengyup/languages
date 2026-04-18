import { NextResponse } from "next/server";
import { ensureDemoUser } from "@/lib/demo-user";

export async function GET() {
  const user = await ensureDemoUser();
  return NextResponse.json(user);
}

