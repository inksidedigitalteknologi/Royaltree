// routes/ads.js
// Endpoint reward video ads (AdMob + multi-vendor)

const express = require('express');
const router = express.Router();
const admin = require('firebase-admin');
const crypto = require('crypto');
const { verifyFirebaseToken } = require('../middleware/firebaseAuth');
const { checkFraud, adCooldown } = require('../middleware/antiFraud');

const db = admin.firestore();

// Reward per video (bisa diubah admin)
const DEFAULT_REWARD_POINTS = 50;   // legacy fallback
const DEFAULT_MAX_ADS_PER_DAY = 20; // fallback kalau config Firestore kosong

/**
 * Baca maxAdsPerDay dari Firestore (system/ad_config).
 * Admin bisa ubah kapan saja via admin panel → tanpa deploy ulang.
 */
async function getMaxAdsPerDay() {
    try {
        const doc = await db.collection('system').doc('ad_config').get();
        const config = doc.exists ? doc.data() : {};
        const val = parseInt(config.maxAdsPerDay);
        return Number.isFinite(val) && val > 0 ? val : DEFAULT_MAX_ADS_PER_DAY;
    } catch (e) {
        console.error('[ADS/CONFIG] Error baca maxAdsPerDay:', e.message);
        return DEFAULT_MAX_ADS_PER_DAY;
    }
}

// ==== Reward random 2-5 RTP per iklan ====
function getRandomReward() {
    // Weighted: 2=40%, 3=30%, 4=20%, 5=10%
    const roll = Math.random() * 100;
    if (roll < 40) return 2;
    if (roll < 70) return 3;
    if (roll < 90) return 4;
    return 5;
}

// ============================================================
// GET /api/v1/ads/config
// Ambil config vendor ads
// ============================================================
router.get('/config', verifyFirebaseToken, async (req, res) => {
    try {
        const doc = await db.collection('system').doc('ad_config').get();
        const config = doc.exists ? doc.data() : {
            primary: 'admob',
            fallback: [],
            admob: {
                enabled: true,
                appId: 'ca-app-pub-5261912953437884~3728027368',
                rewardedUnitId: 'ca-app-pub-5261912953437884/9112305270'
            }
        };
        return res.json({ success: true, data: config });
    } catch (error) {
        return res.status(500).json({ success: false, message: error.message });
    }
});

// ============================================================
// GET /api/v1/ads/status
// Cek status ads user (berapa yang sudah ditonton hari ini)
// ============================================================
router.get('/status', verifyFirebaseToken, async (req, res) => {
    try {
        const { uid } = req.firebaseUser;
        const today = new Date().toISOString().split('T')[0];

        const userDoc = await db.collection('users').doc(uid).get();
        const userData = userDoc.exists ? userDoc.data() : {};

        const lastAdDate = userData.lastAdDate || '';
        const todayAdsWatched = lastAdDate === today
            ? (userData.todayAdsWatched || 0)
            : 0;

        const maxAdsPerDay = await getMaxAdsPerDay();
        const remaining = Math.max(0, maxAdsPerDay - todayAdsWatched);
        const canWatch = remaining > 0;

        return res.json({
            success: true,
            data: {
                todayAdsWatched: todayAdsWatched,
                maxAdsPerDay: maxAdsPerDay,
                remaining: remaining,
                canWatch: canWatch,
                reason: canWatch ? 'ok' : 'limit',
                rewardPerVideo: DEFAULT_REWARD_POINTS
            }
        });
    } catch (error) {
        return res.status(500).json({ success: false, message: error.message });
    }
});

// ============================================================
// POST /api/v1/ads/reward
// Client kirim setelah user selesai nonton
// Body: { vendor, transactionId, customData? }
// ============================================================
router.post('/reward', verifyFirebaseToken, checkFraud, async (req, res) => {
    try {
        const { uid } = req.firebaseUser;
        const { vendor, transactionId } = req.body;

        if (!vendor || !transactionId) {
            console.error('[ADS/REWARD] 400: vendor/txId kosong. vendor=', vendor, 'txId=', transactionId, 'body=', req.body);
            return res.status(400).json({
                success: false,
                message: 'Vendor dan transactionId wajib diisi.'
            });
        }

        const today = new Date().toISOString().split('T')[0];
        const userRef = db.collection('users').doc(uid);
        const userDoc = await userRef.get();
        const userData = userDoc.exists ? userDoc.data() : {};

        // Cek batas harian (baca dari config)
        const lastAdDate = userData.lastAdDate || '';
        const todayAdsWatched = lastAdDate === today
            ? (userData.todayAdsWatched || 0)
            : 0;

        const maxAdsPerDay = await getMaxAdsPerDay();

        if (todayAdsWatched >= maxAdsPerDay) {
            console.error('[ADS/REWARD] 400: batas harian. todayAdsWatched=', todayAdsWatched, 'MAX=', maxAdsPerDay);
            return res.status(400).json({
                success: false,
                message: `Batas tonton video hari ini sudah tercapai (${maxAdsPerDay} video).`
            });
        }

        // Cek transaksi unik (cegah replay)
        const txRef = db.collection('ad_transactions').doc(transactionId);
        const txDoc = await txRef.get();
        if (txDoc.exists) {
            console.error('[ADS/REWARD] 400: transaksi sudah dipakai. txId=', transactionId);
            return res.status(400).json({
                success: false,
                message: 'Transaksi sudah digunakan.'
            });
        }

        // Generate random reward 2-5 RTP
        const rewardPoints = getRandomReward();

        // Simpan transaksi
        await txRef.set({
            userId: uid,
            type: 'REWARD_AD',
            vendor: vendor,
            points: rewardPoints,
            timestamp: admin.firestore.FieldValue.serverTimestamp()
        });

        // Update user + recentAdTimestamps + Miner Token
        const now = Date.now();
        const recentAds = (userData.recentAdTimestamps || []).filter(ts => now - ts < 300000);
        recentAds.push(now);

        // Cek limit token harian
        const lastTokenDate = userData.lastTokenDate || '';
        const todayTokens = lastTokenDate === today ? (userData.todayTokensEarned || 0) : 0;
        const MAX_TOKENS_PER_DAY = 20;
        const tokenToGrant = todayTokens < MAX_TOKENS_PER_DAY ? 1 : 0;

        await userRef.update({
            points: admin.firestore.FieldValue.increment(rewardPoints),
            minerTokens: admin.firestore.FieldValue.increment(tokenToGrant),
            todayTokensEarned: todayTokens + tokenToGrant,
            lastTokenDate: today,
            todayAdsWatched: todayAdsWatched + 1,
            lastAdDate: today,
            recentAdTimestamps: recentAds
        });

        // Set cooldown 30 detik
        adCooldown.set(uid, now);

        // Audit log
        await db.collection('audit_logs').add({
            userId: uid,
            action: 'REWARD_AD',
            vendor: vendor,
            points: rewardPoints,
            timestamp: admin.firestore.FieldValue.serverTimestamp(),
            ip: req.ip || 'unknown'
        });

        return res.json({
            success: true,
            message: `+${rewardPoints} RTP & +${tokenToGrant} Miner Token!`,
            points: rewardPoints,
            tokens: tokenToGrant,
            todayAdsWatched: todayAdsWatched + 1,
            minerTokens: (userData.minerTokens || 0) + tokenToGrant
        });

    } catch (error) {
        console.error('Ad reward error:', error);
        return res.status(500).json({ success: false, message: error.message });
    }
});

// ============================================================
// GET /api/v1/ads/admob-ssv
// Callback dari AdMob (SSV) — untuk produksi
// ============================================================
router.get('/admob-ssv', async (req, res) => {
    // TODO: implementasi verifikasi signature AdMob SSV
    // Sekarang: skip dulu (SSV off di AdMob Console)
    console.log('AdMob SSV callback:', req.query);
    return res.status(200).send('OK');
});

module.exports = router;
