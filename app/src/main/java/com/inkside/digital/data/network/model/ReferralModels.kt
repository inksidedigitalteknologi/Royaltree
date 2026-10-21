package com.inkside.digital.data.network.model

import com.google.gson.annotations.SerializedName

data class ReferralTier(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("min") val min: Int,
    @SerializedName("max") val max: Int,
    @SerializedName("icon") val icon: String,
    @SerializedName("color") val color: String,
    @SerializedName("reward") val reward: Int,
    @SerializedName("perks") val perks: List<String> = emptyList()
)

data class ReferralTierProgress(
    @SerializedName("current") val current: ReferralTier,
    @SerializedName("next") val next: ReferralTier? = null,
    @SerializedName("progress") val progress: Int = 0,
    @SerializedName("remainingToNext") val remainingToNext: Int = 0
)

data class ReferralStats(
    @SerializedName("referralCode") val referralCode: String,
    @SerializedName("referralLink") val referralLink: String,
    @SerializedName("totalInvited") val totalInvited: Int,
    @SerializedName("activeCount") val activeCount: Int,
    @SerializedName("pendingCount") val pendingCount: Int,
    @SerializedName("totalEarned") val totalEarned: Double,
    @SerializedName("affiliateBalance") val affiliateBalance: Double,
    @SerializedName("affiliateBalanceAvailable") val affiliateBalanceAvailable: Double,
    @SerializedName("affiliateBalancePending") val affiliateBalancePending: Double,
    @SerializedName("tier") val tier: ReferralTierProgress
)

data class ReferralItem(
    @SerializedName("uid") val uid: String,
    @SerializedName("username") val username: String,
    @SerializedName("avatar") val avatar: String? = null,
    @SerializedName("city") val city: String? = null,
    @SerializedName("joinedAt") val joinedAt: Long? = null,
    @SerializedName("status") val status: String,
    @SerializedName("earnedFromThem") val earnedFromThem: Double = 0.0
)

data class ApiReferralStatsResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: ReferralStats? = null,
    @SerializedName("message") val message: String? = null
)

data class ApiReferralListData(
    @SerializedName("items") val items: List<ReferralItem> = emptyList(),
    @SerializedName("page") val page: Int = 1,
    @SerializedName("limit") val limit: Int = 20,
    @SerializedName("total") val total: Int = 0,
    @SerializedName("hasMore") val hasMore: Boolean = false
)

data class ApiReferralListResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: ApiReferralListData? = null,
    @SerializedName("message") val message: String? = null
)
