/**
 * utils/efficiency.js
 * 
 * Fungsi deterministik untuk menghitung efisiensi hash power miner.
 * Efisiensi berkisar 85% - 105% (0.85 - 1.05).
 * 
 * Deterministik berdasarkan:
 * - minerInstanceId (unique per miner)
 * - menit saat ini (timeMs / 60000)
 * 
 * Server dan client bisa hitung nilai yang SAMA tanpa komunikasi.
 * Anti-cheat friendly: server tidak perlu percaya client.
 */

/**
 * Hash string ke integer 32-bit — FNV-1a (lebih kuat dari simple hash).
 */
function fnv1a(str) {
    let hash = 0x811c9dc5; // FNV offset basis
    for (let i = 0; i < str.length; i++) {
        hash ^= str.charCodeAt(i);
        // FNV prime: 16777619, pakai Math.imul untuk 32-bit overflow
        hash = Math.imul(hash, 0x01000193);
    }
    return hash >>> 0; // unsigned 32-bit
}

/**
 * Hash string ke integer, dengan salt untuk menghindari collision.
 */
function hashCode(str, salt) {
    return fnv1a(str + ':' + salt);
}

/**
 * Hitung efficiency di menit tertentu (tanpa smoothing).
 * @param {string} minerInstanceId
 * @param {number} minuteBucket - Math.floor(timeMs / 60000)
 * @returns {number} 0.85 .. 1.05
 */
function computeEfficiencyAtBucket(minerInstanceId, minuteBucket) {
    // Pakai 2 hash dengan salt berbeda untuk variasi lebih
    const h1 = hashCode(minerInstanceId, minuteBucket);
    const h2 = hashCode(minerInstanceId, minuteBucket * 31 + 7);
    
    // Mix kedua hash untuk hasil lebih unpredictable
    const mixed = (h1 ^ h2) >>> 0;
    
    // Normalize 0..1
    const normalized = (mixed % 1000000) / 1000000; // 6 digit presisi
    
    // 0.85 .. 1.10
    return 0.85 + normalized * 0.20;
}

/**
 * Hitung efficiency dengan smoothing (interpolasi antar menit).
 * Supaya transisi halus, tidak jump.
 * @param {string} minerInstanceId
 * @param {number} timeMs - unix ms
 * @returns {number} 0.85 .. 1.05
 */
function computeEfficiency(minerInstanceId, timeMs) {
    const minuteBucket = Math.floor(timeMs / 60000);
    const minuteProgress = (timeMs % 60000) / 60000; // 0..1
    
    const effCurrent = computeEfficiencyAtBucket(minerInstanceId, minuteBucket);
    const effNext = computeEfficiencyAtBucket(minerInstanceId, minuteBucket + 1);
    
    // Smoothstep
    const t = minuteProgress * minuteProgress * (3 - 2 * minuteProgress);
    
    return effCurrent * (1 - t) + effNext * t;
}

/**
 * Hitung currentPowerHs dari basePowerHs × efficiency.
 */
function computeCurrentPowerHs(basePowerHs, efficiency) {
    return Math.round(basePowerHs * efficiency);
}

/**
 * Hitung pointsPerMinute dari currentPowerHs.
 * Rate: 1 H/s = 1e-8 RTP/menit.
 */
function computePointsPerMinute(currentPowerHs) {
    return +(currentPowerHs * 1e-8).toFixed(8);
}

/**
 * Hitung pointsPerDay dari currentPowerHs.
 */
function computePointsPerDay(currentPowerHs) {
    return +(currentPowerHs * 1e-8 * 1440).toFixed(4);
}

module.exports = {
    computeEfficiency,
    computeEfficiencyAtBucket,
    computeCurrentPowerHs,
    computePointsPerMinute,
    computePointsPerDay,
};
