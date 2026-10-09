// Migration: minerTokens → points (RTP)
const admin = require('firebase-admin');
require('dotenv').config();

if (!admin.apps.length) {
    admin.initializeApp({
        credential: admin.credential.applicationDefault()
    });
}

const db = admin.firestore();

async function migrate() {
    console.log('🚀 Migration: minerTokens → points (RTP)\n');
    
    const usersSnap = await db.collection('users').get();
    let count = 0;
    let totalMigrated = 0;
    
    for (const doc of usersSnap.docs) {
        const data = doc.data();
        const tokens = data.minerTokens || 0;
        
        if (tokens > 0) {
            await doc.ref.update({
                points: admin.firestore.FieldValue.increment(tokens),
                minerTokens: admin.firestore.FieldValue.delete()
            });
            console.log(`✅ ${doc.id}: +${tokens} RTP (${data.email || 'N/A'})`);
            count++;
            totalMigrated += tokens;
        }
    }
    
    console.log(`\n🎉 Selesai: ${count} user, total ${totalMigrated} tokens → RTP`);
}

migrate().then(() => process.exit(0)).catch(err => {
    console.error('❌ Error:', err);
    process.exit(1);
});
