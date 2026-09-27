# 🌳 Royaltree

Aplikasi affiliasi & rewards berbasis Android dengan backend Node.js + Firestore.

---

## 📚 Dokumentasi

| Dokumen | Deskripsi |
|---|---|
| **[ARCHITECTURE.md](ARCHITECTURE.md)** | Arsitektur sistem, diagram Mermaid, alur data |
| **[backend/ENDPOINTS.md](backend/ENDPOINTS.md)** | Spesifikasi API endpoint backend |

---

## ✨ Fitur Utama

| Fitur | Deskripsi |
|---|---|
| 💰 **Rewards** | Daily check-in, missions, referral |
| 📢 **AdMob Rewarded** | Tonton iklan, dapat RTP |
| 📊 **Banner Ads** | Adaptive banner di semua screen |
| 💸 **Withdrawal** | Tarik saldo ke e-wallet / bank |
| 🌍 **Multi-language** | 6 bahasa (ID, EN, ES, ZH, AR, JA) |
| 🔥 **Firebase** | Auth + Firestore realtime sync |
| 📱 **Step Counter** | Konversi langkah jadi poin |

---

## 🛠️ Tech Stack

**Android**
- Kotlin + Jetpack Compose
- Room Database (v6)
- Retrofit + OkHttp
- Firebase Auth + Firestore
- AdMob (Rewarded + Banner)

**Backend**
- Node.js + Express
- Firebase Admin SDK
- PM2 (production)

**Database**
- Cloud Firestore

---

## 📂 Struktur Proyek
Royaltree/
├── app/ → Android app (Kotlin + Compose)
├── backend/ → Node.js + Express API
├── admin/ → Admin panel (static)
└── ARCHITECTURE.md



Detail lengkap: lihat **[ARCHITECTURE.md](ARCHITECTURE.md)**

---

## 🚀 Setup Development

### Android

1. Buka **Android Studio**
2. `File → Open` → pilih folder `Royaltree`
3. Tunggu Gradle Sync selesai
4. Pastikan file `app/google-services.json` ada (Firebase config)
5. `Build → Rebuild Project`
6. `Run` (▶️)

### Backend

```bash
cd backend
npm install
node server.js
Atau pakai PM2:

bash
pm2 start server.js --name royaltree-api
Server jalan di http://localhost:3000

Firebase
Buka Firebase Console

Buat project baru → tambah Android app (com.inkside.digital)

Download google-services.json → taruh di app/

Enable Authentication (Email/Password)

Enable Cloud Firestore (production mode)

Deploy backend → hubungkan ke Firestore

🚦 Status Fitur
Fitur	Status
Core (Home, Profile, Missions)	✅ Active
Firestore sync	✅ Active
AdMob Rewarded	✅ Active
AdMob Banner	✅ Active
Multi-language	✅ Active
Withdrawal	✅ Active
Analytics / Campaigns / Game Room	⚠️ Disabled
Step Counter	⚠️ Disabled
App Offers / Invest / Profile Security	⚠️ Disabled
Catatan: Screen "Disabled" masih ada di codebase, tapi tombol navigasinya disembunyikan karena backend belum support. Detail: lihat ARCHITECTURE.md.

📄 Lisensi
Internal project — Inkside Digital Teknologi.

📞 Kontak
GitHub: inksidedigitalteknologi

Email: (tambahkan email tim)

Last updated: 2026-09-27



---

## 📌 Cara Edit di GitHub

**1️⃣** Buka:
https://github.com/inksidedigitalteknologi/Royaltree/blob/main/README.md



**2️⃣** Klik ikon **pensil** (✏️) → "Edit this file"

**3️⃣** **Ctrl+A** → **Delete** (hapus semua isi lama)

**4️⃣** **Paste** kode di atas

**5️⃣** Scroll bawah → **Commit changes**

**6️⃣** Pesan commit:
docs: rewrite README — dokumentasi proyek Royaltree



**7️⃣** Klik **Commit changes**

---

## 📌 Setelah Commit

README baru akan tampil di halaman utama repo, dengan:
- ✅ Link ke `ARCHITECTURE.md` dan `ENDPOINTS.md`
- ✅ Tabel fitur dengan emoji
- ✅ Panduan setup Android + Backend + Firebase
- ✅ Status fitur (active vs disabled)

---

## 📌 Cek Hasil

Buka:
https://github.com/inksidedigitalteknologi/Royaltree
