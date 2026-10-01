// routes/affiliate.js
// Endpoint untuk balance, withdraw, dan history komisi affiliate

const express = require('express');
const router = express.Router();
const admin = require('firebase-admin');
const { verifyFirebaseToken } = require('../middleware/firebaseAuth');

const db = admin.firestore();

const MIN_WITHDRAW_USD = 10.00;

// ============================================================
// GET /api/v1/affiliate/balance
// Balance + pending + available
// ============================================================
router.get('/balance', verifyFirebaseToken, async (req, res) => {
    try {
        const { uid } = req.firebaseUser;
        const userDoc = await db.collection('users').doc(uid).get();

        if (!userDoc.exists) {
            return res.status(404).json({ success: false, message: 'User tidak ditemukan.' });
        }

        const data = userDoc.data();
        return res.json({
            success: true,
            data: {
                balance: data.affiliateBalance || 0,
                available: data.affiliateBalanceAvailable || 0,
                pending: data.affiliateBalancePending || 0,
                totalEarned: data.affiliateTotalEarned || 0,
                totalWithdrawn: data.affiliateTotalWithdrawn || 0,
                minWithdraw: MIN_WITHDRAW_USD
            }
        });
    } catch (error) {
        console.error('Get balance error:', error);
        return res.status(500).json({ success: false, message: error.message });
    }
});

// ============================================================
// GET /api/v1/affiliate/transactions
// History komisi
// ============================================================
router.get('/transactions', verifyFirebaseToken, async (req, res) => {
    try {
        const { uid } = req.firebaseUser;
        const limit = parseInt(req.query.limit) || 50;

        const snapshot = await db.collection('affiliate_transactions')
            .where('affiliateId', '==', uid)
            .orderBy('createdAtMs', 'desc')
            .limit(limit)
            .get();

        const txs = snapshot.docs.map(doc => ({ id: doc.id, ...doc.data() }));
        return res.json({ success: true, total: txs.length, data: txs });
    } catch (error) {
        console.error('Get transactions error:', error);
        return res.status(500).json({ success: false, message: error.message });
    }
});

// ============================================================
// POST /api/v1/affiliate/withdraw
// Request withdraw
// Body: { amount, method, destination }
// ============================================================
router.post('/withdraw', verifyFirebaseToken, async (req, res) => {
    try {
        const { uid } = req.firebaseUser;
        const { amount, method, destination } = req.body;

        // Validasi
        if (!amount || amount < MIN_WITHDRAW_USD) {
            return res.status(400).json({
                success: false,
                message: `Minimum withdraw adalah $${MIN_WITHDRAW_USD}.`
            });
        }

        if (!destination || destination.trim() === '') {
            return res.status(400).json({
                success: false,
                message: 'Tujuan withdraw wajib diisi (email PayPal).'
            });
        }

        const userRef = db.collection('users').doc(uid);
        const userDoc = await userRef.get();

        if (!userDoc.exists) {
            return res.status(404).json({ success: false, message: 'User tidak ditemukan.' });
        }

        const data = userDoc.data();
        const available = data.affiliateBalanceAvailable || 0;

        if (available < amount) {
            return res.status(400).json({
                success: false,
                message: `Balance available hanya $${available.toFixed(2)}. Tidak cukup.`
            });
        }

        // Buat withdrawal record
        const wdRef = db.collection('affiliate_withdrawals').doc();
        await wdRef.set({
            id: wdRef.id,
            affiliateId: uid,
            amount: parseFloat(amount),
            currency: 'USD',
            method: method || 'PAYPAL',
            destination: destination.trim(),
            status: 'PENDING',
            requestedAt: admin.firestore.FieldValue.serverTimestamp(),
            requestedAtMs: Date.now(),
            processedAt: null,
            adminNotes: ''
        });

        // Kurangkan balance available (hold sementara)
        await userRef.update({
            affiliateBalanceAvailable: admin.firestore.FieldValue.increment(-amount),
            affiliateBalance: admin.firestore.FieldValue.increment(-amount)
        });

        return res.json({
            success: true,
            message: `Withdraw $${amount} berhasil diajukan. Akan diproses dalam 1-3 hari kerja.`,
            data: { withdrawalId: wdRef.id }
        });

    } catch (error) {
        console.error('Withdraw error:', error);
        return res.status(500).json({ success: false, message: error.message });
    }
});

// ============================================================
// GET /api/v1/affiliate/withdrawals
// History withdraw
// ============================================================
router.get('/withdrawals', verifyFirebaseToken, async (req, res) => {
    try {
        const { uid } = req.firebaseUser;
        const limit = parseInt(req.query.limit) || 20;

        const snapshot = await db.collection('affiliate_withdrawals')
            .where('affiliateId', '==', uid)
            .orderBy('requestedAtMs', 'desc')
            .limit(limit)
            .get();

        const wds = snapshot.docs.map(doc => ({ id: doc.id, ...doc.data() }));
        return res.json({ success: true, total: wds.length, data: wds });
    } catch (error) {
        console.error('Get withdrawals error:', error);
        return res.status(500).json({ success: false, message: error.message });
    }
});

// ============================================================
// POST /api/v1/affiliate/register-fcm
// Simpan FCM token user
// Body: { fcmToken, deviceId }
// ============================================================
router.post('/register-fcm', verifyFirebaseToken, async (req, res) => {
    try {
        const { uid } = req.firebaseUser;
        const { fcmToken, deviceId } = req.body;

        if (!fcmToken) {
            return res.status(400).json({ success: false, message: 'fcmToken wajib diisi.' });
        }

        const update = {
            fcmToken,
            fcmUpdatedAt: admin.firestore.FieldValue.serverTimestamp()
        };
        if (deviceId) update.lastDeviceId = deviceId;
        if (req.ip) update.lastLoginIp = req.ip;

        await db.collection('users').doc(uid).update(update);

        return res.json({ success: true, message: 'FCM token tersimpan.' });
    } catch (error) {
        console.error('Register FCM error:', error);
        return res.status(500).json({ success: false, message: error.message });
    }
});

module.exports = router;
