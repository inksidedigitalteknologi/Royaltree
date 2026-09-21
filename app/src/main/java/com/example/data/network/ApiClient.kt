package com.example.data.network

import android.content.Context
import com.example.data.network.model.ApiHealthResponse
import com.example.data.network.model.ApiUserSyncRequest
import com.example.data.network.model.ApiUserSyncResponse
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {

    private const val PREFS_NAME = "royaltree_portal_prefs"
    private const val KEY_BASE_URL = "portal_base_url"
    private const val KEY_API_KEY = "portal_api_key"

    const val DEFAULT_BASE_URL = "http://45.41.204.21:5000/api/v1/"
    const val DEFAULT_API_KEY = "rt_secret_portal_key_2026"

    private var currentBaseUrl: String = DEFAULT_BASE_URL
    private var currentApiKey: String = DEFAULT_API_KEY

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .build()

    private var retrofit: Retrofit = buildRetrofit(DEFAULT_BASE_URL)
    var apiService: RoyaltreeApiService = retrofit.create(RoyaltreeApiService::class.java)

    fun init(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        currentBaseUrl = prefs.getString(KEY_BASE_URL, DEFAULT_BASE_URL) ?: DEFAULT_BASE_URL
        currentApiKey = prefs.getString(KEY_API_KEY, DEFAULT_API_KEY) ?: DEFAULT_API_KEY
        updateBaseUrl(context, currentBaseUrl, currentApiKey)
    }

    fun getStoredBaseUrl(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_BASE_URL, DEFAULT_BASE_URL) ?: DEFAULT_BASE_URL
    }

    fun getStoredApiKey(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_API_KEY, DEFAULT_API_KEY) ?: DEFAULT_API_KEY
    }

    fun updateBaseUrl(context: Context, newUrl: String, newApiKey: String) {
        var formattedUrl = newUrl.trim()
        if (!formattedUrl.endsWith("/")) {
            formattedUrl += "/"
        }
        currentBaseUrl = formattedUrl
        currentApiKey = newApiKey.trim()

        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit()
            .putString(KEY_BASE_URL, currentBaseUrl)
            .putString(KEY_API_KEY, currentApiKey)
            .apply()

        retrofit = buildRetrofit(currentBaseUrl)
        apiService = retrofit.create(RoyaltreeApiService::class.java)
    }

    private fun buildRetrofit(url: String): Retrofit {
        val safeUrl = if (url.startsWith("http://") || url.startsWith("https://")) url else "https://$url"
        val finalUrl = if (safeUrl.endsWith("/")) safeUrl else "$safeUrl/"
        return Retrofit.Builder()
            .baseUrl(finalUrl)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    suspend fun testConnection(testUrl: String): Result<ApiHealthResponse> = withContext(Dispatchers.IO) {
        try {
            val tempRetrofit = buildRetrofit(testUrl)
            val tempService = tempRetrofit.create(RoyaltreeApiService::class.java)
            val response = tempService.checkHealth()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun syncUserToPortal(
        req: ApiUserSyncRequest,
        token: String = currentApiKey
    ): Result<ApiUserSyncResponse> = withContext(Dispatchers.IO) {
        try {
            val bearer = if (token.startsWith("Bearer ")) token else "Bearer $token"
            val response = apiService.syncUser(bearer, req)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
