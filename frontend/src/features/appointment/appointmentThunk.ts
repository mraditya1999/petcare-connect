import { createAsyncThunk } from "@reduxjs/toolkit";
import axios, { AxiosError } from "axios";
import { customFetch } from "@/utils/customFetch";
import {
  IFetchAppointmentsResponse,
  ICreateAppointmentRequest,
  ICreateAppointmentResponse,
  IUpdateAppointmentRequest,
  IUpdateAppointmentResponse,
  ISubmitFeedbackRequest,
  ISubmitFeedbackResponse,
  ICancelAppointmentRequest,
  ICancelAppointmentResponse,
  ICompleteAppointmentRequest,
  ICompleteAppointmentResponse,
} from "@/types/appointment-thunk-types";
import { ROUTES } from "@/utils/constants";

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
export const fetchAppointments = createAsyncThunk<
  IFetchAppointmentsResponse,
  { page?: number; size?: number; admin?: boolean },
  { rejectValue: string }
>(
  "appointment/fetchAppointments",
  async ({ page = 0, size = 10, admin = false }, { rejectWithValue }) => {
    try {
      const endpoint = admin ? "/admin/appointments" : ROUTES.APPOINTMENTS;
      const response = await customFetch.get(
        `${endpoint}?page=${page}&size=${size}`,
      );
      return response.data;
    } catch (error: unknown) {
      return rejectWithValue(
        getErrorMessage(error) || "Failed to fetch appointments",
      );
    }
  },
);

export const createAppointment = createAsyncThunk<
  ICreateAppointmentResponse,
  ICreateAppointmentRequest,
  { rejectValue: string }
>(
  "appointment/createAppointment",
  async (appointmentData, { rejectWithValue }) => {
    try {
      const response = await customFetch.post(
        ROUTES.APPOINTMENTS,
        appointmentData,
      );
      return response.data;
    } catch (error: unknown) {
      return rejectWithValue(
        getErrorMessage(error) || "Failed to create appointment",
      );
    }
  },
);

export const updateAppointment = createAsyncThunk<
  IUpdateAppointmentResponse,
  IUpdateAppointmentRequest,
  { rejectValue: string }
>(
  "appointment/updateAppointment",
  async ({ appointmentId, ...updateData }, { rejectWithValue }) => {
    try {
      const response = await customFetch.put(
        `${ROUTES.APPOINTMENTS}/${appointmentId}`,
        updateData,
      );
      return response.data;
    } catch (error: unknown) {
      return rejectWithValue(
        getErrorMessage(error) || "Failed to update appointment",
      );
    }
  },
);

export const submitFeedback = createAsyncThunk<
  ISubmitFeedbackResponse,
  ISubmitFeedbackRequest,
  { rejectValue: string }
>(
  "appointment/submitFeedback",
  async ({ appointmentId, ...feedbackData }, { rejectWithValue }) => {
    try {
      const response = await customFetch.post(
        `${ROUTES.APPOINTMENTS}/${appointmentId}/feedback`,
        feedbackData,
      );
      return response.data;
    } catch (error: unknown) {
      return rejectWithValue(
        getErrorMessage(error) || "Failed to submit feedback",
      );
    }
  },
);

export const cancelAppointment = createAsyncThunk<
  ICancelAppointmentResponse,
  ICancelAppointmentRequest,
  { rejectValue: string }
>(
  "appointment/cancelAppointment",
  async ({ appointmentId }, { rejectWithValue }) => {
    try {
      const response = await customFetch.put(
        `${ROUTES.APPOINTMENTS}/${appointmentId}/cancel`,
      );
      return response.data;
    } catch (error: unknown) {
      return rejectWithValue(
        getErrorMessage(error) || "Failed to cancel appointment",
      );
    }
  },
);

export const completeAppointment = createAsyncThunk<
  ICompleteAppointmentResponse,
  ICompleteAppointmentRequest,
  { rejectValue: string }
>(
  "appointment/completeAppointment",
  async ({ appointmentId }, { rejectWithValue }) => {
    try {
      const response = await customFetch.put(
        `${ROUTES.APPOINTMENTS}/${appointmentId}/complete`,
      );
      return response.data;
    } catch (error: unknown) {
      return rejectWithValue(
        getErrorMessage(error) || "Failed to complete appointment",
      );
    }
  },
);
