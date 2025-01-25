import { useState } from "react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { useLocation, useNavigate } from "react-router-dom";
import { useAppDispatch, useAppSelector } from "@/app/hooks";
import { resetPassword } from "@/features/user/userThunk";
import { LoadingSpinner } from "../ui/LoadingSpinner";
import { IResetPasswordCredentials } from "@/types/auth-types";
import { handleError, showToast } from "@/utils/helpers";
import { resetPasswordFormSchema } from "@/utils/validations";
import GenericAlert from "../shared/GenericAlert";

const ResetPassword = () => {
  const dispatch = useAppDispatch();
  const navigate = useNavigate();
  const location = useLocation();
  const query = new URLSearchParams(location.search);

  const [resetPasswordCredentials, setResetPasswordCredentials] =
    useState<IResetPasswordCredentials>({
      password: "",
    });
  const { loading, success } = useAppSelector((state) => state.user);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setResetPasswordCredentials({
      ...resetPasswordCredentials,
      [e.target.name]: e.target.value,
    });
  };

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    try {
      const parsedData = resetPasswordFormSchema.parse(
        resetPasswordCredentials,
      );
      const token = query.get("token");
      const email = query.get("email");
      await dispatch(resetPassword({ parsedData, token, email })).unwrap();
      showToast(
        "Password reset successfully. Redirecting to login page shortly...",
      );
      setTimeout(() => {
        navigate("/login");
      }, 3000);
    } catch (error) {
      const errorMessage = handleError(error);
      showToast(errorMessage, "destructive");
    }
  };

  return (
    <>
      {!success ? (
        <article className="fixed-width rounded-lg px-8 py-8 shadow-md transition-all duration-300 hover:shadow-lg">
          <header className="mb-8 flex flex-col items-center gap-1">
            <h2 className="text-lg font-semibold md:text-2xl lg:text-3xl">
              Reset Password
            </h2>
            <p className="text-sm">Enter your email address</p>
          </header>

          <form className="flex flex-col gap-5" onSubmit={handleSubmit}>
            <div className="flex flex-col gap-2">
              <Label htmlFor="password" className="">
                Password
              </Label>
              <Input
                id="password"
                placeholder="Enter your password"
                type="password"
                required
                name="password"
                value={resetPasswordCredentials.password}
                onChange={handleChange}
              />
            </div>
            <Button type="submit" className="mb-3 px-4 py-2" disabled={loading}>
              {loading ? <LoadingSpinner /> : "Reset Password"}
            </Button>
          </form>
        </article>
      ) : (
        <GenericAlert
          message="Password reset successfully. Redirecting to login page shortly..."
          success={true}
          title="Password Reset"
        />
      )}
    </>
  );
};

export default ResetPassword;
