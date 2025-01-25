import { useEffect } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { useAppDispatch, useAppSelector } from "@/app/hooks";
import { verifyEmail } from "@/features/user/userThunk";
import GenericAlert from "../shared/GenericAlert";
import { ROUTES } from "@/utils/constants";

function useQuery() {
  return new URLSearchParams(useLocation().search);
}

const VerifyEmailPage = () => {
  const query = useQuery();
  const navigate = useNavigate();
  const dispatch = useAppDispatch();
  const { loading, error, success } = useAppSelector((state) => state.user);

  useEffect(() => {
    if (!loading && !success) {
      const token = query.get("token");
      const email = query.get("email");
      if (token && email) {
        dispatch(verifyEmail({ token, email, navigate })).unwrap();
      } else {
        navigate(ROUTES.REGISTER);
      }
    }
  }, []);

  useEffect(() => {
    if (success) {
      const timer = setTimeout(() => {
        navigate(ROUTES.LOGIN);
      }, 3000);
      return () => clearTimeout(timer);
    }
  }, [success, navigate]);

  if (error) {
    return (
      <GenericAlert
        title="Email Verification Failed"
        message="Please register again to verify your email!"
        success={false}
      />
    );
  }

  if (loading) {
    return (
      <GenericAlert
        title="Verifying Email"
        message="Please wait..."
        success={false}
      />
    );
  }

  return (
    <GenericAlert
      title="Email Verified"
      message="Email Verification Successful. Redirecting to Login Page..."
      success={true}
    />
  );
};

export default VerifyEmailPage;
