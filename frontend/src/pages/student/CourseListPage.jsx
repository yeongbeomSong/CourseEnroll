import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { Layout } from '../../components/Layout';
import { coursesApi, departmentsApi } from '../../lib/api';
import { Filter, BookOpen, UserCheck, Loader2, CheckCircle, XCircle, AlertCircle, Clock, UserX } from 'lucide-react';

const REFETCH_INTERVAL = 10000; // 실시간 잔여석 갱신 10초

export function CourseListPage() {
  const [deptFilter, setDeptFilter] = useState('');
  const [categoryFilter, setCategoryFilter] = useState('');
  const queryClient = useQueryClient();

  const { data: departments = [] } = useQuery({
    queryKey: ['departments'],
    queryFn: departmentsApi.list,
  });

  const { data: coursesRaw = [], isLoading } = useQuery({
    queryKey: ['courses', { departmentId: deptFilter || undefined, category: categoryFilter || undefined }],
    queryFn: () => coursesApi.list({ departmentId: deptFilter || undefined, category: categoryFilter || undefined }),
    refetchInterval: REFETCH_INTERVAL,
  });

  const { data: myEnrollments = [] } = useQuery({
    queryKey: ['myEnrollments'],
    queryFn: coursesApi.myEnrollments,
  });

  const { data: waitingPositions = [] } = useQuery({
    queryKey: ['waitingPositions'],
    queryFn: coursesApi.waitingPositions,
  });

  const courseIds = new Set((myEnrollments || []).map((e) => e.courseId));
  const waitingByCourse = Object.fromEntries((waitingPositions || []).map((p) => [p.courseId, p.position]));
  const courses = (Array.isArray(coursesRaw) ? coursesRaw : []).map((c) => ({
    ...c,
    name: c.title ?? c.name,
    credits: c.credit ?? c.credits,
    enrolledCount: c.currentEnrollment ?? c.enrolledCount,
    remainingSeats: (c.capacity ?? 0) - (c.currentEnrollment ?? c.enrolledCount ?? 0),
    creditType: c.category ?? c.creditType,
    enrolled: courseIds.has(c.id),
    waitingPosition: waitingByCourse[c.id] ?? 0,
  }));

  const applyMutation = useMutation({
    mutationFn: (id) => coursesApi.apply(id),
    onSuccess: (result) => {
      queryClient.invalidateQueries({ queryKey: ['courses'] });
      queryClient.invalidateQueries({ queryKey: ['myEnrollments'] });
      queryClient.invalidateQueries({ queryKey: ['waitingPositions'] });
      if (result?.inWaitlist && result?.waitingPosition) {
        alert(`대기열 ${result.waitingPosition}번째로 등록되었습니다. 취소 시 순번대로 자동 배정됩니다.`);
      }
    },
  });

  const leaveWaitingMutation = useMutation({
    mutationFn: (courseId) => coursesApi.leaveWaiting(courseId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['waitingPositions'] });
      queryClient.invalidateQueries({ queryKey: ['courses'] });
    },
  });

  const handleApply = (course) => {
    if (course.enrolled) return;
    applyMutation.mutate(course.id, {
      onError: (err) => {
        alert(err.data?.message || err.message || '신청에 실패했습니다.');
      },
    });
  };

  return (
    <Layout title="강의 목록 / 신청">
      <div className="space-y-6">
        <div className="flex flex-wrap gap-4 items-center bg-white p-4 rounded-xl border border-slate-200">
          <Filter className="w-5 h-5 text-slate-500" />
          <div className="flex gap-4 flex-wrap">
            <div>
              <label className="text-sm text-slate-600 mr-2">학과</label>
              <select
                value={deptFilter}
                onChange={(e) => setDeptFilter(e.target.value)}
                className="px-3 py-2 border border-slate-300 rounded-lg min-w-[180px]"
              >
                <option value="">전체 학과</option>
                {(Array.isArray(departments) ? departments : []).map((d) => (
                  <option key={d.id} value={d.id}>{d.name}</option>
                ))}
              </select>
            </div>
            <div>
              <label className="text-sm text-slate-600 mr-2">이수구분</label>
              <select
                value={categoryFilter}
                onChange={(e) => setCategoryFilter(e.target.value)}
                className="px-3 py-2 border border-slate-300 rounded-lg w-40"
              >
                <option value="">전체</option>
                <option value="MAJOR_REQUIRED">전공필수</option>
                <option value="MAJOR_SELECT">전공선택</option>
                <option value="GENERAL">교양</option>
              </select>
            </div>
          </div>
        </div>

        {isLoading ? (
          <div className="flex justify-center py-12">
            <Loader2 className="w-8 h-8 animate-spin text-indigo-600" />
          </div>
        ) : (
          <div className="grid gap-4">
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
                    <h3 className="font-semibold text-slate-800">{course.name}</h3>
                    <p className="text-sm text-slate-500">
                      {course.departmentName} · {course.creditType || '-'} · {course.credits}학점
                      {(course.targetGrade ?? course.targetYear) != null && (
                        <> · <span className="text-indigo-600 font-medium">{(course.targetGrade ?? course.targetYear)}학년</span></>
                      )}
                    </p>
                    {course.schedule && (
                      <p className="text-sm text-slate-600 mt-0.5">
                        강의 시간: {course.schedule}
                      </p>
                    )}
                    <p className="text-sm text-slate-600 mt-1">
                      정원: {course.capacity}명 · 신청: {course.enrolledCount ?? 0}명 ·{' '}
                      <span className={(course.remainingSeats ?? (course.capacity - (course.enrolledCount || 0))) <= 0 ? 'text-red-600 font-medium' : 'text-green-600'}>
                        잔여 {course.remainingSeats ?? Math.max(0, (course.capacity || 0) - (course.enrolledCount || 0))}석
                      </span>
                    </p>
                  </div>
                </div>
                <div className="flex items-center gap-3 flex-wrap">
                  {course.enrolled ? (
                    <span className="inline-flex items-center gap-1 px-3 py-1.5 rounded-lg bg-green-100 text-green-700 text-sm">
                      <CheckCircle className="w-4 h-4" /> 신청됨
                    </span>
                  ) : course.waitingPosition > 0 ? (
                    <>
                      <span className="inline-flex items-center gap-1 px-3 py-1.5 rounded-lg bg-amber-100 text-amber-800 text-sm">
                        <Clock className="w-4 h-4" /> 대기 {course.waitingPosition}번째
                      </span>
                      <button
                        type="button"
                        onClick={() => leaveWaitingMutation.mutate(course.id)}
                        disabled={leaveWaitingMutation.isPending}
                        className="inline-flex items-center gap-1 px-3 py-1.5 text-sm border border-slate-300 rounded-lg hover:bg-slate-50"
                      >
                        <UserX className="w-4 h-4" /> 대기열 포기
                      </button>
                    </>
                  ) : (course.remainingSeats ?? (course.capacity - (course.enrolledCount || 0))) <= 0 ? (
                    <button
                      type="button"
                      onClick={() => handleApply(course)}
                      disabled={applyMutation.isPending}
                      className="inline-flex items-center gap-2 px-4 py-2 bg-amber-600 text-white rounded-lg hover:bg-amber-700 disabled:opacity-50"
                    >
                      {applyMutation.isPending ? <Loader2 className="w-4 h-4 animate-spin" /> : <Clock className="w-4 h-4" />}
                      대기열 신청
                    </button>
                  ) : (
                    <button
                      type="button"
                      onClick={() => handleApply(course)}
                      disabled={applyMutation.isPending}
                      className="inline-flex items-center gap-2 px-4 py-2 bg-indigo-600 text-white rounded-lg hover:bg-indigo-700 disabled:opacity-50"
                    >
                      {applyMutation.isPending ? <Loader2 className="w-4 h-4 animate-spin" /> : <UserCheck className="w-4 h-4" />}
                      신청
                    </button>
                  )}
                </div>
              </div>
            ))}
          </div>
        )}

        {!isLoading && courses.length === 0 && (
          <div className="text-center py-12 text-slate-500 flex flex-col items-center gap-2">
            <AlertCircle className="w-12 h-12" />
            <p>조건에 맞는 강의가 없습니다.</p>
          </div>
        )}
      </div>
    </Layout>
  );
}
