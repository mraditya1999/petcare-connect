import { ReactNode, useEffect } from "react";
import { useAppSelector } from "@/app/hooks";
import { useNavigate } from "react-router-dom";
import { ROUTES } from "@/utils/constants";

interface ProtectedRouteProps {
  children: ReactNode;
}

const ProtectedRoute = ({ children }: ProtectedRouteProps) => {
  const navigate = useNavigate();
  const user = useAppSelector((state) => state.auth.user);

  useEffect(() => {
    if (!user) {
      navigate(ROUTES.HOME);
    }
  }, [user, navigate]);

  return user ? <>{children}</> : null;
};

export default ProtectedRoute;
