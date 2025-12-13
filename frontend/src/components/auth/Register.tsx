import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useAppDispatch, useAppSelector } from "@/app/hooks";
import { registerUser } from "@/features/auth/authThunk";
import { IRegisterCredentials } from "@/types/auth-types";
import { registerFormSchema } from "@/utils/validations";
import { handleError } from "@/utils/helpers";
import { ROUTES } from "@/utils/constants";
import GenericAlert from "@/components/shared/GenericAlert";
// ui components
import { Label } from "@/components/ui/label";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { PasswordInput } from "@/components/ui/PasswordInput";
import { LoadingSpinner } from "@/components/ui/LoadingSpinner";
import ShowToast from "../shared/ShowToast";

const Register = () => {
  const dispatch = useAppDispatch();
  const navigate = useNavigate();
  const { loading, success } = useAppSelector((state) => state.auth);

  const [registerFormCredentials, setRegisterFormCredentials] =
    useState<IRegisterCredentials>({
      firstName: "",
      lastName: "",
      email: "",
      password: "",
    });

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setRegisterFormCredentials({
      ...registerFormCredentials,
      [e.target.name]: e.target.value,
    });
  };

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    try {
      const parsedData = registerFormSchema.parse(registerFormCredentials);
      const resultAction = await dispatch(registerUser({ parsedData }));
      if (registerUser.fulfilled.match(resultAction)) {
        ShowToast({
          description:
            "Please check your email to verify your account. Redirecting to login... 🚀",
          type: "success",
        });
        setTimeout(() => {
          navigate(ROUTES.LOGIN);
        }, 5000);
      }
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
      {!success ? (
        <article className="fixed-width rounded-lg border bg-card px-8 py-8 shadow-md">
          <h2 className="mb-8 text-lg font-semibold md:text-2xl lg:text-3xl">
            Create an account
          </h2>
          <form
            action=""
            className="flex flex-col gap-5"
            onSubmit={handleSubmit}
          >
            <div className="flex justify-between gap-2">
              {/* First Name */}
              <div className="flex flex-1 flex-col gap-2">
                <Label htmlFor="name" className="text-gray-700">
                  First Name
                </Label>
                <Input
                  placeholder="John Doe"
                  name="firstName"
                  value={registerFormCredentials.firstName}
                  onChange={handleChange}
                />
              </div>

              {/* Last Name */}
              <div className="flex flex-1 flex-col gap-2">
                <Label htmlFor="name" className="text-gray-700">
                  Last Name
                </Label>
                <Input
                  placeholder="John Doe"
                  name="lastName"
                  value={registerFormCredentials.lastName}
                  onChange={handleChange}
                />
              </div>
            </div>

            {/* Email */}
            <div className="flex flex-col gap-2">
              <Label htmlFor="email" className="text-gray-700">
                Email
              </Label>
              <Input
                placeholder="example@gmail.com"
                type="email"
                name="email"
                value={registerFormCredentials.email}
                onChange={handleChange}
                // required
              />
            </div>

            {/* Password */}
            <div className="gap flex w-full flex-col items-center gap-2">
              <Label htmlFor="password" className="w-full text-gray-700">
                Password
              </Label>
              <PasswordInput
                id="password"
                className="w-full flex-1"
                autoComplete="new-password"
                placeholder="Enter your password"
                name="password"
                onChange={handleChange}
                value={registerFormCredentials.password}
                // required
              />
            </div>

            <div className="flex flex-col gap-3">
              <Button type="submit" className="px-4 py-2" disabled={loading}>
                {loading ? <LoadingSpinner /> : "Create account"}
              </Button>
            </div>

            <p className="text-center text-sm text-gray-400">
              Already have an account?{" "}
              <Link
                to={ROUTES.LOGIN}
                className="text-sm text-blue-500 transition-all duration-100 hover:text-blue-700"
              >
                Log in
              </Link>
            </p>
          </form>
        </article>
      ) : (
        <GenericAlert
          success={true}
          title="Registration successful!"
          message="Please check your email to verify your account."
        />
      )}
    </>
  );
};

export default Register;
