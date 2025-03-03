interface IAddress {
  addressId: number;
  pincode: string;
  city: string;
  state: string;
  country: string;
  locality: string;
}

export interface IUserData {
  userId: number;
  firstName: string;
  lastName: string;
  email: string;
  address: IAddress;
  avatarUrl: string | null;
  avatarPublicId: string | null;
  mobileNumber: string | null;
}

export interface IProfileData {
  userId: number;
  firstName: string;
  lastName: string;
  email: string;
  address: IAddress;
  avatarUrl: string | null;
  avatarPublicId: string | null;
  mobileNumber: string | null;
}

export interface IProfileFormData {
  userId: string;
  firstName: string;
  lastName: string;
  email: string;
  pincode: string;
  city: string;
  state: string;
  country: string;
  locality: string;
  avatarUrl: string | null;
  avatarPublicId: string | null;
  mobileNumber: string | null;
}

export interface IProfileState {
  profile: IProfileData | null;
  loading: boolean;
  error: string | null;
  success: string | null;
}
