import React, { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useAppDispatch, useAppSelector } from "@/app/hooks";
import { googleLoginUser, loginUser } from "@/features/auth/authThunk";
import { ILoginCredentials } from "@/types/auth-types";
import { ROUTES } from "@/utils/constants";
import { Label } from "@/components/ui/label";
import { Checkbox } from "@/components/ui/checkbox";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { PasswordInput } from "@/components/ui/PasswordInput";
import { LoadingSpinner } from "@/components/ui/LoadingSpinner";
import { loginFormSchema } from "@/utils/validations";
import { handleError } from "@/utils/helpers";
import ShowToast from "../shared/ShowToast";
import { useGoogleLogin } from "@react-oauth/google";
import GoogleSvg from "@/assets/images/GoogleSvg";
import GitHubSvg from "@/assets/images/GitHubSvg";
import { FaMobileScreenButton } from "react-icons/fa6";
import { customFetch } from "@/utils/customFetch";

const Login = () => {
  const navigate = useNavigate();
  const dispatch = useAppDispatch();
  const { loading } = useAppSelector((state) => state.auth);
  const [rememberMe, setRememberMe] = useState(false);
  const [loginFormCredentials, setLoginFormCredentials] =
    useState<ILoginCredentials>({
      email: import.meta.env.VITE_USERNAME || "",
      password: import.meta.env.VITE_PASSWORD || "",
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
      ShowToast({
        description: "Login Successful🥳",
        type: "success",
      });
      navigate(ROUTES.HOME);
    } catch (error) {
      const errorMessage = handleError(error);
      ShowToast({
        description: errorMessage,
        type: "error",
      });
    }
  };

  const googleLogin = useGoogleLogin({
    onSuccess: async (tokenResponse) => {
      dispatch(
        googleLoginUser({ token: tokenResponse.access_token, navigate }),
      );
    },
    onError: () => {
      ShowToast({
        description: "Google login failed. Try again! ",
        type: "error",
      });
    },
  });

  const githubLogin = () => {
    (async () => {
      try {
        const resp = await customFetch.get("/auth/github/url");
        const data = resp.data?.data;
        if (!data || !data.url || !data.state)
          throw new Error("Invalid auth url response");
        // store server-generated state for later validation
        sessionStorage.setItem("gh_oauth_state", data.state);
        window.location.href = data.url;
      } catch (err) {
        ShowToast({
          description: "Failed to start GitHub login",
          type: "error",
        });
        console.error("githubLogin error:", err);
      }
    })();
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

        <div className="flex flex-col gap-3">
          <Button type="submit" className="w-full px-4 py-2" variant="default">
            {loading ? <LoadingSpinner /> : "Login now"}
          </Button>

          <Button
            variant="secondary"
            type="button"
            className="w-full px-4 py-2"
            onClick={() => googleLogin()}
          >
            <span className="flex items-center justify-center gap-2">
              <GoogleSvg />
              <span>Login with Google</span>
            </span>
          </Button>

          <Button
            variant="secondary"
            type="button"
            className="w-full px-4 py-2"
            onClick={githubLogin}
          >
            <span className="flex items-center justify-center gap-2">
              <GitHubSvg />
              <span>Login with GitHub</span>
            </span>
          </Button>

          <Button
            variant="secondary"
            type="button"
            className="w-full px-4 py-2"
            onClick={() => navigate(ROUTES.SEND_OTP)}
          >
            <span className="flex items-center justify-center gap-2">
              <FaMobileScreenButton />
              Login With Mobile
            </span>
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
        </div>
      </form>
    </article>
  );
};

export default Login;
