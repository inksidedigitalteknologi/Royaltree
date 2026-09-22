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
app.listen(PORT, () => {
    console.log(`====================================================`);
    console.log(` Royaltree Portal API Server aktif di port ${PORT}`);
    console.log(` Database: Firestore`);
    console.log(` Endpoint: http://localhost:${PORT}/api/v1`);
    console.log(` Health:   http://localhost:${PORT}/api/v1/health`);
    console.log(`====================================================`);
});
