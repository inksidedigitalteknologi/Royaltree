/**
 * Royaltree Portal Backend API Server (Node.js + Express)
 * -------------------------------------------------------------
 * Salin dan jalankan file ini di server / hosting / VPS portal Anda.
 * 
 * Cara menjalankan:
 * 1. npm init -y
 * 2. npm install express cors body-parser
 * 3. node server.js
 * 
 * Server default berjalan di port 3000 (http://localhost:3000/api/v1)
 */

const express = require('express');
const cors = require('cors');
const app = express();
const PORT = process.env.PORT || 3000;

app.use(cors());
app.use(express.json());

// Kunci Otentikasi Admin Portal (Ubah sesuai rahasia Anda)
const API_SECRET_KEY = process.env.ROYALTREE_API_KEY || "rt_secret_portal_key_2026";

// Middleware Validasi API Key
function authenticateToken(req, res, next) {
    const authHeader = req.headers['authorization'];
    if (!authHeader) {
        return res.status(401).json({ success: false, message: 'Header Authorization (Bearer Token) diperlukan.' });
    }
    const token = authHeader.startsWith('Bearer ') ? authHeader.substring(7) : authHeader;
    if (token !== API_SECRET_KEY) {
        return res.status(403).json({ success: false, message: 'API Key tidak valid atau kedaluwarsa.' });
    }
    next();
}

// -------------------------------------------------------------
// IN-MEMORY / DATABASE STORAGE (Bisa diganti MySQL / MongoDB / PostgreSQL)
// -------------------------------------------------------------
let systemConfig = {
    maintenanceMode: false,
    minWithdrawalEWallet: 50000,
    minWithdrawalBank: 100000,
    minWithdrawalCrypto: 250000,
    freeTierCommissionRate: 12.0,
    vipTierCommissionRate: 30.0,
    pointsPerUsdRate: 100,
    idrPerHundredPoints: 16000,
    serverVersion: "1.4.0",
    announcement: "Selamat datang di jaringan portal resmi Royaltree! Raih cuan maksimal."
};

let users = [
    {
        id: "user_001",
        name: "Hendra Wijaya",
        email: "hendra.affiliate@gmail.com",
        tier: "FREE",
        balance: 4750000,
        points: 1850,
        todaySteps: 1840,
        referralCode: "PRO8892",
        updatedAt: new Date().toISOString()
    }
];

let withdrawals = [
    {
        id: "WD-90412",
        userId: "user_001",
        userName: "Hendra Wijaya",
        amount: 250000,
        currency: "IDR",
        channelType: "E_WALLET",
        providerName: "GoPay",
        accountDestination: "0812-3456-7890",
        accountHolderName: "Hendra Wijaya",
        status: "PENDING",
        fee: 2500,
        netAmount: 247500,
        txRef: "TX-2026-GP-90412",
        requestedAt: Date.now()
    }
];

let campaigns = [
    {
        id: "camp_001",
        title: "Shopee Affiliate Festival 9.9",
        category: "E-Commerce",
        merchantName: "Shopee Indonesia",
        commissionDisplay: "Hingga 15%",
        payoutType: "CPS",
        baseCommissionRate: 15.0,
        status: "ACTIVE"
    },
    {
        id: "camp_002",
        title: "Tokopedia Traktir Cuan Elektronik",
        category: "Gadget & Lifestyle",
        merchantName: "Tokopedia",
        commissionDisplay: "Hingga 10%",
        payoutType: "CPS",
        baseCommissionRate: 10.0,
        status: "ACTIVE"
    }
];

let taskMissions = [
    {
        id: "m_sub_youtube",
        title: "Langganan Saluran YouTube Resmi",
        category: "SUBSCRIBE",
        rtpReward: 50,
        type: "ENGAGEMENT_MISSION",
        targetPlatform: "YouTube",
        durationSeconds: 10,
        actionUrl: "https://youtube.com/@royaltree_official"
    },
    {
        id: "m_visit_web",
        title: "Kunjungi Halaman Sponsor Finansial",
        category: "WEB",
        rtpReward: 35,
        type: "DAILY_TASK",
        targetPlatform: "Website",
        durationSeconds: 15,
        actionUrl: "https://royaltree.id/sponsor/finance"
    }
];

// -------------------------------------------------------------
// ENDPOINTS
// -------------------------------------------------------------

// 1. Health Check (Untuk Uji Koneksi dari Aplikasi)
app.get('/api/v1/health', (req, res) => {
    res.json({
        success: true,
        status: "ONLINE",
        message: "Royaltree Portal API Server berjalan dengan normal.",
        timestamp: Date.now(),
        serverTime: new Date().toISOString()
    });
});

// 2. Remote Configuration (Pengaturan Sistem)
app.get('/api/v1/config', (req, res) => {
    res.json({
        success: true,
        data: systemConfig
    });
});

app.put('/api/v1/config', authenticateToken, (req, res) => {
    systemConfig = { ...systemConfig, ...req.body };
    res.json({
        success: true,
        message: "Konfigurasi sistem portal berhasil diperbarui.",
        data: systemConfig
    });
});

// 3. User Data Synchronization (Sinkronisasi Pengguna)
app.post('/api/v1/users/sync', authenticateToken, (req, res) => {
    const incomingUser = req.body;
    if (!incomingUser || !incomingUser.id) {
        return res.status(400).json({ success: false, message: "Data pengguna tidak valid (id wajib disertakan)." });
    }

    const existingIndex = users.findIndex(u => u.id === incomingUser.id);
    if (existingIndex >= 0) {
        users[existingIndex] = { ...users[existingIndex], ...incomingUser, updatedAt: new Date().toISOString() };
    } else {
        users.push({ ...incomingUser, updatedAt: new Date().toISOString() });
    }

    res.json({
        success: true,
        message: "Data pengguna berhasil disinkronkan ke portal.",
        serverSyncedAt: Date.now(),
        user: incomingUser
    });
});

// 4. Daftar & Kelola Penarikan Dana (Withdrawals Payout)
app.get('/api/v1/withdrawals', authenticateToken, (req, res) => {
    const statusFilter = req.query.status;
    const result = statusFilter ? withdrawals.filter(w => w.status === statusFilter) : withdrawals;
    res.json({
        success: true,
        total: result.length,
        data: result
    });
});

app.post('/api/v1/withdrawals', authenticateToken, (req, res) => {
    const newWd = req.body;
    if (!newWd.id || !newWd.amount) {
        return res.status(400).json({ success: false, message: "Format data penarikan tidak lengkap." });
    }
    withdrawals.unshift(newWd);
    res.status(201).json({
        success: true,
        message: "Permintaan penarikan dana berhasil diterima portal.",
        data: newWd
    });
});

app.put('/api/v1/withdrawals/:id/status', authenticateToken, (req, res) => {
    const { id } = req.params;
    const { status, adminNotes } = req.body; // status: APPROVED, PAID, REJECTED
    
    const wdIndex = withdrawals.findIndex(w => w.id === id);
    if (wdIndex === -1) {
        return res.status(404).json({ success: false, message: `Penarikan ID ${id} tidak ditemukan.` });
    }

    withdrawals[wdIndex].status = status;
    if (adminNotes) withdrawals[wdIndex].adminNotes = adminNotes;
    withdrawals[wdIndex].processedAt = Date.now();

    res.json({
        success: true,
        message: `Status penarikan ${id} berhasil diubah menjadi ${status}.`,
        data: withdrawals[wdIndex]
    });
});

// 5. Kelola Kampanye Afiliasi (Campaigns)
app.get('/api/v1/campaigns', (req, res) => {
    res.json({
        success: true,
        total: campaigns.length,
        data: campaigns
    });
});

app.post('/api/v1/campaigns', authenticateToken, (req, res) => {
    const newCamp = req.body;
    campaigns.push(newCamp);
    res.status(201).json({
        success: true,
        message: "Kampanye baru berhasil ditambahkan ke portal.",
        data: newCamp
    });
});

// 6. Kelola Misi Interaktif (Tasks & Missions)
app.get('/api/v1/missions', (req, res) => {
    res.json({
        success: true,
        total: taskMissions.length,
        data: taskMissions
    });
});

// 7. Postback Webhook (Untuk Menerima Poin dari Vendor Offerwall/Iklan seperti BitLabs/Torox)
app.get('/api/v1/postback', (req, res) => {
    const { user_id, reward_points, tx_id, secret } = req.query;
    console.log(`[POSTBACK WEBHOOK] User ${user_id} mendapat ${reward_points} poin. TxId: ${tx_id}`);
    
    // Berikan respons status HTTP 200 / "OK" agar vendor tahu notifikasi berhasil diterima
    res.send("OK");
});

// Jalankan Server
app.listen(PORT, () => {
    console.log(`====================================================`);
    console.log(` Royaltree Portal API Server aktif di port ${PORT}`);
    console.log(` Endpoint: http://localhost:${PORT}/api/v1`);
    console.log(` Health:   http://localhost:${PORT}/api/v1/health`);
    console.log(` Auth Key: ${API_SECRET_KEY}`);
    console.log(`====================================================`);
});
