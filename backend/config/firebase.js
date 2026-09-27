// config/firebase.js
// Inisialisasi Firebase Admin SDK — baca service account langsung dari file

const admin = require('firebase-admin');
const path = require('path');

if (!admin.apps.length) {
    const serviceAccountPath = path.join(__dirname, '..', 'service-account.json');
    const serviceAccount = require(serviceAccountPath);

    admin.initializeApp({
        credential: admin.credential.cert(serviceAccount),
        projectId: serviceAccount.project_id
    });

    console.log('✅ Firebase Admin SDK initialized');
    console.log('   Project ID: ' + serviceAccount.project_id);
    console.log('   Client Email: ' + serviceAccount.client_email);
}

const db = admin.firestore();

module.exports = { admin, db };
