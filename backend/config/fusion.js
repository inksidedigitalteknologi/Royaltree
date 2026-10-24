/**
 * config/fusion.js
 * 
 * Konfigurasi biaya fusion (merge) miner.
 * Rate bisa diubah via admin panel → PUT /api/v1/admin/fusion-config
 * Disimpan di Firestore: system/fusion_config
 * 
 * Format:
 * {
 *   rates: {
 *     COMMON: 100,      // 5 COMMON → 1 RARE, biaya 100 RTP
 *     RARE: 500,        // 5 RARE → 1 EPIC, biaya 500 RTP
 *     EPIC: 2000,       // 5 EPIC → 1 LEGENDARY, biaya 2000 RTP
 *     LEGENDARY: 10000  // 5 LEGENDARY → 1 MYTHIC, biaya 10000 RTP
 *   },
 *   updatedAt: Date,
 *   updatedBy: string
 * }
 */

const admin = require('firebase-admin');

const DEFAULT_RATES = {
    COMMON: 100,
    RARE: 500,
    EPIC: 2000,
    LEGENDARY: 10000,
};

// Cache di memori, refresh tiap 5 menit
let _cache = null;
let _cacheTime = 0;
const CACHE_TTL_MS = 5 * 60 * 1000;

/**
 * Ambil config fusion dari Firestore.
 * Kalau belum ada, kembalikan default.
 */
async function getFusionConfig() {
    const now = Date.now();
    if (_cache && (now - _cacheTime) < CACHE_TTL_MS) {
        return _cache;
    }
    
    try {
        const db = admin.firestore();
        const doc = await db.collection('system').doc('fusion_config').get();
        
        if (doc.exists) {
            const data = doc.data();
            _cache = {
                rates: data.rates || DEFAULT_RATES,
                updatedAt: data.updatedAt,
                updatedBy: data.updatedBy,
            };
        } else {
            _cache = { rates: DEFAULT_RATES };
        }
    } catch (err) {
        console.warn('[FUSION] Gagal baca config, pakai default:', err.message);
        _cache = { rates: DEFAULT_RATES };
    }
    
    _cacheTime = now;
    return _cache;
}

/**
 * Invalidate cache — dipanggil setelah admin update config.
 */
function invalidateCache() {
    _cache = null;
    _cacheTime = 0;
}

/**
 * Set config fusion ke Firestore (dipakai admin panel).
 */
async function setFusionConfig(rates, updatedBy) {
    const db = admin.firestore();
    await db.collection('system').doc('fusion_config').set({
        rates,
        updatedAt: admin.firestore.FieldValue.serverTimestamp(),
        updatedBy: updatedBy || 'system',
    }, { merge: true });
    invalidateCache();
}

module.exports = {
    getFusionConfig,
    setFusionConfig,
    invalidateCache,
    DEFAULT_RATES,
};
