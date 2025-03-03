import React, { useMemo, useEffect, useState, useCallback } from "react";
import { useNavigate } from "react-router-dom";
import { IProfileFormData, IUserData } from "@/types/profile-types";
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
import { ProfileShimmer } from "@/components";

const AccountSetting = () => {
  const navigate = useNavigate();
  const dispatch = useAppDispatch();
  const { profile } = useAppSelector((state) => state.user);
  const initialState: IProfileFormData = useMemo(
    () => ({
      userId: profile?.userId?.toString() || "",
      firstName: profile?.firstName || "",
      lastName: profile?.lastName || "",
      email: profile?.email || "",
      pincode: profile?.address?.pincode?.toString() || "",
      city: profile?.address?.city || "",
      state: profile?.address?.state || "",
      country: profile?.address?.country || "",
      locality: profile?.address?.locality || "",
      avatarUrl: profile?.avatarUrl || "",
      avatarPublicId: profile?.avatarPublicId || "",
      mobileNumber: profile?.mobileNumber?.toString() || "",
    }),
    [profile],
  );
  const [profileFormCredentials, setProfileFormCredentials] =
    useState<IProfileFormData>(initialState);
  const [profileImage, setProfileImage] = useState<string | File | null>(
    profile?.avatarUrl || null,
  );
  const [isEditing, setIsEditing] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [isLoadingProfile, setIsLoadingProfile] = useState(true);
  const [isLoadingImage, setIsLoadingImage] = useState(false);
  useEffect(() => {
    dispatch(fetchProfile())
      .unwrap()
      .then((fetchedProfile: IUserData) => {
        setProfileFormCredentials({
          userId: fetchedProfile.userId?.toString() || "",
          firstName: fetchedProfile.firstName || "",
          lastName: fetchedProfile.lastName || "",
          email: fetchedProfile.email || "",
          pincode: fetchedProfile.address?.pincode?.toString() || "",
          city: fetchedProfile.address?.city || "",
          state: fetchedProfile.address?.state || "",
          country: fetchedProfile.address?.country || "",
          locality: fetchedProfile.address?.locality || "",
          avatarUrl: fetchedProfile.avatarUrl || "",
          avatarPublicId: fetchedProfile.avatarPublicId || "",
          mobileNumber: fetchedProfile.mobileNumber?.toString() || "",
        });
        setProfileImage(fetchedProfile.avatarUrl || null);
      })
      .catch((error: unknown) => {
        const errorMessage = handleError(error);
        showToast(errorMessage, "destructive");
      })
      .finally(() => {
        setIsLoadingProfile(false);
      });
  }, [dispatch]);
  const handleChange = useCallback((e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setProfileFormCredentials((prevState) => ({ ...prevState, [name]: value }));
  }, []);
  const handleImageChange = useCallback(
    (e: React.ChangeEvent<HTMLInputElement>) => {
      const file = e.target.files?.[0];
      if (file) {
        setIsLoadingImage(true);
        setTimeout(() => {
          setProfileImage(file);
          setIsLoadingImage(false);
        }, 1000); // Simulating async operation for image upload
      }
    },
    [],
  );

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    setIsSubmitting(true);
    try {
      const parsedData = profileFormSchema.parse(profileFormCredentials);
      const formData = new FormData();

      Object.entries(parsedData).forEach(([key, value]) => {
        formData.append(key, String(value));
      });

      if (profileImage instanceof File) {
        formData.append("profileImage", profileImage);
      }

      await dispatch(updateProfile(formData)).unwrap();
      showToast("Profile Updated Successfully 🥳");
      navigate(ROUTES.PROFILE);
      setIsEditing(false);
    } catch (error: unknown) {
      showToast(handleError(error), "destructive");
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <Card className="mt-6 h-full">
      <CardContent className="pt-6">
        {isLoadingProfile ? (
          <ProfileShimmer />
        ) : (
          <>
            <label
              className={`mx-auto flex h-32 w-32 cursor-pointer flex-col items-center justify-center rounded-full border border-dashed ${!isEditing && "cursor-not-allowed"}`}
            >
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
                  <span className="text-xs text-gray-500">
                    Upload your photo
                  </span>
                </>
              )}
              <input
                type="file"
                className="hidden"
                onChange={handleImageChange}
                disabled={!isEditing || isLoadingImage}
              />
              {isLoadingImage && <LoadingSpinner />}
            </label>

            <form
              onSubmit={handleSubmit}
              className="mt-6 grid grid-cols-1 gap-4 sm:grid-cols-2"
            >
              {[
                { label: "First Name", name: "firstName" },
                { label: "Last Name", name: "lastName" },
                { label: "Email", name: "email", readOnly: true },
                { label: "Phone Number", name: "mobileNumber" },
                { label: "Pincode", name: "pincode" },
                { label: "City", name: "city" },
                { label: "State", name: "state" },
                { label: "Country", name: "country" },
                { label: "Locality", name: "locality" },
              ].map(({ label, name, readOnly }) => (
                <div key={name}>
                  <Label htmlFor={name}>{label}</Label>
                  <Input
                    type="text"
                    id={name}
                    name={name}
                    value={
                      profileFormCredentials[name as keyof IProfileFormData] ||
                      ""
                    }
                    onChange={handleChange}
                    readOnly={readOnly || name === "email"}
                    disabled={!isEditing && name !== "email"}
                    className={
                      readOnly || name === "email" ? "bg-gray-100" : ""
                    }
                    placeholder={label}
                  />
                </div>
              ))}

              <div></div>

              <div>
                {isEditing ? (
                  <div className="space-x-2">
                    <Button
                      type="button"
                      variant="secondary"
                      onClick={() => setIsEditing(false)}
                    >
                      Cancel
                    </Button>
                    <Button type="submit" disabled={isSubmitting}>
                      {isSubmitting ? <LoadingSpinner /> : "Save"}
                    </Button>
                  </div>
                ) : (
                  <Button
                    type="button"
                    variant="default"
                    onClick={() => setIsEditing(true)}
                    className="w-full max-w-32"
                  >
                    Edit Profile
                  </Button>
                )}
              </div>
            </form>
          </>
        )}
      </CardContent>
    </Card>
  );
};

export default AccountSetting;
