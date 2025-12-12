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
import {
  Sheet,
  SheetTrigger,
  SheetContent,
  SheetClose,
  SheetTitle,
} from "@/components/ui/sheet";
import { VisuallyHidden } from "@radix-ui/react-visually-hidden";

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
      {/* Desktop Navbar */}
      <nav className="fixed left-0 right-0 top-6 z-50 mx-auto hidden w-[90vw] max-w-2xl items-center justify-between rounded-full bg-card px-6 py-2 text-foreground shadow-md md:flex">
        <div className="text-lg font-semibold">
          <Link to={ROUTES.HOME}>
            <img src={logoSrc} alt="Paw logo" className="w-12 cursor-pointer" />
          </Link>
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

      {/* Mobile Navbar */}
      <div className="flex-between h-10 w-full px-6 md:hidden">
        <div className="text-lg font-semibold">
          <Link to={ROUTES.HOME}>
            <img src={logoSrc} alt="Paw logo" className="w-10 cursor-pointer" />
          </Link>
        </div>

        <Sheet>
          <SheetTrigger asChild>
            <button type="button">
              <FaBars size={20} />
            </button>
          </SheetTrigger>

          <SheetContent side="left" className="w-64">
            <VisuallyHidden>
              <SheetTitle>Navigation Menu</SheetTitle>
            </VisuallyHidden>

            <div className="mt-8 flex flex-col gap-6">
              {/* Top row in drawer: logo + theme */}
              <div className="flex-between text-lg font-semibold">
                <Link to={ROUTES.HOME}>
                  <img
                    src={logoSrc}
                    alt="Paw logo"
                    className="w-10 cursor-pointer"
                  />
                </Link>
                <ThemeSwitcher />
              </div>

              {/* Navigation Links */}
              <nav className="flex flex-col gap-4 text-lg">
                <NavLinks mobile />
              </nav>

              {/* Join / Logout */}
              <div className="mt-4 flex flex-col gap-3">
                {user ? (
                  <SheetClose asChild>
                    <CustomButton onClick={handleLogout} buttonText="Logout" />
                  </SheetClose>
                ) : (
                  <SheetClose asChild>
                    <Link
                      to={ROUTES.LOGIN}
                      className="rounded-full bg-primary px-6 py-2 text-center text-primary-foreground hover:bg-primary/90"
                    >
                      Join
                    </Link>
                  </SheetClose>
                )}
              </div>
            </div>
          </SheetContent>
        </Sheet>
      </div>
    </div>
  );
};

export default Navbar;
