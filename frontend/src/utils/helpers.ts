import { AxiosError } from "axios";
import { IUser } from "@/types/auth-types";
import { AuthUserPayload } from "@/types/auth-thunk-types";
import { ZodError } from "zod";
import { formatDistanceToNow } from "date-fns";

export type Theme = "dark" | "light" | "system";

export const normalizeAuthUser = (
  payload?: AuthUserPayload | null,
  message = "Authenticated",
): IUser => {
  const provider = payload?.oauthProvider?.toUpperCase();
  const roles = (payload?.roles ?? [])
    .map((role) => role.toUpperCase())
    .filter(
      (role): role is IUser["data"]["roles"][number] =>
        role === "USER" || role === "ADMIN" || role === "SPECIALIST",
    );

  return {
    message,
    data: {
      userId: payload?.userId ?? null,
      email: payload?.email ?? null,
      roles: roles.length > 0 ? roles : ["USER"],
      token: payload?.token ?? payload?.accessToken ?? payload?.jwtToken ?? null,
      refreshToken: payload?.refreshToken ?? null,
      oauthProvider:
        provider === "GOOGLE"
          ? "GOOGLE"
          : provider === "GITHUB"
            ? "GITHUB"
            : provider === "MOBILE"
              ? "MOBILE"
              : "LOCAL",
      newUser: Boolean(payload?.newUser),
      isProfileComplete:
        payload?.isProfileComplete ?? payload?.profileComplete ?? true,
      tempToken: payload?.tempToken ?? null,
      firstName: payload?.firstName ?? null,
      lastName: payload?.lastName ?? null,
      verified: payload?.verified ?? false,
    },
  };
};

export const saveUserToStorage = (user: IUser, rememberMe: boolean) => {
  const storage = rememberMe ? localStorage : sessionStorage;
  storage.setItem("user", JSON.stringify(user));
};

export const persistUserToStorage = (user: IUser) => {
  const hasLocalUser = localStorage.getItem("user") !== null;
  const hasSessionUser = sessionStorage.getItem("user") !== null;

  if (hasLocalUser) {
    localStorage.setItem("user", JSON.stringify(user));
  }

  if (hasSessionUser) {
    sessionStorage.setItem("user", JSON.stringify(user));
  }

  if (!hasLocalUser && !hasSessionUser) {
    localStorage.setItem("user", JSON.stringify(user));
  }
};

export const getUserFromStorage = (): IUser | null => {
  const userJson =
    localStorage.getItem("user") || sessionStorage.getItem("user");
  if (userJson) {
    try {
      return JSON.parse(userJson) as IUser;
    } catch (error) {
      console.error("Error parsing user JSON:", error);
      return null;
    }
  }
  return null;
};

export const getInitialTheme = (): Theme => {
  const storedTheme = localStorage.getItem("theme") as Theme;
  if (
    storedTheme === "dark" ||
    storedTheme === "light" ||
    storedTheme === "system"
  ) {
    return storedTheme;
  }
  return "system";
};

export const getSystemTheme = (): "dark" | "light" => {
  if (typeof window !== "undefined" && window.matchMedia) {
    return window.matchMedia("(prefers-color-scheme: dark)").matches
      ? "dark"
      : "light";
  }
  return "light"; // fallback to light theme
};

export const getAppliedTheme = (theme: Theme): "dark" | "light" => {
  return theme === "system" ? getSystemTheme() : theme;
};

export function handleError(error: unknown): string {
  // Handle ZodError
  if (error instanceof ZodError) {
    return error.errors[0]?.message || "An unexpected error occurred.";
  }

  // Handle AxiosError
  if (error instanceof AxiosError) {
    if (error.response?.data) {
      const serverData = error.response.data;

      // Standard API response object: { message, data }
      if (
        typeof serverData === "object" &&
        serverData !== null &&
        "message" in serverData &&
        typeof (serverData as { message?: unknown }).message === "string" &&
        (serverData as { message?: string }).message?.trim()
      ) {
        return (serverData as { message: string }).message;
      }

      // Some endpoints may use nested data messages
      if (
        typeof serverData === "object" &&
        serverData !== null &&
        "data" in serverData &&
        typeof (serverData as { data?: unknown }).data === "object" &&
        (serverData as { data?: unknown }).data !== null
      ) {
        const nestedData = (serverData as { data: unknown }).data;

        if (
          typeof nestedData === "object" &&
          nestedData !== null &&
          "message" in nestedData &&
          typeof (nestedData as { message?: unknown }).message === "string" &&
          (nestedData as { message?: string }).message?.trim()
        ) {
          return (nestedData as { message: string }).message;
        }

        // Handle validation errors as map/object
        if (typeof nestedData === "object" && nestedData !== null) {
          const validationErrors = nestedData as Record<string, unknown>;
          const firstError = Object.values(validationErrors)[0];
          if (typeof firstError === "string") {
            return firstError;
          }
          if (Array.isArray(firstError) && firstError.length > 0) {
            return String(firstError[0]);
          }
        }
      }

      if (typeof serverData === "string" && serverData.trim()) {
        return serverData;
      }

      return "An unexpected error occurred.";
    } else if (error.request) {
      return "No response received from server.";
    } else {
      return "An error occurred while setting up the request.";
    }
  }

  if (typeof error === "string") {
    return error;
  }

  return "An unexpected error occurred.";
}

export const formatRelativeTime = (dateStr: string | null): string => {
  if (!dateStr) return "Just now";
  const date = new Date(dateStr);
  return formatDistanceToNow(date, { addSuffix: true });
};

export const truncateContent = (content: string, maxLength = 50) => {
  if (!content) return "";
  if (content.length <= maxLength) {
    return content;
  }
  return content.substring(0, maxLength) + "...";
};

export function makeImmutable<T extends object>(obj: T): Readonly<T> {
  return Object.freeze(obj);
}
