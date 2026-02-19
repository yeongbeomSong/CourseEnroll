import { Link } from 'react-router-dom';
import { Layout } from '../../components/Layout';
import { BookOpen, Calendar, User } from 'lucide-react';

export function StudentHomePage() {
  return (
    <Layout title="학생">
      <div className="grid gap-4 md:grid-cols-3">
        <Link
          to="/student/courses"
          className="bg-white rounded-xl border border-slate-200 p-6 hover:border-indigo-300 hover:shadow-md transition flex items-center gap-4"
        >
          <div className="w-14 h-14 rounded-xl bg-indigo-100 flex items-center justify-center">
            <BookOpen className="w-7 h-7 text-indigo-600" />
          </div>
          <div>
            <h2 className="font-semibold text-slate-800">강의 목록 / 신청</h2>
            <p className="text-sm text-slate-500">강의 검색 및 수강 신청</p>
          </div>
        </Link>
        <Link
          to="/student/schedule"
          className="bg-white rounded-xl border border-slate-200 p-6 hover:border-indigo-300 hover:shadow-md transition flex items-center gap-4"
        >
          <div className="w-14 h-14 rounded-xl bg-indigo-100 flex items-center justify-center">
            <Calendar className="w-7 h-7 text-indigo-600" />
          </div>
          <div>
            <h2 className="font-semibold text-slate-800">내 수강 시간표</h2>
            <p className="text-sm text-slate-500">신청 내역 및 취소</p>
          </div>
        </Link>
        <Link
          to="/student/mypage"
          className="bg-white rounded-xl border border-slate-200 p-6 hover:border-indigo-300 hover:shadow-md transition flex items-center gap-4"
        >
          <div className="w-14 h-14 rounded-xl bg-indigo-100 flex items-center justify-center">
            <User className="w-7 h-7 text-indigo-600" />
          </div>
          <div>
            <h2 className="font-semibold text-slate-800">마이페이지</h2>
            <p className="text-sm text-slate-500">개인정보 및 비밀번호 변경</p>
          </div>
        </Link>
      </div>
    </Layout>
  );
}
