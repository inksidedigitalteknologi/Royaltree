package com.inkside.digital.data.model

data class PurchaseTier(
    val productId: String = "",
    val tier: String = "FREE",
    val displayName: String = "",
    val priceUsd: Double = 0.0,
    val maxMinerSlots: Int = 0,
    val affiliateRate: Double = 0.0,
    val tierOrder: Int = 0,
    val benefits: List<String> = emptyList()
)
