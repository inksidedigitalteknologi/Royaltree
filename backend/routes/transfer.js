// ============================================================
// Transfer Routes — Kirim RTP antar user
// - GET  /search-user?q=  : cari user by username/ID/email
// - POST /send            : kirim RTP
// - GET  /history         : history transfer user
// ============================================================

const express = require('express');
const router = express.Router();
const admin = require('firebase-admin');
const { verifyFirebaseToken } = require('../middleware/firebaseAuth');
const { checkFraud } = require('../middleware/antiFraud');
const { createNotification } = require('../utils/notifications');

const db = admin.firestore();

// ============================================================
// CONFIG
// ============================================================
const TRANSFER_CONFIG = {
    minAmount: 20,           // RTP minimum transfer
    maxPerDay: 5,            // Max 5 transfer/hari
    maxAmountPerDay: 50000,  // Max 50,000 RTP/hari
};

// ============================================================
// HELPER: today string (WIB)
// ============================================================
function todayStr() {
    const now = new Date();
    const wib = new Date(now.getTime() + 7 * 60 * 60 * 1000);
    return wib.toISOString().split('T')[0];
}

// ============================================================
// GET /search-user — Cari user
// ============================================================
router.get('/search-user', verifyFirebaseToken, async (req, res) => {
    try {
        const { uid } = req.firebaseUser;
        const query = (req.query.q || '').trim();

        if (!query || query.length < 3) {
            return res.status(400).json({
                success: false,
                message: 'Query minimal 3 karakter'
            });
        }

        // Cari by username, name, atau referralCode
        // Firestore tidak support OR — kita cuba beberapa query
        const searchLower = query.toLowerCase();
        
        // Cari by username dulu
        const byUsername = await db.collection('users')
            .where('username', '==', searchLower)
            .limit(5)
            .get();

        // Cari by referralCode
        const byRefCode = await db.collection('users')
            .where('referralCode', '==', query.toUpperCase())
            .limit(5)
            .get();

        // Gabungkan hasil + exclude diri sendiri
        const results = new Map();
        
        byUsername.forEach(doc => {
            if (doc.id !== uid) results.set(doc.id, doc.data());
        });
        byRefCode.forEach(doc => {
            if (doc.id !== uid) results.set(doc.id, doc.data());
        });

        // Cari by name (case-insensitive partial)
        const byName = await db.collection('users')
            .orderBy('name')
            .startAt(query)
            .endAt(query + '\uf8ff')
            .limit(10)
            .get();

        byName.forEach(doc => {
            if (doc.id !== uid) results.set(doc.id, doc.data());
        });

        const users = Array.from(results.entries())
            .slice(0, 10)
            .map(([id, data]) => ({
                uid: id,
                name: data.name || 'Anonim',
                username: data.username || '',
                referralCode: data.referralCode || '',
                tier: data.tier || 'FREE',
                avatar: '👤'
            }));

        if (users.length === 0) {
            return res.json({
                success: true,
                data: { users: [], message: 'User tidak ditemukan' }
            });
        }

        res.json({ success: true, data: { users } });
    } catch (error) {
        console.error('[TRANSFER] /search-user error:', error);
        res.status(500).json({ success: false, message: 'Internal server error' });
    }
});

// ============================================================
// POST /send — Kirim RTP
// Body: { recipientId, amount, note? }
// ============================================================
router.post('/send', verifyFirebaseToken, checkFraud, async (req, res) => {
    try {
        const { uid } = req.firebaseUser;
        const { recipientId, amount, note } = req.body;

        // Validasi input
        if (!recipientId || !amount) {
            return res.status(400).json({
                success: false,
                message: 'recipientId dan amount wajib diisi'
            });
        }

        const amountNum = parseInt(amount);
        if (isNaN(amountNum) || amountNum < TRANSFER_CONFIG.minAmount) {
            return res.status(400).json({
                success: false,
                message: `Minimal transfer ${TRANSFER_CONFIG.minAmount} RTP`
            });
        }

        if (recipientId === uid) {
            return res.status(400).json({
                success: false,
                message: 'Tidak bisa transfer ke diri sendiri'
            });
        }

        // Cek sender
        const senderRef = db.collection('users').doc(uid);
        const senderDoc = await senderRef.get();
        if (!senderDoc.exists) {
            return res.status(404).json({ success: false, message: 'Sender tidak ditemukan' });
        }
        const senderData = senderDoc.data();
        const senderPoints = senderData.points || 0;

        if (senderPoints < amountNum) {
            return res.status(400).json({
                success: false,
                message: `Saldo tidak cukup. Saldo Anda ${senderPoints} RTP, butuh ${amountNum} RTP`
            });
        }

        // Cek min 20 RTP tersisa
        if (senderPoints - amountNum < TRANSFER_CONFIG.minAmount) {
            return res.status(400).json({
                success: false,
                message: `Harus sisa min ${TRANSFER_CONFIG.minAmount} RTP di saldo Anda`
            });
        }

        // Cek recipient
        const recipientRef = db.collection('users').doc(recipientId);
        const recipientDoc = await recipientRef.get();
        if (!recipientDoc.exists) {
            return res.status(404).json({ success: false, message: 'Penerima tidak ditemukan' });
        }
        const recipientData = recipientDoc.data();

        // Cek rate limit harian
        const today = todayStr();
        const todayStart = new Date(today + 'T00:00:00+07:00').getTime();
        
        const todayTransfers = await db.collection('transfers')
            .where('senderId', '==', uid)
            .where('timestamp', '>=', todayStart)
            .get();

        if (todayTransfers.size >= TRANSFER_CONFIG.maxPerDay) {
            return res.status(429).json({
                success: false,
                message: `Max ${TRANSFER_CONFIG.maxPerDay} transfer/hari`
            });
        }

        const todayTotal = todayTransfers.docs.reduce((sum, d) => sum + (d.data().amount || 0), 0);
        if (todayTotal + amountNum > TRANSFER_CONFIG.maxAmountPerDay) {
            return res.status(429).json({
                success: false,
                message: `Max ${TRANSFER_CONFIG.maxAmountPerDay} RTP/hari`
            });
        }

        // ============ EXECUTE TRANSFER ============
        const batch = db.batch();
        const now = Date.now();

        // 1. Potong sender
        batch.update(senderRef, {
            points: admin.firestore.FieldValue.increment(-amountNum)
        });

        // 2. Tambah recipient
        batch.update(recipientRef, {
            points: admin.firestore.FieldValue.increment(amountNum)
        });

        // 3. Log transfer
        const transferRef = db.collection('transfers').doc();
        batch.set(transferRef, {
            senderId: uid,
            senderName: senderData.name || 'Anonim',
            recipientId: recipientId,
            recipientName: recipientData.name || 'Anonim',
            amount: amountNum,
            note: note || '',
            timestamp: now,
            date: today
        });

        // 4. Audit log
        const auditRef = db.collection('audit_logs').doc();
        batch.set(auditRef, {
            userId: uid,
            action: 'TRANSFER_RTP',
            recipientId: recipientId,
            amount: amountNum,
            timestamp: admin.firestore.FieldValue.serverTimestamp()
        });

        await batch.commit();

        // Notifikasi SENDER
        await createNotification(
            uid,
            '📤 Transfer Terkirim',
            `${amountNum} RTP dikirim ke ${recipientData.name || 'user'}`,
            'TRANSFER'
        );

        // Notifikasi RECIPIENT
        await createNotification(
            recipientId,
            '📥 Terima Transfer',
            `${amountNum} RTP dari ${senderData.name || 'user'}`,
            'TRANSFER'
        );

        res.json({
            success: true,
            message: `Berhasil transfer ${amountNum} RTP ke ${recipientData.name || 'user'}`,
            data: {
                transferId: transferRef.id,
                amount: amountNum,
                recipientName: recipientData.name || 'Anonim',
                remainingBalance: senderPoints - amountNum
            }
        });
    } catch (error) {
        console.error('[TRANSFER] /send error:', error);
        res.status(500).json({ success: false, message: 'Internal server error' });
    }
});

// ============================================================
// GET /history — History transfer user
// ============================================================
router.get('/history', verifyFirebaseToken, async (req, res) => {
    try {
        const { uid } = req.firebaseUser;
        const limit = parseInt(req.query.limit) || 20;

        // Sent transfers
        const sentSnap = await db.collection('transfers')
            .where('senderId', '==', uid)
            .limit(50)
            .get();

        // Received transfers
        const receivedSnap = await db.collection('transfers')
            .where('recipientId', '==', uid)
            .limit(50)
            .get();

        // Gabung + sort by timestamp desc
        const all = [];
        
        sentSnap.forEach(doc => {
            const d = doc.data();
            all.push({
                id: doc.id,
                type: 'SENT',
                amount: d.amount,
                counterpartyName: d.recipientName,
                note: d.note,
                timestamp: d.timestamp
            });
        });
        
        receivedSnap.forEach(doc => {
            const d = doc.data();
            all.push({
                id: doc.id,
                type: 'RECEIVED',
                amount: d.amount,
                counterpartyName: d.senderName,
                note: d.note,
                timestamp: d.timestamp
            });
        });

        all.sort((a, b) => b.timestamp - a.timestamp);

        res.json({
            success: true,
            data: { transfers: all.slice(0, limit) }
        });
    } catch (error) {
        console.error('[TRANSFER] /history error:', error);
        res.status(500).json({ success: false, message: 'Internal server error' });
    }
});

module.exports = router;
