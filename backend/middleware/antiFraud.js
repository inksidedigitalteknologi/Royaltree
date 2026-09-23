// middleware/antiFraud.js
// Proteksi anti-fraud untuk reward (check-in & ads)

const admin = require('firebase-admin');
const db = admin.firestore();

/**
 * Cek apakah user terdeteksi fraud.
 * - Cek user diblokir
 * - Cek device ID (max 3 akun/device)
 * - Rate limiting (max 30 request/menit)
 */
async function checkFraud(req, res, next) {
    try {
        const { uid } = req.firebaseUser;

        // 1. Cek user diblokir
        const userDoc = await db.collection('users').doc(uid).get();
        if (userDoc.exists && userDoc.data().isBlocked) {
            return res.status(403).json({
                success: false,
                message: 'Akun diblokir karena aktivitas mencurigakan.'
            });
        }

        // 2. Cek device ID (max 3 akun per device)
        const deviceId = req.headers['x-device-id'];
        if (deviceId && deviceId.length > 5) {
            const devices = await db.collection('devices')
                .doc(deviceId)
                .get();

            if (devices.exists) {
                const accounts = devices.data().accounts || [];
                if (!accounts.includes(uid) && accounts.length >= 3) {
                    return res.status(403).json({
                        success: false,
                        message: 'Terlalu banyak akun di device ini.'
                    });
                }
                if (!accounts.includes(uid)) {
                    accounts.push(uid);
                    await db.collection('devices').doc(deviceId).update({
                        accounts: accounts,
                        lastSeen: admin.firestore.FieldValue.serverTimestamp()
                    });
                }
            } else {
                await db.collection('devices').doc(deviceId).set({
                    accounts: [uid],
                    firstSeen: admin.firestore.FieldValue.serverTimestamp(),
                    lastSeen: admin.firestore.FieldValue.serverTimestamp()
                });
            }
        }

        // 3. Rate limiting (max 30 request/menit)
        const rateLimitKey = `rate_limit_${uid}`;
        const now = Date.now();
        const windowMs = 60000;  // 1 menit
        const maxReq = 30;

        const rateRef = db.collection('rate_limits').doc(rateLimitKey);
        const rateDoc = await rateRef.get();

        if (rateDoc.exists) {
            const data = rateDoc.data();
            const windowStart = data.windowStart || 0;
            const count = data.count || 0;

            if (now - windowStart > windowMs) {
                // Reset window
                await rateRef.set({
                    windowStart: now,
                    count: 1
                });
            } else if (count >= maxReq) {
                return res.status(429).json({
                    success: false,
                    message: 'Terlalu banyak request. Coba lagi nanti.'
                });
            } else {
                await rateRef.update({
                    count: count + 1
                });
            }
        } else {
            await rateRef.set({
                windowStart: now,
                count: 1
            });
        }

        next();
    } catch (error) {
        console.error('Anti-fraud error:', error);
        return res.status(500).json({
            success: false,
            message: 'Internal server error (fraud check).'
        });
    }
}

module.exports = { checkFraud };
