// routes/chat.js
// Global Chat — limit 50 chat terakhir
// Endpoint:
//   GET  /api/v1/chat/messages  → ambil 50 chat terakhir
//   POST /api/v1/chat/send      → kirim chat

const express = require('express');
const admin = require('firebase-admin');
const db = admin.firestore();
const { verifyFirebaseToken } = require('../middleware/firebaseAuth');

const router = express.Router();

const MAX_MESSAGES = 50;
const MAX_MESSAGE_LENGTH = 200;
const RATE_LIMIT_MS = 5000; // 1 chat per 5 detik

// In-memory rate limit
const lastMessageTime = new Map();

// ============================================================
// GET /api/v1/chat/messages
// Ambil 50 chat terakhir (urut dari lama ke baru)
// ============================================================
router.get('/messages', verifyFirebaseToken, async (req, res) => {
    try {
        const snapshot = await db.collection('global_chat')
            .orderBy('timestamp', 'desc')
            .limit(MAX_MESSAGES)
            .get();

        const messages = [];
        snapshot.forEach(doc => {
            const data = doc.data();
            messages.push({
                id: doc.id,
                userId: data.userId || '',
                userName: data.userName || 'Anonymous',
                message: data.message || '',
                timestamp: data.timestamp || 0
            });
        });

        // Balik urutan: dari lama ke baru
        messages.reverse();

        return res.json({
            success: true,
            messages: messages,
            count: messages.length
        });
    } catch (error) {
        console.error('[CHAT] Get messages error:', error);
        return res.status(500).json({ success: false, message: error.message });
    }
});

// ============================================================
// POST /api/v1/chat/send
// Body: { message: string }
// ============================================================
router.post('/send', verifyFirebaseToken, async (req, res) => {
    try {
        const { uid } = req.firebaseUser;
        const { message } = req.body;

        // Validasi message
        if (!message || typeof message !== 'string') {
            return res.status(400).json({
                success: false,
                message: 'Pesan wajib diisi.'
            });
        }

        const trimmed = message.trim();
        if (trimmed.length === 0) {
            return res.status(400).json({
                success: false,
                message: 'Pesan tidak boleh kosong.'
            });
        }

        if (trimmed.length > MAX_MESSAGE_LENGTH) {
            return res.status(400).json({
                success: false,
                message: `Pesan maksimal ${MAX_MESSAGE_LENGTH} karakter.`
            });
        }

        // Rate limit
        const now = Date.now();
        const lastTime = lastMessageTime.get(uid) || 0;
        if (now - lastTime < RATE_LIMIT_MS) {
            const waitSec = Math.ceil((RATE_LIMIT_MS - (now - lastTime)) / 1000);
            return res.status(429).json({
                success: false,
                message: `Tunggu ${waitSec} detik sebelum kirim pesan lagi.`
            });
        }
        lastMessageTime.set(uid, now);

        // Ambil user data untuk nama
        const userDoc = await db.collection('users').doc(uid).get();
        const userData = userDoc.exists ? userDoc.data() : {};
        const userName = userData.name || userData.username || 'Anonymous';

        // Simpan chat
        const chatRef = await db.collection('global_chat').add({
            userId: uid,
            userName: userName,
            message: trimmed,
            timestamp: now,
            createdAt: admin.firestore.FieldValue.serverTimestamp()
        });

        // Auto-delete: hapus chat >50
        try {
            const oldMessages = await db.collection('global_chat')
                .orderBy('timestamp', 'desc')
                .offset(MAX_MESSAGES)
                .limit(50)
                .get();

            const batch = db.batch();
            oldMessages.forEach(doc => {
                batch.delete(doc.ref);
            });
            if (!oldMessages.empty) {
                await batch.commit();
                console.log(`[CHAT] Deleted ${oldMessages.size} old messages`);
            }
        } catch (deleteError) {
            console.error('[CHAT] Auto-delete error:', deleteError);
            // Tidak fatal, tetap return sukses
        }

        return res.json({
            success: true,
            message: 'Pesan terkirim.',
            chatId: chatRef.id
        });
    } catch (error) {
        console.error('[CHAT] Send error:', error);
        return res.status(500).json({ success: false, message: error.message });
    }
});

module.exports = router;
