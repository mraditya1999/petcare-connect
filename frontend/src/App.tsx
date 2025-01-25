import { RouterProvider } from "react-router-dom";
import router from "./routes";
import ThemeProvider from "./components/shared/ThemeProvider";

const App = () => {
  return (
    <ThemeProvider>
      <RouterProvider router={router} />
    </ThemeProvider>
  );
};
export default App;
