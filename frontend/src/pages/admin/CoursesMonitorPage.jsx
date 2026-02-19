import { useQuery } from '@tanstack/react-query';
import { Layout } from '../../components/Layout';
import { adminApi } from '../../lib/api';
import { BookOpen, Loader2, Users } from 'lucide-react';

export function CoursesMonitorPage() {
  const { data: courses = [], isLoading } = useQuery({
    queryKey: ['adminCourses'],
    queryFn: adminApi.courses,
  });

  return (
    <Layout title="전체 강의 모니터링">
      <div className="space-y-4">
        {isLoading ? (
          <div className="flex justify-center py-12">
            <Loader2 className="w-8 h-8 animate-spin text-indigo-600" />
          </div>
        ) : (
          <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
            {courses.map((course) => {
              const enrolled = course.currentEnrollment ?? course.enrolledCount ?? 0;
              const capacity = course.capacity ?? 0;
              const isFull = capacity > 0 && enrolled >= capacity;
              const ratio = capacity > 0 ? Math.round((enrolled / capacity) * 100) : 0;
              return (
                <div
                  key={course.id}
                  className={`bg-white rounded-xl border p-5 ${
                    isFull ? 'border-amber-200 bg-amber-50/30' : 'border-slate-200'
                  }`}
                >
                  <div className="flex items-start gap-3">
                    <div className="w-10 h-10 rounded-lg bg-indigo-100 flex items-center justify-center shrink-0">
                      <BookOpen className="w-5 h-5 text-indigo-600" />
                    </div>
                    <div className="min-w-0 flex-1">
                      <h3 className="font-semibold text-slate-800 truncate">{course.title ?? course.name}</h3>
                      <p className="text-sm text-slate-500">{course.departmentName} · {course.professorName ?? '-'}</p>
                      <div className="mt-2 flex items-center gap-2 text-sm">
                        <Users className="w-4 h-4 text-slate-400" />
                        <span className={isFull ? 'text-amber-700 font-medium' : 'text-slate-600'}>
                          {enrolled} / {capacity}명
                        </span>
                        <span className="text-slate-400">({ratio}%)</span>
                      </div>
                      <div className="mt-1 h-1.5 bg-slate-200 rounded-full overflow-hidden">
                        <div
                          className={`h-full rounded-full ${isFull ? 'bg-amber-500' : 'bg-indigo-500'}`}
                          style={{ width: `${Math.min(ratio, 100)}%` }}
                        />
                      </div>
                    </div>
                  </div>
                </div>
              );
            })}
          </div>
        )}

        {!isLoading && courses.length === 0 && (
          <div className="text-center py-12 text-slate-500">
            <BookOpen className="w-12 h-12 mx-auto mb-4 opacity-50" />
            <p>등록된 강의가 없습니다.</p>
          </div>
        )}
      </div>
    </Layout>
  );
}
