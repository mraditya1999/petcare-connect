import { ApiResponse } from "@/types/api";
import { IProfileData } from "@/types/profile-types";

export interface FetchProfileResponse {
  message: string;
  data: IProfileData;
}

export type IUpdateProfileResponse = ApiResponse<IProfileData>;

export type IDeleteProfileResponse = ApiResponse<string>;

export interface IUpdatePasswordRequest {
  currentPassword: string;
  newPassword: string;
  confirmPassword: string;
}

export type IUpdatePasswordResponse = ApiResponse<string>;

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
