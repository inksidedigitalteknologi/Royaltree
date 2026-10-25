package com.inkside.digital.data.network

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

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
import com.inkside.digital.util.AppLogger
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.inkside.digital.data.device.DeviceFingerprint
import com.inkside.digital.data.device.DeviceScore
import com.inkside.digital.data.ping.PingTracker
import okhttp3.OkHttpClient
import okhttp3.Headers
import okhttp3.Request
import okhttp3.RequestBody
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
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
import com.inkside.digital.data.network.model.ApiTransferConfigResponse

object ApiClient {

    private var appContext: Context? = null
    private var deviceFingerprint: DeviceFingerprint? = null

    private fun getEncryptedPrefs(context: Context): SharedPreferences {
        fun buildMasterKey() = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        fun open(key: MasterKey) = EncryptedSharedPreferences.create(
            context,
            PREFS_NAME,
            key,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

        // Percobaan 1
        try {
            return open(buildMasterKey())
        } catch (t: Throwable) {
            android.util.Log.w("ApiClient", "EncryptedSharedPreferences FAIL#1 (${t.javaClass.name}): ${t.message}")
        }

        // Wipe total
        try { context.deleteSharedPreferences(PREFS_NAME) } catch (_: Throwable) {}
        try {
            val ks = java.security.KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
            for (alias in ks.aliases().toList()) {
                if (alias.contains("master_key") || alias.contains("_androidx_security") || alias.contains("firebear")) {
                    try { ks.deleteEntry(alias) } catch (_: Throwable) {}
                }
            }
        } catch (t: Throwable) {
            android.util.Log.w("ApiClient", "Keystore wipe failed: ${t.message}")
        }

        // Percobaan 2
        try {
            return open(buildMasterKey())
        } catch (t: Throwable) {
            android.util.Log.w("ApiClient", "EncryptedSharedPreferences FAIL#2 (${t.javaClass.name}): ${t.message}")
        }

        // FALLBACK — prefs biasa
        android.util.Log.w("ApiClient", "FALLBACK ke SharedPreferences biasa untuk $PREFS_NAME")
        return context.getSharedPreferences(PREFS_NAME + "_plain", Context.MODE_PRIVATE)
    }

    // ============ AdGuard Detection Flag ============
    // Set true bila UnknownHostException / SocketTimeoutException (DNS block)
    // Global observable — trigger AdBlockedDialog di MainActivity
    private val _adBlockDetected = androidx.compose.runtime.mutableStateOf(false)
    val adBlockDetected: Boolean get() = _adBlockDetected.value
    
    fun resetAdBlockFlag() { _adBlockDetected.value = false }

    // ============ Banned User State ============
    // Set true bila backend return 403 USER_BANNED
    // Global observable — trigger BannedDialog di MainActivity
    data class BannedInfo(
        val banned: Boolean = false,
        val reason: String = "",
        val bannedAt: String = ""
    )

    private val _bannedState = androidx.compose.runtime.mutableStateOf(BannedInfo())
    val bannedState: BannedInfo get() = _bannedState.value

    fun setBanned(reason: String, bannedAt: String) {
        _bannedState.value = BannedInfo(true, reason, bannedAt)
    }

    fun resetBanned() { _bannedState.value = BannedInfo() }

    // ============ Config Refresh Event (dari FCM) ============
    data class ConfigRefreshEvent(
        val configType: String,
        val timestamp: Long
    )

    private val _configRefreshEvent = androidx.compose.runtime.mutableStateOf<ConfigRefreshEvent?>(null)
    val configRefreshEvent: ConfigRefreshEvent? get() = _configRefreshEvent.value

    fun triggerConfigRefresh(configType: String) {
        _configRefreshEvent.value = ConfigRefreshEvent(configType, System.currentTimeMillis())
        AppLogger.d("ApiClient", "Config refresh triggered: $configType")
    }

    fun consumeConfigRefresh() {
        _configRefreshEvent.value = null
    }

    // ============ Maintenance Mode ============
    private val _maintenanceMode = androidx.compose.runtime.mutableStateOf(false)
    val maintenanceMode: Boolean get() = _maintenanceMode.value

    private val _maintenanceMessage = androidx.compose.runtime.mutableStateOf("")
    val maintenanceMessage: String get() = _maintenanceMessage.value

    fun setMaintenanceMode(enabled: Boolean, message: String = "") {
        _maintenanceMode.value = enabled
        _maintenanceMessage.value = message
    }

    suspend fun checkHealth(): Result<JSONObject> = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url("${currentBaseUrl}health")
                .get()
                .build()
            val response = okHttpClient.newCall(request).execute()
            val body = response.body?.string() ?: "{}"
            Result.success(JSONObject(body))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Cek apakah error body mengandung USER_BANNED, lalu set state.
     * Return true jika banned terdeteksi.
     */
    private fun checkBannedResponse(code: Int, errorBody: String?): Boolean {
        if (code != 403 || errorBody.isNullOrBlank()) return false
        return try {
            val json = JSONObject(errorBody)
            if (json.optString("code") == "USER_BANNED") {
                setBanned(
                    json.optString("message", "Akun Anda diblokir"),
                    json.optString("bannedAt", "")
                )
                true
            } else false
        } catch (e: Exception) {
            false
        }
    }

    private const val PREFS_NAME = "royaltree_portal_prefs"
    private const val KEY_BASE_URL = "portal_base_url"
    private const val KEY_API_KEY = "portal_api_key"

    const val DEFAULT_BASE_URL = "http://45.41.204.21:3000/api/v1/"
    const val DEFAULT_API_KEY = com.inkside.digital.BuildConfig.ROYALTREE_API_KEY

    private var currentBaseUrl: String = DEFAULT_BASE_URL
    private var currentApiKey: String = DEFAULT_API_KEY

    // Helper: ambil Firebase ID token (untuk endpoint yang verifyFirebaseToken)
    private suspend fun getFirebaseToken(): String {
        return try {
            FirebaseAuth.getInstance().currentUser?.getIdToken(false)?.await()?.token ?: ""
        } catch (e: Exception) {
            ""
        }
    }

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .addInterceptor { chain ->
            val b = chain.request().newBuilder()
            deviceFingerprint?.let { fp ->
                b.header("X-Device-Fingerprint", fp.headerValue())
                b.header("X-Device-Score", DeviceScore.compute(fp).toString())
            }
            b.header("X-Ping-Ms", PingTracker.lastRttMs.value.toString())
            b.header("X-Ping-Timestamp", System.currentTimeMillis().toString())
            chain.proceed(b.build())
        }
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
        appContext = context.applicationContext
        deviceFingerprint = DeviceFingerprint.collect(context.applicationContext)

        val prefs = getEncryptedPrefs(context)
        currentBaseUrl = prefs.getString(KEY_BASE_URL, DEFAULT_BASE_URL) ?: DEFAULT_BASE_URL
        currentApiKey = prefs.getString(KEY_API_KEY, DEFAULT_API_KEY) ?: DEFAULT_API_KEY
        updateBaseUrl(context, currentBaseUrl, currentApiKey)
    }

    fun getStoredBaseUrl(context: Context): String {
        val prefs = getEncryptedPrefs(context)
        return prefs.getString(KEY_BASE_URL, DEFAULT_BASE_URL) ?: DEFAULT_BASE_URL
    }

    fun getStoredApiKey(context: Context): String {
        val prefs = getEncryptedPrefs(context)
        return prefs.getString(KEY_API_KEY, DEFAULT_API_KEY) ?: DEFAULT_API_KEY
    }

    fun updateBaseUrl(context: Context, newUrl: String, newApiKey: String) {
        var formattedUrl = newUrl.trim()
        if (!formattedUrl.endsWith("/")) {
            formattedUrl += "/"
        }
        currentBaseUrl = formattedUrl
        currentApiKey = newApiKey.trim()

        val prefs = getEncryptedPrefs(context)
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
        userId: String
    ): Result<ApiNotificationListResponse> = withContext(Dispatchers.IO) {
        try {
            val firebaseToken = getFirebaseToken()
            val bearer = "Bearer $firebaseToken"
            Result.success(apiService.getNotifications(bearer, userId))
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun getUnreadCount(
        userId: String
    ): Result<ApiUnreadResponse> = withContext(Dispatchers.IO) {
        try {
            val firebaseToken = getFirebaseToken()
            val bearer = "Bearer $firebaseToken"
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
    suspend fun getWithdrawals(token: String = ""): Result<ApiWithdrawalListResponse> = withContext(Dispatchers.IO) {
        try {
            val t = if (token.isNotEmpty()) token else getFirebaseToken()
            val b = if (t.startsWith("Bearer ")) t else "Bearer $t"
            Result.success(apiService.getWithdrawals(b))
        } catch (e: Exception) { Result.failure(e) }
    }
    suspend fun createWithdrawal(req: ApiWithdrawalRequest, token: String = ""): Result<ApiGenericResponse> = withContext(Dispatchers.IO) {
        try {
            val t = if (token.isNotEmpty()) token else getFirebaseToken()
            val b = if (t.startsWith("Bearer ")) t else "Bearer $t"
            Result.success(apiService.createWithdrawal(b, req))
        } catch (e: Exception) { Result.failure(e) }
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

    suspend fun postMarketingOptIn(optIn: Boolean, version: String = "1.0"): Result<JSONObject> = withContext(Dispatchers.IO) {
        try {
            val body = JSONObject().apply {
                put("optIn", optIn)
                put("version", version)
            }
            val requestBody = body.toString().toRequestBody(JSON_MEDIA)
            val response: ResponseBody = apiService.postMarketingOptIn(requestBody)
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

    // ============================================================
    // PURCHASE / IAP
    // ============================================================
    suspend fun getPurchaseTiersPublic(): Result<JSONObject> = withContext(Dispatchers.IO) {
        try {
            val resp = apiService.getTiersPublic()
            val json = JSONObject(resp.string())
            Result.success(json)
        } catch (e: Exception) {
            AppLogger.e("ApiClient", "getPurchaseTiersPublic error: ${e.message}", e)
            Result.failure(e)
        }
    }

    // ============================================================
    // ACHIEVEMENT
    // ============================================================
    suspend fun getMyAchievements(): Result<JSONObject> = withContext(Dispatchers.IO) {
        try {
            val resp = apiService.getMyAchievements()
            val json = JSONObject(resp.string())
            Result.success(json)
        } catch (e: Exception) {
            AppLogger.e("ApiClient", "getMyAchievements error: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun claimAchievement(id: String): Result<JSONObject> = withContext(Dispatchers.IO) {
        try {
            val resp = apiService.claimAchievement(id)
            val json = JSONObject(resp.string())
            Result.success(json)
        } catch (e: Exception) {
            AppLogger.e("ApiClient", "claimAchievement error: ${e.message}", e)
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

    // ==== GLOBAL CHAT ====
    suspend fun getChatMessages(): Result<JSONObject> = withContext(Dispatchers.IO) {
        try {
            val response: ResponseBody = apiService.getChatMessages()
            val json = JSONObject(response.string())
            Result.success(json)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun sendChatMessage(message: String): Result<JSONObject> = withContext(Dispatchers.IO) {
        try {
            val body = JSONObject().apply {
                put("message", message)
            }
            val requestBody = body.toString().toRequestBody(JSON_MEDIA)
            val response: ResponseBody = apiService.sendChatMessage(requestBody)
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

    // ============ THERMAL + LOCATION (sensor monitoring) ============
    suspend fun updateThermal(body: JSONObject): Result<JSONObject> = withContext(Dispatchers.IO) {
        try {
            val reqBody = body.toString().toRequestBody(JSON_MEDIA)
            val resp: ResponseBody = apiService.updateThermal(reqBody)
            resetAdBlockFlag()
            Result.success(JSONObject(resp.string()))
        } catch (e: UnknownHostException) {
            _adBlockDetected.value = true
            Result.failure(e)
        } catch (e: SocketTimeoutException) {
            _adBlockDetected.value = true
            Result.failure(e)
        } catch (e: retrofit2.HttpException) {
            try {
                val errorBody = e.response()?.errorBody()?.string() ?: "{}"
                // Cek banned user
                checkBannedResponse(e.code(), errorBody)
                val json = JSONObject(errorBody)
                json.put("httpCode", e.code())
                Result.success(json)
            } catch (parseErr: Exception) {
                Result.failure(e)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateLocation(body: JSONObject): Result<JSONObject> = withContext(Dispatchers.IO) {
        try {
            val reqBody = body.toString().toRequestBody(JSON_MEDIA)
            val resp: ResponseBody = apiService.updateLocation(reqBody)
            resetAdBlockFlag()
            Result.success(JSONObject(resp.string()))
        } catch (e: UnknownHostException) {
            _adBlockDetected.value = true
            Result.failure(e)
        } catch (e: SocketTimeoutException) {
            _adBlockDetected.value = true
            Result.failure(e)
        } catch (e: retrofit2.HttpException) {
            try {
                val errorBody = e.response()?.errorBody()?.string() ?: "{}"
                val json = JSONObject(errorBody)
                json.put("httpCode", e.code())
                Result.success(json)
            } catch (parseErr: Exception) {
                Result.failure(e)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

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
        } catch (e: retrofit2.HttpException) {
            // Parse error body untuk dapat mesej dari backend
            try {
                val errorBody = e.response()?.errorBody()?.string() ?: "{}"
                val json = JSONObject(errorBody)
                json.put("httpCode", e.code())
                Result.success(json)  // Return as success (dengan success: false)
            } catch (parseErr: Exception) {
                Result.failure(e)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun activateMiner(transactionId: String, vendor: String): Result<JSONObject> = withContext(Dispatchers.IO) {
        try {
            val body = JSONObject().apply {
                put("transactionId", transactionId)
                put("vendor", vendor)
            }
            val reqBody = body.toString().toRequestBody(JSON_MEDIA)
            val resp = apiService.activateMiner(reqBody)
            Result.success(JSONObject(resp.string()))
        } catch (e: retrofit2.HttpException) {
            try {
                val errorBody = e.response()?.errorBody()?.string() ?: "{}"
                val json = JSONObject(errorBody)
                json.put("httpCode", e.code())
                Result.success(json)
            } catch (parseErr: Exception) {
                Result.failure(e)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun claimMining(): Result<JSONObject> = withContext(Dispatchers.IO) {
        try {
            val response: ResponseBody = apiService.claimMining()
            val json = JSONObject(response.string())
            Result.success(json)
        } catch (e: retrofit2.HttpException) {
            try {
                val errorBody = e.response()?.errorBody()?.string() ?: "{}"
                val json = JSONObject(errorBody)
                json.put("httpCode", e.code())
                Result.success(json)
            } catch (parseErr: Exception) {
                Result.failure(e)
            }
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
        } catch (e: retrofit2.HttpException) {
            try {
                val errorBody = e.response()?.errorBody()?.string() ?: "{}"
                val json = JSONObject(errorBody)
                json.put("httpCode", e.code())
                Result.success(json)
            } catch (parseErr: Exception) {
                Result.failure(e)
            }
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
        } catch (e: retrofit2.HttpException) {
            try {
                val errorBody = e.response()?.errorBody()?.string() ?: "{}"
                val json = JSONObject(errorBody)
                json.put("httpCode", e.code())
                Result.success(json)
            } catch (parseErr: Exception) {
                Result.failure(e)
            }
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

    /**
     * Merge 5 miner tier sama → 1 miner tier+1.
     * Backend: POST /game/miners/merge
     */
    suspend fun mergeMiners(body: JSONObject): Result<JSONObject> = withContext(Dispatchers.IO) {
        try {
            val requestBody = body.toString().toRequestBody(JSON_MEDIA)
            val response: ResponseBody = apiService.mergeMiners(requestBody)
            val json = JSONObject(response.string())
            Result.success(json)
        } catch (e: retrofit2.HttpException) {
            try {
                val errorBody = e.response()?.errorBody()?.string() ?: "{}"
                val json = JSONObject(errorBody)
                json.put("httpCode", e.code())
                Result.success(json)
            } catch (parseErr: Exception) {
                Result.failure(e)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
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

    suspend fun getTransferConfig(): Result<ApiTransferConfigResponse> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getTransferConfig()
            Result.success(response)
        } catch (e: Exception) {
            AppLogger.w("ApiClient", "getTransferConfig gagal: ${e.message}")
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

    // ============ SWEEPSTAKES ============


    suspend fun getMyTickets(): Result<JSONObject> = withContext(Dispatchers.IO) {
        try {
            val response: ResponseBody = apiService.getMyTickets()
            Result.success(JSONObject(response.string()))
        } catch (e: retrofit2.HttpException) {
            try {
                val errorBody = e.response()?.errorBody()?.string() ?: "{}"
                val json = JSONObject(errorBody)
                json.put("httpCode", e.code())
                Result.success(json)
            } catch (parseErr: Exception) {
                Result.failure(e)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ============ TRANSFER ============

    suspend fun searchTransferUser(query: String): Result<JSONObject> = withContext(Dispatchers.IO) {
        try {
            val response: ResponseBody = apiService.searchTransferUser(query)
            Result.success(JSONObject(response.string()))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun sendTransfer(recipientId: String, amount: Int, note: String): Result<JSONObject> = withContext(Dispatchers.IO) {
        try {
            val body = JSONObject().apply {
                put("recipientId", recipientId)
                put("amount", amount)
                put("note", note)
            }
            val requestBody = body.toString().toRequestBody(JSON_MEDIA)
            val response: ResponseBody = apiService.sendTransfer(requestBody)
            Result.success(JSONObject(response.string()))
        } catch (e: retrofit2.HttpException) {
            try {
                val errorBody = e.response()?.errorBody()?.string() ?: "{}"
                val json = JSONObject(errorBody)
                json.put("httpCode", e.code())
                Result.success(json)
            } catch (parseErr: Exception) {
                Result.failure(e)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getTransferHistory(): Result<JSONObject> = withContext(Dispatchers.IO) {
        try {
            val response: ResponseBody = apiService.getTransferHistory()
            Result.success(JSONObject(response.string()))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ============ MINI GAME ============

    suspend fun finishMiniGame(
        score: Int,
        durationMs: Long = 0,
        gameNonce: String = ""
    ): Result<JSONObject> = withContext(Dispatchers.IO) {
        try {
            val body = JSONObject().apply {
                put("score", score)
                put("durationMs", durationMs)
                put("gameNonce", gameNonce)
            }
            val requestBody = body.toString().toRequestBody(JSON_MEDIA)
            val response: ResponseBody = apiService.finishMiniGame(requestBody)
            Result.success(JSONObject(response.string()))
        } catch (e: retrofit2.HttpException) {
            try {
                val errorBody = e.response()?.errorBody()?.string() ?: "{}"
                val json = JSONObject(errorBody)
                json.put("httpCode", e.code())
                Result.success(json)
            } catch (parseErr: Exception) {
                Result.failure(e)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ============ UNLOCK SLOT ============

    suspend fun unlockSlot(slotIndex: Int): Result<JSONObject> = withContext(Dispatchers.IO) {
        try {
            val body = JSONObject().apply { put("slotIndex", slotIndex) }
            val requestBody = body.toString().toRequestBody(JSON_MEDIA)
            val response: ResponseBody = apiService.unlockSlot(requestBody)
            Result.success(JSONObject(response.string()))
        } catch (e: retrofit2.HttpException) {
            try {
                val errorBody = e.response()?.errorBody()?.string() ?: "{}"
                val json = JSONObject(errorBody)
                json.put("httpCode", e.code())
                Result.success(json)
            } catch (parseErr: Exception) {
                Result.failure(e)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


