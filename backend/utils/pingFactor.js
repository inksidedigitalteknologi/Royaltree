/**
 * utils/pingFactor.js
 * 
 * Delta ping (percentage point) berdasarkan ping RTT.
 * Delta negatif = penalti, 0 = netral.
 * 
 * Prinsip: ping BUKAN salah user — maks -2%.
 */

function calculatePingDelta(pingMs) {
    if (pingMs < 0) return 0;           // invalid
    if (pingMs < 50) return 0;           // WiFi bagus
    if (pingMs < 100) return 0;          // 4G bagus
    if (pingMs < 200) return -0.005;     // 4G normal
    if (pingMs < 500) return -0.010;     // 3G / 4G lambat
    if (pingMs < 1000) return -0.015;    // Edge
    return -0.020;                       // > 1000ms — batas maksimal
}

module.exports = { calculatePingDelta };
