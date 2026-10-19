// services/exchangeRate.js
// Hybrid: Yahoo Finance (primary) + open.er-api.com (fallback)
// Cache 1 jam

const admin = require('firebase-admin');
const { db } = require('../config/firebase');
const AppLogger = require('../utils/AppLogger');

const CACHE_DURATION_MS = 60 * 60 * 1000; // 1 jam

let cachedRate = null;
let cachedSource = null;
let lastFetch = 0;

// ═══════════════════════════════════════════════════════════
// SOURCE 1: Yahoo Finance (unofficial, real-time)
// ═══════════════════════════════════════════════════════════
async function fetchFromYahoo() {
    try {
        const url = 'https://query1.finance.yahoo.com/v8/finance/chart/USDIDR=X?interval=1d';
        const resp = await fetch(url, {
            headers: {
                'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36'
            }
        });
        
        if (!resp.ok) throw new Error('HTTP ' + resp.status);
        
        const data = await resp.json();
        const result = data?.chart?.result?.[0];
        const price = result?.meta?.regularMarketPrice;
        
        if (!price || price <= 0) throw new Error('Invalid price: ' + price);
        
        return { rate: parseFloat(price), source: 'yahoo_finance' };
    } catch (e) {
        console.error('[EXCHANGE] Yahoo error:', e.message);
        return null;
    }
}

// ═══════════════════════════════════════════════════════════
// SOURCE 2: open.er-api.com (fallback, daily update)
// ═══════════════════════════════════════════════════════════
async function fetchFromErApi() {
    try {
        const resp = await fetch('https://open.er-api.com/v6/latest/USD');
        const data = await resp.json();
        
        if (data.result !== 'success') throw new Error('API error: ' + (data.error || 'unknown'));
        
        const idr = data.rates?.IDR;
        if (!idr || idr <= 0) throw new Error('Invalid IDR: ' + idr);
        
        return { rate: parseFloat(idr), source: 'open.er-api.com' };
    } catch (e) {
        console.error('[EXCHANGE] er-api error:', e.message);
        return null;
    }
}

// ═══════════════════════════════════════════════════════════
// MAIN: Fetch dengan cache
// ═══════════════════════════════════════════════════════════
async function fetchExchangeRate(force = false) {
    const now = Date.now();
    
    // Return cache kalau masih fresh
    if (!force && cachedRate && (now - lastFetch < CACHE_DURATION_MS)) {
        return { rate: cachedRate, source: cachedSource, cached: true };
    }
    
    // Coba Yahoo dulu
    let result = await fetchFromYahoo();
    
    // Fallback ke er-api
    if (!result) {
        AppLogger.d('[EXCHANGE] Yahoo failed, fallback to er-api...');
        result = await fetchFromErApi();
    }
    
    // Kalau berhasil, update cache
    if (result) {
        cachedRate = result.rate;
        cachedSource = result.source;
        lastFetch = now;
        AppLogger.d(`[EXCHANGE] ✅ Rate: ${result.rate} IDR/USD (${result.source})`);
        return { ...result, cached: false };
    }
    
    // Kalau semua gagal, pakai cache lama
    if (cachedRate) {
        AppLogger.d('[EXCHANGE] ⚠️ All failed, using cache:', cachedRate);
        return { rate: cachedRate, source: cachedSource, cached: true };
    }
    
    // Tidak ada cache → return null
    AppLogger.d('[EXCHANGE] ❌ No rate available');
    return null;
}

// ═══════════════════════════════════════════════════════════
// SYNC KE FIRESTORE
// ═══════════════════════════════════════════════════════════
async function syncExchangeRateToFirestore() {
    try {
        const result = await fetchExchangeRate(true);
        if (!result) {
            AppLogger.d('[EXCHANGE] Sync skip — no rate');
            return;
        }
        
        const rounded = Math.round(result.rate);
        
        await db.collection('system').doc('firestore_pricing').update({
            usd_to_idr: rounded,
            updated_at: admin.firestore.FieldValue.serverTimestamp(),
            updated_by: 'auto_' + result.source,
            exchange_rate_source: result.source,
            exchange_rate_raw: result.rate
        });
        
        AppLogger.d(`[EXCHANGE] ✅ Synced: ${rounded} IDR/USD (${result.source})`);
    } catch (e) {
        console.error('[EXCHANGE] Sync error:', e.message);
    }
}

// ═══════════════════════════════════════════════════════════
// AUTO SYNC — 1 jam
// ═══════════════════════════════════════════════════════════
function startAutoSync() {
    // Sync saat startup (delay 5s biar Firebase ready)
    setTimeout(() => syncExchangeRateToFirestore(), 5000);
    
    // Sync tiap 1 jam
    setInterval(syncExchangeRateToFirestore, CACHE_DURATION_MS);
    
    AppLogger.d('[EXCHANGE] Auto-sync started (1 jam)');
}

module.exports = { 
    fetchExchangeRate, 
    syncExchangeRateToFirestore, 
    startAutoSync 
};
