// routes/purchase.js
// Endpoint verifikasi pembelian Google Play + grant tier + komisi affiliate

const express = require('express');
const router = express.Router();
const { admin, db } = require('../config/firebase');
const { google } = require('googleapis');
const { verifyFirebaseToken } = require('../middleware/firebaseAuth');
const { creditAffiliateCommission } = require('../services/affiliate');

// Validation
const { validate } = require('../middleware/validate');
const { purchaseSchema } = require('../schemas');
const AppLogger = require('../utils/AppLogger');

// ============================================================
// KATALOG TIER (lifetime) — 5 TIER
// ============================================================
const TIER_CATALOG = {
    'tier_starter_lifetime': {
        tier: 'STARTER',
        displayName: 'Pemula',
        priceUsd: 2.99,
        maxMinerSlots: 3,
        affiliateRate: 0.10,
        tierOrder: 2,
        benefits: ['Slot miner 3', 'Badge Starter']
    },
    'tier_premium_lifetime': {
        tier: 'PREMIUM',
        displayName: 'Premium',
        priceUsd: 5.99,
        maxMinerSlots: 5,
        affiliateRate: 0.15,
        tierOrder: 3,
        benefits: ['Slot miner 5', 'Badge Premium']
    },
    'tier_vip_lifetime': {
        tier: 'VIP',
        displayName: 'VIP',
        priceUsd: 14.95,
        maxMinerSlots: 10,
        affiliateRate: 0.20,
        tierOrder: 4,
        benefits: ['Slot miner 10', 'Badge VIP', 'Komisi affiliate 20%', 'Prioritas support']
    },
    'tier_royal_lifetime': {
        tier: 'ROYAL',
        displayName: 'Royal',
        priceUsd: 19.98,
        maxMinerSlots: 20,
        affiliateRate: 0.25,
        tierOrder: 5,
        benefits: ['Slot miner 20', 'Badge Royal', 'Komisi affiliate 25%', 'Fitur eksklusif']
    }
};


// Syarat referral valid — hanya jika upgrade ke PREMIUM+
const SYARAT_REFERRAL_TIER = 'PREMIUM';
const SYARAT_REFERRAL_ORDER = 3;  // TIER_ORDER['PREMIUM']

// Placeholder produk upgrade (Opsi D — nanti diisi)
const UPGRADE_PRODUCTS = {
    // 'upgrade_starter_to_premium': {
    //     from: 'STARTER', to: 'PREMIUM',
    //     priceUsd: 3.00, tierOrder: 3, maxMinerSlots: 5, affiliateRate: 0.15,
    //     benefits: ['Slot miner 5', 'Badge Premium']
    // },
};

const TIER_ORDER = {
    'FREE': 1,
    'STARTER': 2,
    'PREMIUM': 3,
    'VIP': 4,
    'ROYAL': 5
};

// ============================================================
// RTP PACK CATALOG
// ============================================================
const RTP_PACK_CATALOG = {
    'rtp_pack_1000': {
        rtp: 1000,
        priceUsd: 0.99,
        name: 'Starter Pack',
        emoji: '💰',
        description: '1.000 RTP untuk mulai mining'
    },
    'rtp_pack_5000': {
        rtp: 5000,
        priceUsd: 3.99,
        name: 'Popular Pack',
        emoji: '💎',
        description: '5.000 RTP — paling populer!'
    },
    'rtp_pack_15000': {
        rtp: 15000,
        priceUsd: 9.99,
        name: 'Pro Pack',
        emoji: '👑',
        description: '15.000 RTP untuk pro player'
    },
    'rtp_pack_50000': {
        rtp: 50000,
        priceUsd: 24.99,
        name: 'Whale Pack',
        emoji: '🐋',
        description: '50.000 RTP — value terbaik!'
    }
};

// ============================================================
// MINER PACK CATALOG
// ============================================================
const MINER_PACK_CATALOG = {
    'miner_pack_starter': {
        miners: [
            { catalogId: 'miner_common', qty: 3 }
        ],
        priceUsd: 1.99,
        name: 'Starter Miner',
        emoji: '⛏️',
        description: '3× Pico Miner'
    },
    'miner_pack_pro': {
        miners: [
            { catalogId: 'miner_rare', qty: 2 },
            { catalogId: 'miner_common', qty: 3 }
        ],
        priceUsd: 4.99,
        name: 'Pro Miner',
        emoji: '🔧',
        description: '2× Micro Rig + 3× Pico Miner'
    },
    'miner_pack_elite': {
        miners: [
            { catalogId: 'miner_epic', qty: 1 },
            { catalogId: 'miner_rare', qty: 2 },
            { catalogId: 'miner_common', qty: 3 }
        ],
        priceUsd: 9.99,
        name: 'Elite Miner',
        emoji: '⚡',
        description: '1× Quantum Rig + 2× Micro Rig + 3× Pico Miner'
    },
    'miner_pack_legend': {
        miners: [
            { catalogId: 'miner_legendary', qty: 1 },
            { catalogId: 'miner_epic', qty: 2 },
            { catalogId: 'miner_rare', qty: 3 }
        ],
        priceUsd: 19.99,
        name: 'Legend Miner',
        emoji: '💎',
        description: '1× Titan Miner + 2× Quantum Rig + 3× Micro Rig'
    }
};

// ============================================================
// BUNDLE CAMPURAN (RTP + MINER)
// ============================================================
const BUNDLE_CATALOG = {
    'bundle_starter': {
        rtp: 5000,
        miners: [
            { catalogId: 'miner_common', qty: 3 }
        ],
        priceUsd: 4.99,
        name: 'Starter Bundle',
        emoji: '🎁',
        description: '5.000 RTP + 3× Pico Miner'
    },
    'bundle_pro': {
        rtp: 15000,
        miners: [
            { catalogId: 'miner_rare', qty: 2 },
            { catalogId: 'miner_common', qty: 3 }
        ],
        priceUsd: 14.99,
        name: 'Pro Bundle',
        emoji: '🎁',
        description: '15.000 RTP + 2× Micro Rig + 3× Pico Miner'
    },
    'bundle_elite': {
        rtp: 50000,
        miners: [
            { catalogId: 'miner_epic', qty: 1 },
            { catalogId: 'miner_rare', qty: 2 },
            { catalogId: 'miner_common', qty: 3 }
        ],
        priceUsd: 29.99,
        name: 'Elite Bundle',
        emoji: '🎁',
        description: '50.000 RTP + 1× Quantum Rig + 2× Micro Rig + 3× Pico Miner'
    }
};

// ============================================================
// MINER CATALOG (referensi dari game.js)
// ============================================================
const MINER_CATALOG_REF = {
    'miner_common':    { name: 'Pico Miner',  tier: 'COMMON',    iconEmoji: '⛏️', powerGhs: 0.000001, pointsPerDay: 0.144, bonusPermanentPct: 0 },
    'miner_rare':      { name: 'Micro Rig',   tier: 'RARE',      iconEmoji: '🔧', powerGhs: 0.000005, pointsPerDay: 0.72,  bonusPermanentPct: 0.10 },
    'miner_epic':      { name: 'Quantum Rig', tier: 'EPIC',      iconEmoji: '⚡', powerGhs: 0.00002,  pointsPerDay: 2.88,  bonusPermanentPct: 0.23 },
    'miner_legendary': { name: 'Titan Miner', tier: 'LEGENDARY', iconEmoji: '💎', powerGhs: 0.0001,   pointsPerDay: 14.4,  bonusPermanentPct: 1.20 },
    'miner_mythic':    { name: 'Infinity Core', tier: 'MYTHIC',  iconEmoji: '🌌', powerGhs: 0.0005,   pointsPerDay: 72,    bonusPermanentPct: 3.00 }
};

// ============================================================
// HELPER: Tambah miner ke inventory user
// ============================================================
async function grantMinersToUser(uid, minersList) {
    const userRef = db.collection('users').doc(uid);
    const userDoc = await userRef.get();
    if (!userDoc.exists) throw new Error('User tidak ditemukan');

    const updates = {};
    const auditEntries = [];

    for (const item of minersList) {
        const catalog = MINER_CATALOG_REF[item.catalogId];
        if (!catalog) continue;

        const pointsPerMinute = parseFloat((catalog.pointsPerDay / (24 * 60)).toFixed(8));

        for (let i = 0; i < item.qty; i++) {
            const instanceId = 'miner_' + uid.slice(0, 6) + '_' + Date.now() + '_' + Math.random().toString(36).slice(2, 8);
            updates[`minersOwned.${instanceId}`] = {
                catalogId: item.catalogId,
                instanceId: instanceId,
                name: catalog.name,
                tier: catalog.tier,
                iconEmoji: catalog.iconEmoji,
                tokenCost: 0,
                powerGhs: catalog.powerGhs,
                pointsPerMinute: pointsPerMinute,
                bonusPermanentPct: catalog.bonusPermanentPct,
                isOwned: true,
                isPlacedInRoom: false,
                placedSlotIndex: -1,
                purchasedAt: Date.now(),
                source: 'PACK'
            };
            auditEntries.push({ instanceId, catalogId: item.catalogId, tier: catalog.tier });
        }
    }

    if (Object.keys(updates).length > 0) {
        await userRef.update(updates);
    }

    return auditEntries;
}

async function verifyWithGooglePlay(purchaseToken, productId, packageName) {
    const auth = new google.auth.GoogleAuth({
        keyFile: process.env.GOOGLE_APPLICATION_CREDENTIALS,
        scopes: ['https://www.googleapis.com/auth/androidpublisher']
    });
    const androidPublisher = google.androidpublisher({ version: 'v3', auth: auth });

    try {
        const response = await androidPublisher.purchases.products.get({
            packageName: packageName,
            productId: productId,
            token: purchaseToken
        });
        const data = response.data;
        if (data.purchaseState !== 0) {
            return { valid: false, reason: 'Purchase state not PURCHASED' };
        }
        if (data.consumptionState === 1) {
            return { valid: false, reason: 'Already consumed' };
        }
        return { valid: true, data: data };
    } catch (error) {
        console.error('Google Play verify error:', error.message);
        return { valid: false, reason: error.message };
    }
}

async function acknowledgeWithGooglePlay(purchaseToken, productId, packageName) {
    const auth = new google.auth.GoogleAuth({
        keyFile: process.env.GOOGLE_APPLICATION_CREDENTIALS,
        scopes: ['https://www.googleapis.com/auth/androidpublisher']
    });
    const androidPublisher = google.androidpublisher({ version: 'v3', auth: auth });

    try {
        await androidPublisher.purchases.products.acknowledge({
            packageName: packageName,
            productId: productId,
            token: purchaseToken,
            requestBody: {}
        });
        return true;
    } catch (error) {
        console.error('Acknowledge error:', error.message);
        return false;
    }
}

router.post('/verify', verifyFirebaseToken, validate(purchaseSchema), async (req, res) => {
    try {
        const { uid, email } = req.firebaseUser;
        const { purchaseToken, productId, packageName } = req.body;

        if (!purchaseToken || !productId || !packageName) {
            return res.status(400).json({
                success: false,
                message: 'purchaseToken, productId, packageName wajib diisi.'
            });
        }

        // ============================================================
        // DETEKSI TIPE PRODUK
        // ============================================================
        const tierConfig = TIER_CATALOG[productId];
        const rtpPack = RTP_PACK_CATALOG[productId];
        const minerPack = MINER_PACK_CATALOG[productId];
        const bundle = BUNDLE_CATALOG[productId];

        const isTier = !!tierConfig;
        const isRtpPack = !!rtpPack;
        const isMinerPack = !!minerPack;
        const isBundle = !!bundle;

        if (!isTier && !isRtpPack && !isMinerPack && !isBundle) {
            return res.status(400).json({
                success: false,
                message: 'Produk tidak dikenal.'
            });
        }

        const existingPurchase = await db.collection('purchases')
            .where('purchaseToken', '==', purchaseToken)
            .limit(1)
            .get();

        if (!existingPurchase.empty) {
            return res.status(400).json({
                success: false,
                message: 'Pembelian sudah diverifikasi sebelumnya.'
            });
        }

        // ============================================================
        // MOCK MODE — untuk testing tanpa Google Play
        // ============================================================
        let verification;
        if (purchaseToken.startsWith('mock_')) {
            if (process.env.ALLOW_MOCK_PURCHASE !== 'true') {
                console.warn('[PURCHASE] Mock token ditolak (ALLOW_MOCK_PURCHASE != true)');
                return res.status(400).json({
                    success: false,
                    message: 'Mock purchase tidak diizinkan di production'
                });
            }
            AppLogger.d('[PURCHASE] 🧪 MOCK verification for', productId);
            verification = {
                valid: true,
                data: {
                    mock: true,
                    purchaseTime: Date.now().toString()
                }
            };
        } else {
            verification = await verifyWithGooglePlay(
                purchaseToken, productId, packageName
            );
        }

        if (!verification.valid) {
            return res.status(400).json({
                success: false,
                message: 'Verifikasi gagal: ' + verification.reason
            });
        }

        const googleData = verification.data;

        const userRef = db.collection('users').doc(uid);
        const userDoc = await userRef.get();
        const userData = userDoc.exists ? userDoc.data() : {};

        // ============================================================
        // HANDLE RTP PACK
        // ============================================================
        if (isRtpPack) {
            const rtpToAdd = rtpPack.rtp;

            // Simpan purchase
            const purchaseRef = db.collection('purchases').doc();
            await purchaseRef.set({
                id: purchaseRef.id,
                userId: uid,
                productId: productId,
                productType: 'RTP_PACK',
                rtpGranted: rtpToAdd,
                purchaseToken: purchaseToken,
                orderId: googleData.orderId || '',
                packageName: packageName,
                purchaseState: 'PURCHASED',
                priceAmountMicros: googleData.priceAmountMicros || 0,
                priceCurrencyCode: googleData.priceCurrencyCode || 'USD',
                countryCode: googleData.countryCode || '',
                verificationStatus: 'VERIFIED',
                verifiedAt: admin.firestore.FieldValue.serverTimestamp(),
                verificationMethod: 'GOOGLE_PLAY_API',
                purchasedAt: admin.firestore.FieldValue.serverTimestamp(),
                acknowledged: false,
                refundedAt: null,
            });

            // Tambah RTP
            await userRef.update({
                points: admin.firestore.FieldValue.increment(rtpToAdd),
                totalPurchases: admin.firestore.FieldValue.increment(1),
                totalSpentUsd: admin.firestore.FieldValue.increment(rtpPack.priceUsd),
            });

            // Audit log
            await db.collection('audit_logs').add({
                userId: uid,
                action: 'PURCHASE_RTP_PACK',
                productId: productId,
                rtpAdded: rtpToAdd,
                priceUsd: rtpPack.priceUsd,
                timestamp: admin.firestore.FieldValue.serverTimestamp(),
            });

            // Acknowledge
            acknowledgeWithGooglePlay(purchaseToken, productId, packageName)
                .then(success => {
                    if (success) purchaseRef.update({ acknowledged: true });
                });

            return res.json({
                success: true,
                message: `Berhasil beli ${rtpPack.name}! +${rtpToAdd} RTP`,
                type: 'RTP_PACK',
                rtpAdded: rtpToAdd,
                productId: productId
            });
        }

        // ============================================================
        // HANDLE MINER PACK
        // ============================================================
        if (isMinerPack) {
            // Simpan purchase
            const purchaseRef = db.collection('purchases').doc();
            await purchaseRef.set({
                id: purchaseRef.id,
                userId: uid,
                productId: productId,
                productType: 'MINER_PACK',
                minersGranted: minerPack.miners,
                purchaseToken: purchaseToken,
                orderId: googleData.orderId || '',
                packageName: packageName,
                purchaseState: 'PURCHASED',
                priceAmountMicros: googleData.priceAmountMicros || 0,
                priceCurrencyCode: googleData.priceCurrencyCode || 'USD',
                countryCode: googleData.countryCode || '',
                verificationStatus: 'VERIFIED',
                verifiedAt: admin.firestore.FieldValue.serverTimestamp(),
                verificationMethod: 'GOOGLE_PLAY_API',
                purchasedAt: admin.firestore.FieldValue.serverTimestamp(),
                acknowledged: false,
                refundedAt: null,
            });

            // Grant miners
            const grantedMiners = await grantMinersToUser(uid, minerPack.miners);

            // Update user
            await userRef.update({
                totalPurchases: admin.firestore.FieldValue.increment(1),
                totalSpentUsd: admin.firestore.FieldValue.increment(minerPack.priceUsd),
            });

            // Audit log
            await db.collection('audit_logs').add({
                userId: uid,
                action: 'PURCHASE_MINER_PACK',
                productId: productId,
                minersGranted: grantedMiners,
                priceUsd: minerPack.priceUsd,
                timestamp: admin.firestore.FieldValue.serverTimestamp(),
            });

            // Acknowledge
            acknowledgeWithGooglePlay(purchaseToken, productId, packageName)
                .then(success => {
                    if (success) purchaseRef.update({ acknowledged: true });
                });

            return res.json({
                success: true,
                message: `Berhasil beli ${minerPack.name}!`,
                type: 'MINER_PACK',
                minersGranted: grantedMiners.length,
                productId: productId
            });
        }

        // ============================================================
        // HANDLE BUNDLE (RTP + MINER)
        // ============================================================
        if (isBundle) {
            // Simpan purchase
            const purchaseRef = db.collection('purchases').doc();
            await purchaseRef.set({
                id: purchaseRef.id,
                userId: uid,
                productId: productId,
                productType: 'BUNDLE',
                rtpGranted: bundle.rtp,
                minersGranted: bundle.miners,
                purchaseToken: purchaseToken,
                orderId: googleData.orderId || '',
                packageName: packageName,
                purchaseState: 'PURCHASED',
                priceAmountMicros: googleData.priceAmountMicros || 0,
                priceCurrencyCode: googleData.priceCurrencyCode || 'USD',
                countryCode: googleData.countryCode || '',
                verificationStatus: 'VERIFIED',
                verifiedAt: admin.firestore.FieldValue.serverTimestamp(),
                verificationMethod: 'GOOGLE_PLAY_API',
                purchasedAt: admin.firestore.FieldValue.serverTimestamp(),
                acknowledged: false,
                refundedAt: null,
            });

            // Tambah RTP
            await userRef.update({
                points: admin.firestore.FieldValue.increment(bundle.rtp),
            });

            // Grant miners
            const grantedMiners = await grantMinersToUser(uid, bundle.miners);

            // Update user
            await userRef.update({
                totalPurchases: admin.firestore.FieldValue.increment(1),
                totalSpentUsd: admin.firestore.FieldValue.increment(bundle.priceUsd),
            });

            // Audit log
            await db.collection('audit_logs').add({
                userId: uid,
                action: 'PURCHASE_BUNDLE',
                productId: productId,
                rtpAdded: bundle.rtp,
                minersGranted: grantedMiners,
                priceUsd: bundle.priceUsd,
                timestamp: admin.firestore.FieldValue.serverTimestamp(),
            });

            // Acknowledge
            acknowledgeWithGooglePlay(purchaseToken, productId, packageName)
                .then(success => {
                    if (success) purchaseRef.update({ acknowledged: true });
                });

            return res.json({
                success: true,
                message: `Berhasil beli ${bundle.name}! +${bundle.rtp} RTP + ${grantedMiners.length} miner`,
                type: 'BUNDLE',
                rtpAdded: bundle.rtp,
                minersGranted: grantedMiners.length,
                productId: productId
            });
        }

        // ============================================================
        // HANDLE TIER (existing logic)
        // ============================================================
        const currentTier = userData.tier || 'FREE';
        const currentOrder = TIER_ORDER[currentTier] || 1;
        const newOrder = tierConfig.tierOrder;

        if (newOrder <= currentOrder) {
            return res.status(400).json({
                success: false,
                message: `Anda sudah ${currentTier}. Silakan pilih tier yang lebih tinggi.`
            });
        }

        const purchaseRef = db.collection('purchases').doc();
        await purchaseRef.set({
            id: purchaseRef.id,
            userId: uid,
            userEmail: email || '',
            productId: productId,
            productType: 'INAPP',
            tierGranted: tierConfig.tier,
            purchaseToken: purchaseToken,
            orderId: googleData.orderId || '',
            packageName: packageName,
            purchaseState: 'PURCHASED',
            priceAmountMicros: googleData.priceAmountMicros || 0,
            priceCurrencyCode: googleData.priceCurrencyCode || 'USD',
            countryCode: googleData.countryCode || '',
            verificationStatus: 'VERIFIED',
            verifiedAt: admin.firestore.FieldValue.serverTimestamp(),
            verificationMethod: 'GOOGLE_PLAY_API',
            purchasedAt: admin.firestore.FieldValue.serverTimestamp(),
            acknowledged: false,
            refundedAt: null,
        });

        await userRef.update({
            tier: tierConfig.tier,
            tierPurchasedAt: admin.firestore.FieldValue.serverTimestamp(),
            tierSource: 'GOOGLE_PLAY',
            maxMinerSlots: tierConfig.maxMinerSlots,
            lastPurchaseToken: purchaseToken,
            lastPurchaseProductId: productId,
            totalPurchases: admin.firestore.FieldValue.increment(1),
            totalSpentUsd: admin.firestore.FieldValue.increment(tierConfig.priceUsd),
        });

        // ============================================================
        // KOMISI AFFILIATE — hanya jika tierGranted >= SYARAT_REFERRAL_ORDER
        // ============================================================
        let affiliateResult = { success: false, reason: 'SKIPPED' };
        if (newOrder >= SYARAT_REFERRAL_ORDER) {
            try {
                affiliateResult = await creditAffiliateCommission(
                    uid,
                    tierConfig.tier,
                    tierConfig.priceUsd,
                    req
                );
                AppLogger.d(`[PURCHASE] Affiliate commission:`, affiliateResult);
            } catch (affError) {
                console.error('[PURCHASE] Affiliate commission error:', affError.message);
            }
        } else {
            affiliateResult = {
                success: false,
                reason: 'REFERRAL_NOT_QUALIFIED',
                skipped: true,
                message: `Komisi referral hanya berlaku jika upgrade ke ${SYARAT_REFERRAL_TIER} atau lebih tinggi`
            };
            AppLogger.d(`[PURCHASE] Skip affiliate — ${tierConfig.tier} < ${SYARAT_REFERRAL_TIER}`);
        }

        acknowledgeWithGooglePlay(purchaseToken, productId, packageName)
            .then(success => {
                if (success) {
                    purchaseRef.update({ acknowledged: true });
                    AppLogger.d(`✅ Purchase ${purchaseRef.id} acknowledged`);
                }
            });

        await db.collection('audit_logs').add({
            userId: uid,
            action: 'PURCHASE_TIER',
            tier: tierConfig.tier,
            productId: productId,
            priceUsd: tierConfig.priceUsd,
            affiliateCommission: affiliateResult.commission || 0,
            affiliateId: affiliateResult.affiliateId || null,
            timestamp: admin.firestore.FieldValue.serverTimestamp(),
        });

        return res.json({
            success: true,
            message: `Berhasil upgrade ke ${tierConfig.displayName}!`,
            tier: tierConfig.tier,
            maxMinerSlots: tierConfig.maxMinerSlots,
            benefits: tierConfig.benefits,
            affiliate: affiliateResult
        });

    } catch (error) {
        console.error('Verify purchase error:', error);
        return res.status(500).json({
            success: false,
            message: 'Gagal verifikasi: ' + error.message
        });
    }
});

router.get('/tiers', verifyFirebaseToken, async (req, res) => {
    try {
        const tiers = Object.entries(TIER_CATALOG).map(([productId, config]) => ({
            productId: productId,
            tier: config.tier,
            displayName: config.displayName,
            priceUsd: config.priceUsd,
            maxMinerSlots: config.maxMinerSlots,
            affiliateRate: config.affiliateRate,
            tierOrder: config.tierOrder,
            benefits: config.benefits,
        }));
        return res.json({ success: true, data: tiers });
    } catch (error) {
        return res.status(500).json({ success: false, message: "Terjadi kesalahan server. Coba lagi nanti." });
    }
});

router.get('/tiers-public', async (req, res) => {
    try {
        const tiers = Object.entries(TIER_CATALOG).map(([productId, config]) => ({
            productId: productId,
            tier: config.tier,
            displayName: config.displayName,
            priceUsd: config.priceUsd,
            maxMinerSlots: config.maxMinerSlots,
            affiliateRate: config.affiliateRate,
            tierOrder: config.tierOrder,
            benefits: config.benefits,
        }));
        return res.json({ success: true, data: tiers });
    } catch (error) {
        return res.status(500).json({ success: false, message: "Terjadi kesalahan server. Coba lagi nanti." });
    }
});

// ============================================================
// GET /api/v1/purchase/rtp-packs
// Return daftar RTP pack (public)
// ============================================================
router.get('/rtp-packs', async (req, res) => {
    try {
        const packs = Object.entries(RTP_PACK_CATALOG).map(([productId, config]) => ({
            productId,
            rtp: config.rtp,
            priceUsd: config.priceUsd,
            name: config.name,
            emoji: config.emoji,
            description: config.description
        }));
        return res.json({ success: true, data: packs });
    } catch (error) {
        return res.status(500).json({ success: false, message: 'Terjadi kesalahan server. Coba lagi nanti.' });
    }
});

// ============================================================
// GET /api/v1/purchase/miner-packs
// Return daftar Miner pack (public)
// ============================================================
router.get('/miner-packs', async (req, res) => {
    try {
        const packs = Object.entries(MINER_PACK_CATALOG).map(([productId, config]) => ({
            productId,
            miners: config.miners.map(m => ({
                ...m,
                name: MINER_CATALOG_REF[m.catalogId]?.name || m.catalogId,
                tier: MINER_CATALOG_REF[m.catalogId]?.tier || 'COMMON',
                iconEmoji: MINER_CATALOG_REF[m.catalogId]?.iconEmoji || '⛏️'
            })),
            priceUsd: config.priceUsd,
            name: config.name,
            emoji: config.emoji,
            description: config.description
        }));
        return res.json({ success: true, data: packs });
    } catch (error) {
        return res.status(500).json({ success: false, message: 'Terjadi kesalahan server. Coba lagi nanti.' });
    }
});

// ============================================================
// GET /api/v1/purchase/bundles
// Return daftar Bundle campuran (public)
// ============================================================
router.get('/bundles', async (req, res) => {
    try {
        const bundles = Object.entries(BUNDLE_CATALOG).map(([productId, config]) => ({
            productId,
            rtp: config.rtp,
            miners: config.miners.map(m => ({
                ...m,
                name: MINER_CATALOG_REF[m.catalogId]?.name || m.catalogId,
                tier: MINER_CATALOG_REF[m.catalogId]?.tier || 'COMMON',
                iconEmoji: MINER_CATALOG_REF[m.catalogId]?.iconEmoji || '⛏️'
            })),
            priceUsd: config.priceUsd,
            name: config.name,
            emoji: config.emoji,
            description: config.description
        }));
        return res.json({ success: true, data: bundles });
    } catch (error) {
        return res.status(500).json({ success: false, message: 'Terjadi kesalahan server. Coba lagi nanti.' });
    }
});

module.exports = router;
