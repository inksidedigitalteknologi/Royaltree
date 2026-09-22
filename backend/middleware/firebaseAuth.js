// middleware/firebaseAuth.js
// Verifikasi Firebase ID Token dari Android app

const { admin } = require('../config/firebase');

async function verifyFirebaseToken(req, res, next) {
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

module.exports = { verifyFirebaseToken };
