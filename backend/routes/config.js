// ============================================================
// PUBLIC CONFIG ENDPOINT — untuk APK fetch config dari Firestore
// Fallback ke default kalau Firestore kosong
// ============================================================
const express = require('express');
const router = express.Router();
const { db } = require('../config/firebase');

const CONFIG_MAP = {
    shop: 'shop_config',
    referral: 'referral_config',
    transfer: 'transfer_config',
    spin: 'spin_config',
    daily: 'daily_config',
    ads: 'ad_config',
    game: 'game_config',
    miner_catalog: 'miner_catalog'
};

// ============================================================
// DEFAULT CONFIG — fallback kalau Firestore kosong
// ============================================================
const DEFAULTS = {
    shop: {
        tiers: {
            FREE:    { priceUsd: 0,     maxMinerSlots: 1,  affiliateRate: 0    },
            STARTER: { priceUsd: 2.99,  maxMinerSlots: 3,  affiliateRate: 0.10 },
            PREMIUM: { priceUsd: 5.99,  maxMinerSlots: 5,  affiliateRate: 0.15 },
            VIP:     { priceUsd: 14.95, maxMinerSlots: 10, affiliateRate: 0.20 },
            ROYAL:   { priceUsd: 19.98, maxMinerSlots: 20, affiliateRate: 0.25 }
        },
        rtpPacks: {
            'rtp_pack_1000':  { rtp: 1000,  priceUsd: 0.99,  name: 'Starter Pack', emoji: '💰' },
            'rtp_pack_5000':  { rtp: 5000,  priceUsd: 3.99,  name: 'Popular Pack', emoji: '💎' },
            'rtp_pack_15000': { rtp: 15000, priceUsd: 9.99,  name: 'Pro Pack',     emoji: '👑' },
            'rtp_pack_50000': { rtp: 50000, priceUsd: 24.99, name: 'Whale Pack',   emoji: '🐋' }
        },
        minerPacks: {
            'miner_pack_starter': { priceUsd: 1.99,  name: 'Starter Miner', emoji: '⛏️', miners: [{ catalogId: 'miner_common', qty: 3 }] },
            'miner_pack_pro':     { priceUsd: 4.99,  name: 'Pro Miner',     emoji: '🔧', miners: [{ catalogId: 'miner_rare', qty: 2 }, { catalogId: 'miner_common', qty: 3 }] },
            'miner_pack_elite':   { priceUsd: 9.99,  name: 'Elite Miner',   emoji: '⚡', miners: [{ catalogId: 'miner_epic', qty: 1 }, { catalogId: 'miner_rare', qty: 2 }, { catalogId: 'miner_common', qty: 3 }] },
            'miner_pack_legend':  { priceUsd: 19.99, name: 'Legend Miner',  emoji: '💎', miners: [{ catalogId: 'miner_legendary', qty: 1 }, { catalogId: 'miner_epic', qty: 2 }, { catalogId: 'miner_rare', qty: 3 }] }
        },
        bundles: {
            'bundle_starter': { rtp: 5000,  priceUsd: 4.99,  name: 'Starter Bundle', emoji: '🎁', miners: [{ catalogId: 'miner_common', qty: 3 }] },
            'bundle_pro':     { rtp: 15000, priceUsd: 14.99, name: 'Pro Bundle',     emoji: '🎁', miners: [{ catalogId: 'miner_rare', qty: 2 }, { catalogId: 'miner_common', qty: 3 }] },
            'bundle_elite':   { rtp: 50000, priceUsd: 29.99, name: 'Elite Bundle',   emoji: '🎁', miners: [{ catalogId: 'miner_epic', qty: 1 }, { catalogId: 'miner_rare', qty: 2 }, { catalogId: 'miner_common', qty: 3 }] }
        }
    },
    miner_catalog: {
        miners: [
            { id: 'miner_common',    name: 'Pico Miner',    tier: 'COMMON',    iconEmoji: '⛏️', tokenCost: 10,   powerGhs: 0.000001, pointsPerDay: 0.144, pointsPerMinute: 0.0001, bonusPermanentPct: 0,    description: 'Miner dasar untuk pemula' },
            { id: 'miner_rare',      name: 'Micro Rig',     tier: 'RARE',      iconEmoji: '🔧', tokenCost: 50,   powerGhs: 0.000005, pointsPerDay: 0.72,  pointsPerMinute: 0.0005, bonusPermanentPct: 0.10, description: 'Miner dengan power lebih besar' },
            { id: 'miner_epic',      name: 'Quantum Rig',   tier: 'EPIC',      iconEmoji: '⚡', tokenCost: 200,  powerGhs: 0.00002,  pointsPerDay: 2.88,  pointsPerMinute: 0.002,  bonusPermanentPct: 0.23, description: 'Miner dengan teknologi quantum' },
            { id: 'miner_legendary', name: 'Titan Miner',   tier: 'LEGENDARY', iconEmoji: '💎', tokenCost: 1000, powerGhs: 0.0001,   pointsPerDay: 14.4,  pointsPerMinute: 0.01,   bonusPermanentPct: 1.20, description: 'Miner raksasa dengan power luar biasa' },
            { id: 'miner_mythic',    name: 'Infinity Core', tier: 'MYTHIC',    iconEmoji: '🌌', tokenCost: 5000, powerGhs: 0.0005,   pointsPerDay: 72,    pointsPerMinute: 0.05,   bonusPermanentPct: 3.00, description: 'Miner legendaris dengan power tak terbatas' }
        ]
    },
    referral: {
        syaratTier: 'PREMIUM',
        syaratOrder: 3,
        tiers: [
            { id: 'starter',  name: 'Starter',  min: 0,   max: 0,      commission: 0,  icon: '🌱' },
            { id: 'bronze',   name: 'Bronze',   min: 1,   max: 10,     commission: 10, icon: '🥉' },
            { id: 'silver',   name: 'Silver',   min: 11,  max: 25,     commission: 15, icon: '🥈' },
            { id: 'gold',     name: 'Gold',     min: 26,  max: 100,    commission: 20, icon: '🥇' },
            { id: 'platinum', name: 'Platinum', min: 101, max: 999999, commission: 25, icon: '💎' }
        ]
    },
    transfer: { minAmount: 20, maxAmount: 100000, feePercent: 2, feeMin: 5, dailyLimit: 500000, enabled: true },
    spin: {
        cooldownHours: 24,
        rewards: [
            { amount: 10,  weight: 30 },
            { amount: 25,  weight: 25 },
            { amount: 50,  weight: 20 },
            { amount: 100, weight: 15 },
            { amount: 250, weight: 8  },
            { amount: 500, weight: 2  }
        ],
        enabled: true
    },
    daily: { weekdayPoints: 50, weekendPoints: 100 },
    ads: {
        primary: 'admob',
        maxAdsPerDay: 25,
        admob: { enabled: true, appId: '', rewardedUnitId: '' }
    },
    game: { usdToIdr: 16000, rtpToUsd: 0.01 }
};

// ============================================================
// GET /api/v1/config — semua config sekaligus
// ============================================================
router.get('/', async (req, res) => {
    try {
        const result = {};
        await Promise.all(Object.entries(CONFIG_MAP).map(async ([type, docName]) => {
            try {
                const doc = await db.collection('system').doc(docName).get();
                const firestoreData = doc.exists ? doc.data() : {};
                // Merge dengan default (default dulu, Firestore override)
                result[type] = { ...(DEFAULTS[type] || {}), ...firestoreData };
                // Hapus field meta
                delete result[type].updatedAt;
                delete result[type].updatedBy;
            } catch (e) {
                result[type] = DEFAULTS[type] || {};
            }
        }));
        res.set('Cache-Control', 'public, max-age=60');
        return res.json({ success: true, data: result, fetchedAt: Date.now() });
    } catch (error) {
        console.error('[CONFIG] All error:', error.message);
        return res.status(500).json({ success: false, message: error.message });
    }
});

// ============================================================
// GET /api/v1/config/:type — satu config
// ============================================================
router.get('/:type', async (req, res) => {
    const { type } = req.params;
    if (!CONFIG_MAP[type]) {
        return res.status(404).json({
            success: false,
            message: `Config type "${type}" tidak dikenal. Tersedia: ${Object.keys(CONFIG_MAP).join(', ')}`
        });
    }
    try {
        const doc = await db.collection('system').doc(CONFIG_MAP[type]).get();
        const firestoreData = doc.exists ? doc.data() : {};
        const merged = { ...(DEFAULTS[type] || {}), ...firestoreData };
        delete merged.updatedAt;
        delete merged.updatedBy;
        res.set('Cache-Control', 'public, max-age=60');
        return res.json({
            success: true,
            type,
            data: merged,
            fetchedAt: Date.now()
        });
    } catch (error) {
        console.error(`[CONFIG] ${type} error:`, error.message);
        // Fallback ke default jika error
        return res.json({
            success: true,
            type,
            data: DEFAULTS[type] || {},
            fetchedAt: Date.now(),
            fallback: true
        });
    }
});

module.exports = router;
