import { useEffect } from "react";
import { setTheme } from "@/features/theme/themeSlice";
import { useAppDispatch, useAppSelector } from "@/app/hooks";
import { Theme } from "@/utils/helpers";

const ThemeProvider: React.FC<{ children: React.ReactNode }> = ({
  children,
}) => {
  const dispatch = useAppDispatch();
  const theme = useAppSelector((state) => state.theme.theme);

  // Initialize theme from localStorage
  useEffect(() => {
    const storedTheme = localStorage.getItem("theme") as Theme | null;

    if (storedTheme) {
      dispatch(setTheme(storedTheme));
    } else {
      dispatch(setTheme("system"));
    }
  }, [dispatch]);

  // Apply theme class to <html>
  useEffect(() => {
    document.documentElement.className = theme;
  }, [theme]);

  // Update favicon based on theme
  useEffect(() => {
    const favicon = document.getElementById("favicon") as HTMLLinkElement;
    if (!favicon) return;

    if (theme === "dark") {
      favicon.href = "/icon-dark.ico";
    } else {
      favicon.href = "/icon-light.ico";
    }
  }, [theme]);

  return <>{children}</>;
};

export default ThemeProvider;
