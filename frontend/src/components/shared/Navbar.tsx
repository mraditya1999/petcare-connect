import ThemeSwitcher from "../ui/ThemeSwitcher";
import NavLinks from "./NavLinks";

const Navbar = () => {
  return (
    <div className="relative">
      <nav className="fixed left-0 right-0 top-6 z-50 mx-auto flex w-[90vw] max-w-3xl items-center justify-between rounded-full bg-white px-6 py-2 shadow-md">
        <div className="text-lg font-semibold">PetCare Connect</div>
        <ul className="flex space-x-6">
          <NavLinks />
        </ul>
        <div className="flex items-center gap-3">
          <button className="rounded-full bg-primary px-6 py-2 text-white hover:bg-blue-600">
            Join
          </button>
          <ThemeSwitcher />
        </div>
      </nav>
    </div>
  );
};

export default Navbar;
