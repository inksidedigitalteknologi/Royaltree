// routes/game.js
// Endpoint Game Room — Mining Tycoon + Coin Rush

const express = require('express');
const router = express.Router();
const admin = require('firebase-admin');
const { verifyFirebaseToken } = require('../middleware/firebaseAuth');
const { checkFraud } = require('../middleware/antiFraud');
const { createNotification } = require('../utils/notifications');

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
        pointsPerDay: 0.144,
        pointsPerMinute: 0.0001,
        bonusPermanentPct: 0,
        description: 'Miner dasar untuk pemula'
    },
    {
        id: 'miner_rare',
        name: 'Micro Rig',
        tier: 'RARE',
        iconEmoji: '🔧',
        tokenCost: 50,
        powerGhs: 50,
        pointsPerDay: 0.72,
        pointsPerMinute: 0.0005,
        bonusPermanentPct: 0.10,
        description: 'Miner dengan power lebih besar'
    },
    {
        id: 'miner_epic',
        name: 'Quantum Rig',
        tier: 'EPIC',
        iconEmoji: '⚡',
        tokenCost: 200,
        powerGhs: 200,
        pointsPerDay: 2.88,
        pointsPerMinute: 0.002,
        bonusPermanentPct: 0.23,
        description: 'Miner dengan teknologi quantum'
    },
    {
        id: 'miner_legendary',
        name: 'Titan Miner',
        tier: 'LEGENDARY',
        iconEmoji: '💎',
        tokenCost: 1000,
        powerGhs: 1000,
        pointsPerDay: 14.4,
        pointsPerMinute: 0.01,
        bonusPermanentPct: 1.20,
        description: 'Miner raksasa dengan power luar biasa'
    },
    {
        id: 'miner_mythic',
        name: 'Infinity Core',
        tier: 'MYTHIC',
        iconEmoji: '🌌',
        tokenCost: 5000,
        powerGhs: 5000,
        pointsPerDay: 72,
        pointsPerMinute: 0.05,
        bonusPermanentPct: 3.00,
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
    return totalPowerGhs * 0.0144;
}

// ============================================================
// M43 FEATURE: Bonus Speed System
// - Permanent bonus dari miner (bonusPermanentPct)
// - Temporary bonus dari mini-game (miniGameBonusPct)
// Formula: finalRate = baseRate * (1 + permanent% + miniGame%)
// Cap mini-game: 20%/hari (INTERNAL — jangan dedahkan ke client)
// ============================================================
const MINI_GAME_BONUS_CAP_PCT = 20.0;   // Hidden cap harian
const MINI_GAME_BONUS_DURATION_MS = 24 * 60 * 60 * 1000;  // 24 jam

function calculateFinalRate(baseRate, room, placedMiners) {
    // 1. Bonus permanen dari miner yang placed
    let permanentBonusPct = 0;
    (placedMiners || []).forEach(m => {
        if (m.bonusPermanentPct) permanentBonusPct += m.bonusPermanentPct;
    });

    // 2. Bonus dari mini-game (jika belum expiry)
    let miniGameBonusPct = 0;
    if (room.miniGameBonusExpiry && room.miniGameBonusExpiry > Date.now()) {
        miniGameBonusPct = room.miniGameBonusPct || 0;
        // Safety: cap di MINI_GAME_BONUS_CAP_PCT
        if (miniGameBonusPct > MINI_GAME_BONUS_CAP_PCT) {
            miniGameBonusPct = MINI_GAME_BONUS_CAP_PCT;
        }
    }

    const totalBonusPct = permanentBonusPct + miniGameBonusPct;
    const finalRate = baseRate * (1 + totalBonusPct / 100);

    return {
        baseRate: parseFloat(baseRate.toFixed(10)),
        permanentBonusPct: parseFloat(permanentBonusPct.toFixed(4)),
        miniGameBonusPct: parseFloat(miniGameBonusPct.toFixed(4)),
        totalBonusPct: parseFloat(totalBonusPct.toFixed(4)),
        finalRate: parseFloat(finalRate.toFixed(10))
    };
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
        let room = roomDoc.exists ? roomDoc.data() : {
            unclaimedMiningPoints: 0,
            lastClaimTimestamp: Date.now(),
            totalMinedPointsClaimed: 0,
            tempPowerBonusGhs: 0,
            bonusExpiryTimestamp: 0,
            miniGameHighScore: 0
        };

        // M35 FIX: Check bonus expiry (24 jam)
        if (room.bonusExpiryTimestamp && room.bonusExpiryTimestamp < Date.now()) {
            await roomRef.set({
                tempPowerBonusGhs: 0,
                bonusExpiryTimestamp: 0
            }, { merge: true });
            room.tempPowerBonusGhs = 0;
            room.bonusExpiryTimestamp = 0;
            console.log(`[MINING] Bonus expired untuk ${uid}`);
        }

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

        // ============ M41 FIX: Compute unclaimedMiningPoints ============
        // Guna lastMiningUpdateTimestamp (kalau ada) atau lastClaimTimestamp
        const placedMiners = miners.filter(m => m.isPlacedInRoom === true);
        const ratePerMin = placedMiners.reduce((sum, m) => sum + (m.pointsPerMinute || 0), 0);
        
        const lastUpdate = room.lastMiningUpdateTimestamp 
            || room.lastClaimTimestamp 
            || Date.now();
        const elapsedMs = Math.max(0, Date.now() - lastUpdate);
        const elapsedMin = elapsedMs / 60000;
        
        // Kalau backend punya unclaimedMiningPoints, guna. Kalau tak, compute.
        const storedUnclaimed = room.unclaimedMiningPoints || 0;
        const computedUnclaimed = elapsedMin * ratePerMin;
        const unclaimedMiningPoints = parseFloat((storedUnclaimed + computedUnclaimed).toFixed(8));
        
        // Update lastMiningUpdateTimestamp supaya tidak double-count
        await roomRef.set({
            unclaimedMiningPoints: unclaimedMiningPoints,
            lastMiningUpdateTimestamp: Date.now(),
            lastClaimTimestamp: room.lastClaimTimestamp || Date.now()
        }, { merge: true });
        
        console.log(`[MINING] User ${uid}: unclaimed=${unclaimedMiningPoints}, rate=${ratePerMin}/min, elapsed=${elapsedMin.toFixed(2)}min`);

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
                    unclaimedMiningPoints: unclaimedMiningPoints,
                    lastClaimTimestamp: room.lastClaimTimestamp || Date.now(),
                    lastMiningClaimAt: room.lastMiningClaimAt || 0,
                    unlockedSlots: room.unlockedSlots || 2,
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
const MINING_CLAIM_COOLDOWN_MS = 3 * 60 * 60 * 1000; // 3 jam

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

        // ============ M42 FIX: /mining/claim-with-ad BYPASS cooldown ============
        // User "bayar" dengan tonton iklan, jadi tak perlu tunggu 3 jam.
        // Tapi ada rate limit harian untuk cegah abuse.
        const now = Date.now();
        const today = new Date(now).toISOString().slice(0, 10);
        const adClaimsMap = room.adClaimsMap || {};
        const adClaimsToday = adClaimsMap[today] || 0;
        const AD_CLAIM_LIMIT_PER_DAY = 5;

        if (adClaimsToday >= AD_CLAIM_LIMIT_PER_DAY) {
            return res.status(429).json({
                success: false,
                message: `Limit klaim iklan harian (${AD_CLAIM_LIMIT_PER_DAY}) telah dicapai. Sila tunggu cooldown biasa.`
            });
        }

        // ============ M41 FIX: Compute fresh unclaimed ============
        const minersSnap = await db.collection('game_miners')
            .where('userId', '==', uid)
            .where('isPlacedInRoom', '==', true)
            .get();
        
        const placedMinersArr = [];
        minersSnap.forEach(doc => {
            placedMinersArr.push({ id: doc.id, ...doc.data() });
        });
        
        // M43: Guna calculateFinalRate untuk masukkan bonus permanen + minigame
        const baseRateFresh = placedMinersArr.reduce((sum, m) => sum + (m.pointsPerMinute || 0), 0);
        const rateCalcFresh = calculateFinalRate(baseRateFresh, room, placedMinersArr);
        const ratePerMinFresh = rateCalcFresh.finalRate;
        const lastUpdateFresh = room.lastMiningUpdateTimestamp 
            || room.lastClaimTimestamp 
            || Date.now();
        const elapsedMsFresh = Math.max(0, Date.now() - lastUpdateFresh);
        const elapsedMinFresh = elapsedMsFresh / 60000;
        
        const storedUnclaimedFresh = room.unclaimedMiningPoints || 0;
        const computedUnclaimedFresh = elapsedMinFresh * ratePerMinFresh;
        const unclaimed = parseFloat((storedUnclaimedFresh + computedUnclaimedFresh).toFixed(8));
        
        console.log(`[CLAIM-AD] User ${uid}: unclaimed=${unclaimed}, base=${rateCalcFresh.baseRate}, perm=${rateCalcFresh.permanentBonusPct}%, mini=${rateCalcFresh.miniGameBonusPct}%, final=${ratePerMinFresh}/min`);
        
        if (unclaimed < 0.01) {
            return res.status(400).json({
                success: false,
                message: `Belum ada poin yang bisa diklaim. Unclaimed: ${unclaimed.toFixed(8)} RTP (min 0.01).`
            });
        }

        const claimAmount = parseFloat(unclaimed.toFixed(8));  // Preserve 8 decimals (BTC-style)
        const totalClaimed = (room.totalMinedPointsClaimed || 0) + claimAmount;

        // Update user points
        await db.collection('users').doc(uid).update({
            points: admin.firestore.FieldValue.increment(claimAmount)
        });

        // Reset room + set cooldown + increment ad claim count
        const updatedAdClaimsMap = {
            ...adClaimsMap,
            [today]: adClaimsToday + 1
        };

        await roomRef.set({
            unclaimedMiningPoints: unclaimed - claimAmount,
            lastClaimTimestamp: now,
            lastMiningClaimAt: now,
            totalMinedPointsClaimed: totalClaimed,
            tempPowerBonusGhs: room.tempPowerBonusGhs || 0,
            bonusExpiryTimestamp: room.bonusExpiryTimestamp || 0,
            miniGameHighScore: room.miniGameHighScore || 0,
            adClaimsMap: updatedAdClaimsMap
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
            message: `Berhasil klaim ${claimAmount} RTP! Iklan + cooldown 3 jam.`,
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

        // ============ M41 FIX: Compute fresh unclaimed ============
        const minersSnap = await db.collection('game_miners')
            .where('userId', '==', uid)
            .where('isPlacedInRoom', '==', true)
            .get();
        
        const placedMiners = [];
        minersSnap.forEach(doc => {
            placedMiners.push({ id: doc.id, ...doc.data() });
        });
        
        // M43: Guna calculateFinalRate untuk masukkan bonus permanen + minigame
        const baseRate = placedMiners.reduce((sum, m) => sum + (m.pointsPerMinute || 0), 0);
        const rateCalc = calculateFinalRate(baseRate, room, placedMiners);
        const ratePerMin = rateCalc.finalRate;
        const lastUpdate = room.lastMiningUpdateTimestamp 
            || room.lastClaimTimestamp 
            || Date.now();
        const elapsedMs = Math.max(0, Date.now() - lastUpdate);
        const elapsedMin = elapsedMs / 60000;
        
        const storedUnclaimed = room.unclaimedMiningPoints || 0;
        const computedUnclaimed = elapsedMin * ratePerMin;
        const unclaimed = parseFloat((storedUnclaimed + computedUnclaimed).toFixed(8));
        
        console.log(`[CLAIM] User ${uid}: unclaimed=${unclaimed}, base=${rateCalc.baseRate}, perm=${rateCalc.permanentBonusPct}%, mini=${rateCalc.miniGameBonusPct}%, final=${ratePerMin}/min`);
        
        if (unclaimed < 0.01) {
            return res.status(400).json({
                success: false,
                message: `Belum ada poin yang bisa diklaim. Unclaimed: ${unclaimed.toFixed(8)} RTP (min 0.01).`
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

        // Notifikasi
        await createNotification(
            uid,
            '⛏️ Mining Berhasil!',
            `Klaim ${claimAmount.toFixed(8)} RTP dari rak mining.`,
            'REWARD'
        );

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
        const { minerId, catalogId } = req.body;
        const lookupId = catalogId || minerId;

        if (!lookupId) {
            return res.status(400).json({ success: false, message: 'catalogId wajib diisi' });
        }

        // SECURITY: Cari catalog (server-side) — JANGAN percaya req.body
        const catalogItem = MINER_CATALOG.find(m => m.id === lookupId);
        if (!catalogItem) {
            return res.status(400).json({ success: false, message: 'Miner tidak valid' });
        }

        // Override semua field dari catalog
        const {
            name,
            tier,
            pricePoints,
            powerGhs,
            pointsPerDay,
            iconEmoji
        } = catalogItem;

        // Compute pointsPerMinute dari catalog (bukan dari request)
        const pointsPerMinute = parseFloat((pointsPerDay / (24 * 60)).toFixed(8));

        console.log(`[BUY] User ${uid} beli ${lookupId} — rate=${pointsPerMinute}/min, price=${pricePoints}`);

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
        // M43 FIX: Guna field dari CATALOG (server-side), bukan req.body
        await minerRef.set({
            userId: uid,
            name: name,
            tier: tier,
            iconEmoji: iconEmoji,
            pricePoints: pricePoints,
            powerGhs: powerGhs,
            pointsPerMinute: pointsPerMinute || 0,
            bonusPermanentPct: bonusPermanentPct || 0,
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
            data: {
                id: minerId,
                name, tier, iconEmoji,
                pricePoints, powerGhs, pointsPerMinute,
                bonusPermanentPct: bonusPermanentPct || 0
            }
        });
    } catch (error) {
        console.error('Buy miner error:', error);
        return res.status(500).json({ success: false, message: error.message });
    }
});

// ============================================================
// GET /api/v1/game/mining/rate-info
// Return breakdown rate + bonus untuk Android display
// ============================================================
router.get('/mining/rate-info', verifyFirebaseToken, async (req, res) => {
    try {
        const { uid } = req.firebaseUser;

        const roomDoc = await db.collection('game_rooms').doc(uid).get();
        const room = roomDoc.exists ? roomDoc.data() : {};

        const minersSnap = await db.collection('game_miners')
            .where('userId', '==', uid)
            .where('isPlacedInRoom', '==', true)
            .get();

        const placedMiners = [];
        minersSnap.forEach(doc => {
            placedMiners.push({ id: doc.id, ...doc.data() });
        });

        const baseRate = placedMiners.reduce((sum, m) => sum + (m.pointsPerMinute || 0), 0);
        const rateInfo = calculateFinalRate(baseRate, room, placedMiners);

        return res.json({
            success: true,
            ...rateInfo,
            placedMinerCount: placedMiners.length
        });
    } catch (error) {
        console.error('Rate info error:', error);
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
// POST /api/v1/game/mining/unlock-slot
// Beli slot mining tambahan (slot 3-6)
// Body: { slotIndex }  // 2, 3, 4, 5 (0-indexed)
// ============================================================
const SLOT_PRICES = {
    2: 500,      // slot 3
    3: 1500,     // slot 4
    4: 3000,     // slot 5
    5: 5000      // slot 6
};

router.post('/mining/unlock-slot', verifyFirebaseToken, checkFraud, async (req, res) => {
    try {
        const { uid } = req.firebaseUser;
        const { slotIndex } = req.body;

        // Validasi slot index (2-5 = slot 3-6)
        if (slotIndex === undefined || !SLOT_PRICES.hasOwnProperty(slotIndex)) {
            return res.status(400).json({
                success: false,
                message: 'Slot tidak valid. Hanya slot 3-6 yang perlu dibeli.'
            });
        }

        const price = SLOT_PRICES[slotIndex];

        // Cek user points
        const userRef = db.collection('users').doc(uid);
        const userDoc = await userRef.get();
        if (!userDoc.exists) {
            return res.status(404).json({ success: false, message: 'User tidak ditemukan' });
        }
        const userData = userDoc.data();
        const userPoints = userData.points || 0;

        if (userPoints < price) {
            return res.status(400).json({
                success: false,
                message: `Saldo tidak cukup. Butuh ${price} RTP, saldo Anda ${userPoints} RTP.`
            });
        }

        // Cek unlockedSlots sekarang
        const roomRef = db.collection('game_rooms').doc(uid);
        const roomDoc = await roomRef.get();
        const room = roomDoc.exists ? roomDoc.data() : {};
        const currentUnlocked = room.unlockedSlots || 2;

        // Cek sudah unlock atau belum
        if (slotIndex < currentUnlocked) {
            return res.status(400).json({
                success: false,
                message: 'Slot ini sudah terbuka.'
            });
        }

        // Slot unlock harus berurutan
        if (slotIndex > currentUnlocked) {
            return res.status(400).json({
                success: false,
                message: `Slot harus dibuka secara berurutan. Buka slot ${currentUnlocked + 1} dulu.`
            });
        }

        // Potong points + unlock slot
        const batch = db.batch();
        batch.update(userRef, {
            points: admin.firestore.FieldValue.increment(-price)
        });
        batch.set(roomRef, {
            unlockedSlots: slotIndex + 1
        }, { merge: true });

        // Audit log
        const auditRef = db.collection('audit_logs').doc();
        batch.set(auditRef, {
            userId: uid,
            action: 'MINING_UNLOCK_SLOT',
            slotIndex,
            price,
            timestamp: admin.firestore.FieldValue.serverTimestamp()
        });

        await batch.commit();

        // Notifikasi
        await createNotification(
            uid,
            '🔓 Slot Terbuka!',
            `Slot ${slotIndex + 1} berhasil dibuka (-${price} RTP)`,
            'REWARD'
        );

        return res.json({
            success: true,
            message: `Slot ${slotIndex + 1} berhasil dibuka!`,
            data: {
                unlockedSlots: slotIndex + 1,
                pricePaid: price
            }
        });
    } catch (error) {
        console.error('Unlock slot error:', error);
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
// ============================================================
// POST /api/v1/game/minigame/finish
// Sync reward mini-game ke backend
// Body: { score }
// ============================================================
router.post('/minigame/finish', verifyFirebaseToken, checkFraud, async (req, res) => {
    try {
        const { uid } = req.firebaseUser;
        const { score } = req.body;

        if (typeof score !== 'number' || score < 0 || score > 10000) {
            return res.status(400).json({ success: false, message: 'Score tidak valid' });
        }

        // Hitung reward (sama dengan formula Android)
        const bonusPoints = Math.min(Math.max(Math.floor(score / 10), 15), 60);
        const boostGhs = 80.0;
        const boostExpiry = Date.now() + 7200000;  // 2 jam

        const userRef = db.collection('users').doc(uid);
        const roomRef = db.collection('game_rooms').doc(uid);

        // M43: Random speed bonus drop (0.01% - 0.03%)
        // 60% common (+0.01%), 30% uncommon (+0.02%), 10% rare (+0.03%)
        const roll = Math.random();
        let speedBonusDropped = 0.01;
        if (roll >= 0.90) {
            speedBonusDropped = 0.03;      // 10% rare
        } else if (roll >= 0.60) {
            speedBonusDropped = 0.02;      // 30% uncommon
        } else {
            speedBonusDropped = 0.01;      // 60% common
        }

        // Baca current state untuk stack + cap
        const roomDoc = await roomRef.get();
        const roomData = roomDoc.exists ? roomDoc.data() : {};
        const currentMiniBonus = roomData.miniGameBonusPct || 0;
        const currentExpiry = roomData.miniGameBonusExpiry || 0;
        const now = Date.now();

        // Jika expiry dah lepas, reset dari 0
        const baseBonus = (currentExpiry > now) ? currentMiniBonus : 0;
        const newMiniBonus = Math.min(baseBonus + speedBonusDropped, MINI_GAME_BONUS_CAP_PCT);
        const newExpiry = now + MINI_GAME_BONUS_DURATION_MS;

        const batch = db.batch();

        // 1. Tambah points ke user
        batch.update(userRef, {
            points: admin.firestore.FieldValue.increment(bonusPoints)
        });

        // 2. Update room state (boost power + speed bonus)
        batch.set(roomRef, {
            tempPowerBonusGhs: admin.firestore.FieldValue.increment(boostGhs),
            bonusExpiryTimestamp: boostExpiry,
            miniGameBonusPct: newMiniBonus,
            miniGameBonusExpiry: newExpiry,
            lastMiniGameAt: now
        }, { merge: true });

        // 3. Audit log
        const auditRef = db.collection('audit_logs').doc();
        batch.set(auditRef, {
            userId: uid,
            action: 'MINIGAME_FINISH',
            score,
            bonusPoints,
            boostGhs,
            speedBonusDropped,
            newMiniBonus,
            timestamp: admin.firestore.FieldValue.serverTimestamp()
        });

        await batch.commit();

        console.log(`[MINIGAME] User ${uid}: score=${score}, +${speedBonusDropped}% bonus, total=${newMiniBonus}%, expiry=${new Date(newExpiry).toISOString()}`);

        return res.json({
            success: true,
            data: {
                bonusPoints,
                boostGhs,
                boostExpiry,
                speedBonusDropped,
                totalSpeedBonus: newMiniBonus,
                speedBonusExpiry: newExpiry
            }
        });
    } catch (error) {
        console.error('Mini game finish error:', error);
        return res.status(500).json({ success: false, message: error.message });
    }
});

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
