// middleware/usageTracker.js
// Track Firestore reads/writes/deletes per endpoint
// Flush ke Firestore setiap 1 menit (hemat writes)

const admin = require('firebase-admin');
const { db } = require('../config/firebase');
const AppLogger = require('../utils/AppLogger');

// ============ BUFFER DI MEMORY ============
const buffer = {
    reads: 0,
    writes: 0,
    deletes: 0,
    endpoints: {},
    startedAt: Date.now()
};

// ============ SANITIZE ENDPOINT ============
// Firestore tidak support '.' dan '/' di field name
function sanitizeEndpoint(ep) {
    return (ep || 'unknown')
        .replace(/\./g, '_')
        .replace(/\//g, '|')      // ganti slash dengan pipe
        .replace(/:/g, ';')       // ganti colon
        .replace(/\s+/g, ' ')
        .substring(0, 100);       // max 100 char
}

// ============ TRACK ============
function trackUsage(endpoint, op, count = 1) {
    const safeEndpoint = sanitizeEndpoint(endpoint);
    
    buffer[op] = (buffer[op] || 0) + count;
    
    if (!buffer.endpoints[safeEndpoint]) {
        buffer.endpoints[safeEndpoint] = { reads: 0, writes: 0, deletes: 0 };
    }
    buffer.endpoints[safeEndpoint][op] = (buffer.endpoints[safeEndpoint][op] || 0) + count;
}

// ============ MIDDLEWARE ============
function usageTracker(req, res, next) {
    // Prioritas: route path > full path > unknown
    // Simpan sebagai relative path (tanpa /api/v1)
    let endpoint = req.route?.path || req.path || 'unknown';
    
    // Simplify: ambil path setelah mount point
    // e.g. /api/v1/admin/firestore-usage → /admin/firestore-usage
    if (endpoint.startsWith('/api/v1')) {
        endpoint = endpoint.replace('/api/v1', '');
    }
    
    const originalJson = res.json.bind(res);

    res.json = (data) => {
        // Kalibrasi: 1 API ≈ 3-9 Firestore reads (base 3x)
        const BASE_READS_MULTIPLIER = 3;
        
        if (req.method === 'GET') {
            const size = data ? JSON.stringify(data).length : 0;
            const contentReads = Math.max(1, Math.ceil(size / 5000));
            const estReads = BASE_READS_MULTIPLIER + contentReads;
            trackUsage(endpoint, 'reads', estReads);
        } else if (['POST', 'PUT', 'PATCH'].includes(req.method)) {
            // Writes: 1 API ≈ 2 writes
            trackUsage(endpoint, 'writes', 2);
        } else if (req.method === 'DELETE') {
            trackUsage(endpoint, 'deletes', 1);
        }
        return originalJson(data);
    };

    next();
}

// ============ FLUSH KE FIRESTORE (tiap 1 menit) ============
setInterval(async () => {
    const total = buffer.reads + buffer.writes + buffer.deletes;
    if (total === 0) return;

    const today = new Date().toISOString().split('T')[0];
    const ref = db.collection('firestore_usage').doc(today);

    try {
        // Build nested object (bukan dot notation!)
        const endpointsUpdate = {};
        for (const [ep, counts] of Object.entries(buffer.endpoints)) {
            endpointsUpdate[ep] = {
                reads: admin.firestore.FieldValue.increment(counts.reads || 0),
                writes: admin.firestore.FieldValue.increment(counts.writes || 0),
                deletes: admin.firestore.FieldValue.increment(counts.deletes || 0)
            };
        }

        // Merge update
        const updateData = {
            reads: admin.firestore.FieldValue.increment(buffer.reads),
            writes: admin.firestore.FieldValue.increment(buffer.writes),
            deletes: admin.firestore.FieldValue.increment(buffer.deletes),
            endpoints: endpointsUpdate,   // ← Nested object, bukan dot notation
            lastUpdated: admin.firestore.FieldValue.serverTimestamp()
        };

        await ref.set(updateData, { merge: true });

        AppLogger.d(`[USAGE] Flushed R=${buffer.reads} W=${buffer.writes} D=${buffer.deletes} | endpoints: ${Object.keys(buffer.endpoints).length}`);

        // Reset buffer
        buffer.reads = 0;
        buffer.writes = 0;
        buffer.deletes = 0;
        buffer.endpoints = {};
    } catch (e) {
        console.error('[USAGE] Flush error:', e.message);
    }
}, 60 * 1000);

module.exports = { usageTracker, trackUsage, buffer };
