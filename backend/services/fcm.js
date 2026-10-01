// services/fcm.js
// Helper kirim Firebase Cloud Messaging (push notification)

const { admin } = require('../config/firebase');

/**
 * Kirim push notification ke 1 user via FCM token
 * @param {string} fcmToken - Token FCM user
 * @param {string} title - Judul notifikasi
 * @param {string} body - Isi notifikasi
 * @param {object} data - Data payload (opsional)
 */
async function sendPushNotification(fcmToken, title, body, data = {}) {
    if (!fcmToken) {
        console.log('[FCM] Token kosong, skip.');
        return { success: false, reason: 'NO_TOKEN' };
    }

    try {
        const message = {
            token: fcmToken,
            notification: { title, body },
            data: Object.keys(data).reduce((acc, k) => {
                acc[k] = String(data[k]);
                return acc;
            }, {}),
            android: {
                priority: 'high',
                notification: {
                    channelId: 'royaltree_default',
                    sound: 'default',
                    color: '#10B981'
                }
            }
        };

        const response = await admin.messaging().send(message);
        console.log('[FCM] Sent:', response);
        return { success: true, messageId: response };
    } catch (error) {
        console.error('[FCM] Error:', error.message);
        return { success: false, reason: error.message };
    }
}

/**
 * Kirim notifikasi dalam batch (max 500 per call)
 */
async function sendBatchPush(tokens, title, body, data = {}) {
    if (!tokens || tokens.length === 0) return { success: false };

    try {
        const messages = tokens.map(token => ({
            token,
            notification: { title, body },
            data: Object.keys(data).reduce((acc, k) => {
                acc[k] = String(data[k]);
                return acc;
            }, {}),
            android: {
                priority: 'high',
                notification: { channelId: 'royaltree_default', sound: 'default' }
            }
        }));

        const response = await admin.messaging().sendEach(messages);
        return { success: true, response };
    } catch (error) {
        console.error('[FCM] Batch error:', error.message);
        return { success: false, reason: error.message };
    }
}

module.exports = { sendPushNotification, sendBatchPush };
