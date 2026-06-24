import { createSlice } from "@reduxjs/toolkit";
import { fetchSpecialists } from "./specialistThunk";

interface Specialist {
  id: number;
  fullName: string;
  specialization: string;
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
        state.specialists = action.payload.data.content;
      })
      .addCase(fetchSpecialists.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload || "Failed to fetch specialists";
      });
  },
});

export const { clearError } = specialistSlice.actions;
export default specialistSlice.reducer;
