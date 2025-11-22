import { useEffect, useState, useCallback } from "react";
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
import { fetchProfile, updateProfile } from "@/features/user/userThunk";
import { useAppDispatch, useAppSelector } from "@/app/hooks";
import { ProfileShimmer } from "@/components";
import { ZodError } from "zod";

const fields = [
  { label: "First Name", name: "firstName" },
  { label: "Last Name", name: "lastName" },
  { label: "Email", name: "email", readOnly: true },
  { label: "Phone Number", name: "mobileNumber" },
  { label: "Pincode", name: "pincode" },
  { label: "City", name: "city" },
  { label: "State", name: "state" },
  { label: "Country", name: "country" },
  { label: "Locality", name: "locality" },
];

const mapProfileToForm = (profileData: IUserData | null): IProfileFormData => ({
  userId: profileData?.userId?.toString() || "",
  firstName: profileData?.firstName || "",
  lastName: profileData?.lastName || "",
  email: profileData?.email || "",
  pincode: profileData?.address?.pincode?.toString() || "",
  city: profileData?.address?.city || "",
  state: profileData?.address?.state || "",
  country: profileData?.address?.country || "",
  locality: profileData?.address?.locality || "",
  avatarUrl: profileData?.avatarUrl || "",
  avatarPublicId: profileData?.avatarPublicId || "",
  mobileNumber: profileData?.mobileNumber?.toString() || "",
});

const AccountSetting = () => {
  const navigate = useNavigate();
  const dispatch = useAppDispatch();
  const { profile } = useAppSelector((state) => state.user);

  const [profileForm, setProfileForm] = useState<IProfileFormData>(
    mapProfileToForm(profile),
  );
  const [profileImage, setProfileImage] = useState<File | string | null>(
    profile?.avatarUrl || null,
  );
  const [isEditing, setIsEditing] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [isLoadingProfile, setIsLoadingProfile] = useState(true);
  const [isLoadingImage, setIsLoadingImage] = useState(false);
  const [formErrors, setFormErrors] = useState<Record<string, string>>({});

  // Fetch profile
  useEffect(() => {
    const loadProfile = async () => {
      try {
        const fetchedProfile = await dispatch(fetchProfile()).unwrap();
        setProfileForm(mapProfileToForm(fetchedProfile));
        setProfileImage(fetchedProfile.avatarUrl || null);
      } catch (error) {
        console.error(error);
      } finally {
        setIsLoadingProfile(false);
      }
    };
    loadProfile();
  }, [dispatch]);

  const handleChange = useCallback((e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setProfileForm((prev) => ({ ...prev, [name]: value }));
  }, []);

  const handleImageChange = useCallback(
    (e: React.ChangeEvent<HTMLInputElement>) => {
      const file = e.target.files?.[0];
      if (!file) return;
      setIsLoadingImage(true);
      setProfileImage(file);
      setTimeout(() => setIsLoadingImage(false), 500); 
    },
    [],
  );

  const handleCancel = () => {
    setProfileForm(mapProfileToForm(profile));
    setProfileImage(profile?.avatarUrl || null);
    setIsEditing(false);
  };

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    setIsSubmitting(true);
    setFormErrors({});

    try {
      const parsedData = profileFormSchema.parse(profileForm);
      const formData = new FormData();
      Object.entries(parsedData).forEach(([key, value]) =>
        formData.append(key, String(value)),
      );
      if (profileImage instanceof File)
        formData.append("profileImage", profileImage);

      await dispatch(updateProfile(formData)).unwrap();
      setIsEditing(false);
      navigate(ROUTES.PROFILE);
    } catch (error: unknown) {
      if (error instanceof ZodError) {
        const errors: Record<string, string> = {};
        error.issues.forEach((issue) => {
          errors[issue.path[0] as string] = issue.message;
        });
        setFormErrors(errors);
      } else {
        console.error(error);
      }
    } finally {
      setIsSubmitting(false);
    }
  };

  const previewSrc =
    typeof profileImage === "string"
      ? profileImage
      : profileImage
        ? URL.createObjectURL(profileImage)
        : null;

  return (
    <Card className="mt-6 h-full bg-white text-gray-900 dark:bg-gray-900 dark:text-gray-100">
      <CardContent className="pt-6">
        {isLoadingProfile ? (
          <ProfileShimmer />
        ) : (
          <>
            <label
              className={`mx-auto flex h-32 w-32 cursor-pointer flex-col items-center justify-center rounded-full border border-dashed border-gray-300 dark:border-gray-600 ${
                !isEditing && "cursor-not-allowed"
              }`}
            >
              {previewSrc ? (
                <img
                  src={previewSrc}
                  alt="Profile"
                  className="h-full w-full rounded-lg object-cover"
                />
              ) : (
                <>
                  <UploadIcon className="h-6 w-6 text-gray-400 dark:text-gray-300" />
                  <span className="text-xs text-gray-500 dark:text-gray-300">
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
              {fields.map(({ label, name, readOnly }) => (
                <div key={name}>
                  <Label
                    htmlFor={name}
                    className="text-gray-700 dark:text-gray-300"
                  >
                    {label}
                  </Label>
                  <Input
                    type="text"
                    id={name}
                    name={name}
                    value={profileForm[name as keyof IProfileFormData] || ""}
                    onChange={handleChange}
                    readOnly={readOnly || name === "email"}
                    disabled={!isEditing && name !== "email"}
                    className={`${
                      readOnly || name === "email"
                        ? "bg-gray-100 dark:bg-gray-800"
                        : "bg-white dark:border-gray-700 dark:bg-gray-800 dark:text-gray-100"
                    }`}
                    placeholder={label}
                  />
                  {formErrors[name] && (
                    <p className="mt-1 text-xs text-red-500">
                      {formErrors[name]}
                    </p>
                  )}
                </div>
              ))}
              <div></div>
              <div>
                {isEditing ? (
                  <div className="space-x-2">
                    <Button
                      type="button"
                      variant="secondary"
                      onClick={handleCancel}
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
