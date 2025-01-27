// import { useAppSelector } from "@/app/hooks";
// import { LoadingSpinner } from "@/components/ui/LoadingSpinner";
// import { ROUTES } from "@/utils/constants";
// import { useEffect } from "react";
import { Outlet } from "react-router-dom";
// import { Outlet, useNavigate } from "react-router-dom";
import dogImg from "@/assets/images/authpage/dog.jpeg";

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
      <section
        className="hidden h-full w-full place-items-center bg-cover bg-center md:col-span-1 md:grid"
        style={{ backgroundImage: `url(${dogImg})` }}
      >
        <div className="h-3/5">
          <img
            src={dogImg}
            alt="dog holding teddy in his mouth"
            className="hidden"
          />
        </div>
      </section>
      <section className="col-span-1 grid h-full w-full place-items-center bg-background px-5 shadow-md">
        <Outlet />
      </section>
    </div>

    // <div
    //   className="grid min-h-screen md:grid-cols-2"
    //   style={{
    //     backgroundImage: `url(${dogImg})`,
    //     backgroundSize: "cover",
    //     backgroundPosition: "center",
    //     backgroundRepeat: "no-repeat",
    //   }}
    // >
    //   <div className="hidden md:block"></div>
    //   <section className="h-full w-full place-items-center md:col-span-1 md:grid">
    //     <div className="h-3/5">
    //       <img
    //         src={dogImg}
    //         alt="dog holding teddy in his mouth"
    //         className="hidden"
    //       />
    //     </div>
    //     <Outlet />
    //   </section>
    // </div>
  );
};
export default AuthLayoutPage;
