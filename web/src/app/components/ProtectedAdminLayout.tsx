import { Navigate, Outlet, useLocation } from 'react-router';
import { useAuth } from '../context/AuthContext';

const AUTH_PATHS = ['/admin/login', '/admin/register', '/admin/forgot-password', '/admin/reset-password'];

export function ProtectedAdminLayout() {
  const { isAuthenticated, isLoading } = useAuth();
  const location = useLocation();
  const isAuthPath = AUTH_PATHS.some((p) => location.pathname === p || location.pathname.startsWith(p + '?'));

  if (isLoading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-50">
        <p className="text-gray-500">Betöltés...</p>
      </div>
    );
  }

  if (!isAuthPath && !isAuthenticated) {
    return <Navigate to="/admin/login" replace />;
  }

  return <Outlet />;
}
