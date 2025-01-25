import { Footer, Header } from "@/components";
import { Outlet } from "react-router-dom";

const LayoutPage = () => {
  return (
    <main>
      <Header />
      <Outlet />
      <Footer />
    </main>
  );
};
export default LayoutPage;
