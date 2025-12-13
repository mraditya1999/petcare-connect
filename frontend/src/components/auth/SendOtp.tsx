import { useForm } from "react-hook-form";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
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
import { useAppDispatch, useAppSelector } from "@/app/hooks";
import { sendOtp } from "@/features/auth/authThunk";
import { useNavigate } from "react-router-dom";
import { LoadingSpinner } from "../ui/LoadingSpinner";
import ShowToast from "../shared/ShowToast";

export default function SendOtp() {
  const dispatch = useAppDispatch();
  const navigate = useNavigate();

  // Get loading from Redux
  const loading = useAppSelector((state) => state.auth.loading);

  const form = useForm<{ phone: string }>({
    defaultValues: {
      phone: "",
    },
    mode: "onSubmit",
  });

  const onSubmit = async (data: { phone: string }) => {
    const res = await dispatch(sendOtp({ phone: data.phone }));

    if (sendOtp.fulfilled.match(res)) {
      ShowToast({
        description: "OTP sent successfully!",
        type: "success",
      });
      navigate(`/verify-otp?phone=${data.phone}`);
    } else {
      ShowToast({
        description: res.payload || "Failed to send OTP",
        type: "error",
      });
    }
  };

  return (
    <div className="flex h-screen items-center justify-center">
      <Card className="w-[380px] shadow-xl">
        <CardHeader>
          <CardTitle className="text-center text-xl font-semibold">
            Login with Mobile Number
          </CardTitle>
        </CardHeader>

        <Form {...form}>
          <form onSubmit={form.handleSubmit(onSubmit)}>
            <CardContent className="space-y-5">
              <FormField
                control={form.control}
                name="phone"
                rules={{
                  required: "Phone number is required",
                  minLength: { value: 10, message: "Invalid phone number" },
                }}
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Mobile Number</FormLabel>
                    <Input
                      type="tel"
                      placeholder="Enter mobile number"
                      disabled={loading} // Disable input while loading
                      {...field}
                    />
                    <FormMessage />
                  </FormItem>
                )}
              />
            </CardContent>

            <CardFooter>
              <Button type="submit" disabled={loading} className="w-full">
                {loading ? (
                  <>
                    <LoadingSpinner />
                    Sending OTP…
                  </>
                ) : (
                  "Send OTP"
                )}
              </Button>
            </CardFooter>
          </form>
        </Form>
      </Card>
    </div>
  );
}
