/**
 * config/miningWeights.js
 * 
 * Konfigurasi bobot adaptif untuk perhitungan mining rate.
 * Disimpan di Firestore: system/mining_weights
 * Bisa diubah admin via: PUT /api/v1/admin/mining-weights
 * 
 * Formula:
 *   finalRate = baseRate × (1 + Σ(wᵢ × Δᵢ)) × anomalyFactor
 *   dengan floor 0.90 (90%)
 */

const admin = require('firebase-admin');

const DEFAULT_WEIGHTS = {
    efficiency: 0.40,
    ping: 0.10,
    device: 0.20,
    thermal: 0.30,
};

const DEFAULT_FLOOR = 0.90; // 90% minimum

// Cache 5 menit
let _cache = null;
let _cacheTime = 0;
const CACHE_TTL_MS = 5 * 60 * 1000;

async function getMiningWeights() {
    const now = Date.now();
    if (_cache && (now - _cacheTime) < CACHE_TTL_MS) {
        return _cache;
    }
    
    try {
        const db = admin.firestore();
        const doc = await db.collection('system').doc('mining_weights').get();
        
        if (doc.exists) {
            const data = doc.data();
            _cache = {
                weights: data.weights || DEFAULT_WEIGHTS,
                floor: data.floor || DEFAULT_FLOOR,
                updatedAt: data.updatedAt,
                updatedBy: data.updatedBy,
            };
        } else {
            _cache = { weights: DEFAULT_WEIGHTS, floor: DEFAULT_FLOOR };
        }
    } catch (err) {
        console.warn('[MINING-WEIGHTS] Fallback default:', err.message);
        _cache = { weights: DEFAULT_WEIGHTS, floor: DEFAULT_FLOOR };
    }
    
    _cacheTime = now;
    return _cache;
}

function invalidateCache() {
    _cache = null;
    _cacheTime = 0;
}

async function setMiningWeights(weights, floor, updatedBy) {
    const db = admin.firestore();
    await db.collection('system').doc('mining_weights').set({
        weights,
        floor: floor || DEFAULT_FLOOR,
        updatedAt: admin.firestore.FieldValue.serverTimestamp(),
        updatedBy: updatedBy || 'system',
    }, { merge: true });
    invalidateCache();
}

module.exports = {
    getMiningWeights,
    setMiningWeights,
    invalidateCache,
    DEFAULT_WEIGHTS,
    DEFAULT_FLOOR,
};
