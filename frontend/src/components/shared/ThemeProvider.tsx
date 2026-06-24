import { useEffect, useRef } from "react";
import { setTheme } from "@/features/theme/themeSlice";
import { useAppDispatch, useAppSelector } from "@/app/hooks";
import { getSystemTheme } from "@/utils/helpers";

const ThemeProvider: React.FC<{ children: React.ReactNode }> = ({
  children,
}) => {
  const dispatch = useAppDispatch();
  const theme = useAppSelector((state) => state.theme.theme);
  const initializedRef = useRef(false);

  // Initialize theme from localStorage once
  useEffect(() => {
    if (initializedRef.current) return;
    initializedRef.current = true;

    const storedTheme = localStorage.getItem("theme");
    let initialTheme: "light" | "dark";

    if (storedTheme === "light" || storedTheme === "dark") {
      initialTheme = storedTheme;
    } else {
      // Default to system preference for first-time users
      initialTheme = getSystemTheme();
    }

    dispatch(setTheme(initialTheme));
  }, [dispatch]);

  // Apply theme to document
  useEffect(() => {
    document.documentElement.className = theme;

    // Update favicon
    const favicon = document.querySelector(
      'link[rel="icon"]',
    ) as HTMLLinkElement;
    if (favicon) {
      favicon.href = theme === "dark" ? "/icon-dark.ico" : "/icon-light.ico";
    }
  }, [theme]);

  return <>{children}</>;
};

export default ThemeProvider;
