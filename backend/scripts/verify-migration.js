const admin = require('firebase-admin');
if (admin.apps.length === 0) {
    admin.initializeApp({
        credential: admin.credential.applicationDefault()
    });
}
const db = admin.firestore();

async function verify() {
    const usersSnap = await db.collection('users').get();

    for (const doc of usersSnap.docs) {
        const d = doc.data();
        const owned = d.minersOwned || {};
        const count = Object.keys(owned).length;

        console.log(`\n👤 User: ${doc.id}`);
        console.log(`   minersOwned: ${count} miner`);
        console.log(`   minerTokens: ${d.minerTokens || 0}`);

        if (count > 0) {
            Object.entries(owned).forEach(([id, m]) => {
                console.log(`   ├─ ${id}`);
                console.log(`   │  name: ${m.name} | tier: ${m.tier} | tokenCost: ${m.tokenCost} | slot: ${m.placedSlotIndex}`);
            });
        }
    }
    process.exit(0);
}

verify().catch(err => { console.error(err); process.exit(1); });
