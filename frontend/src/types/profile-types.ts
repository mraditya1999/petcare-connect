// export interface Address {
//   addressId: number;
//   pincode: number;
//   city: string;
//   state: string;
//   country: string;
//   locality: string;
// }

// export interface IProfile {
//   userId: string;
//   firstName: string;
//   lastName: string;
//   email: string;
//   address: Address;
//   avatarUrl: string;
//   avatarPublicId: string;
//   mobileNumber: string;
//   role: "USER" | "ADMIN" | "SPECIALIST";
// }

// export interface IProfileState {
//   profile: IProfile | null;
//   loading: boolean;
//   error: string | null;
//   success: string | null;
// }
export interface Address {
  pincode: string;
  city: string;
  state: string;
  country: string;
  locality: string;
}

export interface IProfile {
  userId: string;
  firstName: string;
  lastName: string;
  email: string;
  address: Address;
  avatarUrl: string;
  avatarPublicId: string;
  mobileNumber: string;
  roles: Array<"USER" | "ADMIN" | "SPECIALIST">;
}

export interface IProfileState {
  profile: IProfile | null;
  loading: boolean;
  error: string | null;
  success: string | null;
}


