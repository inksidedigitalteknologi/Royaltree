// Script: Buang miner dengan data undefined
const admin = require('firebase-admin');
require('dotenv').config();

if (!admin.apps.length) {
    admin.initializeApp({
        credential: admin.credential.applicationDefault()
    });
}

const db = admin.firestore();

async function cleanup() {
    console.log('🚀 Cleanup undefined miners\n');
    
    const uid = 'VTmp1Za0Awc8juAgLa2g3rJ3tS82';
    const userRef = db.collection('users').doc(uid);
    const userDoc = await userRef.get();
    const userData = userDoc.data();
    const miners = userData.minersOwned || {};
    
    const toDelete = [];
    
    for (const [id, m] of Object.entries(miners)) {
        // Kalau miner tak ada catalogId / powerGhs → undefined → buang
        if (!m || !m.catalogId || typeof m.powerGhs !== 'number') {
            toDelete.push(id);
            console.log(`❌ Buang: ${id} (catalogId: ${m?.catalogId || 'undefined'})`);
        }
    }
    
    if (toDelete.length === 0) {
        console.log('✅ Tak ada miner undefined');
        process.exit(0);
    }
    
    // Buang satu per satu
    const updates = {};
    for (const id of toDelete) {
        updates[`minersOwned.${id}`] = admin.firestore.FieldValue.delete();
    }
    
    await userRef.update(updates);
    
    console.log(`\n🎉 Selesai — buang ${toDelete.length} miner undefined`);
    process.exit(0);
}

cleanup().catch(err => {
    console.error('❌ Error:', err);
    process.exit(1);
});
