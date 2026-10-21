/**
 * Royaltree Portal Backend API Server (Node.js + Express + Firestore)
 * -------------------------------------------------------------
 * Versi ini menggunakan Firebase Firestore sebagai database permanen.
 *
 * Cara menjalankan:
 * 1. Pastikan file .env sudah berisi GOOGLE_APPLICATION_CREDENTIALS
 * 2. node server.js
 * 3. Atau gunakan PM2: pm2 start server.js --name royaltree-api
 */

require('dotenv').config();
const express = require('express');
const logger = require('./utils/logger');
const cors = require('cors');
const { admin, db } = require('./config/firebase');
const authRoutes = require('./routes/auth');
const referralRoutes = require('./routes/referral');
const dailyRoutes = require('./routes/daily');
const adsRoutes = require('./routes/ads');
const adminRoutes = require('./routes/admin');
const boardRoutes = require('./routes/board');
const { checkAdmin } = require('./middleware/checkAdmin');
const gameRoutes = require('./routes/game');
const purchaseRoutes = require('./routes/purchase');
const affiliateRoutes = require('./routes/affiliate');
const sweepstakesRoutes = require('./routes/sweepstakes');
const transferRoutes = require('./routes/transfer');
const chatRoutes = require('./routes/chat');
const achievementsRoutes = require('./routes/achievements');

// Rate limiting
const { globalLimiter, authLimiter, sensitiveLimiter, miningLimiter, adsLimiter, chatLimiter, adminLoginLimiter } = require('./middleware/rateLimit');
const sweepstakesJob = require('./jobs/sweepstakesJob');
const { releasePendingCommission } = require('./services/affiliate');
const { verifyFirebaseToken, verifyAuthAndBanCheck } = require('./middleware/firebaseAuth');
const { usageTracker } = require('./middleware/usageTracker');
const exchangeRate = require('./services/exchangeRate');
const anomaly = require('./services/anomaly');

const app = express();
const PORT = process.env.PORT || 3000;

app.use(cors({
    origin: [
        'http://localhost:3000',
        'http://localhost:5173',
        'https://royaltree.id',
        'https://www.royaltree.id',
        // Tambahkan domain lain jika ada
    ],
    credentials: true,
    methods: ['GET', 'POST', 'PUT', 'DELETE', 'OPTIONS'],
    allowedHeaders: ['Content-Type', 'Authorization', 'x-admin-token']
}));
app.use(express.json());

// Track Firestore reads/writes per endpoint (flush ke Firestore tiap 1 menit)
app.use(usageTracker);

// Auto-sync kurs USD → IDR tiap 1 jam
// Yahoo Finance (primary) → open.er-api.com (fallback)
exchangeRate.startAutoSync();
// ============================================================
// ROUTES
// ============================================================
// Auth & Admin: TIDAK cek banned (biar user banned bisa login & lihat status)
app.use('/api', globalLimiter);

app.use('/api/v1/auth/login', authLimiter);
app.use('/api/v1/auth/register', authLimiter);
app.use('/api/v1/auth', authRoutes);
app.use('/api/v1/referral', referralRoutes);
app.use('/api/v1/admin/login', adminLoginLimiter);
app.use('/api/v1/admin', adminRoutes);

// Route sensitif: cek banned (block aksi user banned)
app.use('/api/v1/daily', verifyAuthAndBanCheck, dailyRoutes);
app.use('/api/v1/ads', adsLimiter, verifyAuthAndBanCheck, adsRoutes);
app.use('/api/v1/game', miningLimiter, verifyAuthAndBanCheck, gameRoutes);
app.use('/api/v1/game/board', miningLimiter, verifyAuthAndBanCheck, boardRoutes);
app.use('/api/v1/purchase', sensitiveLimiter, verifyAuthAndBanCheck, purchaseRoutes);
app.use('/api/v1/affiliate', verifyAuthAndBanCheck, affiliateRoutes);
app.use('/api/v1/sweepstakes', sensitiveLimiter, verifyAuthAndBanCheck, sweepstakesRoutes);
app.use('/api/v1/achievements', verifyAuthAndBanCheck, achievementsRoutes);
app.use('/api/v1/transfer', sensitiveLimiter, verifyAuthAndBanCheck, transferRoutes);
app.use('/api/v1/chat', chatLimiter, verifyAuthAndBanCheck, chatRoutes);

// Serve admin panel
// Serve admin panel (relative path — portable)
const path = require('path');
app.use('/admin', express.static(path.join(__dirname, '../admin')));

// Kunci Otentikasi Admin Portal
const API_SECRET_KEY = process.env.ROYALTREE_API_KEY;
if (!API_SECRET_KEY) {
    logger.error("❌ FATAL: ROYALTREE_API_KEY tidak di-set di .env");
    process.exit(1);
}

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
// ENDPOINTS
// -------------------------------------------------------------

// Import notifications helper
const { createNotification } = require('./utils/notifications');

// 1. Health Check
app.get('/api/v1/health', async (req, res) => {
    try {
        const doc = await db.collection('system').doc('config').get();
        const config = doc.exists ? doc.data() : {};
        const isMaintenance = config.maintenanceMode === true;
        res.json({
            success: true,
            status: isMaintenance ? "MAINTENANCE" : "ONLINE",
            message: isMaintenance
                ? (config.maintenanceMessage || 'Sedang dalam perbaikan')
                : "Royaltree Portal API Server berjalan dengan normal.",
            maintenance: isMaintenance,
            maintenanceMessage: config.maintenanceMessage || '',
            announcement: config.announcement || '',
            serverVersion: config.serverVersion || '1.4.0',
            timestamp: Date.now(),
            serverTime: new Date().toISOString(),
            database: "Firestore"
        });
    } catch (e) {
        res.json({
            success: true,
            status: "ONLINE",
            maintenance: false,
            timestamp: Date.now()
        });
    }
});

// 2. Remote Configuration
app.get('/api/v1/config', async (req, res) => {
    try {
        const doc = await db.collection('system').doc('config').get();
        const defaultConfig = {
            maintenanceMode: false,
            minWithdrawalEWallet: 50000,
            minWithdrawalBank: 100000,
            minWithdrawalCrypto: 250000,
            freeTierCommissionRate: 12.0,
            vipTierCommissionRate: 30.0,
            pointsPerUsdRate: 100,
            idrPerHundredPoints: 16000,
            serverVersion: "1.4.0",
            announcement: "Selamat datang di jaringan portal resmi Royaltree!"
        };
        const data = doc.exists ? { ...defaultConfig, ...doc.data() } : defaultConfig;
        res.json({ success: true, data });
    } catch (err) {
        res.status(500).json({ success: false, message: "Terjadi kesalahan server. Coba lagi nanti." });
    }
});

app.put('/api/v1/config', authenticateToken, checkAdmin, async (req, res) => {
    try {
        await db.collection('system').doc('config').set(req.body, { merge: true });
        const doc = await db.collection('system').doc('config').get();
        res.json({
            success: true,
            message: "Konfigurasi sistem portal berhasil diperbarui.",
            data: doc.data()
        });
    } catch (err) {
        res.status(500).json({ success: false, message: "Terjadi kesalahan server. Coba lagi nanti." });
    }
});

// 3. User Data Synchronization
app.post('/api/v1/users/sync', authenticateToken, async (req, res) => {
    try {
        const incomingUser = req.body;
        // ✅ SECURITY FIX: userId dari token, bukan dari body (cegah IDOR)
        const userId = req.user.uid || (req.firebaseUser && req.firebaseUser.uid);
        if (!userId) {
            return res.status(401).json({ success: false, message: "Token tidak valid." });
        }

        // Hapus field sensitif dari body
        const safeData = { ...incomingUser };
        delete safeData.id;
        delete safeData.isAdmin;
        delete safeData.role;
        delete safeData.banned;
        delete safeData.bannedReason;

        const userData = {
            ...safeData,
            id: userId,  // ← ID dari token
            updatedAt: new Date().toISOString()
        };

        await db.collection('users').doc(userId).set(userData, { merge: true });

        res.json({
            success: true,
            message: "Data pengguna berhasil disinkronkan ke portal.",
            serverSyncedAt: Date.now(),
            user: userData
        });
    } catch (err) {
        res.status(500).json({ success: false, message: "Terjadi kesalahan server. Coba lagi nanti." });
    }
});

// 4. Withdrawals
app.get('/api/v1/withdrawals', authenticateToken, async (req, res) => {
    try {
        const statusFilter = req.query.status;
        let query = db.collection('withdrawals');
        // ✅ SECURITY FIX: filter by user (kecuali admin)
        query = query.where('userId', '==', req.user.uid);
        if (statusFilter) {
            query = query.where('status', '==', statusFilter);
        }
        const snapshot = await query.get();
        const result = snapshot.docs.map(doc => ({ id: doc.id, ...doc.data() }));
        res.json({ success: true, total: result.length, data: result });
    } catch (err) {
        res.status(500).json({ success: false, message: "Terjadi kesalahan server. Coba lagi nanti." });
    }
});

app.post('/api/v1/withdrawals', authenticateToken, async (req, res) => {
    try {
        const newWd = req.body;
        if (!newWd.id || !newWd.amount) {
            return res.status(400).json({ success: false, message: "Format data penarikan tidak lengkap." });
        }
        newWd.requestedAt = Date.now();
        await db.collection('withdrawals').doc(newWd.id).set(newWd);
    // ==================== DAILY CHECK-IN (SERVER-SIDE) ====================

        res.status(201).json({
            success: true,
            message: "Permintaan penarikan dana berhasil diterima portal.",
            data: newWd
        });
    } catch (err) {
        res.status(500).json({ success: false, message: "Terjadi kesalahan server. Coba lagi nanti." });
    }
});
    // ==================== END DAILY CHECK-IN ====================

app.put('/api/v1/withdrawals/:id/status', authenticateToken, checkAdmin, async (req, res) => {
    try {
        const { id } = req.params;
        const { status, adminNotes } = req.body;

        const docRef = db.collection('withdrawals').doc(id);
        const doc = await docRef.get();
        if (!doc.exists) {
            return res.status(404).json({ success: false, message: `Penarikan ID ${id} tidak ditemukan.` });
        }

        const updateData = { status, processedAt: Date.now() };
        if (adminNotes) updateData.adminNotes = adminNotes;

        await docRef.update(updateData);
        const updated = await docRef.get();

        res.json({
            success: true,
            message: `Status penarikan ${id} berhasil diubah menjadi ${status}.`,
            data: { id: updated.id, ...updated.data() }
        });
    } catch (err) {
        res.status(500).json({ success: false, message: "Terjadi kesalahan server. Coba lagi nanti." });
    }
});

// 5. Campaigns
app.get('/api/v1/campaigns', async (req, res) => {
    try {
        const snapshot = await db.collection('campaigns').get();
        const result = snapshot.docs.map(doc => ({ id: doc.id, ...doc.data() }));
        res.json({ success: true, total: result.length, data: result });
    } catch (err) {
        res.status(500).json({ success: false, message: "Terjadi kesalahan server. Coba lagi nanti." });
    }
});

app.post('/api/v1/campaigns', authenticateToken, checkAdmin, async (req, res) => {
    try {
        const newCamp = req.body;
        if (!newCamp.id) {
            return res.status(400).json({ success: false, message: "ID kampanye wajib disertakan." });
        }
        await db.collection('campaigns').doc(newCamp.id).set(newCamp);
        res.status(201).json({
            success: true,
            message: "Kampanye baru berhasil ditambahkan ke portal.",
            data: newCamp
        });
    } catch (err) {
        res.status(500).json({ success: false, message: "Terjadi kesalahan server. Coba lagi nanti." });
    }
});

// 6. Missions
app.get('/api/v1/missions', async (req, res) => {
    try {
        const snapshot = await db.collection('missions').get();
        const result = snapshot.docs.map(doc => ({ id: doc.id, ...doc.data() }));
        res.json({ success: true, total: result.length, data: result });
    } catch (err) {
        res.status(500).json({ success: false, message: "Terjadi kesalahan server. Coba lagi nanti." });
    }
});

// 7. Postback Webhook
app.get('/api/v1/postback', async (req, res) => {
    // ✅ SECURITY FIX: Validasi secret dari provider iklan
    const POSTBACK_SECRET = process.env.POSTBACK_SECRET || 'ubah-secret-ini-di-env';
    if (req.query.secret !== POSTBACK_SECRET) {
        logger.warn(`[POSTBACK] Akses ditolak: secret tidak valid dari IP ${req.ip}`);
        return res.status(403).json({ success: false, message: 'Akses ditolak' });
    }

    try {
        const { user_id, reward_points, tx_id, secret } = req.query;
        logger.info(`[POSTBACK WEBHOOK] User ${user_id} mendapat ${reward_points} poin. TxId: ${tx_id}`);

        if (user_id && reward_points) {
            const userRef = db.collection('users').doc(user_id);
            const userDoc = await userRef.get();
            if (userDoc.exists) {
                const currentPoints = userDoc.data().points || 0;
                await userRef.update({
                    points: currentPoints + parseInt(reward_points),
                    updatedAt: new Date().toISOString()
                });
            }
            await db.collection('postback_logs').add({
                user_id, reward_points: parseInt(reward_points), tx_id,
                receivedAt: Date.now()
            });
        }

        res.send("OK");
    } catch (err) {
        logger.error('Postback error:', err);
        res.send("OK");
    }
});

// Jalankan Server

// 8. GET Users (Baca Daftar Pengguna)
app.get('/api/v1/users', authenticateToken, async (req, res) => {
    try {
        const snapshot = await db.collection('users').get();
        const result = snapshot.docs.map(doc => ({ id: doc.id, ...doc.data() }));
        res.json({ success: true, total: result.length, data: result });
    } catch (err) {
        res.status(500).json({ success: false, message: "Terjadi kesalahan server. Coba lagi nanti." });
    }
});

// 9. POST Missions (Tambah Misi)
app.post('/api/v1/missions', authenticateToken, checkAdmin, async (req, res) => {
    try {
        const newMission = req.body;
        if (!newMission.id) {
            return res.status(400).json({ success: false, message: "ID misi wajib disertakan." });
        }
        await db.collection('missions').doc(newMission.id).set(newMission);
        res.status(201).json({
            success: true,
            message: "Misi baru berhasil ditambahkan.",
            data: newMission
        });
    } catch (err) {
        res.status(500).json({ success: false, message: "Terjadi kesalahan server. Coba lagi nanti." });
    }
});

// 10. GET Single User
app.get('/api/v1/users/:id', authenticateToken, async (req, res) => {
    try {
        // ✅ SECURITY FIX: Validasi kepemilikan (kecuali admin)
        if (req.params.id !== req.user.uid) {
            return res.status(403).json({ success: false, message: 'Akses ditolak.' });
        }
        const doc = await db.collection('users').doc(req.params.id).get();
        if (!doc.exists) {
            return res.status(404).json({ success: false, message: "User tidak ditemukan." });
        }
        res.json({ success: true, data: { id: doc.id, ...doc.data() } });
    } catch (err) {
        res.status(500).json({ success: false, message: "Terjadi kesalahan server. Coba lagi nanti." });
    }
});


// PUT User
app.put('/api/v1/users/:id', authenticateToken, async (req, res) => {
    try {
        // ✅ SECURITY FIX: Validasi kepemilikan
        if (req.params.id !== req.user.uid) {
            return res.status(403).json({ success: false, message: 'Akses ditolak.' });
        }
        const docRef = db.collection('users').doc(req.params.id);
        const doc = await docRef.get();
        if (!doc.exists) return res.status(404).json({ success: false, message: 'User tidak ditemukan.' });
        await docRef.update({ ...req.body, updatedAt: new Date().toISOString() });
        const updated = await docRef.get();
        res.json({ success: true, message: 'User diupdate.', data: { id: updated.id, ...updated.data() } });
    } catch (err) { res.status(500).json({ success: false, message: "Terjadi kesalahan server. Coba lagi nanti." }); }
});

// DELETE User
app.delete('/api/v1/users/:id', authenticateToken, async (req, res) => {
    try {
        // ✅ SECURITY FIX: Validasi kepemilikan
        if (req.params.id !== req.user.uid) {
            return res.status(403).json({ success: false, message: 'Akses ditolak.' });
        }
        const docRef = db.collection('users').doc(req.params.id);
        const doc = await docRef.get();
        if (!doc.exists) return res.status(404).json({ success: false, message: 'User tidak ditemukan.' });
        await docRef.delete();
        res.json({ success: true, message: 'User dihapus.' });
    } catch (err) { res.status(500).json({ success: false, message: "Terjadi kesalahan server. Coba lagi nanti." }); }
});

// PUT Campaign
app.put('/api/v1/campaigns/:id', authenticateToken, checkAdmin, async (req, res) => {
    try {
        const docRef = db.collection('campaigns').doc(req.params.id);
        const doc = await docRef.get();
        if (!doc.exists) return res.status(404).json({ success: false, message: 'Kampanye tidak ditemukan.' });
        await docRef.update(req.body);
        const updated = await docRef.get();
        res.json({ success: true, message: 'Kampanye diupdate.', data: { id: updated.id, ...updated.data() } });
    } catch (err) { res.status(500).json({ success: false, message: "Terjadi kesalahan server. Coba lagi nanti." }); }
});

// DELETE Campaign
app.delete('/api/v1/campaigns/:id', authenticateToken, checkAdmin, async (req, res) => {
    try {
        const docRef = db.collection('campaigns').doc(req.params.id);
        const doc = await docRef.get();
        if (!doc.exists) return res.status(404).json({ success: false, message: 'Kampanye tidak ditemukan.' });
        await docRef.delete();
        res.json({ success: true, message: 'Kampanye dihapus.' });
    } catch (err) { res.status(500).json({ success: false, message: "Terjadi kesalahan server. Coba lagi nanti." }); }
});

// PUT Mission
app.put('/api/v1/missions/:id', authenticateToken, checkAdmin, async (req, res) => {
    try {
        const docRef = db.collection('missions').doc(req.params.id);
        const doc = await docRef.get();
        if (!doc.exists) return res.status(404).json({ success: false, message: 'Misi tidak ditemukan.' });
        await docRef.update(req.body);
        const updated = await docRef.get();
        res.json({ success: true, message: 'Misi diupdate.', data: { id: updated.id, ...updated.data() } });
    } catch (err) { res.status(500).json({ success: false, message: "Terjadi kesalahan server. Coba lagi nanti." }); }
});

// DELETE Mission
app.delete('/api/v1/missions/:id', authenticateToken, checkAdmin, async (req, res) => {
    try {
        const docRef = db.collection('missions').doc(req.params.id);
        const doc = await docRef.get();
        if (!doc.exists) return res.status(404).json({ success: false, message: 'Misi tidak ditemukan.' });
        await docRef.delete();
        res.json({ success: true, message: 'Misi dihapus.' });
    } catch (err) { res.status(500).json({ success: false, message: "Terjadi kesalahan server. Coba lagi nanti." }); }
});

// ============================================================
// POST /api/v1/missions/:id/complete
// User selesaikan misi — update progress
// ============================================================
app.post('/api/v1/missions/:id/complete', authenticateToken, async (req, res) => {
    try {
        const { userId } = req.body;
        const missionId = req.params.id;

        if (!userId) {
            return res.status(400).json({ success: false, message: 'userId wajib diisi.' });
        }

        const missionRef = db.collection('missions').doc(missionId);
        const missionDoc = await missionRef.get();

        if (!missionDoc.exists) {
            return res.status(404).json({ success: false, message: 'Misi tidak ditemukan.' });
        }

        // Simpan progress user
        const progressRef = db.collection('user_missions').doc(`${userId}_${missionId}`);
        await progressRef.set({
            userId: userId,
            missionId: missionId,
            progress: 1,
            completed: true,
            completedAt: admin.firestore.FieldValue.serverTimestamp()
        }, { merge: true });

        return res.json({
            success: true,
            message: 'Misi berhasil diselesaikan! Klaim hadiah untuk dapat poin.'
        });
    } catch (error) {
        logger.error('Complete mission error:', error);
        return res.status(500).json({ success: false, message: "Terjadi kesalahan server. Coba lagi nanti." });
    }
});

// ============================================================
// POST /api/v1/missions/:id/claim
// User klaim hadiah misi — tambah poin
// ============================================================
app.post('/api/v1/missions/:id/claim', authenticateToken, async (req, res) => {
    try {
        // ✅ SECURITY FIX: Ambil userId dari token, bukan dari body
        const userId = req.user.uid;
        
        // Cek apakah user sudah pernah klaim misi ini
        const progressRef = db.collection('user_missions').doc(`${userId}_${req.params.id}`);
        const progressDoc = await progressRef.get();
        if (progressDoc.exists && progressDoc.data().isClaimed) {
            return res.status(429).json({ success: false, message: 'Hadiah misi ini sudah pernah Anda klaim.' });
        }

        const missionId = req.params.id;

        if (!userId) {
            return res.status(400).json({ success: false, message: 'userId wajib diisi.' });
        }

        const missionDoc = await db.collection('missions').doc(missionId).get();

        if (!missionDoc.exists) {
            return res.status(404).json({ success: false, message: 'Misi tidak ditemukan.' });
        }

        const mission = missionDoc.data();
        const reward = mission.rtpReward || 50;

        // Cek sudah pernah klaim?
        const claimRef = db.collection('mission_claims').doc(`${userId}_${missionId}`);
        const claimDoc = await claimRef.get();

        if (claimDoc.exists) {
            return res.status(400).json({ success: false, message: 'Hadiah misi sudah pernah diklaim.' });
        }

        // Simpan klaim
        await claimRef.set({
            userId: userId,
            missionId: missionId,
            reward: reward,
            claimedAt: admin.firestore.FieldValue.serverTimestamp()
        });

        // Tambah poin user
        await db.collection('users').doc(userId).update({
            points: admin.firestore.FieldValue.increment(reward)
        });

        return res.json({
            success: true,
            message: `Berhasil klaim +${reward} RTP!`,
            reward: reward
        });
    } catch (error) {
        logger.error('Claim mission error:', error);
        return res.status(500).json({ success: false, message: "Terjadi kesalahan server. Coba lagi nanti." });
    }
});

// DELETE Withdrawal
app.delete('/api/v1/withdrawals/:id', authenticateToken, async (req, res) => {
    try {
        const docRef = db.collection('withdrawals').doc(req.params.id);
        const doc = await docRef.get();
        if (!doc.exists) return res.status(404).json({ success: false, message: 'Penarikan tidak ditemukan.' });
        await docRef.delete();
        res.json({ success: true, message: 'Penarikan dihapus.' });
    } catch (err) { res.status(500).json({ success: false, message: "Terjadi kesalahan server. Coba lagi nanti." }); }
});

// PUT Withdrawal (Update)
app.put('/api/v1/withdrawals/:id', authenticateToken, async (req, res) => {
    try {
        const docRef = db.collection('withdrawals').doc(req.params.id);
        const doc = await docRef.get();
        if (!doc.exists) return res.status(404).json({ success: false, message: 'Penarikan tidak ditemukan.' });
        await docRef.update(req.body);
        const updated = await docRef.get();
        res.json({ success: true, message: 'Penarikan diupdate.', data: { id: updated.id, ...updated.data() } });
    } catch (err) { res.status(500).json({ success: false, message: "Terjadi kesalahan server. Coba lagi nanti." }); }
});

// ============ STEP COUNTER ============

app.post('/api/v1/steps/sync', authenticateToken, async (req, res) => {
    try {
        const { userId, steps, date } = req.body;
        if (!userId || steps === undefined) {
            return res.status(400).json({ success: false, message: 'userId dan steps wajib diisi.' });
        }
        const today = date || new Date().toISOString().split('T')[0];
        const docId = `${userId}_${today}`;
        const docRef = db.collection('step_logs').doc(docId);
        const doc = await docRef.get();
        if (doc.exists) {
            const existing = doc.data().steps || 0;
            await docRef.update({ steps: existing + parseInt(steps), updatedAt: new Date().toISOString() });
        } else {
            await docRef.set({ userId, date: today, steps: parseInt(steps), createdAt: new Date().toISOString(), updatedAt: new Date().toISOString() });
        }
        const userRef = db.collection('users').doc(userId);
        const userDoc = await userRef.get();
        if (userDoc.exists) {
            const userData = userDoc.data();
            const totalSteps = (userData.todaySteps || 0) + parseInt(steps);
            await userRef.update({ todaySteps: totalSteps, updatedAt: new Date().toISOString() });
        }
        const updated = await docRef.get();
        res.json({ success: true, message: 'Langkah berhasil disimpan.', data: { id: updated.id, ...updated.data() } });
    } catch (err) { res.status(500).json({ success: false, message: "Terjadi kesalahan server. Coba lagi nanti." }); }
});

app.get('/api/v1/steps/today/:userId', authenticateToken, async (req, res) => {
    // ✅ SECURITY FIX: Validasi kepemilikan
    if (req.params.userId !== req.user.uid) {
        return res.status(403).json({ success: false, message: 'Akses ditolak.' });
    }
    try {
        const { userId } = req.params;
        const today = new Date().toISOString().split('T')[0];
        const docId = `${userId}_${today}`;
        const doc = await db.collection('step_logs').doc(docId).get();
        if (!doc.exists) {
            return res.json({ success: true, data: { userId, date: today, steps: 0 } });
        }
        res.json({ success: true, data: { id: doc.id, ...doc.data() } });
    } catch (err) { res.status(500).json({ success: false, message: "Terjadi kesalahan server. Coba lagi nanti." }); }
});

app.get('/api/v1/steps/history/:userId', authenticateToken, async (req, res) => {
    // ✅ SECURITY FIX: Validasi kepemilikan
    if (req.params.userId !== req.user.uid) {
        return res.status(403).json({ success: false, message: 'Akses ditolak.' });
    }
    try {
        const { userId } = req.params;
        const limit = parseInt(req.query.limit) || 30;
        const snapshot = await db.collection('step_logs')
            .where('userId', '==', userId)
            .orderBy('date', 'desc')
            .limit(limit)
            .get();
        const result = snapshot.docs.map(doc => ({ id: doc.id, ...doc.data() }));
        res.json({ success: true, total: result.length, data: result });
    } catch (err) { res.status(500).json({ success: false, message: "Terjadi kesalahan server. Coba lagi nanti." }); }
});

app.get('/api/v1/steps/leaderboard', authenticateToken, async (req, res) => {
    try {
        const today = new Date().toISOString().split('T')[0];
        const snapshot = await db.collection('step_logs')
            .where('date', '==', today)
            .orderBy('steps', 'desc')
            .limit(20)
            .get();
        const result = snapshot.docs.map(doc => ({ id: doc.id, ...doc.data() }));
        res.json({ success: true, total: result.length, data: result });
    } catch (err) { res.status(500).json({ success: false, message: "Terjadi kesalahan server. Coba lagi nanti." }); }
});

// ============ HISTORY ============

app.get('/api/v1/history/:userId', authenticateToken, async (req, res) => {
    // ✅ SECURITY FIX: Validasi kepemilikan
    if (req.params.userId !== req.user.uid) {
        return res.status(403).json({ success: false, message: 'Akses ditolak.' });
    }
    try {
        const { userId } = req.params;
        const limit = parseInt(req.query.limit) || 50;
        const snapshot = await db.collection('transactions')
            .where('userId', '==', userId)
            .orderBy('timestamp', 'desc')
            .limit(limit)
            .get();
        const result = snapshot.docs.map(doc => ({ id: doc.id, ...doc.data() }));
        res.json({ success: true, total: result.length, data: result });
    } catch (err) { res.status(500).json({ success: false, message: "Terjadi kesalahan server. Coba lagi nanti." }); }
});

app.get('/api/v1/history/:userId/type/:type', authenticateToken, async (req, res) => {
    // ✅ SECURITY FIX: Validasi kepemilikan
    if (req.params.userId !== req.user.uid) {
        return res.status(403).json({ success: false, message: 'Akses ditolak.' });
    }
    try {
        const { userId, type } = req.params;
        const limit = parseInt(req.query.limit) || 50;
        const snapshot = await db.collection('transactions')
            .where('userId', '==', userId)
            .where('type', '==', type)
            .orderBy('timestamp', 'desc')
            .limit(limit)
            .get();
        const result = snapshot.docs.map(doc => ({ id: doc.id, ...doc.data() }));
        res.json({ success: true, type, total: result.length, data: result });
    } catch (err) { res.status(500).json({ success: false, message: "Terjadi kesalahan server. Coba lagi nanti." }); }
});

app.post('/api/v1/history', authenticateToken, async (req, res) => {
    try {
        const { userId, type, amount, description, referenceId } = req.body;
        if (!userId || !type) {
            return res.status(400).json({ success: false, message: 'userId dan type wajib diisi.' });
        }
        const newTx = {
            userId, type, amount: parseFloat(amount) || 0,
            description: description || '', referenceId: referenceId || null,
            timestamp: Date.now(), createdAt: new Date().toISOString()
        };
        const docRef = await db.collection('transactions').add(newTx);
        res.status(201).json({ success: true, message: 'Transaksi ditambahkan.', data: { id: docRef.id, ...newTx } });
    } catch (err) { res.status(500).json({ success: false, message: "Terjadi kesalahan server. Coba lagi nanti." }); }
});

app.get('/api/v1/point-transactions/:userId', authenticateToken, async (req, res) => {
    // ✅ SECURITY FIX: Validasi kepemilikan
    if (req.params.userId !== req.user.uid) {
        return res.status(403).json({ success: false, message: 'Akses ditolak.' });
    }
    try {
        const { userId } = req.params;
        const limit = parseInt(req.query.limit) || 50;
        const snapshot = await db.collection('point_transactions')
            .where('userId', '==', userId)
            .orderBy('timestamp', 'desc')
            .limit(limit)
            .get();
        const result = snapshot.docs.map(doc => ({ id: doc.id, ...doc.data() }));
        res.json({ success: true, total: result.length, data: result });
    } catch (err) { res.status(500).json({ success: false, message: "Terjadi kesalahan server. Coba lagi nanti." }); }
});

app.post('/api/v1/point-transactions', authenticateToken, async (req, res) => {
    try {
        const { userId, points, action, referenceId } = req.body;
        if (!userId || points === undefined) {
            return res.status(400).json({ success: false, message: 'userId dan points wajib diisi.' });
        }
        const newPt = {
            userId, points: parseInt(points), action: action || 'UNKNOWN',
            referenceId: referenceId || null, timestamp: Date.now(),
            createdAt: new Date().toISOString()
        };
        const docRef = await db.collection('point_transactions').add(newPt);
        const userRef = db.collection('users').doc(userId);
        const userDoc = await userRef.get();
        if (userDoc.exists) {
            // ✅ SECURITY FIX: Cek cooldown spin (1x per hari, waktu server)
            const user = userDoc.data();
            const today = new Date().toISOString().split('T')[0];
            if (user.lastSpinDate === today) {
                return res.status(429).json({ success: false, message: 'Anda sudah spin hari ini. Coba lagi besok.' });
            }
            const currentPoints = userDoc.data().points || 0;
            await userRef.update({ points: currentPoints + parseInt(points), updatedAt: new Date().toISOString() });
        }
        res.status(201).json({ success: true, message: 'Poin ditambahkan.', data: { id: docRef.id, ...newPt } });
    } catch (err) { res.status(500).json({ success: false, message: "Terjadi kesalahan server. Coba lagi nanti." }); }
});

// ============ ANALYTICS ============

app.get('/api/v1/analytics/:userId/summary', authenticateToken, async (req, res) => {
    // ✅ SECURITY FIX: Validasi kepemilikan
    if (req.params.userId !== req.user.uid) {
        return res.status(403).json({ success: false, message: 'Akses ditolak.' });
    }
    try {
        const { userId } = req.params;
        const userDoc = await db.collection('users').doc(userId).get();
        if (!userDoc.exists) {
            return res.status(404).json({ success: false, message: 'User tidak ditemukan.' });
        }
        const user = userDoc.data();
        const txSnapshot = await db.collection('transactions')
            .where('userId', '==', userId)
            .get();
        let totalCommission = 0;
        let totalWithdrawal = 0;
        txSnapshot.docs.forEach(doc => {
            const d = doc.data();
            if (d.type === 'COMMISSION' || d.type === 'REFERRAL') totalCommission += d.amount || 0;
            if (d.type === 'WITHDRAWAL') totalWithdrawal += d.amount || 0;
        });
        res.json({
            success: true,
            data: {
                userId,
                balance: user.balance || 0,
                points: user.points || 0,
                tier: user.tier || 'FREE',
                todaySteps: user.todaySteps || 0,
                totalCommission,
                totalWithdrawal,
                netEarnings: totalCommission - totalWithdrawal
            }
        });
    } catch (err) { res.status(500).json({ success: false, message: "Terjadi kesalahan server. Coba lagi nanti." }); }
});

app.get('/api/v1/analytics/:userId/earnings', authenticateToken, async (req, res) => {
    // ✅ SECURITY FIX: Validasi kepemilikan
    if (req.params.userId !== req.user.uid) {
        return res.status(403).json({ success: false, message: 'Akses ditolak.' });
    }
    try {
        const { userId } = req.params;
        const days = parseInt(req.query.days) || 30;
        const since = Date.now() - (days * 24 * 60 * 60 * 1000);
        const snapshot = await db.collection('transactions')
            .where('userId', '==', userId)
            .where('timestamp', '>=', since)
            .get();
        const earnings = {};
        snapshot.docs.forEach(doc => {
            const d = doc.data();
            const date = new Date(d.timestamp).toISOString().split('T')[0];
            if (!earnings[date]) earnings[date] = 0;
            if (d.type === 'COMMISSION' || d.type === 'REFERRAL') {
                earnings[date] += d.amount || 0;
            }
        });
        const chartData = Object.entries(earnings).map(([date, amount]) => ({ date, amount })).sort((a, b) => a.date.localeCompare(b.date));
        res.json({ success: true, days, total: chartData.length, data: chartData });
    } catch (err) { res.status(500).json({ success: false, message: "Terjadi kesalahan server. Coba lagi nanti." }); }
});

app.get('/api/v1/analytics/:userId/referrals', authenticateToken, async (req, res) => {
    // ✅ SECURITY FIX: Validasi kepemilikan
    if (req.params.userId !== req.user.uid) {
        return res.status(403).json({ success: false, message: 'Akses ditolak.' });
    }
    try {
        const { userId } = req.params;
        const snapshot = await db.collection('users')
            .where('referredBy', '==', userId)
            .get();
        const referrals = snapshot.docs.map(doc => ({ id: doc.id, ...doc.data() }));
        const activeReferrals = referrals.filter(r => (r.points || 0) > 0).length;
        res.json({
            success: true,
            data: {
                totalReferrals: referrals.length,
                activeReferrals,
                referrals: referrals.map(r => ({ id: r.id, name: r.name, tier: r.tier, points: r.points, joinedAt: r.createdAt }))
            }
        });
    } catch (err) { res.status(500).json({ success: false, message: "Terjadi kesalahan server. Coba lagi nanti." }); }
});

app.get('/api/v1/analytics/global', authenticateToken, async (req, res) => {
    try {
        const [usersSnap, wdSnap, campSnap, misSnap] = await Promise.all([
            db.collection('users').get(),
            db.collection('withdrawals').get(),
            db.collection('campaigns').get(),
            db.collection('missions').get()
        ]);
        let totalBalance = 0, totalPoints = 0;
        usersSnap.docs.forEach(doc => {
            const d = doc.data();
            totalBalance += d.balance || 0;
            totalPoints += d.points || 0;
        });
        let totalWithdrawalAmount = 0, pendingWithdrawals = 0;
        wdSnap.docs.forEach(doc => {
            const d = doc.data();
            totalWithdrawalAmount += d.amount || 0;
            if (d.status === 'PENDING') pendingWithdrawals++;
        });
        res.json({
            success: true,
            data: {
                totalUsers: usersSnap.size,
                totalBalance,
                totalPoints,
                totalWithdrawals: wdSnap.size,
                totalWithdrawalAmount,
                pendingWithdrawals,
                totalCampaigns: campSnap.size,
                totalMissions: misSnap.size
            }
        });
    } catch (err) { res.status(500).json({ success: false, message: "Terjadi kesalahan server. Coba lagi nanti." }); }
});

// ============ NOTIFICATIONS ============

app.get('/api/v1/notifications/:userId', verifyFirebaseToken, async (req, res) => {
    // ✅ SECURITY FIX: Validasi kepemilikan
    if (req.params.userId !== req.firebaseUser.uid) {
        return res.status(403).json({ success: false, message: 'Akses ditolak.' });
    }
    try {
        const userId = req.firebaseUser.uid;  // ← UID dari token, bukan URL
        const limit = parseInt(req.query.limit) || 50;
        const snapshot = await db.collection('notifications')
            .where('userId', '==', userId)
            .orderBy('timestamp', 'desc')
            .limit(limit)
            .get();
        const result = snapshot.docs.map(doc => ({ id: doc.id, ...doc.data() }));
        res.json({ success: true, total: result.length, data: result });
    } catch (err) { res.status(500).json({ success: false, message: "Terjadi kesalahan server. Coba lagi nanti." }); }
});

app.get('/api/v1/notifications/:userId/unread', verifyFirebaseToken, async (req, res) => {
    // ✅ SECURITY FIX: Validasi kepemilikan
    if (req.params.userId !== req.firebaseUser.uid) {
        return res.status(403).json({ success: false, message: 'Akses ditolak.' });
    }
    try {
        const userId = req.firebaseUser.uid;  // ← UID dari token
        const snapshot = await db.collection('notifications')
            .where('userId', '==', userId)
            .where('read', '==', false)
            .get();
        res.json({ success: true, unread: snapshot.size });
    } catch (err) { res.status(500).json({ success: false, message: "Terjadi kesalahan server. Coba lagi nanti." }); }
});

app.post('/api/v1/notifications', verifyFirebaseToken, async (req, res) => {
    try {
        // ✅ SECURITY FIX: Ambil userId dari token, bukan dari body (mencegah spam)
        const userId = req.firebaseUser.uid;
        const { title, message, type, referenceId } = req.body;
        if (!title) {
            return res.status(400).json({ success: false, message: 'title wajib diisi.' });
        }
        const newNotif = {
            userId, title, message: message || '', type: type || 'INFO',
            referenceId: referenceId || null, read: false,
            timestamp: Date.now(), createdAt: new Date().toISOString()
        };
        const docRef = await db.collection('notifications').add(newNotif);
        res.status(201).json({ success: true, message: 'Notifikasi dikirim.', data: { id: docRef.id, ...newNotif } });
    } catch (err) { res.status(500).json({ success: false, message: "Terjadi kesalahan server. Coba lagi nanti." }); }
});

app.put('/api/v1/notifications/:id/read', verifyFirebaseToken, async (req, res) => {
    try {
        const docRef = db.collection('notifications').doc(req.params.id);
        const doc = await docRef.get();
        if (!doc.exists) return res.status(404).json({ success: false, message: 'Notifikasi tidak ditemukan.' });
        // ✅ SECURITY FIX: validasi kepemilikan
        if (doc.data().userId !== req.firebaseUser.uid) {
            return res.status(403).json({ success: false, message: 'Akses ditolak.' });
        }
        await docRef.update({ read: true, readAt: new Date().toISOString() });
        const updated = await docRef.get();
        res.json({ success: true, message: 'Notifikasi ditandai sudah dibaca.', data: { id: updated.id, ...updated.data() } });
    } catch (err) { res.status(500).json({ success: false, message: "Terjadi kesalahan server. Coba lagi nanti." }); }
});

app.put('/api/v1/notifications/:userId/read-all', verifyFirebaseToken, async (req, res) => {
    // ✅ SECURITY FIX: Validasi kepemilikan
    if (req.params.userId !== req.firebaseUser.uid) {
        return res.status(403).json({ success: false, message: 'Akses ditolak.' });
    }
    try {
        const userId = req.firebaseUser.uid;  // ← UID dari token
        const snapshot = await db.collection('notifications')
            .where('userId', '==', userId)
            .where('read', '==', false)
            .get();
        const batch = db.batch();
        snapshot.docs.forEach(doc => {
            batch.update(doc.ref, { read: true, readAt: new Date().toISOString() });
        });
        await batch.commit();
        res.json({ success: true, message: `${snapshot.size} notifikasi ditandai sudah dibaca.` });
    } catch (err) { res.status(500).json({ success: false, message: "Terjadi kesalahan server. Coba lagi nanti." }); }
});

app.delete('/api/v1/notifications/:id', verifyFirebaseToken, async (req, res) => {
    try {
        const docRef = db.collection('notifications').doc(req.params.id);
        const doc = await docRef.get();
        if (!doc.exists) return res.status(404).json({ success: false, message: 'Notifikasi tidak ditemukan.' });
        // ✅ SECURITY FIX: validasi kepemilikan
        if (doc.data().userId !== req.firebaseUser.uid) {
            return res.status(403).json({ success: false, message: 'Akses ditolak.' });
        }
        await docRef.delete();
        res.json({ success: true, message: 'Notifikasi dihapus.' });
    } catch (err) { res.status(500).json({ success: false, message: "Terjadi kesalahan server. Coba lagi nanti." }); }
});

// ============ GAME ROOM ============

app.post('/api/v1/games/spin', authenticateToken, async (req, res) => {
    try {
        const { userId } = req.body;
        if (!userId) return res.status(400).json({ success: false, message: 'userId wajib diisi.' });
        

        const prizes = [
            { label: '10 Poin', points: 10, weight: 40 },
            { label: '25 Poin', points: 25, weight: 30 },
            { label: '50 Poin', points: 50, weight: 15 },
            { label: '100 Poin', points: 100, weight: 10 },
            { label: '500 Poin', points: 500, weight: 4 },
            { label: 'Jackpot 1000 Poin', points: 1000, weight: 1 }
        ];
        const totalWeight = prizes.reduce((s, p) => s + p.weight, 0);
        let random = Math.random() * totalWeight;
        let selected = prizes[0];
        for (const p of prizes) {
            if (random < p.weight) { selected = p; break; }
            random -= p.weight;
        }
        const gameLog = {
            userId, gameType: 'SPIN', result: selected.label,
            pointsWon: selected.points, timestamp: Date.now(),
            createdAt: new Date().toISOString()
        };
        const docRef = await db.collection('game_logs').add(gameLog);
        const userRef = db.collection('users').doc(userId);
        const userDoc = await userRef.get();
        if (userDoc.exists) {
            const currentPoints = userDoc.data().points || 0;
            await userRef.update({ 
                points: currentPoints + selected.points, 
                lastSpinDate: new Date().toISOString().split('T')[0],
                updatedAt: new Date().toISOString() 
            });
        }
        res.json({ success: true, message: `Selamat! Anda mendapat ${selected.label}`, data: { id: docRef.id, ...gameLog } });
    } catch (err) { res.status(500).json({ success: false, message: "Terjadi kesalahan server. Coba lagi nanti." }); }
});

app.post('/api/v1/games/quiz/submit', authenticateToken, async (req, res) => {
    try {
        const { userId, quizId, answer, correctAnswer, rewardPoints } = req.body;
        if (!userId || !quizId) return res.status(400).json({ success: false, message: 'userId dan quizId wajib diisi.' });
        const isCorrect = answer === correctAnswer;
        const pointsWon = isCorrect ? (rewardPoints || 50) : 0;
        const gameLog = {
            userId, gameType: 'QUIZ', quizId, answer, isCorrect,
            pointsWon, timestamp: Date.now(), createdAt: new Date().toISOString()
        };
        const docRef = await db.collection('game_logs').add(gameLog);
        if (pointsWon > 0) {
            const userRef = db.collection('users').doc(userId);
            const userDoc = await userRef.get();
            if (userDoc.exists) {
                await userRef.update({ points: (userDoc.data().points || 0) + pointsWon, updatedAt: new Date().toISOString() });
            }
        }
        res.json({ success: true, isCorrect, pointsWon, message: isCorrect ? `Benar! +${pointsWon} poin` : 'Salah, coba lagi!', data: { id: docRef.id, ...gameLog } });
    } catch (err) { res.status(500).json({ success: false, message: "Terjadi kesalahan server. Coba lagi nanti." }); }
});

app.get('/api/v1/games/history/:userId', authenticateToken, async (req, res) => {
    // ✅ SECURITY FIX: Validasi kepemilikan
    if (req.params.userId !== req.user.uid) {
        return res.status(403).json({ success: false, message: 'Akses ditolak.' });
    }
    try {
        const { userId } = req.params;
        const limit = parseInt(req.query.limit) || 30;
        const snapshot = await db.collection('game_logs')
            .where('userId', '==', userId)
            .orderBy('timestamp', 'desc')
            .limit(limit)
            .get();
        const result = snapshot.docs.map(doc => ({ id: doc.id, ...doc.data() }));
        res.json({ success: true, total: result.length, data: result });
    } catch (err) { res.status(500).json({ success: false, message: "Terjadi kesalahan server. Coba lagi nanti." }); }
});

app.get('/api/v1/games/leaderboard', authenticateToken, async (req, res) => {
    try {
        const snapshot = await db.collection('game_logs').orderBy('pointsWon', 'desc').limit(20).get();
        const result = snapshot.docs.map(doc => ({ id: doc.id, ...doc.data() }));
        res.json({ success: true, total: result.length, data: result });
    } catch (err) { res.status(500).json({ success: false, message: "Terjadi kesalahan server. Coba lagi nanti." }); }
});

// ============ PROFILE SECURITY ============

app.get('/api/v1/profile/:userId', authenticateToken, async (req, res) => {
    try {
        // ✅ SECURITY FIX: Validasi kepemilikan
        if (req.params.userId !== req.user.uid) {
            return res.status(403).json({ success: false, message: 'Akses ditolak.' });
        }
        const doc = await db.collection('users').doc(req.params.userId).get();
        if (!doc.exists) return res.status(404).json({ success: false, message: 'User tidak ditemukan.' });
        const u = doc.data();
        delete u.pin; delete u.password;
        res.json({ success: true, data: { id: doc.id, ...u } });
    } catch (err) { res.status(500).json({ success: false, message: "Terjadi kesalahan server. Coba lagi nanti." }); }
});

app.put('/api/v1/profile/:userId/update', authenticateToken, async (req, res) => {
    try {
        // ✅ SECURITY FIX: Validasi kepemilikan
        if (req.params.userId !== req.user.uid) {
            return res.status(403).json({ success: false, message: 'Akses ditolak.' });
        }
        const docRef = db.collection('users').doc(req.params.userId);
        const doc = await docRef.get();
        if (!doc.exists) return res.status(404).json({ success: false, message: 'User tidak ditemukan.' });
        const allowed = ['name', 'email', 'phone', 'city', 'avatarEmoji'];
        const updateData = {};
        allowed.forEach(k => { if (req.body[k] !== undefined) updateData[k] = req.body[k]; });
        updateData.updatedAt = new Date().toISOString();
        await docRef.update(updateData);
        const updated = await docRef.get();
        res.json({ success: true, message: 'Profil diupdate.', data: { id: updated.id, ...updated.data() } });
    } catch (err) { res.status(500).json({ success: false, message: "Terjadi kesalahan server. Coba lagi nanti." }); }
});

app.put('/api/v1/profile/:userId/change-pin', authenticateToken, async (req, res) => {
    try {
        // ✅ SECURITY FIX: Validasi kepemilikan
        if (req.params.userId !== req.user.uid) {
            return res.status(403).json({ success: false, message: 'Akses ditolak.' });
        }
        const { oldPin, newPin } = req.body;
        if (!newPin || newPin.length < 4) return res.status(400).json({ success: false, message: 'PIN baru minimal 4 digit.' });
        const docRef = db.collection('users').doc(req.params.userId);
        const doc = await docRef.get();
        if (!doc.exists) return res.status(404).json({ success: false, message: 'User tidak ditemukan.' });
        const userData = doc.data();
        if (userData.pin && userData.pin !== oldPin) {
            return res.status(403).json({ success: false, message: 'PIN lama salah.' });
        }
        await docRef.update({ pin: newPin, pinUpdatedAt: new Date().toISOString(), updatedAt: new Date().toISOString() });
        await db.collection('security_logs').add({
            userId: req.params.userId, action: 'CHANGE_PIN',
            timestamp: Date.now(), createdAt: new Date().toISOString()
        });
        res.json({ success: true, message: 'PIN berhasil diubah.' });
    } catch (err) { res.status(500).json({ success: false, message: "Terjadi kesalahan server. Coba lagi nanti." }); }
});

app.post('/api/v1/profile/:userId/verify-pin', authenticateToken, async (req, res) => {
    try {
        // ✅ SECURITY FIX: Validasi kepemilikan
        if (req.params.userId !== req.user.uid) {
            return res.status(403).json({ success: false, message: 'Akses ditolak.' });
        }
        const { pin } = req.body;
        const doc = await db.collection('users').doc(req.params.userId).get();
        if (!doc.exists) return res.status(404).json({ success: false, message: 'User tidak ditemukan.' });
        const userData = doc.data();
        const isValid = !userData.pin || userData.pin === pin;
        await db.collection('security_logs').add({
            userId: req.params.userId, action: isValid ? 'VERIFY_PIN_SUCCESS' : 'VERIFY_PIN_FAILED',
            timestamp: Date.now(), createdAt: new Date().toISOString()
        });
        res.json({ success: true, isValid });
    } catch (err) { res.status(500).json({ success: false, message: "Terjadi kesalahan server. Coba lagi nanti." }); }
});

app.get('/api/v1/profile/:userId/security-log', authenticateToken, async (req, res) => {
    try {
        // ✅ SECURITY FIX: Validasi kepemilikan
        if (req.params.userId !== req.user.uid) {
            return res.status(403).json({ success: false, message: 'Akses ditolak.' });
        }
        const limit = parseInt(req.query.limit) || 30;
        const snapshot = await db.collection('security_logs')
            .where('userId', '==', req.params.userId)
            .orderBy('timestamp', 'desc')
            .limit(limit)
            .get();
        const result = snapshot.docs.map(doc => ({ id: doc.id, ...doc.data() }));
        res.json({ success: true, total: result.length, data: result });
    } catch (err) { res.status(500).json({ success: false, message: "Terjadi kesalahan server. Coba lagi nanti." }); }
});

// ============ ADMIN AUTHENTICATION ============

const ADMIN_PASSWORD = process.env.ADMIN_PASSWORD || 'royaltree_admin_2026';
if (!global.__adminTokens) global.__adminTokens = new Map();
const activeSessions = global.__adminTokens; // token -> { createdAt, expiresAt }

function generateToken() {
    return 'adm_' + Math.random().toString(36).substring(2) + Date.now().toString(36);
}

// POST /admin/login - Login admin
app.post('/api/v1/admin/login', async (req, res) => {
    try {
        const { password } = req.body;
        if (!password) {
            return res.status(400).json({ success: false, message: 'Password wajib diisi.' });
        }
        if (password !== ADMIN_PASSWORD) {
            return res.status(401).json({ success: false, message: 'Password salah.' });
        }
        const token = generateToken();
        const expiresAt = Date.now() + (24 * 60 * 60 * 1000); // 24 jam
        activeSessions.set(token, { createdAt: Date.now(), expiresAt });
        res.json({ success: true, message: 'Login berhasil.', token, expiresAt });
    } catch (err) { res.status(500).json({ success: false, message: "Terjadi kesalahan server. Coba lagi nanti." }); }
});

// POST /admin/logout - Logout admin
app.post('/api/v1/admin/logout', (req, res) => {
    const token = req.headers['x-admin-token'];
    if (token) activeSessions.delete(token);
    res.json({ success: true, message: 'Logout berhasil.' });
});

// GET /admin/verify - Cek token masih valid
app.get('/api/v1/admin/verify', (req, res) => {
    const token = req.headers['x-admin-token'];
    if (!token || !activeSessions.has(token)) {
        return res.status(401).json({ success: false, message: 'Token tidak valid.' });
    }
    const session = activeSessions.get(token);
    if (Date.now() > session.expiresAt) {
        activeSessions.delete(token);
        return res.status(401).json({ success: false, message: 'Token kadaluarsa.' });
    }
    res.json({ success: true, message: 'Token valid.' });
});

// Middleware untuk endpoint admin (opsional, bisa dipakai nanti)
function requireAdmin(req, res, next) {
    const token = req.headers['x-admin-token'];
    if (!token || !activeSessions.has(token)) {
        return res.status(401).json({ success: false, message: 'Akses ditolak. Login dulu.' });
    }
    const session = activeSessions.get(token);
    if (Date.now() > session.expiresAt) {
        activeSessions.delete(token);
        return res.status(401).json({ success: false, message: 'Token kadaluarsa.' });
    }
    next();
}
// ============================================================
// CRON JOB — Release pending affiliate commission (setiap 5 menit)
// ============================================================
setInterval(async () => {
    try {
        const result = await releasePendingCommission();
        if (result.released > 0) {
            logger.info(`[CRON] Released ${result.released} pending commissions`);
        }
    } catch (error) {
        logger.error('[CRON] releasePendingCommission error:', error.message);
    }
}, 5 * 60 * 1000); // 5 menit

// Jalankan sekali saat server start (biar yang dah expired langsung cair)
setTimeout(async () => {
    try {
        await releasePendingCommission();
        logger.info('[CRON] Initial release check done');
    } catch (error) {
        logger.error('[CRON] Initial release error:', error.message);
    }
}, 10 * 1000); // 10 detik setelah start

// Load anomaly flags dari Firestore (persist across restart)
anomaly.loadUserFlags().catch(e => logger.error('[STARTUP] loadUserFlags:', e.message));

// Auto-save anomaly flags setiap 5 menit
setInterval(() => {
    anomaly.saveAllFlags().catch(e => logger.error('[CRON] saveAllFlags:', e.message));
}, 5 * 60 * 1000);

// ============================================================
// CRON: Scheduled Notifications — cek setiap 60 detik
// ============================================================
setInterval(async () => {
    try {
        const now = Date.now();
        const snapshot = await db.collection('scheduled_notifications')
            .where('status', '==', 'PENDING')
            .where('scheduledAt', '<=', now)
            .limit(10)
            .get();

        if (snapshot.empty) return;

        for (const doc of snapshot.docs) {
            const data = doc.data();
            try {
                let usersSnapshot;
                if (data.targetUid) {
                    const singleDoc = await db.collection('users').doc(data.targetUid).get();
                    if (!singleDoc.exists) {
                        await doc.ref.update({ status: 'FAILED', error: 'User tidak ditemukan', sentAt: Date.now() });
                        continue;
                    }
                    usersSnapshot = { forEach: (cb) => cb(singleDoc), size: 1 };
                } else {
                    usersSnapshot = await db.collection('users').get();
                }

                const batch = db.batch();
                let count = 0;
                const fcmTokens = [];

                usersSnapshot.forEach(userDoc => {
                    const userData = userDoc.data();
                    const notifRef = db.collection('notifications').doc();
                    batch.set(notifRef, {
                        userId: userDoc.id,
                        title: data.title,
                        message: data.message,
                        type: data.type || 'BROADCAST',
                        timestamp: Date.now(),
                        createdAt: new Date().toISOString(),
                        read: false,
                        scheduled: true
                    });
                    if (userData.fcmToken) fcmTokens.push(userData.fcmToken);
                    count++;
                });
                await batch.commit();

                let fcmSent = 0;
                if (fcmTokens.length > 0) {
                    try {
                        const fcmService = require('./services/fcm');
                        await fcmService.sendBatchPush(
                            fcmTokens, data.title, data.message, { type: data.type || 'BROADCAST' }
                        );
                        fcmSent = fcmTokens.length;
                    } catch (e) {
                        logger.error('[SCHEDULED] FCM error:', e.message);
                    }
                }

                await doc.ref.update({
                    status: 'SENT',
                    sentAt: Date.now(),
                    sentCount: count,
                    fcmSent
                });

                logger.info(`[SCHEDULED] Sent "${data.title}" to ${count} users (${fcmSent} FCM)`);
            } catch (e) {
                // Enhanced error logging
                const errMsg = e?.message || e?.toString() || JSON.stringify(e) || 'Unknown error';
                logger.error('[SCHEDULED] Send error:', {
                    message: errMsg,
                    name: e?.name,
                    code: e?.code,
                    docId: doc?.id
                });
                try {
                    await doc.ref.update({ 
                        status: 'FAILED', 
                        error: errMsg, 
                        sentAt: Date.now() 
                    });
                } catch (updateErr) {
                    logger.error('[SCHEDULED] Failed to update status:', {
                        message: updateErr?.message || 'Unknown',
                        docId: doc?.id
                    });
                }
            }
        }
    } catch (err) {
        // Enhanced cron-level error logging
        const cronErrMsg = err?.message 
            || err?.toString() 
            || err?.code 
            || JSON.stringify(err)
            || 'Unknown cron error';
        
        logger.error('[SCHEDULED-CRON] Error:', {
            message: cronErrMsg,
            name: err?.name,
            code: err?.code,
            stack: err?.stack?.split('\n').slice(0, 3).join(' | ')
        });
    }
}, 60 * 1000);

// ============================================================
// GLOBAL ERROR HANDLERS
// ============================================================
process.on('uncaughtException', (err) => {
    logger.error('UNCAUGHT EXCEPTION', {
        error: err.message,
        stack: err.stack,
        type: 'uncaughtException'
    });
});

process.on('unhandledRejection', (reason, promise) => {
    logger.error('UNHANDLED REJECTION', {
        reason: String(reason),
        stack: reason?.stack || '',
        type: 'unhandledRejection'
    });
});

// Express error middleware (WAJIB di paling akhir sebelum listen)
app.use((err, req, res, next) => {
    logger.error('Express error', {
        error: err.message,
        stack: err.stack,
        path: req.path,
        method: req.method,
        ip: req.ip,
        userId: req.firebaseUser?.uid || 'anonymous'
    });
    if (res.headersSent) {
        return next(err);
    }
    res.status(500).json({
        success: false,
        message: process.env.NODE_ENV === 'production'
            ? 'Internal server error'
            : err.message
    });
});

app.listen(PORT, () => {
    logger.info(`====================================================`);
    logger.info(` Royaltree Portal API Server aktif di port ${PORT}`);
    logger.info(` Database: Firestore`);
    logger.info(` Endpoint: http://localhost:${PORT}/api/v1`);
    logger.info(` Health:   http://localhost:${PORT}/api/v1/health`);
    logger.info(`====================================================`);

    // Start sweepstakes auto-draw job (cek setiap 1 jam)
    sweepstakesJob.start();
});

module.exports = app;
module.exports.createNotification = createNotification;
