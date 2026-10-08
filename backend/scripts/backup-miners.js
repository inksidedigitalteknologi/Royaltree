const admin = require('firebase-admin');
const fs = require('fs');
const path = require('path');

if (admin.apps.length === 0) {
    admin.initializeApp({
        credential: admin.credential.applicationDefault()
    });
}
const db = admin.firestore();

async function backup() {
    console.log('🚀 Backup game_miners + users\n');

    const minersSnap = await db.collection('game_miners').get();
    const usersSnap = await db.collection('users').get();

    const backupDir = path.join(__dirname, '../backups');
    if (!fs.existsSync(backupDir)) fs.mkdirSync(backupDir, { recursive: true });

    const timestamp = new Date().toISOString().replace(/[:.]/g, '-');

    // Backup miners
    const miners = [];
    minersSnap.forEach(doc => miners.push({ id: doc.id, ...doc.data() }));
    fs.writeFileSync(
        path.join(backupDir, `miners-${timestamp}.json`),
        JSON.stringify(miners, null, 2)
    );

    // Backup users
    const users = [];
    usersSnap.forEach(doc => users.push({ id: doc.id, ...doc.data() }));
    fs.writeFileSync(
        path.join(backupDir, `users-${timestamp}.json`),
        JSON.stringify(users, null, 2)
    );

    console.log(`✅ Backup selesai:`);
    console.log(`   Miners: ${miners.length} docs → backups/miners-${timestamp}.json`);
    console.log(`   Users:  ${users.length} docs  → backups/users-${timestamp}.json`);
    console.log(`\n📁 Lokasi: /root/Royaltree/backend/backups/`);

    process.exit(0);
}

backup().catch(err => {
    console.error('❌ Backup fail:', err);
    process.exit(1);
});
