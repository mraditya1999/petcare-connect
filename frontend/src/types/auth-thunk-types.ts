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
  email: string;
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
}

export interface LogoutUserResponse {
  message: string;
}

export interface VerifyEmailResponse {
  message: string;
}

export interface ForgetPasswordResponse {
  message: string;
}

export interface ResetPasswordResponse {
  message: string;
}
