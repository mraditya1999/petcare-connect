import { RouterProvider } from "react-router-dom";
import router from "./routes";
import ThemeProvider from "./components/shared/ThemeProvider";
import { GoogleOAuthProvider } from "@react-oauth/google";

const clientId = import.meta.env.VITE_GOOGLE_CLIENT_ID || "";

const App = () => {
  const appContent = (
    <ThemeProvider>
      <RouterProvider router={router} />
    </ThemeProvider>
  );

  return clientId ? (
    <GoogleOAuthProvider clientId={clientId}>{appContent}</GoogleOAuthProvider>
  ) : (
    appContent
  );
};

export default App;
