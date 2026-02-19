import { useState } from 'react';
import { Layout } from '../../components/Layout';
import { useAuth } from '../../contexts/AuthContext';
import { authApi } from '../../lib/api';
import { User, Lock, Loader2 } from 'lucide-react';

export function MypagePage() {
  const { user, refreshUser } = useAuth();
  const [currentPassword, setCurrentPassword] = useState('');
  const [newPassword, setNewPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [message, setMessage] = useState({ type: '', text: '' });
  const [submitting, setSubmitting] = useState(false);

  const handleChangePassword = async (e) => {
    e.preventDefault();
    setMessage({ type: '', text: '' });
    if (newPassword !== confirmPassword) {
      setMessage({ type: 'error', text: '새 비밀번호가 일치하지 않습니다.' });
      return;
    }
    if (newPassword.length < 4) {
      setMessage({ type: 'error', text: '비밀번호는 4자 이상이어야 합니다.' });
      return;
    }
    setSubmitting(true);
    try {
      await authApi.changePassword({ currentPassword, newPassword });
      setMessage({ type: 'success', text: '비밀번호가 변경되었습니다.' });
      setCurrentPassword('');
      setNewPassword('');
      setConfirmPassword('');
    } catch (err) {
      setMessage({ type: 'error', text: err.data?.message || err.message || '비밀번호 변경에 실패했습니다.' });
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <Layout title="마이페이지">
      <div className="max-w-2xl space-y-8">
        <section className="bg-white rounded-xl border border-slate-200 p-6">
          <h2 className="text-lg font-semibold text-slate-800 mb-4 flex items-center gap-2">
            <User className="w-5 h-5" /> 개인 정보
          </h2>
          <dl className="grid grid-cols-2 gap-3 text-sm">
            <dt className="text-slate-500">학번/사번</dt>
            <dd className="text-slate-800 font-medium">{user?.studentId}</dd>
            <dt className="text-slate-500">이름</dt>
            <dd className="text-slate-800">{user?.name}</dd>
            <dt className="text-slate-500">역할</dt>
            <dd className="text-slate-800">{user?.role === 'student' ? '학생' : user?.role === 'professor' ? '교수' : '관리자'}</dd>
            {user?.departmentName && (
              <>
                <dt className="text-slate-500">학과</dt>
                <dd className="text-slate-800">{user.departmentName}</dd>
              </>
            )}
          </dl>
        </section>

        <section className="bg-white rounded-xl border border-slate-200 p-6">
          <h2 className="text-lg font-semibold text-slate-800 mb-4 flex items-center gap-2">
            <Lock className="w-5 h-5" /> 비밀번호 변경
          </h2>
          <form onSubmit={handleChangePassword} className="space-y-4 max-w-sm">
            <div>
              <label className="block text-sm font-medium text-slate-700 mb-1">현재 비밀번호</label>
              <input
                type="password"
                value={currentPassword}
                onChange={(e) => setCurrentPassword(e.target.value)}
                className="w-full px-4 py-2 border border-slate-300 rounded-lg focus:ring-2 focus:ring-indigo-500"
                required
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-slate-700 mb-1">새 비밀번호</label>
              <input
                type="password"
                value={newPassword}
                onChange={(e) => setNewPassword(e.target.value)}
                className="w-full px-4 py-2 border border-slate-300 rounded-lg focus:ring-2 focus:ring-indigo-500"
                required
                minLength={4}
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-slate-700 mb-1">새 비밀번호 확인</label>
              <input
                type="password"
                value={confirmPassword}
                onChange={(e) => setConfirmPassword(e.target.value)}
                className="w-full px-4 py-2 border border-slate-300 rounded-lg focus:ring-2 focus:ring-indigo-500"
                required
              />
            </div>
            {message.text && (
              <div
                className={`text-sm px-3 py-2 rounded-lg ${
                  message.type === 'success' ? 'bg-green-50 text-green-700' : 'bg-red-50 text-red-700'
                }`}
              >
                {message.text}
              </div>
            )}
            <button
              type="submit"
              disabled={submitting}
              className="px-4 py-2 bg-indigo-600 text-white rounded-lg hover:bg-indigo-700 disabled:opacity-50 flex items-center gap-2"
            >
              {submitting ? <Loader2 className="w-4 h-4 animate-spin" /> : null} 변경하기
            </button>
          </form>
        </section>
      </div>
    </Layout>
  );
}
