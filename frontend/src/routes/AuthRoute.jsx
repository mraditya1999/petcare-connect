import { Navigate, Outlet } from 'react-router-dom';

const AuthRoute = ({ isAuthenticated, redirectPath = '/login', children }) => {
  if (!isAuthenticated) {
    return <Navigate to={redirectPath} replace />;
  }

  return children ? children : <Outlet />;
};

export default AuthRoute;
