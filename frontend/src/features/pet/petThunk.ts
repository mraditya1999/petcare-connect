import axios, { AxiosError } from "axios";
import { createAsyncThunk } from "@reduxjs/toolkit";
import { customFetch } from "@/utils/customFetch";

interface Pet {
  petId: number;
  name: string;
  species: string;
  breed: string;
}

const getErrorMessage = (error: unknown): string => {
  if (axios.isAxiosError(error)) {
    const axiosError = error as AxiosError<{ message?: string }>;
    return axiosError.response?.data?.message || axiosError.message || "An error occurred";
  }
  if (error instanceof Error) {
    return error.message;
  }
  return "An unknown error occurred";
};
interface IFetchPetsResponse {
  data: Pet[];
  message: string;
}

export const fetchPets = createAsyncThunk<
  IFetchPetsResponse,
  void,
  { rejectValue: string }
>("pet/fetchPets", async (_, { rejectWithValue }) => {
  try {
    const response = await customFetch.get("/pets");
    return response.data;
  } catch (error: unknown) {
    return rejectWithValue(getErrorMessage(error) || "Failed to fetch pets");
  }
});
