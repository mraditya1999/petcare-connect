import { ReactNode } from "react";
import { useAppSelector } from "@/app/hooks";

interface RoleBasedUIProps {
  adminComponent: ReactNode;
  specialistComponent: ReactNode;
  userComponent: ReactNode;
}

const RoleBasedUI = ({
  adminComponent,
  specialistComponent,
  userComponent,
}: RoleBasedUIProps) => {
  const user = useAppSelector((state) => state.auth.user);

  const roles = user?.data?.roles || [];

  if (roles.includes("ADMIN")) return <>{adminComponent}</>;
  if (roles.includes("SPECIALIST")) return <>{specialistComponent}</>;

  return <>{userComponent}</>;
};

export default RoleBasedUI;
