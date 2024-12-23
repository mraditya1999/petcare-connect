import { Outlet } from 'react-router-dom';
import { Header, Footer } from '@components';
const RootLayout = () => {
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

export default RootLayout;
