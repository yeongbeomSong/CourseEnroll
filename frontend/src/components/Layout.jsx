import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import { LogOut, User, LayoutDashboard, BookOpen, Users, GraduationCap } from 'lucide-react';

export function Layout({ children, title }) {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <div className="min-h-screen bg-slate-50">
      <header className="bg-white border-b border-slate-200 sticky top-0 z-10">
        <div className="max-w-6xl mx-auto px-4 h-14 flex items-center justify-between">
          <Link to={user?.role === 'admin' ? '/admin' : user?.role === 'professor' ? '/professor' : '/student'} className="font-semibold text-slate-800 flex items-center gap-2">
            <GraduationCap className="w-6 h-6 text-indigo-600" />
            수강신청
          </Link>
          <nav className="flex items-center gap-4">
            {user?.role === 'student' && (
              <>
                <Link to="/student/courses" className="text-slate-600 hover:text-indigo-600 flex items-center gap-1">
                  <BookOpen className="w-4 h-4" /> 강의 목록
                </Link>
                <Link to="/student/schedule" className="text-slate-600 hover:text-indigo-600 flex items-center gap-1">
                  <LayoutDashboard className="w-4 h-4" /> 시간표
                </Link>
                <Link to="/student/mypage" className="text-slate-600 hover:text-indigo-600 flex items-center gap-1">
                  <User className="w-4 h-4" /> 마이페이지
                </Link>
              </>
            )}
            {user?.role === 'professor' && (
              <>
                <Link to="/professor" className="text-slate-600 hover:text-indigo-600 flex items-center gap-1">
                  <LayoutDashboard className="w-4 h-4" /> 대시보드
                </Link>
                <Link to="/professor/courses/new" className="text-slate-600 hover:text-indigo-600">강의 등록</Link>
              </>
            )}
            {user?.role === 'admin' && (
              <>
                <Link to="/admin/departments" className="text-slate-600 hover:text-indigo-600 flex items-center gap-1">
                  <GraduationCap className="w-4 h-4" /> 학과
                </Link>
                <Link to="/admin/users" className="text-slate-600 hover:text-indigo-600 flex items-center gap-1">
                  <Users className="w-4 h-4" /> 사용자
                </Link>
                <Link to="/admin/courses" className="text-slate-600 hover:text-indigo-600">강의 모니터링</Link>
              </>
            )}
            <button type="button" onClick={handleLogout} className="text-slate-500 hover:text-red-600 flex items-center gap-1">
              <LogOut className="w-4 h-4" /> 로그아웃
            </button>
          </nav>
        </div>
      </header>
      <main className="max-w-6xl mx-auto px-4 py-6">
        {title && <h1 className="text-2xl font-bold text-slate-800 mb-6">{title}</h1>}
        {children}
      </main>
    </div>
  );
}
