// routes/ads.js
// Endpoint reward video ads (AdMob + multi-vendor)

const express = require('express');
const router = express.Router();
const admin = require('firebase-admin');
const crypto = require('crypto');
const { verifyFirebaseToken } = require('../middleware/firebaseAuth');
const { checkFraud } = require('../middleware/antiFraud');

const db = admin.firestore();

// Reward per video (bisa diubah admin)
const DEFAULT_REWARD_POINTS = 50;
const MAX_ADS_PER_DAY = 10;

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

        return res.json({
            success: true,
            data: {
                todayAdsWatched: todayAdsWatched,
                maxAdsPerDay: MAX_ADS_PER_DAY,
                remaining: Math.max(0, MAX_ADS_PER_DAY - todayAdsWatched),
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
            return res.status(400).json({
                success: false,
                message: 'Vendor dan transactionId wajib diisi.'
            });
        }

        const today = new Date().toISOString().split('T')[0];
        const userRef = db.collection('users').doc(uid);
        const userDoc = await userRef.get();
        const userData = userDoc.exists ? userDoc.data() : {};

        // Cek batas harian
        const lastAdDate = userData.lastAdDate || '';
        const todayAdsWatched = lastAdDate === today
            ? (userData.todayAdsWatched || 0)
            : 0;

        if (todayAdsWatched >= MAX_ADS_PER_DAY) {
            return res.status(400).json({
                success: false,
                message: 'Batas tonton video hari ini sudah tercapai.'
            });
        }

        // Cek transaksi unik (cegah replay)
        const txRef = db.collection('ad_transactions').doc(transactionId);
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
            type: 'REWARD_AD',
            vendor: vendor,
            points: DEFAULT_REWARD_POINTS,
            timestamp: admin.firestore.FieldValue.serverTimestamp()
        });

        // Update user
        await userRef.update({
            points: admin.firestore.FieldValue.increment(DEFAULT_REWARD_POINTS),
            todayAdsWatched: todayAdsWatched + 1,
            lastAdDate: today
        });

        // Audit log
        await db.collection('audit_logs').add({
            userId: uid,
            action: 'REWARD_AD',
            vendor: vendor,
            points: DEFAULT_REWARD_POINTS,
            timestamp: admin.firestore.FieldValue.serverTimestamp(),
            ip: req.ip || 'unknown'
        });

        return res.json({
            success: true,
            message: `+${DEFAULT_REWARD_POINTS} poin dari video!`,
            points: DEFAULT_REWARD_POINTS,
            todayAdsWatched: todayAdsWatched + 1
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
