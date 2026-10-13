// services/anomaly.js
// Anomaly detection untuk user

const admin = require('firebase-admin');
const db = admin.firestore();

// In-memory cache
const userActivity = new Map();      // uid -> { shares, claims, blocks, rtpGained, lastReset, ips, devices }
const userFlags = new Map();         // uid -> { flags: [], score, lastUpdate }
const anomalies = [];                // Max 200 anomaly terakhir

const CONFIG = {
  // Threshold
  MAX_RTP_PER_HOUR: 10000,           // Max 10,000 RTP/jam
  MAX_BLOCKS_PER_HOUR: 100,          // Max 100 block/jam
  MAX_SHARES_PER_MIN: 1000,          // Max 1,000 share/menit
  MAX_CLAIMS_PER_DAY: 50,            // Max 50 claim/hari
  MAX_ADS_PER_DAY: 20,               // Max 20 ad/hari
  MAX_IPS_PER_DAY: 5,                // Max 5 IP/hari
  MAX_ACCOUNTS_PER_DEVICE: 3,        // Max 3 akun/device
  
  // Score threshold
  FLAG_THRESHOLD: 50,                // Score > 50 = flag
  BAN_THRESHOLD: 100,                // Score > 100 = auto-ban candidate
  
  // Retention
  ANOMALY_RETENTION: 200
};

/**
 * Get user activity (dengan reset window)
 */
function getUserActivity(uid) {
  const now = Date.now();
  let activity = userActivity.get(uid);
  
  if (!activity) {
    activity = {
      shares: 0,
      claims: 0,
      blocks: 0,
      rtpGained: 0,
      adsWatched: 0,
      ips: new Set(),
      devices: new Set(),
      hourWindow: now,
      dayWindow: now,
      minuteWindow: now
    };
    userActivity.set(uid, activity);
    return activity;
  }
  
  // Reset per menit
  if (now - activity.minuteWindow > 60000) {
    activity.shares = 0;
    activity.minuteWindow = now;
  }
  
  // Reset per jam
  if (now - activity.hourWindow > 3600000) {
    activity.rtpGained = 0;
    activity.blocks = 0;
    activity.hourWindow = now;
  }
  
  // Reset per hari
  if (now - activity.dayWindow > 86400000) {
    activity.claims = 0;
    activity.adsWatched = 0;
    activity.ips.clear();
    activity.dayWindow = now;
  }
  
  return activity;
}

/**
 * Track user activity
 */
function trackActivity(uid, type, value = 1, meta = {}) {
  const activity = getUserActivity(uid);
  
  switch (type) {
    case 'SHARE':
      activity.shares += value;
      if (activity.shares > CONFIG.MAX_SHARES_PER_MIN) {
        addAnomaly(uid, 'SHARE_SPAM', 'HIGH', 
          `User ${uid}: ${activity.shares} shares in 1 minute`,
          { shares: activity.shares });
      }
      break;
      
    case 'CLAIM':
      activity.claims += value;
      if (activity.claims > CONFIG.MAX_CLAIMS_PER_DAY) {
        addAnomaly(uid, 'CLAIM_SPAM', 'MEDIUM',
          `User ${uid}: ${activity.claims} claims today`,
          { claims: activity.claims });
      }
      break;
      
    case 'BLOCK':
      activity.blocks += value;
      if (activity.blocks > CONFIG.MAX_BLOCKS_PER_HOUR) {
        addAnomaly(uid, 'FAST_MINING', 'HIGH',
          `User ${uid}: ${activity.blocks} blocks in 1 hour`,
          { blocks: activity.blocks });
      }
      break;
      
    case 'RTP_GAIN':
      activity.rtpGained += value;
      if (activity.rtpGained > CONFIG.MAX_RTP_PER_HOUR) {
        addAnomaly(uid, 'RTP_SPIKE', 'HIGH',
          `User ${uid}: gained ${activity.rtpGained.toFixed(2)} RTP in 1 hour`,
          { rtpGained: activity.rtpGained });
      }
      break;
      
    case 'AD_WATCH':
      activity.adsWatched += value;
      if (activity.adsWatched > CONFIG.MAX_ADS_PER_DAY) {
        addAnomaly(uid, 'AD_SPAM', 'MEDIUM',
          `User ${uid}: ${activity.adsWatched} ads today`,
          { adsWatched: activity.adsWatched });
      }
      break;
      
    case 'IP':
      activity.ips.add(value);
      if (activity.ips.size > CONFIG.MAX_IPS_PER_DAY) {
        addAnomaly(uid, 'MULTIPLE_IPS', 'MEDIUM',
          `User ${uid}: ${activity.ips.size} IPs today`,
          { ipCount: activity.ips.size });
      }
      break;
      
    case 'DEVICE':
      activity.devices.add(value);
      break;
  }
}

/**
 * Tambah anomali
 */
function addAnomaly(uid, type, severity, message, data = {}) {
  const anomaly = {
    id: 'anom_' + Date.now() + '_' + Math.random().toString(36).substr(2, 6),
    uid,
    type,
    severity,
    message,
    data,
    timestamp: Date.now(),
    status: 'OPEN'   // OPEN, REVIEWED, BANNED, DISMISSED
  };
  
  anomalies.unshift(anomaly);
  if (anomalies.length > CONFIG.ANOMALY_RETENTION) anomalies.pop();
  
  // Update user flag score
  let flag = userFlags.get(uid) || { score: 0, flags: [], lastUpdate: Date.now() };
  flag.score += severity === 'HIGH' ? 25 : (severity === 'MEDIUM' ? 10 : 5);
  flag.flags.push(type);
  flag.lastUpdate = Date.now();
  userFlags.set(uid, flag);
  
  console.log(`[ANOMALY/${severity}] ${type}: ${message}`);
  
  // Simpan ke Firestore
  db.collection('user_anomalies').add({
    ...anomaly,
    createdAt: admin.firestore.FieldValue.serverTimestamp()
  }).catch(err => console.error('[ANOMALY] Save error:', err));
  
  return anomaly;
}

/**
 * Get semua anomali
 */
function getAnomalies(limit = 100, filter = {}) {
  let result = anomalies;
  
  if (filter.severity) {
    result = result.filter(a => a.severity === filter.severity);
  }
  if (filter.status) {
    result = result.filter(a => a.status === filter.status);
  }
  if (filter.uid) {
    result = result.filter(a => a.uid === filter.uid);
  }
  
  return result.slice(0, limit);
}

/**
 * Get flagged users
 */
function getFlaggedUsers() {
  const result = [];
  
  for (const [uid, flag] of userFlags.entries()) {
    if (flag.score >= CONFIG.FLAG_THRESHOLD) {
      result.push({
        uid,
        score: flag.score,
        flags: flag.flags,
        lastUpdate: flag.lastUpdate,
        autoBanCandidate: flag.score >= CONFIG.BAN_THRESHOLD
      });
    }
  }
  
  return result.sort((a, b) => b.score - a.score);
}

/**
 * Get user flag detail
 */
function getUserFlag(uid) {
  return userFlags.get(uid) || { score: 0, flags: [], lastUpdate: 0 };
}

/**
 * Update anomaly status
 */
function updateAnomalyStatus(id, status) {
  const anomaly = anomalies.find(a => a.id === id);
  if (anomaly) {
    anomaly.status = status;
    console.log(`[ANOMALY] ${id} → ${status}`);
  }
  return anomaly;
}

/**
 * Clear user flag (setelah ban/dismiss)
 */
function clearUserFlag(uid) {
  userFlags.delete(uid);
  userActivity.delete(uid);
}

/**
 * Get stats
 */
function getStats() {
  const stats = {
    totalAnomalies: anomalies.length,
    openAnomalies: anomalies.filter(a => a.status === 'OPEN').length,
    highSeverity: anomalies.filter(a => a.severity === 'HIGH').length,
    flaggedUsers: 0,
    autoBanCandidates: 0
  };
  
  for (const [uid, flag] of userFlags.entries()) {
    if (flag.score >= CONFIG.FLAG_THRESHOLD) stats.flaggedUsers++;
    if (flag.score >= CONFIG.BAN_THRESHOLD) stats.autoBanCandidates++;
  }
  
  return stats;
}

module.exports = {
  trackActivity,
  addAnomaly,
  getAnomalies,
  getFlaggedUsers,
  getUserFlag,
  updateAnomalyStatus,
  clearUserFlag,
  getStats,
  CONFIG
};
