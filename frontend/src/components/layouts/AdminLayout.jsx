import { Outlet } from 'react-router-dom';
import Footer from '../common/Footer';
import Header from '../common/Header';

// eslint-disable-next-line react/prop-types
const AdminLayout = () => {
  return (
    <div>
      <Header />
      <main>
        <Outlet />
      </main>
      <Footer />
    </div>
  );
};

export default AdminLayout;
