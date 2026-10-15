/**
 * Achievement System — Catalog + Logic
 * Royaltree
 */

const { db, admin } = require('../config/firebase');

// ============================================================
// CATALOG — 20 Achievement
// ============================================================
const ACHIEVEMENTS = [
    // MINING
    { id: 'mining_first_claim', category: 'MINING', tier: 'BRONZE', icon: '⛏️',
      name: 'First Claim', description: 'Klaim mining 1x',
      condition: { type: 'COUNT', field: 'miningClaimCount', target: 1 },
      reward: { rtp: 0, badge: 'badge_newbie_miner' } },
    { id: 'mining_claim_100', category: 'MINING', tier: 'SILVER', icon: '⛏️',
      name: 'Miner Rajin', description: 'Klaim mining 100x',
      condition: { type: 'COUNT', field: 'miningClaimCount', target: 100 },
      reward: { rtp: 0, badge: 'badge_dedicated_miner' } },
    { id: 'mining_claim_1000', category: 'MINING', tier: 'GOLD', icon: '⛏️',
      name: 'Miner Pro', description: 'Klaim mining 1.000x',
      condition: { type: 'COUNT', field: 'miningClaimCount', target: 1000 },
      reward: { rtp: 0.05, badge: 'badge_pro_miner' } },
    { id: 'mining_collector', category: 'MINING', tier: 'SILVER', icon: '📦',
      name: 'Kolektor', description: 'Beli 10 miner',
      condition: { type: 'COUNT', field: 'minersOwnedCount', target: 10 },
      reward: { rtp: 0, frame: 'frame_collector' } },
    { id: 'mining_rtp_1000', category: 'MINING', tier: 'DIAMOND', icon: '💎',
      name: 'RTP Jutawan', description: 'Total RTP 1.000',
      condition: { type: 'TOTAL', field: 'totalRTPEarned', target: 1000 },
      reward: { rtp: 0.1, badge: 'badge_diamond_miner' } },

    // STEP
    { id: 'step_10000', category: 'STEP', tier: 'BRONZE', icon: '🚶',
      name: 'Jalan Kaki', description: 'Jalan 10.000 langkah',
      condition: { type: 'TOTAL', field: 'totalSteps', target: 10000 },
      reward: { rtp: 0, badge: 'badge_walker' } },
    { id: 'step_100000', category: 'STEP', tier: 'GOLD', icon: '🚶',
      name: 'Marathon', description: 'Jalan 100.000 langkah',
      condition: { type: 'TOTAL', field: 'totalSteps', target: 100000 },
      reward: { rtp: 0.05, badge: 'badge_marathon' } },
    { id: 'step_streak_7', category: 'STEP', tier: 'SILVER', icon: '🔥',
      name: 'Streak 7 Hari', description: 'Check-in 7 hari berturut',
      condition: { type: 'STREAK', field: 'checkInStreak', target: 7 },
      reward: { rtp: 0, badge: 'badge_consistent' } },

    // REFERRAL
    { id: 'referral_1', category: 'REFERRAL', tier: 'BRONZE', icon: '👥',
      name: 'Ajak 1 Teman', description: '1 referral aktif',
      condition: { type: 'COUNT', field: 'referralCount', target: 1 },
      reward: { rtp: 0, badge: 'badge_recruiter' } },
    { id: 'referral_10', category: 'REFERRAL', tier: 'SILVER', icon: '👥',
      name: 'Ajak 10 Teman', description: '10 referral aktif',
      condition: { type: 'COUNT', field: 'referralCount', target: 10 },
      reward: { rtp: 0, badge: 'badge_influencer' } },
    { id: 'referral_100', category: 'REFERRAL', tier: 'LEGENDARY', icon: '👑',
      name: 'Ajak 100 Teman', description: '100 referral aktif',
      condition: { type: 'COUNT', field: 'referralCount', target: 100 },
      reward: { rtp: 0.1, badge: 'badge_legend' } },

    // MINI-GAME
    { id: 'game_score_1000', category: 'MINI_GAME', tier: 'BRONZE', icon: '🎮',
      name: 'Skor 1000', description: 'Coin Rush skor 1000',
      condition: { type: 'MAX', field: 'minigameHighScore', target: 1000 },
      reward: { rtp: 0, badge: 'badge_gamer' } },
    { id: 'game_score_5000', category: 'MINI_GAME', tier: 'GOLD', icon: '🎮',
      name: 'Skor 5000', description: 'Coin Rush skor 5000',
      condition: { type: 'MAX', field: 'minigameHighScore', target: 5000 },
      reward: { rtp: 0.05, badge: 'badge_pro_gamer' } },
    { id: 'game_play_50', category: 'MINI_GAME', tier: 'SILVER', icon: '🕹️',
      name: 'Gamer Sejati', description: 'Main mini-game 50x',
      condition: { type: 'COUNT', field: 'minigamePlayCount', target: 50 },
      reward: { rtp: 0, frame: 'frame_arcade' } },

    // WITHDRAWAL
    { id: 'withdraw_first', category: 'WITHDRAWAL', tier: 'BRONZE', icon: '💰',
      name: 'Withdraw Pertama', description: 'Withdraw pertama',
      condition: { type: 'COUNT', field: 'withdrawCount', target: 1 },
      reward: { rtp: 0, badge: 'badge_first_cash' } },
    { id: 'withdraw_1m', category: 'WITHDRAWAL', tier: 'GOLD', icon: '💰',
      name: 'Withdraw Rp 1 Juta', description: 'Total withdraw Rp 1 juta',
      condition: { type: 'TOTAL', field: 'totalWithdrawAmount', target: 1000000 },
      reward: { rtp: 0.05, badge: 'badge_big_withdrawal' } },

    // SOCIAL
    { id: 'chat_100', category: 'SOCIAL', tier: 'BRONZE', icon: '💬',
      name: 'Chat Aktif', description: 'Kirim 100 pesan',
      condition: { type: 'COUNT', field: 'chatMessageCount', target: 100 },
      reward: { rtp: 0, badge: 'badge_talkative' } },
    { id: 'chat_1000', category: 'SOCIAL', tier: 'SILVER', icon: '💬',
      name: 'Komentator', description: 'Kirim 1.000 pesan',
      condition: { type: 'COUNT', field: 'chatMessageCount', target: 1000 },
      reward: { rtp: 0, badge: 'badge_commentator' } },

    // STREAK
    { id: 'streak_30', category: 'STREAK', tier: 'GOLD', icon: '🔥',
      name: 'Streak 30 Hari', description: 'Check-in 30 hari',
      condition: { type: 'STREAK', field: 'checkInStreak', target: 30 },
      reward: { rtp: 0.05, badge: 'badge_devoted' } },
    { id: 'streak_100', category: 'STREAK', tier: 'LEGENDARY', icon: '👑',
      name: 'Streak 100 Hari', description: 'Check-in 100 hari',
      condition: { type: 'STREAK', field: 'checkInStreak', target: 100 },
      reward: { rtp: 0.1, frame: 'frame_legendary' } },
];

// ============================================================
// SEED — Insert catalog ke Firestore (sekali saja)
// ============================================================
async function seedAchievements() {
    const batch = db.batch();
    let count = 0;
    for (const ach of ACHIEVEMENTS) {
        const ref = db.collection('achievements').doc(ach.id);
        batch.set(ref, { ...ach, order: count, active: true }, { merge: true });
        count++;
    }
    await batch.commit();
    return count;
}

// ============================================================
// GET CATALOG
// ============================================================
async function getCatalog() {
    const snap = await db.collection('achievements')
        .where('active', '==', true)
        .get();
    return snap.docs
        .map(d => ({ id: d.id, ...d.data() }))
        .sort((a, b) => (a.order || 0) - (b.order || 0));
}

// ============================================================
// CHECK & UNLOCK — dipanggil dari event handler
// ============================================================
async function checkAchievements(uid) {
    try {
        const userDoc = await db.collection('users').doc(uid).get();
        if (!userDoc.exists) return [];

        const user = userDoc.data();
        const catalog = await getCatalog();

        const userAchSnap = await db.collection('user_achievements')
            .where('uid', '==', uid)
            .get();
        const userAchMap = {};
        userAchSnap.forEach(d => { userAchMap[d.data().achievementId] = d.data(); });

        const newlyUnlocked = [];

        for (const ach of catalog) {
            const existing = userAchMap[ach.id];
            if (existing?.unlocked) continue;

            const currentValue = getValueForCondition(user, ach.condition.field);
            const target = ach.condition.target;
            const isUnlocked = currentValue >= target;

            const ref = db.collection('user_achievements').doc(`${uid}_${ach.id}`);

            if (isUnlocked) {
                await ref.set({
                    uid,
                    achievementId: ach.id,
                    progress: currentValue,
                    target,
                    unlocked: true,
                    claimed: false,
                    unlockedAt: admin.firestore.FieldValue.serverTimestamp()
                }, { merge: true });
                newlyUnlocked.push(ach);
            } else {
                await ref.set({
                    uid,
                    achievementId: ach.id,
                    progress: currentValue,
                    target,
                    unlocked: false,
                    claimed: false
                }, { merge: true });
            }
        }

        return newlyUnlocked;
    } catch (e) {
        console.error('[ACHIEVEMENT] checkAchievements error:', e.message);
        return [];
    }
}

// ============================================================
// GET VALUE dari user doc
// ============================================================
function getValueForCondition(user, field) {
    // Map field ke sumber data
    const map = {
        miningClaimCount: user.miningClaimCount || 0,
        minersOwnedCount: user.minersOwned ? Object.keys(user.minersOwned).length : 0,
        totalRTPEarned: user.totalRTPEarned || 0,
        totalSteps: user.totalSteps || 0,
        checkInStreak: user.checkInStreak || 0,
        referralCount: user.referralCount || 0,
        minigameHighScore: user.minigameHighScore || 0,
        minigamePlayCount: user.minigamePlayCount || 0,
        withdrawCount: user.withdrawCount || 0,
        totalWithdrawAmount: user.totalWithdrawAmount || 0,
        chatMessageCount: user.chatMessageCount || 0,
    };
    return map[field] || 0;
}

// ============================================================
// CLAIM REWARD
// ============================================================
async function claimAchievement(uid, achievementId) {
    const docId = `${uid}_${achievementId}`;
    const ref = db.collection('user_achievements').doc(docId);
    const doc = await ref.get();
    if (!doc.exists) throw new Error('Achievement tidak ditemukan');

    const data = doc.data();
    if (!data.unlocked) throw new Error('Achievement belum unlocked');
    if (data.claimed) throw new Error('Reward sudah diklaim');

    const achDoc = await db.collection('achievements').doc(achievementId).get();
    if (!achDoc.exists) throw new Error('Achievement tidak valid');
    const ach = achDoc.data();

    const reward = ach.reward || {};

    // Beri reward RTP
    if (reward.rtp && reward.rtp > 0) {
        const userRef = db.collection('users').doc(uid);
        await userRef.update({
            points: admin.firestore.FieldValue.increment(reward.rtp),
            totalRTPEarned: admin.firestore.FieldValue.increment(reward.rtp)
        });
    }

    // Beri badge/frame (simpan di user.badges)
    if (reward.badge || reward.frame) {
        const userRef = db.collection('users').doc(uid);
        const updates = {};
        if (reward.badge) {
            updates.badges = admin.firestore.FieldValue.arrayUnion(reward.badge);
        }
        if (reward.frame) {
            updates.frames = admin.firestore.FieldValue.arrayUnion(reward.frame);
        }
        await userRef.update(updates);
    }

    // Mark claimed
    await ref.update({
        claimed: true,
        claimedAt: admin.firestore.FieldValue.serverTimestamp()
    });

    return { achievement: ach, reward };
}

module.exports = {
    ACHIEVEMENTS,
    seedAchievements,
    getCatalog,
    checkAchievements,
    claimAchievement
};
