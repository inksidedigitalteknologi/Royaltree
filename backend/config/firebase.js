// config/firebase.js
// Inisialisasi Firebase Admin SDK - dipanggil sekali di awal

const admin = require('firebase-admin');

if (!admin.apps.length) {
    admin.initializeApp({
        credential: admin.credential.applicationDefault()
    });
    console.log('✅ Firebase Admin SDK initialized');
}

const db = admin.firestore();

module.exports = { admin, db };
