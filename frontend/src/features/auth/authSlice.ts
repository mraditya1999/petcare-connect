import { createSlice, PayloadAction } from "@reduxjs/toolkit";
import {
  loginUser,
  registerUser,
  logoutUser,
  verifyEmail,
  forgetPassword,
  resetPassword,
} from "./authThunk";
import { IUser, IUserState } from "@/types/auth-types";
import { getUserFromStorage, showToast } from "@/utils/helpers";

const initialState: IUserState = {
  user: getUserFromStorage(),
  loading: false,
  error: null,
  success: null,
};

export const authSlice = createSlice({
  name: "auth",
  initialState,
  reducers: {},
  extraReducers: (builder) => {
    builder
      .addCase(loginUser.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(loginUser.fulfilled, (state, action: PayloadAction<IUser>) => {
        state.user = action.payload;
        state.loading = false;
      })
      .addCase(loginUser.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload as string;
      })
      .addCase(registerUser.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(registerUser.fulfilled, (state, action) => {
        state.loading = false;
        state.success = action.payload.message;
      })
      .addCase(registerUser.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload as string;
        state.success = null;
      })
      .addCase(logoutUser.pending, (state) => {
        state.loading = true;
        state.error = null;
        showToast("Logging out...");
      })
      .addCase(logoutUser.fulfilled, (state) => {
        state.user = null;
        state.loading = false;
        localStorage.removeItem("user");
        showToast("Logged out!🙂");
      })
      .addCase(logoutUser.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload as string;
        showToast(state.error, "destructive");
      })
      .addCase(verifyEmail.pending, (state) => {
        state.loading = true;
        state.error = null;
        showToast("Verifying email...");
      })
      .addCase(verifyEmail.fulfilled, (state, action) => {
        state.loading = false;
        state.success = action.payload.message;
        showToast("Email verified successfully!");
      })
      .addCase(verifyEmail.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload as string;
        state.success = null;
        showToast(state.error, "destructive");
      })
      .addCase(forgetPassword.pending, (state) => {
        state.loading = true;
        state.error = null;
        showToast("Sending reset password email...");
      })
      .addCase(forgetPassword.fulfilled, (state, action) => {
        state.loading = false;
        state.success = action.payload.message;
        showToast("Reset password email sent!");
      })
      .addCase(forgetPassword.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload as string;
        showToast(state.error, "destructive");
      })
      .addCase(resetPassword.pending, (state) => {
        state.loading = true;
        state.error = null;
        showToast("Resetting password...");
      })
      .addCase(resetPassword.fulfilled, (state, action) => {
        state.loading = false;
        state.success = action.payload.message;
        showToast("Password reset successful!");
      })
      .addCase(resetPassword.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload as string;
        showToast(state.error, "destructive");
      });
  },
});

// export const {  } = authSlice.actions;
export default authSlice.reducer;
