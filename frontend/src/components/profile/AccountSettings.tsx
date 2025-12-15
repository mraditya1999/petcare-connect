// import { useEffect, useState, useCallback } from "react";
// import { useNavigate } from "react-router-dom";
// import { IProfileFormData, IUserData } from "@/types/profile-types";
// import { ROUTES } from "@/utils/constants";
// import { Label } from "@/components/ui/label";
// import { Input } from "@/components/ui/input";
// import { Button } from "@/components/ui/button";
// import { LoadingSpinner } from "@/components/ui/LoadingSpinner";
// import { profileFormSchema } from "@/utils/validations";
// import { Card, CardContent } from "@/components/ui/card";
// import { UploadIcon } from "lucide-react";
// import { fetchProfile, updateProfile } from "@/features/user/userThunk";
// import { useAppDispatch, useAppSelector } from "@/app/hooks";
// import { ProfileShimmer } from "@/components";
// import { ZodError } from "zod";

// const fields = [
//   { label: "First Name", name: "firstName" },
//   { label: "Last Name", name: "lastName" },
//   { label: "Email", name: "email", readOnly: true },
//   { label: "Phone Number", name: "mobileNumber" },
//   { label: "Pincode", name: "pincode" },
//   { label: "City", name: "city" },
//   { label: "State", name: "state" },
//   { label: "Country", name: "country" },
//   { label: "Locality", name: "locality" },
// ];

// const mapProfileToForm = (profileData: IUserData | null): IProfileFormData => {
//   const rawMobile = profileData?.mobileNumber?.toString() || "";
//   const mobileWithoutPrefix = rawMobile.startsWith("+91")
//     ? rawMobile.slice(3) // remove +91
//     : rawMobile;

//   return {
//     userId: profileData?.userId?.toString() || "",
//     firstName: profileData?.firstName || "",
//     lastName: profileData?.lastName || "",
//     email: profileData?.email || "",
//     pincode: profileData?.address?.pincode?.toString() || "",
//     city: profileData?.address?.city || "",
//     state: profileData?.address?.state || "",
//     country: profileData?.address?.country || "",
//     locality: profileData?.address?.locality || "",
//     avatarUrl: profileData?.avatarUrl || "",
//     avatarPublicId: profileData?.avatarPublicId || "",
//     mobileNumber: mobileWithoutPrefix, // ✅ only digits stored in form
//   };
// };
// const AccountSetting = () => {
//   const navigate = useNavigate();
//   const dispatch = useAppDispatch();
//   const { profile } = useAppSelector((state) => state.user);

//   const [profileForm, setProfileForm] = useState<IProfileFormData>(
//     mapProfileToForm(profile),
//   );
//   const [profileImage, setProfileImage] = useState<File | string | null>(
//     profile?.avatarUrl || null,
//   );
//   const [isEditing, setIsEditing] = useState(false);
//   const [isSubmitting, setIsSubmitting] = useState(false);
//   const [isLoadingProfile, setIsLoadingProfile] = useState(true);
//   const [isLoadingImage, setIsLoadingImage] = useState(false); // Used only during image selection/preview
//   const [formErrors, setFormErrors] = useState<Record<string, string>>({});
//   useEffect(() => {
//     const loadProfile = async () => {
//       try {
//         const fetchedProfile = await dispatch(fetchProfile()).unwrap();
//         setProfileForm(mapProfileToForm(fetchedProfile));
//         setProfileImage(fetchedProfile.avatarUrl || null);
//       } catch (error) {
//         console.error("Failed to load profile:", error);
//       } finally {
//         setIsLoadingProfile(false);
//       }
//     };
//     loadProfile();
//   }, [dispatch]);

//   const handleChange = useCallback(
//     (e: React.ChangeEvent<HTMLInputElement>) => {
//       const { name, value } = e.target;
//       setProfileForm((prev) => ({ ...prev, [name]: value }));
//       if (formErrors[name]) {
//         setFormErrors((prev) => {
//           const newErrors = { ...prev };
//           delete newErrors[name];
//           return newErrors;
//         });
//       }
//     },
//     [formErrors],
//   );

//   const handleImageChange = useCallback(
//     (e: React.ChangeEvent<HTMLInputElement>) => {
//       const file = e.target.files?.[0];
//       if (!file) return;

//       setIsLoadingImage(true);
//       setProfileImage(file);

//       setTimeout(() => setIsLoadingImage(false), 300);
//     },
//     [],
//   );

//   const handleCancel = () => {
//     setProfileForm(mapProfileToForm(profile));
//     setProfileImage(profile?.avatarUrl || null);
//     setIsEditing(false);
//     setFormErrors({});
//   };

//   const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
//     e.preventDefault();
//     setIsSubmitting(true);
//     setFormErrors({});

//     try {
//       // 1. Zod Validation
//       const parsedData = profileFormSchema.parse(profileForm);

//       // 2. FormData Construction (for file upload)
//       const formData = new FormData();

//       // Append form fields
//       Object.entries(parsedData).forEach(([key, value]) =>
//         formData.append(key, String(value)),
//       );

//       // Append new image file if it's a File object
//       if (profileImage instanceof File) {
//         formData.append("profileImage", profileImage);
//       }

//       await dispatch(updateProfile(formData)).unwrap();
//       setIsEditing(false);
//       navigate(ROUTES.PROFILE);
//     } catch (error: unknown) {
//       if (error instanceof ZodError) {
//         const errors: Record<string, string> = {};
//         error.issues.forEach((issue) => {
//           errors[issue.path[0] as string] = issue.message;
//         });
//         setFormErrors(errors);
//       } else {
//         console.error("Profile update failed:", error);
//       }
//     } finally {
//       setIsSubmitting(false);
//     }
//   };

//   // Create local URL for file preview
//   const previewSrc =
//     typeof profileImage === "string"
//       ? profileImage
//       : profileImage
//         ? URL.createObjectURL(profileImage)
//         : null;

//   return (
//     <Card className="mt-6 h-full bg-white text-gray-900 dark:bg-gray-900 dark:text-gray-100">
//       <CardContent className="pt-6">
//         {isLoadingProfile ? (
//           <ProfileShimmer />
//         ) : (
//           <>
//             <label
//               className={`mx-auto flex h-32 w-32 cursor-pointer flex-col items-center justify-center rounded-full border border-dashed border-gray-300 dark:border-gray-600 ${
//                 !isEditing && "cursor-not-allowed"
//               }`}
//             >
//               {isLoadingImage && isEditing ? (
//                 <LoadingSpinner />
//               ) : previewSrc ? (
//                 <img
//                   src={previewSrc}
//                   alt="Profile"
//                   className="h-full w-full rounded-full object-cover" // Changed to rounded-full for the image tag itself
//                 />
//               ) : (
//                 <>
//                   <UploadIcon className="h-6 w-6 text-gray-400 dark:text-gray-300" />
//                   <span className="text-xs text-gray-500 dark:text-gray-300">
//                     Upload your photo
//                   </span>
//                 </>
//               )}
//               <input
//                 type="file"
//                 className="hidden"
//                 accept="image/*" // Added accept attribute for usability
//                 onChange={handleImageChange}
//                 disabled={!isEditing || isLoadingImage}
//               />
//             </label>

//             <form
//               onSubmit={handleSubmit}
//               className="mt-6 grid grid-cols-1 gap-4 sm:grid-cols-2"
//             >
//               {/* {fields.map(({ label, name, readOnly }) => (
//                 <div key={name}>
//                   <Label
//                     htmlFor={name}
//                     className="text-gray-700 dark:text-gray-300"
//                   >
//                     {label}
//                   </Label>
//                   <Input
//                     type="text"
//                     id={name}
//                     name={name}
//                     // Handle potential null/undefined by converting to empty string
//                     value={profileForm[name as keyof IProfileFormData] || ""}
//                     onChange={handleChange}
//                     readOnly={readOnly || name === "email"}
//                     disabled={!isEditing && name !== "email"}
//                     className={`${
//                       readOnly || name === "email"
//                         ? "bg-gray-100 opacity-80 dark:bg-gray-800" // Added opacity for readOnly clarity
//                         : "bg-white dark:border-gray-700 dark:bg-gray-800 dark:text-gray-100"
//                     }`}
//                     placeholder={label}
//                   />
//                   {formErrors[name] && (
//                     <p className="mt-1 text-xs text-red-500">
//                       {formErrors[name]}
//                     </p>
//                   )}
//                 </div>
//               ))} */}
//               {fields.map(({ label, name, readOnly }) => (
//                 <div key={name}>
//                   <Label
//                     htmlFor={name}
//                     className="text-gray-700 dark:text-gray-300"
//                   >
//                     {label}
//                   </Label>

//                   {name === "mobileNumber" ? (
//                     <div className="flex items-center rounded-md border border-gray-300 bg-white dark:border-gray-700 dark:bg-gray-800">
//                       <span className="px-2 text-sm text-gray-600 dark:text-gray-300">
//                         +91
//                       </span>
//                       <Input
//                         type="text"
//                         id={name}
//                         name={name}
//                         value={
//                           profileForm[name as keyof IProfileFormData] || ""
//                         }
//                         onChange={handleChange}
//                         disabled={!isEditing}
//                         className="flex-1 border-0 bg-transparent focus-visible:ring-0 dark:text-gray-100"
//                         placeholder="Enter phone number"
//                       />
//                     </div>
//                   ) : (
//                     <Input
//                       type="text"
//                       id={name}
//                       name={name}
//                       value={profileForm[name as keyof IProfileFormData] || ""}
//                       onChange={handleChange}
//                       readOnly={readOnly || name === "email"}
//                       disabled={!isEditing && name !== "email"}
//                       className={`${
//                         readOnly || name === "email"
//                           ? "bg-gray-100 opacity-80 dark:bg-gray-800"
//                           : "bg-white dark:border-gray-700 dark:bg-gray-800 dark:text-gray-100"
//                       }`}
//                       placeholder={label}
//                     />
//                   )}

//                   {formErrors[name] && (
//                     <p className="mt-1 text-xs text-red-500">
//                       {formErrors[name]}
//                     </p>
//                   )}
//                 </div>
//               ))}
//               <div></div>
//               <div>
//                 {isEditing ? (
//                   <div className="space-x-2">
//                     <Button
//                       type="button"
//                       variant="secondary"
//                       onClick={handleCancel}
//                     >
//                       Cancel
//                     </Button>
//                     <Button type="submit" disabled={isSubmitting}>
//                       {isSubmitting ? <LoadingSpinner /> : "Save"}
//                     </Button>
//                   </div>
//                 ) : (
//                   <Button
//                     type="button"
//                     variant="default"
//                     onClick={() => setIsEditing(true)}
//                     className="w-full max-w-32"
//                   >
//                     Edit Profile
//                   </Button>
//                 )}
//               </div>
//             </form>
//           </>
//         )}
//       </CardContent>
//     </Card>
//   );
// };

// export default AccountSetting;
import { useEffect, useState, useCallback, useRef } from "react";
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
    locality: profileData?.address?.locality || "",
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
  const [formErrors, setFormErrors] = useState<Record<string, string>>({});

  // ✅ SNAPSHOT OF LAST LOADED PROFILE (FOR CANCEL)
  const originalProfileRef = useRef<IProfileFormData | null>(null);

  useEffect(() => {
    const loadProfile = async () => {
      try {
        const fetchedProfile = await dispatch(fetchProfile()).unwrap();
        const mappedProfile = mapProfileToForm(fetchedProfile);

        setProfileForm(mappedProfile);
        setProfileImage(fetchedProfile.avatarUrl || null);

        // ✅ Save snapshot
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
    (e: React.ChangeEvent<HTMLInputElement>) => {
      const { name, value } = e.target;
      setProfileForm((prev) => ({ ...prev, [name]: value }));

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

  // ✅ FIXED CANCEL HANDLER
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
        locality: profileForm.locality || undefined,
      };

      const parsedData = profileFormSchema.parse(normalizedForm);

      const formData = new FormData();
      Object.entries(parsedData).forEach(([key, value]) =>
        formData.append(key, String(value)),
      );

      if (profileImage instanceof File) {
        formData.append("profileImage", profileImage);
      }

      await dispatch(updateProfile(formData)).unwrap();

      // ✅ refresh snapshot after save
      const updatedProfile = await dispatch(fetchProfile()).unwrap();
      originalProfileRef.current = mapProfileToForm(updatedProfile);

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
