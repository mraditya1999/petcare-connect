export interface IUser {
  message: string;
  data: {
    userId: string | number | null;
    email: string | null;
    roles: ("USER" | "ADMIN" | "SPECIALIST")[];
    token: string | null;
    oauthProvider: "GOOGLE" | "LOCAL" | "GITHUB" | "MOBILE" | null;
    newUser: boolean;
    profileComplete: boolean;
    tempToken?: string | null;
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
