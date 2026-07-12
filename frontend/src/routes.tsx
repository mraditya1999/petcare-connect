import {
  createBrowserRouter,
  createRoutesFromElements,
  Route,
} from "react-router-dom";
import {
  HomePage,
  AboutPage,
  ForumPage,
  ServicePage,
  LayoutPage,
  AuthLayoutPage,
  ErrorPage,
  ProfilePage,
  SingleForumPage,
  AppointmentPage,
} from "@/pages";
import { ROUTES } from "@/utils/constants";
import {
  ForgetPassword,
  Login,
  ProtectedRoute,
  Register,
  ResetPassword,
  VerifyEmailPage,
  GitHubCallback,
  GoogleCallback,
  SendOtp,
  VerifyOtp,
} from "@/components";

const router = createBrowserRouter(
  createRoutesFromElements(
    <>
      {/* Protected routes for authenticated users */}
      <Route
        element={
          <ProtectedRoute>
            <LayoutPage />
          </ProtectedRoute>
        }
        errorElement={<ErrorPage />}
      >
        <Route path={ROUTES.PROFILE} element={<ProfilePage />} />
        <Route path={ROUTES.APPOINTMENTS} element={<AppointmentPage />} />
      </Route>

      {/* Public Route */}
      <Route element={<LayoutPage />}>
        <Route index path={ROUTES.HOME} element={<HomePage />} />
        <Route path={ROUTES.ABOUT} element={<AboutPage />} />
        <Route path={ROUTES.FORUM} element={<ForumPage />} />
        <Route
          path={`${ROUTES.FORUM}/:forumId`}
          element={<SingleForumPage />}
        />
        <Route path={ROUTES.SERVICE} element={<ServicePage />} />
      </Route>

      {/* Auth Route */}
      <Route element={<AuthLayoutPage />} errorElement={<ErrorPage />}>
        <Route path={ROUTES.LOGIN} element={<Login />} />
        <Route path={ROUTES.REGISTER} element={<Register />} />
        <Route path={ROUTES.VERIFY_EMAIL} element={<VerifyEmailPage />} />
        <Route path={ROUTES.FORGET_PASSWORD} element={<ForgetPassword />} />
        <Route path={ROUTES.RESET_PASSWORD} element={<ResetPassword />} />
        <Route path={ROUTES.GITHUB_CALLBACK} element={<GitHubCallback />} />
        <Route path={ROUTES.GOOGLE_CALLBACK} element={<GoogleCallback />} />
        <Route path={ROUTES.SEND_OTP} element={<SendOtp />} />
        <Route path={ROUTES.VERIFY_OTP} element={<VerifyOtp />} />
      </Route>
    </>,
  ),
);

export default router;
