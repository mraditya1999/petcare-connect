import { useState } from "react";
import { Label } from "@/components/ui/label";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { useAppDispatch, useAppSelector } from "@/app/hooks";
import { forgetPassword } from "@/features/auth/authThunk";
import { LoadingSpinner } from "@/components/ui/LoadingSpinner";
import GenericAlert from "@/components/shared/GenericAlert";
import { IForgetPasswordCredentials } from "@/types/auth-types";
import { forgetPasswordFormSchema } from "@/utils/validations";
import { handleError } from "@/utils/helpers";
import ShowToast from "../shared/ShowToast";

const ForgetPassword = () => {
  const dispatch = useAppDispatch();
  const { loading } = useAppSelector((state) => state.auth);

  const [showSuccess, setShowSuccess] = useState(false);
  const [forgetPasswordCredentials, setForgetPasswordCredentials] =
    useState<IForgetPasswordCredentials>({ email: "" });

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setForgetPasswordCredentials({
      ...forgetPasswordCredentials,
      [e.target.name]: e.target.value,
    });
  };

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    try {
      const parsedData = forgetPasswordFormSchema.parse(
        forgetPasswordCredentials,
      );

      await dispatch(forgetPassword({ parsedData })).unwrap();

      setShowSuccess(true);
      setTimeout(() => {
        setShowSuccess(false);
      }, 3000);

      ShowToast({
        description: "Please check your email to reset your password.",
        type: "success",
      });
    } catch (error) {
      const errorMessage = handleError(error);
      ShowToast({
        description: errorMessage,
        type: "error",
      });
    }
  };

  return (
    <>
      {!showSuccess ? (
        <article className="fixed-width rounded-lg px-8 py-8 shadow-md transition-all duration-300 hover:shadow-lg">
          <header className="mb-8 flex flex-col items-center gap-1">
            <h2 className="text-lg font-semibold md:text-2xl lg:text-3xl">
              Forget Password
            </h2>
            <p className="text-sm text-gray-500">Enter your email address</p>
          </header>

          <form className="flex flex-col gap-5" onSubmit={handleSubmit}>
            <div className="flex flex-col gap-2">
              <Label htmlFor="email" className="text-gray-700">
                Email
              </Label>
              <Input
                id="email"
                placeholder="johndoe@gmail.com"
                type="email"
                name="email"
                value={forgetPasswordCredentials.email}
                onChange={handleChange}
              />
            </div>
            <Button type="submit" className="mb-3 px-4 py-2" disabled={loading}>
              {loading ? <LoadingSpinner /> : "Get Reset Password Link"}
            </Button>
          </form>
        </article>
      ) : (
        <GenericAlert
          message="Password reset link sent. Please check your email to reset your password."
          success={true}
          title="Password Reset"
        />
      )}
    </>
  );
};

export default ForgetPassword;
