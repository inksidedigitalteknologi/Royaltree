// ============================================================
// Sweepstakes Routes — 2 minggu per periode
// - GET  /current         : info sweepstakes aktif + user tickets
// - POST /buy             : beli kupon
// - GET  /history         : history periode lalu
// - GET  /winners/:id     : pemenang periode tertentu
// - POST /draw            : trigger draw (admin/cron)
// - GET  /my-tickets      : tiket user saat ini
// ============================================================

const express = require('express');
const router = express.Router();
const admin = require('firebase-admin');
const crypto = require('crypto');
const { verifyFirebaseToken } = require('../middleware/firebaseAuth');
const { checkFraud } = require('../middleware/antiFraud');
const { createNotification } = require('../utils/notifications');

// Validation
const { validate } = require('../middleware/validate');
const { buyTicketSchema } = require('../schemas');
const AppLogger = require('../utils/AppLogger');

const db = admin.firestore();

// ============================================================
// CONFIG
// ============================================================
const SWEEPSTAKES_CONFIG = {
  ticketPrice: 50,                    // RTP per kupon
  maxTicketsPerUser: 200,             // Max kupon/user
  durationDays: 14,                   // 2 minggu
  platformCut: 0.20,                  // 20%
  treasuryCut: 0.03,                  // 3%
  rewardPool: 0.77,                   // 77%
  
  // Reward FIXED
  totalReward: 255000,                // RTP ($17)
  rewards: [
    { rank: 1, count: 1, amount: 75000 },   // $5
    { rank: 2, count: 1, amount: 45000 },   // $3
    { rank: 3, count: 1, amount: 30000 },   // $2
    { rank: 4, count: 7, amount: 15000 }    // $1 × 7 = $7
  ],
  
  // Minimum kupon untuk draw
  // Dynamic: 50% dari reward pool / ticketPrice
  // 255,000 / 50 * 0.5 = 2,550
  minTickets: 2550,
  
  // Rule
  oneWinPerAccount: true,
  
  // Bonus packages
  bonusPackages: {
    5: 1,    // Beli 5 gratis 1
    10: 3,   // Beli 10 gratis 3
    20: 8,   // Beli 20 gratis 8
    50: 25   // Beli 50 gratis 25
  }
};

// ============================================================
// HELPER: Generate sweepstakes ID
// ============================================================
function generateSweepstakesId() {
  const now = new Date();
  const yyyy = now.getFullYear();
  const mm = String(now.getMonth() + 1).padStart(2, '0');
  const dd = String(now.getDate()).padStart(2, '0');
  return `sweep_${yyyy}_${mm}_${dd}`;
}

// ============================================================
// HELPER: Get atau create sweepstakes aktif
// ============================================================
async function getOrCreateActiveSweepstakes() {
  const now = Date.now();
  
  // Fetch all + filter di memory (zero index required)
  const allSweepsSnap = await db.collection('sweeps_v2').get();

  const activeSweeps = allSweepsSnap.docs
    .map(doc => ({ id: doc.id, ...doc.data() }))
    .filter(s => s.status === 'active' && s.endTime > now)
    .sort((a, b) => b.endTime - a.endTime);

  if (activeSweeps.length > 0) {
    return activeSweeps[0];
  }

  
  // Tiada sweepstakes aktif → buat baru
  const sweepId = generateSweepstakesId();
  const startTime = now;
  const endTime = now + (SWEEPSTAKES_CONFIG.durationDays * 24 * 60 * 60 * 1000);
  
  const newSweep = {
    status: 'active',
    startTime,
    endTime,
    ticketPrice: SWEEPSTAKES_CONFIG.ticketPrice,
    totalTickets: 0,
    totalPool: 0,
    platformCut: 0,
    treasuryCut: 0,
    rewardPool: 0,
    winners: [],
    createdAt: admin.firestore.FieldValue.serverTimestamp()
  };
  
  await db.collection('sweeps_v2').doc(sweepId).set(newSweep);
  return { id: sweepId, ...newSweep };
}

// ============================================================
// GET /current — Info sweepstakes aktif
// ============================================================
router.get('/current', verifyFirebaseToken, async (req, res) => {
  try {
    const { uid } = req.firebaseUser;
    const sweep = await getOrCreateActiveSweepstakes();
    
    // Ambil tiket user
    const userTicketsSnap = await db.collection('tickets')
      .where('sweepstakesId', '==', sweep.id)
      .where('uid', '==', uid)
      .get();
    
    const userTickets = userTicketsSnap.size;
    const userChance = sweep.totalTickets > 0 
      ? (userTickets / sweep.totalTickets * 100).toFixed(4) 
      : '0.0000';
    
    // PRIVASI: tidak compute top holders

    
    res.json({
      success: true,
      data: {
        id: sweep.id,
        status: sweep.status,
        startTime: sweep.startTime,
        endTime: sweep.endTime,
        ticketPrice: sweep.ticketPrice,
        // totalTickets: DISEMBUNYIKAN untuk privasi
        totalPool: sweep.totalPool,
        userTickets,
        userChance: userChance + '%',
        minTickets: SWEEPSTAKES_CONFIG.minTickets,
        rewards: SWEEPSTAKES_CONFIG.rewards,
        bonusPackages: SWEEPSTAKES_CONFIG.bonusPackages,
        maxTicketsPerUser: SWEEPSTAKES_CONFIG.maxTicketsPerUser
        // topHolders: DISEMBUNYIKAN untuk privasi
      }
    });
  } catch (error) {
    console.error('[SWEEPSTAKES] /current error:', error);
    res.status(500).json({ success: false, message: 'Internal server error' });
  }
});

// ============================================================
// POST /buy — Beli kupon
// Body: { quantity: 1|5|10|20|50 }
// ============================================================
router.post('/buy', verifyFirebaseToken, validate(buyTicketSchema), checkFraud, async (req, res) => {
  try {
    const { uid } = req.firebaseUser;
    const { quantity } = req.body;
    
    // Validasi quantity
    const validQty = [1, 5, 10, 20, 50];
    if (!validQty.includes(quantity)) {
      return res.status(400).json({ 
        success: false, 
        message: 'Kuantiti tidak valid. Pilih 1, 5, 10, 20, atau 50.' 
      });
    }
    
    const sweep = await getOrCreateActiveSweepstakes();
    
    // Cek user tickets sekarang
    const userTicketsSnap = await db.collection('tickets')
      .where('sweepstakesId', '==', sweep.id)
      .where('uid', '==', uid)
      .get();
    
    const currentTickets = userTicketsSnap.size;
    const bonus = SWEEPSTAKES_CONFIG.bonusPackages[quantity] || 0;
    const totalNew = quantity + bonus;
    
    if (currentTickets + totalNew > SWEEPSTAKES_CONFIG.maxTicketsPerUser) {
      return res.status(400).json({
        success: false,
        message: `Max ${SWEEPSTAKES_CONFIG.maxTicketsPerUser} kupon per periode. Anda sudah punya ${currentTickets}.`
      });
    }
    
    // Hitung harga
    const totalPrice = quantity * SWEEPSTAKES_CONFIG.ticketPrice;
    
    // Cek saldo user
    const userRef = db.collection('users').doc(uid);
    const userDoc = await userRef.get();
    if (!userDoc.exists) {
      return res.status(404).json({ success: false, message: 'User tidak ditemukan' });
    }
    const userData = userDoc.data();
    const userPoints = userData.points || 0;
    
    if (userPoints < totalPrice) {
      return res.status(400).json({
        success: false,
        message: `Saldo tidak cukup. Butuh ${totalPrice} RTP, saldo Anda ${userPoints} RTP.`
      });
    }
    
    // ============ TRANSAKSI ============
    const batch = db.batch();
    
    // 1. Potong saldo user
    batch.update(userRef, {
      points: admin.firestore.FieldValue.increment(-totalPrice)
    });
    
    // 2. Buat tiket baru
    const ticketIds = [];
    const now = Date.now();
    for (let i = 0; i < totalNew; i++) {
      const ticketRef = db.collection('tickets').doc();
      batch.set(ticketRef, {
        sweepstakesId: sweep.id,
        uid,
        ticketNumber: String(sweep.totalTickets + i + 1).padStart(6, '0'),
        isBonus: i >= quantity,  // Bonus ticket flag
        purchasedAt: now,
        batchId: `${uid}_${now}`
      });
      ticketIds.push(ticketRef.id);
    }
    
    // 3. Update sweepstakes total
    const sweepRef = db.collection('sweeps_v2').doc(sweep.id);
    batch.update(sweepRef, {
      totalTickets: admin.firestore.FieldValue.increment(totalNew),
      totalPool: admin.firestore.FieldValue.increment(totalPrice)
    });
    
    // 4. Log transaction
    const txRef = db.collection('sweepstakes_transactions').doc();
    batch.set(txRef, {
      uid,
      sweepstakesId: sweep.id,
      quantity,
      bonus,
      totalTickets: totalNew,
      totalPrice,
      timestamp: admin.firestore.FieldValue.serverTimestamp()
    });
    
    await batch.commit();
    
    // Notifikasi
    await createNotification(
        uid,
        '🎟️ Kupon Terbeli',
        `${totalNew} kupon sweepstakes` + (bonus > 0 ? ` (+${bonus} bonus)` : ''),
        'SWEEPSTAKES'
    );

    // Get updated user points
    const updatedUserDoc = await userRef.get();
    const remainingRtp = updatedUserDoc.data().points || 0;
    
    res.json({
      success: true,
      message: `Berhasil beli ${totalNew} kupon!`,
      data: {
        quantity,
        bonus,
        totalTickets: totalNew,
        totalPrice,
        remainingRtp,
        ticketIds: ticketIds.slice(0, 10)  // Return 10 pertama
      }
    });
  } catch (error) {
    console.error('[SWEEPSTAKES] /buy error:', error);
    res.status(500).json({ success: false, message: 'Internal server error' });
  }
});

// ============================================================
// GET /history — History sweepstakes
// ============================================================
router.get('/history', verifyFirebaseToken, async (req, res) => {
  try {
    const { uid } = req.firebaseUser;
    const limit = parseInt(req.query.limit) || 10;
    
    // Fetch all + filter di memory
    const snap = await db.collection('sweeps_v2').get();
    
    // Sort by endTime desc, limit
    const sortedDocs = snap.docs
      .map(doc => ({ id: doc.id, ...doc.data() }))
      .sort((a, b) => b.endTime - a.endTime)
      .slice(0, limit);
    
    const history = [];
    for (const data of sortedDocs) {
      const doc = { id: data.id };
      
      // Cek apakah user menang di sweepstakes ini
      const userWon = (data.winners || []).find(w => w.uid === uid);
      

      // M49: Fetch names untuk top 3 winners
      const topRaw = (data.winners || []).slice(0, 3);
      const topWithNames = await Promise.all(
        topRaw.map(async (w) => {
          const userDoc = await db.collection('users').doc(w.uid).get();
          return {
            ...w,
            name: userDoc.exists ? (userDoc.data().name || 'Anonim') : 'Anonim'
          };
        })
      );
      history.push({
        id: data.id,
        status: data.status || 'ended',
        refundedAt: data.refundedAt || null,
        minTickets: SWEEPSTAKES_CONFIG.minTickets,
        startTime: data.startTime,
        endTime: data.endTime,
        totalTickets: data.totalTickets,
        totalPool: data.totalPool,
        winnerCount: (data.winners || []).length,
        topWinners: topWithNames,
        userWon: userWon || null
      });
    }
    
    res.json({ success: true, data: history });
  } catch (error) {
    console.error('[SWEEPSTAKES] /history error:', error);
    res.status(500).json({ success: false, message: 'Internal server error' });
  }
});

// ============================================================
// GET /winners/:id — Pemenang periode
// ============================================================
router.get('/winners/:id', verifyFirebaseToken, async (req, res) => {
  try {
    const { id } = req.params;
    const doc = await db.collection('sweeps_v2').doc(id).get();
    
    if (!doc.exists) {
      return res.status(404).json({ success: false, message: 'Sweepstakes tidak ditemukan' });
    }
    
    const data = doc.data();
    const winners = data.winners || [];
    
    // Get usernames
    const winnersWithNames = await Promise.all(
      winners.map(async (w) => {
        const userDoc = await db.collection('users').doc(w.uid).get();
        return {
          ...w,
          name: userDoc.exists ? (userDoc.data().name || 'Anonim') : 'Anonim'
        };
      })
    );
    
    res.json({
      success: true,
      data: {
        id,
        status: data.status,
        endTime: data.endTime,
        totalTickets: data.totalTickets,
        totalPool: data.totalPool,
        winners: winnersWithNames,
        seed: data.drawSeed || null,
        drawnAt: data.drawnAt || null
      }
    });
  } catch (error) {
    console.error('[SWEEPSTAKES] /winners error:', error);
    res.status(500).json({ success: false, message: 'Internal server error' });
  }
});

// ============================================================
// GET /my-tickets — Tiket user
// ============================================================
router.get('/my-tickets', verifyFirebaseToken, async (req, res) => {
  try {
    const { uid } = req.firebaseUser;
    const sweep = await getOrCreateActiveSweepstakes();
    
    const snap = await db.collection('tickets')
      .where('sweepstakesId', '==', sweep.id)
      .where('uid', '==', uid)
      .get();
    
    const tickets = snap.docs.map(doc => ({
      id: doc.id,
      number: doc.data().ticketNumber,
      isBonus: doc.data().isBonus || false,
      purchasedAt: doc.data().purchasedAt
    }));
    
    res.json({
      success: true,
      data: {
        sweepstakesId: sweep.id,
        count: tickets.length,
        tickets
      }
    });
  } catch (error) {
    console.error('[SWEEPSTAKES] /my-tickets error:', error);
    res.status(500).json({ success: false, message: 'Internal server error' });
  }
});

// ============================================================
// POST /draw — Trigger draw (admin only)
// ============================================================
router.post('/draw', verifyFirebaseToken, async (req, res) => {
  try {
    const { uid } = req.firebaseUser;
    
    // Cek admin
    const userDoc = await db.collection('users').doc(uid).get();
    if (!userDoc.exists || !userDoc.data().isAdmin) {
      return res.status(403).json({ success: false, message: 'Admin only' });
    }
    
    const result = await drawSweepstakes();
    res.json({ success: true, data: result });
  } catch (error) {
    console.error('[SWEEPSTAKES] /draw error:', error);
    res.status(500).json({ success: false, message: "Terjadi kesalahan server. Coba lagi nanti." });
  }
});

// ============================================================
// DRAW ALGORITHM — Fair random dengan crypto
// ============================================================
async function drawSweepstakes(sweepstakesId = null) {
  // 1. Get sweepstakes
  let sweep;
  if (sweepstakesId) {
    const doc = await db.collection('sweeps_v2').doc(sweepstakesId).get();
    sweep = { id: doc.id, ...doc.data() };
  } else {
    // Get sweepstakes yang endTime < now dan masih active
    // Simplified query — filter di memory (tak perlu composite index)
    // Fetch all + filter di memory (zero index required)
    const allSnap = await db.collection('sweeps_v2').get();

    const expired = allSnap.docs
      .map(doc => ({ id: doc.id, ...doc.data() }))
      .filter(s => s.status === 'active' && s.endTime <= Date.now())
      .sort((a, b) => a.endTime - b.endTime)
    
    if (expired.length === 0) {
      throw new Error('Tiada sweepstakes yang perlu di-draw');
    }
    
    sweep = expired[0];
  }
  
  // 2. Cek minimum kupon
  if (sweep.totalTickets < SWEEPSTAKES_CONFIG.minTickets) {
    // Refund semua
    AppLogger.d(`[SWEEPSTAKES] ${sweep.id} — kupon kurang (${sweep.totalTickets} < ${SWEEPSTAKES_CONFIG.minTickets}), refund`);
    await refundSweepstakes(sweep.id);
    return { refunded: true, reason: 'min_tickets_not_met' };
  }
  
  // 3. Ambil semua tiket
  const ticketsSnap = await db.collection('tickets')
    .where('sweepstakesId', '==', sweep.id)
    .get();
  
  const allTickets = ticketsSnap.docs.map(doc => ({
    id: doc.id,
    uid: doc.data().uid,
    number: doc.data().ticketNumber
  }));
  
  if (allTickets.length === 0) {
    throw new Error('Tiada tiket');
  }
  
  // 4. Crypto shuffle (fair)
  const seed = crypto.randomBytes(32).toString('hex');
  const shuffled = [...allTickets];
  for (let i = shuffled.length - 1; i > 0; i--) {
    const j = crypto.randomInt(0, i + 1);
    [shuffled[i], shuffled[j]] = [shuffled[j], shuffled[i]];
  }
  
  // 5. Pilih pemenang (1 akun max 1 kemenangan)
  const winners = [];
  const seenUids = new Set();
  const totalNeeded = SWEEPSTAKES_CONFIG.rewards.reduce((sum, r) => sum + r.count, 0);
  
  for (const ticket of shuffled) {
    if (winners.length >= totalNeeded) break;
    if (seenUids.has(ticket.uid)) continue;  // Skip kalau dah menang
    seenUids.add(ticket.uid);
    winners.push(ticket);
  }
  
  // 6. Distribusi reward
  let winnerIdx = 0;
  const resultWinners = [];
  for (const reward of SWEEPSTAKES_CONFIG.rewards) {
    for (let i = 0; i < reward.count; i++) {
      if (winnerIdx >= winners.length) break;
      const winner = winners[winnerIdx];
      resultWinners.push({
        rank: reward.rank,
        uid: winner.uid,
        ticketNumber: winner.number,
        amount: reward.amount
      });
      winnerIdx++;
    }
  }
  
  // 7. Batch update
  const batch = db.batch();
  
  // Bayar reward
  for (const w of resultWinners) {
    batch.update(db.collection('users').doc(w.uid), {
      points: admin.firestore.FieldValue.increment(w.amount)
    });
  }
  
  // Update sweepstakes
  const platformCut = Math.floor(sweep.totalPool * SWEEPSTAKES_CONFIG.platformCut);
  const treasuryCut = Math.floor(sweep.totalPool * SWEEPSTAKES_CONFIG.treasuryCut);
  const totalRewardPaid = resultWinners.reduce((s, w) => s + w.amount, 0);
  
  batch.update(db.collection('sweeps_v2').doc(sweep.id), {
    status: 'ended',
    winners: resultWinners,
    drawSeed: seed,
    drawnAt: admin.firestore.FieldValue.serverTimestamp(),
    platformCut,
    treasuryCut,
    rewardPaid: totalRewardPaid,
    reserve: sweep.totalPool - platformCut - treasuryCut - totalRewardPaid
  });
  
  // Treasury
  if (treasuryCut > 0) {
    const treasuryRef = db.collection('system').doc('treasury');
    batch.set(treasuryRef, {
      balance: admin.firestore.FieldValue.increment(treasuryCut)
    }, { merge: true });
  }
  
  await batch.commit();
  
  // Notifikasi ke pemenang
  for (const w of resultWinners) {
    const medal = w.rank === 1 ? '🥇' : w.rank === 2 ? '🥈' : w.rank === 3 ? '🥉' : '🎁';
    await createNotification(
      w.uid,
      `${medal} SELAMAT! Anda Menang!`,
      `Juara ${w.rank} — ${w.amount} RTP dari sweepstakes! 🎉`,
      'SWEEPSTAKES'
    );
  }

  // 8. Buat sweepstakes baru
  const newSweep = await getOrCreateActiveSweepstakes();
  
  return {
    drawn: true,
    sweepstakesId: sweep.id,
    totalWinners: resultWinners.length,
    totalRewardPaid,
    platformCut,
    treasuryCut,
    seed,
    newSweepstakesId: newSweep.id
  };
}

// ============================================================
// REFUND — Kembalikan RTP ke user kalau draw dibatalkan
// ============================================================
async function refundSweepstakes(sweepstakesId) {
  const ticketsSnap = await db.collection('tickets')
    .where('sweepstakesId', '==', sweepstakesId)
    .get();
  
  const userRefundMap = new Map();
  ticketsSnap.forEach(doc => {
    const d = doc.data();
    if (!d.isBonus) {  // Bonus tickets tidak di-refund
      userRefundMap.set(d.uid, (userRefundMap.get(d.uid) || 0) + SWEEPSTAKES_CONFIG.ticketPrice);
    }
  });
  
  const batch = db.batch();
  for (const [uid, amount] of userRefundMap) {
    batch.update(db.collection('users').doc(uid), {
      points: admin.firestore.FieldValue.increment(amount)
    });
  }
  
  batch.update(db.collection('sweeps_v2').doc(sweepstakesId), {
    status: 'refunded',
    refundedAt: admin.firestore.FieldValue.serverTimestamp()
  });
  
  await batch.commit();
  AppLogger.d(`[SWEEPSTAKES] Refunded ${userRefundMap.size} users`);
}

module.exports = router;
module.exports.drawSweepstakes = drawSweepstakes;
module.exports.SWEEPSTAKES_CONFIG = SWEEPSTAKES_CONFIG;
