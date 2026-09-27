// routes/auth.js
// Endpoint autentikasi user (Firebase Auth)

const express = require('express');
const router = express.Router();
const { admin, db } = require('../config/firebase');
const { verifyFirebaseToken } = require('../middleware/firebaseAuth');

// -------------------------------------------------------------
// POST /api/v1/auth/sync
// Dipanggil setelah user login/register via Firebase Auth
// Body: { name?, phone?, city?, referralCode? }
// -------------------------------------------------------------
router.post('/sync', verifyFirebaseToken, async (req, res) => {
    try {
        const { uid, email, name: firebaseName } = req.firebaseUser;
        const { name, phone, city, referralCode } = req.body;

        // ==== Normalize email & phone ====
        const normalizedEmail = (email || '').trim().toLowerCase();
        const normalizedPhone = (phone || '').trim().replace(/[\s\-]/g, '');

        // ==== Validasi HP wajib ====
        if (!normalizedPhone || normalizedPhone.length < 9) {
            return res.status(400).json({
                success: false,
                message: 'Nomor HP wajib diisi (minimal 9 digit).'
            });
        }

        const userRef = db.collection('users').doc(uid);
        const userDoc = await userRef.get();

        // ==== Cek duplikat email (hanya untuk user BARU) ====
        if (!userDoc.exists && normalizedEmail) {
            const emailCheck = await db.collection('users')
                .where('email', '==', normalizedEmail)
                .limit(1)
                .get();

            if (!emailCheck.empty) {
                // Log fraud attempt
                await db.collection('audit_logs').add({
                    userId: uid,
                    action: 'DUPLICATE_EMAIL',
                    email: normalizedEmail,
                    existingUserId: emailCheck.docs[0].id,
                    timestamp: admin.firestore.FieldValue.serverTimestamp()
                });

                return res.status(409).json({
                    success: false,
                    message: 'Email sudah terdaftar dengan akun lain. Gunakan email lain.'
                });
            }
        }

        // ==== Cek duplikat HP (hanya untuk user BARU) ====
        if (!userDoc.exists && normalizedPhone) {
            const phoneCheck = await db.collection('users')
                .where('phone', '==', normalizedPhone)
                .limit(1)
                .get();

            if (!phoneCheck.empty) {
                // Log fraud attempt
                await db.collection('audit_logs').add({
                    userId: uid,
                    action: 'DUPLICATE_PHONE',
                    phone: normalizedPhone,
                    existingUserId: phoneCheck.docs[0].id,
                    timestamp: admin.firestore.FieldValue.serverTimestamp()
                });

                return res.status(409).json({
                    success: false,
                    message: 'Nomor HP sudah terdaftar dengan akun lain. Gunakan nomor lain.'
                });
            }
        }

        // Kalau user sudah ada -> update lastLoginAt & auto-fill field baru
        if (userDoc.exists) {
            const existingData = userDoc.data();
            
            // Default field baru (untuk user lama yang belum punya)
            const defaults = {
                pendingBalance: 0,
                totalPaidOut: 0,
                unclaimedSteps: 0,
                dailyStepGoal: 5000,
                isLocationTrackingAllowed: true,
                latitude: 0,
                longitude: 0,
                locationCity: '',
                locationProvince: '',
                regionZone: 'ID',
                is2FAEnabled: false,
                twoFactorSecret: '',
                commissionRateMultiplier: 1.0,
                convertedStepsToday: 0,
                checkInStreak: 0,
                lastCheckInDate: ''
            };

            // Cari field yang belum ada
            const toFill = {};
            Object.keys(defaults).forEach(key => {
                if (existingData[key] === undefined) {
                    toFill[key] = defaults[key];
                }
            });

            // Update lastLoginAt + fill missing fields
            await userRef.update({
                lastLoginAt: admin.firestore.FieldValue.serverTimestamp(),
                ...toFill
            });

            const updated = await userRef.get();
            return res.json({
                success: true,
                isNewUser: false,
                data: { id: updated.id, ...updated.data() }
            });
        }

        // User baru -> buat dokumen
        // Generate referral code unik dari uid
        const generatedRefCode = 'RT' + uid.substring(0, 6).toUpperCase();

        const newUser = {
            // === Core fields ===
            email: normalizedEmail,
            name: name || firebaseName || 'User Baru',
            phone: normalizedPhone,
            city: city || '',
            tier: 'FREE',
            balance: 0,
            points: 0,
            todaySteps: 0,
            checkInStreak: 0,
            lastCheckInDate: '',
            referralCode: generatedRefCode,
            referredBy: null,
            createdAt: admin.firestore.FieldValue.serverTimestamp(),
            lastLoginAt: admin.firestore.FieldValue.serverTimestamp(),

            // === Local-only fields (sync ke Firestore untuk persist) ===
            pendingBalance: 0,
            totalPaidOut: 0,
            unclaimedSteps: 0,
            dailyStepGoal: 5000,
            isLocationTrackingAllowed: true,
            latitude: 0,
            longitude: 0,
            locationCity: '',
            locationProvince: '',
            regionZone: 'ID',
            is2FAEnabled: false,
            twoFactorSecret: '',
            commissionRateMultiplier: 1.0,
            convertedStepsToday: 0
        };

        // Kalau ada referral code -> validasi & simpan
        if (referralCode && referralCode.trim() !== '') {
            const refQuery = await db.collection('users')
                .where('referralCode', '==', referralCode.trim().toUpperCase())
                .limit(1)
                .get();

            if (!refQuery.empty) {
                const referrerDoc = refQuery.docs[0];
                newUser.referredBy = referrerDoc.id;
                newUser.referredByCode = referralCode.trim().toUpperCase();
            }
        }

        await userRef.set(newUser);

        return res.json({
            success: true,
            isNewUser: true,
            data: { id: uid, ...newUser }
        });

    } catch (error) {
        console.error('Error in /auth/sync:', error);
        return res.status(500).json({
            success: false,
            message: 'Gagal sinkronisasi user: ' + error.message
        });
    }
});

// -------------------------------------------------------------
// GET /api/v1/auth/me
// Ambil data user yang sedang login
// -------------------------------------------------------------
router.get('/me', verifyFirebaseToken, async (req, res) => {
    try {
        const { uid } = req.firebaseUser;
        const userDoc = await db.collection('users').doc(uid).get();

        if (!userDoc.exists) {
            return res.status(404).json({
                success: false,
                message: 'User belum terdaftar. Silakan sync terlebih dahulu.'
            });
        }

        return res.json({
            success: true,
            data: { id: userDoc.id, ...userDoc.data() }
        });

    } catch (error) {
        console.error('Error in /auth/me:', error);
        return res.status(500).json({
            success: false,
            message: 'Gagal ambil data user: ' + error.message
        });
    }
});

// -------------------------------------------------------------
// POST /api/v1/auth/link-referral
// Link referral code ke user yang sudah ada
// Body: { referralCode }
// -------------------------------------------------------------
router.post('/link-referral', verifyFirebaseToken, async (req, res) => {
    try {
        const { uid } = req.firebaseUser;
        const { referralCode } = req.body;

        if (!referralCode || referralCode.trim() === '') {
            return res.status(400).json({
                success: false,
                message: 'Referral code tidak boleh kosong.'
            });
        }

        const userRef = db.collection('users').doc(uid);
        const userDoc = await userRef.get();

        if (!userDoc.exists) {
            return res.status(404).json({
                success: false,
                message: 'User belum terdaftar.'
            });
        }

        // Cek user sudah punya referredBy
        const userData = userDoc.data();
        if (userData.referredBy) {
            return res.status(400).json({
                success: false,
                message: 'Anda sudah pernah menggunakan referral code.'
            });
        }

        // Cari referrer
        const refQuery = await db.collection('users')
            .where('referralCode', '==', referralCode.trim().toUpperCase())
            .limit(1)
            .get();

        if (refQuery.empty) {
            return res.status(404).json({
                success: false,
                message: 'Referral code tidak ditemukan.'
            });
        }

        const referrerDoc = refQuery.docs[0];

        // Tidak boleh referral diri sendiri
        if (referrerDoc.id === uid) {
            return res.status(400).json({
                success: false,
                message: 'Tidak bisa menggunakan referral code sendiri.'
            });
        }

        await userRef.update({
            referredBy: referrerDoc.id,
            referredByCode: referralCode.trim().toUpperCase(),
            referredAt: admin.firestore.FieldValue.serverTimestamp()
        });

        return res.json({
            success: true,
            message: 'Referral code berhasil digunakan.',
            referrerName: referrerDoc.data().name || 'User'
        });

    } catch (error) {
        console.error('Error in /auth/link-referral:', error);
        return res.status(500).json({
            success: false,
            message: 'Gagal link referral: ' + error.message
        });
    }
});



// -------------------------------------------------------------
// GET /api/v1/auth/referral-stats
// Hitung jumlah referral & total earning
// -------------------------------------------------------------
router.get('/referral-stats', verifyFirebaseToken, async (req, res) => {
    try {
        const { uid } = req.firebaseUser;

        // Hitung user yang referredBy = uid
        const snapshot = await db.collection('users')
            .where('referredBy', '==', uid)
            .get();

        const referredCount = snapshot.size;
        let totalEarning = 0;

        snapshot.forEach(doc => {
            const data = doc.data();
            if (data.referralEarning) {
                totalEarning += data.referralEarning;
            }
        });

        // Update user doc dengan referredCount
        await db.collection('users').doc(uid).update({
            referredCount: referredCount
        });

        return res.json({
            success: true,
            data: {
                referredCount: referredCount,
                referralEarnings: totalEarning
            }
        });
    } catch (error) {
        console.error('Error in /auth/referral-stats:', error);
        return res.status(500).json({
            success: false,
            message: 'Gagal ambil data referral: ' + error.message
        });
    }
});

module.exports = router;
