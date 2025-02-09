// import axios from "axios";
// import { getUserFromStorage } from "./helpers";

// const baseUrl = "http://localhost:8080/api/v1";

// export const customFetch = axios.create({
//   baseURL: baseUrl,
//   withCredentials: true,
// });

// customFetch.interceptors.request.use(
//   (config) => {
//     const user = getUserFromStorage();
//     if (user) {
//       config.headers.Authorization = `Bearer ${user.data.token}`;
//     }
//     return config;
//   },
//   (error) => {
//     return Promise.reject(error);
//   },
// );
import axios from "axios";
import { getUserFromStorage } from "./helpers";

const baseUrl = "http://localhost:8080/api/v1";

export const customFetch = axios.create({
  baseURL: baseUrl,
  withCredentials: true,
});

customFetch.interceptors.request.use(
  (config) => {
    const user = getUserFromStorage();
    if (user) {
      config.headers.Authorization = `Bearer ${user.data.token}`;
    }
    if (config.data instanceof FormData) {
      delete config.headers["Content-Type"]; // Let the browser set it
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  },
);
