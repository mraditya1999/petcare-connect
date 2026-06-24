import { configureStore } from "@reduxjs/toolkit";
import themeReducer from "@/features/theme/themeSlice";
import authReducer from "@/features/auth/authSlice";
import userReducer from "@/features/user/userSlice";
import forumDetailReducer from "@/features/forum/forumDetailSlice";
import forumListReducer from "@/features/forumList/forumListSlice";
import appointmentReducer from "@/features/appointment/appointmentSlice";
import petReducer from "@/features/pet/petSlice";
import specialistReducer from "@/features/specialist/specialistSlice";

export const store = configureStore({
  reducer: {
    auth: authReducer,
    user: userReducer,
    theme: themeReducer,
    forumList: forumListReducer,
    forumDetail: forumDetailReducer,
    appointment: appointmentReducer,
    pet: petReducer,
    specialist: specialistReducer,
  },
});

export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;

export type ReduxStore = {
  getState: () => RootState;
  dispatch: AppDispatch;
};
