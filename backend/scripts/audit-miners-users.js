const admin = require('firebase-admin');

if (admin.apps.length === 0) {
    admin.initializeApp({
        credential: admin.credential.applicationDefault()
    });
}

const db = admin.firestore();

async function audit() {
    // === USERS ===
    const usersSnap = await db.collection('users').get();
    console.log(`=== USERS (${usersSnap.size}) ===\n`);

    usersSnap.forEach(doc => {
        const d = doc.data();
        console.log(`UID: ${doc.id}`);
        console.log(`  email: ${d.email || '-'}`);
        console.log(`  name: ${d.name || '-'}`);
        console.log(`  points: ${d.points || 0}`);
        console.log(`  minersOwned: ${JSON.stringify(d.minersOwned || {})}`);
        console.log('');
    });

    // === GAME_MINERS ===
    const minersSnap = await db.collection('game_miners').get();
    console.log(`=== GAME_MINERS (${minersSnap.size}) ===\n`);

    minersSnap.forEach(doc => {
        const d = doc.data();
        console.log(`Doc ID: ${doc.id}`);
        console.log(`  userId: ${d.userId}`);
        console.log(`  name: ${d.name}`);
        console.log(`  tier: ${d.tier}`);
        console.log(`  isOwned: ${d.isOwned}`);
        console.log(`  isPlacedInRoom: ${d.isPlacedInRoom}`);
        console.log(`  placedSlotIndex: ${d.placedSlotIndex}`);
        console.log(`  powerGhs: ${d.powerGhs}`);
        console.log('');
    });

    process.exit(0);
}

audit().catch(err => {
    console.error('Error:', err.message);
    process.exit(1);
});
