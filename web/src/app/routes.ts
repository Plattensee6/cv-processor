import { createBrowserRouter } from 'react-router';

// Layouts
import { AdminLayout } from './layouts/AdminLayout';
import { PublicLayout } from './layouts/PublicLayout';
import { ProtectedAdminLayout } from './components/ProtectedAdminLayout';

// Auth Pages
import { Login } from './pages/auth/Login';
import { Register } from './pages/auth/Register';
import { ForgotPassword } from './pages/auth/ForgotPassword';
import { ResetPassword } from './pages/auth/ResetPassword';

// Admin Pages
import { Dashboard } from './pages/admin/Dashboard';
import { Applications } from './pages/admin/Applications';
import { JobAds } from './pages/admin/JobAds';
import { CreateJobAd } from './pages/admin/CreateJobAd';
import { Campaigns } from './pages/admin/Campaigns';
import { Settings } from './pages/admin/Settings';

// Public Pages
import { Home } from './pages/public/Home';
import { Careers } from './pages/public/Careers';
import { ApplyJob } from './pages/public/ApplyJob';

export const router = createBrowserRouter([
  // Public Routes
  {
    path: '/',
    Component: PublicLayout,
    children: [
      { index: true, Component: Home },
      { path: 'careers', Component: Careers },
      { path: 'careers/:jobId/apply', Component: ApplyJob },
      { path: 'about', Component: Home }, // Placeholder
      { path: 'contact', Component: Home }, // Placeholder
    ],
  },
  // Admin: auth pages + protected dashboard (HR only)
  {
    path: '/admin',
    Component: ProtectedAdminLayout,
    children: [
      { path: 'login', Component: Login },
      { path: 'register', Component: Register },
      { path: 'forgot-password', Component: ForgotPassword },
      { path: 'reset-password', Component: ResetPassword },
      {
        path: '',
        Component: AdminLayout,
        children: [
          { index: true, Component: Dashboard },
          { path: 'applications', Component: Applications },
          { path: 'jobs', Component: JobAds },
          { path: 'jobs/new', Component: CreateJobAd },
          { path: 'jobs/:jobId', Component: CreateJobAd },
          { path: 'jobs/:jobId/edit', Component: CreateJobAd },
          { path: 'campaigns', Component: Campaigns },
          { path: 'settings', Component: Settings },
        ],
      },
    ],
  },
]);
