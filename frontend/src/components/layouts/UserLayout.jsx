import { Outlet } from 'react-router-dom';
import Footer from '../common/Footer';
import Header from '../common/Header';

const UserLayout = () => {
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

export default UserLayout;
