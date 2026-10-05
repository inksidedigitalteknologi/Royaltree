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

module.exports = router;
