import { configureStore } from "@reduxjs/toolkit";
import themeReducer from "@/features/theme/themeSlice";
import authReducer from "@/features/auth/authSlice";
import userReducer from "@/features/user/userSlice";
import forumDetailReducer from "@/features/forum/forumDetailSlice";
import forumListReducer from "@/features/forumList/forumListSlice";

export const store = configureStore({
  reducer: {
    auth: authReducer,
    user: userReducer,
    theme: themeReducer,
    forumDetail: forumDetailReducer,
    forumList: forumListReducer,
  },
});

export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;

export type ReduxStore = {
  getState: () => RootState;
  dispatch: AppDispatch;
};
