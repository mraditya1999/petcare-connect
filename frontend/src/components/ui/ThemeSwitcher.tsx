import { useAppDispatch, useAppSelector } from "@/app/hooks";
import { setTheme } from "@/features/theme/themeSlice";
import { FaSun, FaMoon } from "react-icons/fa";

type Theme = "dark" | "light" | "system";

const ThemeSwitcher: React.FC = () => {
  const dispatch = useAppDispatch();
  const currentTheme = useAppSelector((state) => state.theme.theme);

  const handleThemeChange = (theme: Theme) => {
    dispatch(setTheme(theme));
  };

  return (
    <div>
      {currentTheme === "light" ? (
        <button
          onClick={() => handleThemeChange("dark")}
          style={{
            background: "transparent",
            border: "none",
            cursor: "pointer",
          }}
        >
          <FaMoon />
        </button>
      ) : (
        <button
          onClick={() => handleThemeChange("light")}
          style={{
            background: "transparent",
            border: "none",
            cursor: "pointer",
          }}
        >
          <FaSun />
        </button>
      )}
    </div>
  );
};

export default ThemeSwitcher;
