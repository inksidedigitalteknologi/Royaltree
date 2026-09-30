// routes/purchase.js
// Endpoint verifikasi pembelian Google Play + grant tier

const express = require('express');
const router = express.Router();
const admin = require('firebase-admin');
const { google } = require('googleapis');
const { verifyFirebaseToken } = require('../middleware/firebaseAuth');

const db = admin.firestore();

// ============================================================
// KATALOG TIER (lifetime)
// ============================================================
const TIER_CATALOG = {
    'tier_vip_lifetime': {
        tier: 'VIP',
        maxMinerSlots: 7,
        priceUsd: 2.49,
        displayName: 'VIP',
        benefits: ['Slot miner 7', 'Badge VIP']
    },
    'tier_vip_pro_lifetime': {
        tier: 'VIP_PRO',
        maxMinerSlots: 8,
        priceUsd: 3.49,
        displayName: 'VIP Pro',
        benefits: ['Slot miner 8', 'Badge VIP Pro', 'Prioritas support']
    }
};

// ============================================================
// Helper: verifikasi ke Google Play Developer API
// ============================================================
async function verifyWithGooglePlay(purchaseToken, productId, packageName) {
    // Setup auth pakai service account
    const auth = new google.auth.GoogleAuth({
        keyFile: process.env.GOOGLE_APPLICATION_CREDENTIALS,
        scopes: ['https://www.googleapis.com/auth/androidpublisher']
    });

    const androidPublisher = google.androidpublisher({
        version: 'v3',
        auth: auth
    });

    try {
        const response = await androidPublisher.purchases.products.get({
            packageName: packageName,
            productId: productId,
            token: purchaseToken
        });

        const data = response.data;

        // purchaseState: 0 = Purchased, 1 = Canceled, 2 = Pending
        if (data.purchaseState !== 0) {
            return { valid: false, reason: 'Purchase state not PURCHASED' };
        }

        // Belum dikonsumsi (untuk INAPP)
        if (data.consumptionState === 1) {
            return { valid: false, reason: 'Already consumed' };
        }

        return { valid: true, data: data };
    } catch (error) {
        console.error('Google Play verify error:', error.message);
        return { valid: false, reason: error.message };
    }
}

// ============================================================
// Helper: acknowledge pembelian ke Google Play
// ============================================================
async function acknowledgeWithGooglePlay(purchaseToken, productId, packageName) {
    const auth = new google.auth.GoogleAuth({
        keyFile: process.env.GOOGLE_APPLICATION_CREDENTIALS,
        scopes: ['https://www.googleapis.com/auth/androidpublisher']
    });

    const androidPublisher = google.androidpublisher({
        version: 'v3',
        auth: auth
    });

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

// ============================================================
// POST /api/v1/purchase/verify
// Verifikasi pembelian + grant tier
// Body: { purchaseToken, productId, packageName }
// ============================================================
router.post('/verify', verifyFirebaseToken, async (req, res) => {
    try {
        const { uid, email } = req.firebaseUser;
        const { purchaseToken, productId, packageName } = req.body;

        // Validasi
        if (!purchaseToken || !productId || !packageName) {
            return res.status(400).json({
                success: false,
                message: 'purchaseToken, productId, packageName wajib diisi.'
            });
        }

        // Cek tier config
        const tierConfig = TIER_CATALOG[productId];
        if (!tierConfig) {
            return res.status(400).json({
                success: false,
                message: 'Produk tidak dikenal.'
            });
        }

        // Cek purchaseToken belum pernah dipakai (anti-replay)
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

        // Verifikasi ke Google Play
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

        // Cek user sekarang (untuk validasi upgrade)
        const userRef = db.collection('users').doc(uid);
        const userDoc = await userRef.get();
        const userData = userDoc.exists ? userDoc.data() : {};
        const currentTier = userData.tier || 'FREE';

        // Validasi: tidak boleh downgrade
        if (currentTier === 'VIP_PRO') {
            return res.status(400).json({
                success: false,
                message: 'Anda sudah VIP Pro. Tidak bisa downgrade.'
            });
        }

        // Jika user VIP, hanya boleh beli VIP_PRO (bukan VIP lagi)
        if (currentTier === 'VIP' && tierConfig.tier === 'VIP') {
            return res.status(400).json({
                success: false,
                message: 'Anda sudah VIP. Upgrade ke VIP Pro untuk tambah slot.'
            });
        }

        // Simpan purchase record
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

        // Update user tier
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

        // Acknowledge ke Google (async, jangan block response)
        acknowledgeWithGooglePlay(purchaseToken, productId, packageName)
            .then(success => {
                if (success) {
                    purchaseRef.update({ acknowledged: true });
                    console.log(`✅ Purchase ${purchaseRef.id} acknowledged`);
                }
            });

        // Audit log
        await db.collection('audit_logs').add({
            userId: uid,
            action: 'PURCHASE_TIER',
            tier: tierConfig.tier,
            productId: productId,
            priceUsd: tierConfig.priceUsd,
            timestamp: admin.firestore.FieldValue.serverTimestamp(),
        });

        return res.json({
            success: true,
            message: `Berhasil upgrade ke ${tierConfig.displayName}!`,
            tier: tierConfig.tier,
            maxMinerSlots: tierConfig.maxMinerSlots,
            benefits: tierConfig.benefits,
        });

    } catch (error) {
        console.error('Verify purchase error:', error);
        return res.status(500).json({
            success: false,
            message: 'Gagal verifikasi: ' + error.message
        });
    }
});

// ============================================================
// GET /api/v1/purchase/tiers
// Ambil daftar tier (untuk tampil di UI)
// ============================================================
router.get('/tiers', verifyFirebaseToken, async (req, res) => {
    try {
        const tiers = Object.entries(TIER_CATALOG).map(([productId, config]) => ({
            productId: productId,
            tier: config.tier,
            displayName: config.displayName,
            priceUsd: config.priceUsd,
            maxMinerSlots: config.maxMinerSlots,
            benefits: config.benefits,
        }));

        return res.json({ success: true, data: tiers });
    } catch (error) {
        return res.status(500).json({ success: false, message: error.message });
    }
});

// ============================================================
// GET /api/v1/purchase/tiers-public
// Versi publik (tanpa auth) untuk MVP — user belum login Firebase.
// Nanti bila Firebase Auth stabil, buang endpoint ini & guna /tiers.
// ============================================================
router.get('/tiers-public', async (req, res) => {
    try {
        const tiers = Object.entries(TIER_CATALOG).map(([productId, config]) => ({
            productId: productId,
            tier: config.tier,
            displayName: config.displayName,
            priceUsd: config.priceUsd,
            maxMinerSlots: config.maxMinerSlots,
            benefits: config.benefits,
        }));
        return res.json({ success: true, data: tiers });
    } catch (error) {
        return res.status(500).json({ success: false, message: error.message });
    }
});

module.exports = router;
