const API_BASE = import.meta.env.VITE_API_URL || 'http://localhost:8080/api';

function getToken() {
  return localStorage.getItem('token');
}

export async function request(path, options = {}) {
  const url = `${API_BASE}${path}`;
  const headers = {
    'Content-Type': 'application/json',
    ...options.headers,
  };
  const token = getToken();
  if (token) headers.Authorization = `Bearer ${token}`;

  const res = await fetch(url, { ...options, headers });
  const data = res.ok ? await res.json().catch(() => ({})) : null;
  if (!res.ok) {
    const err = new Error(data?.message || res.statusText || 'Request failed');
    err.status = res.status;
    err.data = data;
    throw err;
  }
  return data;
}

// Auth (백엔드: username=학번/사번, accessToken)
export const authApi = {
  login: (body) => request('/auth/login', { method: 'POST', body: JSON.stringify({ username: body.username || body.studentId, password: body.password }) }),
  signup: (body) => request('/auth/signup', { method: 'POST', body: JSON.stringify(body) }),
  me: () => request('/users/me'),
  changePassword: (body) => request('/users/me/password', { method: 'PUT', body: JSON.stringify(body) }).catch(() => { throw new Error('비밀번호 변경 API가 없습니다.'); }),
};

// Departments (공개/회원가입용)
export const departmentsApi = {
  list: () => request('/departments'),
};

// Admin - Departments
export const adminDepartmentsApi = {
  list: () => request('/admin/departments'),
  create: (body) => request('/admin/departments', { method: 'POST', body: JSON.stringify(body) }),
  update: (id, body) => request(`/admin/departments/${id}`, { method: 'PUT', body: JSON.stringify(body) }),
  delete: (id) => request(`/admin/departments/${id}`, { method: 'DELETE' }),
};

// Courses (학생: 목록/신청)
export const coursesApi = {
  list: async (params) => {
    const clean = Object.fromEntries(
      Object.entries(params || {}).filter(([, v]) => v != null && v !== '')
    );
    const q = new URLSearchParams(clean).toString();
    const res = await request(`/courses${q ? `?${q}` : ''}`);
    return Array.isArray(res) ? res : res?.content ?? res;
  },
  get: (id) => request(`/courses/${id}`),
  apply: (id) => request('/enrollments', { method: 'POST', body: JSON.stringify({ courseId: id }) }),
  cancel: (id) => request(`/enrollments/${id}`, { method: 'DELETE' }),
  myEnrollments: () => request('/enrollments/me'),
};

// Professor - Courses (백엔드 경로: /api/professors/courses)
export const professorCoursesApi = {
  myCourses: () => request('/professors/courses'),
  create: (body) => request('/professors/courses', { method: 'POST', body: JSON.stringify(body) }),
  update: (id, body) => request(`/professors/courses/${id}`, { method: 'PUT', body: JSON.stringify(body) }),
  get: (id) => request(`/professors/courses/${id}`),
  enrollments: (courseId) => request(`/professors/courses/${courseId}/students`),
};

// Admin - Users & Courses (백엔드: Page 응답 시 content 사용)
export const adminApi = {
  users: async (params) => {
    const p = { keyword: params?.q, name: params?.name, role: params?.role ? `ROLE_${String(params.role).toUpperCase()}` : undefined };
    const q = new URLSearchParams(Object.fromEntries(Object.entries(p).filter(([, v]) => v != null))).toString();
    const res = await request(`/admin/users${q ? `?${q}` : ''}`);
    return Array.isArray(res) ? res : { users: res?.content ?? [], totalElements: res?.totalElements ?? 0 };
  },
  deleteUser: (id) => request(`/admin/users/${id}`, { method: 'DELETE' }),
  courses: async () => {
    const res = await request('/admin/courses');
    return Array.isArray(res) ? res : res?.content ?? res;
  },
};

// Time slots for dropdowns (강의 시간 선택용)
export const timeSlotsApi = {
  list: () => request('/time-slots').catch(() => []),
};
