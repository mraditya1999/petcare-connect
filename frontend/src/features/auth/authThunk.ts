import { createAsyncThunk } from "@reduxjs/toolkit";
import { IUser } from "@/types/auth-types";
import { customFetch } from "@/utils/customFetch";
import { handleError, saveUserToStorage } from "@/utils/helpers";
import { ROUTES } from "@/utils/constants";
import {
  ForgetPasswordParams,
  ForgetPasswordResponse,
  LoginUserParams,
  LogoutUserResponse,
  RegisterUserParams,
  RegisterUserResponse,
  ResetPasswordParams,
  ResetPasswordResponse,
  VerifyEmailParams,
  VerifyEmailResponse,
} from "@/types/auth-thunk-types";
import ShowToast from "@/components/shared/ShowToast";

export const loginUser = createAsyncThunk<
  IUser,
  LoginUserParams,
  { rejectValue: string }
>(
  "auth/loginUser",
  async ({ parsedData, rememberMe }: LoginUserParams, { rejectWithValue }) => {
    try {
      const response = await customFetch.post<IUser>("/auth/login", parsedData);
      const user = response.data;
      saveUserToStorage(user, rememberMe);
      ShowToast({ description: "Logged in successfully!", type: "success" });
      return user;
    } catch (error) {
      const errMsg = handleError(error);
      ShowToast({ description: errMsg, type: "error" });
      return rejectWithValue(errMsg);
    }
  },
);

export const registerUser = createAsyncThunk<
  RegisterUserResponse,
  RegisterUserParams,
  { rejectValue: string }
>(
  "auth/registerUser",
  async ({ parsedData }: RegisterUserParams, { rejectWithValue }) => {
    try {
      const response = await customFetch.post<RegisterUserResponse>(
        "/auth/register",
        parsedData,
      );
      ShowToast({ description: "Registered successfully!", type: "success" });
      return response.data;
    } catch (error) {
      const errMsg = handleError(error);
      ShowToast({ description: errMsg, type: "error" });
      return rejectWithValue(errMsg);
    }
  },
);

export const logoutUser = createAsyncThunk<
  LogoutUserResponse,
  void,
  { rejectValue: string }
>("auth/logoutUser", async (_, { rejectWithValue }) => {
  try {
    ShowToast({ description: "Logging out...", type: "success" });
    const response = await customFetch.delete("/auth/logout");
    localStorage.removeItem("user");
    sessionStorage.removeItem("user");
    ShowToast({ description: "Logged out successfully!", type: "success" });
    return response.data;
  } catch (error) {
    const errMsg = handleError(error);
    ShowToast({ description: errMsg, type: "error" });
    return rejectWithValue(errMsg);
  }
});

export const verifyEmail = createAsyncThunk<
  VerifyEmailResponse,
  VerifyEmailParams,
  { rejectValue: string }
>(
  "auth/verifyEmail",
  async ({ token, email, navigate }, { rejectWithValue }) => {
    try {
      ShowToast({ description: "Verifying email...", type: "success" });
      const response = await customFetch.post("/auth/verify-email", {
        verificationToken: token,
        email: email,
      });
      ShowToast({
        description: "Email verified successfully!",
        type: "success",
      });
      setTimeout(() => navigate(ROUTES.LOGIN), 3000);
      return response.data;
    } catch (error) {
      const errMsg = handleError(error);
      ShowToast({ description: errMsg, type: "error" });
      return rejectWithValue(errMsg);
    }
  },
);

export const forgetPassword = createAsyncThunk<
  ForgetPasswordResponse,
  ForgetPasswordParams,
  { rejectValue: string }
>(
  "auth/forgetPassword",
  async ({ parsedData }: ForgetPasswordParams, { rejectWithValue }) => {
    try {
      const response = await customFetch.post(
        "/auth/forget-password",
        parsedData,
      );
      return response.data;
    } catch (error) {
      return rejectWithValue(handleError(error));
    }
  },
);

export const resetPassword = createAsyncThunk<
  ResetPasswordResponse,
  ResetPasswordParams,
  { rejectValue: string }
>(
  "auth/resetPassword",
  async (
    { parsedData, token, email }: ResetPasswordParams,
    { rejectWithValue },
  ) => {
    try {
      ShowToast({ description: "Resetting password...", type: "success" });
      const response = await customFetch.post("/auth/reset-password", {
        newPassword: parsedData.password,
        token,
        email,
      });
      ShowToast({ description: "Password reset successful!", type: "success" });
      return response.data;
    } catch (error) {
      const errMsg = handleError(error);
      ShowToast({ description: errMsg, type: "error" });
      return rejectWithValue(errMsg);
    }
  },
);

export const googleLoginUser = createAsyncThunk<
  IUser,
  { token: string; navigate: (path: string) => void },
  { rejectValue: string }
>("auth/googleLoginUser", async ({ token, navigate }, { rejectWithValue }) => {
  try {
    const response = await customFetch.post<IUser>("/auth/google", { token });
    const user = response.data; // same as login/register

    saveUserToStorage(user, true);

    ShowToast({
      description: "Logged in successfully!", // same toast as login
      type: "success",
    });

    navigate(ROUTES.HOME);
    return user;
  } catch (error) {
    const errMsg = handleError(error);
    ShowToast({ description: errMsg, type: "error" });
    return rejectWithValue(errMsg);
  }
});
