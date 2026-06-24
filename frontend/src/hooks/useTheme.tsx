import { useAppDispatch, useAppSelector } from "@/app/hooks";
import { setTheme } from "@/features/theme/themeSlice";

export const useTheme = () => {
  const theme = useAppSelector((state) => state.theme.theme);
  const dispatch = useAppDispatch();

  const changeTheme = (newTheme: "light" | "dark") => {
    dispatch(setTheme(newTheme));
  };

  const toggleTheme = () => {
    const newTheme = theme === "dark" ? "light" : "dark";
    dispatch(setTheme(newTheme));
  };

  return {
    theme,
    setTheme: changeTheme,
    toggleTheme,
    isLight: theme === "light",
    isDark: theme === "dark",
  };
};
