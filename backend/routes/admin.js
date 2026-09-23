// routes/admin.js
// Endpoint admin: atur config daily reward & ad vendors

const express = require('express');
const router = express.Router();
const admin = require('firebase-admin');
const { verifyFirebaseToken } = require('../middleware/firebaseAuth');

const db = admin.firestore();

// Middleware: cek admin
async function checkAdmin(req, res, next) {
    try {
        const { uid } = req.firebaseUser;
        const userDoc = await db.collection('users').doc(uid).get();
        if (!userDoc.exists || userDoc.data().role !== 'ADMIN') {
            return res.status(403).json({
                success: false,
                message: 'Akses admin diperlukan.'
            });
        }
        next();
    } catch (error) {
        return res.status(500).json({ success: false, message: error.message });
    }
}

// ============================================================
// GET /api/v1/admin/daily-config
// ============================================================
router.get('/daily-config', verifyFirebaseToken, checkAdmin, async (req, res) => {
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
router.put('/daily-config', verifyFirebaseToken, checkAdmin, async (req, res) => {
    try {
        const config = req.body;
        await db.collection('system').doc('daily_reward_config').set({
            ...config,
            updatedAt: admin.firestore.FieldValue.serverTimestamp(),
            updatedBy: req.firebaseUser.uid
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
router.get('/ad-config', verifyFirebaseToken, checkAdmin, async (req, res) => {
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
router.put('/ad-config', verifyFirebaseToken, checkAdmin, async (req, res) => {
    try {
        await db.collection('system').doc('ad_config').set({
            ...req.body,
            updatedAt: admin.firestore.FieldValue.serverTimestamp(),
            updatedBy: req.firebaseUser.uid
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
router.get('/audit-logs', verifyFirebaseToken, checkAdmin, async (req, res) => {
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

module.exports = router;
