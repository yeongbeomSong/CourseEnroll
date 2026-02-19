import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { AuthProvider, useAuth } from './contexts/AuthContext';
import { ProtectedRoute } from './components/ProtectedRoute';

import { LoginPage } from './pages/common/LoginPage';
import { SignupPage } from './pages/common/SignupPage';
import { ForbiddenPage } from './pages/common/ForbiddenPage';
import { NotFoundPage } from './pages/common/NotFoundPage';

import { StudentHomePage } from './pages/student/StudentHomePage';
import { CourseListPage } from './pages/student/CourseListPage';
import { SchedulePage } from './pages/student/SchedulePage';
import { MypagePage } from './pages/student/MypagePage';

import { ProfessorDashboardPage } from './pages/professor/ProfessorDashboardPage';
import { CourseFormPage } from './pages/professor/CourseFormPage';
import { EnrollmentsPage } from './pages/professor/EnrollmentsPage';

import { AdminHomePage } from './pages/admin/AdminHomePage';
import { DepartmentsPage } from './pages/admin/DepartmentsPage';
import { UsersPage } from './pages/admin/UsersPage';
import { CoursesMonitorPage } from './pages/admin/CoursesMonitorPage';

const queryClient = new QueryClient({
  defaultOptions: {
    queries: { staleTime: 30 * 1000, retry: 1 },
  },
});

function HomeRedirect() {
  const { user, loading } = useAuth();
  if (loading) return null;
  if (!user) return <Navigate to="/login" replace />;
  if (user.role === 'admin') return <Navigate to="/admin" replace />;
  if (user.role === 'professor') return <Navigate to="/professor" replace />;
  return <Navigate to="/student" replace />;
}

function AppRoutes() {
  return (
    <Routes>
      <Route path="/" element={<HomeRedirect />} />
      <Route path="/login" element={<LoginPage />} />
      <Route path="/signup" element={<SignupPage />} />
      <Route path="/403" element={<ForbiddenPage />} />
      <Route path="/404" element={<NotFoundPage />} />

      <Route
        path="/student"
        element={
          <ProtectedRoute allowedRoles={['student']}>
            <StudentHomePage />
          </ProtectedRoute>
        }
      />
      <Route
        path="/student/courses"
        element={
          <ProtectedRoute allowedRoles={['student']}>
            <CourseListPage />
          </ProtectedRoute>
        }
      />
      <Route
        path="/student/schedule"
        element={
          <ProtectedRoute allowedRoles={['student']}>
            <SchedulePage />
          </ProtectedRoute>
        }
      />
      <Route
        path="/student/mypage"
        element={
          <ProtectedRoute allowedRoles={['student']}>
            <MypagePage />
          </ProtectedRoute>
        }
      />

      <Route
        path="/professor"
        element={
          <ProtectedRoute allowedRoles={['professor']}>
            <ProfessorDashboardPage />
          </ProtectedRoute>
        }
      />
      <Route
        path="/professor/courses/new"
        element={
          <ProtectedRoute allowedRoles={['professor']}>
            <CourseFormPage />
          </ProtectedRoute>
        }
      />
      <Route
        path="/professor/courses/:id/edit"
        element={
          <ProtectedRoute allowedRoles={['professor']}>
            <CourseFormPage />
          </ProtectedRoute>
        }
      />
      <Route
        path="/professor/courses/:id/enrollments"
        element={
          <ProtectedRoute allowedRoles={['professor']}>
            <EnrollmentsPage />
          </ProtectedRoute>
        }
      />

      <Route
        path="/admin"
        element={
          <ProtectedRoute allowedRoles={['admin']}>
            <AdminHomePage />
          </ProtectedRoute>
        }
      />
      <Route
        path="/admin/departments"
        element={
          <ProtectedRoute allowedRoles={['admin']}>
            <DepartmentsPage />
          </ProtectedRoute>
        }
      />
      <Route
        path="/admin/users"
        element={
          <ProtectedRoute allowedRoles={['admin']}>
            <UsersPage />
          </ProtectedRoute>
        }
      />
      <Route
        path="/admin/courses"
        element={
          <ProtectedRoute allowedRoles={['admin']}>
            <CoursesMonitorPage />
          </ProtectedRoute>
        }
      />

      <Route path="*" element={<NotFoundPage />} />
    </Routes>
  );
}

export default function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <AuthProvider>
        <BrowserRouter>
          <AppRoutes />
        </BrowserRouter>
      </AuthProvider>
    </QueryClientProvider>
  );
}
