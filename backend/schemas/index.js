/**
 * Joi Schemas untuk Royaltree endpoints
 */

const Joi = require('joi');

// ============================================================
// AUTH
// ============================================================
const registerSchema = Joi.object({
    referralCode: Joi.string().trim().max(20).allow('', null).optional(),
    deviceModel: Joi.string().trim().max(80).optional(),
    androidVersion: Joi.string().trim().max(20).optional(),
    appVersion: Joi.string().trim().max(20).optional(),
    language: Joi.string().valid('id', 'en').optional(),
    city: Joi.string().trim().max(80).optional()
});

const loginSchema = Joi.object({
    fcmToken: Joi.string().trim().max(500).optional(),
    deviceModel: Joi.string().trim().max(80).optional(),
    androidVersion: Joi.string().trim().max(20).optional(),
    appVersion: Joi.string().trim().max(20).optional()
});

const adminLoginSchema = Joi.object({
    password: Joi.string().min(8).max(200).required(),
    apiKey: Joi.string().min(16).max(200).required()
});

// ============================================================
// GAME — Thermal & Location
// ============================================================
const thermalSchema = Joi.object({
    phone_temp: Joi.number().min(-10).max(120).required(),
    battery_level: Joi.number().integer().min(0).max(100).required(),
    is_charging: Joi.boolean().required(),
    thermal_level: Joi.string().valid('NONE', 'LIGHT', 'MODERATE', 'SEVERE', 'CRITICAL', 'EMERGENCY').optional()
});

const locationSchema = Joi.object({
    latitude: Joi.number().min(-90).max(90).required(),
    longitude: Joi.number().min(-180).max(180).required(),
    region: Joi.string().trim().max(100).optional()
});

// ============================================================
// GAME — Mining
// ============================================================
const placeMinerSchema = Joi.object({
    minerId: Joi.string().trim().min(1).max(100).required(),
    slotIndex: Joi.number().integer().min(0).max(20).required()
});

const purchaseMinerSchema = Joi.object({
    tier: Joi.string().valid('COMMON', 'RARE', 'EPIC', 'LEGENDARY', 'MYTHIC').required()
});

const claimSchema = Joi.object({
    missionId: Joi.string().trim().max(100).optional()
}).unknown(false);

// ============================================================
// ADS
// ============================================================
const adRewardSchema = Joi.object({
    adUnit: Joi.string().trim().max(100).optional(),
    adType: Joi.string().valid('rewarded', 'interstitial').optional()
});

// ============================================================
// DAILY
// ============================================================
const checkInSchema = Joi.object({
    streak: Joi.number().integer().min(0).max(365).optional()
});

// ============================================================
// PURCHASE
// ============================================================
const purchaseSchema = Joi.object({
    amount: Joi.number().positive().max(1000000).required(),
    method: Joi.string().valid('google_play', 'bank', 'ewallet', 'crypto').required(),
    reference: Joi.string().trim().max(200).optional()
});

// ============================================================
// TRANSFER
// ============================================================
const transferSchema = Joi.object({
    toUid: Joi.string().trim().min(1).max(100).required(),
    amount: Joi.number().positive().max(1000000).required(),
    note: Joi.string().trim().max(200).optional()
});

// ============================================================
// WITHDRAWAL
// ============================================================
const withdrawSchema = Joi.object({
    amount: Joi.number().positive().max(10000000).required(),
    method: Joi.string().valid('bank', 'ewallet', 'crypto').required(),
    accountNumber: Joi.string().trim().min(4).max(50).required(),
    accountName: Joi.string().trim().min(2).max(100).required(),
    bankName: Joi.string().trim().max(50).optional(),
    walletType: Joi.string().trim().max(50).optional()
});

// ============================================================
// CHAT
// ============================================================
const chatSchema = Joi.object({
    message: Joi.string().trim().min(1).max(500).required()
});


// ============================================================
// MISSION / CAMPAIGN
// ============================================================
const claimMissionSchema = Joi.object({
    missionId: Joi.string().trim().min(1).max(100).required()
});

// ============================================================
// EXPORT
// ============================================================
module.exports = {
    registerSchema,
    loginSchema,
    adminLoginSchema,
    thermalSchema,
    locationSchema,
    placeMinerSchema,
    purchaseMinerSchema,
    claimSchema,
    adRewardSchema,
    checkInSchema,
    purchaseSchema,
    transferSchema,
    withdrawSchema,
    chatSchema,
    buyTicketSchema,
    claimMissionSchema
};
