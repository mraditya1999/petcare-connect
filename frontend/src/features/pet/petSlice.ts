import { createSlice } from "@reduxjs/toolkit";
import { fetchPets } from "./petThunk";

interface Pet {
  petId: number;
  name: string;
  species: string;
  breed: string;
}

export interface PetState {
  pets: Pet[];
  loading: boolean;
  error: string | null;
}

const initialState: PetState = {
  pets: [],
  loading: false,
  error: null,
};

const petSlice = createSlice({
  name: "pet",
  initialState,
  reducers: {
    clearError: (state) => {
      state.error = null;
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(fetchPets.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchPets.fulfilled, (state, action) => {
        state.loading = false;
        state.pets = action.payload.data;
      })
      .addCase(fetchPets.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload || "Failed to fetch pets";
      });
  },
});

export const { clearError } = petSlice.actions;
export default petSlice.reducer;
