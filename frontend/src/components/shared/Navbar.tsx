import { ROUTES } from "@/utils/constants";
import NavLinks from "./NavLinks";
import { FaBars } from "react-icons/fa6";
import { Link, useNavigate } from "react-router-dom";
import { useAppSelector, useAppDispatch } from "@/app/hooks";
import { logoutUser } from "@/features/auth/authThunk";
import logo from "@/assets/images/logo.png";
import darkLogo from "@/assets/images/dark-logo.png";
import CustomButton from "./CustomButton";
import ThemeSwitcher from "../ui/ThemeSwitcher";
import { useTheme } from "@/hooks/useTheme";

const Navbar = () => {
  const dispatch = useAppDispatch();
  const navigate = useNavigate();
  const { theme } = useTheme();
  const user = useAppSelector((state) => state.auth.user);
  const logoSrc = theme === "dark" ? darkLogo : logo;

  const handleLogout = async () => {
    await dispatch(logoutUser());
    navigate(ROUTES.HOME);
  };

  return (
    <div className="relative">
      <nav className="fixed left-0 right-0 top-6 z-50 mx-auto hidden w-[90vw] max-w-2xl items-center justify-between rounded-full bg-card px-6 py-2 text-foreground shadow-md md:flex">
        <div className="text-lg font-semibold">
          <img src={logoSrc} alt="Paw logo" className="w-12" />
        </div>

        <ul className="flex space-x-6">
          <NavLinks />
        </ul>

        <div className="flex items-center gap-4">
          {user ? (
            <CustomButton onClick={handleLogout} buttonText="Logout" />
          ) : (
            <Link
              to={ROUTES.LOGIN}
              className="rounded-full bg-primary px-6 py-2 text-primary-foreground hover:bg-primary/90"
            >
              Join
            </Link>
          )}
          <ThemeSwitcher />
        </div>
      </nav>

      {/* Mobile nav */}
      <div className="flex-between h-10 w-full px-6 text-foreground md:hidden">
        <div className="h2">Petcare</div>
        <button type="button">
          <FaBars />
        </button>
      </div>
    </div>
  );
};

export default Navbar;
