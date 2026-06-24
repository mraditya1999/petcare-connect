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
  AdminDashboardPage,
  SpecialistDashboardPage,
  SpecialistNotificationsPage,
  UserDashboardPage,
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
  SendOtp,
  VerifyOtp,
} from "@/components";

const router = createBrowserRouter(
  createRoutesFromElements(
    <>
      {/* Dashboard route (role-based UI only) */}
      <Route
        path={ROUTES.DASHBOARD}
        element={
          <ProtectedRoute
            adminComponent={<AdminDashboardPage />}
            specialistComponent={<SpecialistDashboardPage />}
            userComponent={<UserDashboardPage />}
          >
            <LayoutPage />
          </ProtectedRoute>
        }
        errorElement={<ErrorPage />}
      />

      {/* Protected routes (profile + appointments) */}
      <Route
        element={
          <ProtectedRoute
            adminComponent={<AdminDashboardPage />}
            specialistComponent={<SpecialistDashboardPage />}
            userComponent={<UserDashboardPage />}
            showDashboard={false}
          >
            <LayoutPage />
          </ProtectedRoute>
        }
        errorElement={<ErrorPage />}
      >
        <Route path={ROUTES.PROFILE} element={<ProfilePage />} />
        <Route path={ROUTES.APPOINTMENTS} element={<AppointmentPage />} />
        <Route
          path={ROUTES.NOTIFICATIONS}
          element={<SpecialistNotificationsPage />}
        />
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
        <Route path={ROUTES.SEND_OTP} element={<SendOtp />} />
        <Route path={ROUTES.VERIFY_OTP} element={<VerifyOtp />} />
      </Route>
    </>,
  ),
);

export default router;
