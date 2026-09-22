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

@JsonClass(generateAdapter = true)
data class StepSyncRequest(
    @Json(name = "userId") val userId: String,
    @Json(name = "steps") val steps: Int
)

@JsonClass(generateAdapter = true)
data class ApiHistoryResponse(
    @Json(name = "success") val success: Boolean = true,
    @Json(name = "total") val total: Int = 0,
    @Json(name = "data") val data: List<HistoryItem> = emptyList()
)

@JsonClass(generateAdapter = true)
data class HistoryItem(
    @Json(name = "id") val id: String = "",
    @Json(name = "userId") val userId: String = "",
    @Json(name = "type") val type: String = "",
    @Json(name = "amount") val amount: Double = 0.0,
    @Json(name = "description") val description: String = "",
    @Json(name = "timestamp") val timestamp: Long = 0L
)

@JsonClass(generateAdapter = true)
data class ApiNotificationListResponse(
    @Json(name = "success") val success: Boolean = true,
    @Json(name = "total") val total: Int = 0,
    @Json(name = "data") val data: List<NotificationItem> = emptyList()
)

@JsonClass(generateAdapter = true)
data class NotificationItem(
    @Json(name = "id") val id: String = "",
    @Json(name = "userId") val userId: String = "",
    @Json(name = "title") val title: String = "",
    @Json(name = "message") val message: String = "",
    @Json(name = "type") val type: String = "INFO",
    @Json(name = "read") val read: Boolean = false,
    @Json(name = "timestamp") val timestamp: Long = 0L
)

@JsonClass(generateAdapter = true)
data class ApiUnreadResponse(
    @Json(name = "success") val success: Boolean = true,
    @Json(name = "unread") val unread: Int = 0
)

@JsonClass(generateAdapter = true)
data class ApiAnalyticsSummaryResponse(
    @Json(name = "success") val success: Boolean = true,
    @Json(name = "data") val data: AnalyticsSummaryData? = null
)

@JsonClass(generateAdapter = true)
data class AnalyticsSummaryData(
    @Json(name = "userId") val userId: String = "",
    @Json(name = "balance") val balance: Double = 0.0,
    @Json(name = "points") val points: Int = 0,
    @Json(name = "tier") val tier: String = "FREE",
    @Json(name = "todaySteps") val todaySteps: Int = 0,
    @Json(name = "totalCommission") val totalCommission: Double = 0.0,
    @Json(name = "totalWithdrawal") val totalWithdrawal: Double = 0.0,
    @Json(name = "netEarnings") val netEarnings: Double = 0.0
)

@JsonClass(generateAdapter = true)
data class SpinRequest(
    @Json(name = "userId") val userId: String
)

@JsonClass(generateAdapter = true)
data class ApiSpinResponse(
    @Json(name = "success") val success: Boolean = true,
    @Json(name = "message") val message: String = "",
    @Json(name = "data") val data: SpinData? = null
)

@JsonClass(generateAdapter = true)
data class SpinData(
    @Json(name = "id") val id: String = "",
    @Json(name = "userId") val userId: String = "",
    @Json(name = "gameType") val gameType: String = "SPIN",
    @Json(name = "result") val result: String = "",
    @Json(name = "pointsWon") val pointsWon: Int = 0,
    @Json(name = "timestamp") val timestamp: Long = 0L
)

@JsonClass(generateAdapter = true)
data class ApiProfileResponse(
    @Json(name = "success") val success: Boolean = true,
    @Json(name = "data") val data: ProfileData? = null
)

@JsonClass(generateAdapter = true)
data class ProfileData(
    @Json(name = "id") val id: String = "",
    @Json(name = "name") val name: String = "",
    @Json(name = "email") val email: String = "",
    @Json(name = "phone") val phone: String = "",
    @Json(name = "city") val city: String = "",
    @Json(name = "tier") val tier: String = "FREE",
    @Json(name = "balance") val balance: Double = 0.0,
    @Json(name = "points") val points: Int = 0,
    @Json(name = "todaySteps") val todaySteps: Int = 0
)

@JsonClass(generateAdapter = true)
data class ApiWithdrawalListResponse(
    @Json(name = "success") val success: Boolean = true,
    @Json(name = "total") val total: Int = 0,
    @Json(name = "data") val data: List<WithdrawalData> = emptyList()
)

@JsonClass(generateAdapter = true)
data class WithdrawalData(
    @Json(name = "id") val id: String = "",
    @Json(name = "userId") val userId: String = "",
    @Json(name = "userName") val userName: String = "",
    @Json(name = "amount") val amount: Double = 0.0,
    @Json(name = "currency") val currency: String = "IDR",
    @Json(name = "channelType") val channelType: String = "E_WALLET",
    @Json(name = "providerName") val providerName: String = "",
    @Json(name = "accountDestination") val accountDestination: String = "",
    @Json(name = "status") val status: String = "PENDING",
    @Json(name = "fee") val fee: Double = 0.0,
    @Json(name = "netAmount") val netAmount: Double = 0.0,
    @Json(name = "requestedAt") val requestedAt: Long = 0L
)

@JsonClass(generateAdapter = true)
data class ApiWithdrawalRequest(
    @Json(name = "id") val id: String,
    @Json(name = "userId") val userId: String,
    @Json(name = "userName") val userName: String,
    @Json(name = "amount") val amount: Double,
    @Json(name = "currency") val currency: String = "IDR",
    @Json(name = "channelType") val channelType: String = "E_WALLET",
    @Json(name = "providerName") val providerName: String,
    @Json(name = "accountDestination") val accountDestination: String,
    @Json(name = "accountHolderName") val accountHolderName: String,
    @Json(name = "status") val status: String = "PENDING",
    @Json(name = "fee") val fee: Double = 0.0,
    @Json(name = "netAmount") val netAmount: Double = 0.0,
    @Json(name = "txRef") val txRef: String = ""
)
