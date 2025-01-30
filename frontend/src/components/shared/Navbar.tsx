// import ThemeSwitcher from "../ui/ThemeSwitcher";
import { ROUTES } from "@/utils/constants";
import NavLinks from "./NavLinks";
import { FaBars } from "react-icons/fa6";
import { Link } from "react-router-dom";
import { useAppSelector } from "@/app/hooks";

const Navbar = () => {
  const user = useAppSelector((state) => state.user.user);

  return (
    <div className="relative">
      <nav className="fixed left-0 right-0 top-6 z-50 mx-auto hidden w-[90vw] max-w-2xl items-center justify-between rounded-full bg-white px-6 py-2 shadow-md md:flex">
        <div className="text-lg font-semibold">PawCare</div>
        <ul className="flex space-x-6">
          <NavLinks />
        </ul>
        <div className="flex items-center gap-1">
        {user ? (
            <Link
              to={ROUTES.PROFILE} 
              className="rounded-full bg-primary px-6 py-2 text-white hover:bg-primary/90"
            >
              Profile
            </Link>
          ) : (
            <Link
              to={ROUTES.LOGIN}
              className="rounded-full bg-primary px-6 py-2 text-white hover:bg-primary/90"
            >
              Join
            </Link>
          )}
          {/* <ThemeSwitcher /> */}
        </div>
      </nav>

      <div className="flex-between h-10 w-full px-6 md:hidden">
        <div className="h2">Petcare</div>
        <button type="button">
          <FaBars />
        </button>
      </div>
    </div>
  );
};

export default Navbar;
