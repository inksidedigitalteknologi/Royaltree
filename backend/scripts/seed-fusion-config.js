#!/usr/bin/env node
/**
 * scripts/seed-fusion-config.js
 * 
 * Seed config fusion default ke Firestore.
 * Jalankan sekali: node scripts/seed-fusion-config.js
 * 
 * Aman dijalankan berulang — pakai { merge: true }, tidak menimpa field lain.
 */

require('dotenv').config();
const admin = require('firebase-admin');

// Init Firebase Admin
if (!admin.apps.length) {
    const serviceAccount = require('../service-account.json');
    admin.initializeApp({
        credential: admin.credential.cert(serviceAccount),
    });
}

const db = admin.firestore();

const DEFAULT_RATES = {
    COMMON: 100,
    RARE: 500,
    EPIC: 2000,
    LEGENDARY: 10000,
};

async function main() {
    console.log('🔧 Seed fusion config...');
    
    const ref = db.collection('system').doc('fusion_config');
    const snap = await ref.get();
    
    if (snap.exists) {
        console.log('ℹ️  Config sudah ada:');
        console.log(JSON.stringify(snap.data(), null, 2));
        console.log('\n✅ Skip seed (sudah ada). Kalau mau update, pakai admin panel.');
        return;
    }
    
    await ref.set({
        rates: DEFAULT_RATES,
        createdAt: admin.firestore.FieldValue.serverTimestamp(),
        updatedAt: admin.firestore.FieldValue.serverTimestamp(),
        updatedBy: 'seed-script',
    });
    
    console.log('✅ Config fusion berhasil di-seed:');
    console.log(JSON.stringify({ rates: DEFAULT_RATES }, null, 2));
}

main()
    .then(() => process.exit(0))
    .catch(err => {
        console.error('❌ Error:', err);
        process.exit(1);
    });
