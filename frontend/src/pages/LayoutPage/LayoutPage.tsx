import { Footer, } from "@/components";
import Navbar from "@/components/shared/Navbar";
import { Outlet } from "react-router-dom";

const LayoutPage = () => {
  return (
    <main>
      <Navbar />
      <div className="min-h-screen">
        <Outlet />
      </div>
      <Footer />
    </main>
  );
};
export default LayoutPage;
