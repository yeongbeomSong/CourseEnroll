import { useParams, Link } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';
import { Layout } from '../../components/Layout';
import { professorCoursesApi } from '../../lib/api';
import { Users, Loader2, ArrowLeft } from 'lucide-react';

export function EnrollmentsPage() {
  const { id } = useParams();
  const { data: enrollments = [], isLoading } = useQuery({
    queryKey: ['professorEnrollments', id],
    queryFn: () => professorCoursesApi.enrollments(id),
  });

  return (
    <Layout title="수강생 명단">
      <div className="space-y-6">
        <Link to="/professor" className="inline-flex items-center gap-2 text-slate-600 hover:text-indigo-600">
          <ArrowLeft className="w-4 h-4" /> 대시보드로
        </Link>

        {isLoading ? (
          <div className="flex justify-center py-12">
            <Loader2 className="w-8 h-8 animate-spin text-indigo-600" />
          </div>
        ) : (
          <div className="bg-white rounded-xl border border-slate-200 overflow-hidden">
            <table className="w-full">
              <thead className="bg-slate-50 border-b border-slate-200">
                <tr>
                  <th className="p-3 text-left text-sm font-medium text-slate-700">학번</th>
                  <th className="p-3 text-left text-sm font-medium text-slate-700">이름</th>
                  <th className="p-3 text-left text-sm font-medium text-slate-700">학과</th>
                </tr>
              </thead>
              <tbody>
                {enrollments.map((e) => (
                  <tr key={e.registrationId ?? e.id} className="border-b border-slate-100 last:border-0">
                    <td className="p-3 text-slate-800">{e.studentNumber ?? e.studentId}</td>
                    <td className="p-3 text-slate-800">{e.studentName ?? e.name}</td>
                    <td className="p-3 text-slate-600">{e.departmentName ?? '-'}</td>
                  </tr>
                ))}
              </tbody>
            </table>
            <div className="p-3 bg-slate-50 text-sm text-slate-500 flex items-center gap-2">
              <Users className="w-4 h-4" /> 총 {enrollments.length}명
            </div>
          </div>
        )}

        {!isLoading && enrollments.length === 0 && (
          <div className="text-center py-12 text-slate-500">
            <Users className="w-12 h-12 mx-auto mb-4 opacity-50" />
            <p>수강 신청한 학생이 없습니다.</p>
          </div>
        )}
      </div>
    </Layout>
  );
}
