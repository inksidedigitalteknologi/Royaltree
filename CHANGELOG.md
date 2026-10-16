# Changelog

Semua perubahan penting proyek Royaltree akan didokumentasikan di file ini.

Format: [Keep a Changelog](https://keepachangelog.com/id/1.0.0/)
Versioning: [Semantic Versioning](https://semver.org/lang/id/)

---

## [1.12.0] - 2026-10-16

### ✨ Added — i18n Migration (Enterprise)
- **LanguageLoader.kt** — load translations dari JSON assets (parse + cache + fallback chain)
- **assets/i18n/** — 6 file JSON (id, en, es, zh, ar, ja) — total **708 key × 6 bahasa = 4,248 entries**
- **I18N_GUIDE.txt** — panduan i18n untuk developer (format key, cara pakai, best practice)
- **LanguageManager.init()** — dipanggil di `MainActivity.onCreate()` untuk inisialisasi context

### 🔧 Changed — Architecture
- **LanguageManager.kt** — **5,785 baris → 42 baris** (data translation dipindah ke JSON)
- API `LanguageManager.translate()` **tidak berubah** — ~700 call site tidak perlu di-refactor
- LanguageLoader support **fallback chain**: target language → ID → param → key

### 🐛 Fixed — Critical Bugs
- **Konten blank hitam** — root cause: `bottomNavHeightState.value` terlalu besar (full screen), fix: hapus bottom padding di MainActivity
- **JVM MethodTooLargeException** — LanguageManager `<clinit>` > 64KB, fix: migrate ke JSON asset
- **~120 build error** `Unresolved reference 'currentLanguage'` — fix: global state `AppThemePreferences.currentLanguage` untuk composable helper
- **~150 text hardcoded** — extract ke LanguageManager (screens + components + ViewModel)
- **Syntax error** komentar `*.json` di LanguageLoader (bikin unclosed comment)
- **`$` escape** — string template `$fileName`/`$url`/`$message` di JSON
- **Duplikat key** — 4 key (`common_cancel`, `common_close`, `faq_subtitle`, `faq_contact_support`) dibersihkan → 708 key unik

### 🎨 Improved — UI
- **Efek daun jatuh terlihat** di HomeScreen, GlobalChatScreen, GameRoomScreen
  - Background solid → semi-transparan (`alpha = 0.7f`)
- **Debug log dirapikan** — `Log.d` → `Log.e` untuk HP vivo yang filter debug level

### 🧹 Technical
- **Auth screens i18n** — ProfileSetupScreen, ForgotPasswordScreen (tambah param `currentLanguage`)
- **ViewModel i18n** — AffiliateViewModel: 112 translate usage (dari ~40 hardcoded)
- **Content description i18n** — 52 `contentDescription` di 15+ file
- **Toast i18n** — 11 Toast message (Referral, Campaigns, Admin)
- **Label + placeholder i18n** — 7 form label

---

## [1.11.0] - 2026-10-14

### ✨ Added — Thermal & Power Management
- **Battery temperature monitoring**: `BatteryTempHelper.kt` (read suhu, level, charging)
- **Location region detection**: `LocationHelper.kt` (FusedLocationProviderClient)
- **ThermalStatusCard**: card UI di Game Room dengan emoji + warna + hashrate progress
- **Power-aware hashrate**: multi-factor (temp × battery × charging)
  - Charging boost 1.2× (configurable) saat suhu normal
  - Battery level factor (0.3× kalau < 15%)
  - Temperature factor (pause @ 60°C)
- **Auto-pause hysteresis**: pause @ 60°C, resume @ 55°C
- **Battery critical pause**: auto-pause saat battery < 5% (configurable)
- **Notif thermal**: in-app + FCM
  - ⚠️ HP Panas (> 45°C)
  - 🚨 HP Sangat Panas (> 50°C)
  - ⛔ Mining Dijeda (> 60°C)
  - 🔋 Baterai Kritis (< 5%)
  - ✅ Mining Dilanjutkan (resume)
- **Admin config thermal** (`config.html` tab Game):
  - `thermalPauseTemp` (60)
  - `thermalResumeTemp` (55)
  - `thermalAlertTemp` (45)
  - `thermalCriticalTemp` (50)
  - `chargingBoost` (1.2)
  - `minBatteryForMining` (5)

### ✨ Added — Admin Panel Refactor
- **12 halaman admin** (Bootstrap 5 modular, dari monolith 2562 baris):
  - `login.html` — auth dengan password + API key
  - `index.html` — dashboard (7 stat cards + 2 panel)
  - `users.html` — list + modal detail + ban/unban
  - `withdrawals.html` — approve/reject + 4 status filter
  - `anomalies.html` — fraud detection + flagged users
  - `chat.html` — monitor chat + moderasi + auto-refresh
  - `mining.html` — pool fund health + blocks history
  - `broadcast.html` — notif massal + live preview
  - `config.html` — daily/ads/game/thermal config
  - `missions.html` — read-only list
  - `campaigns.html` — read-only list
  - `audit-logs.html` — log aktivitas
- **Shared assets**:
  - `api.js` (~270 baris, 50+ endpoints)
  - `layout.js` (sidebar + navbar reusable)
  - `admin.css` (138 baris)

### ✨ Added — Config Configurable via Admin
- **`maxAdsPerDay`**: dari hardcoded 10 → configurable (default 20)
- **6 thermal fields**: pause/resume/alert/critical/boost/battery

### ✨ Added — Bottom Nav Refactor
- **5 items** (dari 6, hapus `GAME_MINING`)
- Icon unik: Game → 🎮 (SportsEsports), Misi → 🚩 (Flag)
- `navigationBarsPadding()` untuk gesture bar
- Floating RTP coin di tengah (collapsed state)

### ✨ Added — Anomaly Detection
- **Backend service** (`services/anomaly.js`):
  - Threshold: RTP/hour, blocks/hour, shares/min, claims/day, ads/day, IPs/day, accounts/device
  - Score-based flagging (FLAG_THRESHOLD=50, BAN_THRESHOLD=100)
- **Admin endpoints**:
  - `GET /admin/anomalies`
  - `GET /admin/anomalies/stats`
  - `GET /admin/anomalies/flagged-users`
  - `POST /admin/anomalies/:id/status`

### ⚡ Changed — Performance
- **`/thermal` endpoint**: read `game_states` ONCE (dari 2× get) → 50% fewer Firestore reads
- **Chat auto-refresh**: stop kalau tab hidden (visibilitychange) → ~50% fewer requests
- **Proguard enabled**: `isMinifyEnabled=true` + `isShrinkResources=true` → APK ~30% smaller
- **Log spam removed**: `DEBUG_ADMIN=0` di `.env` → clean production log

### 🐛 Fixed
- `admin.html` redirect ke `login.html` / `index.html` (legacy monolith → modular)
- Anti-flash auth check di semua halaman
- Relative static path (`path.join`) di `server.js`
- Duplicate Firestore read di `/thermal`

### 🔧 Technical
- **Android**: minSdk 24, targetSdk 36, compileSdk 36
- **Backend**: Express 5.2.1, Firebase Admin 13.10.0
- **Admin**: Bootstrap 5.3.3 (CDN)
- **Dependencies**: +play-services-location 21.3.0
- **Permissions**: 11 izin (INTERNET, LOCATION, CAMERA, FOREGROUND_SERVICE, dll)

---

## [1.10.0] - Sebelumnya

- Base release: step counter, daily check-in, mining tycoon, mini-game, withdrawal, affiliate, referral
- Firebase Auth + Firestore
- AdMob rewarded video
- FCM push notif
- Sweepstakes, transfer, global chat

---

### 📝 Legend
- ✨ Added — fitur baru
- ⚡ Changed — perubahan yang ada
- 🐛 Fixed — bug fix
- 🔧 Technical — internal/technical changes
- 🗑️ Removed — dihapus
- 🔒 Security — security-related
