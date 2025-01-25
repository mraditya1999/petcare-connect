// import { useAppSelector } from "@/app/hooks";
import AuthSvg from "@/assets/images/AuthSvg";
// import { LoadingSpinner } from "@/components/ui/LoadingSpinner";
// import { ROUTES } from "@/utils/constants";
// import { useEffect } from "react";
import { Outlet } from "react-router-dom";
// import { Outlet, useNavigate } from "react-router-dom";

const AuthLayoutPage = () => {
  // const navigate = useNavigate();
  // const user = useAppSelector((state) => state.user.user);

  // useEffect(() => {
  //   if (user) {
  //     navigate(ROUTES.HOME);
  //   }
  // }, [user, navigate]);

  // if (user) {
  //   return (
  //     <div className="flex min-h-screen w-full items-center justify-center">
  //       <LoadingSpinner />
  //     </div>
  //   );
  // }

  return (
    <div className="grid min-h-screen grid-cols-1 place-items-center md:grid-cols-2">
      <section className="hidden h-full w-full place-items-center bg-background md:col-span-1 md:grid">
        <div className="h-3/5">
          <AuthSvg />
        </div>
      </section>
      <section className="col-span-1 grid h-full w-full place-items-center bg-background px-5 shadow-md">
        <Outlet />
      </section>
    </div>
  );
};
export default AuthLayoutPage;
