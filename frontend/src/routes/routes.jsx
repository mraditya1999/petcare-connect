import {
  createBrowserRouter,
  createRoutesFromElements,
  Route,
} from 'react-router-dom';
import { HomePage, AboutPage, ContactPage } from '@pages';
import AdminLayout from '@layouts/AdminLayout';
import SpecialistLayout from '@layouts/SpecialistLayout';
import UserLayout from '@layouts/UserLayout';
import ProtectedRoute from './ProtectedRoute';
import RootLayout from '@layouts/RootLayout';

const isAuthenticated = true; // Replace with your actual authentication logic
const userRole = 'admin'; // Replace with your actual role logic

const router = createBrowserRouter(
  createRoutesFromElements(
    <Route path='/' element={<RootLayout />}>
      <Route index element={<HomePage />} />
      <Route path='about' element={<AboutPage />} />
      <Route path='contact' element={<ContactPage />} />

      {/* Admin Routes */}
      <Route
        path='admin'
        element={
          <ProtectedRoute isAllowed={isAuthenticated && userRole === 'admin'}>
            <AdminLayout />
          </ProtectedRoute>
        }
      >
        <Route path='dashboard' element={<h1>Admin Dashboard</h1>} />
        {/* Add more admin routes here */}
      </Route>

      {/* Specialist Routes */}
      <Route
        path='specialist'
        element={
          <ProtectedRoute
            isAllowed={isAuthenticated && userRole === 'specialist'}
          >
            <SpecialistLayout />
          </ProtectedRoute>
        }
      >
        <Route path='dashboard' element={<h1>Specialist Dashboard</h1>} />
        {/* Add more specialist routes here */}
      </Route>

      {/* User Routes */}
      <Route
        path='user'
        element={
          <ProtectedRoute isAllowed={isAuthenticated && userRole === 'user'}>
            <UserLayout />
          </ProtectedRoute>
        }
      >
        <Route path='dashboard' element={<h1>User Dashboard</h1>} />
        {/* Add more user routes here */}
      </Route>
    </Route>
  )
);

export default router;
