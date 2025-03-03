import { AxiosError } from "axios";
import { toast } from "@/components/ui/use-toast";
import { IUser } from "@/types/auth-types";
import { ZodError } from "zod";
import { formatDistanceToNow } from "date-fns";

export const saveUserToStorage = (user: IUser, rememberMe: boolean) => {
  const storage = rememberMe ? localStorage : sessionStorage;
  storage.setItem("user", JSON.stringify(user));
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

export const showToast = (
  description: string,
  variant?: "default" | "destructive",
) => {
  toast({ description, variant });
};

export type Theme = "dark" | "light" | "system";

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

export function handleError(error: unknown): string {
  // Handle ZodError
  if (error instanceof ZodError) {
    return error.errors[0]?.message || "An unexpected error occurred.";
  }

  // Handle AxiosError
  if (error instanceof AxiosError) {
    if (error.response) {
      return error.response.data?.message || "An unexpected error occurred.";
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
