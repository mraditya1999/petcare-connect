import axios, { AxiosError } from "axios";
import { createAsyncThunk } from "@reduxjs/toolkit";
import { customFetch } from "@/utils/customFetch";

interface Specialist {
  specialistId: number;
  userId?: number;
  firstName?: string;
  lastName?: string;
  fullName?: string;
  specialization?: string;
  available?: boolean;
  consultationFee?: string;
  location?: string;
  experienceYears?: number;
}

const getErrorMessage = (error: unknown): string => {
  if (axios.isAxiosError(error)) {
    const axiosError = error as AxiosError<{ message?: string }>;
    return (
      axiosError.response?.data?.message ||
      axiosError.message ||
      "An error occurred"
    );
  }
  if (error instanceof Error) {
    return error.message;
  }
  return "An unknown error occurred";
};
interface IFetchSpecialistsResponse {
  data: {
    content: Specialist[];
  };
  message: string;
}

export const fetchSpecialists = createAsyncThunk<
  IFetchSpecialistsResponse,
  { page?: number; size?: number },
  { rejectValue: string }
>(
  "specialist/fetchSpecialists",
  async ({ page = 0, size = 10 }, { rejectWithValue }) => {
    try {
      const response = await customFetch.get(
        `/specialists?page=${page}&size=${size}`,
      );
      return response.data;
    } catch (error: unknown) {
      return rejectWithValue(
        getErrorMessage(error) || "Failed to fetch specialists",
      );
    }
  },
);
