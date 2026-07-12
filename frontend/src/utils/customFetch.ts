import axios, { AxiosError, InternalAxiosRequestConfig } from "axios";
import { getUserFromStorage, persistUserToStorage } from "./helpers";

const baseUrl =
  import.meta.env.VITE_API_BASE_URL || "http://localhost:8080/api/v1";

type RetryableRequestConfig = InternalAxiosRequestConfig & {
  _retry?: boolean;
};

let isRefreshing = false;
let pendingRefreshCallbacks: Array<(token: string) => void> = [];

const clearAuthStorage = () => {
  localStorage.removeItem("user");
  sessionStorage.removeItem("user");
  localStorage.removeItem("tempSignupToken");
  sessionStorage.removeItem("tempSignupToken");
};

const refreshAccessToken = async () => {
  const storedUser = getUserFromStorage();
  const refreshToken = storedUser?.data?.refreshToken;

  if (!refreshToken) {
    clearAuthStorage();
    throw new Error("No refresh token available");
  }

  const response = await customFetch.post("/auth/refresh", { refreshToken });
  const payload = response.data?.data;
  const newAccessToken = payload?.accessToken || payload?.token || payload?.jwtToken;

  if (!newAccessToken) {
    clearAuthStorage();
    throw new Error("Unable to refresh access token");
  }

  const updatedUser = {
    ...storedUser,
    data: {
      ...storedUser?.data,
      token: newAccessToken,
      refreshToken: payload?.refreshToken ?? refreshToken,
    },
  };

  persistUserToStorage(updatedUser);
  return newAccessToken as string;
};

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

    if (tempToken && url.includes("/oauth/complete-profile")) {
      config.headers.Authorization = `Bearer ${tempToken}`;
      return config;
    }

    if (mainToken && url.includes("/auth/logout")) {
      config.headers.Authorization = `Bearer ${mainToken}`;
      return config;
    }

    if (mainToken && !url.startsWith("/auth/")) {
      config.headers.Authorization = `Bearer ${mainToken}`;
    }

    return config;
  },
  (error) => Promise.reject(error),
);

customFetch.interceptors.response.use(
  (response) => response,
  async (error: AxiosError) => {
    const originalRequest = error.config as RetryableRequestConfig | undefined;
    const status = error.response?.status;
    const url = originalRequest?.url || "";

    const isAuthRequest = url.includes("/auth/");
    const shouldRetryWithRefresh =
      status === 401 && originalRequest && !originalRequest._retry && !isAuthRequest;

    if (!shouldRetryWithRefresh) {
      return Promise.reject(error);
    }

    if (isRefreshing) {
      return new Promise((resolve, reject) => {
        pendingRefreshCallbacks.push((token: string) => {
          if (!originalRequest.headers) {
            originalRequest.headers = {};
          }
          originalRequest.headers.Authorization = `Bearer ${token}`;
          originalRequest._retry = true;
          resolve(customFetch(originalRequest));
        });
      });
    }

    isRefreshing = true;
    originalRequest._retry = true;

    try {
      const newToken = await refreshAccessToken();
      if (!originalRequest.headers) {
        originalRequest.headers = {};
      }
      originalRequest.headers.Authorization = `Bearer ${newToken}`;

      pendingRefreshCallbacks.forEach((callback) => callback(newToken));
      pendingRefreshCallbacks = [];

      return customFetch(originalRequest);
    } catch (refreshError) {
      pendingRefreshCallbacks.forEach((callback) => {
        callback("");
      });
      pendingRefreshCallbacks = [];
      clearAuthStorage();
      return Promise.reject(refreshError);
    } finally {
      isRefreshing = false;
    }
  },
);
