// routes/board.js
// Monopoly Adventure — grid 50x50

const express = require('express');
const router = express.Router();
const admin = require('firebase-admin');
const fs = require('fs');
const path = require('path');
const { verifyFirebaseToken } = require('../middleware/firebaseAuth');
const { checkFraud } = require('../middleware/antiFraud');
const { validate } = require('../middleware/validate');
const Joi = require('joi');

const db = admin.firestore();
const AppLogger = require('../utils/AppLogger');

const BOARD_CONFIG_PATH = path.join(__dirname, '../config/board_config.json');
const COOLDOWN_MS = 24 * 60 * 60 * 1000;
const GRID_SIZE = 50; // 50x50

let BOARD_CONFIG = { grid: [], path: [], size: GRID_SIZE };
let PATH_POSITIONS = []; // array of {x, y, index}
let EFFECTS = {};        // index -> effect

function loadConfig() {
    try {
        if (fs.existsSync(BOARD_CONFIG_PATH)) {
            BOARD_CONFIG = JSON.parse(fs.readFileSync(BOARD_CONFIG_PATH, 'utf8'));
            PATH_POSITIONS = BOARD_CONFIG.path || [];
            EFFECTS = {};
            PATH_POSITIONS.forEach((p, i) => {
                EFFECTS[i] = {
                    type: p.type,
                    reward: p.reward,
                    label: p.label,
                    moveBack: p.moveBack,
                    resetToStart: p.resetToStart,
                    skipTurn: p.skipTurn
                };
            });
            AppLogger.d(`[BOARD] Loaded ${PATH_POSITIONS.length} path points (grid ${BOARD_CONFIG.size}x${BOARD_CONFIG.size})`);
        } else {
            console.warn('[BOARD] Config not found');
        }
    } catch (e) {
        console.error('[BOARD] Load error:', e.message);
    }
}

function reloadConfig() { loadConfig(); }
loadConfig();

const playSchema = Joi.object({});

router.post('/play', verifyFirebaseToken, checkFraud, validate(playSchema), async (req, res) => {
    try {
        const { uid } = req.firebaseUser;
        const pathSize = PATH_POSITIONS.length;
        if (pathSize === 0) {
            return res.status(503).json({ success: false, message: 'Board belum dikonfigurasi' });
        }

        const userRef = db.collection('users').doc(uid);
        const userDoc = await userRef.get();
        if (!userDoc.exists) return res.status(404).json({ success: false, message: 'User tidak ditemukan' });
        const userData = userDoc.data();

        const now = Date.now();
        const lastPlay = userData.lastBoardPlay || 0;
        const cooldownRemaining = (lastPlay + COOLDOWN_MS) - now;
        if (cooldownRemaining > 0) {
            const hoursLeft = Math.ceil(cooldownRemaining / (60 * 60 * 1000));
            return res.status(429).json({ success: false, message: `Cooldown. Coba lagi dalam ${hoursLeft} jam.`, cooldownRemainingMs: cooldownRemaining });
        }

        const dice = Math.floor(Math.random() * 6) + 1;
        const currentPos = userData.boardPosition || 0;
        let newPos = (currentPos + dice) % pathSize;
        let effect = EFFECTS[newPos] || { type: 'neutral', reward: 0, label: 'Netral' };

        let totalReward = effect.reward || 0;
        let skipTurn = false;
        let resetToStart = false;

        if (effect.moveBack) {
            newPos = (newPos - effect.moveBack + pathSize) % pathSize;
            const newEffect = EFFECTS[newPos] || { type: 'neutral', reward: 0, label: 'Netral' };
            totalReward += newEffect.reward || 0;
            effect = newEffect;
        }
        if (effect.resetToStart) { newPos = 0; resetToStart = true; }
        if (effect.skipTurn) skipTurn = true;

        const updates = {
            lastBoardPlay: now,
            boardPosition: newPos,
            boardLastRoll: dice,
            boardLastEffect: effect.label,
            boardLastReward: totalReward,
            boardTotalPlays: (userData.boardTotalPlays || 0) + 1
        };
        if (totalReward > 0) {
            updates.points = admin.firestore.FieldValue.increment(totalReward);
            updates.totalRTPEarned = admin.firestore.FieldValue.increment(totalReward);
        } else if (totalReward < 0) {
            updates.points = admin.firestore.FieldValue.increment(totalReward);
        }
        if (skipTurn) updates.boardSkipUntil = now + COOLDOWN_MS;

        await userRef.update(updates);

        await db.collection('board_logs').add({
            userId: uid, dice, fromPos: currentPos, toPos: newPos,
            effect: effect.label, reward: totalReward, skipTurn, resetToStart,
            timestamp: admin.firestore.FieldValue.serverTimestamp()
        });

        return res.json({
            success: true,
            data: {
                dice, fromPos: currentPos, toPos: newPos,
                effect: effect.label, effectType: effect.type,
                reward: totalReward, skipTurn, resetToStart,
                cooldownMs: COOLDOWN_MS, cooldownUntil: now + COOLDOWN_MS
            }
        });
    } catch (error) {
        console.error('[BOARD] Play error:', error);
        return res.status(500).json({ success: false, message: 'Terjadi kesalahan server.' });
    }
});

router.get('/status', verifyFirebaseToken, async (req, res) => {
    try {
        const { uid } = req.firebaseUser;
        const userDoc = await db.collection('users').doc(uid).get();
        if (!userDoc.exists) return res.status(404).json({ success: false, message: 'User tidak ditemukan' });
        const userData = userDoc.data();
        const now = Date.now();
        const lastPlay = userData.lastBoardPlay || 0;
        const cooldownRemaining = Math.max(0, (lastPlay + COOLDOWN_MS) - now);
        return res.json({
            success: true,
            data: {
                position: userData.boardPosition || 0,
                totalPlays: userData.boardTotalPlays || 0,
                lastRoll: userData.boardLastRoll || 0,
                lastEffect: userData.boardLastEffect || '',
                lastReward: userData.boardLastReward || 0,
                cooldownRemainingMs: cooldownRemaining,
                canPlay: cooldownRemaining === 0
            }
        });
    } catch (error) {
        return res.status(500).json({ success: false, message: 'Terjadi kesalahan server.' });
    }
});

// GET /config — board config (grid + path)
router.get('/config', verifyFirebaseToken, async (req, res) => {
    try {
        return res.json({
            success: true,
            data: {
                grid: BOARD_CONFIG.grid || [],
                path: PATH_POSITIONS,
                size: BOARD_CONFIG.size || GRID_SIZE
            }
        });
    } catch (error) {
        return res.status(500).json({ success: false, message: 'Terjadi kesalahan server.' });
    }
});

module.exports = router;
module.exports.reloadConfig = reloadConfig;
