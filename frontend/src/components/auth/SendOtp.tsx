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
import { Alert, AlertDescription, AlertTitle } from "@/components/ui/alert";
import { FaTriangleExclamation } from "react-icons/fa6";

export default function SendOtp() {
  const dispatch = useAppDispatch();
  const navigate = useNavigate();

  const loading = useAppSelector((state) => state.auth.loading);

  const form = useForm<{ phone: string }>({
    defaultValues: { phone: "" },
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
    <>
      {/*  */}
      <div className="mt-36 flex h-full w-full items-center justify-center px-4">
        <Card className="w-full max-w-md shadow-xl">
          <CardHeader>
            <CardTitle className="text-center text-lg font-semibold md:text-xl">
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
                    minLength: {
                      value: 10,
                      message: "Enter a valid 10-digit number",
                    },
                    maxLength: {
                      value: 10,
                      message: "Enter a valid 10-digit number",
                    },
                    pattern: {
                      value: /^[0-9]{10}$/,
                      message: "Enter a valid 10-digit number",
                    },
                  }}
                  render={({ field }) => (
                    <FormItem>
                      <FormLabel>Mobile Number</FormLabel>
                      <div className="flex items-center">
                        <span className="select-none rounded-l-md border border-r-0 border-gray-300 bg-gray-100 px-3 py-2 text-gray-600">
                          +91
                        </span>
                        <Input
                          type="tel"
                          placeholder="Enter 10-digit number"
                          disabled={loading}
                          className="rounded-l-none"
                          {...field}
                        />
                      </div>
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

      {/* Warning Note */}
      <div className="mb-12 self-end px-6 pb-2">
        <Alert
          variant="default"
          className="border-yellow-300 bg-yellow-50 dark:border-yellow-600 dark:bg-yellow-900/30"
        >
          <FaTriangleExclamation className="h-5 w-5 text-yellow-600 dark:text-yellow-400" />
          <AlertTitle className="font-semibold text-yellow-700 dark:text-yellow-300">
            Important
          </AlertTitle>
          <AlertDescription className="text-sm text-gray-700 dark:text-gray-200">
            Twilio only allows sending OTPs to <strong>verified numbers</strong>{" "}
            in testing mode. If your number is not verified, you may not receive
            the OTP.
          </AlertDescription>
        </Alert>
      </div>
    </>
  );
}
