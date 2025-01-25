import React, { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useAppDispatch, useAppSelector } from "@/app/hooks";
import { loginUser } from "@/features/user/userThunk";
import { ILoginCredentials } from "@/types/auth-types";
import { ROUTES } from "@/utils/constants";
import { Label } from "@/components/ui/label";
import { Checkbox } from "@/components/ui/checkbox";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { PasswordInput } from "@/components/ui/PasswordInput";
import { LoadingSpinner } from "@/components/ui/LoadingSpinner";
import { loginFormSchema } from "@/utils/validations";
import { handleError, showToast } from "@/utils/helpers";

const Login = () => {
  const navigate = useNavigate();
  const dispatch = useAppDispatch();
  const { loading } = useAppSelector((state) => state.user);
  const [rememberMe, setRememberMe] = useState(false);
  const [loginFormCredentials, setLoginFormCredentials] =
    useState<ILoginCredentials>({
      email: "ay5480620@gmail.com",
      password: "@mrAditya1999",
    });

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setLoginFormCredentials({
      ...loginFormCredentials,
      [e.target.name]: e.target.value,
    });
  };

  const handleCheckboxChange = (checked: boolean) => {
    setRememberMe(checked);
  };

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    try {
      const parsedData = loginFormSchema.parse(loginFormCredentials);
      await dispatch(loginUser({ parsedData, rememberMe })).unwrap();
      showToast("Login Successful🥳");
      navigate(ROUTES.HOME);
    } catch (error) {
      const errorMessage = handleError(error);
      showToast(errorMessage, "destructive");
    }
  };

  return (
    <article className="fixed-width rounded-lg border bg-card px-8 py-8 shadow-md transition-all duration-300 hover:shadow-lg">
      <h2 className="mb-8 text-lg font-semibold md:text-2xl lg:text-3xl">
        Login to your account
      </h2>
      <form action="" className="flex flex-col gap-5" onSubmit={handleSubmit}>
        {/* Email */}
        <div className="flex flex-col gap-2">
          <Label htmlFor="email" className="text-primary">
            Email
          </Label>
          <Input
            placeholder="balamia@gmail.com"
            type="email"
            name="email"
            value={loginFormCredentials.email}
            onChange={handleChange}
          />
        </div>

        {/* Password */}
        <div className="flex w-full flex-col items-center gap-2">
          <Label htmlFor="password" className="w-full text-primary">
            Password
          </Label>
          <PasswordInput
            id="password"
            className="w-full flex-1"
            autoComplete="new-password"
            placeholder="Enter your password"
            name="password"
            onChange={handleChange}
            value={loginFormCredentials.password}
          />
        </div>

        <div className="flex items-center justify-between">
          <div className="flex items-center space-x-2">
            <Checkbox
              id="rememberMe"
              checked={rememberMe}
              onCheckedChange={handleCheckboxChange}
            />
            <Label
              htmlFor="rememberMe"
              className="text-sm font-medium leading-none text-primary peer-disabled:cursor-not-allowed peer-disabled:opacity-70"
            >
              Remember me
            </Label>
          </div>
          <Link to={ROUTES.FORGET_PASSWORD} className="text-sm text-blue-500">
            Forgot Password?
          </Link>
        </div>

        <Button type="submit" className="px-4 py-2">
          {loading ? <LoadingSpinner /> : "Login now"}
        </Button>

        <p className="text-center text-sm">
          Don't have an account?{" "}
          <Link
            to={ROUTES.REGISTER}
            className="text-sm text-blue-500 transition-all duration-100 hover:text-blue-700"
          >
            Register
          </Link>
        </p>
      </form>
    </article>
  );
};

export default Login;
