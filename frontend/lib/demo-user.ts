import { cookies } from "next/headers";
import { createUser, getUser } from "@/lib/backend";

const USER_COOKIE = "mandarin_reader_user_id";

export async function ensureDemoUser() {
  const cookieStore = await cookies();
  const cookieValue = cookieStore.get(USER_COOKIE)?.value;
  const cookieUserId = cookieValue ? Number(cookieValue) : null;

  if (cookieUserId && !Number.isNaN(cookieUserId)) {
    try {
      return await getUser(cookieUserId);
    } catch {
      // Re-create below if the backend was reset.
    }
  }

  const user = await createUser({
    email: `reader-${crypto.randomUUID().slice(0, 8)}@demo.local`,
    displayName: "Curious Reader"
  });

  cookieStore.set(USER_COOKIE, String(user.id), {
    httpOnly: true,
    sameSite: "lax",
    path: "/",
    maxAge: 60 * 60 * 24 * 365
  });

  return user;
}
