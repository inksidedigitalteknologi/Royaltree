package com.inkside.digital.data.network

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import okhttp3.Interceptor
import okhttp3.Response

/**
 * AuthInterceptor: otomatis tambahkan Firebase ID Token
 * ke header Authorization — HANYA kalau belum ada header Authorization.
 *
 * Prioritas:
 * 1. Kalau request sudah punya header Authorization (dari ApiClient) -> pakai itu
 * 2. Kalau tidak ada -> inject Firebase ID Token (kalau user login)
 */
class AuthInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // Kalau sudah ada Authorization header, jangan override
        if (originalRequest.header("Authorization") != null) {
            return chain.proceed(originalRequest)
        }

        // Coba ambil Firebase ID token
        val token = runBlocking {
            try {
                val user = FirebaseAuth.getInstance().currentUser
                user?.getIdToken(false)?.await()?.token
            } catch (e: Exception) {
                Log.e("AuthInterceptor", "Gagal ambil token: ${e.message}")
                null
            }
        }

        val newRequest = if (token != null) {
            originalRequest.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        } else {
            originalRequest
        }

        return chain.proceed(newRequest)
    }
}
