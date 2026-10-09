// routes/admin.js
// Endpoint admin: atur config daily reward & ad vendors

const express = require('express');
const router = express.Router();
const admin = require('firebase-admin');
const { verifyFirebaseToken } = require('../middleware/firebaseAuth');

const db = admin.firestore();

// ============================================================
// Middleware: cek admin (terima 2 jenis auth)
// 1. x-admin-token (dari web panel)
// 2. Firebase ID token + role ADMIN (dari Android)
// ============================================================

// Simpan token admin web dalam memory (untuk verify)
// Boleh guna global map yang di-share dengan server.js
if (!global.__adminTokens) global.__adminTokens = new Map();

async function checkAdmin(req, res, next) {
    try {
        // Opsyen 1: x-admin-token (web panel)
        const adminToken = req.headers['x-admin-token'];
        if (adminToken && global.__adminTokens.has(adminToken)) {
            const tokenData = global.__adminTokens.get(adminToken);
            if (tokenData.expiresAt > Date.now()) {
                req.adminUser = { source: 'web', token: adminToken };
                return next();
            } else {
                global.__adminTokens.delete(adminToken);
            }
        }

        // Opsyen 2: Firebase ID token
        if (req.firebaseUser) {
            const { uid } = req.firebaseUser;
            const userDoc = await db.collection('users').doc(uid).get();
            if (userDoc.exists && (userDoc.data().role === 'ADMIN' || userDoc.data().role === 'admin')) {
                req.adminUser = { source: 'firebase', uid };
                return next();
            }
        }

        return res.status(403).json({
            success: false,
            message: 'Akses admin diperlukan.'
        });
    } catch (error) {
        return res.status(500).json({ success: false, message: error.message });
    }
}

// ============================================================
// GET /api/v1/admin/daily-config
// ============================================================
router.get('/daily-config', checkAdmin, async (req, res) => {
    try {
        const doc = await db.collection('system').doc('daily_reward_config').get();
        const config = doc.exists ? doc.data() : {
            weekdayPoints: 50,
            weekendPoints: 100,
            specialDays: {},
            streakRecovery: {
                enabled: true,
                maxDaysBack: 7,
                maxAdsPerDay: 1,
                pointCostPerDay: 500,
                allowPointRecovery: true,
                allowAdRecovery: true,
                resetIfExceeded: true
            }
        };
        return res.json({ success: true, data: config });
    } catch (error) {
        return res.status(500).json({ success: false, message: error.message });
    }
});

// ============================================================
// PUT /api/v1/admin/daily-config
// ============================================================
router.put('/daily-config', checkAdmin, async (req, res) => {
    try {
        const config = req.body;
        await db.collection('system').doc('daily_reward_config').set({
            ...config,
            updatedAt: admin.firestore.FieldValue.serverTimestamp(),
            updatedBy: req.adminUser?.uid || 'admin_web'
        }, { merge: true });

        return res.json({
            success: true,
            message: 'Config berhasil disimpan.'
        });
    } catch (error) {
        return res.status(500).json({ success: false, message: error.message });
    }
});

// ============================================================
// GET /api/v1/admin/ad-config
// ============================================================
router.get('/ad-config', checkAdmin, async (req, res) => {
    try {
        const doc = await db.collection('system').doc('ad_config').get();
        return res.json({
            success: true,
            data: doc.exists ? doc.data() : {}
        });
    } catch (error) {
        return res.status(500).json({ success: false, message: error.message });
    }
});

// ============================================================
// PUT /api/v1/admin/ad-config
// ============================================================
router.put('/ad-config', checkAdmin, async (req, res) => {
    try {
        await db.collection('system').doc('ad_config').set({
            ...req.body,
            updatedAt: admin.firestore.FieldValue.serverTimestamp(),
            updatedBy: req.adminUser?.uid || 'admin_web'
        }, { merge: true });

        return res.json({
            success: true,
            message: 'Config ads berhasil disimpan.'
        });
    } catch (error) {
        return res.status(500).json({ success: false, message: error.message });
    }
});

// ============================================================
// GET /api/v1/admin/audit-logs
// ============================================================
router.get('/audit-logs', checkAdmin, async (req, res) => {
    try {
        const limit = parseInt(req.query.limit) || 50;
        const snapshot = await db.collection('audit_logs')
            .orderBy('timestamp', 'desc')
            .limit(limit)
            .get();

        const logs = snapshot.docs.map(d => ({ id: d.id, ...d.data() }));
        return res.json({ success: true, data: logs });
    } catch (error) {
        return res.status(500).json({ success: false, message: error.message });
    }
});

// ============================================================
// GET /api/v1/admin/game-config
// Ambil config game (mining, exchange, rate limit, bonus)
// ============================================================
router.get('/game-config', checkAdmin, async (req, res) => {
    try {
        const configDoc = await db.collection('app_config').doc('game_config').get();
        const config = configDoc.exists ? configDoc.data() : {};
        
        const rateDoc = await db.collection('app_config').doc('exchange_rates').get();
        const rate = rateDoc.exists ? rateDoc.data() : {};
        
        return res.json({
            success: true,
            data: {
                // Exchange
                usdToIdr: rate.usdToIdr || config.usdToIdr || 15000,
                rtpToUsd: rate.rtpToUsd || config.rtpToUsd || 500,
                rtpToIdr: rate.rtpToIdr || (rate.usdToIdr / rate.rtpToUsd) || 30,
                
                // Mining
                miningRatePerGhs: config.miningRatePerGhs || 0.0144,
                minClaimRtp: config.minClaimRtp || 0.01,
                claimCooldownMs: config.claimCooldownMs || 10800000,
                
                // Rate limits
                adClaimLimitPerDay: config.adClaimLimitPerDay || 5,
                minigameLimitPerDay: config.minigameLimitPerDay || 10,
                minigameCooldownMs: config.minigameCooldownMs || 300000,
                
                // Bonus
                minigameBonusCap: config.minigameBonusCap || 20,
                
                updatedAt: config.updatedAt,
                updatedBy: config.updatedBy
            }
        });
    } catch (error) {
        console.error('Get game config error:', error);
        return res.status(500).json({ success: false, message: error.message });
    }
});

// ============================================================
// PUT /api/v1/admin/game-config
// Update config game
// ============================================================
router.put('/game-config', checkAdmin, async (req, res) => {
    try {
        // M46: uid dari checkAdmin (boleh dari web atau firebase)
        const uid = req.adminUser?.uid || 'admin_web';
        const updates = req.body || {};
        
        // Validate & pisah exchange rate
        const exchangeUpdates = {};
        if (typeof updates.usdToIdr === 'number' && updates.usdToIdr > 0) {
            exchangeUpdates.usdToIdr = updates.usdToIdr;
        }
        if (typeof updates.rtpToUsd === 'number' && updates.rtpToUsd > 0) {
            exchangeUpdates.rtpToUsd = updates.rtpToUsd;
        }
        if (exchangeUpdates.usdToIdr && exchangeUpdates.rtpToUsd) {
            exchangeUpdates.rtpToIdr = exchangeUpdates.usdToIdr / exchangeUpdates.rtpToUsd;
        }
        
        if (Object.keys(exchangeUpdates).length > 0) {
            exchangeUpdates.updatedAt = Date.now();
            exchangeUpdates.updatedBy = uid;
            await db.collection('app_config').doc('exchange_rates').set(exchangeUpdates, { merge: true });
        }
        
        // Game config
        const gameConfig = {};
        const validKeys = [
            'miningRatePerGhs', 'minClaimRtp', 'claimCooldownMs',
            'adClaimLimitPerDay', 'minigameLimitPerDay', 'minigameCooldownMs',
            'minigameBonusCap'
        ];
        
        validKeys.forEach(k => {
            if (updates[k] !== undefined && typeof updates[k] === 'number' && updates[k] >= 0) {
                gameConfig[k] = updates[k];
            }
        });
        
        if (Object.keys(gameConfig).length > 0) {
            gameConfig.updatedAt = Date.now();
            gameConfig.updatedBy = uid;
            await db.collection('app_config').doc('game_config').set(gameConfig, { merge: true });
        }
        
        // Audit log
        await db.collection('audit_logs').add({
            userId: uid,
            action: 'ADMIN_UPDATE_GAME_CONFIG',
            changes: updates,
            timestamp: admin.firestore.FieldValue.serverTimestamp()
        });
        
        // Reset cache backend (kalau ada)
        if (typeof global.__resetGameConfigCache === 'function') {
            global.__resetGameConfigCache();
        }
        
        return res.json({ success: true, message: 'Config disimpan' });
    } catch (error) {
        console.error('Update game config error:', error);
        return res.status(500).json({ success: false, message: error.message });
    }
});

// ============================================================
// USER MANAGEMENT
// ============================================================
router.get('/users', checkAdmin, async (req, res) => {
    try {
        const snapshot = await db.collection('users').get();
        const users = snapshot.docs.map(doc => ({ id: doc.id, ...doc.data() }));
        res.json({ success: true, total: users.length, data: users });
    } catch (err) {
        res.status(500).json({ success: false, message: err.message });
    }
});

router.get('/users/:uid', checkAdmin, async (req, res) => {
    try {
        const doc = await db.collection('users').doc(req.params.uid).get();
        if (!doc.exists) return res.status(404).json({ success: false, message: 'User tidak ditemukan' });
        res.json({ success: true, data: { id: doc.id, ...doc.data() } });
    } catch (err) {
        res.status(500).json({ success: false, message: err.message });
    }
});

router.put('/users/:uid', checkAdmin, async (req, res) => {
    try {
        const docRef = db.collection('users').doc(req.params.uid);
        const doc = await docRef.get();
        if (!doc.exists) return res.status(404).json({ success: false, message: 'User tidak ditemukan' });
        await docRef.update({ ...req.body, updatedAt: new Date().toISOString() });
        res.json({ success: true, message: 'User diupdate' });
    } catch (err) {
        res.status(500).json({ success: false, message: err.message });
    }
});

router.post('/users/:uid/ban', checkAdmin, async (req, res) => {
    try {
        await db.collection('users').doc(req.params.uid).update({
            banned: true,
            bannedAt: new Date().toISOString(),
            bannedReason: req.body.reason || 'Dibanned admin'
        });
        res.json({ success: true, message: 'User dibanned' });
    } catch (err) {
        res.status(500).json({ success: false, message: err.message });
    }
});

router.post('/users/:uid/unban', checkAdmin, async (req, res) => {
    try {
        await db.collection('users').doc(req.params.uid).update({
            banned: false,
            unbannedAt: new Date().toISOString()
        });
        res.json({ success: true, message: 'User di-unban' });
    } catch (err) {
        res.status(500).json({ success: false, message: err.message });
    }
});

// ============================================================
// WITHDRAWAL MANAGEMENT
// ============================================================
router.get('/withdrawals', checkAdmin, async (req, res) => {
    try {
        const statusFilter = req.query.status || 'PENDING';
        const snapshot = await db.collection('withdrawals')
            .where('status', '==', statusFilter)
            .get();
        const result = snapshot.docs.map(doc => ({ id: doc.id, ...doc.data() }));
        res.json({ success: true, total: result.length, data: result });
    } catch (err) {
        res.status(500).json({ success: false, message: err.message });
    }
});

router.get('/withdrawals/all', checkAdmin, async (req, res) => {
    try {
        const snapshot = await db.collection('withdrawals')
            .orderBy('timestamp', 'desc')
            .limit(100)
            .get();
        const result = snapshot.docs.map(doc => ({ id: doc.id, ...doc.data() }));
        res.json({ success: true, total: result.length, data: result });
    } catch (err) {
        res.status(500).json({ success: false, message: err.message });
    }
});

router.post('/withdrawals/:id/approve', checkAdmin, async (req, res) => {
    try {
        const docRef = db.collection('withdrawals').doc(req.params.id);
        const doc = await docRef.get();
        if (!doc.exists) return res.status(404).json({ success: false, message: 'Withdrawal tidak ditemukan' });
        
        await docRef.update({
            status: 'PAID',
            processedAt: Date.now(),
            adminNotes: req.body.note || 'Disetujui admin'
        });
        res.json({ success: true, message: 'Withdrawal disetujui' });
    } catch (err) {
        res.status(500).json({ success: false, message: err.message });
    }
});

router.post('/withdrawals/:id/reject', checkAdmin, async (req, res) => {
    try {
        const docRef = db.collection('withdrawals').doc(req.params.id);
        const doc = await docRef.get();
        if (!doc.exists) return res.status(404).json({ success: false, message: 'Withdrawal tidak ditemukan' });
        
        await docRef.update({
            status: 'REJECTED',
            processedAt: Date.now(),
            adminNotes: req.body.note || 'Ditolak admin'
        });
        res.json({ success: true, message: 'Withdrawal ditolak' });
    } catch (err) {
        res.status(500).json({ success: false, message: err.message });
    }
});

// ============================================================
// NOTIFICATION BROADCAST
// ============================================================
router.post('/notifications/broadcast', checkAdmin, async (req, res) => {
    try {
        const { title, message, type } = req.body;
        if (!title || !message) return res.status(400).json({ success: false, message: 'Title & message wajib' });
        
        const usersSnapshot = await db.collection('users').get();
        const batch = db.batch();
        let count = 0;
        usersSnapshot.forEach(userDoc => {
            const notifRef = db.collection('notifications').doc();
            batch.set(notifRef, {
                userId: userDoc.id,
                title, message,
                type: type || 'BROADCAST',
                timestamp: Date.now(),
                createdAt: new Date().toISOString(),
                read: false
            });
            count++;
        });
        await batch.commit();
        res.json({ success: true, message: `Broadcast ke ${count} user.` });
    } catch (err) {
        res.status(500).json({ success: false, message: err.message });
    }
});

// ============================================================
// SYSTEM CONTROL
// ============================================================
router.post('/system/maintenance', checkAdmin, async (req, res) => {
    try {
        const { enabled } = req.body;
        await db.collection('system').doc('config').set({
            maintenanceMode: !!enabled,
            updatedAt: new Date().toISOString()
        }, { merge: true });
        res.json({ success: true, message: `Maintenance mode ${enabled ? 'ON' : 'OFF'}` });
    } catch (err) {
        res.status(500).json({ success: false, message: err.message });
    }
});

router.put('/rates', checkAdmin, async (req, res) => {
    try {
        const { usdToIdr, rtpToIdr, rtpToUsd } = req.body;
        await db.collection('app_config').doc('exchange_rates').set({
            usdToIdr, rtpToIdr, rtpToUsd,
            updatedAt: new Date().toISOString()
        }, { merge: true });
        res.json({ success: true, message: 'Exchange rate diupdate' });
    } catch (err) {
        res.status(500).json({ success: false, message: err.message });
    }
});

router.post('/login', async (req, res) => {
    try {
        const { password } = req.body;
        const ADMIN_PASSWORD = process.env.ADMIN_PASSWORD || 'royaltree_admin_2026';
        if (password !== ADMIN_PASSWORD) {
            return res.status(401).json({ success: false, message: 'Password salah' });
        }
        const token = 'admin_' + Math.random().toString(36).substr(2, 24) + Date.now();
        if (!global.__adminTokens) global.__adminTokens = new Map();
        global.__adminTokens.set(token, { expiresAt: Date.now() + (24 * 60 * 60 * 1000) });
        res.json({ success: true, token, message: 'Login admin berhasil' });
    } catch (err) {
        res.status(500).json({ success: false, message: err.message });
    }
});

module.exports = router;
