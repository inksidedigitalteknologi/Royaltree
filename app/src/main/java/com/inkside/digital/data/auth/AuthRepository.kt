package com.inkside.digital.data.auth

import android.util.Log
import com.inkside.digital.data.network.ApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

/**
 * AuthRepository: handle sync user ke backend setelah login Firebase.
 * Backend endpoint: POST /api/v1/auth/sync
 */
class AuthRepository {

    private val TAG = "AuthRepository"

    /**
     * Sync user ke backend setelah login Firebase Auth.
     * @param firebaseIdToken ID token dari Firebase
     * @param name nama user (opsional)
     * @param phone nomor HP (opsional)
     * @param city kota (opsional)
     * @param referralCode kode referral (opsional)
     * @return JSONObject respons atau null kalau gagal
     */
    suspend fun syncUserToBackend(
        firebaseIdToken: String,
        name: String? = null,
        phone: String? = null,
        city: String? = null,
        referralCode: String? = null
    ): Result<JSONObject> = withContext(Dispatchers.IO) {
        try {
            val body = JSONObject().apply {
                name?.let { put("name", it) }
                phone?.let { put("phone", it) }
                city?.let { put("city", it) }
                referralCode?.let { put("referralCode", it) }
            }

            // Pakai ApiClient yang sudah ada AuthInterceptor
            // Endpoint ini dilindungi verifyFirebaseToken -> AuthInterceptor inject token otomatis
            val result = ApiClient.syncFirebaseUser(body)
            if (result.isSuccess) {
                Result.success(result.getOrThrow())
            } else {
                Result.failure(result.exceptionOrNull() ?: Exception("Sync gagal"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "syncUserToBackend error: ${e.message}")
            Result.failure(e)
        }
    }

    /**
     * Ambil data user dari backend.
     */
    suspend fun getMyProfile(): Result<JSONObject> = withContext(Dispatchers.IO) {
        try {
            val result = ApiClient.getMyProfile()
            if (result.isSuccess) {
                Result.success(result.getOrThrow())
            } else {
                Result.failure(result.exceptionOrNull() ?: Exception("Gagal ambil profil"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "getMyProfile error: ${e.message}")
            Result.failure(e)
        }
    }

    /**
     * Link referral code ke user yang sudah ada.
     */
    suspend fun linkReferral(referralCode: String): Result<JSONObject> = withContext(Dispatchers.IO) {
        try {
            val body = JSONObject().apply {
                put("referralCode", referralCode)
            }
            val result = ApiClient.linkReferral(body)
            if (result.isSuccess) {
                Result.success(result.getOrThrow())
            } else {
                Result.failure(result.exceptionOrNull() ?: Exception("Link referral gagal"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "linkReferral error: ${e.message}")
            Result.failure(e)
        }
    }
}
