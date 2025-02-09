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
} from "@/pages";
import { ROUTES } from "@/utils/constants";
import {
  ForgetPassword,
  Login,
  ProtectedRoute,
  // ProtectedRoute,
  Register,
  ResetPassword,
  VerifyEmailPage,
} from "@/components";

const router = createBrowserRouter(
  createRoutesFromElements(
    <>
      {/* Protected Route  */}
      <Route
        element={
          <ProtectedRoute>
            <LayoutPage />
          </ProtectedRoute>
        }
        errorElement={<ErrorPage />}
      >
        <Route path={ROUTES.PROFILE} element={<ProfilePage />} />
      </Route>

      {/* Public Route */}
      <Route element={<LayoutPage />}>
        {/* <Route path={ROUTES.PROFILE} element={<ProfilePage />} /> */}
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
      </Route>
    </>,
  ),
);

export default router;
