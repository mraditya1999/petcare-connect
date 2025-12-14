import { useEffect, useState } from "react";
import { useForm } from "react-hook-form";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import {
  Card,
  CardContent,
  CardHeader,
  CardTitle,
  CardFooter,
} from "@/components/ui/card";
import {
  Form,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/components/ui/form";
import { useAppDispatch } from "@/app/hooks";
import { verifyOtp, sendOtp, completeProfile } from "@/features/auth/authThunk";
import { useNavigate, useSearchParams } from "react-router-dom";
import ShowToast from "../shared/ShowToast";
import { saveUserToStorage } from "@/utils/helpers";
import { setUser } from "@/features/auth/authSlice";

interface NewUserInfo {
  firstName: string;
  lastName: string;
  email: string;
}

export default function VerifyOtp() {
  const dispatch = useAppDispatch();
  const navigate = useNavigate();
  const [params] = useSearchParams();
  const phone = params.get("phone") || "";

  const [timer, setTimer] = useState(30);
  const [isNewUser, setIsNewUser] = useState(false);

  // OTP form
  const otpForm = useForm<{ otp: string }>({
    defaultValues: { otp: "" },
  });

  // Profile form for new user
  const profileForm = useForm<NewUserInfo>({
    defaultValues: {
      firstName: "",
      lastName: "",
      email: "",
    },
  });

  // Countdown timer for resend
  useEffect(() => {
    const interval = setInterval(() => {
      setTimer((t) => (t > 0 ? t - 1 : 0));
    }, 1000);
    return () => clearInterval(interval);
  }, []);

  const handleVerifyOtp = async (data: { otp: string }) => {
    const res = await dispatch(verifyOtp({ phone, otp: data.otp }));
    if (verifyOtp.fulfilled.match(res)) {
      const payload = res.payload!;
      if (payload.isNewUser) {
        // store temporary token & show profile form
        if (payload.tempToken) {
          localStorage.setItem("tempSignupToken", payload.tempToken);
        }
        setIsNewUser(true);
        ShowToast({
          description: "OTP verified. Please complete your profile.",
          type: "success",
        });
      } else {
        const userData = {
          message: "User logged in successfully.",
          data: payload,
        };

        saveUserToStorage(userData, true);
        dispatch(setUser(userData));
        navigate("/");
      }
    } else {
      ShowToast({
        description: res.payload || "OTP verification failed",
        type: "error",
      });
    }
  };

  const handleCompleteProfile = async (data: NewUserInfo) => {
    // Use the completeProfile thunk (which posts to /auth/complete-profile)
    const res = await dispatch(
      completeProfile({
        phone,
        firstName: data.firstName,
        lastName: data.lastName,
        email: data.email,
      }),
    );

    if (completeProfile.fulfilled.match(res)) {
      // Clear temp token
      localStorage.removeItem("tempSignupToken");
      dispatch(setUser(res.payload.data));

      navigate("/");
    } else {
      // If server returns 401, you should prompt user to resend OTP
      ShowToast({
        description: res.payload || "Failed to complete profile",
        type: "error",
      });
    }
  };

  const handleResend = async () => {
    if (timer === 0) {
      const res = await dispatch(sendOtp({ phone }));
      if (sendOtp.fulfilled.match(res)) {
        setTimer(30);
      }
    }
  };

  return (
    <div className="flex h-screen items-center justify-center">
      <Card className="w-[380px] shadow-xl">
        <CardHeader>
          <CardTitle className="text-center text-xl font-semibold">
            {isNewUser ? "Complete Your Profile" : "Enter OTP"}
          </CardTitle>
        </CardHeader>

        {/* OTP FORM */}
        {!isNewUser ? (
          <Form key="otp-form" {...otpForm}>
            <form onSubmit={otpForm.handleSubmit(handleVerifyOtp)}>
              <CardContent className="space-y-4">
                <div className="text-center text-sm text-muted-foreground">
                  OTP sent to <b>{phone}</b>
                </div>

                <FormField
                  control={otpForm.control}
                  name="otp"
                  rules={{ required: "OTP is required" }}
                  render={({ field }) => (
                    <FormItem>
                      <FormLabel>OTP</FormLabel>
                      <Input
                        type="number"
                        placeholder="Enter 6-digit OTP"
                        {...field}
                      />
                      <FormMessage />
                    </FormItem>
                  )}
                />
              </CardContent>

              <CardFooter className="flex flex-col space-y-2">
                <Button type="submit" className="w-full">
                  Verify OTP
                </Button>

                {timer > 0 ? (
                  <span className="text-center text-sm">
                    Resend OTP in {timer}s
                  </span>
                ) : (
                  <Button variant="link" onClick={handleResend}>
                    Resend OTP
                  </Button>
                )}
              </CardFooter>
            </form>
          </Form>
        ) : (
          /* PROFILE FORM */
          <Form key="profile-form" {...profileForm}>
            <form onSubmit={profileForm.handleSubmit(handleCompleteProfile)}>
              <CardContent className="space-y-4">
                <FormField
                  control={profileForm.control}
                  name="firstName"
                  rules={{ required: "First name is required" }}
                  render={({ field }) => (
                    <FormItem>
                      <FormLabel>First Name</FormLabel>
                      <Input placeholder="Enter first name" {...field} />
                      <FormMessage />
                    </FormItem>
                  )}
                />

                <FormField
                  control={profileForm.control}
                  name="lastName"
                  rules={{ required: "Last name is required" }}
                  render={({ field }) => (
                    <FormItem>
                      <FormLabel>Last Name</FormLabel>
                      <Input placeholder="Enter last name" {...field} />
                      <FormMessage />
                    </FormItem>
                  )}
                />

                <FormField
                  control={profileForm.control}
                  name="email"
                  rules={{ required: "Email is required" }}
                  render={({ field }) => (
                    <FormItem>
                      <FormLabel>Email</FormLabel>
                      <Input placeholder="Enter email" {...field} />
                      <FormMessage />
                    </FormItem>
                  )}
                />
              </CardContent>

              <CardFooter>
                <Button type="submit" className="w-full">
                  Complete Profile & Login
                </Button>
              </CardFooter>
            </form>
          </Form>
        )}
      </Card>
    </div>
  );
}
