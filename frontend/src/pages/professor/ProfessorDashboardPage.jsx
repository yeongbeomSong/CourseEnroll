import { Link } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';
import { Layout } from '../../components/Layout';
import { professorCoursesApi } from '../../lib/api';
import { BookOpen, Users, Plus, Loader2 } from 'lucide-react';

export function ProfessorDashboardPage() {
  const { data: courses = [], isLoading } = useQuery({
    queryKey: ['professorCourses'],
    queryFn: professorCoursesApi.myCourses,
  });

  return (
    <Layout title="강의 관리 대시보드">
      <div className="space-y-6">
        <div className="flex justify-end">
          <Link
            to="/professor/courses/new"
            className="inline-flex items-center gap-2 px-4 py-2 bg-indigo-600 text-white rounded-lg hover:bg-indigo-700"
          >
            <Plus className="w-4 h-4" /> 강의 등록
          </Link>
        </div>

        {isLoading ? (
          <div className="flex justify-center py-12">
            <Loader2 className="w-8 h-8 animate-spin text-indigo-600" />
          </div>
        ) : (
          <div className="grid gap-4 md:grid-cols-2">
            {courses.map((course) => (
              <div
                key={course.id}
                className="bg-white rounded-xl border border-slate-200 p-5 flex flex-wrap items-center justify-between gap-4"
              >
                <div className="flex items-start gap-4 flex-1 min-w-0">
                  <div className="w-12 h-12 rounded-lg bg-indigo-100 flex items-center justify-center shrink-0">
                    <BookOpen className="w-6 h-6 text-indigo-600" />
                  </div>
                  <div className="min-w-0">
                    <h3 className="font-semibold text-slate-800">{course.title ?? course.name}</h3>
                    <p className="text-sm text-slate-500">
                      {course.credit ?? course.credits}학점 · 정원 {course.capacity}명
                    </p>
                    <p className="text-sm text-slate-600 mt-1 flex items-center gap-1">
                      <Users className="w-4 h-4" /> 신청 인원: {course.currentEnrollment ?? course.enrolledCount ?? 0}명
                    </p>
                  </div>
                </div>
                <div className="flex gap-2">
                  <Link
                    to={`/professor/courses/${course.id}/enrollments`}
                    className="px-3 py-2 text-sm border border-slate-300 rounded-lg hover:bg-slate-50"
                  >
                    수강생 명단
                  </Link>
                  <Link
                    to={`/professor/courses/${course.id}/edit`}
                    className="px-3 py-2 text-sm bg-slate-100 rounded-lg hover:bg-slate-200"
                  >
                    수정
                  </Link>
                </div>
              </div>
            ))}
          </div>
        )}

        {!isLoading && courses.length === 0 && (
          <div className="text-center py-12 bg-white rounded-xl border border-slate-200 text-slate-500">
            <BookOpen className="w-12 h-12 mx-auto mb-4 opacity-50" />
            <p>개설한 강의가 없습니다.</p>
            <Link to="/professor/courses/new" className="mt-4 inline-block text-indigo-600 font-medium hover:underline">
              강의 등록하기
            </Link>
          </div>
        )}
      </div>
    </Layout>
  );
}
