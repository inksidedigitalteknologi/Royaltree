#!/usr/bin/env node
/**
 * scripts/seed-mining-weights.js
 * 
 * Seed config bobot mining default ke Firestore.
 * Jalankan sekali: node scripts/seed-mining-weights.js
 */

require('dotenv').config();
const admin = require('firebase-admin');

if (admin.apps.length === 0) {
    const serviceAccount = require('../service-account.json');
    admin.initializeApp({
        credential: admin.credential.cert(serviceAccount),
    });
}

const db = admin.firestore();

const DEFAULT_WEIGHTS = {
    efficiency: 0.40,
    ping: 0.10,
    device: 0.20,
    thermal: 0.30,
};

const DEFAULT_FLOOR = 0.90;

async function main() {
    console.log('🔧 Seed mining weights...');
    
    const ref = db.collection('system').doc('mining_weights');
    const snap = await ref.get();
    
    if (snap.exists) {
        console.log('ℹ️  Config sudah ada:');
        console.log(JSON.stringify(snap.data(), null, 2));
        return;
    }
    
    await ref.set({
        weights: DEFAULT_WEIGHTS,
        floor: DEFAULT_FLOOR,
        createdAt: admin.firestore.FieldValue.serverTimestamp(),
        updatedAt: admin.firestore.FieldValue.serverTimestamp(),
        updatedBy: 'seed-script',
    });
    
    console.log('✅ Mining weights berhasil di-seed:');
    console.log(JSON.stringify({ weights: DEFAULT_WEIGHTS, floor: DEFAULT_FLOOR }, null, 2));
}

main()
    .then(() => process.exit(0))
    .catch(err => {
        console.error('❌ Error:', err);
        process.exit(1);
    });
