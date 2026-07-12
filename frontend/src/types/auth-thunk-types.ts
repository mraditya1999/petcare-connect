import { NavigateFunction } from "react-router-dom";
import {
  IForgetPasswordCredentials,
  ILoginCredentials,
  IRegisterCredentials,
  IResetPasswordCredentials,
} from "./auth-types";

export interface ApiResponse<T> {
  message: string;
  data: T;
}

export interface AuthUserPayload {
  userId?: string | number | null;
  email?: string | null;
  roles?: Array<"USER" | "ADMIN" | "SPECIALIST">;
  token?: string | null;
  refreshToken?: string | null;
  jwtToken?: string | null;
  accessToken?: string | null;
  oauthProvider?: string | null;
  newUser?: boolean;
  profileComplete?: boolean;
  isProfileComplete?: boolean;
  tempToken?: string | null;
  firstName?: string | null;
  lastName?: string | null;
  verified?: boolean;
}

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

export interface RegisterUserResponse {
  message: string;
  data: null;
}

export interface LogoutUserResponse {
  message: string;
  data: null;
}

export interface VerifyEmailResponse {
  message: string;
  data: {
    success: boolean;
  };
}

export interface ForgetPasswordResponse {
  message: string;
  data: null;
}

export interface ResetPasswordResponse {
  message: string;
  data: null;
}

export interface SendOtpParams {
  phone: string;
}

export interface SendOtpResponse extends ApiResponse<{ phone: string }> {}

export interface VerifyOtpParams {
  phone: string;
  otp: string;
}

export interface VerifyOtpResponse extends ApiResponse<IOtpLoginResponse> {}

export interface IOtpLoginResponse extends AuthUserPayload {
  newUser?: boolean;
  tempToken?: string | null;
  accessToken?: string | null;
  token?: string | null;
}
