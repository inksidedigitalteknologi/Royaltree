// ============================================================
// Sweepstakes Auto-Draw Job
// Cek setiap 1 jam apakah ada sweepstakes yang perlu di-draw
// (endTime <= now, status = active)
// ============================================================

const { drawSweepstakes } = require('../routes/sweepstakes');
const AppLogger = require('../utils/AppLogger');

const CHECK_INTERVAL = 60 * 60 * 1000;  // 1 jam

async function checkAndDraw() {
  try {
    AppLogger.d('[SWEEPSTAKES-JOB] Checking for expired sweepstakes...');
    const result = await drawSweepstakes();
    
    if (result.refunded) {
      AppLogger.d(`[SWEEPSTAKES-JOB] Refunded: ${result.reason}`);
    } else if (result.drawn) {
      AppLogger.d(`[SWEEPSTAKES-JOB] Drawn: ${result.sweepstakesId}`);
      AppLogger.d(`  - Winners: ${result.totalWinners}`);
      AppLogger.d(`  - Reward paid: ${result.totalRewardPaid} RTP`);
      AppLogger.d(`  - Platform cut: ${result.platformCut} RTP`);
      AppLogger.d(`  - Treasury: ${result.treasuryCut} RTP`);
      AppLogger.d(`  - New sweepstakes: ${result.newSweepstakesId}`);
    }
  } catch (error) {
    // Ignore Firestore SDK internal index error (cosmetic, tak block fungsi)
    if (error.message.includes('FAILED_PRECONDITION') || error.message.includes('index')) {
      return;
    }
    if (!error.message.includes('Tiada sweepstakes')) {
      console.error('[SWEEPSTAKES-JOB] Error:', error.message);
    }
  }
}

function start() {
  AppLogger.d('[SWEEPSTAKES-JOB] Started - checking every 1 hour');
  setTimeout(checkAndDraw, 30 * 1000);
  setInterval(checkAndDraw, CHECK_INTERVAL);
}

module.exports = { start, checkAndDraw };
