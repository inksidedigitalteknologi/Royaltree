// backend/routes/referral.js
// Referral API v1.15 — stats, list, tiers
const express = require('express');
const router = express.Router();
const { verifyFirebaseToken } = require('../middleware/firebaseAuth');
const { admin, db } = require('../config/firebase');
const tiersConfig = require('../config/tiers.json');

function computeTier(referredCount) {
    const tiers = tiersConfig.tiers;
    let current = tiers[0];
    let next = null;
    for (let i = 0; i < tiers.length; i++) {
        if (referredCount >= tiers[i].min && referredCount <= tiers[i].max) {
            current = tiers[i];
            next = tiers[i + 1] || null;
            break;
        }
    }
    return { current, next };
}

// GET /api/v1/referral/tiers (public)
router.get('/tiers', (req, res) => {
    return res.json({ success: true, data: tiersConfig });
});

// GET /api/v1/referral/stats
router.get('/stats', verifyFirebaseToken, async (req, res) => {
    try {
        const { uid } = req.firebaseUser;
        const userDoc = await db.collection('users').doc(uid).get();
        if (!userDoc.exists) {
            return res.status(404).json({ success: false, message: 'User tidak ditemukan' });
        }
        const userData = userDoc.data();
        const snapshot = await db.collection('users').where('referredBy', '==', uid).get();
        const totalInvited = snapshot.size;
        let activeCount = 0, pendingCount = 0, totalEarned = 0;
        snapshot.forEach(doc => {
            const d = doc.data();
            const isActive = (d.referralEarning && d.referralEarning > 0) || (d.totalEarned && d.totalEarned > 0);
            if (isActive) activeCount++; else pendingCount++;
            if (d.referralEarning) totalEarned += d.referralEarning;
        });
        const { current, next } = computeTier(totalInvited);
        const progress = next ? Math.min(100, Math.round(((totalInvited - current.min) / (next.min - current.min)) * 100)) : 100;
        return res.json({
            success: true,
            data: {
                referralCode: userData.referralCode || '',
                referralLink: 'https://royaltree.app/invite/' + (userData.referralCode || ''),
                totalInvited, activeCount, pendingCount,
                totalEarned: Math.round(totalEarned * 100) / 100,
                affiliateBalance: userData.affiliateBalance || 0,
                affiliateBalanceAvailable: userData.affiliateBalanceAvailable || 0,
                affiliateBalancePending: userData.affiliateBalancePending || 0,
                tier: { current, next, progress, remainingToNext: next ? Math.max(0, next.min - totalInvited) : 0 }
            }
        });
    } catch (error) {
        console.error('Error /referral/stats:', error);
        return res.status(500).json({ success: false, message: 'Gagal ambil stats referral' });
    }
});

// GET /api/v1/referral/list?page=1&limit=20
router.get('/list', verifyFirebaseToken, async (req, res) => {
    try {
        const { uid } = req.firebaseUser;
        const page = Math.max(1, parseInt(req.query.page) || 1);
        const limit = Math.min(50, Math.max(1, parseInt(req.query.limit) || 20));
        const offset = (page - 1) * limit;
        let snapshot;
        try {
            snapshot = await db.collection('users').where('referredBy', '==', uid).orderBy('createdAt', 'desc').get();
        } catch (idxErr) {
            snapshot = await db.collection('users').where('referredBy', '==', uid).get();
        }
        const allDocs = snapshot.docs;
        const total = allDocs.length;
        const pageDocs = allDocs.slice(offset, offset + limit);
        const items = pageDocs.map(doc => {
            const d = doc.data();
            const isActive = (d.referralEarning && d.referralEarning > 0) || (d.totalEarned && d.totalEarned > 0);
            return {
                uid: doc.id,
                username: d.name || d.username || 'User',
                avatar: d.avatar || null,
                city: d.city || null,
                joinedAt: d.createdAt && d.createdAt.toMillis ? d.createdAt.toMillis() : null,
                status: isActive ? 'active' : 'pending',
                earnedFromThem: Math.round((d.referralEarning || 0) * 100) / 100
            };
        });
        return res.json({
            success: true,
            data: { items, page, limit, total, hasMore: offset + limit < total }
        });
    } catch (error) {
        console.error('Error /referral/list:', error);
        return res.status(500).json({ success: false, message: 'Gagal ambil list referral' });
    }
});

module.exports = router;
