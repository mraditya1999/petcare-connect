import { useEffect, useState, useCallback, useRef } from "react";
import { useNavigate } from "react-router-dom";
import { IProfileFormData, IUserData } from "@/types/profile-types";
import { ROUTES } from "@/utils/constants";
import { ApiResponse } from "@/types/api";
import { customFetch } from "@/utils/customFetch";
import {
  fetchExternalCities,
  fetchExternalCountries,
  fetchExternalPincodes,
  fetchExternalStates,
} from "@/utils/locationApi";
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
];

const addressFields = [
  { label: "Country", name: "country" },
  { label: "State", name: "state" },
  { label: "City", name: "city" },
  { label: "Pincode", name: "pincode" },
];

const mapProfileToForm = (profileData: IUserData | null): IProfileFormData => {
  const rawMobile = profileData?.mobileNumber?.toString() || "";
  const mobileWithoutPrefix = rawMobile.startsWith("+91")
    ? rawMobile.slice(3)
    : rawMobile;

  return {
    userId: profileData?.userId?.toString() || "",
    firstName: profileData?.firstName || "",
    lastName: profileData?.lastName || "",
    email: profileData?.email || "",
    pincode: profileData?.address?.pincode?.toString() || "",
    city: profileData?.address?.city || "",
    state: profileData?.address?.state || "",
    country: profileData?.address?.country || "",
    avatarUrl: profileData?.avatarUrl || "",
    avatarPublicId: profileData?.avatarPublicId || "",
    mobileNumber: mobileWithoutPrefix,
  };
};

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

  const [countryOptions, setCountryOptions] = useState<string[]>([]);
  const [stateOptions, setStateOptions] = useState<string[]>([]);
  const [cityOptions, setCityOptions] = useState<string[]>([]);
  const [pincodeOptions, setPincodeOptions] = useState<string[]>([]);
  const [formErrors, setFormErrors] = useState<Record<string, string>>({});

  const originalProfileRef = useRef<IProfileFormData | null>(null);

  useEffect(() => {
    const loadProfile = async () => {
      try {
        const { data: fetchedProfile } =
          await dispatch(fetchProfile()).unwrap();
        const mappedProfile = mapProfileToForm(fetchedProfile);

        setProfileForm(mappedProfile);
        setProfileImage(fetchedProfile.avatarUrl || null);

        originalProfileRef.current = mappedProfile;
      } catch (error) {
        console.error("Failed to load profile:", error);
      } finally {
        setIsLoadingProfile(false);
      }
    };

    loadProfile();
  }, [dispatch]);

  const handleChange = useCallback(
    (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
      const { name, value } = e.target;
      setProfileForm((prev) => {
        if (name === "country") {
          return { ...prev, country: value, state: "", city: "", pincode: "" };
        }
        if (name === "state") {
          return { ...prev, state: value, city: "", pincode: "" };
        }
        if (name === "city") {
          return { ...prev, city: value, pincode: "" };
        }
        return { ...prev, [name]: value };
      });

      if (formErrors[name]) {
        setFormErrors((prev) => {
          const newErrors = { ...prev };
          delete newErrors[name];
          return newErrors;
        });
      }
    },
    [formErrors],
  );

  useEffect(() => {
    const fetchCountries = async () => {
      try {
        const countries = await fetchExternalCountries();
        setCountryOptions(countries);
      } catch (err) {
        console.error("Failed to fetch countries from external API", err);
      }
    };
    fetchCountries();
  }, []);

  useEffect(() => {
    if (!profileForm.country) {
      setStateOptions([]);
      setCityOptions([]);
      setPincodeOptions([]);
      return;
    }

    const fetchStates = async () => {
      try {
        const states = await fetchExternalStates(profileForm.country);
        setStateOptions(states);
      } catch (err) {
        console.error("Failed to fetch states from external API", err);
        setStateOptions([]);
      }
    };

    fetchStates();
  }, [profileForm.country]);

  useEffect(() => {
    if (!profileForm.country || !profileForm.state) {
      setCityOptions([]);
      setPincodeOptions([]);
      return;
    }

    const fetchCities = async () => {
      try {
        const cities = await fetchExternalCities(
          profileForm.country,
          profileForm.state,
        );
        setCityOptions(cities);
      } catch (err) {
        console.error("Failed to fetch cities from external API", err);
        setCityOptions([]);
      }
    };

    fetchCities();
  }, [profileForm.country, profileForm.state]);

  useEffect(() => {
    if (!profileForm.country || !profileForm.state || !profileForm.city) {
      setPincodeOptions([]);
      return;
    }

    const fetchPincodes = async () => {
      try {
        const pincodes = await fetchExternalPincodes(
          profileForm.country,
          profileForm.state,
          profileForm.city,
        );
        setPincodeOptions(pincodes);
      } catch (err) {
        console.error("Failed to fetch pincodes from external API", err);
        setPincodeOptions([]);
      }
    };

    fetchPincodes();
  }, [profileForm.country, profileForm.state, profileForm.city]);

  const handleImageChange = useCallback(
    (e: React.ChangeEvent<HTMLInputElement>) => {
      const file = e.target.files?.[0];
      if (!file) return;

      setIsLoadingImage(true);
      setProfileImage(file);
      setTimeout(() => setIsLoadingImage(false), 300);
    },
    [],
  );

  const handleCancel = () => {
    if (originalProfileRef.current) {
      setProfileForm(originalProfileRef.current);
      setProfileImage(originalProfileRef.current.avatarUrl || null);
    }
    setIsEditing(false);
    setFormErrors({});
  };

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    setIsSubmitting(true);
    setFormErrors({});

    try {
      const normalizedForm = {
        ...profileForm,
        mobileNumber: profileForm.mobileNumber || undefined,
        pincode: profileForm.pincode || undefined,
        city: profileForm.city || undefined,
        state: profileForm.state || undefined,
        country: profileForm.country || undefined,
      };

      const parsedData = profileFormSchema.parse(normalizedForm);

      const formData = new FormData();
      Object.entries(parsedData).forEach(([key, value]) => {
        if (value === undefined || value === null) return;
        formData.append(key, String(value));
      });

      if (profileImage instanceof File) {
        formData.append("profileImage", profileImage);
      }

      await dispatch(updateProfile(formData)).unwrap();

      const { data: updatedProfile } = await dispatch(fetchProfile()).unwrap();
      const mappedUpdatedProfile = mapProfileToForm(updatedProfile);
      originalProfileRef.current = mappedUpdatedProfile;
      setProfileForm(mappedUpdatedProfile);
      setProfileImage(updatedProfile.avatarUrl || null);

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
        console.error("Profile update failed:", error);
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
              {isLoadingImage && isEditing ? (
                <LoadingSpinner />
              ) : previewSrc ? (
                <img
                  src={previewSrc}
                  alt="Profile"
                  className="h-full w-full rounded-full object-cover"
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
                accept="image/*"
                onChange={handleImageChange}
                disabled={!isEditing || isLoadingImage}
              />
            </label>

            <form
              onSubmit={handleSubmit}
              className="mt-6 grid grid-cols-1 gap-4 sm:grid-cols-2"
            >
              {fields.map(({ label, name, readOnly }) => (
                <div key={name}>
                  <Label htmlFor={name}>{label}</Label>

                  {name === "mobileNumber" ? (
                    <div className="flex items-center rounded-md border border-gray-300 dark:border-gray-700">
                      <span className="px-2 text-sm text-gray-600 dark:text-gray-300">
                        +91
                      </span>
                      <Input
                        id={name}
                        name={name}
                        value={
                          profileForm[name as keyof IProfileFormData] || ""
                        }
                        onChange={handleChange}
                        disabled={!isEditing}
                        className="border-0 bg-transparent focus-visible:ring-0"
                      />
                    </div>
                  ) : (
                    <Input
                      id={name}
                      name={name}
                      value={profileForm[name as keyof IProfileFormData] || ""}
                      onChange={handleChange}
                      readOnly={readOnly || name === "email"}
                      disabled={!isEditing && name !== "email"}
                    />
                  )}

                  {formErrors[name] && (
                    <p className="mt-1 text-xs text-red-500">
                      {formErrors[name]}
                    </p>
                  )}
                </div>
              ))}

              {addressFields.map(({ label, name }) => (
                <div key={name}>
                  <Label htmlFor={name}>{label}</Label>
                  {name === "country" ? (
                    <select
                      id={name}
                      name={name}
                      value={profileForm.country}
                      onChange={handleChange}
                      className="w-full rounded-md border border-gray-300 px-2 py-1"
                      disabled={!isEditing}
                    >
                      <option value="">Select Country</option>
                      {countryOptions.map((country) => (
                        <option key={country} value={country}>
                          {country}
                        </option>
                      ))}
                    </select>
                  ) : name === "state" ? (
                    <select
                      id={name}
                      name={name}
                      value={profileForm.state}
                      onChange={handleChange}
                      className="w-full rounded-md border border-gray-300 px-2 py-1"
                      disabled={!isEditing || !profileForm.country}
                    >
                      <option value="">Select State</option>
                      {stateOptions.map((state) => (
                        <option key={state} value={state}>
                          {state}
                        </option>
                      ))}
                    </select>
                  ) : name === "city" ? (
                    <select
                      id={name}
                      name={name}
                      value={profileForm.city}
                      onChange={handleChange}
                      className="w-full rounded-md border border-gray-300 px-2 py-1"
                      disabled={!isEditing || !profileForm.state}
                    >
                      <option value="">Select City</option>
                      {cityOptions.map((city) => (
                        <option key={city} value={city}>
                          {city}
                        </option>
                      ))}
                    </select>
                  ) : (
                    <div className="flex gap-2">
                      <select
                        id={name}
                        name={name}
                        value={profileForm.pincode}
                        onChange={handleChange}
                        className="w-full rounded-md border border-gray-300 px-2 py-1"
                        disabled={!isEditing || !profileForm.city}
                      >
                        <option value="">Select Pincode</option>
                        {pincodeOptions.map((pin) => (
                          <option key={pin} value={pin}>
                            {pin}
                          </option>
                        ))}
                      </select>
                      <Input
                        id="pincode"
                        name="pincode"
                        value={profileForm.pincode || ""}
                        onChange={handleChange}
                        disabled={!isEditing}
                        placeholder="Or type pincode"
                      />
                    </div>
                  )}

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
