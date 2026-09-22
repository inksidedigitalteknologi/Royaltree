package com.inkside.digital.data.network.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ApiHealthResponse(
    @Json(name = "success") val success: Boolean = true,
    @Json(name = "status") val status: String = "ONLINE",
    @Json(name = "message") val message: String = "",
    @Json(name = "timestamp") val timestamp: Long = 0L,
    @Json(name = "serverTime") val serverTime: String? = null
)

@JsonClass(generateAdapter = true)
data class ApiConfigResponse(
    @Json(name = "success") val success: Boolean = true,
    @Json(name = "data") val data: ApiConfigData? = null
)

@JsonClass(generateAdapter = true)
data class ApiConfigData(
    @Json(name = "maintenanceMode") val maintenanceMode: Boolean = false,
    @Json(name = "minWithdrawalEWallet") val minWithdrawalEWallet: Double = 50000.0,
    @Json(name = "minWithdrawalBank") val minWithdrawalBank: Double = 100000.0,
    @Json(name = "minWithdrawalCrypto") val minWithdrawalCrypto: Double = 250000.0,
    @Json(name = "freeTierCommissionRate") val freeTierCommissionRate: Double = 12.0,
    @Json(name = "vipTierCommissionRate") val vipTierCommissionRate: Double = 30.0,
    @Json(name = "pointsPerUsdRate") val pointsPerUsdRate: Int = 100,
    @Json(name = "idrPerHundredPoints") val idrPerHundredPoints: Double = 16000.0,
    @Json(name = "serverVersion") val serverVersion: String? = "1.0.0",
    @Json(name = "announcement") val announcement: String? = null
)

@JsonClass(generateAdapter = true)
data class ApiUserSyncRequest(
    @Json(name = "id") val id: String,
    @Json(name = "name") val name: String,
    @Json(name = "email") val email: String,
    @Json(name = "phone") val phone: String,
    @Json(name = "tier") val tier: String,
    @Json(name = "balance") val balance: Double,
    @Json(name = "points") val points: Int,
    @Json(name = "todaySteps") val todaySteps: Int,
    @Json(name = "referralCode") val referralCode: String,
    @Json(name = "city") val city: String? = null
)

@JsonClass(generateAdapter = true)
data class ApiUserSyncResponse(
    @Json(name = "success") val success: Boolean = true,
    @Json(name = "message") val message: String = "",
    @Json(name = "serverSyncedAt") val serverSyncedAt: Long = 0L
)

@JsonClass(generateAdapter = true)
data class ApiGenericResponse(
    @Json(name = "success") val success: Boolean = true,
    @Json(name = "message") val message: String = ""
)
