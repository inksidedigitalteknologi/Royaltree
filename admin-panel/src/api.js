const API_URL = import.meta.env.VITE_API_URL || 'http://45.41.204.21:3000/api/v1'

export async function apiRequest(path, options = {}) {
  const token = localStorage.getItem('admin_token')
  const headers = {
    'Content-Type': 'application/json',
    ...(token ? { 'Authorization': `Bearer ${token}` } : {}),
    ...options.headers
  }

  const response = await fetch(`${API_URL}${path}`, { ...options, headers })

  if (response.status === 401) {
    localStorage.removeItem('admin_token')
    window.location.reload()
    throw new Error('Session expired')
  }

  const data = await response.json()
  if (!response.ok) throw new Error(data.message || 'Request failed')
  return data
}

export const api = {
  login: (password, apiKey) => apiRequest('/admin/login', {
    method: 'POST',
    body: JSON.stringify({ password, apiKey })
  }),
  stats: () => apiRequest('/admin/stats'),
  users: (page = 1, limit = 20, search = '') =>
    apiRequest(`/admin/users?page=${page}&limit=${limit}&search=${encodeURIComponent(search)}`),
  userDetail: (uid) => apiRequest(`/admin/users/${uid}`),
  banUser: (uid, reason) => apiRequest(`/admin/users/${uid}/ban`, {
    method: 'POST',
    body: JSON.stringify({ reason })
  }),
  unbanUser: (uid) => apiRequest(`/admin/users/${uid}/unban`, { method: 'POST' }),
  withdrawals: () => apiRequest('/admin/withdrawals/all'),
  approveWithdrawal: (id) => apiRequest(`/admin/withdrawals/${id}/approve`, { method: 'POST' }),
  rejectWithdrawal: (id, reason) => apiRequest(`/admin/withdrawals/${id}/reject`, {
    method: 'POST',
    body: JSON.stringify({ reason })
  }),
  broadcast: (title, body) => apiRequest('/admin/notifications/broadcast', {
    method: 'POST',
    body: JSON.stringify({ title, body })
  }),
  missions: () => apiRequest('/admin/missions'),
  campaigns: () => apiRequest('/admin/campaigns'),
  auditLogs: () => apiRequest('/admin/audit-logs')
}
