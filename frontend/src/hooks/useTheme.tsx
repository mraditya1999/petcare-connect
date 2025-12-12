import { useAppDispatch, useAppSelector } from "@/app/hooks";
import { setTheme } from "@/features/theme/themeSlice";

export const useTheme = () => {
  const theme = useAppSelector((state) => state.theme.theme);
  const dispatch = useAppDispatch();

  const changeTheme = (newTheme: "dark" | "light" | "system") => {
    dispatch(setTheme(newTheme));
  };

  return { theme, setTheme: changeTheme };
};
