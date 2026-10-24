/**
 * utils/thermalFactor.js
 * 
 * Delta thermal (percentage point) berdasarkan suhu & baterai.
 * Delta negatif = penalti.
 * 
 * Prinsip: thermal maks -10%, kecuali pause (bukan mining).
 */

function calculateThermalDelta(tempC, batteryLevel, isCharging) {
    // Baterai kritis & tidak charging → -10%
    if (batteryLevel < 20 && !isCharging) {
        return -0.100;
    }
    
    // Suhu tinggi → penalti bertahap
    if (tempC < 35) return 0;
    if (tempC < 42) return -0.025;
    if (tempC < 48) return -0.050;
    if (tempC < 55) return -0.075;
    
    // > 55°C → pause (bukan delta)
    return null;
}

module.exports = { calculateThermalDelta };
