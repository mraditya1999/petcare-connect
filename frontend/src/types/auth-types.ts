export interface IUser {
  userId: string;
  email: string;
  role: "USER" | "ADMIN" | "SPECIALIST";
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
