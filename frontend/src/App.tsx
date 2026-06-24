import { RouterProvider } from "react-router-dom";
import router from "./routes";
import ThemeProvider from "./components/shared/ThemeProvider";
import { GoogleOAuthProvider } from "@react-oauth/google";

const clientId = import.meta.env.VITE_GOOGLE_CLIENT_ID || "";

const App = () => {
  return (
    <GoogleOAuthProvider clientId={clientId}>
      <ThemeProvider>
        <RouterProvider router={router} />
      </ThemeProvider>
    </GoogleOAuthProvider>
  );
};

export default App;
