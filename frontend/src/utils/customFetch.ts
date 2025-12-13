import axios from "axios";
import { getUserFromStorage } from "./helpers";

export const customFetch = axios.create({
  baseURL: "http://localhost:8080/api/v1",
  withCredentials: true,
});

customFetch.interceptors.request.use(
  (config) => {
    const user = getUserFromStorage();
    const mainToken = user?.data?.token;
    const tempToken = localStorage.getItem("tempSignupToken");

    const url = config.url || "";

    // 1️⃣ Only use temp token for complete-profile
    if (tempToken && url.includes("/auth/complete-profile")) {
      config.headers.Authorization = `Bearer ${tempToken}`;
      return config;
    }

    // 2️⃣ Use main JWT for everything except auth routes
    if (mainToken && !url.startsWith("/auth/")) {
      config.headers.Authorization = `Bearer ${mainToken}`;
    }

    return config;
  },
  (error) => Promise.reject(error),
);
