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
} from "@/pages";
import { ROUTES } from "@/utils/constants";
import {
  ForgetPassword,
  Login,
  // ProtectedRoute,
  Register,
  ResetPassword,
  VerifyEmailPage,
} from "@/components";

const router = createBrowserRouter(
  createRoutesFromElements(
    <>
      {/* <Route
        element={
          <ProtectedRoute>
            <LayoutPage />
          </ProtectedRoute>
        }
        errorElement={<ErrorPage />}
      >
        <Route index path={ROUTES.HOME} element={<HomePage />} />
        <Route path={ROUTES.ABOUT} element={<AboutPage />} />
        <Route path={ROUTES.SERVICE} element={<ServicePage />} />
      </Route> */}

      <Route element={<LayoutPage />}>
        <Route index path={ROUTES.HOME} element={<HomePage />} />
        <Route path={ROUTES.ABOUT} element={<AboutPage />} />
        <Route path={ROUTES.SERVICE} element={<ServicePage />} />
        <Route path={ROUTES.FORUM} element={<ForumPage />} />
      </Route>

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
