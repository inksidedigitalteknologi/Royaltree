// services/configRefresh.js
// Broadcast config refresh (silent push) ke semua user
// Trigger: admin ubah config di portal

const { admin } = require('../config/firebase');
const AppLogger = require('../utils/AppLogger');
const db = admin.firestore();

/**
 * Broadcast config refresh ke semua user.
 * Silent push (data-only, tanpa notif UI).
 * App terima → re-fetch config via API.
 *
 * @param {string} configType — 'game' | 'ads' | 'daily' | 'thermal'
 * @returns {Promise<{sent: number, failed: number, error?: string}>}
 */
async function broadcastConfigRefresh(configType) {
    try {
        // Ambil semua user dengan fcmToken
        const usersSnapshot = await db.collection('users').get();

        const tokens = [];
        usersSnapshot.forEach(doc => {
            const token = doc.data().fcmToken;
            if (token && typeof token === 'string' && token.length > 20) {
                tokens.push(token);
            }
        });

        if (tokens.length === 0) {
            AppLogger.d('[CONFIG-REFRESH] No FCM tokens to send');
            return { sent: 0, failed: 0 };
        }

        // Data-only silent push (tanpa notification block)
        const message = {
            data: {
                type: 'CONFIG_REFRESH',
                configType: String(configType),
                timestamp: String(Date.now())
            },
            tokens: tokens
        };

        const response = await admin.messaging().sendEachForMulticast(message);

        AppLogger.d(`[CONFIG-REFRESH] Sent ${configType} refresh to ${response.successCount}/${tokens.length} users`);

        return {
            sent: response.successCount,
            failed: response.failureCount
        };
    } catch (e) {
        console.error('[CONFIG-REFRESH] Error:', e.message);
        return { sent: 0, failed: 0, error: e.message };
    }
}

module.exports = { broadcastConfigRefresh };
