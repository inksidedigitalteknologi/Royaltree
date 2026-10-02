// routes/game.js
// Endpoint Game Room — Mining Tycoon + Coin Rush

const express = require('express');
const router = express.Router();
const admin = require('firebase-admin');
const { verifyFirebaseToken } = require('../middleware/firebaseAuth');
const { checkFraud } = require('../middleware/antiFraud');

const db = admin.firestore();

// ============================================================
// KATALOG MINER DEFAULT
// ============================================================
const MINER_CATALOG = [
    {
        id: 'miner_common',
        name: 'Pico Miner',
        tier: 'COMMON',
        iconEmoji: '⛏️',
        tokenCost: 10,
        powerGhs: 10,
        pointsPerDay: 1,
        description: 'Miner dasar untuk pemula'
    },
    {
        id: 'miner_rare',
        name: 'Micro Rig',
        tier: 'RARE',
        iconEmoji: '🔧',
        tokenCost: 50,
        powerGhs: 50,
        pointsPerDay: 4,
        description: 'Miner dengan power lebih besar'
    },
    {
        id: 'miner_epic',
        name: 'Quantum Rig',
        tier: 'EPIC',
        iconEmoji: '⚡',
        tokenCost: 200,
        powerGhs: 200,
        pointsPerDay: 15,
        description: 'Miner dengan teknologi quantum'
    },
    {
        id: 'miner_legendary',
        name: 'Titan Miner',
        tier: 'LEGENDARY',
        iconEmoji: '💎',
        tokenCost: 1000,
        powerGhs: 1000,
        pointsPerDay: 60,
        description: 'Miner raksasa dengan power luar biasa'
    },
    {
        id: 'miner_mythic',
        name: 'Infinity Core',
        tier: 'MYTHIC',
        iconEmoji: '🌌',
        tokenCost: 5000,
        powerGhs: 5000,
        pointsPerDay: 300,
        description: 'Miner legendaris dengan power tak terbatas'
    }
];

// ============================================================
// HELPER: Hitung token reward
// ============================================================
function getTokenReward(source) {
    const rewards = {
        'AD': 1,
        'CHECKIN': 2,
        'MISSION': 2,
        'REFERRAL': 5
    };
    return rewards[source] || 1;
}

// ============================================================
// HELPER: Hitung poin dari total power
// ============================================================
function calculatePointsPerDay(totalPowerGhs, miners = []) {
    // Utamakan sum pointsPerDay dari catalog (tepat)
    if (Array.isArray(miners) && miners.length > 0) {
        const sum = miners.reduce((s, m) => s + (m.pointsPerDay || 0), 0);
        if (sum > 0) return sum;
    }
    // Fallback: formula ratio (kalau miners array kosong)
    return totalPowerGhs * 0.06;
}

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

        // Hitung total power
        const totalPowerGhs = miners.reduce((sum, m) => sum + (m.powerGhs || 0), 0);
        const pointsPerDay = calculatePointsPerDay(totalPowerGhs, miners);

        return res.json({
            success: true,
            data: {
                user: {
                    points: userData.points || 0,
                    balance: userData.balance || 0,
                    minerTokens: userData.minerTokens || 0,
                    todayTokensEarned: userData.todayTokensEarned || 0
                },
                room: {
                    ...room,
                    totalPowerGhs: totalPowerGhs,
                    pointsPerDay: pointsPerDay
                },
                miners: miners,
                catalog: MINER_CATALOG
            }
        });
    } catch (error) {
        console.error('Game state error:', error);
        return res.status(500).json({ success: false, message: error.message });
    }
});

// ============================================================
// POST /api/v1/game/mining/claim-with-ad
// Claim mining points — WAJIB nonton iklan dulu
// Body: { transactionId, vendor }
// ============================================================
const MINING_CLAIM_COOLDOWN_MS = 60 * 60 * 1000; // 1 jam

router.post('/mining/claim-with-ad', verifyFirebaseToken, checkFraud, async (req, res) => {
    try {
        const { uid } = req.firebaseUser;
        const { transactionId, vendor } = req.body;

        // Validasi transactionId (anti-replay)
        if (!transactionId || !vendor) {
            return res.status(400).json({
                success: false,
                message: 'transactionId dan vendor wajib diisi.'
            });
        }

        // Cek anti-replay
        const txRef = db.collection('mining_claim_transactions').doc(transactionId);
        const txDoc = await txRef.get();
        if (txDoc.exists) {
            return res.status(400).json({
                success: false,
                message: 'Transaksi sudah pernah diproses.'
            });
        }

        const roomRef = db.collection('game_rooms').doc(uid);
        const roomDoc = await roomRef.get();
        const room = roomDoc.exists ? roomDoc.data() : {};

        // Cek cooldown
        const lastClaimAt = room.lastMiningClaimAt || 0;
        const now = Date.now();
        const cooldownRemaining = (lastClaimAt + MINING_CLAIM_COOLDOWN_MS) - now;

        if (cooldownRemaining > 0) {
            return res.status(429).json({
                success: false,
                message: `Tunggu ${Math.ceil(cooldownRemaining / 60000)} menit lagi untuk klaim berikutnya.`,
                cooldownRemainingMs: cooldownRemaining,
                nextClaimAt: lastClaimAt + MINING_CLAIM_COOLDOWN_MS
            });
        }

        const unclaimed = room.unclaimedMiningPoints || 0;
        if (unclaimed < 1) {
            return res.status(400).json({
                success: false,
                message: 'Belum ada poin yang bisa diklaim.'
            });
        }

        const claimAmount = parseFloat(unclaimed.toFixed(8));  // Preserve 8 decimals (BTC-style)
        const totalClaimed = (room.totalMinedPointsClaimed || 0) + claimAmount;

        // Update user points
        await db.collection('users').doc(uid).update({
            points: admin.firestore.FieldValue.increment(claimAmount)
        });

        // Reset room + set cooldown
        await roomRef.set({
            unclaimedMiningPoints: unclaimed - claimAmount,
            lastClaimTimestamp: now,
            lastMiningClaimAt: now,
            totalMinedPointsClaimed: totalClaimed,
            tempPowerBonusGhs: room.tempPowerBonusGhs || 0,
            bonusExpiryTimestamp: room.bonusExpiryTimestamp || 0,
            miniGameHighScore: room.miniGameHighScore || 0
        }, { merge: true });

        // Simpan transaction (anti-replay)
        await txRef.set({
            uid,
            transactionId,
            vendor,
            claimAmount,
            claimedAt: admin.firestore.FieldValue.serverTimestamp(),
            claimedAtMs: now
        });

        // Audit log
        await db.collection('audit_logs').add({
            userId: uid,
            action: 'GAME_MINING_CLAIM_WITH_AD',
            points: claimAmount,
            vendor,
            transactionId,
            timestamp: admin.firestore.FieldValue.serverTimestamp()
        });

        const nextClaimAt = now + MINING_CLAIM_COOLDOWN_MS;

        return res.json({
            success: true,
            message: `Berhasil klaim ${claimAmount} RTP! Iklan + cooldown 1 jam.`,
            points: claimAmount,
            nextClaimAt,
            cooldownRemainingMs: MINING_CLAIM_COOLDOWN_MS
        });
    } catch (error) {
        console.error('Claim mining with ad error:', error);
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

        const claimAmount = parseFloat(unclaimed.toFixed(8));  // Preserve 8 decimals (BTC-style)

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

// ============================================================
// GET /api/v1/game/miners/catalog
// Katalog miner (untuk ditampilkan di toko)
// ============================================================
router.get('/miners/catalog', verifyFirebaseToken, async (req, res) => {
    try {
        const { uid } = req.firebaseUser;

        // Ambil miner yang sudah dimiliki user
        const ownedSnapshot = await db.collection('game_miners')
            .where('userId', '==', uid)
            .get();

        const ownedIds = new Set();
        const ownedDetail = {};
        ownedSnapshot.forEach(doc => {
            const data = doc.data();
            if (data.catalogId) {
                ownedIds.add(data.catalogId);
                // Simpan detail (instance terakhir) — untuk isPlacedInRoom + placedSlotIndex
                if (!ownedDetail[data.catalogId]) {
                    ownedDetail[data.catalogId] = {
                        isPlacedInRoom: data.isPlacedInRoom || false,
                        placedSlotIndex: data.placedSlotIndex ?? -1,
                        minerInstanceId: doc.id
                    };
                }
            }
        });

        // Gabung katalog + status owned
        const catalog = MINER_CATALOG.map(miner => ({
            ...miner,
            isOwned: ownedIds.has(miner.id),
            isPlacedInRoom: ownedDetail[miner.id]?.isPlacedInRoom || false,
            placedSlotIndex: ownedDetail[miner.id]?.placedSlotIndex ?? -1,
            minerInstanceId: ownedDetail[miner.id]?.minerInstanceId || null,
            ownedCount: ownedSnapshot.docs.filter(d => d.data().catalogId === miner.id).length
        }));

        return res.json({
            success: true,
            data: {
                catalog: catalog,
                ownedCount: ownedIds.size
            }
        });
    } catch (error) {
        console.error('Miner catalog error:', error);
        return res.status(500).json({ success: false, message: error.message });
    }
});

// ============================================================
// POST /api/v1/game/miners/claim-token
// Claim Miner Token dari aktivitas (iklan, check-in, misi)
// Body: { source, transactionId }
// ============================================================
router.post('/miners/claim-token', verifyFirebaseToken, checkFraud, async (req, res) => {
    try {
        const { uid } = req.firebaseUser;
        const { source, transactionId } = req.body;

        if (!source || !transactionId) {
            return res.status(400).json({ success: false, message: 'source & transactionId wajib' });
        }

        const today = new Date().toISOString().split('T')[0];
        const userRef = db.collection('users').doc(uid);
        const userDoc = await userRef.get();
        const userData = userDoc.exists ? userDoc.data() : {};

        // Cek limit harian token
        const lastTokenDate = userData.lastTokenDate || '';
        const todayTokens = lastTokenDate === today ? (userData.todayTokensEarned || 0) : 0;

        const MAX_TOKENS_PER_DAY = 20;
        if (todayTokens >= MAX_TOKENS_PER_DAY) {
            return res.status(400).json({
                success: false,
                message: 'Limit token harian tercapai.'
            });
        }

        // Cek transaction unik
        const txRef = db.collection('token_transactions').doc(transactionId);
        const txDoc = await txRef.get();
        if (txDoc.exists) {
            return res.status(400).json({ success: false, message: 'Transaksi sudah digunakan.' });
        }

        const tokenAmount = getTokenReward(source);

        // Simpan transaksi
        await txRef.set({
            userId: uid,
            type: 'MINT_TOKEN',
            source: source,
            amount: tokenAmount,
            timestamp: admin.firestore.FieldValue.serverTimestamp()
        });

        // Update user
        await userRef.update({
            minerTokens: admin.firestore.FieldValue.increment(tokenAmount),
            todayTokensEarned: todayTokens + tokenAmount,
            lastTokenDate: today
        });

        // Audit log
        await db.collection('audit_logs').add({
            userId: uid,
            action: 'CLAIM_MINER_TOKEN',
            source: source,
            amount: tokenAmount,
            timestamp: admin.firestore.FieldValue.serverTimestamp()
        });

        return res.json({
            success: true,
            message: `+${tokenAmount} Miner Token!`,
            tokens: tokenAmount,
            totalTokens: (userData.minerTokens || 0) + tokenAmount
        });
    } catch (error) {
        console.error('Claim token error:', error);
        return res.status(500).json({ success: false, message: error.message });
    }
});

// ============================================================
// POST /api/v1/game/miners/unlock
// Unlock miner pakai token
// Body: { catalogId }
// ============================================================
router.post('/miners/unlock', verifyFirebaseToken, checkFraud, async (req, res) => {
    try {
        const { uid } = req.firebaseUser;
        const { catalogId } = req.body;

        if (!catalogId) {
            return res.status(400).json({ success: false, message: 'catalogId wajib' });
        }

        const catalogItem = MINER_CATALOG.find(m => m.id === catalogId);
        if (!catalogItem) {
            return res.status(404).json({ success: false, message: 'Miner tidak ditemukan di katalog' });
        }

        const userRef = db.collection('users').doc(uid);
        const userDoc = await userRef.get();
        const userData = userDoc.exists ? userDoc.data() : {};

        const currentTokens = userData.minerTokens || 0;
        if (currentTokens < catalogItem.tokenCost) {
            return res.status(400).json({
                success: false,
                message: `Token tidak cukup. Butuh ${catalogItem.tokenCost}, punya ${currentTokens}.`
            });
        }

        // Potong token
        await userRef.update({
            minerTokens: admin.firestore.FieldValue.increment(-catalogItem.tokenCost)
        });

        // Buat instance miner baru
        const minerInstanceId = 'miner_' + uid.substring(0, 6) + '_' + Date.now();
        const minerRef = db.collection('game_miners').doc(minerInstanceId);
        await minerRef.set({
            userId: uid,
            catalogId: catalogItem.id,
            name: catalogItem.name,
            tier: catalogItem.tier,
            iconEmoji: catalogItem.iconEmoji,
            powerGhs: catalogItem.powerGhs,
            pointsPerDay: catalogItem.pointsPerDay,
            isOwned: true,
            isPlacedInRoom: false,
            placedSlotIndex: -1,
            unlockedAt: admin.firestore.FieldValue.serverTimestamp()
        });

        // Audit log
        await db.collection('audit_logs').add({
            userId: uid,
            action: 'UNLOCK_MINER',
            catalogId: catalogId,
            tier: catalogItem.tier,
            tokenCost: catalogItem.tokenCost,
            timestamp: admin.firestore.FieldValue.serverTimestamp()
        });

        return res.json({
            success: true,
            message: `Berhasil unlock ${catalogItem.name}!`,
            minerId: minerInstanceId,
            tokensRemaining: currentTokens - catalogItem.tokenCost
        });
    } catch (error) {
        console.error('Unlock miner error:', error);
        return res.status(500).json({ success: false, message: error.message });
    }
});

module.exports = router;
