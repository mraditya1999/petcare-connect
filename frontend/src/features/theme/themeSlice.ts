import { getInitialTheme, Theme } from "@/utils/helpers";
import { createSlice, PayloadAction } from "@reduxjs/toolkit";

export type ThemeState = {
  theme: Theme;
};

const initialState: ThemeState = {
  theme: getInitialTheme(),
};

export const themeSlice = createSlice({
  name: "theme",
  initialState,
  reducers: {
    setTheme: (state, action: PayloadAction<Theme>) => {
      state.theme = action.payload;
      localStorage.setItem("theme", action.payload);
      document.documentElement.className = action.payload;
    },
  },
});

export const { setTheme } = themeSlice.actions;
export default themeSlice.reducer;
