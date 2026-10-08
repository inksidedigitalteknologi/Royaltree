const admin = require('firebase-admin');

if (!admin.apps.length) {
    admin.initializeApp({
        credential: admin.credential.applicationDefault()
    });
}
const db = admin.firestore();

async function audit() {
    console.log('🔍 Audit Firestore Royaltree\n');

    const collections = await db.listCollections();
    console.log(`📊 Total collections: ${collections.length}\n`);

    for (const col of collections) {
        const snapshot = await col.limit(1).get();
        let total = 0;
        try {
            const totalSnap = await col.count().get();
            total = totalSnap.data().count;
        } catch (e) {
            total = -1;
        }

        console.log(`📁 ${col.id}  (${total} docs)`);

        if (!snapshot.empty) {
            const sample = snapshot.docs[0].data();
            const fields = Object.keys(sample).slice(0, 10).join(', ');
            console.log(`   Field sample: ${fields}${Object.keys(sample).length > 10 ? '...' : ''}`);
            console.log(`   Doc ID sample: ${snapshot.docs[0].id}`);
        } else {
            console.log('   (kosong)');
        }
        console.log('');
    }

    process.exit(0);
}

audit().catch(err => {
    console.error('❌ Error:', err.message);
    console.error('\n💡 Debug:');
    console.error('   - Check .env ada GOOGLE_APPLICATION_CREDENTIALS');
    console.error('   - Cek fail: ls -la $GOOGLE_APPLICATION_CREDENTIALS');
    process.exit(1);
});
