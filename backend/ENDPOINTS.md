# Dokumentasi Endpoint Portal API Royaltree

File ini mendokumentasikan spesifikasi REST API resmi untuk menghubungkan aplikasi Android **Royaltree** dengan server/portal website Anda.

---

## 🔑 Autentikasi
Kecuali endpoint publik seperti `/health`, semua permintaan dari aplikasi menyertakan header:
```http
Authorization: Bearer rt_secret_portal_key_2026
Content-Type: application/json
```

---

## 📌 Daftar Endpoint

### 1. Health Check (Uji Koneksi Server)
* **Method:** `GET`
* **URL:** `/api/v1/health`
* **Deskripsi:** Digunakan oleh aplikasi Android untuk mengetes apakah URL portal aktif dan bisa dijangkau.
* **Respons (200 OK):**
```json
{
  "success": true,
  "status": "ONLINE",
  "message": "Royaltree Portal API Server berjalan dengan normal.",
  "timestamp": 1789182390123
}
```

---

### 2. Konfigurasi Sistem Dinamis (Remote Config)
* **Method:** `GET`
* **URL:** `/api/v1/config`
* **Deskripsi:** Aplikasi mengambil pengaturan limit penarikan, rate komisi, dan mode maintenance.
* **Respons (200 OK):**
```json
{
  "success": true,
  "data": {
    "maintenanceMode": false,
    "minWithdrawalEWallet": 50000,
    "minWithdrawalBank": 100000,
    "minWithdrawalCrypto": 250000,
    "freeTierCommissionRate": 12.0,
    "vipTierCommissionRate": 30.0,
    "pointsPerUsdRate": 100,
    "idrPerHundredPoints": 16000
  }
}
```

---

### 3. Sinkronisasi Data Pengguna (User Sync)
* **Method:** `POST`
* **URL:** `/api/v1/users/sync`
* **Headers:** `Authorization: Bearer <API_KEY>`
* **Body Request:**
```json
{
  "id": "user_001",
  "name": "Hendra Wijaya",
  "email": "hendra.affiliate@gmail.com",
  "phone": "+62 812-3456-7890",
  "tier": "FREE",
  "balance": 4750000,
  "points": 1850,
  "todaySteps": 1840,
  "referralCode": "PRO8892",
  "city": "Jakarta Pusat"
}
```
* **Respons (200 OK):**
```json
{
  "success": true,
  "message": "Data pengguna berhasil disinkronkan ke portal.",
  "serverSyncedAt": 1789182400000
}
```

---

### 4. Daftar & Kelola Penarikan Dana (Withdrawals)
* **Mengambil Antrean Penarikan:**
  * `GET /api/v1/withdrawals?status=PENDING`
* **Membuat Permintaan Penarikan Baru:**
  * `POST /api/v1/withdrawals`
  * Body:
  ```json
  {
    "id": "WD-90412",
    "userId": "user_001",
    "amount": 250000,
    "currency": "IDR",
    "channelType": "E_WALLET",
    "providerName": "GoPay",
    "accountDestination": "0812-3456-7890",
    "accountHolderName": "Hendra Wijaya",
    "status": "PENDING",
    "fee": 2500,
    "netAmount": 247500,
    "txRef": "TX-2026-GP-90412"
  }
  ```
* **Memperbarui Status Penarikan (Approve / Paid / Reject):**
  * `PUT /api/v1/withdrawals/:id/status`
  * Body:
  ```json
  {
    "status": "PAID",
    "adminNotes": "Ditransfer via BCA Bisnis batch #891"
  }
  ```

---

### 5. Webhook Postback Vendor (Offerwall / Ad Network)
* **Method:** `GET` atau `POST`
* **URL:** `/api/v1/postback?user_id={user_id}&reward_points={points}&tx_id={tx_id}`
* **Deskripsi:** Vendor seperti BitLabs, Torox, atau Pollfish menembak endpoint ini saat pengguna menyelesaikan survei atau misi.
* **Respons:** Kirim teks `OK` atau HTTP 200.

---

## 🚀 Cara Menjalankan di Server Anda:
1. Simpan file `backend/server.js` di server Anda.
2. Jalankan `npm install express cors` lalu `node server.js` (atau gunakan PM2: `pm2 start server.js`).
3. Di dalam aplikasi Android Royaltree, masuk ke menu **Admin / Pengaturan Portal**, masukkan URL server Anda (misal: `https://api.domainanda.com/api/v1`), lalu klik tombol **Uji Koneksi** dan **Sinkronkan**.
