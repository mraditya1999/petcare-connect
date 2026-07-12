export type UserRole = "USER" | "ADMIN" | "SPECIALIST";
export type OAuthProvider = "GOOGLE" | "LOCAL" | "GITHUB" | "MOBILE" | null;

export interface IUserData {
  userId: string | number | null;
  email: string | null;
  roles: UserRole[];
  token: string | null;
  refreshToken?: string | null;
  oauthProvider: OAuthProvider;
  newUser: boolean;
  isProfileComplete: boolean;
  tempToken?: string | null;
  firstName?: string | null;
  lastName?: string | null;
  profileComplete?: boolean;
  verified?: boolean;
}

export interface IUser {
  message: string;
  data: IUserData;
}

export interface IUserState {
  user: IUser | null;
  loading: boolean;
  error: string | null;
  success: string | null;
}

export type ILoginCredentials = {
  email: string;
  password: string;
};

export interface IRegisterCredentials {
  firstName: string;
  lastName: string;
  email: string;
  password: string;
}

export interface IForgetPasswordCredentials {
  email: string;
}

export interface IResetPasswordCredentials {
  password: string;
  confirmPassword: string;
}
