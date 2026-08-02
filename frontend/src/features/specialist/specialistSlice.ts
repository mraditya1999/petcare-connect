import { createSlice } from "@reduxjs/toolkit";
import { fetchSpecialists } from "./specialistThunk";

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

export interface SpecialistState {
  specialists: Specialist[];
  loading: boolean;
  error: string | null;
}

const initialState: SpecialistState = {
  specialists: [],
  loading: false,
  error: null,
};

const specialistSlice = createSlice({
  name: "specialist",
  initialState,
  reducers: {
    clearError: (state) => {
      state.error = null;
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(fetchSpecialists.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchSpecialists.fulfilled, (state, action) => {
        state.loading = false;
        state.specialists = (action.payload?.data?.content || []).map((item) => ({
          ...item,
          id: item.specialistId,
          fullName: item.fullName || `${item.firstName || ""} ${item.lastName || ""}`.trim(),
          specialization: item.specialization || "General Veterinary Care",
        }));
      })
      .addCase(fetchSpecialists.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload || "Failed to fetch specialists";
      });
  },
});

export const { clearError } = specialistSlice.actions;
export default specialistSlice.reducer;
