// middleware/antiFraud.js
// Proteksi anti-fraud untuk reward (check-in & ads) — production-grade

const admin = require('firebase-admin');
const db = admin.firestore();

// ==== In-memory cache untuk rate limit (lebih cepat dari Firestore) ====
const rateCache = new Map();      // key: uid -> { count, windowStart, daily }
const ipCache = new Map();        // key: ip -> { count, windowStart }
const adCooldown = new Map();     // key: uid -> last ad timestamp

// ==== Konfigurasi ====
const CONFIG = {
    RATE_WINDOW_MS: 60000,        // 1 menit
    MAX_REQ_PER_MIN: 30,          // 30 req/menit per user
    MAX_IP_PER_MIN: 60,           // 60 req/menit per IP
    MAX_ADS_PER_DAY: 10,          // 10 iklan/hari per user
    AD_COOLDOWN_MS: 30000,        // 30 detik antar iklan
    MAX_ACCOUNTS_PER_DEVICE: 3,   // max 3 akun/device
    SUSPICIOUS_ADS_PER_5MIN: 5    // flag kalau >5 iklan dalam 5 menit
};

/**
 * Middleware checkFraud — cek user, device, rate limit, IP.
 */
async function checkFraud(req, res, next) {
    try {
        const { uid } = req.firebaseUser;
        const now = Date.now();
        const ip = req.ip || req.headers['x-forwarded-for'] || 'unknown';

        // ==== 1. Cek user diblokir ====
        const userDoc = await db.collection('users').doc(uid).get();
        if (userDoc.exists && userDoc.data().isBlocked) {
            return res.status(403).json({
                success: false,
                message: 'Akun diblokir karena aktivitas mencurigakan.'
            });
        }

        // ==== 2. Rate limit per USER (in-memory) ====
        const userRate = rateCache.get(uid) || { count: 0, windowStart: now };
        if (now - userRate.windowStart > CONFIG.RATE_WINDOW_MS) {
            userRate.count = 0;
            userRate.windowStart = now;
        }
        userRate.count++;
        rateCache.set(uid, userRate);

        if (userRate.count > CONFIG.MAX_REQ_PER_MIN) {
            return res.status(429).json({
                success: false,
                message: 'Terlalu banyak request. Coba lagi nanti.'
            });
        }

        // ==== 3. Rate limit per IP (in-memory) ====
        const ipRate = ipCache.get(ip) || { count: 0, windowStart: now };
        if (now - ipRate.windowStart > CONFIG.RATE_WINDOW_MS) {
            ipRate.count = 0;
            ipRate.windowStart = now;
        }
        ipRate.count++;
        ipCache.set(ip, ipRate);

        if (ipRate.count > CONFIG.MAX_IP_PER_MIN) {
            return res.status(429).json({
                success: false,
                message: 'Terlalu banyak request dari jaringan ini.'
            });
        }

        // ==== 4. Endpoint-specific rules ====
        const endpoint = req.path || req.url;
        const isAdReward = endpoint.includes('/ads/reward');

        if (isAdReward) {
            // 4a. Device ID — opsional (validasi hanya kalau ada)
            const deviceId = req.headers['x-device-id'];
            if (deviceId && deviceId.length > 0 && deviceId.length < 5) {
                return res.status(400).json({
                    success: false,
                    message: 'Device ID tidak valid.'
                });
            }

            // 4b. Cooldown antar iklan (30 detik)
            const lastAd = adCooldown.get(uid) || 0;
            if (now - lastAd < CONFIG.AD_COOLDOWN_MS) {
                return res.status(429).json({
                    success: false,
                    message: 'Tunggu sebentar sebelum nonton iklan lagi.'
                });
            }

            // 4c. Cek max ads/hari dari Firestore
            const today = new Date().toISOString().split('T')[0];
            const userData = userDoc.exists ? userDoc.data() : {};
            const lastAdDate = userData.lastAdDate || '';
            const todayAdsWatched = lastAdDate === today ? (userData.todayAdsWatched || 0) : 0;

            if (todayAdsWatched >= CONFIG.MAX_ADS_PER_DAY) {
                return res.status(429).json({
                    success: false,
                    message: 'Batas tonton iklan hari ini sudah tercapai.'
                });
            }

            // 4d. Deteksi anomaly (>5 iklan dalam 5 menit)
            const recentAds = (userData.recentAdTimestamps || []).filter(ts => now - ts < 300000);
            if (recentAds.length >= CONFIG.SUSPICIOUS_ADS_PER_5MIN) {
                await db.collection('audit_logs').add({
                    userId: uid,
                    action: 'SUSPICIOUS_ADS',
                    ip: ip,
                    deviceId: deviceId,
                    timestamp: admin.firestore.FieldValue.serverTimestamp(),
                    note: '>5 ads dalam 5 menit'
                });
                return res.status(429).json({
                    success: false,
                    message: 'Aktivitas tidak wajar terdeteksi. Tunggu beberapa menit.'
                });
            }
        }

        // ==== 5. Device ID tracking (max 3 akun/device) ====
        const deviceId = req.headers['x-device-id'];
        if (deviceId && deviceId.length > 5) {
            const deviceRef = db.collection('devices').doc(deviceId);
            const deviceDoc = await deviceRef.get();

            if (deviceDoc.exists) {
                const accounts = deviceDoc.data().accounts || [];
                if (!accounts.includes(uid)) {
                    if (accounts.length >= CONFIG.MAX_ACCOUNTS_PER_DEVICE) {
                        return res.status(403).json({
                            success: false,
                            message: 'Terlalu banyak akun di device ini.'
                        });
                    }
                    accounts.push(uid);
                    await deviceRef.update({
                        accounts: accounts,
                        lastSeen: admin.firestore.FieldValue.serverTimestamp()
                    });
                } else {
                    await deviceRef.update({
                        lastSeen: admin.firestore.FieldValue.serverTimestamp()
                    });
                }
            } else {
                await deviceRef.set({
                    accounts: [uid],
                    firstSeen: admin.firestore.FieldValue.serverTimestamp(),
                    lastSeen: admin.firestore.FieldValue.serverTimestamp()
                });
            }
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

// ==== Cleanup cache berkala (tiap 10 menit) ====
setInterval(() => {
    const now = Date.now();
    for (const [key, val] of rateCache.entries()) {
        if (now - val.windowStart > 300000) rateCache.delete(key);
    }
    for (const [key, val] of ipCache.entries()) {
        if (now - val.windowStart > 300000) ipCache.delete(key);
    }
    for (const [key, ts] of adCooldown.entries()) {
        if (now - ts > 300000) adCooldown.delete(key);
    }
}, 600000);

module.exports = { checkFraud, adCooldown, CONFIG };
