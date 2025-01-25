import { useEffect } from "react";
import { setTheme } from "@/features/theme/themeSlice";
import { useAppDispatch, useAppSelector } from "@/app/hooks";
import { Theme } from "@/utils/helpers";

const ThemeProvider: React.FC<{ children: React.ReactNode }> = ({
  children,
}) => {
  const dispatch = useAppDispatch();
  const theme = useAppSelector((state) => state.theme.theme);

  useEffect(() => {
    const storedTheme = localStorage.getItem("theme") as Theme | null;

    if (storedTheme) {
      dispatch(setTheme(storedTheme));
    } else {
      dispatch(setTheme("system"));
    }
  }, [dispatch]);

  useEffect(() => {
    document.documentElement.className = theme;
  }, [theme]);

  return <>{children}</>;
};

export default ThemeProvider;
