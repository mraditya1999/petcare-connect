import { useAppDispatch, useAppSelector } from "@/app/hooks";
import { setTheme } from "@/features/theme/themeSlice";
import { FaSun, FaMoon } from "react-icons/fa";
import { Button } from "@/components/ui/button";

const ThemeSwitcher: React.FC = () => {
  const dispatch = useAppDispatch();
  const currentTheme = useAppSelector((state) => state.theme.theme);

  const handleToggle = () => {
    const newTheme = currentTheme === "dark" ? "light" : "dark";
    dispatch(setTheme(newTheme));
  };

  return (
    <Button
      variant="ghost"
      size="sm"
      className="w-9 px-0"
      onClick={handleToggle}
    >
      {currentTheme === "dark" ? (
        <FaSun className="h-4 w-4" />
      ) : (
        <FaMoon className="h-4 w-4" />
      )}
      <span className="sr-only">Toggle theme</span>
    </Button>
  );
};

export default ThemeSwitcher;
