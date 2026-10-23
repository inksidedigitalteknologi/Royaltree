import { renderLogin } from './screens/Login.js'

const app = document.getElementById('app')
const token = localStorage.getItem('admin_token')

if (token) {
  import('./screens/Dashboard.js').then(m => m.renderDashboard(app))
} else {
  renderLogin(app)
}
