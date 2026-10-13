// routes/admin.js
// Endpoint admin: atur config daily reward & ad vendors

const express = require('express');
const anomaly = require('../services/anomaly');
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
        
        // DEBUG LOG
        console.log(`[checkAdmin] token prefix=${adminToken ? adminToken.substring(0, 12) : 'NULL'}, mapSize=${global.__adminTokens ? global.__adminTokens.size : 'NO_MAP'}`);
        if (global.__adminTokens) {
            const keys = [...global.__adminTokens.keys()].map(k => k.substring(0, 12));
            console.log(`[checkAdmin] keys in map: ${JSON.stringify(keys)}`);
        }
        
        if (adminToken && global.__adminTokens && global.__adminTokens.has(adminToken)) {
            const tokenData = global.__adminTokens.get(adminToken);
            if (tokenData.expiresAt > Date.now()) {
                console.log(`[checkAdmin] ✅ token match`);
                req.adminUser = { source: 'web', token: adminToken };
                return next();
            } else {
                console.log(`[checkAdmin] ⚠️ token expired`);
                global.__adminTokens.delete(adminToken);
            }
        } else {
            console.log(`[checkAdmin] ❌ token NOT in map`);
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
        const { title, message, type, targetUid } = req.body;
        if (!title || !message) return res.status(400).json({ success: false, message: 'Title & message wajib' });

        let usersSnapshot;
        if (targetUid) {
            const singleDoc = await db.collection('users').doc(targetUid).get();
            if (!singleDoc.exists) {
                return res.status(404).json({ success: false, message: 'User tidak ditemukan' });
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
                title, message,
                type: type || 'BROADCAST',
                timestamp: Date.now(),
                createdAt: new Date().toISOString(),
                read: false
            });
            if (userData.fcmToken) {
                fcmTokens.push(userData.fcmToken);
            }
            count++;
        });
        await batch.commit();

        let fcmResult = { success: false, reason: 'NO_TOKENS' };
        if (fcmTokens.length > 0) {
            try {
                const fcmService = require('../services/fcm');
                fcmResult = await fcmService.sendBatchPush(fcmTokens, title, message, { type: type || 'BROADCAST' });
                console.log('[BROADCAST] FCM sent to ' + fcmTokens.length + ' tokens');
            } catch (fcmErr) {
                console.error('[BROADCAST] FCM error:', fcmErr.message);
            }
        }

        res.json({
            success: true,
            message: 'Broadcast ke ' + count + ' user.',
            fcmSent: fcmTokens.length,
            fcmResult
        });
    } catch (err) {
        console.error('[BROADCAST] Error:', err);
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
        const { password, apiKey } = req.body;
        const ADMIN_PASSWORD = process.env.ADMIN_PASSWORD || 'royaltree_admin_2026';
        const API_KEY = process.env.ROYALTREE_API_KEY;

        // Verifikasi password
        if (!password || password !== ADMIN_PASSWORD) {
            return res.status(401).json({ success: false, message: 'Password salah' });
        }

        // Verifikasi API key
        if (!apiKey || !API_KEY || apiKey !== API_KEY) {
            return res.status(401).json({ success: false, message: 'API Key salah' });
        }

        // Generate admin token
        const token = 'admin_' + Math.random().toString(36).substr(2, 24) + Date.now();
        if (!global.__adminTokens) global.__adminTokens = new Map();
        const expiresAt = Date.now() + (24 * 60 * 60 * 1000);
        global.__adminTokens.set(token, { expiresAt });

        console.log('[ADMIN] Login sukses');
        res.json({
            success: true,
            token,
            expiresAt,
            message: 'Login admin berhasil'
        });
    } catch (err) {
        res.status(500).json({ success: false, message: err.message });
    }
});

// ============================================================
// ADMIN STATS — Overview Dashboard
// GET /api/v1/admin/stats
// ============================================================
router.get('/stats', checkAdmin, async (req, res) => {
    try {
        // Total users
        const usersSnapshot = await db.collection('users').count().get();
        const totalUsers = usersSnapshot.data().count;

        // Total RTP (sum points)
        const usersData = await db.collection('users').select('points').get();
        let totalRTP = 0;
        usersData.forEach(doc => {
            totalRTP += (doc.data().points || 0);
        });

        // Total miners
        let totalMiners = 0;
        usersData.forEach(doc => {
            const minersOwned = doc.data().minersOwned || {};
            totalMiners += Object.keys(minersOwned).length;
        });

        // Active miners (minerActiveUntil > now)
        const now = Date.now();
        const roomsSnapshot = await db.collection('game_rooms').get();
        let activeMiners = 0;
        let totalPoolFund = 0;
        roomsSnapshot.forEach(doc => {
            const data = doc.data();
            if ((data.minerActiveUntil || 0) > now) activeMiners++;
            totalPoolFund += (data.poolFundContribution || 0);
        });

        // Total transactions
        const txSnapshot = await db.collection('transactions').count().get();
        const totalTransactions = txSnapshot.data().count;

        // Pending withdrawals
        const pendingWdSnapshot = await db.collection('withdrawals')
            .where('status', '==', 'PENDING')
            .count().get();
        const pendingWithdrawals = pendingWdSnapshot.data().count;

        // Total blocks
        const blocksSnapshot = await db.collection('blocks').count().get();
        const totalBlocks = blocksSnapshot.data().count;

        // Chat messages
        const chatSnapshot = await db.collection('global_chat').count().get();
        const totalChatMessages = chatSnapshot.data().count;

        return res.json({
            success: true,
            data: {
                totalUsers,
                totalRTP: parseFloat(totalRTP.toFixed(2)),
                totalMiners,
                activeMiners,
                totalPoolFund: parseFloat(totalPoolFund.toFixed(2)),
                totalTransactions,
                pendingWithdrawals,
                totalBlocks,
                totalChatMessages,
                timestamp: now
            }
        });
    } catch (error) {
        console.error('[ADMIN/STATS] Error:', error);
        return res.status(500).json({ success: false, message: error.message });
    }
});

// ============================================================
// MINING STATS
// GET /api/v1/admin/mining/pool-fund
// ============================================================
router.get('/mining/pool-fund', checkAdmin, async (req, res) => {
    try {
        const configDoc = await db.collection('game_config').doc('default').get();
        const config = configDoc.exists ? configDoc.data() : {};

        const poolFund = config.poolFund || 0;
        const totalCollected = config.poolFundTotalCollected || 0;
        const totalPaidOut = config.poolFundTotalPaidOut || 0;

        return res.json({
            success: true,
            data: {
                balance: poolFund,
                totalCollected,
                totalPaidOut,
                devProfit: totalCollected - totalPaidOut,
                health: poolFund > 1000 ? 'HEALTHY' : (poolFund > 200 ? 'LOW' : 'CRITICAL'),
                timestamp: Date.now()
            }
        });
    } catch (error) {
        console.error('[ADMIN/POOL] Error:', error);
        return res.status(500).json({ success: false, message: error.message });
    }
});

// ============================================================
// BLOCK HISTORY
// GET /api/v1/admin/mining/blocks
// ============================================================
router.get('/mining/blocks', checkAdmin, async (req, res) => {
    try {
        const limit = Math.min(parseInt(req.query.limit) || 50, 100);
        const snapshot = await db.collection('blocks')
            .orderBy('height', 'desc')
            .limit(limit)
            .get();

        const blocks = [];
        snapshot.forEach(doc => {
            blocks.push({ id: doc.id, ...doc.data() });
        });

        return res.json({ success: true, blocks, count: blocks.length });
    } catch (error) {
        console.error('[ADMIN/BLOCKS] Error:', error);
        return res.status(500).json({ success: false, message: error.message });
    }
});

// ============================================================
// CHAT MODERATION — List
// GET /api/v1/admin/chat/messages
// ============================================================
router.get('/chat/messages', checkAdmin, async (req, res) => {
    try {
        const limit = Math.min(parseInt(req.query.limit) || 100, 200);
        const snapshot = await db.collection('global_chat')
            .orderBy('timestamp', 'desc')
            .limit(limit)
            .get();

        const messages = [];
        snapshot.forEach(doc => {
            messages.push({ id: doc.id, ...doc.data() });
        });

        return res.json({ success: true, messages, count: messages.length });
    } catch (error) {
        console.error('[ADMIN/CHAT] Error:', error);
        return res.status(500).json({ success: false, message: error.message });
    }
});

// ============================================================
// CHAT MODERATION — Delete
// DELETE /api/v1/admin/chat/:id
// ============================================================
router.delete('/chat/:id', checkAdmin, async (req, res) => {
    try {
        const { id } = req.params;
        await db.collection('global_chat').doc(id).delete();
        return res.json({ success: true, message: 'Pesan dihapus.' });
    } catch (error) {
        console.error('[ADMIN/CHAT-DELETE] Error:', error);
        return res.status(500).json({ success: false, message: error.message });
    }
});

// ============================================================
// CAMPAIGNS
// GET /api/v1/admin/campaigns
// ============================================================
router.get('/campaigns', checkAdmin, async (req, res) => {
    try {
        const snapshot = await db.collection('campaigns').limit(100).get();
        const campaigns = [];
        snapshot.forEach(doc => {
            campaigns.push({ id: doc.id, ...doc.data() });
        });
        return res.json({ success: true, campaigns, count: campaigns.length });
    } catch (error) {
        console.error('[ADMIN/CAMPAIGNS] Error:', error);
        return res.status(500).json({ success: false, message: error.message });
    }
});

// ============================================================
// MISSIONS
// GET /api/v1/admin/missions
// ============================================================
router.get('/missions', checkAdmin, async (req, res) => {
    try {
        const snapshot = await db.collection('missions').limit(100).get();
        const missions = [];
        snapshot.forEach(doc => {
            missions.push({ id: doc.id, ...doc.data() });
        });
        return res.json({ success: true, missions, count: missions.length });
    } catch (error) {
        console.error('[ADMIN/MISSIONS] Error:', error);
        return res.status(500).json({ success: false, message: error.message });
    }
});

// ============================================================
// ANOMALY — Get list
// GET /api/v1/admin/anomalies
// ============================================================
router.get('/anomalies', checkAdmin, async (req, res) => {
    try {
        const limit = Math.min(parseInt(req.query.limit) || 100, 200);
        const filter = {};
        if (req.query.severity) filter.severity = req.query.severity;
        if (req.query.status) filter.status = req.query.status;
        if (req.query.uid) filter.uid = req.query.uid;

        const anomalies = anomaly.getAnomalies(limit, filter);
        return res.json({ success: true, anomalies, count: anomalies.length });
    } catch (error) {
        return res.status(500).json({ success: false, message: error.message });
    }
});

// ============================================================
// ANOMALY — Stats
// GET /api/v1/admin/anomalies/stats
// ============================================================
router.get('/anomalies/stats', checkAdmin, async (req, res) => {
    try {
        const stats = anomaly.getStats();
        return res.json({ success: true, data: stats });
    } catch (error) {
        return res.status(500).json({ success: false, message: error.message });
    }
});

// ============================================================
// ANOMALY — Flagged users
// GET /api/v1/admin/anomalies/flagged-users
// ============================================================
router.get('/anomalies/flagged-users', checkAdmin, async (req, res) => {
    try {
        const users = anomaly.getFlaggedUsers();
        return res.json({ success: true, users, count: users.length });
    } catch (error) {
        return res.status(500).json({ success: false, message: error.message });
    }
});

// ============================================================
// ANOMALY — Update status
// POST /api/v1/admin/anomalies/:id/status
// ============================================================
router.post('/anomalies/:id/status', checkAdmin, async (req, res) => {
    try {
        const { id } = req.params;
        const { status } = req.body;

        if (!['OPEN', 'REVIEWED', 'BANNED', 'DISMISSED'].includes(status)) {
            return res.status(400).json({ success: false, message: 'Status tidak valid' });
        }

        const result = anomaly.updateAnomalyStatus(id, status);
        if (!result) {
            return res.status(404).json({ success: false, message: 'Anomaly tidak ditemukan' });
        }

        return res.json({ success: true, anomaly: result });
    } catch (error) {
        return res.status(500).json({ success: false, message: error.message });
    }
});

// ============================================================
// ANOMALY — Clear user flag
// POST /api/v1/admin/users/:uid/clear-flag
// ============================================================
router.post('/users/:uid/clear-flag', checkAdmin, async (req, res) => {
    try {
        const { uid } = req.params;
        anomaly.clearUserFlag(uid);
        return res.json({ success: true, message: 'Flag cleared' });
    } catch (error) {
        return res.status(500).json({ success: false, message: error.message });
    }
});

module.exports = router;
