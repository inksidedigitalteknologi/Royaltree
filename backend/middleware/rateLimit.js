/**
 * Rate Limiting Middleware
 * Royaltree — Express 5 compatible
 */

const rateLimit = require('express-rate-limit');

// Helper: custom handler — response JSON konsisten
const jsonHandler = (message) => (req, res) => {
    res.status(429).json({
        success: false,
        message,
        retryAfter: Math.ceil((req.rateLimit?.resetTime - Date.now()) / 1000) || 60
    });
};

// ============================================================
// GLOBAL — semua /api/* — 200 req/menit per IP
// ============================================================
const globalLimiter = rateLimit({
    windowMs: 60 * 1000,
    max: 200,
    standardHeaders: true,
    legacyHeaders: false,
    handler: jsonHandler('Terlalu banyak request. Coba lagi nanti.'),
    skip: (req) => {
        // Skip health check biar monitoring tidak kena limit
        return req.path === '/health' || req.path === '/api/v1/health';
    }
});

// ============================================================
// AUTH — login/register — 10 req/menit per IP (anti brute force)
// ============================================================
const authLimiter = rateLimit({
    windowMs: 60 * 1000,
    max: 10,
    standardHeaders: true,
    legacyHeaders: false,
    handler: jsonHandler('Terlalu banyak percobaan login. Tunggu 1 menit.')
});

// ============================================================
// SENSITIVE — withdraw/purchase/transfer — 5 req/menit
// ============================================================
const sensitiveLimiter = rateLimit({
    windowMs: 60 * 1000,
    max: 5,
    standardHeaders: true,
    legacyHeaders: false,
    handler: jsonHandler('Terlalu banyak request. Tunggu 1 menit.')
});

// ============================================================
// MINING — claim/share/rtp — 30 req/menit
// ============================================================
const miningLimiter = rateLimit({
    windowMs: 60 * 1000,
    max: 30,
    standardHeaders: true,
    legacyHeaders: false,
    handler: jsonHandler('Terlalu banyak request mining. Tunggu sebentar.')
});

// ============================================================
// ADS — watch reward — 20 req/menit (jaga-jaga spam)
// ============================================================
const adsLimiter = rateLimit({
    windowMs: 60 * 1000,
    max: 20,
    standardHeaders: true,
    legacyHeaders: false,
    handler: jsonHandler('Terlalu banyak request ads.')
});

// ============================================================
// CHAT — kirim pesan — 20 req/menit
// ============================================================
const chatLimiter = rateLimit({
    windowMs: 60 * 1000,
    max: 20,
    standardHeaders: true,
    legacyHeaders: false,
    handler: jsonHandler('Terlalu banyak pesan. Pelan-pelan ya.')
});

// ============================================================
// ADMIN — login admin — 5 req/menit (extra ketat)
// ============================================================
const adminLoginLimiter = rateLimit({
    windowMs: 60 * 1000,
    max: 5,
    standardHeaders: true,
    legacyHeaders: false,
    handler: jsonHandler('Terlalu banyak percobaan login admin.')
});

module.exports = {
    globalLimiter,
    authLimiter,
    sensitiveLimiter,
    miningLimiter,
    adsLimiter,
    chatLimiter,
    adminLoginLimiter
};
