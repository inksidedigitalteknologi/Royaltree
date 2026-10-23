import { api } from '../api.js'

export function renderLogin(app) {
  app.innerHTML = `
    <div class="min-h-screen flex items-center justify-center p-4">
      <div class="card w-full max-w-md">
        <div class="text-center mb-6">
          <h1 class="text-2xl font-bold text-royal-500">🌳 Royaltree Admin</h1>
          <p class="text-slate-400 text-sm mt-1">Login untuk akses admin panel</p>
        </div>
        <form id="loginForm" class="space-y-4">
          <div>
            <label class="block text-sm font-medium mb-1">Password</label>
            <input type="password" id="password" class="input" placeholder="Password admin" required />
          </div>
          <div>
            <label class="block text-sm font-medium mb-1">API Key</label>
            <input type="password" id="apiKey" class="input" placeholder="API key" required />
          </div>
          <button type="submit" class="btn btn-primary w-full">Login</button>
        </form>
        <div id="error" class="hidden mt-4 text-red-400 text-sm text-center"></div>
      </div>
    </div>
  `
  
  document.getElementById('loginForm').addEventListener('submit', async (e) => {
    e.preventDefault()
    const password = document.getElementById('password').value
    const apiKey = document.getElementById('apiKey').value
    const errorEl = document.getElementById('error')
    
    try {
      errorEl.classList.add('hidden')
      const result = await api.login(password, apiKey)
      localStorage.setItem('admin_token', result.token)
      window.location.reload()
    } catch (err) {
      errorEl.textContent = err.message
      errorEl.classList.remove('hidden')
    }
  })
}
