// services/affiliate.js
// Logik komisi affiliate — retroaktif + hold 24 jam + anti-fraud

const { admin, db } = require('../config/firebase');
const { sendPushNotification } = require('./fcm');

const HOLD_MS = 24 * 60 * 60 * 1000;        // 24 jam
const DAILY_LIMIT_USD = 50.00;               // $50/hari
const MIN_ACCOUNT_AGE_MS = 60 * 60 * 1000;   // 1 jam (basic)

const TIER_RATE = {
    'FREE':    0.05,
    'STARTER': 0.10,
    'PREMIUM': 0.15,
    'VIP':     0.25,
    'ROYAL':   0.35
};

/**
 * Kira & credit komisi affiliate (PENDING status)
 * @param {string} referredUserId - User yang upgrade
 * @param {string} tierGranted - Tier yang diupgrade
 * @param {number} priceUsd - Harga tier
 * @param {object} req - Express request (untuk IP)
 */
async function creditAffiliateCommission(referredUserId, tierGranted, priceUsd, req) {
    try {
        // 1. Cek siapa yang bawa user ini
        const referredDoc = await db.collection('users').doc(referredUserId).get();
        if (!referredDoc.exists) return { success: false, reason: 'USER_NOT_FOUND' };

        const referredData = referredDoc.data();
        const affiliateId = referredData.referredBy;

        if (!affiliateId) {
            console.log(`[AFF] User ${referredUserId} tiada referredBy, skip.`);
            return { success: false, reason: 'NO_REFERRER' };
        }

        // 2. Cek self-referral
        if (affiliateId === referredUserId) {
            console.log(`[AFF] Self-referral detected: ${affiliateId}`);
            return { success: false, reason: 'SELF_REFERRAL' };
        }

        // 3. Ambil data affiliator
        const affiliateDoc = await db.collection('users').doc(affiliateId).get();
        if (!affiliateDoc.exists) return { success: false, reason: 'AFFILIATE_NOT_FOUND' };

        const affiliateData = affiliateDoc.data();

        // 4. Anti-fraud: device fingerprint + IP
        const currentIp = req.ip || req.headers['x-forwarded-for'] || 'unknown';
        const currentDevice = req.body.deviceId || req.headers['x-device-id'] || '';

        // Cek IP sama (self-referral via IP)
        if (affiliateData.lastLoginIp && affiliateData.lastLoginIp === currentIp) {
            console.log(`[AFF] Same IP: ${currentIp} — flag for review`);
            // Flag tapi masih lanjut (mungkin WiFi sama)
        }

        // Cek device sama
        if (currentDevice && affiliateData.lastDeviceId === currentDevice) {
            console.log(`[AFF] Same device — BLOCK`);
            await db.collection('audit_logs').add({
                action: 'AFFILIATE_SAME_DEVICE',
                affiliateId,
                referredUserId,
                deviceId: currentDevice,
                timestamp: admin.firestore.FieldValue.serverTimestamp()
            });
            return { success: false, reason: 'SAME_DEVICE' };
        }

        // 5. Cek rate limit harian
        const today = new Date().toISOString().split('T')[0];
        const todayKey = `${affiliateId}_${today}`;
        const dailyDoc = await db.collection('affiliate_daily').doc(todayKey).get();
        const todayTotal = dailyDoc.exists ? (dailyDoc.data().totalUsd || 0) : 0;

        if (todayTotal >= DAILY_LIMIT_USD) {
            console.log(`[AFF] Daily limit reached: $${todayTotal}`);
            return { success: false, reason: 'DAILY_LIMIT' };
        }

        // 6. Kira komisi (retroaktif — rate semasa affiliator)
        const affiliateTier = affiliateData.tier || 'FREE';
        const rate = TIER_RATE[affiliateTier] || 0.05;
        const commission = parseFloat((priceUsd * rate).toFixed(4));

        // 7. Cek cap rate limit harian (sisa quota)
        let actualCommission = commission;
        if (todayTotal + commission > DAILY_LIMIT_USD) {
            actualCommission = parseFloat((DAILY_LIMIT_USD - todayTotal).toFixed(4));
            console.log(`[AFF] Partial commission (daily cap): $${actualCommission}`);
        }

        // 8. Simpan transaksi (status PENDING)
        const txRef = db.collection('affiliate_transactions').doc();
        const now = Date.now();
        const availableAt = now + HOLD_MS;

        await txRef.set({
            id: txRef.id,
            affiliateId,
            referredUserId,
            referredUserName: referredData.name || 'User',
            referredUserTier: tierGranted,
            upgradePrice: priceUsd,
            commissionRate: rate,
            commissionAmount: actualCommission,
            currency: 'USD',
            status: 'PENDING',
            createdAt: admin.firestore.FieldValue.serverTimestamp(),
            createdAtMs: now,
            availableAtMs: availableAt,
            notifiedAt: null,
            releasedAt: null
        });

        // 9. Update balance affiliator
        await db.collection('users').doc(affiliateId).update({
            affiliateBalance: admin.firestore.FieldValue.increment(actualCommission),
            affiliateBalancePending: admin.firestore.FieldValue.increment(actualCommission),
            affiliateTotalEarned: admin.firestore.FieldValue.increment(actualCommission)
        });

        // 10. Update daily total
        await db.collection('affiliate_daily').doc(todayKey).set({
            affiliateId,
            date: today,
            totalUsd: todayTotal + actualCommission,
            updatedAt: admin.firestore.FieldValue.serverTimestamp()
        }, { merge: true });

        // 11. Kirim notifikasi PENDING
        const fcmToken = affiliateData.fcmToken;
        if (fcmToken) {
            await sendPushNotification(
                fcmToken,
                '💰 Komisi Masuk (Pending)',
                `+$${actualCommission.toFixed(2)} dari upgrade ${tierGranted} oleh ${referredData.name}. Cair dalam 24 jam.`,
                {
                    type: 'AFFILIATE_COMMISSION_PENDING',
                    amount: actualCommission.toFixed(2),
                    tier: tierGranted,
                    referredUser: referredData.name,
                    txId: txRef.id
                }
            );
        }

        console.log(`[AFF] ✅ Commission $${actualCommission} (rate ${rate*100}%) credited PENDING for ${affiliateId}`);
        return { success: true, commission: actualCommission, txId: txRef.id };

    } catch (error) {
        console.error('[AFF] creditAffiliateCommission error:', error);
        return { success: false, reason: error.message };
    }
}

/**
 * Cron job: cair komisi PENDING → AVAILABLE setelah 24 jam
 */
async function releasePendingCommission() {
    try {
        const now = Date.now();
        const snapshot = await db.collection('affiliate_transactions')
            .where('status', '==', 'PENDING')
            .where('availableAtMs', '<=', now)
            .limit(100)
            .get();

        if (snapshot.empty) {
            console.log('[AFF CRON] Tiada komisi pending untuk dicair.');
            return { success: true, released: 0 };
        }

        let releasedCount = 0;
        const batch = db.batch();

        for (const doc of snapshot.docs) {
            const tx = doc.data();
            const affiliateId = tx.affiliateId;

            batch.update(doc.ref, {
                status: 'AVAILABLE',
                releasedAt: admin.firestore.FieldValue.serverTimestamp(),
                releasedAtMs: now
            });

            batch.update(db.collection('users').doc(affiliateId), {
                affiliateBalancePending: admin.firestore.FieldValue.increment(-tx.commissionAmount),
                affiliateBalanceAvailable: admin.firestore.FieldValue.increment(tx.commissionAmount)
            });

            releasedCount++;
        }

        await batch.commit();

        // Kirim notifikasi AVAILABLE (batch)
        for (const doc of snapshot.docs) {
            const tx = doc.data();
            const affiliateDoc = await db.collection('users').doc(tx.affiliateId).get();
            if (affiliateDoc.exists && affiliateDoc.data().fcmToken) {
                await sendPushNotification(
                    affiliateDoc.data().fcmToken,
                    '✅ Komisi Siap Di-withdraw',
                    `+$${tx.commissionAmount.toFixed(2)} dari ${tx.referredUserName} kini tersedia.`,
                    {
                        type: 'AFFILIATE_COMMISSION_AVAILABLE',
                        amount: tx.commissionAmount.toFixed(2),
                        txId: tx.id
                    }
                );
            }
        }

        console.log(`[AFF CRON] ✅ Released ${releasedCount} commissions.`);
        return { success: true, released: releasedCount };

    } catch (error) {
        console.error('[AFF CRON] Error:', error);
        return { success: false, reason: error.message };
    }
}

module.exports = {
    creditAffiliateCommission,
    releasePendingCommission,
    TIER_RATE,
    HOLD_MS,
    DAILY_LIMIT_USD
};
