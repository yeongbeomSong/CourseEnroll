import { Link } from 'react-router-dom';
import { FileQuestion } from 'lucide-react';

export function NotFoundPage() {
  return (
    <div className="min-h-screen bg-slate-50 flex items-center justify-center p-4">
      <div className="text-center max-w-md">
        <div className="inline-flex items-center justify-center w-20 h-20 rounded-full bg-slate-200 mb-6">
          <FileQuestion className="w-10 h-10 text-slate-600" />
        </div>
        <h1 className="text-3xl font-bold text-slate-800 mb-2">404</h1>
        <p className="text-slate-600 mb-6">요청하신 페이지를 찾을 수 없습니다.</p>
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
