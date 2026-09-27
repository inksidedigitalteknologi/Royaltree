# 🏗️ Royaltree Architecture

Dokumen ini menjelaskan arsitektur sistem Royaltree — aplikasi Android + backend Node.js + Firestore.

---

## 📊 Diagram Arsitektur Utama

```mermaid
flowchart TB
    subgraph USER["User Layer"]
        U1[App User]
    end

    subgraph ANDROID["Android App"]
        UI["UI Layer<br/>HomeScreen - MissionsScreen - ProfileScreen<br/>WithdrawalScreen - ReferralScreen<br/>SettingsScreen - FaqScreen - InboxScreen"]
        VM["ViewModel<br/>AffiliateViewModel - AuthViewModel"]
        REPO["Repository<br/>AffiliateRepository"]
        DATA["Data Layer<br/>Ads - Network - Room - Preferences"]
    end

    subgraph BACKEND["Backend Node.js"]
        SRV["server.js"]
        ROUTES["Routes<br/>auth.js - ads.js - daily.js - admin.js"]
        MW["Middleware<br/>firebaseAuth.js - antiFraud.js"]
    end

    subgraph FIREBASE["Firebase"]
        FA["Firebase Auth"]
        FS["Cloud Firestore"]
    end

    ADMOB["AdMob<br/>Rewarded + Banner"]

    U1 --> UI
    UI --> VM
    VM --> REPO
    REPO --> DATA
    DATA --> ADMOB
    DATA --> SRV
    SRV --> ROUTES
    SRV --> MW
    ROUTES --> FS
    MW --> FA

---

## Layer-by-Layer Breakdown

### 1. UI Layer

| Kategori | File | Status |
|---|---|---|
| Core | HomeScreen, MissionsScreen, ProfileScreen | Active |
| Rewards | DailyCheckInScreen, ReferralScreen | Active |
| Finance | WithdrawalScreen, HistoryScreen | Active |
| System | SettingsScreen, FaqScreen, InboxScreen, NotificationsScreen | Active |
| Admin | AdminScreen | Active |
| Auth | auth/ (Login, Register, Splash, Onboarding) | Active |
| Disabled | AnalyticsScreen, CampaignsScreen, GameRoomScreen, StepCounterScreen, AppOffersScreen, InvestmentCouponScreen, ProfileSecurityScreen | Navigasi disembunyikan |

### 2. ViewModel Layer

- **AffiliateViewModel.kt** — ViewModel utama
  - StateFlow: user, missions, withdrawals, notifications, language
  - Fungsi: loadUserFromBackend(), onAdRewardEarned(), performDailyCheckIn()
- **AuthViewModel.kt** — Firebase Auth flow

### 3. Repository Layer

- **AffiliateRepository.kt** — single repository
  - User CRUD (updateUserFromBackend, getUserSync)
  - Withdrawal (requestWithdrawal, updateWithdrawalStatusByAdmin)
  - Missions, Daily Check-In, Step conversion

### 4. Data Layer

| Sub-layer | File | Fungsi |
|---|---|---|
| Ads | AdConfig.kt, AdManager.kt, AdMobProvider.kt | Load and show Rewarded + Banner |
| Network | ApiClient.kt, RoyaltreeApiService.kt, AuthInterceptor.kt | Retrofit API client |
| Room DB | AppDatabase.kt, AppDao.kt, Entities.kt | Local cache |
| Localization | LanguageManager.kt, AppLanguage.kt | 6 bahasa |
| Security | security/ | Enkripsi, 2FA |
| Sensor | sensor/ | Step counter |
| Preferences | AppThemePreferences.kt | SharedPreferences |

### 5. Backend

- **server.js** — Express server, port 3000
- **Routes**:
  - auth.js — /sync, /me, /link-referral
  - ads.js — /config, /status, /reward, /admob-ssv
  - daily.js — /config, /status, /check-in, /recover-day
  - admin.js — /daily-config, /ad-config, /audit-logs
- **Middleware**:
  - firebaseAuth.js — verify Firebase ID token
  - antiFraud.js — deteksi abuse

### 6. External Services

| Service | Fungsi |
|---|---|
| Firebase Auth | Autentikasi user (UID) |
| Cloud Firestore | Database utama |
| AdMob | Rewarded Video + Adaptive Banner |

---

## Struktur Folder
Royaltree/
app/ Android app
src/main/java/com/inkside/digital/
MainActivity.kt Entry point, NavHost
data/
ads/ AdMob integration
network/ Retrofit API
repository/ AffiliateRepository
database/ Room
model/ Entities
preferences/ SharedPreferences
localization/ LanguageManager
security/ Encryption, 2FA
sensor/ Step counter
ui/
screens/ Compose screens
components/ Reusable UI
util/ Utilities
viewmodel/ ViewModels
backend/ Node.js backend
server.js Express server
routes/ API endpoints
auth.js
ads.js
daily.js
admin.js
middleware/ Auth and anti-fraud
config/ Firebase config
admin/ Admin panel (static)
---

## Tech Stack

| Layer | Teknologi |
|---|---|
| UI | Jetpack Compose, Material 3, Navigation Compose |
| State | StateFlow, CollectAsState |
| ViewModel | AndroidX Lifecycle ViewModel |
| Repository | Manual DI |
| Local DB | Room (version 6, royaltree.db) |
| Network | Retrofit + OkHttp + Gson |
| Auth | Firebase Auth |
| Database | Cloud Firestore |
| Ads | AdMob (Rewarded + Adaptive Banner) |
| Backend | Node.js + Express + Firebase Admin SDK |
| i18n | Custom LanguageManager (6 bahasa) |

---

## Catatan Penting

### Screen yang Disabled

7 screen dinonaktifkan dari navigasi karena backend belum support:

- AnalyticsScreen — tidak ada endpoint analytics
- CampaignsScreen — tidak ada endpoint campaigns
- GameRoomScreen — tidak ada endpoint game
- StepCounterScreen — tidak ada endpoint step counter
- AppOffersScreen — tidak ada endpoint app offers
- InvestmentCouponScreen — tidak ada endpoint investasi
- ProfileSecurityScreen — tidak ada endpoint security

File screen tetap ada di codebase, hanya tombol navigasinya yang disembunyikan.

### Field UserEntity - Dual Source

UserEntity punya 25 field dengan 2 sumber:

- 11 field core — sync dari Firestore (id, name, email, phone, tier, balance, points, referralCode, checkInStreak, lastCheckInDate, todaySteps)
- 14 field local-only — hanya di Room (pendingBalance, totalPaidOut, dailyStepGoal, unclaimedSteps, dll.)

### Room Database Version

- Saat ini version = 6
- Pakai fallbackToDestructiveMigration() — data lokal di-drop saat versi naik
- Data sync ulang dari Firestore via loadUserFromBackend()

---

## Deployment

| Komponen | Environment |
|---|---|
| Android app | Google Play Store (belum dipublikasikan) |
| Backend | VPS (Node.js + PM2) |
| Database | Firebase Cloud Firestore |
| Ads | AdMob (test ID saat development) |

---

Last updated: 2026-09-27

---


---

## Alur Data Utama

### A. Login and Sync User

```

```mermaid
sequenceDiagram
    participant U as User
    participant UI as UI
    participant VM as AffiliateViewModel
    participant FA as Firebase Auth
    participant API as ApiClient
    participant SRV as Backend
    participant FS as Firestore

    U->>UI: Buka app dan login
    UI->>FA: Firebase Auth
    FA-->>UI: UID
    UI->>VM: syncFirebaseUser
    VM->>API: POST /auth/sync
    API->>SRV: HTTP + Bearer token
    SRV->>FS: Create/update user
    FS->>SRV: OK
    SRV->>API: user data JSON
    API->>VM: JSON
    VM-0>>UI: StateFlow update
```

### B. Watch Rewarded Ad

```mermaid
sequenceDiagram
    participant U as User
    participant H as HomeScreen
    participant M as MainActivity
    participant A as AdManager
    participant P as AdMobProvider
    participant D as AdMob SDK
    participant A as ApiClient
    participant S as Backend
    participant F as Firestore

    U-0>>H: Tap card Tonton Iclan
    H->>M: onOpenAdReward
    M->>A: showRewardedAd
    A->>P: loadRewardedAd
    P->>D: RewardedAd.load
    D-->>P: Ad loaded
    P->>D: ad.show
    D->>U:Full-screen ad
    U->>D: Nonton selesai
    D-->>A: onUserEarnedReward
    A->>A: POST /ads/reward
    A->>S: HTTP + txId
    S->>F: increment points
    F->>S: OK
    S->>A: success + points
    A->>M: onSuccess
    M->>V: onAdRewardEarned
    V->>A: Snackbar
```

### C. Daily Check-IT

```mermaid
sequenceDiagram
    participant U as User
    participant I as DailyCheckInScreen
    participant V as AffiliateViewModel
    participant R as AffiliateRepository
    participant A as ApiClient
    participant S as Backend
    participant F as Firestore

    U-0>>I: Tap Klaim Hadiah
    I->>V: performDailyCheckIn
    V->>R: performDailyCheckIn
    R->>A: POST /daily/check-in
    A->>S: HTTP
    S->>F: increment points + streak
    S->>V: reward + streak
    V->>U: Snackbar +X RTP
```
