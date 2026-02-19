import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { Layout } from '../../components/Layout';
import { adminDepartmentsApi } from '../../lib/api';
import { GraduationCap, Plus, Pencil, Trash2, Loader2, AlertTriangle } from 'lucide-react';

export function DepartmentsPage() {
  const queryClient = useQueryClient();
  const [editingId, setEditingId] = useState(null);
  const [formName, setFormName] = useState('');
  const [showCreate, setShowCreate] = useState(false);
  const [deleteError, setDeleteError] = useState('');

  const { data: departments = [], isLoading } = useQuery({
    queryKey: ['adminDepartments'],
    queryFn: adminDepartmentsApi.list,
  });

  const createMutation = useMutation({
    mutationFn: adminDepartmentsApi.create,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['adminDepartments'] });
      queryClient.invalidateQueries({ queryKey: ['departments'] });
      setShowCreate(false);
      setFormName('');
    },
  });

  const updateMutation = useMutation({
    mutationFn: ({ id, name }) => adminDepartmentsApi.update(id, { name }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['adminDepartments'] });
      queryClient.invalidateQueries({ queryKey: ['departments'] });
      setEditingId(null);
      setFormName('');
    },
  });

  const deleteMutation = useMutation({
    mutationFn: adminDepartmentsApi.delete,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['adminDepartments'] });
      queryClient.invalidateQueries({ queryKey: ['departments'] });
      setDeleteError('');
    },
    onError: (err) => {
      setDeleteError(err.data?.message || err.message || '삭제할 수 없습니다. 소속 인원이 있을 수 있습니다.');
    },
  });

  const handleCreate = (e) => {
    e.preventDefault();
    if (!formName.trim()) return;
    createMutation.mutate({ name: formName.trim() }, { onError: () => {} });
  };

  const handleUpdate = (e, id) => {
    e.preventDefault();
    if (!formName.trim()) return;
    updateMutation.mutate({ id, name: formName.trim() });
  };

  const handleDelete = (d) => {
    if (!confirm(`"${d.name}" 학과를 삭제하시겠습니까? 소속 인원이 있으면 삭제되지 않을 수 있습니다.`)) return;
    setDeleteError('');
    deleteMutation.mutate(d.id);
  };

  return (
    <Layout title="학과 관리">
      <div className="space-y-6">
        <div className="flex justify-end">
          <button
            type="button"
            onClick={() => { setShowCreate(true); setFormName(''); }}
            className="inline-flex items-center gap-2 px-4 py-2 bg-indigo-600 text-white rounded-lg hover:bg-indigo-700"
          >
            <Plus className="w-4 h-4" /> 학과 추가
          </button>
        </div>

        {showCreate && (
          <div className="bg-white rounded-xl border border-slate-200 p-4 flex items-center gap-4">
            <input
              type="text"
              value={formName}
              onChange={(e) => setFormName(e.target.value)}
              placeholder="학과명"
              className="flex-1 px-4 py-2 border border-slate-300 rounded-lg"
              onKeyDown={(e) => e.key === 'Enter' && handleCreate(e)}
            />
            <button
              type="button"
              onClick={handleCreate}
              disabled={createMutation.isPending || !formName.trim()}
              className="px-4 py-2 bg-indigo-600 text-white rounded-lg hover:bg-indigo-700 disabled:opacity-50"
            >
              {createMutation.isPending ? <Loader2 className="w-4 h-4 animate-spin" /> : '추가'}
            </button>
            <button type="button" onClick={() => setShowCreate(false)} className="px-4 py-2 border rounded-lg hover:bg-slate-50">
              취소
            </button>
          </div>
        )}

        {deleteError && (
          <div className="flex items-center gap-2 p-4 bg-amber-50 border border-amber-200 rounded-xl text-amber-800">
            <AlertTriangle className="w-5 h-5 shrink-0" />
            <span>{deleteError}</span>
            <button type="button" onClick={() => setDeleteError('')} className="ml-auto text-amber-600 hover:underline">
              닫기
            </button>
          </div>
        )}

        {isLoading ? (
          <div className="flex justify-center py-12">
            <Loader2 className="w-8 h-8 animate-spin text-indigo-600" />
          </div>
        ) : (
          <div className="bg-white rounded-xl border border-slate-200 overflow-hidden">
            <ul className="divide-y divide-slate-100">
              {departments.map((d) => (
                <li key={d.id} className="p-4 flex items-center justify-between gap-4">
                  {editingId === d.id ? (
                    <form onSubmit={(e) => handleUpdate(e, d.id)} className="flex-1 flex items-center gap-2">
                      <input
                        type="text"
                        value={formName}
                        onChange={(e) => setFormName(e.target.value)}
                        className="flex-1 px-3 py-2 border border-slate-300 rounded-lg"
                      />
                      <button type="submit" disabled={updateMutation.isPending} className="px-3 py-2 bg-indigo-600 text-white rounded-lg text-sm">
                        저장
                      </button>
                      <button type="button" onClick={() => { setEditingId(null); setFormName(''); }} className="px-3 py-2 border rounded-lg text-sm">
                        취소
                      </button>
                    </form>
                  ) : (
                    <>
                      <div className="flex items-center gap-2">
                        <GraduationCap className="w-5 h-5 text-slate-400" />
                        <span className="font-medium text-slate-800">{d.name}</span>
                      </div>
                      <div className="flex items-center gap-2">
                        <button
                          type="button"
                          onClick={() => { setEditingId(d.id); setFormName(d.name); }}
                          className="p-2 text-slate-500 hover:text-indigo-600 hover:bg-indigo-50 rounded-lg"
                          title="수정"
                        >
                          <Pencil className="w-4 h-4" />
                        </button>
                        <button
                          type="button"
                          onClick={() => handleDelete(d)}
                          disabled={deleteMutation.isPending}
                          className="p-2 text-slate-500 hover:text-red-600 hover:bg-red-50 rounded-lg"
                          title="삭제"
                        >
                          <Trash2 className="w-4 h-4" />
                        </button>
                      </div>
                    </>
                  )}
                </li>
              ))}
            </ul>
          </div>
        )}
      </div>
    </Layout>
  );
}
