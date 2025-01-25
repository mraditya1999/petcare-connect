import axios from "axios";

const baseUrl = "http://localhost:8080/api/v1";
export const customFetch = axios.create({
  baseURL: baseUrl,
});
