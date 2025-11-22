import { createAsyncThunk } from "@reduxjs/toolkit";
import { IProfileData } from "@/types/profile-types";
import { customFetch } from "@/utils/customFetch";
import { handleError } from "@/utils/helpers";
import {
  IDeleteProfileResponse,
  IUpdatePasswordRequest,
  IUpdatePasswordResponse,
} from "@/types/profile-thunk-types";
import ShowToast from "@/components/shared/ShowToast";

// Fetch Profile
export const fetchProfile = createAsyncThunk<
  IProfileData,
  void,
  { rejectValue: string }
>("user/fetchProfile", async (_, { rejectWithValue }) => {
  try {
    const response = await customFetch.get("/profile");
    return response.data.data;
  } catch (error) {
    const errMsg = handleError(error);
    ShowToast({ description: errMsg, type: "error" });
    return rejectWithValue(errMsg);
  }
});

// Update Profile
export const updateProfile = createAsyncThunk<
  IProfileData,
  FormData,
  { rejectValue: string }
>("user/updateProfile", async (formData: FormData, { rejectWithValue }) => {
  try {
    const response = await customFetch.put<IProfileData>("/profile", formData);
    ShowToast({
      description: "Profile updated successfully!",
      type: "success",
    });
    return response.data;
  } catch (error) {
    const errMsg = handleError(error);
    ShowToast({ description: errMsg, type: "error" });
    return rejectWithValue(errMsg);
  }
});

// Delete Profile
export const deleteProfile = createAsyncThunk<
  IDeleteProfileResponse,
  void,
  { rejectValue: string }
>("user/deleteProfile", async (_, { rejectWithValue }) => {
  try {
    const response =
      await customFetch.delete<IDeleteProfileResponse>("/profile");
    ShowToast({ description: response.data.message, type: "success" });
    return response.data;
  } catch (error) {
    const errMsg = handleError(error);
    ShowToast({ description: errMsg, type: "error" });
    return rejectWithValue(errMsg);
  }
});

// Update Password
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
    ShowToast({
      description: "Password updated successfully!",
      type: "success",
    });
    return response.data;
  } catch (error) {
    const errMsg = handleError(error);
    ShowToast({ description: errMsg, type: "error" });
    return rejectWithValue(errMsg);
  }
});
