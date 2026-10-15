/**
 * Achievement Routes — User
 * Royaltree
 */

const express = require('express');
const router = express.Router();
const { verifyFirebaseToken } = require('../middleware/firebaseAuth');
const {
    getCatalog,
    checkAchievements,
    claimAchievement
} = require('../services/achievements');
const { db } = require('../config/firebase');

// ============================================================
// GET /api/v1/achievements — list catalog
// ============================================================
router.get('/', verifyFirebaseToken, async (req, res) => {
    try {
        const catalog = await getCatalog();
        return res.json({ success: true, data: catalog });
    } catch (e) {
        console.error('[ACHIEVEMENTS] list error:', e.message);
        return res.status(500).json({ success: false, message: e.message });
    }
});

// ============================================================
// GET /api/v1/achievements/my — progress user
// ============================================================
router.get('/my', verifyFirebaseToken, async (req, res) => {
    try {
        const uid = req.firebaseUser.uid;

        // Trigger check dulu — biar progress fresh
        await checkAchievements(uid);

        const catalog = await getCatalog();
        const userAchSnap = await db.collection('user_achievements')
            .where('uid', '==', uid)
            .get();

        const userAchMap = {};
        userAchSnap.forEach(d => {
            userAchMap[d.data().achievementId] = d.data();
        });

        const result = catalog.map(ach => {
            const userAch = userAchMap[ach.id] || {};
            return {
                ...ach,
                progress: userAch.progress || 0,
                target: ach.condition?.target || 0,
                unlocked: userAch.unlocked || false,
                claimed: userAch.claimed || false,
                unlockedAt: userAch.unlockedAt || null,
                claimedAt: userAch.claimedAt || null
            };
        });

        // Stats
        const stats = {
            total: result.length,
            unlocked: result.filter(a => a.unlocked).length,
            claimed: result.filter(a => a.claimed).length,
            claimable: result.filter(a => a.unlocked && !a.claimed).length
        };

        return res.json({ success: true, data: result, stats });
    } catch (e) {
        console.error('[ACHIEVEMENTS] my error:', e.message);
        return res.status(500).json({ success: false, message: e.message });
    }
});

// ============================================================
// POST /api/v1/achievements/claim/:id — klaim reward
// ============================================================
router.post('/claim/:id', verifyFirebaseToken, async (req, res) => {
    try {
        const uid = req.firebaseUser.uid;
        const achievementId = req.params.id;

        const result = await claimAchievement(uid, achievementId);

        return res.json({
            success: true,
            message: 'Reward berhasil diklaim',
            data: result
        });
    } catch (e) {
        console.error('[ACHIEVEMENTS] claim error:', e.message);
        return res.status(400).json({ success: false, message: e.message });
    }
});

module.exports = router;
