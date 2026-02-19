import { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { Layout } from '../../components/Layout';
import { professorCoursesApi, departmentsApi } from '../../lib/api';
import { Loader2 } from 'lucide-react';

const DAYS = ['월', '화', '수', '목', '금'];
const HOURS = Array.from({ length: 12 }, (_, i) => i + 9);

function scheduleToString(schedules) {
  if (!schedules?.length) return '';
  return schedules
    .map((s) => `${s.dayOfWeek ?? s.day} ${s.startHour ?? s.start}:00-${s.endHour ?? s.end ?? (s.startHour ?? s.start) + 1}:00`)
    .join(', ');
}

export function CourseFormPage() {
  const { id } = useParams();
  const isEdit = !!id;
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const [name, setName] = useState('');
  const [courseCode, setCourseCode] = useState('');
  const [departmentId, setDepartmentId] = useState('');
  const [credits, setCredits] = useState(3);
  const [capacity, setCapacity] = useState(30);
  const [targetYear, setTargetYear] = useState(1);
  const [creditType, setCreditType] = useState('MAJOR_REQUIRED');
  const [schedules, setSchedules] = useState([{ dayOfWeek: '월', startHour: 9, endHour: 10 }]);

  const { data: departments = [] } = useQuery({ queryKey: ['departments'], queryFn: departmentsApi.list });
  useEffect(() => {
    const list = Array.isArray(departments) ? departments : [];
    if (list.length && !departmentId && !isEdit) setDepartmentId(String(list[0].id));
  }, [departments, isEdit]);
  const { data: course, isLoading: loadingCourse } = useQuery({
    queryKey: ['professorCourse', id],
    queryFn: () => professorCoursesApi.get(id),
    enabled: isEdit,
  });

  useEffect(() => {
    if (course) {
      setName(course.title ?? course.name ?? '');
      setCourseCode(course.courseCode ?? '');
      setDepartmentId(String(course.departmentId ?? ''));
      setCredits(course.credit ?? course.credits ?? 3);
      setCapacity(course.capacity ?? 30);
      setTargetYear(course.targetGrade ?? course.targetYear ?? 1);
      setCreditType(course.category ?? 'MAJOR_REQUIRED');
      const s = course.schedule;
      if (typeof s === 'string' && s) {
        const parts = s.split(',').map((p) => p.trim());
        setSchedules(
          parts.length
            ? parts.map((p) => {
                const m = p.match(/(월|화|수|목|금)\s*(\d+):?\d*-(\d+):?\d*/);
                return m ? { dayOfWeek: m[1], startHour: parseInt(m[2], 10), endHour: parseInt(m[3], 10) } : { dayOfWeek: '월', startHour: 9, endHour: 10 };
              })
            : [{ dayOfWeek: '월', startHour: 9, endHour: 10 }]
        );
      } else {
        setSchedules(Array.isArray(s) && s.length ? s.map((x) => ({ dayOfWeek: x.dayOfWeek ?? '월', startHour: x.startHour ?? 9, endHour: x.endHour ?? 10 })) : [{ dayOfWeek: '월', startHour: 9, endHour: 10 }]);
      }
    }
  }, [course]);

  const saveMutation = useMutation({
    mutationFn: (body) => (isEdit ? professorCoursesApi.update(id, body) : professorCoursesApi.create(body)),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['professorCourses'] });
      navigate('/professor');
    },
  });

  const addSchedule = () => {
    setSchedules((prev) => [...prev, { dayOfWeek: '월', startHour: 9, endHour: 10 }]);
  };
  const removeSchedule = (idx) => {
    setSchedules((prev) => prev.filter((_, i) => i !== idx));
  };
  const updateSchedule = (idx, field, value) => {
    setSchedules((prev) =>
      prev.map((s, i) => (i === idx ? { ...s, [field]: field === 'startHour' || field === 'endHour' ? Number(value) : value } : s))
    );
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    const payload = {
      departmentId: departmentId ? Number(departmentId) : undefined,
      courseCode: courseCode || (isEdit ? undefined : 'C' + Date.now()),
      title: name,
      category: creditType,
      credit: credits,
      capacity,
      targetGrade: targetYear,
      schedule: scheduleToString(schedules),
    };
    if (isEdit) {
      payload.departmentId = payload.departmentId ?? course?.departmentId;
      payload.courseCode = payload.courseCode ?? course?.courseCode;
    }
    saveMutation.mutate(payload, {
      onError: (err) => alert(err.data?.message || err.message || '저장에 실패했습니다.'),
    });
  };

  if (isEdit && loadingCourse) {
    return (
      <Layout>
        <div className="flex justify-center py-12">
          <Loader2 className="w-8 h-8 animate-spin text-indigo-600" />
        </div>
      </Layout>
    );
  }

  return (
    <Layout title={isEdit ? '강의 수정' : '강의 등록'}>
      <form onSubmit={handleSubmit} className="max-w-2xl space-y-6">
        <div className="bg-white rounded-xl border border-slate-200 p-6 space-y-4">
          <div>
            <label className="block text-sm font-medium text-slate-700 mb-1">강의명</label>
            <input
              type="text"
              value={name}
              onChange={(e) => setName(e.target.value)}
              className="w-full px-4 py-2 border border-slate-300 rounded-lg focus:ring-2 focus:ring-indigo-500"
              required
            />
          </div>
          {!isEdit && (
            <div>
              <label className="block text-sm font-medium text-slate-700 mb-1">과목 코드</label>
              <input
                type="text"
                value={courseCode}
                onChange={(e) => setCourseCode(e.target.value)}
                className="w-full px-4 py-2 border border-slate-300 rounded-lg focus:ring-2 focus:ring-indigo-500"
                placeholder="예: CS101"
                required={!isEdit}
              />
            </div>
          )}
          <div>
            <label className="block text-sm font-medium text-slate-700 mb-1">개설 학과</label>
            <select
              value={departmentId}
              onChange={(e) => setDepartmentId(e.target.value)}
              className="w-full px-4 py-2 border border-slate-300 rounded-lg focus:ring-2 focus:ring-indigo-500"
              required
            >
              <option value="">학과 선택</option>
              {(Array.isArray(departments) ? departments : []).map((d) => (
                <option key={d.id} value={d.id}>{d.name}</option>
              ))}
            </select>
          </div>
          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-medium text-slate-700 mb-1">학점</label>
              <select
                value={credits}
                onChange={(e) => setCredits(Number(e.target.value))}
                className="w-full px-4 py-2 border border-slate-300 rounded-lg focus:ring-2 focus:ring-indigo-500"
              >
                {[1, 2, 3, 4].map((n) => (
                  <option key={n} value={n}>{n}학점</option>
                ))}
              </select>
            </div>
            <div>
              <label className="block text-sm font-medium text-slate-700 mb-1">정원</label>
              <input
                type="number"
                min={1}
                value={capacity}
                onChange={(e) => setCapacity(Number(e.target.value))}
                className="w-full px-4 py-2 border border-slate-300 rounded-lg focus:ring-2 focus:ring-indigo-500"
              />
            </div>
          </div>
          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-medium text-slate-700 mb-1">이수구분</label>
              <select
                value={creditType}
                onChange={(e) => setCreditType(e.target.value)}
                className="w-full px-4 py-2 border border-slate-300 rounded-lg focus:ring-2 focus:ring-indigo-500"
              >
                <option value="MAJOR_REQUIRED">전공필수</option>
                <option value="MAJOR_SELECT">전공선택</option>
                <option value="GENERAL">교양</option>
              </select>
            </div>
            <div>
              <label className="block text-sm font-medium text-slate-700 mb-1">대상 학년</label>
              <select
                value={targetYear}
                onChange={(e) => setTargetYear(Number(e.target.value))}
                className="w-full px-4 py-2 border border-slate-300 rounded-lg focus:ring-2 focus:ring-indigo-500"
              >
                {[1, 2, 3, 4].map((n) => (
                  <option key={n} value={n}>{n}학년</option>
                ))}
              </select>
            </div>
          </div>
        </div>

        <div className="bg-white rounded-xl border border-slate-200 p-6">
          <div className="flex items-center justify-between mb-4">
            <label className="block text-sm font-medium text-slate-700">강의 시간</label>
            <button type="button" onClick={addSchedule} className="text-sm text-indigo-600 hover:underline">
              + 시간 추가
            </button>
          </div>
          <div className="space-y-3">
            {schedules.map((s, idx) => (
              <div key={idx} className="flex flex-wrap items-center gap-2">
                <select
                  value={s.dayOfWeek}
                  onChange={(e) => updateSchedule(idx, 'dayOfWeek', e.target.value)}
                  className="px-3 py-2 border border-slate-300 rounded-lg"
                >
                  {DAYS.map((d) => (
                    <option key={d} value={d}>{d}</option>
                  ))}
                </select>
                <select
                  value={s.startHour}
                  onChange={(e) => updateSchedule(idx, 'startHour', e.target.value)}
                  className="px-3 py-2 border border-slate-300 rounded-lg"
                >
                  {HOURS.map((h) => (
                    <option key={h} value={h}>{h}:00</option>
                  ))}
                </select>
                <span className="text-slate-500">~</span>
                <select
                  value={s.endHour}
                  onChange={(e) => updateSchedule(idx, 'endHour', e.target.value)}
                  className="px-3 py-2 border border-slate-300 rounded-lg"
                >
                  {HOURS.map((h) => (
                    <option key={h} value={h}>{h}:00</option>
                  ))}
                </select>
                <button type="button" onClick={() => removeSchedule(idx)} className="text-red-600 text-sm hover:underline">
                  삭제
                </button>
              </div>
            ))}
          </div>
        </div>

        <div className="flex gap-3">
          <button
            type="submit"
            disabled={saveMutation.isPending}
            className="px-6 py-2 bg-indigo-600 text-white rounded-lg hover:bg-indigo-700 disabled:opacity-50 flex items-center gap-2"
          >
            {saveMutation.isPending ? <Loader2 className="w-4 h-4 animate-spin" /> : null}
            {isEdit ? '수정' : '등록'}
          </button>
          <button type="button" onClick={() => navigate(-1)} className="px-6 py-2 border border-slate-300 rounded-lg hover:bg-slate-50">
            취소
          </button>
        </div>
      </form>
    </Layout>
  );
}
