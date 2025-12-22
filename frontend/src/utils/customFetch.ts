import axios from "axios";
import { getUserFromStorage } from "./helpers";

const baseUrl =
  import.meta.env.VITE_API_BASE_URL || "http://localhost:8080/api/v1";

export const customFetch = axios.create({
  baseURL: baseUrl,
  withCredentials: true,
});

customFetch.interceptors.request.use(
  (config) => {
    const user = getUserFromStorage();
    const mainToken = user?.data?.token;
    const tempToken = localStorage.getItem("tempSignupToken");

    const url = config.url || "";

    // Only use temp token for complete-profile
    if (tempToken && url.includes("/auth/complete-profile")) {
      config.headers.Authorization = `Bearer ${tempToken}`;
      return config;
    }

    // Use main JWT for everything except auth routes
    if (mainToken && !url.startsWith("/auth/")) {
      config.headers.Authorization = `Bearer ${mainToken}`;
    }

    return config;
  },
  (error) => Promise.reject(error),
);
