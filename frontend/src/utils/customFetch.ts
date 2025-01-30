import axios from "axios";

const baseUrl = "http://localhost:8080/api/v1";

export const customFetch = axios.create({
  baseURL: baseUrl,
  withCredentials: true,
});

customFetch.interceptors.request.use(
  (config) => {
    const token =
      localStorage.getItem("token") || sessionStorage.getItem("token");
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  },
);
