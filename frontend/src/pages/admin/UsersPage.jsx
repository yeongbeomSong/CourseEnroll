import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { Layout } from '../../components/Layout';
import { adminApi } from '../../lib/api';
import { Search, Trash2, Loader2, Users as UsersIcon } from 'lucide-react';

export function UsersPage() {
  const queryClient = useQueryClient();
  const [search, setSearch] = useState('');
  const [roleFilter, setRoleFilter] = useState('');

  const { data: result, isLoading } = useQuery({
    queryKey: ['adminUsers', { q: search, name: search, role: roleFilter }],
    queryFn: () => adminApi.users({ q: search, name: search, role: roleFilter }),
  });

  const users = result?.users ?? result ?? [];
  const deleteMutation = useMutation({
    mutationFn: adminApi.deleteUser,
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['adminUsers'] }),
  });

  const handleDelete = (user) => {
    if (!confirm(`"${user.name}" 계정을 강제 삭제하시겠습니까? 이 작업은 되돌릴 수 없습니다.`)) return;
    deleteMutation.mutate(user.id);
  };

  return (
    <Layout title="사용자 관리">
      <div className="space-y-6">
        <div className="flex flex-wrap gap-4 items-center bg-white p-4 rounded-xl border border-slate-200">
          <div className="relative flex-1 min-w-[200px]">
            <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-slate-400" />
            <input
              type="text"
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              placeholder="학번, 이름 검색"
              className="w-full pl-10 pr-4 py-2 border border-slate-300 rounded-lg focus:ring-2 focus:ring-indigo-500"
            />
          </div>
          <select
            value={roleFilter}
            onChange={(e) => setRoleFilter(e.target.value)}
            className="px-4 py-2 border border-slate-300 rounded-lg"
          >
            <option value="">전체</option>
            <option value="student">학생</option>
            <option value="professor">교수</option>
          </select>
        </div>

        {isLoading ? (
          <div className="flex justify-center py-12">
            <Loader2 className="w-8 h-8 animate-spin text-indigo-600" />
          </div>
        ) : (
          <div className="bg-white rounded-xl border border-slate-200 overflow-hidden">
            <table className="w-full">
              <thead className="bg-slate-50 border-b border-slate-200">
                <tr>
                  <th className="p-3 text-left text-sm font-medium text-slate-700">학번/사번</th>
                  <th className="p-3 text-left text-sm font-medium text-slate-700">이름</th>
                  <th className="p-3 text-left text-sm font-medium text-slate-700">역할</th>
                  <th className="p-3 text-left text-sm font-medium text-slate-700">학과</th>
                  <th className="p-3 text-right text-sm font-medium text-slate-700">관리</th>
                </tr>
              </thead>
              <tbody>
                {users.map((u) => (
                  <tr key={u.id} className="border-b border-slate-100 last:border-0 hover:bg-slate-50/50">
                    <td className="p-3 text-slate-800">{u.studentId}</td>
                    <td className="p-3 text-slate-800">{u.name}</td>
                    <td className="p-3">
                      <span className={`px-2 py-0.5 rounded text-xs ${u.role === 'admin' ? 'bg-slate-200' : u.role === 'professor' ? 'bg-indigo-100 text-indigo-700' : 'bg-slate-100 text-slate-700'}`}>
                        {u.role === 'admin' ? '관리자' : u.role === 'professor' ? '교수' : '학생'}
                      </span>
                    </td>
                    <td className="p-3 text-slate-600">{u.departmentName ?? '-'}</td>
                    <td className="p-3 text-right">
                      {u.role !== 'admin' && (
                        <button
                          type="button"
                          onClick={() => handleDelete(u)}
                          disabled={deleteMutation.isPending}
                          className="p-2 text-slate-500 hover:text-red-600 hover:bg-red-50 rounded-lg"
                          title="강제 삭제"
                        >
                          <Trash2 className="w-4 h-4" />
                        </button>
                      )}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
            <div className="p-3 bg-slate-50 text-sm text-slate-500 flex items-center gap-2">
              <UsersIcon className="w-4 h-4" /> 총 {users.length}명
            </div>
          </div>
        )}

        {!isLoading && users.length === 0 && (
          <div className="text-center py-12 text-slate-500">
            <UsersIcon className="w-12 h-12 mx-auto mb-4 opacity-50" />
            <p>조건에 맞는 사용자가 없습니다.</p>
          </div>
        )}
      </div>
    </Layout>
  );
}
