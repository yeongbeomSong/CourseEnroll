import { Link } from 'react-router-dom';
import { ShieldAlert } from 'lucide-react';

export function ForbiddenPage() {
  return (
    <div className="min-h-screen bg-slate-50 flex items-center justify-center p-4">
      <div className="text-center max-w-md">
        <div className="inline-flex items-center justify-center w-20 h-20 rounded-full bg-amber-100 mb-6">
          <ShieldAlert className="w-10 h-10 text-amber-600" />
        </div>
        <h1 className="text-3xl font-bold text-slate-800 mb-2">403</h1>
        <p className="text-slate-600 mb-6">
          이 페이지에 접근할 권한이 없습니다. 학생은 관리자/교수 전용 페이지에 접근할 수 없습니다.
        </p>
        <Link
          to="/"
          className="inline-flex items-center justify-center px-6 py-3 bg-indigo-600 text-white font-medium rounded-lg hover:bg-indigo-700"
        >
          홈으로 돌아가기
        </Link>
      </div>
    </div>
  );
}
