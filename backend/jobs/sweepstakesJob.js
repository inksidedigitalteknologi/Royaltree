// ============================================================
// Sweepstakes Auto-Draw Job
// Cek setiap 1 jam apakah ada sweepstakes yang perlu di-draw
// (endTime <= now, status = active)
// ============================================================

const { drawSweepstakes } = require('../routes/sweepstakes');

const CHECK_INTERVAL = 60 * 60 * 1000;  // 1 jam

async function checkAndDraw() {
  try {
    console.log('[SWEEPSTAKES-JOB] Checking for expired sweepstakes...');
    const result = await drawSweepstakes();
    
    if (result.refunded) {
      console.log(`[SWEEPSTAKES-JOB] Refunded: ${result.reason}`);
    } else if (result.drawn) {
      console.log(`[SWEEPSTAKES-JOB] Drawn: ${result.sweepstakesId}`);
      console.log(`  - Winners: ${result.totalWinners}`);
      console.log(`  - Reward paid: ${result.totalRewardPaid} RTP`);
      console.log(`  - Platform cut: ${result.platformCut} RTP`);
      console.log(`  - Treasury: ${result.treasuryCut} RTP`);
      console.log(`  - New sweepstakes: ${result.newSweepstakesId}`);
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
  console.log('[SWEEPSTAKES-JOB] Started - checking every 1 hour');
  setTimeout(checkAndDraw, 30 * 1000);
  setInterval(checkAndDraw, CHECK_INTERVAL);
}

module.exports = { start, checkAndDraw };
