// middleware/firebaseAuth.js
// Verifikasi Firebase ID Token dari Android app + cek banned

const { admin, db } = require('../config/firebase');

async function verifyFirebaseToken(req, res, next) {
    // Skip kalau sudah di-verify oleh verifyAuthAndBanCheck (server.js level)
    if (req.firebaseUser && req.firebaseUser.uid) {
        return next();
    }

    const authHeader = req.headers['authorization'];

    if (!authHeader || !authHeader.startsWith('Bearer ')) {
        return res.status(401).json({
            success: false,
            message: 'Token tidak ditemukan. Silakan login ulang.'
        });
    }

    const idToken = authHeader.split('Bearer ')[1];

    try {
        const decodedToken = await admin.auth().verifyIdToken(idToken);
        req.firebaseUser = {
            uid: decodedToken.uid,
            email: decodedToken.email || '',
            emailVerified: decodedToken.email_verified || false,
            name: decodedToken.name || '',
            picture: decodedToken.picture || ''
        };
        next();
    } catch (error) {
        console.error('Firebase token verification failed:', error.message);
        return res.status(401).json({
            success: false,
            message: 'Token tidak valid atau kadaluarsa. Silakan login ulang.'
        });
    }
}

/**
 * Middleware: cek apakah user di-banned.
 * Pakai SETELAH verifyFirebaseToken.
 * Return 403 USER_BANNED jika banned.
 */
async function checkBanned(req, res, next) {
    try {
        const uid = req.firebaseUser?.uid;
        if (!uid) return next();

        const userDoc = await db.collection('users').doc(uid).get();
        if (!userDoc.exists) return next();

        const userData = userDoc.data() || {};
        if (userData.banned === true) {
            return res.status(403).json({
                success: false,
                code: 'USER_BANNED',
                message: userData.bannedReason
                    ? `Akun Anda diblokir: ${userData.bannedReason}`
                    : 'Akun Anda diblokir. Hubungi support untuk informasi.',
                bannedAt: userData.bannedAt,
                bannedReason: userData.bannedReason || 'Pelanggaran aturan',
            });
        }
        next();
    } catch (err) {
        console.error('[checkBanned] Error:', err.message);
        // Fail-closed: kalau error, blokir (safety first)
        return res.status(500).json({
            success: false,
            message: 'Verifikasi akun gagal. Coba lagi.'
        });
    }
}


/**
 * Middleware: verify token + cek banned (combined).
 * Pakai di level router: app.use('/api/v1/game', verifyAuthAndBanCheck, gameRoutes)
 * 
 * WAJIB: endpoint di dalam router tetap harus panggil verifyFirebaseToken
 * untuk dapat uid. Middleware ini cuma tambahan layer cek banned.
 * 
 * Implementasi: 
 * - Kalau request tidak punya Authorization header, skip (biar endpoint public tetap bisa).
 * - Kalau ada header, verify token DULU, baru cek banned.
 */
async function verifyAuthAndBanCheck(req, res, next) {
    const authHeader = req.headers['authorization'];

    // Skip kalau tidak ada token (biar public endpoint tetap jalan)
    if (!authHeader || !authHeader.startsWith('Bearer ')) {
        return next();
    }

    // Verify token dulu
    const idToken = authHeader.split('Bearer ')[1];
    try {
        const { admin, db } = require('../config/firebase');
        const decodedToken = await admin.auth().verifyIdToken(idToken);
        req.firebaseUser = {
            uid: decodedToken.uid,
            email: decodedToken.email || '',
            emailVerified: decodedToken.email_verified || false,
            name: decodedToken.name || '',
            picture: decodedToken.picture || ''
        };

        // Cek banned
        const userDoc = await db.collection('users').doc(decodedToken.uid).get();
        if (userDoc.exists) {
            const userData = userDoc.data() || {};
            if (userData.banned === true) {
                return res.status(403).json({
                    success: false,
                    code: 'USER_BANNED',
                    message: userData.bannedReason
                        ? `Akun Anda diblokir: ${userData.bannedReason}`
                        : 'Akun Anda diblokir. Hubungi support untuk informasi.',
                    bannedAt: userData.bannedAt,
                    bannedReason: userData.bannedReason || 'Pelanggaran aturan',
                });
            }
        }

        next();
    } catch (error) {
        // Kalau token invalid, biarkan router yang handle (via verifyFirebaseToken)
        // Jangan block di sini biar error message konsisten
        next();
    }
}

module.exports = { verifyFirebaseToken, checkBanned, verifyAuthAndBanCheck };
