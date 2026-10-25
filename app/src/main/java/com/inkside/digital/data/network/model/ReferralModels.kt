package com.inkside.digital.data.network.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ReferralTier(
    @Json(name = "id") val id: String,
    @Json(name = "name") val name: String,
    @Json(name = "min") val min: Int,
    @Json(name = "max") val max: Int,
    @Json(name = "icon") val icon: String,
    @Json(name = "color") val color: String,
    @Json(name = "reward") val reward: Int = 0,
    @Json(name = "perks") val perks: List<String> = emptyList()
)

@JsonClass(generateAdapter = true)
data class ReferralTierProgress(
    @Json(name = "current") val current: ReferralTier,
    @Json(name = "next") val next: ReferralTier? = null,
    @Json(name = "progress") val progress: Int = 0,
    @Json(name = "remainingToNext") val remainingToNext: Int = 0
)

@JsonClass(generateAdapter = true)
data class ReferralStats(
    @Json(name = "referralCode") val referralCode: String,
    @Json(name = "referralLink") val referralLink: String,
    @Json(name = "totalInvited") val totalInvited: Int,
    @Json(name = "activeCount") val activeCount: Int,
    @Json(name = "pendingCount") val pendingCount: Int,
    @Json(name = "totalEarned") val totalEarned: Double,
    @Json(name = "affiliateBalance") val affiliateBalance: Double,
    @Json(name = "affiliateBalanceAvailable") val affiliateBalanceAvailable: Double,
    @Json(name = "affiliateBalancePending") val affiliateBalancePending: Double,
    @Json(name = "tier") val tier: ReferralTierProgress
)

@JsonClass(generateAdapter = true)
data class ReferralItem(
    @Json(name = "uid") val uid: String,
    @Json(name = "username") val username: String,
    @Json(name = "avatar") val avatar: String? = null,
    @Json(name = "city") val city: String? = null,
    @Json(name = "joinedAt") val joinedAt: Long? = null,
    @Json(name = "status") val status: String,
    @Json(name = "earnedFromThem") val earnedFromThem: Double = 0.0
)

@JsonClass(generateAdapter = true)
data class ApiReferralStatsResponse(
    @Json(name = "success") val success: Boolean,
    @Json(name = "data") val data: ReferralStats? = null,
    @Json(name = "message") val message: String? = null
)

@JsonClass(generateAdapter = true)
data class ApiReferralListData(
    @Json(name = "items") val items: List<ReferralItem> = emptyList(),
    @Json(name = "page") val page: Int = 1,
    @Json(name = "limit") val limit: Int = 20,
    @Json(name = "total") val total: Int = 0,
    @Json(name = "hasMore") val hasMore: Boolean = false
)

@JsonClass(generateAdapter = true)
data class ApiReferralListResponse(
    @Json(name = "success") val success: Boolean,
    @Json(name = "data") val data: ApiReferralListData? = null,
    @Json(name = "message") val message: String? = null
)
