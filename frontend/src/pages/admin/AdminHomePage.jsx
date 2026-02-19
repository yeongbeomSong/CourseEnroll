import { Link } from 'react-router-dom';
import { Layout } from '../../components/Layout';
import { GraduationCap, Users, BookOpen } from 'lucide-react';

export function AdminHomePage() {
  return (
    <Layout title="관리자">
      <div className="grid gap-4 md:grid-cols-3">
        <Link
          to="/admin/departments"
          className="bg-white rounded-xl border border-slate-200 p-6 hover:border-indigo-300 hover:shadow-md transition flex items-center gap-4"
        >
          <div className="w-14 h-14 rounded-xl bg-indigo-100 flex items-center justify-center">
            <GraduationCap className="w-7 h-7 text-indigo-600" />
          </div>
          <div>
            <h2 className="font-semibold text-slate-800">학과 관리</h2>
            <p className="text-sm text-slate-500">학과 추가/수정/삭제</p>
          </div>
        </Link>
        <Link
          to="/admin/users"
          className="bg-white rounded-xl border border-slate-200 p-6 hover:border-indigo-300 hover:shadow-md transition flex items-center gap-4"
        >
          <div className="w-14 h-14 rounded-xl bg-indigo-100 flex items-center justify-center">
            <Users className="w-7 h-7 text-indigo-600" />
          </div>
          <div>
            <h2 className="font-semibold text-slate-800">사용자 관리</h2>
            <p className="text-sm text-slate-500">유저 검색 및 삭제</p>
          </div>
        </Link>
        <Link
          to="/admin/courses"
          className="bg-white rounded-xl border border-slate-200 p-6 hover:border-indigo-300 hover:shadow-md transition flex items-center gap-4"
        >
          <div className="w-14 h-14 rounded-xl bg-indigo-100 flex items-center justify-center">
            <BookOpen className="w-7 h-7 text-indigo-600" />
          </div>
          <div>
            <h2 className="font-semibold text-slate-800">강의 모니터링</h2>
            <p className="text-sm text-slate-500">전체 강의 정원 현황</p>
          </div>
        </Link>
      </div>
    </Layout>
  );
}
