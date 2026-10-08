// Migration — pindah game_miners → users.minersOwned
// Idempotent + dry-run support
const admin = require('firebase-admin');

const DRY_RUN = process.argv.includes('--dry-run');

if (admin.apps.length === 0) {
    admin.initializeApp({
        credential: admin.credential.applicationDefault()
    });
}
const db = admin.firestore();

async function migrate() {
    console.log(`🚀 Migration game_miners → users.minersOwned`);
    console.log(`   Mode: ${DRY_RUN ? 'DRY-RUN (no write)' : 'LIVE (write)'}\n`);

    // 1. Baca semua game_miners
    const minersSnap = await db.collection('game_miners').get();
    console.log(`📊 Total game_miners: ${minersSnap.size}\n`);

    // Group by userId
    const byUser = {};
    minersSnap.forEach(doc => {
        const data = doc.data();
        if (!data.userId) return;
        if (!byUser[data.userId]) byUser[data.userId] = [];
        byUser[data.userId].push({ id: doc.id, ...data });
    });

    let migratedCount = 0;
    let skippedCount = 0;

    for (const [uid, miners] of Object.entries(byUser)) {
        console.log(`\n👤 User: ${uid} (${miners.length} miner)`);

        // Baca user doc
        const userRef = db.collection('users').doc(uid);
        const userDoc = await userRef.get();
        if (!userDoc.exists) {
            console.log(`   ⚠️ User doc tak jumpa — skip`);
            skippedCount += miners.length;
            continue;
        }

        const userData = userDoc.data();
        const existingOwned = userData.minersOwned || {};

        // Prepare update
        const updates = {};
        let newCount = 0;

        for (const miner of miners) {
            const instanceId = miner.instanceId || miner.id;
            // Skip kalau dah ada di users.minersOwned
            if (existingOwned[instanceId]) {
                console.log(`   ⏭️ Skip ${instanceId} (dah ada)`);
                skippedCount++;
                continue;
            }

            updates[`minersOwned.${instanceId}`] = {
                catalogId: miner.catalogId || miner.id,
                instanceId: instanceId,
                name: miner.name || 'Unknown',
                tier: miner.tier || 'COMMON',
                iconEmoji: miner.iconEmoji || '⛏️',
                tokenCost: miner.tokenCost || miner.pricePoints || 0,
                powerGhs: miner.powerGhs || 0,
                pointsPerMinute: miner.pointsPerMinute || 0,
                bonusPermanentPct: miner.bonusPermanentPct || 0,
                isOwned: miner.isOwned || true,
                isPlacedInRoom: miner.isPlacedInRoom || false,
                placedSlotIndex: miner.placedSlotIndex ?? -1,
                purchasedAt: miner.purchasedAt || new Date().toISOString(),
                migratedAt: new Date().toISOString()
            };
            newCount++;
        }

        if (newCount === 0) {
            console.log(`   ✅ Semua miner dah migrated`);
            continue;
        }

        console.log(`   📝 Akan migrate ${newCount} miner`);

        if (!DRY_RUN) {
            await userRef.update(updates);
            console.log(`   ✅ Migrated ${newCount} miner`);
        } else {
            console.log(`   🚫 DRY-RUN — skip write`);
        }

        migratedCount += newCount;
    }

    console.log(`\n📊 RINGKASAN:`);
    console.log(`   Migrated: ${migratedCount}`);
    console.log(`   Skipped: ${skippedCount}`);
    console.log(`   Total: ${migratedCount + skippedCount}`);
    console.log(`   Mode: ${DRY_RUN ? 'DRY-RUN' : 'LIVE'}\n`);
    console.log(`💡 ${DRY_RUN ? 'Run tanpa --dry-run untuk apply' : 'Migration selesai!'}`);

    process.exit(0);
}

migrate().catch(err => {
    console.error('❌ Migration fail:', err);
    process.exit(1);
});
