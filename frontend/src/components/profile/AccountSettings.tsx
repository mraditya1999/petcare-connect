import React, { useMemo, useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { IProfile } from "@/types/profile-types";
import { ROUTES } from "@/utils/constants";
import { Label } from "@/components/ui/label";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { LoadingSpinner } from "@/components/ui/LoadingSpinner";
import { profileFormSchema } from "@/utils/validations";
import { Card, CardContent } from "@/components/ui/card";
import { UploadIcon } from "lucide-react";
import { handleError, showToast } from "@/utils/helpers";
import { fetchProfile, updateProfile } from "@/features/user/userThunk";
import { useAppDispatch, useAppSelector } from "@/app/hooks";

const AccountSetting = () => {
  const navigate = useNavigate();
  const dispatch = useAppDispatch();
  const { loading, profile } = useAppSelector((state) => state.user);

  const initialState: IProfile = useMemo(
    () => ({
      userId: profile?.userId || "",
      firstName: profile?.firstName || "",
      lastName: profile?.lastName || "",
      email: profile?.email || "",
      pincode: profile?.address?.pincode || "",
      city: profile?.address?.city || "",
      state: profile?.address?.state || "",
      country: profile?.address?.country || "",
      locality: profile?.address?.locality || "",
      avatarUrl: profile?.avatarUrl || "",
      avatarPublicId: profile?.avatarPublicId || "",
      mobileNumber: profile?.mobileNumber || "",
      roles: profile?.roles || ["USER"],
    }),
    [profile],
  );

  const [profileFormCredentials, setProfileFormCredentials] =
    useState<IProfile>(initialState);
  const [profileImage, setProfileImage] = useState<string | File | null>(
    profile?.avatarUrl || null,
  );

  useEffect(() => {
    dispatch(fetchProfile())
      .unwrap()
      .then((fetchedProfile) => {
        setProfileFormCredentials({
          userId: fetchedProfile.userId || "",
          firstName: fetchedProfile.firstName || "",
          lastName: fetchedProfile.lastName || "",
          email: fetchedProfile.email || "",
          pincode: fetchedProfile.address?.pincode || "",
          city: fetchedProfile.address?.city || "",
          state: fetchedProfile.address?.state || "",
          country: fetchedProfile.address?.country || "",
          locality: fetchedProfile.address?.locality || "",
          avatarUrl: fetchedProfile.avatarUrl || "",
          avatarPublicId: fetchedProfile.avatarPublicId || "",
          mobileNumber: fetchedProfile.mobileNumber || "",
          roles: fetchedProfile.roles || ["USER"],
        });
        setProfileImage(fetchedProfile.avatarUrl || null);
      })
      .catch((error) => {
        const errorMessage = handleError(error);
        showToast(errorMessage, "destructive");
      });
  }, [dispatch]);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setProfileFormCredentials((prevState) => ({
      ...prevState,
      [name]: value,
    }));
  };

  const handleImageChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (file) {
      // Example of file type validation (allowing only image files)
      if (!file.type.startsWith("image/")) {
        showToast("Please upload a valid image file", "destructive");
        return;
      }

      // Example of file size validation (max size: 2MB)
      if (file.size > 5 * 1024 * 1024) {
        showToast(
          "File size exceeds 2MB. Please upload a smaller image.",
          "destructive",
        );
        return;
      }

      setProfileImage(file);
    }
  };

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    try {
      const dataToValidate = { ...profileFormCredentials };
      if (typeof dataToValidate.pincode === "number") {
        dataToValidate.pincode = String(dataToValidate.pincode);
      }
      const parsedData = profileFormSchema.parse(dataToValidate);
      const formData = new FormData();

      // Flatten the object while appending to formData
      Object.entries(parsedData).forEach(([key, value]) => {
        formData.append(key, String(value));
      });

      // Append profile image if it's a File
      if (profileImage instanceof File) {
        formData.append("profileImage", profileImage);
      }

      await dispatch(updateProfile(formData)).unwrap();
      showToast("Profile Updated Successfully 🥳");
      navigate(ROUTES.PROFILE);
    } catch (error) {
      const errorMessage = handleError(error);
      showToast(errorMessage, "destructive");
    }
  };

  return (
    <Card className="mt-6 h-full">
      <CardContent className="pt-6">
        <label className="mx-auto flex h-32 w-32 cursor-pointer flex-col items-center justify-center rounded-full border border-dashed">
          {profileImage ? (
            typeof profileImage === "string" ? (
              <img
                src={profileImage}
                alt="Profile"
                className="h-full w-full rounded-lg"
              />
            ) : (
              <img
                src={URL.createObjectURL(profileImage)}
                alt="Profile"
                className="h-full w-full rounded-lg"
              />
            )
          ) : (
            <>
              <UploadIcon className="h-6 w-6 text-gray-400" />
              <span className="text-xs text-gray-500">Upload your photo</span>
            </>
          )}
          <input type="file" className="hidden" onChange={handleImageChange} />
        </label>

        <form onSubmit={handleSubmit} className="mt-6 grid grid-cols-2 gap-4">
          <div>
            <Label className="mb-2 block text-sm font-medium">First Name</Label>
            <Input
              placeholder="Enter your first name"
              name="firstName"
              value={profileFormCredentials.firstName}
              onChange={handleChange}
            />
          </div>
          <div>
            <Label className="mb-2 block text-sm font-medium">Last Name</Label>
            <Input
              placeholder="Enter your last name"
              name="lastName"
              value={profileFormCredentials.lastName}
              onChange={handleChange}
            />
          </div>
          <div>
            <Label className="mb-2 block text-sm font-medium">Email</Label>
            <Input
              placeholder="Enter your email"
              name="email"
              value={profileFormCredentials.email}
              onChange={handleChange}
            />
          </div>
          <div>
            <Label className="mb-2 block text-sm font-medium">
              Phone Number
            </Label>
            <Input
              placeholder="Enter your phone number"
              name="mobileNumber"
              value={profileFormCredentials.mobileNumber}
              onChange={handleChange}
            />
          </div>
          <div>
            <Label className="mb-2 block text-sm font-medium">Pincode</Label>
            <Input
              placeholder="Enter your pincode"
              name="pincode"
              value={profileFormCredentials.pincode}
              onChange={handleChange}
            />
          </div>
          <div>
            <Label className="mb-2 block text-sm font-medium">City</Label>
            <Input
              placeholder="Enter your city"
              name="city"
              value={profileFormCredentials.city}
              onChange={handleChange}
            />
          </div>
          <div>
            <Label className="mb-2 block text-sm font-medium">State</Label>
            <Input
              placeholder="Enter your state"
              name="state"
              value={profileFormCredentials.state}
              onChange={handleChange}
            />
          </div>
          <div>
            <Label className="mb-2 block text-sm font-medium">Country</Label>
            <Input
              placeholder="Enter your country"
              name="country"
              value={profileFormCredentials.country}
              onChange={handleChange}
            />
          </div>
          <div>
            <Label className="mb-2 block text-sm font-medium">Locality</Label>
            <Input
              placeholder="Enter your locality"
              name="locality"
              value={profileFormCredentials.locality}
              onChange={handleChange}
            />
          </div>

          <div className="col-span-2 flex justify-between">
            <Button type="submit" className="px-4 py-2" disabled={loading}>
              {loading ? <LoadingSpinner /> : "Update Profile"}
            </Button>
          </div>
        </form>
      </CardContent>
    </Card>
  );
};

export default AccountSetting;
