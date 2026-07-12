/* eslint-disable @typescript-eslint/no-explicit-any */
import { createAsyncThunk } from "@reduxjs/toolkit";
import { IUser } from "@/types/auth-types";
import { customFetch } from "@/utils/customFetch";
import { getUserFromStorage, handleError, normalizeAuthUser, saveUserToStorage } from "@/utils/helpers";
import { ROUTES } from "@/utils/constants";
import {
  ApiResponse,
  AuthUserPayload,
  ForgetPasswordParams,
  ForgetPasswordResponse,
  IOtpLoginResponse,
  LoginUserParams,
  LogoutUserResponse,
  RegisterUserParams,
  RegisterUserResponse,
  ResetPasswordParams,
  ResetPasswordResponse,
  SendOtpResponse,
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
      const response = await customFetch.post<ApiResponse<AuthUserPayload>>(
        "/auth/login",
        parsedData,
      );
      const user = normalizeAuthUser(
        response.data?.data,
        response.data?.message || "Logged in successfully!",
      );
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
  { message: string; data: null },
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
      return { message: response.data.message, data: null };
    } catch (error) {
      const errMsg = handleError(error);
      ShowToast({ description: errMsg, type: "error" });
      return rejectWithValue(errMsg);
    }
  },
);

export const logoutUser = createAsyncThunk<
  { message: string; data: null },
  void,
  { rejectValue: string }
>("auth/logoutUser", async (_, { rejectWithValue }) => {
  try {
    ShowToast({ description: "Logging out...", type: "success" });
    const storedUser = getUserFromStorage();
    const refreshToken = storedUser?.data?.refreshToken ?? null;

    if (refreshToken) {
      await customFetch.delete<LogoutUserResponse>("/auth/logout", {
        data: { refreshToken },
      });
    }

    localStorage.removeItem("user");
    sessionStorage.removeItem("user");
    localStorage.removeItem("tempSignupToken");
    sessionStorage.removeItem("tempSignupToken");
    localStorage.removeItem("gh_oauth_state");
    sessionStorage.removeItem("gh_oauth_state");
    localStorage.removeItem("google_oauth_state");
    sessionStorage.removeItem("google_oauth_state");

    ShowToast({ description: "Logged out successfully!", type: "success" });
    return { message: "Logged out successfully!", data: null };
  } catch (error) {
    const errMsg = handleError(error);
    localStorage.removeItem("user");
    sessionStorage.removeItem("user");
    localStorage.removeItem("tempSignupToken");
    sessionStorage.removeItem("tempSignupToken");
    localStorage.removeItem("gh_oauth_state");
    sessionStorage.removeItem("gh_oauth_state");
    localStorage.removeItem("google_oauth_state");
    sessionStorage.removeItem("google_oauth_state");

    ShowToast({ description: errMsg, type: "error" });
    return rejectWithValue(errMsg);
  }
});

export const verifyEmail = createAsyncThunk<
  { message: string; data: { success: boolean } },
  VerifyEmailParams,
  { rejectValue: string }
>("auth/verifyEmail", async ({ token, navigate }, { rejectWithValue }) => {
  try {
    ShowToast({ description: "Verifying email...", type: "success" });
    const response = await customFetch.post<VerifyEmailResponse>(
      "/auth/verify-email",
      {
        verificationToken: token,
      },
    );
    ShowToast({
      description: "Email verified successfully!",
      type: "success",
    });
    setTimeout(() => navigate(ROUTES.LOGIN), 3000);
    return {
      message: response.data.message,
      data: { success: response.data.data.success },
    };
  } catch (error) {
    const errMsg = handleError(error);
    ShowToast({ description: errMsg, type: "error" });
    return rejectWithValue(errMsg);
  }
});

export const forgetPassword = createAsyncThunk<
  { message: string; data: null },
  ForgetPasswordParams,
  { rejectValue: string }
>(
  "auth/forgetPassword",
  async ({ parsedData }: ForgetPasswordParams, { rejectWithValue }) => {
    try {
      const response = await customFetch.post<ForgetPasswordResponse>(
        "/auth/forget-password",
        parsedData,
      );
      return { message: response.data.message, data: null };
    } catch (error) {
      return rejectWithValue(handleError(error));
    }
  },
);

export const resetPassword = createAsyncThunk<
  { message: string; data: null },
  ResetPasswordParams,
  { rejectValue: string }
>(
  "auth/resetPassword",
  async ({ parsedData, token }: ResetPasswordParams, { rejectWithValue }) => {
    try {
      const response = await customFetch.post<ResetPasswordResponse>(
        "/auth/reset-password",
        {
          newPassword: parsedData.password,
          token,
        },
      );
      ShowToast({ description: "Password reset successful!", type: "success" });
      return { message: response.data.message, data: null };
    } catch (error) {
      const errMsg = handleError(error);
      ShowToast({ description: errMsg, type: "error" });
      return rejectWithValue(errMsg);
    }
  },
);

export const googleLoginUser = createAsyncThunk<
  IUser,
  { code: string; state?: string; navigate: (path: string) => void },
  { rejectValue: string }
>("auth/googleLoginUser", async ({ code, state, navigate }, { rejectWithValue }) => {
  try {
    const payload: Record<string, string> = { code };
    if (state) payload.state = state;

    const response = await customFetch.post<ApiResponse<AuthUserPayload>>(
      "/oauth/google",
      payload,
    );
    const user = normalizeAuthUser(
      response.data?.data,
      response.data?.message || "Logged in successfully!",
    );

    saveUserToStorage(user, true);

    ShowToast({
      description: "Logged in successfully!",
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

export const githubLoginUser = createAsyncThunk<
  IUser,
  { code: string; state?: string; navigate: (path: string) => void },
  { rejectValue: string }
>(
  "auth/githubLoginUser",
  async ({ code, state, navigate }, { rejectWithValue }) => {
    try {
      const payload: Record<string, string> = { code };
      if (state) payload.state = state;
      const response = await customFetch.post<ApiResponse<AuthUserPayload>>(
        "/oauth/github",
        payload,
      );
      const user = normalizeAuthUser(
        response.data?.data,
        response.data?.message || "Logged in successfully!",
      );

      saveUserToStorage(user, true);

      ShowToast({
        description: "Logged in successfully!",
        type: "success",
      });

      navigate(ROUTES.HOME);
      return user;
    } catch (error) {
      const errMsg = handleError(error);
      ShowToast({ description: errMsg, type: "error" });
      return rejectWithValue(errMsg);
    }
  },
);

export const sendOtp = createAsyncThunk<
  SendOtpResponse,
  { phone: string },
  { rejectValue: string }
>("auth/sendOtp", async ({ phone }, { rejectWithValue }) => {
  try {
    const response = await customFetch.post<SendOtpResponse>("/oauth/send-otp", {
      phone,
    });
    ShowToast({ description: response.data.message, type: "success" });
    return response.data;
  } catch (err) {
    console.error("sendOtp error:", err);
    const msg = handleError(err);
    ShowToast({ description: msg, type: "error" });
    return rejectWithValue(msg);
  }
});

export const verifyOtp = createAsyncThunk<
  { message: string; data: IOtpLoginResponse },
  { phone: string; otp: string },
  { rejectValue: string }
>("auth/verifyOtp", async ({ phone, otp }, { rejectWithValue }) => {
  try {
    const response = await customFetch.post<ApiResponse<IOtpLoginResponse>>(
      "/oauth/verify-otp",
      { phone, otp },
    );

    const { data, message } = response.data;
    if (data.newUser) {
      const tempToken = data.accessToken ?? data.token ?? data.tempToken ?? null;
      if (tempToken) {
        localStorage.setItem("tempSignupToken", tempToken);
      }
    }

    return { message, data };
  } catch (err) {
    return rejectWithValue(handleError(err));
  }
});

export const completeProfile = createAsyncThunk<
  IUser,
  { phone: string; firstName: string; lastName: string; email: string },
  { rejectValue: string }
>("auth/completeProfile", async (payload, { rejectWithValue }) => {
  try {
    const tempToken = localStorage.getItem("tempSignupToken");
    const config = tempToken
      ? { headers: { Authorization: `Bearer ${tempToken}` } }
      : {};

    const response = await customFetch.post<ApiResponse<AuthUserPayload>>(
      "/oauth/complete-profile",
      payload,
      config,
    );
    const respData = normalizeAuthUser(
      response.data?.data,
      response.data?.message || "Profile completed successfully!",
    );

    saveUserToStorage(respData, true);
    localStorage.removeItem("tempSignupToken");

    return respData;
  } catch (err) {
    return rejectWithValue(handleError(err));
  }
});
