import { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';
import { UserPlus, GraduationCap } from 'lucide-react';
import { authApi, departmentsApi } from '../../lib/api';

export function SignupPage() {
  const [userType, setUserType] = useState('student');
  const [studentId, setStudentId] = useState('');
  const [password, setPassword] = useState('');
  const [name, setName] = useState('');
  const [departmentId, setDepartmentId] = useState('');
  const [error, setError] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const navigate = useNavigate();

  const { data: departments = [], isLoading: deptLoading } = useQuery({
    queryKey: ['departments'],
    queryFn: departmentsApi.list,
  });

  useEffect(() => {
    if (departments.length && !departmentId) setDepartmentId(departments[0].id ?? '');
  }, [departments, departmentId]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSubmitting(true);
    try {
      await authApi.signup({
        username: studentId,
        password,
        name,
        role: `ROLE_${userType.toUpperCase()}`,
        departmentId: departmentId ? Number(departmentId) : undefined,
        studentNumber: userType === 'student' ? studentId : undefined,
        professorCode: userType === 'professor' ? studentId : undefined,
      });
      navigate('/login', { state: { message: '회원가입이 완료되었습니다. 로그인해 주세요.' } });
    } catch (err) {
      setError(err.data?.message || err.message || '회원가입에 실패했습니다.');
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-indigo-50 to-slate-100 flex items-center justify-center p-4">
      <div className="w-full max-w-md bg-white rounded-2xl shadow-xl p-8">
        <div className="flex justify-center mb-6">
          <div className="w-14 h-14 rounded-full bg-indigo-100 flex items-center justify-center">
            <UserPlus className="w-8 h-8 text-indigo-600" />
          </div>
        </div>
        <h1 className="text-2xl font-bold text-center text-slate-800 mb-2">회원가입</h1>
        <p className="text-slate-500 text-center text-sm mb-6">유형을 선택하고 학과를 선택하세요</p>

        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label className="block text-sm font-medium text-slate-700 mb-2">유저 타입</label>
            <div className="flex gap-4">
              <label className="flex items-center gap-2 cursor-pointer">
                <input
                  type="radio"
                  name="userType"
                  value="student"
                  checked={userType === 'student'}
                  onChange={() => setUserType('student')}
                  className="text-indigo-600"
                />
                학생
              </label>
              <label className="flex items-center gap-2 cursor-pointer">
                <input
                  type="radio"
                  name="userType"
                  value="professor"
                  checked={userType === 'professor'}
                  onChange={() => setUserType('professor')}
                  className="text-indigo-600"
                />
                교수
              </label>
            </div>
          </div>

          <div>
            <label className="block text-sm font-medium text-slate-700 mb-1">학번 / 사번</label>
            <input
              type="text"
              value={studentId}
              onChange={(e) => setStudentId(e.target.value)}
              className="w-full px-4 py-2.5 border border-slate-300 rounded-lg focus:ring-2 focus:ring-indigo-500"
              placeholder={userType === 'student' ? '학번' : '사번'}
              required
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-slate-700 mb-1">비밀번호</label>
            <input
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              className="w-full px-4 py-2.5 border border-slate-300 rounded-lg focus:ring-2 focus:ring-indigo-500"
              placeholder="비밀번호"
              required
              minLength={4}
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-slate-700 mb-1">이름</label>
            <input
              type="text"
              value={name}
              onChange={(e) => setName(e.target.value)}
              className="w-full px-4 py-2.5 border border-slate-300 rounded-lg focus:ring-2 focus:ring-indigo-500"
              placeholder="이름"
              required
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-slate-700 mb-1">학과</label>
            <select
              value={departmentId}
              onChange={(e) => setDepartmentId(e.target.value)}
              className="w-full px-4 py-2.5 border border-slate-300 rounded-lg focus:ring-2 focus:ring-indigo-500"
              required
              disabled={deptLoading}
            >
              <option value="">학과 선택</option>
              {departments.map((d) => (
                <option key={d.id} value={d.id}>
                  {d.name}
                </option>
              ))}
            </select>
          </div>

          {error && (
            <div className="text-sm text-red-600 bg-red-50 px-3 py-2 rounded-lg">{error}</div>
          )}
          <button
            type="submit"
            disabled={submitting || deptLoading}
            className="w-full py-3 bg-indigo-600 text-white font-medium rounded-lg hover:bg-indigo-700 disabled:opacity-50 flex items-center justify-center gap-2"
          >
            <GraduationCap className="w-4 h-4" /> 가입하기
          </button>
        </form>

        <p className="text-center text-slate-500 text-sm mt-6">
          이미 계정이 있으신가요?{' '}
          <Link to="/login" className="text-indigo-600 font-medium hover:underline">로그인</Link>
        </p>
      </div>
    </div>
  );
}
