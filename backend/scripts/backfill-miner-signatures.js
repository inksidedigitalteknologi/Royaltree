// scripts/backfill-miner-signatures.js
// Generate signature untuk miner existing yang belum punya signature

const { admin, db } = require('../config/firebase');
const crypto = require('crypto');

function getMinerSigningSecret() {
    return process.env.MINER_SIGNING_SECRET || 'DEFAULT_INSECURE_SECRET_CHANGE_ME';
}

function generateMinerSignature(instanceId, catalogId, uid, purchasedAt, powerGhs, pointsPerMinute) {
    const data = `${instanceId}|${catalogId}|${uid}|${purchasedAt}|${powerGhs}|${pointsPerMinute}`;
    return crypto.createHmac('sha256', getMinerSigningSecret()).update(data).digest('hex');
}

async function backfill() {
    console.log('🚀 Backfill miner signatures...\n');

    const usersSnapshot = await db.collection('users').get();
    let totalUsers = 0;
    let totalMiners = 0;
    let updatedMiners = 0;
    let skippedMiners = 0;

    for (const userDoc of usersSnapshot.docs) {
        const uid = userDoc.id;
        const userData = userDoc.data();
        const minersOwned = userData.minersOwned || {};
        
        const minerIds = Object.keys(minersOwned);
        if (minerIds.length === 0) continue;

        totalUsers++;
        const updates = {};

        for (const minerId of minerIds) {
            totalMiners++;
            const miner = minersOwned[minerId];

            if (miner.signature) {
                skippedMiners++;
                continue;
            }

            const signature = generateMinerSignature(
                miner.instanceId || minerId,
                miner.catalogId || '',
                uid,
                miner.purchasedAt || Date.now(),
                miner.powerGhs || 0,
                miner.pointsPerMinute || 0
            );

            updates[`minersOwned.${minerId}.signature`] = signature;
            updatedMiners++;
        }

        if (Object.keys(updates).length > 0) {
            await userDoc.ref.update(updates);
            console.log(`  ✅ ${uid}: ${Object.keys(updates).length} miner di-sign`);
        }
    }

    console.log('\n📊 Summary:');
    console.log(`  Total users dengan miner: ${totalUsers}`);
    console.log(`  Total miners: ${totalMiners}`);
    console.log(`  Updated: ${updatedMiners}`);
    console.log(`  Skipped (sudah punya sig): ${skippedMiners}`);
    console.log('\n✅ Backfill selesai');
}

backfill()
    .then(() => process.exit(0))
    .catch(err => {
        console.error('❌ Error:', err);
        process.exit(1);
    });
