import { IProfileData } from "@/types/profile-types";

export interface FetchProfileResponse {
  message: string;
  data: IProfileData;
}

export interface IUpdateProfileResponse {
  profile: IProfileData;
}

export interface IDeleteProfileResponse {
  message: string;
}

export interface IDeleteProfileResponse {
  message: string;
}

export interface IUpdatePasswordRequest {
  currentPassword: string;
  newPassword: string;
  confirmPassword: string;
}

export interface IUpdatePasswordResponse {
  message: string;
}

export interface IUpdateProfile {
  firstName: string;
  lastName: string;
  email: string;
  mobileNumber?: string | null;
  pincode?: string;
  city?: string;
  state?: string;
  country?: string;
  locality?: string;
}
