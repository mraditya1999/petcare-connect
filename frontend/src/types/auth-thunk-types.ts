import { NavigateFunction } from "react-router-dom";
import {
  IForgetPasswordCredentials,
  ILoginCredentials,
  IRegisterCredentials,
  IResetPasswordCredentials,
  IUser,
} from "./auth-types";

export interface LoginUserParams {
  parsedData: ILoginCredentials;
  rememberMe: boolean;
}

export interface RegisterUserParams {
  parsedData: IRegisterCredentials;
}

export interface VerifyEmailParams {
  token: string;
  navigate: NavigateFunction;
}

export interface ForgetPasswordParams {
  parsedData: IForgetPasswordCredentials;
}

export interface ResetPasswordParams {
  parsedData: IResetPasswordCredentials;
  token: string | null;
  email: string | null;
}

export interface LoginUserResponse {
  user: IUser;
}

export interface RegisterUserResponse {
  message: string;
  data: {
    message: string;
  };
}

export interface LogoutUserResponse {
  message: string;
  data: {
    message: string;
  };
}

export interface VerifyEmailResponse {
  message: string;
  data: {
    message: string;
    success: boolean;
  };
}

export interface ForgetPasswordResponse {
  message: string;
  data: {
    message: string;
  };
}

export interface ResetPasswordResponse {
  message: string;
  data: {
    message: string;
  };
}

// ======================================================
// ⬇️ OTP LOGIN TYPES (🔔 FIXED & PROPERLY NAMED)
// ======================================================

// SEND OTP
export interface SendOtpParams {
  phone: string;
}

export interface SendOtpResponse {
  message: string;
  data: {
    phone: string;
  };
}

// VERIFY OTP
export interface VerifyOtpParams {
  phone: string;
  otp: string;
  navigate: NavigateFunction;
}

export interface UserLoginResponseDTO {
  message: string;
  token: string;
  refreshToken?: string;
  data: IUser["data"];
}

export interface VerifyOtpResponse {
  message: string;
  data: UserLoginResponseDTO;
}

export interface IOtpLoginResponse {
  userId: string | number;
  email: string | null;
  roles: Array<"USER" | "ADMIN" | "SPECIALIST">;
  token: string | null;
  oauthProvider: "GOOGLE" | "LOCAL" | "GITHUB" | "MOBILE" | null;
  isNewUser: boolean;
  isProfileComplete: boolean;
  tempToken?: string | null;
}
