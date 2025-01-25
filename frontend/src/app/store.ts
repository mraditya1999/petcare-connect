import { configureStore } from "@reduxjs/toolkit";
import themeReducer from "@/features/theme/themeSlice";
import userReducer from "@/features/user/userSlice";

export const store = configureStore({
  reducer: {
    user: userReducer,
    theme: themeReducer,
  },
});

export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;

export type ReduxStore = {
  getState: () => RootState;
  dispatch: AppDispatch;
};
