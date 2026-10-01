// routes/purchase.js
// Endpoint verifikasi pembelian Google Play + grant tier + komisi affiliate

const express = require('express');
const router = express.Router();
const { admin, db } = require('../config/firebase');
const { google } = require('googleapis');
const { verifyFirebaseToken } = require('../middleware/firebaseAuth');
const { creditAffiliateCommission } = require('../services/affiliate');

// ============================================================
// KATALOG TIER (lifetime) — 5 TIER
// ============================================================
const TIER_CATALOG = {
    'tier_starter_lifetime': {
        tier: 'STARTER',
        displayName: 'Pemula',
        priceUsd: 1.99,
        maxMinerSlots: 3,
        affiliateRate: 0.10,
        tierOrder: 2,
        benefits: ['Slot miner 3', 'Badge Starter']
    },
    'tier_premium_lifetime': {
        tier: 'PREMIUM',
        displayName: 'Premium',
        priceUsd: 2.99,
        maxMinerSlots: 5,
        affiliateRate: 0.15,
        tierOrder: 3,
        benefits: ['Slot miner 5', 'Badge Premium']
    },
    'tier_vip_lifetime': {
        tier: 'VIP',
        displayName: 'VIP',
        priceUsd: 4.99,
        maxMinerSlots: 8,
        affiliateRate: 0.25,
        tierOrder: 4,
        benefits: ['Slot miner 8', 'Badge VIP', 'Prioritas support']
    },
    'tier_royal_lifetime': {
        tier: 'ROYAL',
        displayName: 'Royal',
        priceUsd: 9.99,
        maxMinerSlots: 10,
        affiliateRate: 0.35,
        tierOrder: 5,
        benefits: ['Slot miner 10', 'Badge Royal', 'Fitur eksklusif']
    }
};

const TIER_ORDER = {
    'FREE': 1,
    'STARTER': 2,
    'PREMIUM': 3,
    'VIP': 4,
    'ROYAL': 5
};

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

router.post('/verify', verifyFirebaseToken, async (req, res) => {
    try {
        const { uid, email } = req.firebaseUser;
        const { purchaseToken, productId, packageName } = req.body;

        if (!purchaseToken || !productId || !packageName) {
            return res.status(400).json({
                success: false,
                message: 'purchaseToken, productId, packageName wajib diisi.'
            });
        }

        const tierConfig = TIER_CATALOG[productId];
        if (!tierConfig) {
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

        const verification = await verifyWithGooglePlay(
            purchaseToken, productId, packageName
        );

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

        let affiliateResult = { success: false, reason: 'SKIPPED' };
        try {
            affiliateResult = await creditAffiliateCommission(
                uid,
                tierConfig.tier,
                tierConfig.priceUsd,
                req
            );
            console.log(`[PURCHASE] Affiliate commission:`, affiliateResult);
        } catch (affError) {
            console.error('[PURCHASE] Affiliate commission error:', affError.message);
        }

        acknowledgeWithGooglePlay(purchaseToken, productId, packageName)
            .then(success => {
                if (success) {
                    purchaseRef.update({ acknowledged: true });
                    console.log(`✅ Purchase ${purchaseRef.id} acknowledged`);
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
        return res.status(500).json({ success: false, message: error.message });
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
        return res.status(500).json({ success: false, message: error.message });
    }
});

module.exports = router;
