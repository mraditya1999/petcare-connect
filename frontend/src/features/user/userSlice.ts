import { createSlice, PayloadAction } from "@reduxjs/toolkit";
import { IProfileState, IProfileData } from "@/types/profile-types";
import { ApiResponse } from "@/types/api";
import { fetchProfile, updateProfile, deleteProfile } from "./userThunk";
import { IDeleteProfileResponse } from "@/types/profile-thunk-types";

const initialState: IProfileState = {
  profile: null,
  loading: false,
  error: null,
  success: null,
};

export const profileSlice = createSlice({
  name: "user",
  initialState,
  reducers: {},
  extraReducers: (builder) => {
    builder
      .addCase(fetchProfile.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(
        fetchProfile.fulfilled,
        (state, action: PayloadAction<ApiResponse<IProfileData>>) => {
          state.profile = action.payload.data;
          state.loading = false;
        },
      )
      .addCase(fetchProfile.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload as string;
      })
      .addCase(updateProfile.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(
        updateProfile.fulfilled,
        (state, action: PayloadAction<ApiResponse<IProfileData>>) => {
          state.profile = action.payload.data;
          state.loading = false;
          state.success = action.payload.message;
        },
      )
      .addCase(updateProfile.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload as string;
      })
      .addCase(deleteProfile.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(
        deleteProfile.fulfilled,
        (state, action: PayloadAction<IDeleteProfileResponse>) => {
          state.profile = null;
          state.loading = false;
          state.success = action.payload.message;
        },
      )
      .addCase(deleteProfile.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload as string;
      });
  },
});

export default profileSlice.reducer;
