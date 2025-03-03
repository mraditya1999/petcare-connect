import { ReactNode } from "react";
import { useAppSelector } from "@/app/hooks";

interface RoleBasedUIProps {
  adminComponent: ReactNode;
  specialistComponent: ReactNode;
  userComponent: ReactNode;
}

const RoleBasedUI = ({ adminComponent, specialistComponent, userComponent }: RoleBasedUIProps) => {
  const user = useAppSelector((state) => state.auth.user);

  if (user?.data.roles.includes("ADMIN")) {
    return <>{adminComponent}</>;
  } else if (user?.data.roles.includes("SPECIALIST")) {
    return <>{specialistComponent}</>;
  } else {
    return <>{userComponent}</>;
  }
};

export default RoleBasedUI;
