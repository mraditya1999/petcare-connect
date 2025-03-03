import { ReactNode, useEffect } from "react";
import { useAppSelector } from "@/app/hooks";
import { useNavigate } from "react-router-dom";
import { ROUTES } from "@/utils/constants";
import RoleBasedUI from "@/components/shared/RoleBasedUI";

interface ProtectedRouteProps {
  children: ReactNode;
  adminComponent: ReactNode;
  specialistComponent: ReactNode;
  userComponent: ReactNode;
}

const ProtectedRoute = ({
  children,
  adminComponent,
  specialistComponent,
  userComponent,
}: ProtectedRouteProps) => {
  const navigate = useNavigate();
  const user = useAppSelector((state) => state.auth.user);

  useEffect(() => {
    if (!user) {
      navigate(ROUTES.HOME);
    }
  }, [user, navigate]);

  return user ? (
    <>
      <RoleBasedUI
        adminComponent={adminComponent}
        specialistComponent={specialistComponent}
        userComponent={userComponent}
      />
      {children}
    </>
  ) : null;
};

export default ProtectedRoute;
