// import React, { useState, useEffect, useMemo } from "react";
// import { useNavigate } from "react-router-dom";
// import { useAppDispatch, useAppSelector } from "@/app/hooks";
// import { fetchProfile, updateProfile } from "@/features/user/userThunk";
// import { IProfile } from "@/types/profile-types";
// import { ROUTES } from "@/utils/constants";
// import { Label } from "@/components/ui/label";
// import { Input } from "@/components/ui/input";
// import { Button } from "@/components/ui/button";
// import { LoadingSpinner } from "@/components/ui/LoadingSpinner";
// import { profileFormSchema } from "@/utils/validations";
// import { handleError, showToast } from "@/utils/helpers";
// import { Card, CardContent } from "@/components/ui/card";
// import { UploadIcon } from "lucide-react";

// const AccountSetting = () => {
//   const navigate = useNavigate();
//   const dispatch = useAppDispatch();
//   const { loading, profile } = useAppSelector((state) => state.user);

//   const initialState: IProfile = useMemo(
//     () => ({
//       userId: profile?.userId || "",
//       firstName: profile?.firstName || "",
//       lastName: profile?.lastName || "",
//       email: profile?.email || "",
//       address: {
//         pincode: profile?.address?.pincode || "",
//         city: profile?.address?.city || "",
//         state: profile?.address?.state || "",
//         country: profile?.address?.country || "",
//         locality: profile?.address?.locality || "",
//       },
//       avatarUrl: profile?.avatarUrl || "",
//       avatarPublicId: profile?.avatarPublicId || "",
//       mobileNumber: profile?.mobileNumber || "",
//       roles: profile?.roles || ["USER"],
//     }),
//     [profile],
//   );

//   const [profileFormCredentials, setProfileFormCredentials] =
//     useState<IProfile>(initialState);
//   const [profileImage, setProfileImage] = useState<string | null>(
//     profile?.avatarUrl || null,
//   );

//   useEffect(() => {
//     dispatch(fetchProfile())
//       .unwrap()
//       .then((fetchedProfile) => {
//         setProfileFormCredentials({
//           userId: fetchedProfile.userId || "",
//           firstName: fetchedProfile.firstName || "",
//           lastName: fetchedProfile.lastName || "",
//           email: fetchedProfile.email || "",
//           address: {
//             pincode: fetchedProfile.address?.pincode || "",
//             city: fetchedProfile.address?.city || "",
//             state: fetchedProfile.address?.state || "",
//             country: fetchedProfile.address?.country || "",
//             locality: fetchedProfile.address?.locality || "",
//           },
//           avatarUrl: fetchedProfile.avatarUrl || "",
//           avatarPublicId: fetchedProfile.avatarPublicId || "",
//           mobileNumber: fetchedProfile.mobileNumber || "",
//           roles: fetchedProfile.roles || ["USER"],
//         });
//         setProfileImage(fetchedProfile.avatarUrl || null);
//       })
//       .catch((error) => {
//         const errorMessage = handleError(error);
//         showToast(errorMessage, "destructive");
//       });
//   }, [dispatch]);

//   const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
//     const { name, value } = e.target;
//     setProfileFormCredentials((prevState) => {
//       if (name in prevState.address) {
//         return {
//           ...prevState,
//           address: {
//             ...prevState.address,
//             [name]: value,
//           },
//         };
//       }
//       return { ...prevState, [name]: value };
//     });
//   };

//   // const handleImageChange = (e: React.ChangeEvent<HTMLInputElement>) => {
//   //   const file = e.target.files?.[0];
//   //   if (file) {
//   //     setProfileImage(file);
//   //   }
//   // };

//   const handleImageChange = (e: React.ChangeEvent<HTMLInputElement>) => {
//     const file = e.target.files?.[0];
//     if (file) {
//       setProfileImage(file); // Set the file itself for uploading
//     }
//   };

//   // const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
//   //   e.preventDefault();
//   //   try {
//   //     // Parse the form data
//   //     const parsedData = profileFormSchema.parse(profileFormCredentials);

//   //     // Create a FormData object
//   //     const formData = new FormData();
//   //     formData.append(
//   //       "user",
//   //       new Blob([JSON.stringify(parsedData)], { type: "application/json" }),
//   //     );

//   //     // Append the profile image if it exists
//   //     if (profileImage) {
//   //       formData.append("profile-image", profileImage);
//   //     }

//   //     // Log FormData contents for debugging
//   //     for (let [key, value] of formData.entries()) {
//   //       console.log(key, value);
//   //     }

//   //     // Dispatch the updateProfile action with formData
//   //     await dispatch(updateProfile(formData)).unwrap();
//   //     showToast("Profile Updated Successfully🥳");
//   //     navigate(ROUTES.PROFILE);
//   //   } catch (error) {
//   //     const errorMessage = handleError(error);
//   //     showToast(errorMessage, "destructive");
//   //   }
//   // };
//   const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
//     e.preventDefault();
//     try {
//       // Parse the form data
//       const parsedData = profileFormSchema.parse(profileFormCredentials);

//       // Create a FormData object
//       const formData = new FormData();
//       formData.append(
//         "user",
//         new Blob([JSON.stringify(parsedData)], { type: "application/json" }),
//       );

//       // Append the profile image if it exists
//       if (profileImage && typeof profileImage !== "string") {
//         formData.append("profile-image", profileImage);
//       }

//       // Log FormData contents for debugging
//       for (let [key, value] of formData.entries()) {
//         console.log(key, value);
//       }

//       // Dispatch the updateProfile action with formData
//       await dispatch(updateProfile(formData)).unwrap();
//       showToast("Profile Updated Successfully🥳");
//       navigate(ROUTES.PROFILE);
//     } catch (error) {
//       const errorMessage = handleError(error);
//       showToast(errorMessage, "destructive");
//     }
//   };

//   return (
//     <Card className="mt-6 h-full">
//       <CardContent className="pt-6">
//         {/* <div className="flex items-center space-x-6 py-8">
//           <label className="flex h-32 w-32 cursor-pointer flex-col items-center justify-center rounded-lg border border-dashed">
//             {profileImage ? (
//               <img
//                 src={profileImage}
//                 alt="Profile"
//                 className="h-full w-full rounded-lg"
//               />
//             ) : (
//               <>
//                 <UploadIcon className="h-6 w-6 text-gray-400" />
//                 <span className="text-xs text-gray-500">Upload your photo</span>
//               </>
//             )}
//             <input
//               type="file"
//               className="hidden"
//               onChange={handleImageChange}
//             />
//           </label>
//         </div> */}
//         <label className="mx-auto flex h-32 w-32 cursor-pointer flex-col items-center justify-center rounded-full border border-dashed">
//           {profileImage ? (
//             typeof profileImage === "string" ? (
//               <img
//                 src={profileImage}
//                 alt="Profile"
//                 className="h-full w-full rounded-lg"
//               />
//             ) : (
//               <img
//                 src={URL.createObjectURL(profileImage)}
//                 alt="Profile"
//                 className="h-full w-full rounded-lg"
//               />
//             )
//           ) : (
//             <>
//               <UploadIcon className="h-6 w-6 text-gray-400" />
//               <span className="text-xs text-gray-500">Upload your photo</span>
//             </>
//           )}
//           <input type="file" className="hidden" onChange={handleImageChange} />
//         </label>

//         <form onSubmit={handleSubmit} className="mt-6 grid grid-cols-2 gap-4">
//           <div>
//             <Label className="block text-sm font-medium">First Name</Label>
//             <Input
//               placeholder="Enter your first name"
//               name="firstName"
//               value={profileFormCredentials.firstName}
//               onChange={handleChange}
//             />
//           </div>
//           <div>
//             <Label className="block text-sm font-medium">Last Name</Label>
//             <Input
//               placeholder="Enter your last name"
//               name="lastName"
//               value={profileFormCredentials.lastName}
//               onChange={handleChange}
//             />
//           </div>
//           <div>
//             <Label className="block text-sm font-medium">Email</Label>
//             <Input
//               placeholder="Enter your email"
//               name="email"
//               value={profileFormCredentials.email}
//               onChange={handleChange}
//             />
//           </div>
//           <div>
//             <Label className="block text-sm font-medium">Phone Number</Label>
//             <Input
//               placeholder="Enter your phone number"
//               name="mobileNumber"
//               value={profileFormCredentials.mobileNumber}
//               onChange={handleChange}
//             />
//           </div>
//           <div>
//             <Label className="block text-sm font-medium">Pincode</Label>
//             <Input
//               placeholder="Enter your pincode"
//               name="pincode"
//               value={profileFormCredentials.address.pincode}
//               onChange={handleChange}
//             />
//           </div>
//           <div>
//             <Label className="block text-sm font-medium">City</Label>
//             <Input
//               placeholder="Enter your city"
//               name="city"
//               value={profileFormCredentials.address.city}
//               onChange={handleChange}
//             />
//           </div>
//           <div>
//             <Label className="block text-sm font-medium">State</Label>
//             <Input
//               placeholder="Enter your state"
//               name="state"
//               value={profileFormCredentials.address.state}
//               onChange={handleChange}
//             />
//           </div>
//           <div>
//             <Label className="block text-sm font-medium">Country</Label>
//             <Input
//               placeholder="Enter your country"
//               name="country"
//               value={profileFormCredentials.address.country}
//               onChange={handleChange}
//             />
//           </div>
//           <div>
//             <Label className="block text-sm font-medium">Locality</Label>
//             <Input
//               placeholder="Enter your locality"
//               name="locality"
//               value={profileFormCredentials.address.locality}
//               onChange={handleChange}
//             />
//           </div>

//           <div className="col-span-2 flex justify-between">
//             <Button type="submit" className="px-4 py-2" disabled={loading}>
//               {loading ? <LoadingSpinner /> : "Update Profile"}
//             </Button>
//           </div>
//         </form>
//       </CardContent>
//     </Card>
//   );
// };

// export default AccountSetting;
import React, { useState, useEffect, useMemo } from "react";
import { useNavigate } from "react-router-dom";
import { useAppDispatch, useAppSelector } from "@/app/hooks";
import { fetchProfile, updateProfile } from "@/features/user/userThunk";
import { IProfile } from "@/types/profile-types";
import { ROUTES } from "@/utils/constants";
import { Label } from "@/components/ui/label";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { LoadingSpinner } from "@/components/ui/LoadingSpinner";
import { profileFormSchema } from "@/utils/validations";
import { handleError, showToast } from "@/utils/helpers";
import { Card, CardContent } from "@/components/ui/card";
import { UploadIcon } from "lucide-react";

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
      address: {
        pincode: profile?.address?.pincode || "",
        city: profile?.address?.city || "",
        state: profile?.address?.state || "",
        country: profile?.address?.country || "",
        locality: profile?.address?.locality || "",
      },
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
          address: {
            pincode: fetchedProfile.address?.pincode || "",
            city: fetchedProfile.address?.city || "",
            state: fetchedProfile.address?.state || "",
            country: fetchedProfile.address?.country || "",
            locality: fetchedProfile.address?.locality || "",
          },
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
    setProfileFormCredentials((prevState) => {
      if (name in prevState.address) {
        return {
          ...prevState,
          address: {
            ...prevState.address,
            [name]: value,
          },
        };
      }
      return { ...prevState, [name]: value };
    });
  };

  const handleImageChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (file) {
      setProfileImage(file);
    }
  };

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    try {
      // Ensure user data exists before proceeding
      if (!profileFormCredentials.user) {
        throw new Error("User information is missing.");
      }

      // Parse the form data using Zod schema validation
      const parsedData = profileFormSchema.parse(profileFormCredentials);

      // Create a FormData object
      const formData = new FormData();

      // Append user ID (or necessary user details)
      formData.append("user", String(parsedData.user)); // Ensure user ID is present

      // Append other fields separately, ensuring correct handling of nested objects
      Object.entries(parsedData).forEach(([key, value]) => {
        if (key === "address" && typeof value === "object") {
          // Flatten address object and append its fields
          Object.entries(value).forEach(([addrKey, addrValue]) => {
            formData.append(`address[${addrKey}]`, String(addrValue));
          });
        } else if (key !== "user") {
          // Already added `user`, so skip here
          formData.append(key, String(value));
        }
      });

      // Append the profile image if it's a File (not a URL)
      if (profileImage instanceof File) {
        formData.append("profileImage", profileImage);
      }

      // Debugging: Log FormData entries
      for (let [key, value] of formData.entries()) {
        console.log(`${key}:`, value instanceof File ? value.name : value);
      }

      // Dispatch the updateProfile action with formData
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
            <Label className="mb-2 block text-sm font-medium">Phone Number</Label>
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
              value={profileFormCredentials.address.pincode}
              onChange={handleChange}
            />
          </div>
          <div>
            <Label className="mb-2 block text-sm font-medium">City</Label>
            <Input
              placeholder="Enter your city"
              name="city"
              value={profileFormCredentials.address.city}
              onChange={handleChange}
            />
          </div>
          <div>
            <Label className="mb-2 block text-sm font-medium">State</Label>
            <Input
              placeholder="Enter your state"
              name="state"
              value={profileFormCredentials.address.state}
              onChange={handleChange}
            />
          </div>
          <div>
            <Label className="mb-2 block text-sm font-medium">Country</Label>
            <Input
              placeholder="Enter your country"
              name="country"
              value={profileFormCredentials.address.country}
              onChange={handleChange}
            />
          </div>
          <div>
            <Label className="mb-2 block text-sm font-medium">Locality</Label>
            <Input
              placeholder="Enter your locality"
              name="locality"
              value={profileFormCredentials.address.locality}
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
