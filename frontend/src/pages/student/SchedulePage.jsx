import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { Layout } from '../../components/Layout';
import { coursesApi } from '../../lib/api';
import { Calendar, Trash2, Loader2 } from 'lucide-react';

const DAYS = ['월', '화', '수', '목', '금'];
const HOURS = Array.from({ length: 12 }, (_, i) => i + 9); // 9~20

function parseSchedule(scheduleStr) {
  if (!scheduleStr || typeof scheduleStr !== 'string') return [];
  const m = scheduleStr.match(/^(월|화|수|목|금)\s*(\d{1,2}):(\d{2})-(\d{1,2}):(\d{2})$/);
  if (!m) return [];
  const [, day, sh, , eh] = m;
  return [{ day, start: parseInt(sh, 10), end: parseInt(eh, 10) }];
}

function buildGrid(enrollments) {
  const grid = {};
  DAYS.forEach((d) => {
    grid[d] = {};
    HOURS.forEach((h) => (grid[d][h] = null));
  });
  (enrollments || []).forEach((e) => {
    const course = e.course || e;
    const scheduleStr = course?.schedule ?? e.schedule;
    const scheduleList = Array.isArray(course?.schedule) ? course.schedule : parseSchedule(scheduleStr);
    const items = scheduleList.length ? scheduleList : [{ day: '월', start: 9, end: 10 }];
    items.forEach((s) => {
      const day = s.dayOfWeek ?? s.day;
      const start = s.startHour ?? s.start;
      const end = s.endHour ?? s.end ?? start + 1;
      if (grid[day]) {
        for (let h = start; h < end; h++) {
          if (grid[day][h] == null) grid[day][h] = { name: e.title ?? course?.title ?? e.courseCode, courseId: e.courseId, registrationId: e.registrationId };
        }
      }
    });
  });
  return grid;
}

export function SchedulePage() {
  const queryClient = useQueryClient();
  const { data: enrollments = [], isLoading } = useQuery({
    queryKey: ['myEnrollments'],
    queryFn: coursesApi.myEnrollments,
  });

  const cancelMutation = useMutation({
    mutationFn: (id) => coursesApi.cancel(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['myEnrollments'] });
      queryClient.invalidateQueries({ queryKey: ['courses'] });
    },
  });

  const grid = buildGrid(enrollments);

  return (
    <Layout title="내 수강 시간표">
      <div className="space-y-6">
        {isLoading ? (
          <div className="flex justify-center py-12">
            <Loader2 className="w-8 h-8 animate-spin text-indigo-600" />
          </div>
        ) : (
          <>
            <div className="overflow-x-auto bg-white rounded-xl border border-slate-200">
              <table className="w-full border-collapse min-w-[600px]">
                <thead>
                  <tr className="border-b border-slate-200">
                    <th className="p-3 text-left text-slate-600 w-16">시간</th>
                    {DAYS.map((d) => (
                      <th key={d} className="p-3 text-center text-slate-700 font-medium w-24">
                        {d}
                      </th>
                    ))}
                  </tr>
                </thead>
                <tbody>
                  {HOURS.map((hour) => (
                    <tr key={hour} className="border-b border-slate-100">
                      <td className="p-2 text-sm text-slate-500">{hour}:00</td>
                      {DAYS.map((day) => {
                        const cell = grid[day]?.[hour];
                        return (
                          <td key={`${day}-${hour}`} className="p-1 align-top">
                            {cell ? (
                              <div className="rounded-lg bg-indigo-50 border border-indigo-200 p-2 text-sm">
                                <div className="font-medium text-slate-800 truncate">{cell.name}</div>
                                <div className="text-xs text-slate-500">{cell.departmentName ?? ''}</div>
                                <button
                                  type="button"
                                  onClick={() => {
                                    if (confirm('이 강의 신청을 취소하시겠습니까?')) {
                                      cancelMutation.mutate(cell.courseId);
                                    }
                                  }}
                                  disabled={cancelMutation.isPending}
                                  className="mt-1 text-xs text-red-600 hover:underline flex items-center gap-0.5"
                                >
                                  <Trash2 className="w-3 h-3" /> 취소
                                </button>
                              </div>
                            ) : (
                              <div className="h-12" />
                            )}
                          </td>
                        );
                      })}
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
            <div className="flex items-center gap-2 text-slate-500 text-sm">
              <Calendar className="w-4 h-4" /> 총 {enrollments.length}개 강의 신청
            </div>
          </>
        )}
      </div>
    </Layout>
  );
}
