package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String = "user_001",
    val name: String = "Hendra Wijaya",
    val email: String = "hendra.affiliate@gmail.com",
    val phone: String = "+62 812-3456-7890",
    val tier: String = "FREE", // FREE or PREMIUM
    val commissionRateMultiplier: Double = 1.0, // 1.0 for Free (12%), 2.5 for Premium (30%)
    val balance: Double = 4750000.0, // IDR
    val pendingBalance: Double = 820000.0,
    val totalPaidOut: Double = 12500000.0,
    val points: Int = 1850,
    val is2FAEnabled: Boolean = false,
    val twoFactorSecret: String = "JBSWY3DPEHPK3PXP",
    val encryptionKeyHash: String = "AES-256-GCM#e8f2...9b1a",
    val regionZone: String = "ID", // ID, GLOBAL, US, EU
    val referralCode: String = "PRO8892",
    val referredCount: Int = 24,
    val referralEarnings: Double = 640000.0,
    val checkInStreak: Int = 3,
    val lastCheckInDate: Long = 0L,
    val latitude: Double = -6.2088, // Jakarta Default
    val longitude: Double = 106.8456,
    val locationCity: String = "Jakarta Pusat",
    val locationProvince: String = "DKI Jakarta",
    val isLocationTrackingAllowed: Boolean = true,
    val lastLocationUpdate: Long = System.currentTimeMillis(),
    val todaySteps: Int = 1840,
    val unclaimedSteps: Int = 1840,
    val convertedStepsToday: Int = 0,
    val dailyStepGoal: Int = 5000,
    val lastStepTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "user_locations")
data class UserLocationLogEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val userName: String,
    val userTier: String,
    val latitude: Double,
    val longitude: Double,
    val cityName: String,
    val provinceOrRegion: String,
    val countryCode: String = "ID",
    val accuracyMeters: Float = 15.0f,
    val timestamp: Long = System.currentTimeMillis(),
    val activityStatus: String = "ACTIVE" // ACTIVE, IDLE, OFFLINE
)

@Entity(tableName = "system_settings")
data class SystemSettingsEntity(
    @PrimaryKey val id: String = "default_config",
    val minWithdrawalEWallet: Double = 50000.0,
    val minWithdrawalBank: Double = 100000.0,
    val minWithdrawalCrypto: Double = 250000.0,
    val freeTierCommissionRate: Double = 12.0,
    val vipTierCommissionRate: Double = 30.0,
    val pointsPerUsdRate: Int = 100, // 100 Pts = 1 USD
    val idrPerHundredPoints: Double = 16000.0,
    val qrTransferFeePoints: Int = 5,
    val qrTransferDailyLimitPoints: Int = 5000,
    val antiFraudVelocitySeconds: Int = 10,
    val isLocationTrackingEnabled: Boolean = true,
    val autoApproveWithdrawalMaxAmount: Double = 0.0, // 0 = require manual approval
    val maintenanceMode: Boolean = false,
    val updatedTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "campaigns")
data class CampaignEntity(
    @PrimaryKey val id: String,
    val title: String,
    val category: String,
    val merchantName: String,
    val commissionDisplay: String,
    val payoutType: String, // CPA, CPS, CPL
    val baseCommissionRate: Double, // e.g. 15.0 (%)
    val clicksCount: Int,
    val conversionsCount: Int,
    val totalEarned: Double,
    val status: String, // ACTIVE, PAUSED
    val isHighTicket: Boolean = false,
    val bannerBgColor: Long = 0xFF1E293B
)

@Entity(tableName = "affiliate_links")
data class AffiliateLinkEntity(
    @PrimaryKey val id: String,
    val campaignId: String,
    val campaignTitle: String,
    val customSlug: String,
    val shortUrl: String,
    val targetUrl: String,
    val subId: String,
    val clicks: Int,
    val conversions: Int,
    val totalEarnings: Double,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "withdrawals")
data class WithdrawalEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val amount: Double,
    val currency: String, // IDR, USD, USDT
    val channelType: String, // BANK, CRYPTO, E_WALLET
    val providerName: String, // BCA, Mandiri, USDT (TRC20), GoPay, Dana, PayPal
    val accountDestination: String, // Account number or wallet address
    val accountHolderName: String,
    val status: String, // PENDING, APPROVED, PAID, REJECTED
    val fee: Double,
    val netAmount: Double,
    val requestedAt: Long = System.currentTimeMillis(),
    val processedAt: Long? = null,
    val txRef: String,
    val adminNotes: String = ""
)

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey val id: String,
    val type: String, // COMMISSION, WITHDRAWAL, POINT_REWARD, COUPON_PURCHASE, COUPON_YIELD, COUPON_SALE, UPGRADE_FEE
    val title: String,
    val description: String,
    val amount: Double,
    val currency: String = "IDR",
    val isCredit: Boolean, // true for in, false for out
    val status: String = "SUCCESS", // SUCCESS, PENDING, REJECTED
    val timestamp: Long = System.currentTimeMillis(),
    val referenceId: String = ""
)

@Entity(tableName = "investment_coupons")
data class InvestmentCouponEntity(
    @PrimaryKey val id: String,
    val projectTitle: String,
    val projectCategory: String,
    val unitPrice: Double, // IDR or Points
    val expectedApyPercent: Double, // e.g. 14.8 (%)
    val durationDays: Int,
    val riskRating: String, // Low, Moderate
    val totalUnitsAvailable: Int,
    val ownedUnits: Int = 0,
    val isListedOnSecondaryMarket: Boolean = false,
    val secondaryMarketPrice: Double = 0.0,
    val sellerNote: String = "",
    val accumulatedYield: Double = 0.0,
    val lastYieldClaimAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey val id: String,
    val title: String,
    val message: String,
    val type: String, // COMMISSION, WITHDRAWAL, INVESTMENT, SECURITY, REWARD
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,
    val actionDeepLink: String = ""
)

@Entity(tableName = "task_missions")
data class TaskMissionEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val rtpReward: Int,
    val type: String, // DAILY_TASK, MILESTONE_MISSION, ENGAGEMENT_MISSION
    val category: String, // LIKE, SUBSCRIBE, VIEW, WATCH, WEB, GAME, LOGIN, SHARE, CLICKS, CONVERSION, SECURITY, AD, INVEST, VIP
    val currentProgress: Int,
    val maxProgress: Int,
    val isCompleted: Boolean = false,
    val isClaimed: Boolean = false,
    val iconKey: String = "star",
    val targetPlatform: String = "",
    val durationSeconds: Int = 15,
    val actionUrl: String = ""
)

@Entity(tableName = "game_miner_items")
data class GameMinerItemEntity(
    @PrimaryKey val id: String,
    val name: String,
    val tier: String, // COMMON, RARE, EPIC, LEGENDARY, MYTHIC
    val iconEmoji: String,
    val pricePoints: Int, // Cost to buy using points
    val powerGhs: Double, // Hash rate e.g. 150.0 GH/s
    val pointsPerMinute: Double, // Generation rate e.g. 0.15 poin/menit
    val isOwned: Boolean = false,
    val isPlacedInRoom: Boolean = false,
    val placedSlotIndex: Int = -1, // 0 to 5 for rack slots, or -1 if in inventory
    val description: String = ""
)

@Entity(tableName = "game_room_state")
data class GameRoomStateEntity(
    @PrimaryKey val id: String = "default_room",
    val unclaimedMiningPoints: Double = 0.0,
    val lastClaimTimestamp: Long = System.currentTimeMillis(),
    val totalMinedPointsClaimed: Double = 0.0,
    val tempPowerBonusGhs: Double = 0.0,
    val bonusExpiryTimestamp: Long = 0L,
    val miniGameHighScore: Int = 0
)

@Entity(tableName = "app_download_ads")
data class AppDownloadAdEntity(
    @PrimaryKey val id: String,
    val appName: String,
    val developer: String,
    val category: String,
    val tagline: String,
    val description: String,
    val packageName: String,
    val appSizeMb: Int,
    val rating: Double,
    val totalDownloads: String,
    val rewardCoins: Int,
    val rewardRupiah: Double,
    val iconEmoji: String,
    val themeColorHex: Long,
    val isInstalled: Boolean = false,
    val isRewardClaimed: Boolean = false,
    val downloadProgressPercent: Int = 0, // 0 to 100
    val isDownloading: Boolean = false,
    val badgeLabel: String = "POPULER"
)

data class AdBannerItem(
    val id: String,
    val sponsorName: String,
    val title: String,
    val subtitle: String,
    val ctaText: String,
    val bannerEmoji: String,
    val gradientColors: List<Long>,
    val rewardCoins: Int = 15,
    val targetUrl: String = "https://play.google.com"
)

