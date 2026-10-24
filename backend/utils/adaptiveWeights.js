/**
 * utils/adaptiveWeights.js
 * 
 * Bobot adaptif berdasarkan konteks user:
 * - Device low-end → bobot device turun, efficiency naik
 * - Ping tinggi → bobot ping turun, thermal naik
 * - HP panas → bobot thermal naik
 * - Premium user → bobot efficiency naik
 * - Flagship → bobot device naik
 * 
 * Total bobot selalu = 1.0 (100%)
 */

/**
 * @param {Object} base - bobot default { efficiency, ping, device, thermal }
 * @param {Object} ctx - konteks user
 * @param {number} ctx.deviceScore - 0..100
 * @param {number} ctx.pingMs - RTT dalam ms
 * @param {number} ctx.tempC - suhu HP dalam °C
 * @param {string} ctx.userTier - FREE/STARTER/PREMIUM/VIP/ROYAL
 * @returns {Object} bobot yang sudah disesuaikan, total = 1.0
 */
function adaptWeights(base, ctx) {
    let w = { ...base };

    // Rule 1: Device low-end → bobot device turun, efficiency naik
    if (ctx.deviceScore < 40) {
        const diff = 0.10;
        w.device = Math.max(0.05, w.device - diff);
        w.efficiency += diff;
    }

    // Rule 2: Ping tinggi → bobot ping turun, thermal naik
    if (ctx.pingMs > 500) {
        const diff = Math.min(0.05, w.ping - 0.01); // minimal ping weight 1%
        if (diff > 0) {
            w.ping -= diff;
            w.thermal += diff;
        }
    }

    // Rule 3: HP panas → bobot thermal naik
    if (ctx.tempC > 45) {
        const diff = 0.10;
        w.thermal = Math.min(0.50, w.thermal + diff);
        w.efficiency = Math.max(0.20, w.efficiency - diff);
    }

    // Rule 4: Premium user → bobot efficiency naik
    if (ctx.userTier === 'VIP' || ctx.userTier === 'ROYAL') {
        const diff = 0.10;
        w.efficiency = Math.min(0.60, w.efficiency + diff);
        w.device = Math.max(0.10, w.device - diff);
    }

    // Rule 5: Flagship → bobot device naik
    if (ctx.deviceScore > 90) {
        const diff = 0.10;
        w.device = Math.min(0.40, w.device + diff);
        w.efficiency = Math.max(0.20, w.efficiency - diff);
    }

    // Normalize: pastikan total = 1.0
    const total = w.efficiency + w.ping + w.device + w.thermal;
    if (Math.abs(total - 1.0) > 0.001) {
        w.efficiency /= total;
        w.ping /= total;
        w.device /= total;
        w.thermal /= total;
    }

    return w;
}

module.exports = { adaptWeights };
