import { createSlice, PayloadAction } from "@reduxjs/toolkit";
import { Appointment } from "@/types/appointment-types";
import {
  fetchAppointments,
  createAppointment,
  updateAppointment,
  submitFeedback,
  cancelAppointment,
  completeAppointment,
} from "./appointmentThunk";

export interface AppointmentState {
  appointments: Appointment[];
  loading: boolean;
  error: string | null;
  totalElements: number;
  totalPages: number;
  currentPage: number;
  pageSize: number;
}

const initialState: AppointmentState = {
  appointments: [],
  loading: false,
  error: null,
  totalElements: 0,
  totalPages: 0,
  currentPage: 0,
  pageSize: 10,
};

const appointmentSlice = createSlice({
  name: "appointment",
  initialState,
  reducers: {
    clearError: (state) => {
      state.error = null;
    },
    setCurrentPage: (state, action: PayloadAction<number>) => {
      state.currentPage = action.payload;
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(fetchAppointments.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchAppointments.fulfilled, (state, action) => {
        state.loading = false;
        state.appointments = action.payload.data.content;
        state.totalElements = action.payload.data.totalElements;
        state.totalPages = action.payload.data.totalPages;
        state.currentPage = action.payload.data.number;
        state.pageSize = action.payload.data.size;
      })
      .addCase(fetchAppointments.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload || "Failed to fetch appointments";
      })
      .addCase(createAppointment.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(createAppointment.fulfilled, (state, action) => {
        state.loading = false;
        state.appointments.unshift(action.payload.data);
        state.totalElements += 1;
      })
      .addCase(createAppointment.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload || "Failed to create appointment";
      })
      .addCase(updateAppointment.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(updateAppointment.fulfilled, (state, action) => {
        state.loading = false;
        const index = state.appointments.findIndex(
          (apt) => apt.appointmentId === action.payload.data.appointmentId,
        );
        if (index !== -1) {
          state.appointments[index] = action.payload.data;
        }
      })
      .addCase(updateAppointment.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload || "Failed to update appointment";
      })
      .addCase(submitFeedback.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(submitFeedback.fulfilled, (state) => {
        state.loading = false;
        // Optionally update the appointment with feedback
      })
      .addCase(submitFeedback.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload || "Failed to submit feedback";
      })
      .addCase(cancelAppointment.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(cancelAppointment.fulfilled, (state, action) => {
        state.loading = false;
        // Update the appointment status to CANCELLED
        const index = state.appointments.findIndex(
          (apt) => apt.appointmentId === action.meta.arg.appointmentId,
        );
        if (index !== -1) {
          state.appointments[index].status = "CANCELLED";
        }
      })
      .addCase(cancelAppointment.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload || "Failed to cancel appointment";
      })
      .addCase(completeAppointment.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(completeAppointment.fulfilled, (state, action) => {
        state.loading = false;
        // Update the appointment status to COMPLETED
        const index = state.appointments.findIndex(
          (apt) => apt.appointmentId === action.meta.arg.appointmentId,
        );
        if (index !== -1) {
          state.appointments[index].status = "COMPLETED";
        }
      })
      .addCase(completeAppointment.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload || "Failed to complete appointment";
      });
  },
});

export const { clearError, setCurrentPage } = appointmentSlice.actions;
export default appointmentSlice.reducer;
