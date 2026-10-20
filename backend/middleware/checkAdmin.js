// middleware/checkAdmin.js
// Middleware: cek admin (terima 2 jenis auth)
// 1. x-admin-token (dari web panel)
// 2. Firebase ID token + role ADMIN (dari Android)

const admin = require('firebase-admin');
const AppLogger = require('../utils/AppLogger');

const DEBUG_ADMIN_LOG = process.env.DEBUG_ADMIN === '1';

// Token admin web disimpan di global map (shared dengan server.js)
if (!global.__adminTokens) global.__adminTokens = new Map();

async function checkAdmin(req, res, next) {
    try {
        // Opsi 1: x-admin-token (web panel)
        const adminToken = req.headers['x-admin-token'];

        if (DEBUG_ADMIN_LOG) AppLogger.d(`[checkAdmin] token prefix=${adminToken ? adminToken.substring(0, 12) : 'NULL'}, mapSize=${global.__adminTokens ? global.__adminTokens.size : 'NO_MAP'}`);

        if (adminToken && global.__adminTokens && global.__adminTokens.has(adminToken)) {
            const tokenData = global.__adminTokens.get(adminToken);
            if (tokenData.expiresAt > Date.now()) {
                if (DEBUG_ADMIN_LOG) AppLogger.d(`[checkAdmin] ✅ token match`);
                req.adminUser = { source: 'web', token: adminToken };
                return next();
            } else {
                if (DEBUG_ADMIN_LOG) AppLogger.d(`[checkAdmin] ⚠️ token expired`);
                return res.status(401).json({ success: false, message: 'Admin token expired. Login ulang.' });
            }
        }

        // Opsi 2: Firebase ID token + role admin
        const authHeader = req.headers['authorization'];
        if (authHeader && authHeader.startsWith('Bearer ')) {
            const idToken = authHeader.split('Bearer ')[1];
            try {
                const decodedToken = await admin.auth().verifyIdToken(idToken);
                const db = admin.firestore();
                const userDoc = await db.collection('users').doc(decodedToken.uid).get();
                if (userDoc.exists) {
                    const userData = userDoc.data() || {};
                    if (userData.role === 'admin' || userData.isAdmin === true) {
                        req.adminUser = { source: 'firebase', uid: decodedToken.uid };
                        return next();
                    }
                }
            } catch (e) {
                if (DEBUG_ADMIN_LOG) AppLogger.d(`[checkAdmin] Firebase token error: ${e.message}`);
            }
        }

        return res.status(403).json({ success: false, message: 'Admin access required.' });
    } catch (err) {
        console.error('[checkAdmin] Error:', err.message);
        return res.status(500).json({ success: false, message: 'Admin check failed.' });
    }
}

module.exports = { checkAdmin };
