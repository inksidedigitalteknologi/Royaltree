🌳 Royaltree

Aplikasi affiliasi & rewards berbasis Android dengan backend Node.js + Cloud Firestore.

Pengguna mengumpulkan poin lewat check-in harian, misi, referral, dan menonton iklan, lalu menukarkan saldo ke e-wallet atau rekening bank.

📑 Daftar Isi
Dokumentasi
Fitur Utama
Status Fitur
Tech Stack
Struktur Proyek
Prasyarat
Setup Development
1. Firebase
2. Backend
3. Android
4. Admin Panel
Deployment
Keamanan
Troubleshooting
Kontribusi
Lisensi
Kontak
📚 Dokumentasi
Dokumen	Deskripsi
ARCHITECTURE.md	Arsitektur sistem, diagram Mermaid, alur data
backend/ENDPOINTS.md	Spesifikasi API endpoint backend
CHANGELOG.md	Riwayat perubahan
✨ Fitur Utama
Fitur	Deskripsi
💰 Rewards	Daily check-in, misi, dan referral
📢 AdMob Rewarded	Tonton iklan, dapat RTP
📊 Banner Ads	Adaptive banner di semua screen
💸 Withdrawal	Tarik saldo ke e-wallet / bank
🌍 Multi-language	6 bahasa: ID, EN, ES, ZH, AR, JA
🔥 Firebase	Auth + sinkronisasi realtime dengan Firestore
🚦 Status Fitur
Fitur	Status
Core (Home, Profile, Missions)	✅ Active
Firestore sync	✅ Active
AdMob Rewarded	✅ Active
AdMob Banner	✅ Active
Multi-language	✅ Active
Withdrawal	✅ Active
Analytics / Campaigns / Game Room	⚠️ Disabled
Step Counter (konversi langkah jadi poin)	⚠️ Disabled
App Offers / Invest / Profile Security	⚠️ Disabled

Catatan: Screen yang berstatus Disabled masih ada di codebase, tetapi tombol navigasinya disembunyikan karena backend belum mendukungnya. Detail lengkap ada di ARCHITECTURE.md.

🛠️ Tech Stack

Android

Kotlin + Jetpack Compose
Room Database (v6)
Retrofit + OkHttp
Firebase Auth + Firestore
AdMob (Rewarded + Banner)
Gradle (Kotlin DSL)

Backend

Node.js + Express
Firebase Admin SDK
PM2 (production)

Database

Cloud Firestore
📂 Struktur Proyek
text
Royaltree/
├── app/                      # Android app (Kotlin + Compose)
├── backend/                  # Node.js + Express API
│   └── ENDPOINTS.md          # Spesifikasi API
├── admin/                    # Admin panel (static)
├── gradle/                   # Gradle wrapper
├── .env.example              # Contoh environment variable
├── .firebaserc               # Alias project Firebase
├── firebase.json             # Konfigurasi Firebase
├── firestore.rules           # Security rules Firestore
├── firestore.indexes.json    # Index Firestore
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
├── ARCHITECTURE.md
├── CHANGELOG.md
└── README.md
📋 Prasyarat
Android Studio (versi terbaru yang stabil)
JDK sesuai kebutuhan Android Gradle Plugin yang dipakai proyek
Node.js dan npm untuk backend
Akun Firebase dan akses ke Firebase Console
(Opsional) PM2 untuk menjalankan backend di production
(Opsional) Firebase CLI untuk deploy rules dan index
🚀 Setup Development
1. Firebase
Buka Firebase Console dan buat project baru.
Tambahkan aplikasi Android dengan package name com.inkside.digital.
Unduh google-services.json dan letakkan di folder app/.
Aktifkan Authentication dengan provider Email/Password.
Aktifkan Cloud Firestore (production mode).
Siapkan kredensial service account untuk backend (lihat bagian Backend).
Deploy rules dan index Firestore:
bash
   firebase deploy --only firestore:rules,firestore:indexes
2. Backend
bash
cd backend
cp ../.env.example .env     # lalu isi nilai sesuai environment kamu
npm install
node server.js

Server berjalan di http://localhost:3000.

Untuk production, jalankan dengan PM2:

bash
pm2 start server.js --name royaltree-api
pm2 save

Perintah PM2 yang sering dipakai:

bash
pm2 status
pm2 logs royaltree-api
pm2 restart royaltree-api

Daftar variabel environment yang dibutuhkan ada di .env.example. Spesifikasi endpoint ada di backend/ENDPOINTS.md.

3. Android
Buka Android Studio.
Pilih File → Open, lalu pilih folder Royaltree.
Tunggu Gradle Sync selesai.
Pastikan file app/google-services.json sudah ada.
Pastikan aplikasi Android mengarah ke URL backend yang benar (lokal atau production).
Jalankan Build → Rebuild Project.
Klik Run (▶️) ke emulator atau perangkat fisik.

Build lewat command line:

bash
./gradlew assembleDebug

Saat development, gunakan ID unit iklan test dari AdMob agar akun kamu tidak terkena pelanggaran kebijakan akibat klik pada iklan asli.

4. Admin Panel

Panel admin berupa halaman statis di folder admin/. Hubungkan ke backend yang sedang berjalan sesuai konfigurasi di folder tersebut.

🌐 Deployment
Komponen	Cara deploy
Backend	Jalankan di server dengan PM2 (pm2 start server.js --name royaltree-api)
Firestore rules & index	firebase deploy --only firestore:rules,firestore:indexes
Admin panel	Hosting statis (mis. Firebase Hosting atau web server biasa)
Android	Build release (./gradlew assembleRelease atau AAB lewat Android Studio) lalu publikasikan lewat Google Play Console
🔐 Keamanan

Aplikasi ini mengelola saldo dan penarikan dana, jadi perhatikan hal berikut:

Jangan commit file .env, kredensial service account, atau keystore. Pastikan semuanya ada di .gitignore.
Perubahan saldo, poin, dan withdrawal sebaiknya hanya dilakukan lewat backend (Admin SDK), bukan langsung dari klien. Pastikan firestore.rules melarang klien menulis field sensitif tersebut.
Validasi semua input di backend, termasuk klaim reward, referral, dan permintaan withdrawal.
Verifikasi Firebase ID token di setiap endpoint yang butuh autentikasi.
Batasi akses panel admin hanya untuk akun yang berwenang.
🧰 Troubleshooting
Masalah	Solusi
Gradle Sync gagal	Cek koneksi internet, versi JDK, dan jalankan File → Invalidate Caches / Restart
google-services.json tidak ditemukan	Unduh dari Firebase Console dan letakkan di app/
Package name tidak cocok	Pastikan package name di Firebase sama dengan com.inkside.digital
Backend tidak bisa konek ke Firestore	Periksa kredensial service account dan isi .env
Aplikasi tidak bisa memanggil backend dari emulator	Emulator Android memakai 10.0.2.2 untuk mengakses localhost komputer host
Iklan tidak muncul	Cek ID unit iklan, status akun AdMob, dan gunakan ID test saat development
🤝 Kontribusi
Buat branch baru dari main.
Lakukan perubahan dan pastikan aplikasi serta backend tetap berjalan.
Perbarui CHANGELOG.md dan dokumentasi terkait bila perlu.
Ajukan pull request dengan deskripsi perubahan yang jelas.

Format pesan commit yang disarankan:

text
feat: tambah fitur baru
fix: perbaiki bug
docs: ubah dokumentasi
refactor: rapikan kode tanpa mengubah perilaku
📄 Lisensi

Internal project — Inkside Digital Teknologi.

📞 Kontak
GitHub: inksidedigitalteknologi
Email: (tambahkan email tim)
