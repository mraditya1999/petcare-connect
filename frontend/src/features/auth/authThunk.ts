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
} from "@/types/thunk-types";

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
      return user;
    } catch (error) {
      console.log(error);
      return rejectWithValue(handleError(error));
    }
  },
);

export const registerUser = createAsyncThunk<
  RegisterUserResponse, // Return type
  RegisterUserParams, // First parameter type
  { rejectValue: string } // Extra argument type
>(
  "auth/registerUser",
  async ({ parsedData }: RegisterUserParams, { rejectWithValue }) => {
    try {
      const response = await customFetch.post<RegisterUserResponse>(
        "/auth/register",
        parsedData,
      );
      return response.data;
    } catch (error) {
      return rejectWithValue(handleError(error));
    }
  },
);

export const logoutUser = createAsyncThunk<
  LogoutUserResponse,
  void,
  { rejectValue: string }
>("auth/logoutUser", async (_, { rejectWithValue }) => {
  try {
    const response = await customFetch.delete("/auth/logout");
    localStorage.removeItem("user");
    sessionStorage.removeItem("user");
    return response.data;
  } catch (error) {
    return rejectWithValue(handleError(error));
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
      const response = await customFetch.post("/auth/verify-email", {
        verificationToken: token,
        email: email,
      });
      setTimeout(() => {
        navigate(ROUTES.LOGIN);
      }, 3000);
      return response.data;
    } catch (error) {
      return rejectWithValue(handleError(error));
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
      const response = await customFetch.post("/auth/reset-password", {
        newPassword: parsedData.password,
        token,
        email,
      });

      return response.data;
    } catch (error) {
      return rejectWithValue(handleError(error));
    }
  },
);
