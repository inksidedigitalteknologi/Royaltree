// ============================================================
// Notifications Helper — buat notifikasi ke user
// Letak di fail berasingan untuk elak circular dependency
// ============================================================

const admin = require('firebase-admin');
const AppLogger = require('../utils/AppLogger');

async function createNotification(userId, title, message, type = 'INFO', referenceId = null) {
    try {
        const db = admin.firestore();
        await db.collection('notifications').add({
            userId,
            title,
            message: message || '',
            type,
            referenceId: referenceId || null,
            read: false,
            timestamp: Date.now(),
            createdAt: new Date().toISOString()
        });
        AppLogger.d(`[NOTIF] Created for ${userId}: ${title}`);
    } catch (e) {
        console.error('[NOTIF] Error:', e.message);
    }
}

module.exports = { createNotification };
