// routes/admin.js
// Endpoint admin: atur config daily reward & ad vendors

const express = require('express');

// Debug flag untuk log checkAdmin (set DEBUG_ADMIN=1 di env untuk enable)
const DEBUG_ADMIN_LOG = process.env.DEBUG_ADMIN === '1';
const anomaly = require('../services/anomaly');
const { broadcastConfigRefresh } = require('../services/configRefresh');
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
        if (DEBUG_ADMIN_LOG) console.log(`[checkAdmin] token prefix=${adminToken ? adminToken.substring(0, 12) : 'NULL'}, mapSize=${global.__adminTokens ? global.__adminTokens.size : 'NO_MAP'}`);
        if (global.__adminTokens) {
            const keys = [...global.__adminTokens.keys()].map(k => k.substring(0, 12));
            if (DEBUG_ADMIN_LOG) console.log(`[checkAdmin] keys in map: ${JSON.stringify(keys)}`);
        }
        
        if (adminToken && global.__adminTokens && global.__adminTokens.has(adminToken)) {
            const tokenData = global.__adminTokens.get(adminToken);
            if (tokenData.expiresAt > Date.now()) {
                if (DEBUG_ADMIN_LOG) console.log(`[checkAdmin] ✅ token match`);
                req.adminUser = { source: 'web', token: adminToken };
                return next();
            } else {
                if (DEBUG_ADMIN_LOG) console.log(`[checkAdmin] ⚠️ token expired`);
                global.__adminTokens.delete(adminToken);
            }
        } else {
            if (DEBUG_ADMIN_LOG) console.log(`[checkAdmin] ❌ token NOT in map`);
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

        // Broadcast config refresh ke semua user (silent push)
        broadcastConfigRefresh('daily').catch(e =>
            console.error('[DAILY-CONFIG] Broadcast error:', e.message)
        );

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

        // Broadcast config refresh
        broadcastConfigRefresh('ads').catch(e =>
            console.error('[AD-CONFIG] Broadcast error:', e.message)
        );

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

                // Thermal config
                thermalPauseTemp: config.thermalPauseTemp || 60,
                thermalResumeTemp: config.thermalResumeTemp || 55,
                thermalAlertTemp: config.thermalAlertTemp || 45,
                thermalCriticalTemp: config.thermalCriticalTemp || 50,
                chargingBoost: config.chargingBoost || 1.2,
                minBatteryForMining: config.minBatteryForMining || 5,
                
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
            'minigameBonusCap',
            // Thermal config (baru)
            'thermalPauseTemp', 'thermalResumeTemp', 'thermalAlertTemp',
            'thermalCriticalTemp', 'chargingBoost', 'minBatteryForMining'
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
        
        // Broadcast config refresh ke semua user (silent push)
        broadcastConfigRefresh('game').catch(e =>
            console.error('[GAME-CONFIG] Broadcast error:', e.message)
        );

        return res.json({ success: true, message: 'Config disimpan' });
    } catch (error) {
        console.error('Update game config error:', error);
        return res.status(500).json({ success: false, message: error.message });
    }
});

// ============================================================
// ANALYTICS — User growth + DAU + Mining activity
// GET /api/v1/admin/analytics?days=30
// ============================================================
router.get('/analytics', checkAdmin, async (req, res) => {
    try {
        const days = Math.min(parseInt(req.query.days) || 30, 90);
        const since = Date.now() - (days * 24 * 60 * 60 * 1000);

        // 1. User growth (new users per day)
        const usersSnap = await db.collection('users').get();
        const userGrowth = {};
        let totalUsers = 0;
        const dau = {};

        usersSnap.forEach(doc => {
            const data = doc.data();
            totalUsers++;

            // New users per day
            const created = data.createdAt;
            if (created && created._seconds) {
                const ts = created._seconds * 1000;
                const day = new Date(ts).toISOString().split('T')[0];
                userGrowth[day] = (userGrowth[day] || 0) + 1;
            }

            // DAU from lastLoginAt
            const lastLogin = data.lastLoginAt;
            if (lastLogin && lastLogin._seconds) {
                const ts = lastLogin._seconds * 1000;
                if (ts >= since) {
                    const day = new Date(ts).toISOString().split('T')[0];
                    if (!dau[day]) dau[day] = new Set();
                    dau[day].add(doc.id);
                }
            }
        });

        // 2. Mining activity dari thermal_logs
        const thermalSnap = await db.collection('thermal_logs')
            .orderBy('createdAt', 'desc')
            .limit(2000)
            .get();

        const mining = {};
        let totalLogs = 0;

        thermalSnap.forEach(doc => {
            const data = doc.data();
            const ts = data.createdAt?._seconds * 1000;
            if (ts && ts >= since) {
                const day = new Date(ts).toISOString().split('T')[0];
                if (!mining[day]) mining[day] = { count: 0, sumTemp: 0, activeUsers: new Set() };
                mining[day].count++;
                mining[day].sumTemp += (data.phone_temp || 0);
                if (data.uid) mining[day].activeUsers.add(data.uid);
                totalLogs++;
            }
        });

        // 3. Format output
        const dailyGrowth = Object.entries(userGrowth)
            .filter(([d]) => new Date(d).getTime() >= since)
            .map(([date, count]) => ({ date, newUsers: count }))
            .sort((a, b) => a.date.localeCompare(b.date));

        const dailyDau = Object.entries(dau)
            .map(([date, set]) => ({ date, users: set.size }))
            .sort((a, b) => a.date.localeCompare(b.date));

        const dailyMining = Object.entries(mining)
            .map(([date, m]) => ({
                date,
                count: m.count,
                avgTemp: m.count ? parseFloat((m.sumTemp / m.count).toFixed(1)) : 0,
                activeUsers: m.activeUsers.size
            }))
            .sort((a, b) => a.date.localeCompare(b.date));

        // 4. Summary
        const avgDau = dailyDau.length
            ? parseFloat((dailyDau.reduce((s, d) => s + d.users, 0) / dailyDau.length).toFixed(1))
            : 0;
        const avgNewUsers = dailyGrowth.length
            ? parseFloat((dailyGrowth.reduce((s, d) => s + d.newUsers, 0) / dailyGrowth.length).toFixed(1))
            : 0;
        const totalNewUsers = dailyGrowth.reduce((s, d) => s + d.newUsers, 0);

        return res.json({
            success: true,
            data: {
                summary: {
                    totalUsers,
                    avgDau,
                    avgNewUsers,
                    totalNewUsers,
                    totalMiningLogs: totalLogs,
                    days
                },
                userGrowth: dailyGrowth,
                dau: dailyDau,
                mining: dailyMining
            }
        });
    } catch (err) {
        console.error('[ADMIN/ANALYTICS] Error:', err);
        return res.status(500).json({ success: false, message: err.message });
    }
});

// ============================================================
// THERMAL STATS — Aggregate thermal_logs
// GET /api/v1/admin/thermal-stats?days=7
// ============================================================
router.get('/thermal-stats', checkAdmin, async (req, res) => {
    try {
        const days = Math.min(parseInt(req.query.days) || 7, 30);
        const since = Date.now() - (days * 24 * 60 * 60 * 1000);
        
        const snapshot = await db.collection('thermal_logs')
            .orderBy('createdAt', 'desc')
            .limit(2000)
            .get();
        
        const logs = [];
        snapshot.forEach(doc => {
            const data = doc.data();
            if (data.createdAt && data.createdAt._seconds) {
                const ts = data.createdAt._seconds * 1000;
                if (ts >= since) {
                    logs.push({ ...data, _ts: ts });
                }
            }
        });
        
        const dailyAvg = {};
        let totalTemp = 0, maxTemp = 0, minTemp = 100;
        const distribution = { cold: 0, normal: 0, warm: 0, hot: 0, critical: 0 };
        let pauseCount = 0;
        
        logs.forEach(log => {
            const t = Number(log.phone_temp) || 0;
            const day = new Date(log._ts).toISOString().split('T')[0];
            
            if (!dailyAvg[day]) dailyAvg[day] = { sum: 0, count: 0, max: 0 };
            dailyAvg[day].sum += t;
            dailyAvg[day].count++;
            dailyAvg[day].max = Math.max(dailyAvg[day].max, t);
            
            totalTemp += t;
            maxTemp = Math.max(maxTemp, t);
            minTemp = Math.min(minTemp, t);
            
            if (t < 30) distribution.cold++;
            else if (t < 40) distribution.normal++;
            else if (t < 45) distribution.warm++;
            else if (t < 55) distribution.hot++;
            else distribution.critical++;
            
            if (t >= 60) pauseCount++;
        });
        
        const daily = Object.entries(dailyAvg).map(([date, d]) => ({
            date,
            avgTemp: parseFloat((d.sum / d.count).toFixed(1)),
            maxTemp: parseFloat(d.max.toFixed(1)),
            count: d.count
        })).sort((a, b) => a.date.localeCompare(b.date));
        
        return res.json({
            success: true,
            data: {
                daily,
                distribution,
                summary: {
                    totalLogs: logs.length,
                    avgTemp: logs.length ? parseFloat((totalTemp / logs.length).toFixed(1)) : 0,
                    maxTemp: parseFloat(maxTemp.toFixed(1)),
                    minTemp: minTemp === 100 ? 0 : parseFloat(minTemp.toFixed(1)),
                    pauseEvents: pauseCount,
                    days
                }
            }
        });
    } catch (err) {
        console.error('[ADMIN/THERMAL-STATS] Error:', err);
        return res.status(500).json({ success: false, message: err.message });
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
        const uid = req.params.uid;
        const doc = await db.collection('users').doc(uid).get();
        if (!doc.exists) return res.status(404).json({ success: false, message: 'User tidak ditemukan' });

        const userData = doc.data();

        // ============================================================
        // COMPUTED FIELDS
        // ============================================================
        let referralCount = 0;
        let totalMiners = 0;
        let thermal = null;
        let location = null;
        let miningPaused = false;
        let totalMiningLogs = 0;

        try {
            // 1. Referral count (user yang referredBy == uid)
            const refSnap = await db.collection('users')
                .where('referredBy', '==', uid)
                .count()
                .get();
            referralCount = refSnap.data().count;
        } catch (e) {
            console.error('[USER-DETAIL] referralCount error:', e.message);
        }

        try {
            // 2. Total miners owned
            const minersOwned = userData.minersOwned || {};
            totalMiners = Object.keys(minersOwned).length;
        } catch (e) {}

        try {
            // 3. Game state (thermal + location)
            const stateDoc = await db.collection('game_states').doc(uid).get();
            if (stateDoc.exists) {
                const state = stateDoc.data();
                thermal = state.thermal || null;
                location = state.location || null;
                miningPaused = state.miningPaused === true;
            }
        } catch (e) {
            console.error('[USER-DETAIL] game_states error:', e.message);
        }

        try {
            // 4. Total mining logs
            const logsSnap = await db.collection('thermal_logs')
                .where('uid', '==', uid)
                .count()
                .get();
            totalMiningLogs = logsSnap.data().count;
        } catch (e) {}

        return res.json({
            success: true,
            data: {
                id: doc.id,
                uid: uid,
                ...userData,
                // Computed fields
                referralCount,
                totalMiners,
                thermal,
                location,
                miningPaused,
                totalMiningLogs,
                hasFcmToken: !!(userData.fcmToken && userData.fcmToken.length > 20),
                language: userData.language || 'id',
                banned: userData.banned === true
            }
        });
    } catch (err) {
        console.error('[ADMIN/USER-DETAIL] Error:', err);
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
        const { uid } = req.params;
        const { reason } = req.body;
        const bannedBy = req.adminUser?.uid || 'admin_web';

        await db.collection('users').doc(uid).update({
            banned: true,
            bannedAt: new Date().toISOString(),
            bannedReason: reason || 'Dibanned admin',
            bannedBy
        });

        // Notif in-app (banned user tetap dapat notif)
        try {
            await db.collection('notifications').add({
                userId: uid,
                title: '🚫 Akun Anda Diblokir',
                message: `Alasan: ${reason || 'Pelanggaran aturan'}. Hubungi support untuk informasi.`,
                type: 'ACCOUNT_BANNED',
                severity: 'CRITICAL',
                timestamp: Date.now(),
                createdAt: new Date().toISOString(),
                read: false
            });

            // FCM push
            const userDoc = await db.collection('users').doc(uid).get();
            const userData = userDoc.data() || {};
            if (userData.fcmToken) {
                try {
                    const fcmService = require('../services/fcm');
                    await fcmService.sendBatchPush(
                        [userData.fcmToken],
                        '🚫 Akun Anda Diblokir',
                        `Alasan: ${reason || 'Pelanggaran aturan'}`,
                        { type: 'ACCOUNT_BANNED', severity: 'CRITICAL' }
                    );
                } catch (e) {}
            }
        } catch (e) {
            console.error('[BAN] Notif error:', e.message);
        }

        res.json({ success: true, message: 'User dibanned' });
    } catch (err) {
        res.status(500).json({ success: false, message: err.message });
    }
});

router.post('/users/:uid/unban', checkAdmin, async (req, res) => {
    try {
        const { uid } = req.params;
        const unbannedBy = req.adminUser?.uid || 'admin_web';

        await db.collection('users').doc(uid).update({
            banned: false,
            unbannedAt: new Date().toISOString(),
            unbannedBy
        });

        // Notif in-app
        try {
            await db.collection('notifications').add({
                userId: uid,
                title: '✅ Akun Anda Diaktifkan Kembali',
                message: 'Akun Anda sudah di-unban. Silakan lanjut mining!',
                type: 'ACCOUNT_UNBANNED',
                severity: 'INFO',
                timestamp: Date.now(),
                createdAt: new Date().toISOString(),
                read: false
            });

            // FCM push
            const userDoc = await db.collection('users').doc(uid).get();
            const userData = userDoc.data() || {};
            if (userData.fcmToken) {
                try {
                    const fcmService = require('../services/fcm');
                    await fcmService.sendBatchPush(
                        [userData.fcmToken],
                        '✅ Akun Diaktifkan Kembali',
                        'Akun Anda sudah di-unban. Selamat mining kembali!',
                        { type: 'ACCOUNT_UNBANNED', severity: 'INFO' }
                    );
                } catch (e) {}
            }
        } catch (e) {
            console.error('[UNBAN] Notif error:', e.message);
        }

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
// SCHEDULED NOTIFICATIONS
// ============================================================

router.post('/notifications/schedule', checkAdmin, async (req, res) => {
    try {
        const { title, message, type, targetUid, scheduledAt } = req.body;

        if (!title || !message) {
            return res.status(400).json({ success: false, message: 'Title & message wajib' });
        }
        if (!scheduledAt || scheduledAt <= Date.now()) {
            return res.status(400).json({ success: false, message: 'Scheduled time harus di masa depan' });
        }

        const docRef = await db.collection('scheduled_notifications').add({
            title: String(title).slice(0, 100),
            message: String(message).slice(0, 500),
            type: type || 'BROADCAST',
            targetUid: targetUid || '',
            scheduledAt: Number(scheduledAt),
            status: 'PENDING',
            createdAt: Date.now(),
            sentAt: 0,
            sentCount: 0,
            createdBy: req.adminUser?.uid || 'admin_web'
        });

        return res.json({
            success: true,
            id: docRef.id,
            message: 'Notifikasi terjadwal dibuat'
        });
    } catch (err) {
        console.error('[SCHEDULE/CREATE] Error:', err);
        return res.status(500).json({ success: false, message: err.message });
    }
});

router.get('/notifications/scheduled', checkAdmin, async (req, res) => {
    try {
        const snapshot = await db.collection('scheduled_notifications')
            .orderBy('scheduledAt', 'desc')
            .limit(100)
            .get();

        const list = [];
        snapshot.forEach(doc => {
            list.push({ id: doc.id, ...doc.data() });
        });

        return res.json({ success: true, data: list, count: list.length });
    } catch (err) {
        return res.status(500).json({ success: false, message: err.message });
    }
});

router.delete('/notifications/scheduled/:id', checkAdmin, async (req, res) => {
    try {
        const docRef = db.collection('scheduled_notifications').doc(req.params.id);
        const doc = await docRef.get();

        if (!doc.exists) {
            return res.status(404).json({ success: false, message: 'Notifikasi tidak ditemukan' });
        }
        if (doc.data().status !== 'PENDING') {
            return res.status(400).json({ success: false, message: 'Hanya PENDING yang bisa dibatalkan' });
        }

        await docRef.update({
            status: 'CANCELLED',
            cancelledAt: Date.now(),
            cancelledBy: req.adminUser?.uid || 'admin_web'
        });

        return res.json({ success: true, message: 'Notifikasi dibatalkan' });
    } catch (err) {
        return res.status(500).json({ success: false, message: err.message });
    }
});

// ============================================================
// SYSTEM CONTROL
// ============================================================
router.post('/system/maintenance', checkAdmin, async (req, res) => {
    try {
        const { enabled, message } = req.body;
        const updates = {
            maintenanceMode: !!enabled,
            updatedAt: new Date().toISOString()
        };
        if (message !== undefined) updates.maintenanceMessage = String(message);

        await db.collection('system').doc('config').set(updates, { merge: true });

        // Broadcast config refresh
        broadcastConfigRefresh('maintenance').catch(e =>
            console.error('[MAINTENANCE] Broadcast error:', e.message)
        );

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
// ANOMALY — Detail per user (score + flags + history)
// GET /api/v1/admin/anomalies/user/:uid
// ============================================================
router.get('/anomalies/user/:uid', checkAdmin, async (req, res) => {
    try {
        const { uid } = req.params;
        
        // 1. Flag data dari memory
        const flag = anomaly.getUserFlag(uid);
        
        // 2. History anomalies dari list
        const userAnomalies = anomaly.getAnomalies(100, { uid });
        
        // 3. User data dari Firestore
        const userDoc = await db.collection('users').doc(uid).get();
        const userData = userDoc.exists ? userDoc.data() : {};
        
        return res.json({
            success: true,
            data: {
                uid,
                score: flag.score || 0,
                flags: flag.flags || [],
                lastUpdate: flag.lastUpdate || 0,
                autoBanCandidate: (flag.score || 0) >= (anomaly.CONFIG?.BAN_THRESHOLD || 100),
                flagged: (flag.score || 0) >= (anomaly.CONFIG?.FLAG_THRESHOLD || 50),
                user: {
                    name: userData.name || '',
                    email: userData.email || '',
                    tier: userData.tier || 'FREE',
                    banned: userData.banned === true,
                    bannedReason: userData.bannedReason || '',
                    bannedAt: userData.bannedAt || '',
                    balance: userData.balance || 0,
                    points: userData.points || 0,
                    createdAt: userData.createdAt || null
                },
                history: userAnomalies.map(a => ({
                    id: a.id,
                    type: a.type,
                    severity: a.severity,
                    message: a.message,
                    timestamp: a.timestamp,
                    status: a.status
                })),
                config: {
                    flagThreshold: anomaly.CONFIG?.FLAG_THRESHOLD || 50,
                    banThreshold: anomaly.CONFIG?.BAN_THRESHOLD || 100
                }
            }
        });
    } catch (err) {
        console.error('[ADMIN/ANOMALY-USER] Error:', err);
        return res.status(500).json({ success: false, message: err.message });
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
