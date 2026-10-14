/**
 * Royaltree Admin — Layout Injector
 * Render sidebar + navbar otomatis di semua halaman
 */
(function () {
  'use strict';

  const MENU = [
    { href: 'index.html',       icon: 'speedometer2',      label: 'Dashboard' },
    { href: 'users.html',       icon: 'people',            label: 'Users' },
    { href: 'withdrawals.html', icon: 'cash-stack',        label: 'Withdrawals' },
    { href: 'anomalies.html',   icon: 'shield-exclamation',label: 'Anomalies', badge: 'NEW' },
    { href: 'mining.html',      icon: 'pickaxe',           label: 'Mining' },
    { href: 'missions.html',    icon: 'flag',              label: 'Missions' },
    { href: 'campaigns.html',   icon: 'megaphone',         label: 'Campaigns' },
    { href: 'chat.html',        icon: 'chat-dots',         label: 'Chat' },
    { href: 'broadcast.html',   icon: 'send',              label: 'Broadcast' },
    { href: 'audit-logs.html',  icon: 'journal-text',      label: 'Audit Logs' },
    { href: 'config.html',      icon: 'gear',              label: 'Config' }
  ];

  function renderSidebar() {
    const el = document.getElementById('admin-sidebar');
    if (!el) return;

    const current = (window.location.pathname.split('/').pop() || 'index.html');
    const adminName = (window.AdminAPI && AdminAPI.Auth.getName()) || 'Admin';

    el.innerHTML = `
      <div class="d-flex flex-column flex-shrink-0 p-3 text-white bg-dark sidebar-inner">
        <a href="index.html" class="d-flex align-items-center mb-3 mb-md-0 me-md-auto text-white text-decoration-none">
          <i class="bi bi-tree-fill fs-4 me-2"></i>
          <span class="fs-5 fw-bold">Royaltree</span>
          <span class="badge bg-success ms-2">Admin</span>
        </a>
        <hr>
        <ul class="nav nav-pills flex-column mb-auto">
          ${MENU.map(m => `
            <li class="nav-item">
              <a href="${m.href}" class="nav-link text-white ${m.href === current ? 'active' : ''}">
                <i class="bi bi-${m.icon} me-2"></i>${m.label}
                ${m.badge ? `<span class="badge bg-danger ms-2">${m.badge}</span>` : ''}
              </a>
            </li>
          `).join('')}
        </ul>
        <hr>
        <div class="dropdown">
          <a href="#" class="d-flex align-items-center text-white text-decoration-none dropdown-toggle" data-bs-toggle="dropdown">
            <i class="bi bi-person-circle fs-4 me-2"></i>
            <strong>${window.AdminAPI ? AdminAPI.escapeHtml(adminName) : 'Admin'}</strong>
          </a>
          <ul class="dropdown-menu dropdown-menu-dark text-small shadow">
            <li><a class="dropdown-item" href="#" onclick="AdminAPI.Auth.clear();location.href='login.html';return false;">
              <i class="bi bi-box-arrow-right me-2"></i>Logout
            </a></li>
          </ul>
        </div>
      </div>`;
  }

  function renderNavbar(pageTitle) {
    const el = document.getElementById('admin-navbar');
    if (!el) return;

    el.innerHTML = `
      <nav class="navbar navbar-expand-lg navbar-light bg-white border-bottom sticky-top">
        <div class="container-fluid">
          <button class="btn btn-sm btn-outline-secondary d-lg-none me-2" type="button"
                  data-bs-toggle="offcanvas" data-bs-target="#sidebarOffcanvas">
            <i class="bi bi-list"></i>
          </button>
          <span class="navbar-brand mb-0 h5">${window.AdminAPI ? AdminAPI.escapeHtml(pageTitle || 'Admin') : (pageTitle || 'Admin')}</span>
          <div class="ms-auto d-flex align-items-center gap-2">
            <span class="text-muted small d-none d-md-inline">
              <i class="bi bi-clock me-1"></i>${new Date().toLocaleDateString('id-ID')}
            </span>
          </div>
        </div>
      </nav>`;
  }

  window.AdminLayout = {
    init(pageTitle) {
      if (!AdminAPI.Auth.requireLogin()) return;
      renderSidebar();
      renderNavbar(pageTitle);
    }
  };
})();
