/**
 * Royaltree Admin — API Client
 * Wrapper fetch dengan auth token + error handling
 */

(function () {
  'use strict';

  const API_BASE = '/api/v1/admin';
  const TOKEN_KEY = 'royaltree_admin_token';
  const ADMIN_NAME_KEY = 'royaltree_admin_name';

  // ============ TOKEN MANAGEMENT ============
  const Auth = {
    getToken() {
      return localStorage.getItem(TOKEN_KEY) || '';
    },
    setToken(token) {
      localStorage.setItem(TOKEN_KEY, token);
    },
    getName() {
      return localStorage.getItem(ADMIN_NAME_KEY) || 'Admin';
    },
    setName(name) {
      localStorage.setItem(ADMIN_NAME_KEY, name || 'Admin');
    },
    clear() {
      localStorage.removeItem(TOKEN_KEY);
      localStorage.removeItem(ADMIN_NAME_KEY);
    },
    isLoggedIn() {
      return !!this.getToken();
    },
    requireLogin() {
      if (!this.isLoggedIn()) {
        window.location.href = 'login.html';
        return false;
      }
      return true;
    }
  };

  // ============ FETCH WRAPPER ============
  async function request(path, options = {}) {
    const url = path.startsWith('http') ? path : API_BASE + path;
    const headers = {
      'Content-Type': 'application/json',
      'x-admin-token': Auth.getToken(),
      ...(options.headers || {})
    };

    try {
      const resp = await fetch(url, {
        ...options,
        headers,
        body: options.body ? JSON.stringify(options.body) : undefined
      });

      const text = await resp.text();
      let data;
      try {
        data = text ? JSON.parse(text) : {};
      } catch (e) {
        data = { success: false, message: text || 'Invalid JSON response' };
      }

      // Auto-logout kalau 401 ATAU 403 (unauthorized / forbidden)
      if (resp.status === 401 || resp.status === 403) {
        Auth.clear();
        // Clear semua related storage
        try {
          localStorage.removeItem('royaltree_admin_apikey');
        } catch (e) {}

        // Redirect ke login dengan flag expired
        if (!window.location.pathname.endsWith('login.html')) {
          window.location.replace('login.html?expired=1');
        }
        return { success: false, message: 'Session expired', httpCode: resp.status };
      }

      if (!resp.ok) {
        return {
          success: false,
          httpCode: resp.status,
          message: data.message || data.error || `HTTP ${resp.status}`,
          ...data
        };
      }

      return data;
    } catch (err) {
      return {
        success: false,
        message: err.message || 'Network error',
        error: err
      };
    }
  }

  // ============ API ENDPOINTS ============
  const API = {
    // Auth
    login: (password, apiKey) =>
      request('/login', { method: 'POST', body: { password, apiKey } }),

    // Stats
    getStats: () => request('/stats'),

    // Users
    getUsers: (params = {}) => {
      const q = new URLSearchParams(params).toString();
      return request('/users' + (q ? '?' + q : ''));
    },
    getUser: (uid) => request('/users/' + uid),
    banUser: (uid, reason = '') =>
      request(`/users/${uid}/ban`, { method: 'POST', body: { reason } }),
    unbanUser: (uid) =>
      request(`/users/${uid}/unban`, { method: 'POST' }),
    clearFlag: (uid) =>
      request(`/users/${uid}/clear-flag`, { method: 'POST' }),

    // Withdrawals
    getWithdrawals: (status = 'PENDING') =>
      request('/withdrawals?status=' + status),
    getAllWithdrawals: () => request('/withdrawals/all'),
    approveWithdrawal: (id, note = '') =>
      request(`/withdrawals/${id}/approve`, { method: 'POST', body: { note } }),
    rejectWithdrawal: (id, note = '') =>
      request(`/withdrawals/${id}/reject`, { method: 'POST', body: { note } }),

    // Anomalies
    getAnomalies: (params = {}) => {
      const q = new URLSearchParams(params).toString();
      return request('/anomalies' + (q ? '?' + q : ''));
    },
    getAnomalyStats: () => request('/anomalies/stats'),
    getFlaggedUsers: () => request('/anomalies/flagged-users'),
    getUserDetail: (uid) => request(`/anomalies/user/${uid}`),
    getThermalStats: (days = 7) => request(`/thermal-stats?days=${days}`),
    getFirestoreUsage: (days = 7) => request(`/firestore-usage?days=${days}`),
    getAnalytics: (days = 30) => request(`/analytics?days=${days}`),

    // Scheduled notifications
    scheduleNotification: (body) =>
      request('/notifications/schedule', { method: 'POST', body }),
    getScheduledNotifications: () => request('/notifications/scheduled'),
    cancelScheduledNotification: (id) =>
      request(`/notifications/scheduled/${id}`, { method: 'DELETE' }),
    updateAnomalyStatus: (id, status) =>
      request(`/anomalies/${id}/status`, { method: 'POST', body: { status } }),

    // Mining
    getPoolFund: () => request('/mining/pool-fund'),
    getBlocks: () => request('/mining/blocks'),

    // Chat
    getChatMessages: () => request('/chat/messages'),
    // Config (daily / ads / game) — GET + PUT
    getConfig: (type) => {
      const map = { daily: '/daily-config', ads: '/ad-config', game: '/game-config' };
      return request(map[type] || '/daily-config');
    },
    saveConfig: (type, body) => {
      const map = { daily: '/daily-config', ads: '/ad-config', game: '/game-config' };
      return request(map[type] || '/daily-config', { method: 'PUT', body });
    },
    del: (path) => request(path, { method: 'DELETE' }),

    // Missions / Campaigns
    getMissions: () => request('/missions'),
    getCampaigns: () => request('/campaigns'),

    // Config
    getDailyConfig: () => request('/daily-config'),
    getAdConfig: () => request('/ad-config'),
    getGameConfig: () => request('/game-config'),
    getAuditLogs: () => request('/audit-logs'),

    // Notifications
    broadcastNotification: (title, message, type = 'BROADCAST', targetUid) => {
      const body = { title, message, type };
      if (targetUid) body.targetUid = targetUid;
      return request('/notifications/broadcast', { method: 'POST', body });
    },

    // Maintenance
    toggleMaintenance: (enabled, message = '') =>
      request('/system/maintenance', { method: 'POST', body: { enabled, message } })
  };

  // ============ HELPERS ============
  window.AdminAPI = {
    Auth,
    ...API,
    // Helper untuk format
    fmtDate(ts) {
      if (!ts) return '-';

      let d;
      try {
        // 1. Firestore Timestamp (client SDK style): { _seconds, _nanoseconds }
        if (typeof ts === 'object' && ts._seconds !== undefined) {
          d = new Date(ts._seconds * 1000);
        }
        // 2. Firestore Timestamp (admin SDK style): { seconds, nanoseconds }
        else if (typeof ts === 'object' && ts.seconds !== undefined) {
          d = new Date(ts.seconds * 1000);
        }
        // 3. Firestore Timestamp class with toDate()
        else if (typeof ts === 'object' && typeof ts.toDate === 'function') {
          d = ts.toDate();
        }
        // 4. ISO string: "2026-10-09T17:41:29.221Z"
        else if (typeof ts === 'string') {
          d = new Date(ts);
        }
        // 5. Number (epoch ms)
        else if (typeof ts === 'number') {
          d = new Date(ts);
        }
        // 6. Unknown object
        else {
          return '-';
        }

        if (isNaN(d.getTime())) return '-';

        return d.toLocaleString('id-ID', {
          day: '2-digit',
          month: 'short',
          year: 'numeric',
          hour: '2-digit',
          minute: '2-digit'
        });
      } catch (e) {
        console.warn('[fmtDate] Error:', e, ts);
        return '-';
      }
    },
    fmtNumber(n) {
      return new Intl.NumberFormat('id-ID').format(n || 0);
    },
    escapeHtml(s) {
      if (s == null) return '';
      return String(s)
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&#39;');
    },
    toast(msg, type = 'info') {
      // Simple toast — pakai alert kalau bootstrap belum siap
      if (window.bootstrap && bootstrap.Toast) {
        let container = document.getElementById('toast-container');
        if (!container) {
          container = document.createElement('div');
          container.id = 'toast-container';
          container.className = 'toast-container position-fixed top-0 end-0 p-3';
          document.body.appendChild(container);
        }
        const bgClass = { success: 'bg-success', error: 'bg-danger', warning: 'bg-warning', info: 'bg-primary' }[type] || 'bg-primary';
        const el = document.createElement('div');
        el.className = `toast align-items-center text-white ${bgClass} border-0`;
        el.innerHTML = `
          <div class="d-flex">
            <div class="toast-body">${this.escapeHtml(msg)}</div>
            <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast"></button>
          </div>`;
        container.appendChild(el);
        const t = new bootstrap.Toast(el, { delay: 3500 });
        t.show();
        el.addEventListener('hidden.bs.toast', () => el.remove());
      } else {
        console.log(`[${type}]`, msg);
      }
    },

    // ============================================================
    // ACHIEVEMENT
    // ============================================================
    getAchievements: () => request('/achievements'),
    getAchievement: (id) => request('/achievements/' + id),
    createAchievement: (data) => request('/achievements', { method: 'POST', body: data }),
    updateAchievement: (id, data) => request('/achievements/' + id, { method: 'PUT', body: data }),
    deleteAchievement: (id) => request('/achievements/' + id, { method: 'DELETE' }),
    seedAchievements: () => request('/achievements/seed', { method: 'POST' }),
    getUserAchievements: (uid) => request('/user-achievements/' + uid),

  };

  console.log('[AdminAPI] loaded');
})();
