import { api } from '../api.js'

export function renderDashboard(app) {
  app.innerHTML = `
    <div class="min-h-screen flex">
      <aside class="w-64 bg-slate-800 border-r border-slate-700 p-4">
        <h1 class="text-lg font-bold text-royal-500 mb-6">🌳 Royaltree Admin</h1>
        <nav class="space-y-1">
          <button data-screen="dashboard" class="nav-item w-full text-left px-3 py-2 rounded-lg bg-royal-600 text-white">📊 Dashboard</button>
          <button data-screen="users" class="nav-item w-full text-left px-3 py-2 rounded-lg hover:bg-slate-700">👥 Users</button>
          <button data-screen="withdrawals" class="nav-item w-full text-left px-3 py-2 rounded-lg hover:bg-slate-700">💸 Withdrawals</button>
          <button data-screen="notifications" class="nav-item w-full text-left px-3 py-2 rounded-lg hover:bg-slate-700">📢 Notifications</button>
          <button data-screen="missions" class="nav-item w-full text-left px-3 py-2 rounded-lg hover:bg-slate-700">🎯 Missions</button>
          <button data-screen="campaigns" class="nav-item w-full text-left px-3 py-2 rounded-lg hover:bg-slate-700">📈 Campaigns</button>
          <button data-screen="audit" class="nav-item w-full text-left px-3 py-2 rounded-lg hover:bg-slate-700">📋 Audit Logs</button>
        </nav>
        <button id="logoutBtn" class="mt-6 w-full text-left px-3 py-2 rounded-lg hover:bg-red-700 text-red-400">🚪 Logout</button>
      </aside>
      <main class="flex-1 p-6" id="content">
        <div class="text-center py-12"><div class="text-slate-400">Loading...</div></div>
      </main>
    </div>
  `
  
  document.getElementById('logoutBtn').addEventListener('click', () => {
    localStorage.removeItem('admin_token')
    window.location.reload()
  })
  
  document.querySelectorAll('.nav-item').forEach(btn => {
    btn.addEventListener('click', () => loadScreen(btn.dataset.screen))
  })
  
  loadScreen('dashboard')
}

async function loadScreen(screen) {
  const content = document.getElementById('content')
  content.innerHTML = `<div class="text-slate-400">Loading ${screen}...</div>`
  
  document.querySelectorAll('.nav-item').forEach(b => b.classList.remove('bg-royal-600', 'text-white'))
  document.querySelector(`[data-screen="${screen}"]`)?.classList.add('bg-royal-600', 'text-white')
  
  try {
    if (screen === 'dashboard') {
      const stats = await api.stats()
      const s = stats.data || stats
      content.innerHTML = `
        <h2 class="text-2xl font-bold mb-6">📊 Dashboard</h2>
        <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
          <div class="card"><div class="text-slate-400 text-sm">Total Users</div><div class="text-2xl font-bold text-royal-500">${(s.totalUsers || 0).toLocaleString()}</div></div>
          <div class="card"><div class="text-slate-400 text-sm">Total RTP</div><div class="text-2xl font-bold text-purple-500">${(s.totalRTP || 0).toLocaleString()}</div></div>
          <div class="card"><div class="text-slate-400 text-sm">Pending Withdrawals</div><div class="text-2xl font-bold text-yellow-500">${s.pendingWithdrawals || 0}</div></div>
        </div>
        <div class="grid grid-cols-1 md:grid-cols-3 gap-4 mt-4">
          <div class="card"><div class="text-slate-400 text-sm">Total Miners</div><div class="text-2xl font-bold text-green-500">${(s.totalMiners || 0).toLocaleString()}</div></div>
          <div class="card"><div class="text-slate-400 text-sm">Active Miners</div><div class="text-2xl font-bold text-emerald-500">${s.activeMiners || 0}</div></div>
          <div class="card"><div class="text-slate-400 text-sm">Total Transactions</div><div class="text-2xl font-bold text-blue-500">${(s.totalTransactions || 0).toLocaleString()}</div></div>
        </div>
      `
    } else if (screen === 'users') {
      const users = await api.users()
      content.innerHTML = `
        <h2 class="text-2xl font-bold mb-6">👥 Users</h2>
        <div class="card overflow-x-auto">
          <table class="w-full">
            <thead><tr class="border-b border-slate-700 text-left">
              <th class="py-2 px-2">Name</th><th class="py-2 px-2">Email</th><th class="py-2 px-2">Tier</th><th class="py-2 px-2">Balance</th><th class="py-2 px-2">Action</th>
            </tr></thead>
            <tbody>
              ${(users.data || []).map(u => `
                <tr class="border-b border-slate-700/50">
                  <td class="py-2 px-2">${u.name || '-'}</td>
                  <td class="py-2 px-2 text-sm text-slate-400">${u.email || '-'}</td>
                  <td class="py-2 px-2"><span class="px-2 py-1 rounded bg-royal-600 text-xs">${u.tier || 'FREE'}</span></td>
                  <td class="py-2 px-2">${u.points || 0} RTP</td>
                  <td class="py-2 px-2"><button class="text-red-400 text-xs" onclick="banUser('${u.id}')">Ban</button></td>
                </tr>
              `).join('')}
            </tbody>
          </table>
        </div>
      `
    } else {
      content.innerHTML = `<div class="text-slate-400">${screen} — coming soon</div>`
    }
  } catch (err) {
    content.innerHTML = `<div class="text-red-400">Error: ${err.message}</div>`
  }
}

window.banUser = async (uid) => {
  if (!confirm('Yakin ban user ini?')) return
  try {
    await api.banUser(uid, 'Admin ban')
    alert('User banned')
    loadScreen('users')
  } catch (err) {
    alert('Error: ' + err.message)
  }
}
