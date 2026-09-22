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
const cors = require('cors');
const admin = require('firebase-admin');

const app = express();
const PORT = process.env.PORT || 3000;

app.use(cors());

// Serve admin panel
app.use('/admin', express.static('/root/Royaltree/admin'));
app.use(express.json());

// Serve admin panel
app.use('/admin', express.static('/root/Royaltree/admin'));

// -------------------------------------------------------------
// INISIALISASI FIREBASE ADMIN SDK
// -------------------------------------------------------------
if (!admin.apps.length) {
    admin.initializeApp({
        credential: admin.credential.applicationDefault()
    });
}

const db = admin.firestore();

// Kunci Otentikasi Admin Portal
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
// ENDPOINTS
// -------------------------------------------------------------

// 1. Health Check
app.get('/api/v1/health', (req, res) => {
    res.json({
        success: true,
        status: "ONLINE",
        message: "Royaltree Portal API Server berjalan dengan normal.",
        timestamp: Date.now(),
        serverTime: new Date().toISOString(),
        database: "Firestore"
    });
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
        res.status(500).json({ success: false, message: err.message });
    }
});

app.put('/api/v1/config', authenticateToken, async (req, res) => {
    try {
        await db.collection('system').doc('config').set(req.body, { merge: true });
        const doc = await db.collection('system').doc('config').get();
        res.json({
            success: true,
            message: "Konfigurasi sistem portal berhasil diperbarui.",
            data: doc.data()
        });
    } catch (err) {
        res.status(500).json({ success: false, message: err.message });
    }
});

// 3. User Data Synchronization
app.post('/api/v1/users/sync', authenticateToken, async (req, res) => {
    try {
        const incomingUser = req.body;
        if (!incomingUser || !incomingUser.id) {
            return res.status(400).json({ success: false, message: "Data pengguna tidak valid (id wajib disertakan)." });
        }

        const userData = {
            ...incomingUser,
            updatedAt: new Date().toISOString()
        };

        await db.collection('users').doc(incomingUser.id).set(userData, { merge: true });

        res.json({
            success: true,
            message: "Data pengguna berhasil disinkronkan ke portal.",
            serverSyncedAt: Date.now(),
            user: userData
        });
    } catch (err) {
        res.status(500).json({ success: false, message: err.message });
    }
});

// 4. Withdrawals
app.get('/api/v1/withdrawals', authenticateToken, async (req, res) => {
    try {
        const statusFilter = req.query.status;
        let query = db.collection('withdrawals');
        if (statusFilter) {
            query = query.where('status', '==', statusFilter);
        }
        const snapshot = await query.get();
        const result = snapshot.docs.map(doc => ({ id: doc.id, ...doc.data() }));
        res.json({ success: true, total: result.length, data: result });
    } catch (err) {
        res.status(500).json({ success: false, message: err.message });
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
        res.status(201).json({
            success: true,
            message: "Permintaan penarikan dana berhasil diterima portal.",
            data: newWd
        });
    } catch (err) {
        res.status(500).json({ success: false, message: err.message });
    }
});

app.put('/api/v1/withdrawals/:id/status', authenticateToken, async (req, res) => {
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
        res.status(500).json({ success: false, message: err.message });
    }
});

// 5. Campaigns
app.get('/api/v1/campaigns', async (req, res) => {
    try {
        const snapshot = await db.collection('campaigns').get();
        const result = snapshot.docs.map(doc => ({ id: doc.id, ...doc.data() }));
        res.json({ success: true, total: result.length, data: result });
    } catch (err) {
        res.status(500).json({ success: false, message: err.message });
    }
});

app.post('/api/v1/campaigns', authenticateToken, async (req, res) => {
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
        res.status(500).json({ success: false, message: err.message });
    }
});

// 6. Missions
app.get('/api/v1/missions', async (req, res) => {
    try {
        const snapshot = await db.collection('missions').get();
        const result = snapshot.docs.map(doc => ({ id: doc.id, ...doc.data() }));
        res.json({ success: true, total: result.length, data: result });
    } catch (err) {
        res.status(500).json({ success: false, message: err.message });
    }
});

// 7. Postback Webhook
app.get('/api/v1/postback', async (req, res) => {
    try {
        const { user_id, reward_points, tx_id, secret } = req.query;
        console.log(`[POSTBACK WEBHOOK] User ${user_id} mendapat ${reward_points} poin. TxId: ${tx_id}`);

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
        console.error('Postback error:', err);
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
        res.status(500).json({ success: false, message: err.message });
    }
});

// 9. POST Missions (Tambah Misi)
app.post('/api/v1/missions', authenticateToken, async (req, res) => {
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
        res.status(500).json({ success: false, message: err.message });
    }
});

// 10. GET Single User
app.get('/api/v1/users/:id', authenticateToken, async (req, res) => {
    try {
        const doc = await db.collection('users').doc(req.params.id).get();
        if (!doc.exists) {
            return res.status(404).json({ success: false, message: "User tidak ditemukan." });
        }
        res.json({ success: true, data: { id: doc.id, ...doc.data() } });
    } catch (err) {
        res.status(500).json({ success: false, message: err.message });
    }
});


// PUT User
app.put('/api/v1/users/:id', authenticateToken, async (req, res) => {
    try {
        const docRef = db.collection('users').doc(req.params.id);
        const doc = await docRef.get();
        if (!doc.exists) return res.status(404).json({ success: false, message: 'User tidak ditemukan.' });
        await docRef.update({ ...req.body, updatedAt: new Date().toISOString() });
        const updated = await docRef.get();
        res.json({ success: true, message: 'User diupdate.', data: { id: updated.id, ...updated.data() } });
    } catch (err) { res.status(500).json({ success: false, message: err.message }); }
});

// DELETE User
app.delete('/api/v1/users/:id', authenticateToken, async (req, res) => {
    try {
        const docRef = db.collection('users').doc(req.params.id);
        const doc = await docRef.get();
        if (!doc.exists) return res.status(404).json({ success: false, message: 'User tidak ditemukan.' });
        await docRef.delete();
        res.json({ success: true, message: 'User dihapus.' });
    } catch (err) { res.status(500).json({ success: false, message: err.message }); }
});

// PUT Campaign
app.put('/api/v1/campaigns/:id', authenticateToken, async (req, res) => {
    try {
        const docRef = db.collection('campaigns').doc(req.params.id);
        const doc = await docRef.get();
        if (!doc.exists) return res.status(404).json({ success: false, message: 'Kampanye tidak ditemukan.' });
        await docRef.update(req.body);
        const updated = await docRef.get();
        res.json({ success: true, message: 'Kampanye diupdate.', data: { id: updated.id, ...updated.data() } });
    } catch (err) { res.status(500).json({ success: false, message: err.message }); }
});

// DELETE Campaign
app.delete('/api/v1/campaigns/:id', authenticateToken, async (req, res) => {
    try {
        const docRef = db.collection('campaigns').doc(req.params.id);
        const doc = await docRef.get();
        if (!doc.exists) return res.status(404).json({ success: false, message: 'Kampanye tidak ditemukan.' });
        await docRef.delete();
        res.json({ success: true, message: 'Kampanye dihapus.' });
    } catch (err) { res.status(500).json({ success: false, message: err.message }); }
});

// PUT Mission
app.put('/api/v1/missions/:id', authenticateToken, async (req, res) => {
    try {
        const docRef = db.collection('missions').doc(req.params.id);
        const doc = await docRef.get();
        if (!doc.exists) return res.status(404).json({ success: false, message: 'Misi tidak ditemukan.' });
        await docRef.update(req.body);
        const updated = await docRef.get();
        res.json({ success: true, message: 'Misi diupdate.', data: { id: updated.id, ...updated.data() } });
    } catch (err) { res.status(500).json({ success: false, message: err.message }); }
});

// DELETE Mission
app.delete('/api/v1/missions/:id', authenticateToken, async (req, res) => {
    try {
        const docRef = db.collection('missions').doc(req.params.id);
        const doc = await docRef.get();
        if (!doc.exists) return res.status(404).json({ success: false, message: 'Misi tidak ditemukan.' });
        await docRef.delete();
        res.json({ success: true, message: 'Misi dihapus.' });
    } catch (err) { res.status(500).json({ success: false, message: err.message }); }
});

// DELETE Withdrawal
app.delete('/api/v1/withdrawals/:id', authenticateToken, async (req, res) => {
    try {
        const docRef = db.collection('withdrawals').doc(req.params.id);
        const doc = await docRef.get();
        if (!doc.exists) return res.status(404).json({ success: false, message: 'Penarikan tidak ditemukan.' });
        await docRef.delete();
        res.json({ success: true, message: 'Penarikan dihapus.' });
    } catch (err) { res.status(500).json({ success: false, message: err.message }); }
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
    } catch (err) { res.status(500).json({ success: false, message: err.message }); }
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
    } catch (err) { res.status(500).json({ success: false, message: err.message }); }
});

app.get('/api/v1/steps/today/:userId', authenticateToken, async (req, res) => {
    try {
        const { userId } = req.params;
        const today = new Date().toISOString().split('T')[0];
        const docId = `${userId}_${today}`;
        const doc = await db.collection('step_logs').doc(docId).get();
        if (!doc.exists) {
            return res.json({ success: true, data: { userId, date: today, steps: 0 } });
        }
        res.json({ success: true, data: { id: doc.id, ...doc.data() } });
    } catch (err) { res.status(500).json({ success: false, message: err.message }); }
});

app.get('/api/v1/steps/history/:userId', authenticateToken, async (req, res) => {
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
    } catch (err) { res.status(500).json({ success: false, message: err.message }); }
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
    } catch (err) { res.status(500).json({ success: false, message: err.message }); }
});

// ============ HISTORY ============

app.get('/api/v1/history/:userId', authenticateToken, async (req, res) => {
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
    } catch (err) { res.status(500).json({ success: false, message: err.message }); }
});

app.get('/api/v1/history/:userId/type/:type', authenticateToken, async (req, res) => {
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
    } catch (err) { res.status(500).json({ success: false, message: err.message }); }
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
    } catch (err) { res.status(500).json({ success: false, message: err.message }); }
});

app.get('/api/v1/point-transactions/:userId', authenticateToken, async (req, res) => {
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
    } catch (err) { res.status(500).json({ success: false, message: err.message }); }
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
            const currentPoints = userDoc.data().points || 0;
            await userRef.update({ points: currentPoints + parseInt(points), updatedAt: new Date().toISOString() });
        }
        res.status(201).json({ success: true, message: 'Poin ditambahkan.', data: { id: docRef.id, ...newPt } });
    } catch (err) { res.status(500).json({ success: false, message: err.message }); }
});

// ============ ANALYTICS ============

app.get('/api/v1/analytics/:userId/summary', authenticateToken, async (req, res) => {
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
    } catch (err) { res.status(500).json({ success: false, message: err.message }); }
});

app.get('/api/v1/analytics/:userId/earnings', authenticateToken, async (req, res) => {
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
    } catch (err) { res.status(500).json({ success: false, message: err.message }); }
});

app.get('/api/v1/analytics/:userId/referrals', authenticateToken, async (req, res) => {
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
    } catch (err) { res.status(500).json({ success: false, message: err.message }); }
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
    } catch (err) { res.status(500).json({ success: false, message: err.message }); }
});

// ============ NOTIFICATIONS ============

app.get('/api/v1/notifications/:userId', authenticateToken, async (req, res) => {
    try {
        const { userId } = req.params;
        const limit = parseInt(req.query.limit) || 50;
        const snapshot = await db.collection('notifications')
            .where('userId', '==', userId)
            .orderBy('timestamp', 'desc')
            .limit(limit)
            .get();
        const result = snapshot.docs.map(doc => ({ id: doc.id, ...doc.data() }));
        res.json({ success: true, total: result.length, data: result });
    } catch (err) { res.status(500).json({ success: false, message: err.message }); }
});

app.get('/api/v1/notifications/:userId/unread', authenticateToken, async (req, res) => {
    try {
        const { userId } = req.params;
        const snapshot = await db.collection('notifications')
            .where('userId', '==', userId)
            .where('read', '==', false)
            .get();
        res.json({ success: true, unread: snapshot.size });
    } catch (err) { res.status(500).json({ success: false, message: err.message }); }
});

app.post('/api/v1/notifications', authenticateToken, async (req, res) => {
    try {
        const { userId, title, message, type, referenceId } = req.body;
        if (!userId || !title) {
            return res.status(400).json({ success: false, message: 'userId dan title wajib diisi.' });
        }
        const newNotif = {
            userId, title, message: message || '', type: type || 'INFO',
            referenceId: referenceId || null, read: false,
            timestamp: Date.now(), createdAt: new Date().toISOString()
        };
        const docRef = await db.collection('notifications').add(newNotif);
        res.status(201).json({ success: true, message: 'Notifikasi dikirim.', data: { id: docRef.id, ...newNotif } });
    } catch (err) { res.status(500).json({ success: false, message: err.message }); }
});

app.put('/api/v1/notifications/:id/read', authenticateToken, async (req, res) => {
    try {
        const docRef = db.collection('notifications').doc(req.params.id);
        const doc = await docRef.get();
        if (!doc.exists) return res.status(404).json({ success: false, message: 'Notifikasi tidak ditemukan.' });
        await docRef.update({ read: true, readAt: new Date().toISOString() });
        const updated = await docRef.get();
        res.json({ success: true, message: 'Notifikasi ditandai sudah dibaca.', data: { id: updated.id, ...updated.data() } });
    } catch (err) { res.status(500).json({ success: false, message: err.message }); }
});

app.put('/api/v1/notifications/:userId/read-all', authenticateToken, async (req, res) => {
    try {
        const { userId } = req.params;
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
    } catch (err) { res.status(500).json({ success: false, message: err.message }); }
});

app.delete('/api/v1/notifications/:id', authenticateToken, async (req, res) => {
    try {
        const docRef = db.collection('notifications').doc(req.params.id);
        const doc = await docRef.get();
        if (!doc.exists) return res.status(404).json({ success: false, message: 'Notifikasi tidak ditemukan.' });
        await docRef.delete();
        res.json({ success: true, message: 'Notifikasi dihapus.' });
    } catch (err) { res.status(500).json({ success: false, message: err.message }); }
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
            await userRef.update({ points: currentPoints + selected.points, updatedAt: new Date().toISOString() });
        }
        res.json({ success: true, message: `Selamat! Anda mendapat ${selected.label}`, data: { id: docRef.id, ...gameLog } });
    } catch (err) { res.status(500).json({ success: false, message: err.message }); }
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
    } catch (err) { res.status(500).json({ success: false, message: err.message }); }
});

app.get('/api/v1/games/history/:userId', authenticateToken, async (req, res) => {
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
    } catch (err) { res.status(500).json({ success: false, message: err.message }); }
});

app.get('/api/v1/games/leaderboard', authenticateToken, async (req, res) => {
    try {
        const snapshot = await db.collection('game_logs').orderBy('pointsWon', 'desc').limit(20).get();
        const result = snapshot.docs.map(doc => ({ id: doc.id, ...doc.data() }));
        res.json({ success: true, total: result.length, data: result });
    } catch (err) { res.status(500).json({ success: false, message: err.message }); }
});

// ============ PROFILE SECURITY ============

app.get('/api/v1/profile/:userId', authenticateToken, async (req, res) => {
    try {
        const doc = await db.collection('users').doc(req.params.userId).get();
        if (!doc.exists) return res.status(404).json({ success: false, message: 'User tidak ditemukan.' });
        const u = doc.data();
        delete u.pin; delete u.password;
        res.json({ success: true, data: { id: doc.id, ...u } });
    } catch (err) { res.status(500).json({ success: false, message: err.message }); }
});

app.put('/api/v1/profile/:userId/update', authenticateToken, async (req, res) => {
    try {
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
    } catch (err) { res.status(500).json({ success: false, message: err.message }); }
});

app.put('/api/v1/profile/:userId/change-pin', authenticateToken, async (req, res) => {
    try {
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
    } catch (err) { res.status(500).json({ success: false, message: err.message }); }
});

app.post('/api/v1/profile/:userId/verify-pin', authenticateToken, async (req, res) => {
    try {
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
    } catch (err) { res.status(500).json({ success: false, message: err.message }); }
});

app.get('/api/v1/profile/:userId/security-log', authenticateToken, async (req, res) => {
    try {
        const limit = parseInt(req.query.limit) || 30;
        const snapshot = await db.collection('security_logs')
            .where('userId', '==', req.params.userId)
            .orderBy('timestamp', 'desc')
            .limit(limit)
            .get();
        const result = snapshot.docs.map(doc => ({ id: doc.id, ...doc.data() }));
        res.json({ success: true, total: result.length, data: result });
    } catch (err) { res.status(500).json({ success: false, message: err.message }); }
});

// ============ ADMIN AUTHENTICATION ============

const ADMIN_PASSWORD = process.env.ADMIN_PASSWORD || 'royaltree_admin_2026';
const activeSessions = new Map(); // token -> { createdAt, expiresAt }

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
    } catch (err) { res.status(500).json({ success: false, message: err.message }); }
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
app.listen(PORT, () => {
    console.log(`====================================================`);
    console.log(` Royaltree Portal API Server aktif di port ${PORT}`);
    console.log(` Database: Firestore`);
    console.log(` Endpoint: http://localhost:${PORT}/api/v1`);
    console.log(` Health:   http://localhost:${PORT}/api/v1/health`);
    console.log(`====================================================`);
});
