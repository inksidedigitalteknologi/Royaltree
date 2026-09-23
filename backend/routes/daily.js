// routes/daily.js
// Endpoint login harian + recovery streak

const express = require('express');
const router = express.Router();
const admin = require('firebase-admin');
const { verifyFirebaseToken } = require('../middleware/firebaseAuth');
const { checkFraud } = require('../middleware/antiFraud');

const db = admin.firestore();

// Default config (kalau di Firestore belum ada)
const DEFAULT_CONFIG = {
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

// Helper: ambil config
async function getConfig() {
    const doc = await db.collection('system').doc('daily_reward_config').get();
    if (doc.exists) {
        return { ...DEFAULT_CONFIG, ...doc.data() };
    }
    return DEFAULT_CONFIG;
}

// Helper: format tanggal YYYY-MM-DD (server time)
function todayStr() {
    return new Date().toISOString().split('T')[0];
}

// Helper: cek weekend
function isWeekend(dateStr) {
    const d = new Date(dateStr + 'T00:00:00Z');
    const day = d.getUTCDay();
    return day === 0 || day === 6;
}

// Helper: tanggal N hari lalu
function daysAgo(n) {
    const d = new Date();
    d.setUTCDate(d.getUTCDate() - n);
    return d.toISOString().split('T')[0];
}

// ============================================================
// GET /api/v1/daily/config
// ============================================================
router.get('/config', verifyFirebaseToken, async (req, res) => {
    try {
        const config = await getConfig();
        return res.json({ success: true, data: config });
    } catch (error) {
        return res.status(500).json({ success: false, message: error.message });
    }
});

// ============================================================
// GET /api/v1/daily/status
// Cek status check-in user + missed days
// ============================================================
router.get('/status', verifyFirebaseToken, async (req, res) => {
    try {
        const { uid } = req.firebaseUser;
        const config = await getConfig();
        const today = todayStr();

        const userDoc = await db.collection('users').doc(uid).get();
        const userData = userDoc.exists ? userDoc.data() : {};

        const lastCheckIn = userData.lastCheckInDate || '';
        const streak = userData.checkInStreak || 0;
        const checkedInToday = lastCheckIn === today;

        // Cek missed days (max maxDaysBack)
        const missedDays = [];
        for (let i = 1; i <= config.streakRecovery.maxDaysBack; i++) {
            const date = daysAgo(i);
            const checkinDoc = await db.collection('users').doc(uid)
                .collection('daily_checkins').doc(date).get();
            if (!checkinDoc.exists) {
                missedDays.push({
                    date: date,
                    points: isWeekend(date) ? config.weekendPoints : config.weekdayPoints,
                    isWeekend: isWeekend(date),
                    recovered: false
                });
            }
        }

        // Cek ads watched today
        const lastRecoveryAdDate = userData.lastRecoveryAdDate || '';
        const adsWatchedToday = lastRecoveryAdDate === today
            ? (userData.adsWatchedForRecovery || 0)
            : 0;

        return res.json({
            success: true,
            data: {
                today: today,
                checkedInToday: checkedInToday,
                lastCheckInDate: lastCheckIn,
                streak: streak,
                missedDays: missedDays,
                recoveryOptions: {
                    adRecovery: {
                        available: config.streakRecovery.allowAdRecovery
                            && missedDays.length > 0
                            && adsWatchedToday < config.streakRecovery.maxAdsPerDay,
                        adsWatchedToday: adsWatchedToday,
                        maxAdsPerDay: config.streakRecovery.maxAdsPerDay
                    },
                    pointRecovery: {
                        available: config.streakRecovery.allowPointRecovery
                            && missedDays.length > 0,
                        pointCostPerDay: config.streakRecovery.pointCostPerDay,
                        userPoints: userData.points || 0
                    }
                }
            }
        });
    } catch (error) {
        console.error('Daily status error:', error);
        return res.status(500).json({ success: false, message: error.message });
    }
});

// ============================================================
// POST /api/v1/daily/check-in
// ============================================================
router.post('/check-in', verifyFirebaseToken, checkFraud, async (req, res) => {
    try {
        const { uid } = req.firebaseUser;
        const config = await getConfig();
        const today = todayStr();

        const userRef = db.collection('users').doc(uid);
        const userDoc = await userRef.get();
        const userData = userDoc.exists ? userDoc.data() : {};

        // Cek sudah check-in hari ini?
        if (userData.lastCheckInDate === today) {
            return res.status(400).json({
                success: false,
                message: 'Anda sudah check-in hari ini.'
            });
        }

        // Hitung poin
        const isWeekendDay = isWeekend(today);
        let points = isWeekendDay ? config.weekendPoints : config.weekdayPoints;

        // Cek special days (format "MM-DD")
        const mmdd = today.substring(5);
        if (config.specialDays && config.specialDays[mmdd]) {
            points = config.specialDays[mmdd];
        }

        // Hitung streak
        const yesterday = daysAgo(1);
        let streak = (userData.lastCheckInDate === yesterday)
            ? (userData.checkInStreak || 0) + 1
            : 1;

        // Simpan check-in
        const checkinRef = userRef.collection('daily_checkins').doc(today);
        await checkinRef.set({
            date: today,
            points: points,
            isWeekend: isWeekendDay,
            checkedAt: admin.firestore.FieldValue.serverTimestamp(),
            recovered: false
        });

        await userRef.update({
            lastCheckInDate: today,
            checkInStreak: streak,
            points: admin.firestore.FieldValue.increment(points),
            monthCheckInCount: admin.firestore.FieldValue.increment(1)
        });

        // Audit log
        await db.collection('audit_logs').add({
            userId: uid,
            action: 'DAILY_CHECK_IN',
            points: points,
            streak: streak,
            isWeekend: isWeekendDay,
            timestamp: admin.firestore.FieldValue.serverTimestamp(),
            ip: req.ip || 'unknown',
            deviceId: req.headers['x-device-id'] || 'unknown'
        });

        return res.json({
            success: true,
            message: `Check-in hari ${streak}! +${points} poin`,
            points: points,
            streak: streak,
            isWeekend: isWeekendDay
        });
    } catch (error) {
        console.error('Check-in error:', error);
        return res.status(500).json({ success: false, message: error.message });
    }
});

// ============================================================
// POST /api/v1/daily/recover-day
// Body: { date, method: "AD" | "POINT", adTransactionId?, vendor? }
// ============================================================
router.post('/recover-day', verifyFirebaseToken, checkFraud, async (req, res) => {
    try {
        const { uid } = req.firebaseUser;
        const { date, method, adTransactionId, vendor } = req.body;
        const config = await getConfig();
        const today = todayStr();

        if (!date) {
            return res.status(400).json({ success: false, message: 'Tanggal wajib diisi.' });
        }

        // Validasi: tidak boleh recover hari ini atau masa depan
        if (date >= today) {
            return res.status(400).json({ success: false, message: 'Tanggal tidak valid.' });
        }

        // Validasi: max maxDaysBack
        const daysBack = Math.floor((new Date(today) - new Date(date)) / 86400000);
        if (daysBack > config.streakRecovery.maxDaysBack) {
            return res.status(400).json({
                success: false,
                message: `Hanya bisa recover ${config.streakRecovery.maxDaysBack} hari terakhir.`
            });
        }

        // Cek apakah tanggal ini kosong (belum check-in)
        const checkinRef = db.collection('users').doc(uid)
            .collection('daily_checkins').doc(date);
        const checkinDoc = await checkinRef.get();

        if (checkinDoc.exists && checkinDoc.data().recovered !== false) {
            return res.status(400).json({
                success: false,
                message: 'Hari ini sudah terisi.'
            });
        }

        const userRef = db.collection('users').doc(uid);
        const userDoc = await userRef.get();
        const userData = userDoc.data() || {};

        // Hitung poin hari itu
        const isWeekendDay = isWeekend(date);
        const points = isWeekendDay ? config.weekendPoints : config.weekdayPoints;

        if (method === 'AD') {
            // Cek batas iklan per hari
            const lastRecoveryAdDate = userData.lastRecoveryAdDate || '';
            const adsWatchedToday = lastRecoveryAdDate === today
                ? (userData.adsWatchedForRecovery || 0)
                : 0;

            if (adsWatchedToday >= config.streakRecovery.maxAdsPerDay) {
                return res.status(400).json({
                    success: false,
                    message: 'Batas iklan recovery hari ini sudah tercapai.'
                });
            }

            if (!adTransactionId) {
                return res.status(400).json({
                    success: false,
                    message: 'Transaction ID iklan wajib diisi.'
                });
            }

            // Cek transaksi unik
            const txRef = db.collection('ad_transactions').doc(adTransactionId);
            const txDoc = await txRef.get();
            if (txDoc.exists) {
                return res.status(400).json({
                    success: false,
                    message: 'Transaksi sudah digunakan.'
                });
            }

            // Simpan transaksi
            await txRef.set({
                userId: uid,
                type: 'RECOVERY',
                recoveredDate: date,
                vendor: vendor || 'admob',
                timestamp: admin.firestore.FieldValue.serverTimestamp()
            });

            // Update user
            await userRef.update({
                adsWatchedForRecovery: adsWatchedToday + 1,
                lastRecoveryAdDate: today,
                recoveredDaysThisMonth: admin.firestore.FieldValue.increment(1)
            });

        } else if (method === 'POINT') {
            const cost = config.streakRecovery.pointCostPerDay;
            if ((userData.points || 0) < cost) {
                return res.status(400).json({
                    success: false,
                    message: `Poin tidak cukup. Butuh ${cost} poin.`
                });
            }

            await userRef.update({
                points: admin.firestore.FieldValue.increment(-cost),
                totalPointsSpentOnRecovery: admin.firestore.FieldValue.increment(cost),
                recoveredDaysThisMonth: admin.firestore.FieldValue.increment(1)
            });

        } else {
            return res.status(400).json({ success: false, message: 'Method tidak valid.' });
        }

        // Tandai hari ter-recover
        await checkinRef.set({
            date: date,
            points: points,
            isWeekend: isWeekendDay,
            recovered: true,
            recoveredAt: admin.firestore.FieldValue.serverTimestamp(),
            recoveredBy: method,
            adTransactionId: adTransactionId || null,
            pointCost: method === 'POINT' ? config.streakRecovery.pointCostPerDay : 0
        });

        // Audit log
        await db.collection('audit_logs').add({
            userId: uid,
            action: 'STREAK_RECOVERY',
            recoveredDate: date,
            method: method,
            pointsSpent: method === 'POINT' ? config.streakRecovery.pointCostPerDay : 0,
            timestamp: admin.firestore.FieldValue.serverTimestamp(),
            ip: req.ip || 'unknown'
        });

        return res.json({
            success: true,
            message: `Hari ${date} berhasil dipulihkan!`,
            recoveredDate: date,
            method: method
        });

    } catch (error) {
        console.error('Recover day error:', error);
        return res.status(500).json({ success: false, message: error.message });
    }
});

module.exports = router;
