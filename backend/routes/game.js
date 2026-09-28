// routes/game.js
// Endpoint Game Room — Mining Tycoon + Coin Rush

const express = require('express');
const router = express.Router();
const admin = require('firebase-admin');
const { verifyFirebaseToken } = require('../middleware/firebaseAuth');
const { checkFraud } = require('../middleware/antiFraud');

const db = admin.firestore();

// ============================================================
// GET /api/v1/game/state
// Ambil state game user (room, miners, stats)
// ============================================================
router.get('/state', verifyFirebaseToken, async (req, res) => {
    try {
        const { uid } = req.firebaseUser;

        // Ambil user data
        const userDoc = await db.collection('users').doc(uid).get();
        if (!userDoc.exists) {
            return res.status(404).json({ success: false, message: 'User tidak ditemukan' });
        }
        const userData = userDoc.data();

        // Ambil game room state
        const roomRef = db.collection('game_rooms').doc(uid);
        const roomDoc = await roomRef.get();
        const room = roomDoc.exists ? roomDoc.data() : {
            unclaimedMiningPoints: 0,
            lastClaimTimestamp: Date.now(),
            totalMinedPointsClaimed: 0,
            tempPowerBonusGhs: 0,
            bonusExpiryTimestamp: 0,
            miniGameHighScore: 0
        };

        // Ambil miner items
        const minersSnapshot = await db.collection('game_miners')
            .where('userId', '==', uid)
            .get();

        const miners = [];
        minersSnapshot.forEach(doc => {
            miners.push({ id: doc.id, ...doc.data() });
        });

        return res.json({
            success: true,
            data: {
                user: {
                    points: userData.points || 0,
                    balance: userData.balance || 0
                },
                room: room,
                miners: miners
            }
        });
    } catch (error) {
        console.error('Game state error:', error);
        return res.status(500).json({ success: false, message: error.message });
    }
});

// ============================================================
// POST /api/v1/game/mining/claim
// Claim mining points
// ============================================================
router.post('/mining/claim', verifyFirebaseToken, checkFraud, async (req, res) => {
    try {
        const { uid } = req.firebaseUser;

        const roomRef = db.collection('game_rooms').doc(uid);
        const roomDoc = await roomRef.get();
        const room = roomDoc.exists ? roomDoc.data() : {};

        const unclaimed = room.unclaimedMiningPoints || 0;
        if (unclaimed < 1) {
            return res.status(400).json({
                success: false,
                message: 'Belum ada poin yang bisa diklaim.'
            });
        }

        const claimAmount = Math.floor(unclaimed);

        // Update user points
        await db.collection('users').doc(uid).update({
            points: admin.firestore.FieldValue.increment(claimAmount)
        });

        // Reset room
        await roomRef.set({
            unclaimedMiningPoints: unclaimed - claimAmount,
            lastClaimTimestamp: Date.now(),
            totalMinedPointsClaimed: (room.totalMinedPointsClaimed || 0) + claimAmount,
            tempPowerBonusGhs: room.tempPowerBonusGhs || 0,
            bonusExpiryTimestamp: room.bonusExpiryTimestamp || 0,
            miniGameHighScore: room.miniGameHighScore || 0
        }, { merge: true });

        // Audit log
        await db.collection('audit_logs').add({
            userId: uid,
            action: 'GAME_MINING_CLAIM',
            points: claimAmount,
            timestamp: admin.firestore.FieldValue.serverTimestamp()
        });

        return res.json({
            success: true,
            message: `Berhasil klaim ${claimAmount} RTP!`,
            points: claimAmount
        });
    } catch (error) {
        console.error('Claim mining error:', error);
        return res.status(500).json({ success: false, message: error.message });
    }
});

// ============================================================
// POST /api/v1/game/mining/buy
// Beli miner item (potong points)
// Body: { minerId, tier, name, pricePoints, powerGhs, pointsPerMinute, iconEmoji }
// ============================================================
router.post('/mining/buy', verifyFirebaseToken, checkFraud, async (req, res) => {
    try {
        const { uid } = req.firebaseUser;
        const { minerId, tier, name, pricePoints, powerGhs, pointsPerMinute, iconEmoji } = req.body;

        if (!minerId || !pricePoints) {
            return res.status(400).json({ success: false, message: 'Data miner tidak lengkap' });
        }

        const userRef = db.collection('users').doc(uid);
        const userDoc = await userRef.get();
        const userData = userDoc.data();

        if ((userData.points || 0) < pricePoints) {
            return res.status(400).json({
                success: false,
                message: 'Poin tidak mencukupi.'
            });
        }

        // Potong points
        await userRef.update({
            points: admin.firestore.FieldValue.increment(-pricePoints)
        });

        // Simpan miner ke inventory
        const minerRef = db.collection('game_miners').doc(minerId);
        await minerRef.set({
            userId: uid,
            name: name || 'Miner',
            tier: tier || 'COMMON',
            iconEmoji: iconEmoji || '⛏️',
            pricePoints: pricePoints,
            powerGhs: powerGhs || 0,
            pointsPerMinute: pointsPerMinute || 0,
            isOwned: true,
            isPlacedInRoom: false,
            placedSlotIndex: -1,
            purchasedAt: admin.firestore.FieldValue.serverTimestamp()
        });

        // Audit log
        await db.collection('audit_logs').add({
            userId: uid,
            action: 'GAME_MINER_BUY',
            minerId: minerId,
            price: pricePoints,
            timestamp: admin.firestore.FieldValue.serverTimestamp()
        });

        return res.json({
            success: true,
            message: `Berhasil beli ${name}!`,
            data: { id: minerId, ...req.body }
        });
    } catch (error) {
        console.error('Buy miner error:', error);
        return res.status(500).json({ success: false, message: error.message });
    }
});

// ============================================================
// POST /api/v1/game/mining/place
// Pasang miner di slot
// Body: { minerId, slotIndex }
// ============================================================
router.post('/mining/place', verifyFirebaseToken, async (req, res) => {
    try {
        const { uid } = req.firebaseUser;
        const { minerId, slotIndex } = req.body;

        if (!minerId || slotIndex === undefined || slotIndex < 0 || slotIndex > 5) {
            return res.status(400).json({ success: false, message: 'Slot tidak valid' });
        }

        // Cek slot kosong
        const slotCheck = await db.collection('game_miners')
            .where('userId', '==', uid)
            .where('isPlacedInRoom', '==', true)
            .where('placedSlotIndex', '==', slotIndex)
            .get();

        if (!slotCheck.empty) {
            return res.status(400).json({
                success: false,
                message: 'Slot sudah terisi.'
            });
        }

        // Update miner
        await db.collection('game_miners').doc(minerId).update({
            isPlacedInRoom: true,
            placedSlotIndex: slotIndex,
            placedAt: admin.firestore.FieldValue.serverTimestamp()
        });

        return res.json({ success: true, message: 'Miner dipasang.' });
    } catch (error) {
        console.error('Place miner error:', error);
        return res.status(500).json({ success: false, message: error.message });
    }
});

// ============================================================
// POST /api/v1/game/mining/unplace
// Lepas miner dari slot
// Body: { minerId }
// ============================================================
router.post('/mining/unplace', verifyFirebaseToken, async (req, res) => {
    try {
        const { uid } = req.firebaseUser;
        const { minerId } = req.body;

        if (!minerId) {
            return res.status(400).json({ success: false, message: 'Miner ID wajib' });
        }

        await db.collection('game_miners').doc(minerId).update({
            isPlacedInRoom: false,
            placedSlotIndex: -1
        });

        return res.json({ success: true, message: 'Miner dilepas.' });
    } catch (error) {
        console.error('Unplace miner error:', error);
        return res.status(500).json({ success: false, message: error.message });
    }
});

// ============================================================
// GET /api/v1/game/leaderboard
// Top 20 global mining
// ============================================================
router.get('/leaderboard', verifyFirebaseToken, async (req, res) => {
    try {
        // Ambil dari users, sort by points (atau field baru)
        const snapshot = await db.collection('users')
            .orderBy('points', 'desc')
            .limit(20)
            .get();

        const leaderboard = [];
        let rank = 1;
        snapshot.forEach(doc => {
            const data = doc.data();
            leaderboard.push({
                rank: rank++,
                userId: doc.id,
                name: data.name || 'Anonim',
                points: data.points || 0,
                tier: data.tier || 'FREE'
            });
        });

        return res.json({ success: true, data: leaderboard });
    } catch (error) {
        console.error('Leaderboard error:', error);
        return res.status(500).json({ success: false, message: error.message });
    }
});

// ============================================================
// GET /api/v1/game/stats
// Stats user (total mined, rank, games played)
// ============================================================
router.get('/stats', verifyFirebaseToken, async (req, res) => {
    try {
        const { uid } = req.firebaseUser;

        const roomDoc = await db.collection('game_rooms').doc(uid).get();
        const room = roomDoc.exists ? roomDoc.data() : {};

        // Cek rank user
        const userDoc = await db.collection('users').doc(uid).get();
        const userPoints = userDoc.exists ? (userDoc.data().points || 0) : 0;

        const higherSnapshot = await db.collection('users')
            .where('points', '>', userPoints)
            .count()
            .get();

        const rank = higherSnapshot.data().count + 1;

        return res.json({
            success: true,
            data: {
                totalMined: room.totalMinedPointsClaimed || 0,
                unclaimed: room.unclaimedMiningPoints || 0,
                rank: rank,
                highScore: room.miniGameHighScore || 0
            }
        });
    } catch (error) {
        console.error('Stats error:', error);
        return res.status(500).json({ success: false, message: error.message });
    }
});

module.exports = router;
