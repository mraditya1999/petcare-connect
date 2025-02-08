// import { createAsyncThunk } from "@reduxjs/toolkit";
// import { IProfile } from "@/types/profile-types";
// import { IDeleteProfileResponse } from "@/types/profile-thunk-types";
// import { customFetch } from "@/utils/customFetch";
// import { handleError } from "@/utils/helpers";

// export const fetchProfile = createAsyncThunk<
//   IProfile,
//   void,
//   { rejectValue: string }
// >("profile/fetchProfile", async (_, { rejectWithValue }) => {
//   try {
//     const response = await customFetch.get<IProfile>("/profile");
//     return response.data;
//   } catch (error) {
//     return rejectWithValue(handleError(error));
//   }
// });

// export const updateProfile = createAsyncThunk<
//   IProfile,
//   IProfile,
//   { rejectValue: string }
// >("profile/updateProfile", async (data: IProfile, { rejectWithValue }) => {
//   try {
//     const response = await customFetch.put<IProfile>("/profile", data);
//     return response.data;
//   } catch (error) {
//     return rejectWithValue(handleError(error));
//   }
// });

// export const deleteProfile = createAsyncThunk<
//   IDeleteProfileResponse,
//   void,
//   { rejectValue: string }
// >("profile/deleteProfile", async (_, { rejectWithValue }) => {
//   try {
//     const response =
//       await customFetch.delete<IDeleteProfileResponse>("/profile");
//     return response.data;
//   } catch (error) {
//     return rejectWithValue(handleError(error));
//   }
// });
import { createAsyncThunk } from "@reduxjs/toolkit";
import { IProfile } from "@/types/profile-types";
import { customFetch } from "@/utils/customFetch";
import { handleError } from "@/utils/helpers";
import {
  IDeleteProfileResponse,
  IUpdatePasswordRequest,
  IUpdatePasswordResponse,
  // IUpdateProfile,
} from "@/types/profile-thunk-types";

export const fetchProfile = createAsyncThunk<
  IProfile,
  void,
  { rejectValue: string }
>("user/fetchProfile", async (_, { rejectWithValue }) => {
  try {
    const response = await customFetch.get("/profile");
    const data: IProfile = response.data.data;
    return data;
  } catch (error) {
    return rejectWithValue(handleError(error));
  }
});

export const updateProfile = createAsyncThunk<
  IProfile,
  FormData,
  { rejectValue: string }
>("user/updateProfile", async (formData: FormData, { rejectWithValue }) => {
  try {
    const response = await customFetch.put<IProfile>("/profile", formData);
    console.log(response);
    return response.data;
  } catch (error) {
    return rejectWithValue(handleError(error));
  }
});

export const deleteProfile = createAsyncThunk<
  IDeleteProfileResponse,
  void,
  { rejectValue: string }
>("user/deleteProfile", async (_, { rejectWithValue }) => {
  try {
    const response =
      await customFetch.delete<IDeleteProfileResponse>("/profile");
    return response.data;
  } catch (error) {
    return rejectWithValue(handleError(error));
  }
});

export const updatePassword = createAsyncThunk<
  IUpdatePasswordResponse,
  IUpdatePasswordRequest,
  { rejectValue: string }
>("user/updatePassword", async (updatePasswordRequest, { rejectWithValue }) => {
  try {
    const response = await customFetch.put<IUpdatePasswordResponse>(
      "/profile/update-password",
      updatePasswordRequest,
    );
    return response.data;
  } catch (error) {
    return rejectWithValue(handleError(error));
  }
});
