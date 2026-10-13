// routes/game.js
// Endpoint Game Room — Mining Tycoon + Coin Rush

const express = require('express');
const anomaly = require('../services/anomaly');
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
const MINI_GAME_BONUS_CAP_PCT = 20.0;
const MINI_GAME_BONUS_DURATION_MS = 24 * 60 * 60 * 1000;

// ============================================================
// M46: Game Config Cache (5 minit)
// Baca config dari Firestore, cache untuk performance
// ============================================================
let gameConfigCache = { data: null, timestamp: 0 };
const GAME_CONFIG_CACHE_MS = 5 * 60 * 1000;

async function getGameConfig() {
    const now = Date.now();
    if (gameConfigCache.data && (now - gameConfigCache.timestamp) < GAME_CONFIG_CACHE_MS) {
        return gameConfigCache.data;
    }
    try {
        const doc = await db.collection('app_config').doc('game_config').get();
        gameConfigCache.data = doc.exists ? doc.data() : {};
        gameConfigCache.timestamp = now;
        return gameConfigCache.data;
    } catch (e) {
        console.error('[CONFIG] Gagal baca config:', e.message);
        return gameConfigCache.data || {};
    }
}

// Reset cache (dipanggil bila admin update config)
global.__resetGameConfigCache = () => {
    gameConfigCache = { data: null, timestamp: 0 };
    console.log('[CONFIG] Cache reset');
};

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
            finalRate: finalRate,
            baseRate: baseRate,
            permanentBonusPct: permanentBonusPct || 0,
            miniGameBonusPct: miniGameBonusPct || 0,
            totalBonusPct: totalBonusPct || 0
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

        // Ambil miner items — baca dari users.minersOwned (primary)
        let miners = Object.entries(userData.minersOwned || {}).map(([id, m]) => ({
            id, ...m
        }));

        // Fallback transitional: kalau kosong, baca dari game_miners lama
        if (miners.length === 0) {
            const minersSnapshot = await db.collection('game_miners')
                .where('userId', '==', uid)
                .get();
            minersSnapshot.forEach(doc => {
                miners.push({ id: doc.id, ...doc.data() });
            });
        }

        // Hitung total power
        const totalPowerGhs = miners.reduce((sum, m) => sum + (m.powerGhs || 0), 0);
        const pointsPerDay = calculatePointsPerDay(totalPowerGhs, miners);

        // ============ M41 FIX: Compute unclaimedMiningPoints ============
        // Guna lastMiningUpdateTimestamp (kalau ada) atau lastClaimTimestamp
        const placedMiners = miners.filter(m => m.isPlacedInRoom === true);
        // M43: Guna calculateFinalRate untuk masukkan bonus dalam display
        const baseRateForState = placedMiners.reduce((sum, m) => sum + (m.pointsPerMinute || 0), 0);
        const rateInfoForState = calculateFinalRate(baseRateForState, room, placedMiners);
        const ratePerMin = rateInfoForState.finalRate;
        
        const lastUpdate = room.lastMiningUpdateTimestamp 
            || room.lastClaimTimestamp 
            || Date.now();
        const elapsedMs = Math.max(0, Date.now() - lastUpdate);
        const elapsedMin = elapsedMs / 60000;
        
        // ============ FASA A FIX: Check minerActiveUntil ============
        // Kalau miner tak aktif, jangan accumulate unclaimed
        const minerActiveUntil = room.minerActiveUntil || 0;
        const isMinerActive = minerActiveUntil > Date.now();
        
        const storedUnclaimed = room.unclaimedMiningPoints || 0;
        let unclaimedMiningPoints;
        
        if (!isMinerActive) {
            // Miner tak aktif — freeze unclaimed (jangan accumulate)
            unclaimedMiningPoints = parseFloat(storedUnclaimed.toFixed(8));
            console.log(`[MINING] Miner TIDAK aktif — freeze unclaimed=${unclaimedMiningPoints}`);
        } else {
            // Miner aktif — accumulate seperti biasa
            const computedUnclaimed = elapsedMin * ratePerMin;
            unclaimedMiningPoints = parseFloat((storedUnclaimed + computedUnclaimed).toFixed(8));
        }
        
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
                    pointsPerDay: pointsPerDay,
                    // M43: Rate info breakdown untuk Android display
                    ratePerMin: rateInfoForState.finalRate,
                    baseRate: rateInfoForState.baseRate,
                    permanentBonusPct: rateInfoForState.permanentBonusPct,
                    miniGameBonusPct: rateInfoForState.miniGameBonusPct,
                    totalBonusPct: rateInfoForState.totalBonusPct
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
// M46: Default cooldown (boleh override dari config)
const MINING_CLAIM_COOLDOWN_MS_DEFAULT = 3 * 60 * 60 * 1000;

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

        // ============ M46: Baca config dari Firestore ============
        const gameConfig = await getGameConfig();
        const now = Date.now();
        const today = new Date(now).toISOString().slice(0, 10);
        const adClaimsMap = room.adClaimsMap || {};
        const adClaimsToday = adClaimsMap[today] || 0;
        const AD_CLAIM_LIMIT_PER_DAY = gameConfig.adClaimLimitPerDay || 5;
        const MIN_CLAIM_RTP = gameConfig.minClaimRtp || 0.01;

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
        
        if (unclaimed < MIN_CLAIM_RTP) {
            return res.status(400).json({
                success: false,
                message: `Belum ada poin yang bisa diklaim. Unclaimed: ${unclaimed.toFixed(8)} RTP (min ${MIN_CLAIM_RTP}).`
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
// POST /api/v1/game/mining/activate
// Aktifkan miner 6 jam (tonton iklan)
// Body: { transactionId, vendor }
// ============================================================
const MINER_ACTIVE_DURATION_MS = 6 * 60 * 60 * 1000; // 6 jam

router.post('/mining/activate', verifyFirebaseToken, checkFraud, async (req, res) => {
    try {
        const { uid } = req.firebaseUser;
        const { transactionId, vendor } = req.body;

        if (!transactionId || !vendor) {
            return res.status(400).json({
                success: false,
                message: 'transactionId dan vendor wajib diisi.'
            });
        }

        // Anti-replay
        const txRef = db.collection('mining_activate_transactions').doc(transactionId);
        const txDoc = await txRef.get();
        if (txDoc.exists) {
            return res.status(400).json({
                success: false,
                message: 'Transaksi aktivasi sudah diproses.'
            });
        }

        const roomRef = db.collection('game_rooms').doc(uid);
        const now = Date.now();
        const activeUntil = now + MINER_ACTIVE_DURATION_MS;

        await roomRef.set({
            minerActiveUntil: activeUntil,
            minerActivatedAt: now,
            lastMiningUpdateTimestamp: now
        }, { merge: true });

        await txRef.set({
            userId: uid,
            vendor: vendor,
            timestamp: admin.firestore.FieldValue.serverTimestamp()
        });

        console.log(`[ACTIVATE] User ${uid}: miner aktif sampai ${new Date(activeUntil).toISOString()}`);

        return res.json({
            success: true,
            minerActiveUntil: activeUntil,
            minerActivatedAt: now,
            durationMs: MINER_ACTIVE_DURATION_MS,
            message: '⛏️ Miner aktif selama 6 jam!'
        });
    } catch (error) {
        console.error('Activate miner error:', error);
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
        anomaly.trackActivity(uid, 'CLAIM');

        const roomRef = db.collection('game_rooms').doc(uid);
        const roomDoc = await roomRef.get();
        const room = roomDoc.exists ? roomDoc.data() : {};

        // M46: Baca config
        const gameConfig = await getGameConfig();
        const MINING_CLAIM_COOLDOWN_MS = gameConfig.claimCooldownMs || MINING_CLAIM_COOLDOWN_MS_DEFAULT;
        const MIN_CLAIM_RTP = gameConfig.minClaimRtp || 0.01;

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

        // ============ CHECK MINER ACTIVE (6 jam) ============
        const minerActiveUntil = room.minerActiveUntil || 0;
        if (minerActiveUntil < Date.now()) {
            return res.status(400).json({
                success: false,
                code: 'MINER_INACTIVE',
                message: 'Miner tidak aktif. Sila aktifkan semula (tonton iklan).',
                minerActiveUntil: minerActiveUntil
            });
        }
        
        // AUTO-CLAIM: tiada min claim, tiada cooldown
        // Kalau unclaimed sangat kecil, skip (elak spam Firestore)
        if (unclaimed < 0.00000001) {
            return res.json({
                success: true,
                points: 0,
                message: 'Tiada poin baru untuk diklaim.',
                skipped: true
            });
        }

        const claimAmount = parseFloat(unclaimed.toFixed(8));

        // Update user points
        await db.collection('users').doc(uid).update({
            points: admin.firestore.FieldValue.increment(claimAmount)
        });

        // Reset room (AUTO-CLAIM: tiada cooldown)
        await roomRef.set({
            unclaimedMiningPoints: 0,
            lastClaimTimestamp: Date.now(),
            lastMiningUpdateTimestamp: Date.now(),
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
            powerGhs,
            pointsPerDay,
            iconEmoji,
            bonusPermanentPct = 0
        } = catalogItem;

        // Compute pointsPerMinute dari catalog (bukan dari request)
        const pointsPerMinute = parseFloat((pointsPerDay / (24 * 60)).toFixed(8));

        console.log(`[BUY] User ${uid} beli ${lookupId} — rate=${pointsPerMinute}/min, tokenCost=${catalogItem.tokenCost}`);

        const userRef = db.collection('users').doc(uid);
        const userDoc = await userRef.get();
        const userData = userDoc.data();

        // ============ BUG FIX: Guna tokenCost (Miner Token) ============
        // (Buang duplicate pricePoints check — catalog guna tokenCost)
        const tokenCost = catalogItem.tokenCost || 0;
        if ((userData.points || 0) < tokenCost) {
            return res.status(400).json({
                success: false,
                message: `RTP tidak cukup. Butuh ${tokenCost}, punya ${userData.points || 0}.`
            });
        }

        // ============ Instance ID unik (elak overwrite) ============
        const instanceId = 'miner_' + uid.slice(0, 6) + '_' + Date.now() + '_' + Math.random().toString(36).slice(2, 8);

        // Potong RTP + simpan miner
        // NOTE: guna Date.now() — serverTimestamp() tak berfungsi dalam nested object update
        await userRef.update({
            points: admin.firestore.FieldValue.increment(-tokenCost),
            [`minersOwned.${instanceId}`]: {
                catalogId: lookupId || '',
                instanceId: instanceId || '',
                name: name || '',
                tier: tier || 'COMMON',
                iconEmoji: iconEmoji || '⛏️',
                tokenCost: tokenCost || 0,
                powerGhs: powerGhs || 0,
                pointsPerMinute: pointsPerMinute || 0,
                bonusPermanentPct: bonusPermanentPct || 0,
                isOwned: true,
                isPlacedInRoom: false,
                placedSlotIndex: -1,
                purchasedAt: Date.now()
            }
        });

        // Dual-write ke game_miners (backward compat)
        const minerRef = db.collection('game_miners').doc(instanceId);
        await minerRef.set({
            userId: uid,
            catalogId: lookupId,
            instanceId: instanceId,
            name: name,
            tier: tier,
            iconEmoji: iconEmoji,
            tokenCost: tokenCost,
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
            catalogId: lookupId,
            instanceId: instanceId,
            tokenCost: tokenCost,
            timestamp: admin.firestore.FieldValue.serverTimestamp()
        });

        return res.json({
            success: true,
            message: `Berhasil beli ${name}!`,
            data: {
                id: instanceId,
                catalogId: lookupId,
                instanceId: instanceId,
                name, tier, iconEmoji,
                tokenCost, powerGhs, pointsPerMinute,
                bonusPermanentPct: bonusPermanentPct || 0
            }
        });
    } catch (error) {
        console.error('Buy miner error:', error);
        return res.status(500).json({ success: false, message: error.message });
    }
});

// ============================================================
// GET /api/v1/game/config/exchange-rate
// Return exchange rate config (USD↔IDR, RTP↔USD)
// ============================================================
router.get('/config/exchange-rate', async (req, res) => {
    try {
        const configDoc = await db.collection('app_config').doc('exchange_rates').get();
        const config = configDoc.exists ? configDoc.data() : {};

        return res.json({
            success: true,
            data: {
                usdToIdr: config.usdToIdr || 15000,        // $1 = Rp 15.000
                rtpToUsd: config.rtpToUsd || 100,           // 100 RTP = $1
                rtpToIdr: config.rtpToIdr || 150,           // 100 RTP × 15000/100 = Rp 150
                updatedAt: config.updatedAt || Date.now()
            }
        });
    } catch (error) {
        console.error('Exchange rate config error:', error);
        return res.status(500).json({ success: false, message: error.message });
    }
});

// ============================================================
// POST /api/v1/game/config/exchange-rate (admin only)
// Update exchange rate (admin only)
// ============================================================
router.post('/config/exchange-rate', verifyFirebaseToken, async (req, res) => {
    try {
        const { uid } = req.firebaseUser;
        const { usdToIdr, rtpToUsd } = req.body;

        // Cek admin
        const userDoc = await db.collection('users').doc(uid).get();
        const userData = userDoc.exists ? userDoc.data() : {};

        if (userData.role !== 'admin' && userData.isAdmin !== true) {
            return res.status(403).json({ success: false, message: 'Admin access required' });
        }

        if (typeof usdToIdr !== 'number' || usdToIdr <= 0) {
            return res.status(400).json({ success: false, message: 'usdToIdr tidak valid' });
        }
        if (typeof rtpToUsd !== 'number' || rtpToUsd <= 0) {
            return res.status(400).json({ success: false, message: 'rtpToUsd tidak valid' });
        }

        await db.collection('app_config').doc('exchange_rates').set({
            usdToIdr,
            rtpToUsd,
            rtpToIdr: usdToIdr / rtpToUsd,
            updatedAt: Date.now(),
            updatedBy: uid
        }, { merge: true });

        return res.json({ success: true, message: 'Exchange rate updated' });
    } catch (error) {
        console.error('Update exchange rate error:', error);
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

        // Update miner — dual-write
        const userRef = db.collection('users').doc(uid);
        await userRef.update({
            [`minersOwned.${minerId}.isPlacedInRoom`]: true,
            [`minersOwned.${minerId}.placedSlotIndex`]: slotIndex
        });

        // Dual-write ke game_miners
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

        // Dual-write
        const userRef = db.collection('users').doc(uid);
        await userRef.update({
            [`minersOwned.${minerId}.isPlacedInRoom`]: false,
            [`minersOwned.${minerId}.placedSlotIndex`]: -1
        });

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
// GET /api/v1/game/notifications/:userId
// Notifications user (from Firestore)
// ============================================================
router.get('/notifications/:userId', verifyFirebaseToken, async (req, res) => {
    try {
        const { userId } = req.params;
        const { uid } = req.firebaseUser;
        
        // Security: user hanya boleh lihat notif sendiri
        if (uid !== userId) {
            return res.status(403).json({
                success: false,
                message: 'Akses ditolak'
            });
        }
        
        const limit = parseInt(req.query.limit) || 50;
        
        const snap = await db.collection('notifications')
            .where('userId', '==', userId)
            .orderBy('timestamp', 'desc')
            .limit(limit)
            .get();
        
        const notifications = [];
        snap.forEach(doc => {
            const data = doc.data();
            notifications.push({
                id: doc.id,
                userId: data.userId,
                title: data.title || '',
                body: data.body || data.message || '',
                type: data.type || 'INFO',
                isRead: data.isRead || false,
                timestamp: data.timestamp?.toMillis() || data.timestampMs || Date.now()
            });
        });
        
        return res.json({
            success: true,
            data: notifications
        });
    } catch (error) {
        console.error('[GAME-NOTIFICATIONS] Error:', error);
        return res.status(500).json({
            success: false,
            message: error.message
        });
    }
});

// ============================================================
// GET /api/v1/game/history/:userId
// History transaksi user (from Firestore transactions)
// ============================================================
router.get('/history/:userId', verifyFirebaseToken, async (req, res) => {
    try {
        const { userId } = req.params;
        const { uid } = req.firebaseUser;
        
        // Security: user hanya boleh lihat history sendiri
        if (uid !== userId) {
            return res.status(403).json({
                success: false,
                message: 'Akses ditolak'
            });
        }
        
        const limit = parseInt(req.query.limit) || 50;
        
        // Query dari Firestore transactions
        const snap = await db.collection('transactions')
            .where('userId', '==', userId)
            .orderBy('timestamp', 'desc')
            .limit(limit)
            .get();
        
        const transactions = [];
        snap.forEach(doc => {
            const data = doc.data();
            const amount = data.amount || 0;
            transactions.push({
                id: doc.id,
                userId: data.userId,
                type: data.type || 'UNKNOWN',
                title: data.title || data.description?.substring(0, 40) || 'Transaksi',
                description: data.description || '',
                amount: amount,
                isCredit: amount > 0 || data.isCredit === true,
                points: data.points || 0,
                referenceId: data.referenceId || '',
                timestamp: data.timestamp?.toMillis() || data.timestampMs || Date.now()
            });
        });
        
        return res.json({
            success: true,
            data: transactions
        });
    } catch (error) {
        console.error('[GAME-HISTORY] Error:', error);
        return res.status(500).json({
            success: false,
            message: error.message
        });
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
        const { score, durationMs, gameNonce } = req.body;

        if (typeof score !== 'number' || score < 0 || score > 10000) {
            return res.status(400).json({ success: false, message: 'Score tidak valid' });
        }

        // M45 ANTI-FRAUD: Validate duration & nonce
        const MIN_DURATION_MS = 10000;
        const MAX_SCORE_PER_SECOND = 100;

        if (typeof durationMs !== 'number' || durationMs < MIN_DURATION_MS) {
            console.warn(`[MINIGAME-FRAUD] User ${uid}: duration terlalu cepat ${durationMs}ms`);
            return res.status(400).json({ success: false, message: 'Game terlalu cepat' });
        }

        const maxScoreAllowed = (durationMs / 1000) * MAX_SCORE_PER_SECOND;
        if (score > maxScoreAllowed) {
            console.warn(`[MINIGAME-FRAUD] User ${uid}: score ${score} > max ${maxScoreAllowed}`);
            return res.status(400).json({ success: false, message: 'Score tidak wajar' });
        }

        // Cek nonce (anti-replay)
        if (gameNonce && typeof gameNonce === 'string') {
            const nonceRef = db.collection('minigame_nonces').doc(gameNonce);
            const nonceDoc = await nonceRef.get();
            if (nonceDoc.exists) {
                console.warn(`[MINIGAME-FRAUD] User ${uid}: replay nonce ${gameNonce}`);
                return res.status(400).json({ success: false, message: 'Replay terdeteksi' });
            }
            await nonceRef.set({ uid, createdAt: Date.now() });
        }

        const userRef = db.collection('users').doc(uid);
        const roomRef = db.collection('game_rooms').doc(uid);

        // ============ M44 FIX: Rate limit minigame ============
        const gameConfig = await getGameConfig();
        const MINIGAME_COOLDOWN_MS = gameConfig.minigameCooldownMs || (5 * 60 * 1000);
        const MINIGAME_DAILY_LIMIT = gameConfig.minigameLimitPerDay || 10;
        const MINI_GAME_BONUS_CAP = gameConfig.minigameBonusCap || MINI_GAME_BONUS_CAP_PCT;

        const roomDocCheck = await roomRef.get();
        const roomCheck = roomDocCheck.exists ? roomDocCheck.data() : {};

        const now = Date.now();

        // Cek cooldown
        const lastMiniGame = roomCheck.lastMiniGameAt || 0;
        const cooldownRemaining = (lastMiniGame + MINIGAME_COOLDOWN_MS) - now;
        if (cooldownRemaining > 0) {
            const minsLeft = Math.ceil(cooldownRemaining / 60000);
            return res.status(429).json({
                success: false,
                message: `Tunggu ${minsLeft} menit lagi untuk main mini-game berikutnya.`,
                cooldownRemainingMs: cooldownRemaining
            });
        }

        // Cek rate limit harian
        const today = new Date(now).toISOString().slice(0, 10);
        const miniGameDailyMap = roomCheck.miniGameDailyMap || {};
        const miniGameToday = miniGameDailyMap[today] || 0;

        if (miniGameToday >= MINIGAME_DAILY_LIMIT) {
            return res.status(429).json({
                success: false,
                message: `Limit mini-game harian (${MINIGAME_DAILY_LIMIT}) telah dicapai. Coba lagi besok.`,
                dailyLimit: MINIGAME_DAILY_LIMIT
            });
        }

        // Hitung reward (sama dengan formula Android)
        const bonusPoints = Math.min(Math.max(Math.floor(score / 10), 15), 60);
        const boostGhs = 80.0;
        const boostExpiry = now + 7200000;  // 2 jam

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
        const currentMiniBonus = roomCheck.miniGameBonusPct || 0;
        const currentExpiry = roomCheck.miniGameBonusExpiry || 0;

        // Jika expiry dah lepas, reset dari 0
        const baseBonus = (currentExpiry > now) ? currentMiniBonus : 0;
        const newMiniBonus = Math.min(baseBonus + speedBonusDropped, MINI_GAME_BONUS_CAP);
        const newExpiry = now + MINI_GAME_BONUS_DURATION_MS;

        // Update daily counter
        const updatedMiniGameDailyMap = {
            ...miniGameDailyMap,
            [today]: miniGameToday + 1
        };

        const batch = db.batch();

        // 1. Tambah points ke user
        batch.update(userRef, {
            points: admin.firestore.FieldValue.increment(bonusPoints)
        });

        // 2. Update room state (boost power + speed bonus + daily counter)
        batch.set(roomRef, {
            tempPowerBonusGhs: admin.firestore.FieldValue.increment(boostGhs),
            bonusExpiryTimestamp: boostExpiry,
            miniGameBonusPct: newMiniBonus,
            miniGameBonusExpiry: newExpiry,
            lastMiniGameAt: now,
            miniGameDailyMap: updatedMiniGameDailyMap
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

        console.log(`[MINIGAME] User ${uid}: score=${score}, +${speedBonusDropped}% bonus, total=${newMiniBonus}%, today=${miniGameToday + 1}/${MINIGAME_DAILY_LIMIT}, expiry=${new Date(newExpiry).toISOString()}`);

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
        // Baca dari user doc dulu
        const userDoc = await db.collection('users').doc(uid).get();
        const userData = userDoc.exists ? userDoc.data() : {};
        const ownedFromUser = Object.keys(userData.minersOwned || {});

        // Fallback transitional
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
            points: admin.firestore.FieldValue.increment(tokenAmount),
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

        const currentPoints = userData.points || 0;
        if (currentPoints < catalogItem.tokenCost) {
            return res.status(400).json({
                success: false,
                message: `RTP tidak cukup. Butuh ${catalogItem.tokenCost}, punya ${currentPoints.toFixed(4)}.`
            });
        }

        // Potong RTP
        await userRef.update({
            points: admin.firestore.FieldValue.increment(-catalogItem.tokenCost)
        });

        // Buat instance miner baru — dual-write
        const minerInstanceId = 'miner_' + uid.substring(0, 6) + '_' + Date.now() + '_' + Math.random().toString(36).slice(2, 8);

        // Simpan ke user doc — guna Date.now() sebab serverTimestamp() tak berfungsi nested
        await userRef.update({
            [`minersOwned.${minerInstanceId}`]: {
                catalogId: catalogItem.id || '',
                instanceId: minerInstanceId || '',
                name: catalogItem.name || '',
                tier: catalogItem.tier || 'COMMON',
                iconEmoji: catalogItem.iconEmoji || '⛏️',
                tokenCost: catalogItem.tokenCost || 0,
                powerGhs: catalogItem.powerGhs || 0,
                pointsPerMinute: catalogItem.pointsPerMinute || 0,
                bonusPermanentPct: catalogItem.bonusPermanentPct || 0,
                isOwned: true,
                isPlacedInRoom: false,
                placedSlotIndex: -1,
                unlockedAt: Date.now()
            }
        });

        // Dual-write ke game_miners — guna Date.now()
        const minerRef = db.collection('game_miners').doc(minerInstanceId);
        await minerRef.set({
            userId: uid,
            catalogId: catalogItem.id || '',
            instanceId: minerInstanceId || '',
            name: catalogItem.name || '',
            tier: catalogItem.tier || 'COMMON',
            iconEmoji: catalogItem.iconEmoji || '⛏️',
            tokenCost: catalogItem.tokenCost || 0,
            powerGhs: catalogItem.powerGhs || 0,
            pointsPerDay: catalogItem.pointsPerDay || 0,
            pointsPerMinute: catalogItem.pointsPerMinute || 0,
            bonusPermanentPct: catalogItem.bonusPermanentPct || 0,
            isOwned: true,
            isPlacedInRoom: false,
            placedSlotIndex: -1,
            unlockedAt: Date.now()
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

// ============================================================
// THERMAL MONITORING (battery temp dari HP user)
// ============================================================
router.post('/thermal', verifyFirebaseToken, async (req, res) => {
    try {
        const uid = req.user.uid;
        const { phone_temp, battery_level, is_charging } = req.body || {};

        // Validasi
        const temp = Number(phone_temp);
        const level = Number(battery_level);
        if (!Number.isFinite(temp) || temp < -20 || temp > 100) {
            return res.status(400).json({ success: false, error: 'Invalid phone_temp' });
        }

        const now = admin.firestore.FieldValue.serverTimestamp();

        // Simpan ke game_state user
        await db.collection('game_states').doc(uid).set({
            thermal: {
                phone_temp: temp,
                battery_level: Number.isFinite(level) ? level : 0,
                is_charging: !!is_charging,
                updatedAt: now
            }
        }, { merge: true });

        // Log history (max 100 entry)
        await db.collection('thermal_logs').add({
            uid,
            phone_temp: temp,
            battery_level: Number.isFinite(level) ? level : 0,
            is_charging: !!is_charging,
            createdAt: now
        });

        // Anti-fraud: suhu tinggi + hashrate tinggi = suspicious
        const hashrateFactor =
            temp >= 50 ? 0.30 :
            temp >= 45 ? 0.50 :
            temp >= 40 ? 0.70 :
            temp >= 35 ? 0.85 :
            temp >= 30 ? 0.95 : 1.00;

        res.json({
            success: true,
            thermal: {
                phone_temp: temp,
                battery_level: level,
                is_charging: !!is_charging
            },
            hashrate_factor: hashrateFactor,
            timestamp: Date.now()
        });

    } catch (err) {
        console.error('[thermal] error:', err);
        res.status(500).json({ success: false, error: err.message });
    }
});

// ============================================================
// LOCATION (region detection dari GPS user)
// ============================================================
router.post('/location', verifyFirebaseToken, async (req, res) => {
    try {
        const uid = req.user.uid;
        const { region, latitude, longitude } = req.body || {};

        const lat = Number(latitude);
        const lon = Number(longitude);

        const now = admin.firestore.FieldValue.serverTimestamp();

        await db.collection('game_states').doc(uid).set({
            location: {
                region: String(region || 'Unknown').substring(0, 32),
                latitude: Number.isFinite(lat) ? lat : 0,
                longitude: Number.isFinite(lon) ? lon : 0,
                updatedAt: now
            }
        }, { merge: true });

        res.json({
            success: true,
            location: { region, latitude: lat, longitude: lon },
            timestamp: Date.now()
        });

    } catch (err) {
        console.error('[location] error:', err);
        res.status(500).json({ success: false, error: err.message });
    }
});

module.exports = router;
