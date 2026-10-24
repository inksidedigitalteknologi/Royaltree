/**
 * utils/deviceFactor.js
 * 
 * Delta device (percentage point) berdasarkan device score (0-100).
 * 
 * Score heuristic dari:
 * - RAM size
 * - CPU cores
 * - Build.MODEL (flagship detection)
 * - Android version
 */

function calculateDeviceDelta(deviceScore) {
    if (deviceScore >= 95) return +0.030;   // Flagship
    if (deviceScore >= 80) return +0.015;   // High-end
    if (deviceScore >= 60) return 0;        // Mid-range
    if (deviceScore >= 40) return -0.025;   // Low-end
    return -0.050;                          // Very low — batas maksimal
}

/**
 * Heuristic device score dari fingerprint.
 * @param {Object} fp - device fingerprint
 * @param {number} fp.ramGb
 * @param {number} fp.cpuCores
 * @param {string} fp.model
 * @param {string} fp.brand
 * @param {number} fp.androidSdk
 * @returns {number} 0..100
 */
function computeDeviceScore(fp) {
    let score = 50; // baseline

    // RAM
    if (fp.ramGb >= 12) score += 20;
    else if (fp.ramGb >= 8) score += 15;
    else if (fp.ramGb >= 6) score += 5;
    else if (fp.ramGb >= 4) score -= 5;
    else score -= 20;

    // CPU cores
    if (fp.cpuCores >= 8) score += 15;
    else if (fp.cpuCores >= 6) score += 5;
    else if (fp.cpuCores >= 4) score -= 5;
    else score -= 15;

    // Flagship detection (brand + model)
    const flagshipKeywords = ['pro', 'ultra', 'max', 'plus', 'fold'];
    const lowEndKeywords = ['go', 'lite', 'mini', 'e'];
    const modelLower = (fp.model || '').toLowerCase();
    if (flagshipKeywords.some(k => modelLower.includes(k))) score += 10;
    if (lowEndKeywords.some(k => modelLower.includes(k))) score -= 10;

    // Android SDK
    if (fp.androidSdk >= 34) score += 5; // Android 14+
    else if (fp.androidSdk >= 30) score += 0; // Android 11-13
    else score -= 5;

    return Math.max(0, Math.min(100, score));
}

module.exports = { calculateDeviceDelta, computeDeviceScore };
