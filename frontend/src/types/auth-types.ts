export interface IUser {
  message: string;
  data: {
    userId: string;
    email: string;
    roles: Array<"USER" | "ADMIN" | "SPECIALIST">;
    token: string;
    oauthProvider: "GOOGLE" | "LOCAL";
  };
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
