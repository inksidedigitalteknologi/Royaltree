package com.inkside.digital.data.model

data class AchievementEntity(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val category: String = "",
    val tier: String = "BRONZE",
    val icon: String = "🏆",
    val progress: Int = 0,
    val target: Int = 1,
    val unlocked: Boolean = false,
    val claimed: Boolean = false,
    val reward: AchievementReward? = null
) {
    val progressPercent: Float
        get() = if (target > 0) (progress.toFloat() / target).coerceIn(0f, 1f) else 0f

    val canClaim: Boolean
        get() = unlocked && !claimed
}

data class AchievementReward(
    val rtp: Double = 0.0,
    val badge: String? = null,
    val frame: String? = null
)

data class AchievementStats(
    val total: Int = 0,
    val unlocked: Int = 0,
    val claimed: Int = 0,
    val claimable: Int = 0
)
