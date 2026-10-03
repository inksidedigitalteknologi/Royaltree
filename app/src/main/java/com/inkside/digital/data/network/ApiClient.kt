package com.inkside.digital.data.network

import android.content.Context
import com.inkside.digital.data.network.model.ApiHealthResponse
import com.inkside.digital.data.network.model.ApiUserSyncRequest
import com.inkside.digital.data.network.model.ApiUserSyncResponse
import com.inkside.digital.data.network.model.StepSyncRequest
import com.inkside.digital.data.network.model.ApiGenericResponse
import com.inkside.digital.data.network.model.ApiHistoryResponse
import com.inkside.digital.data.network.model.ApiNotificationListResponse
import com.inkside.digital.data.network.model.ApiUnreadResponse
import com.inkside.digital.data.network.model.ApiAnalyticsSummaryResponse
import com.inkside.digital.data.network.model.SpinRequest
import com.inkside.digital.data.network.model.ApiSpinResponse
import com.inkside.digital.data.network.model.ApiProfileResponse
import com.inkside.digital.data.network.model.ApiWithdrawalListResponse
import com.inkside.digital.data.network.model.ApiWithdrawalRequest
import com.inkside.digital.data.network.model.ApiCampaignListResponse
import com.inkside.digital.data.network.model.ApiMissionListResponse
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import org.json.JSONObject
import java.net.SocketTimeoutException
import java.net.UnknownHostException

object ApiClient {
    // ============ AdGuard Detection Flag ============
    // Set true bila UnknownHostException / SocketTimeoutException (DNS block)
    // Global observable — trigger AdBlockedDialog di MainActivity
    private val _adBlockDetected = androidx.compose.runtime.mutableStateOf(false)
    val adBlockDetected: Boolean get() = _adBlockDetected.value
    
    fun resetAdBlockFlag() { _adBlockDetected.value = false }

    private const val PREFS_NAME = "royaltree_portal_prefs"
    private const val KEY_BASE_URL = "portal_base_url"
    private const val KEY_API_KEY = "portal_api_key"

    const val DEFAULT_BASE_URL = "http://45.41.204.21:3000/api/v1/"
    const val DEFAULT_API_KEY = "rt_secret_portal_key_2026"

    private var currentBaseUrl: String = DEFAULT_BASE_URL
    private var currentApiKey: String = DEFAULT_API_KEY

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .addInterceptor(AuthInterceptor())  // Firebase ID token (kalau ada user login)
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = if (com.inkside.digital.BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
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

    suspend fun syncStepsToPortal(
        userId: String,
        steps: Int,
        token: String = currentApiKey
    ): Result<ApiGenericResponse> = withContext(Dispatchers.IO) {
        try {
            val bearer = if (token.startsWith("Bearer ")) token else "Bearer $token"
            val response = apiService.syncSteps(bearer, StepSyncRequest(userId, steps))
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

    suspend fun getHistory(
        userId: String,
        token: String = currentApiKey
    ): Result<ApiHistoryResponse> = withContext(Dispatchers.IO) {
        try {
            val bearer = if (token.startsWith("Bearer ")) token else "Bearer $token"
            Result.success(apiService.getHistory(bearer, userId))
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun getNotifications(
        userId: String,
        token: String = currentApiKey
    ): Result<ApiNotificationListResponse> = withContext(Dispatchers.IO) {
        try {
            val bearer = if (token.startsWith("Bearer ")) token else "Bearer $token"
            Result.success(apiService.getNotifications(bearer, userId))
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun getUnreadCount(
        userId: String,
        token: String = currentApiKey
    ): Result<ApiUnreadResponse> = withContext(Dispatchers.IO) {
        try {
            val bearer = if (token.startsWith("Bearer ")) token else "Bearer $token"
            Result.success(apiService.getUnreadCount(bearer, userId))
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun getAnalyticsSummary(userId: String, token: String = currentApiKey): Result<ApiAnalyticsSummaryResponse> = withContext(Dispatchers.IO) {
        try { val b = if (token.startsWith("Bearer ")) token else "Bearer $token"; Result.success(apiService.getAnalyticsSummary(b, userId)) } catch (e: Exception) { Result.failure(e) }
    }
    suspend fun spinWheel(userId: String, token: String = currentApiKey): Result<ApiSpinResponse> = withContext(Dispatchers.IO) {
        try { val b = if (token.startsWith("Bearer ")) token else "Bearer $token"; Result.success(apiService.spinWheel(b, SpinRequest(userId))) } catch (e: Exception) { Result.failure(e) }
    }
    suspend fun getProfile(userId: String, token: String = currentApiKey): Result<ApiProfileResponse> = withContext(Dispatchers.IO) {
        try { val b = if (token.startsWith("Bearer ")) token else "Bearer $token"; Result.success(apiService.getProfile(b, userId)) } catch (e: Exception) { Result.failure(e) }
    }
    suspend fun updateProfile(userId: String, body: Map<String, String>, token: String = currentApiKey): Result<ApiGenericResponse> = withContext(Dispatchers.IO) {
        try { val b = if (token.startsWith("Bearer ")) token else "Bearer $token"; Result.success(apiService.updateProfile(b, userId, body)) } catch (e: Exception) { Result.failure(e) }
    }
    suspend fun changePin(userId: String, body: Map<String, String>, token: String = currentApiKey): Result<ApiGenericResponse> = withContext(Dispatchers.IO) {
        try { val b = if (token.startsWith("Bearer ")) token else "Bearer $token"; Result.success(apiService.changePin(b, userId, body)) } catch (e: Exception) { Result.failure(e) }
    }
    suspend fun getWithdrawals(token: String = currentApiKey): Result<ApiWithdrawalListResponse> = withContext(Dispatchers.IO) {
        try { val b = if (token.startsWith("Bearer ")) token else "Bearer $token"; Result.success(apiService.getWithdrawals(b)) } catch (e: Exception) { Result.failure(e) }
    }
    suspend fun createWithdrawal(req: ApiWithdrawalRequest, token: String = currentApiKey): Result<ApiGenericResponse> = withContext(Dispatchers.IO) {
        try { val b = if (token.startsWith("Bearer ")) token else "Bearer $token"; Result.success(apiService.createWithdrawal(b, req)) } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun getCampaigns(token: String = currentApiKey): Result<ApiCampaignListResponse> = withContext(Dispatchers.IO) {
        try { val b = if (token.startsWith("Bearer ")) token else "Bearer $token"; Result.success(apiService.getCampaigns(b)) } catch (e: Exception) { Result.failure(e) }
    }
    suspend fun getMissions(token: String = currentApiKey): Result<ApiMissionListResponse> = withContext(Dispatchers.IO) {
        try { val b = if (token.startsWith("Bearer ")) token else "Bearer $token"; Result.success(apiService.getMissions(b)) } catch (e: Exception) { Result.failure(e) }
    }

    // ============ AFFILIATE ============
    suspend fun getAffiliateBalance(): Result<JSONObject> = withContext(Dispatchers.IO) {
        try {
            val resp = apiService.getAffiliateBalance()
            Result.success(JSONObject(resp.string()))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAffiliateTransactions(limit: Int = 50): Result<JSONObject> = withContext(Dispatchers.IO) {
        try {
            val resp = apiService.getAffiliateTransactions(limit)
            Result.success(JSONObject(resp.string()))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun requestAffiliateWithdraw(amount: Double, destination: String, method: String = "PAYPAL"): Result<JSONObject> = withContext(Dispatchers.IO) {
        try {
            val body = JSONObject().apply {
                put("amount", amount)
                put("destination", destination)
                put("method", method)
            }
            val reqBody = body.toString().toRequestBody(JSON_MEDIA)
            val resp = apiService.requestAffiliateWithdraw(reqBody)
            Result.success(JSONObject(resp.string()))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAffiliateWithdrawals(limit: Int = 20): Result<JSONObject> = withContext(Dispatchers.IO) {
        try {
            val resp = apiService.getAffiliateWithdrawals(limit)
            Result.success(JSONObject(resp.string()))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun registerFcmToken(fcmToken: String, deviceId: String = ""): Result<JSONObject> = withContext(Dispatchers.IO) {
        try {
            val body = JSONObject().apply {
                put("fcmToken", fcmToken)
                if (deviceId.isNotEmpty()) put("deviceId", deviceId)
            }
            val reqBody = body.toString().toRequestBody(JSON_MEDIA)
            val resp = apiService.registerFcmToken(reqBody)
            Result.success(JSONObject(resp.string()))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getTiersPublic(): Result<JSONObject> = withContext(Dispatchers.IO) {
        try {
            val resp = apiService.getTiersPublic()
            Result.success(JSONObject(resp.string()))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun verifyPurchase(body: JSONObject): Result<JSONObject> = withContext(Dispatchers.IO) {
        try {
            val media = JSON_MEDIA
            val reqBody = body.toString().toRequestBody(media)
            val resp = apiService.verifyPurchase(reqBody)
            Result.success(JSONObject(resp.string()))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ============ AUTH (Firebase) ============

    private val JSON_MEDIA = "application/json; charset=utf-8".toMediaType()

    suspend fun syncFirebaseUser(body: JSONObject): Result<JSONObject> = withContext(Dispatchers.IO) {
        try {
            val requestBody = body.toString().toRequestBody(JSON_MEDIA)
            val response: ResponseBody = apiService.syncFirebaseUser(requestBody)
            val json = JSONObject(response.string())
            Result.success(json)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getMyProfile(): Result<JSONObject> = withContext(Dispatchers.IO) {
        try {
            val response: ResponseBody = apiService.getMyProfile()
            val json = JSONObject(response.string())
            Result.success(json)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun linkReferral(body: JSONObject): Result<JSONObject> = withContext(Dispatchers.IO) {
        try {
            val requestBody = body.toString().toRequestBody(JSON_MEDIA)
            val response: ResponseBody = apiService.linkReferral(requestBody)
            val json = JSONObject(response.string())
            Result.success(json)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ============ ADS REWARD ============

    suspend fun getAdStatus(): Result<JSONObject> = withContext(Dispatchers.IO) {
        try {
            val response: ResponseBody = apiService.getAdStatus()
            val json = JSONObject(response.string())
            Result.success(json)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun rewardAd(body: JSONObject): Result<JSONObject> = withContext(Dispatchers.IO) {
        try {
            val requestBody = body.toString().toRequestBody(JSON_MEDIA)
            val response: ResponseBody = apiService.rewardAd(requestBody)
            val json = JSONObject(response.string())
            Result.success(json)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getDailyStatus(): Result<JSONObject> = withContext(Dispatchers.IO) {
        try {
            val response: ResponseBody = apiService.getDailyStatus()
            val json = JSONObject(response.string())
            Result.success(json)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun dailyCheckIn(): Result<JSONObject> = withContext(Dispatchers.IO) {
        try {
            val response: ResponseBody = apiService.dailyCheckIn()
            val json = JSONObject(response.string())
            Result.success(json)
        } catch (e: retrofit2.HttpException) {
            if (e.code() == 409) {
                // Already checked in — bukan error, treat as success dengan flag
                val json = JSONObject()
                json.put("success", false)
                json.put("code", "ALREADY_CHECKED_IN")
                json.put("message", "Anda sudah check-in hari ini.")
                Result.success(json)
            } else {
                Result.failure(e)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun dailyCheckInWithAd(body: JSONObject): Result<JSONObject> = withContext(Dispatchers.IO) {
        try {
            val requestBody = body.toString().toRequestBody(JSON_MEDIA)
            val response: ResponseBody = apiService.dailyCheckInWithAd(requestBody)
            val json = JSONObject(response.string())
            Result.success(json)
        } catch (e: retrofit2.HttpException) {
            if (e.code() == 409) {
                // Already checked in — bukan error
                val json = JSONObject()
                json.put("success", false)
                json.put("code", "ALREADY_CHECKED_IN")
                json.put("message", "Anda sudah check-in hari ini.")
                Result.success(json)
            } else {
                Result.failure(e)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun recoverDay(body: JSONObject): Result<JSONObject> = withContext(Dispatchers.IO) {
        try {
            val requestBody = body.toString().toRequestBody(JSON_MEDIA)
            val response: ResponseBody = apiService.recoverDay(requestBody)
            val json = JSONObject(response.string())
            Result.success(json)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ============ GAME — MINING TYCOON ============

    suspend fun getGameState(): Result<JSONObject> = withContext(Dispatchers.IO) {
        try {
            val response: ResponseBody = apiService.getGameState()
            val json = JSONObject(response.string())
            // Reset flag bila berjaya
            resetAdBlockFlag()
            Result.success(json)
        } catch (e: UnknownHostException) {
            // DNS fail = AdGuard block API
            _adBlockDetected.value = true
            Result.failure(e)
        } catch (e: SocketTimeoutException) {
            // Timeout = kemungkinan AdGuard slow-block
            _adBlockDetected.value = true
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun claimMiningWithAd(transactionId: String, vendor: String): Result<JSONObject> = withContext(Dispatchers.IO) {
        try {
            val body = JSONObject().apply {
                put("transactionId", transactionId)
                put("vendor", vendor)
            }
            val reqBody = body.toString().toRequestBody(JSON_MEDIA)
            val resp = apiService.claimMiningWithAd(reqBody)
            Result.success(JSONObject(resp.string()))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun claimMining(): Result<JSONObject> = withContext(Dispatchers.IO) {
        try {
            val response: ResponseBody = apiService.claimMining()
            val json = JSONObject(response.string())
            Result.success(json)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun buyMiner(body: JSONObject): Result<JSONObject> = withContext(Dispatchers.IO) {
        try {
            val requestBody = body.toString().toRequestBody(JSON_MEDIA)
            val response: ResponseBody = apiService.buyMiner(requestBody)
            val json = JSONObject(response.string())
            Result.success(json)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun placeMiner(body: JSONObject): Result<JSONObject> = withContext(Dispatchers.IO) {
        try {
            val requestBody = body.toString().toRequestBody(JSON_MEDIA)
            val response: ResponseBody = apiService.placeMiner(requestBody)
            val json = JSONObject(response.string())
            Result.success(json)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun unplaceMiner(body: JSONObject): Result<JSONObject> = withContext(Dispatchers.IO) {
        try {
            val requestBody = body.toString().toRequestBody(JSON_MEDIA)
            val response: ResponseBody = apiService.unplaceMiner(requestBody)
            val json = JSONObject(response.string())
            Result.success(json)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getGameLeaderboard(): Result<JSONObject> = withContext(Dispatchers.IO) {
        try {
            val response: ResponseBody = apiService.getGameLeaderboard()
            val json = JSONObject(response.string())
            Result.success(json)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getGameStats(): Result<JSONObject> = withContext(Dispatchers.IO) {
        try {
            val response: ResponseBody = apiService.getGameStats()
            val json = JSONObject(response.string())
            Result.success(json)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getMinerCatalog(): Result<JSONObject> = withContext(Dispatchers.IO) {
        try {
            val response: ResponseBody = apiService.getMinerCatalog()
            val json = JSONObject(response.string())
            Result.success(json)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun claimMinerToken(body: JSONObject): Result<JSONObject> = withContext(Dispatchers.IO) {
        try {
            val requestBody = body.toString().toRequestBody(JSON_MEDIA)
            val response: ResponseBody = apiService.claimMinerToken(requestBody)
            val json = JSONObject(response.string())
            Result.success(json)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun unlockMiner(body: JSONObject): Result<JSONObject> = withContext(Dispatchers.IO) {
        try {
            val requestBody = body.toString().toRequestBody(JSON_MEDIA)
            val response: ResponseBody = apiService.unlockMiner(requestBody)
            val json = JSONObject(response.string())
            Result.success(json)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ============ MISSIONS ============

    suspend fun completeMission(missionId: String, userId: String): Result<JSONObject> = withContext(Dispatchers.IO) {
        try {
            val body = JSONObject().apply { put("userId", userId) }
            val requestBody = body.toString().toRequestBody(JSON_MEDIA)
            val response: ResponseBody = apiService.completeMission(missionId, requestBody)
            val json = JSONObject(response.string())
            Result.success(json)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun claimMission(missionId: String, userId: String): Result<JSONObject> = withContext(Dispatchers.IO) {
        try {
            val body = JSONObject().apply { put("userId", userId) }
            val requestBody = body.toString().toRequestBody(JSON_MEDIA)
            val response: ResponseBody = apiService.claimMission(missionId, requestBody)
            val json = JSONObject(response.string())
            Result.success(json)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}


