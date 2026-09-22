package com.inkside.digital.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.inkside.digital.data.dao.AppDao
import com.inkside.digital.data.model.AffiliateLinkEntity
import com.inkside.digital.data.model.AppDownloadAdEntity
import com.inkside.digital.data.model.CampaignEntity
import com.inkside.digital.data.model.GameMinerItemEntity
import com.inkside.digital.data.model.GameRoomStateEntity
import com.inkside.digital.data.model.InvestmentCouponEntity
import com.inkside.digital.data.model.NotificationEntity
import com.inkside.digital.data.model.SystemSettingsEntity
import com.inkside.digital.data.model.TaskMissionEntity
import com.inkside.digital.data.model.TransactionEntity
import com.inkside.digital.data.model.UserEntity
import com.inkside.digital.data.model.UserLocationLogEntity
import com.inkside.digital.data.model.WithdrawalEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserEntity::class,
        CampaignEntity::class,
        AffiliateLinkEntity::class,
        WithdrawalEntity::class,
        TransactionEntity::class,
        InvestmentCouponEntity::class,
        NotificationEntity::class,
        TaskMissionEntity::class,
        UserLocationLogEntity::class,
        SystemSettingsEntity::class,
        AppDownloadAdEntity::class,
        GameMinerItemEntity::class,
        GameRoomStateEntity::class
    ],
    version = 5,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appDao(): AppDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "royaltree.db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateDatabase(database.appDao())
                }
            }
        }

        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    val dao = database.appDao()
                    if (dao.getAllGameMinerItemsSync().isEmpty()) {
                        ensureGameAndMissionData(dao)
                    }
                }
            }
        }

        suspend fun ensureGameAndMissionData(dao: AppDao) {
            val defaultMiners = listOf(
                GameMinerItemEntity(
                    id = "miner_mini_01",
                    name = "Roller Mini Rig V1",
                    tier = "COMMON",
                    iconEmoji = "🖥️",
                    pricePoints = 60,
                    powerGhs = 120.0,
                    pointsPerMinute = 0.10,
                    isOwned = true,
                    isPlacedInRoom = true,
                    placedSlotIndex = 0,
                    description = "Rig penambang pemula hemat daya dengan lampu indikator hijau."
                ),
                GameMinerItemEntity(
                    id = "miner_spark_02",
                    name = "Dual GPU Spark 200",
                    tier = "RARE",
                    iconEmoji = "⚡",
                    pricePoints = 150,
                    powerGhs = 300.0,
                    pointsPerMinute = 0.25,
                    isOwned = true,
                    isPlacedInRoom = true,
                    placedSlotIndex = 1,
                    description = "Pendingin kipas ganda dengan hash rate stabil 24 jam non-stop."
                ),
                GameMinerItemEntity(
                    id = "miner_cyber_03",
                    name = "Neon Cyber Core 3000",
                    tier = "EPIC",
                    iconEmoji = "🔮",
                    pricePoints = 350,
                    powerGhs = 750.0,
                    pointsPerMinute = 0.60,
                    isOwned = false,
                    isPlacedInRoom = false,
                    placedSlotIndex = -1,
                    description = "Dilengkapi pencahayaan RGB neon dan prosesor quantum micro canggih."
                ),
                GameMinerItemEntity(
                    id = "miner_solar_04",
                    name = "Solar Bit Box Extreme",
                    tier = "EPIC",
                    iconEmoji = "☀️",
                    pricePoints = 600,
                    powerGhs = 1400.0,
                    pointsPerMinute = 1.10,
                    isOwned = false,
                    isPlacedInRoom = false,
                    placedSlotIndex = -1,
                    description = "Didukung panel surya hybrid dengan efisiensi mining poin maksimal."
                ),
                GameMinerItemEntity(
                    id = "miner_hydro_05",
                    name = "Royaltree Hydro Server",
                    tier = "LEGENDARY",
                    iconEmoji = "🚀",
                    pricePoints = 1200,
                    powerGhs = 3200.0,
                    pointsPerMinute = 2.50,
                    isOwned = false,
                    isPlacedInRoom = false,
                    placedSlotIndex = -1,
                    description = "Server industri water-cooled berkecepatan tinggi penghasil cuan pasif."
                ),
                GameMinerItemEntity(
                    id = "miner_singularity_06",
                    name = "Quantum Singularity Core",
                    tier = "MYTHIC",
                    iconEmoji = "💎",
                    pricePoints = 2500,
                    powerGhs = 7500.0,
                    pointsPerMinute = 5.50,
                    isOwned = false,
                    isPlacedInRoom = false,
                    placedSlotIndex = -1,
                    description = "Pembangkit poin revolusioner dengan akselerasi partikel & super AI."
                )
            )
            dao.insertGameMinerItems(defaultMiners)

            val initialRoomState = GameRoomStateEntity(
                id = "default_room",
                unclaimedMiningPoints = 4.25,
                lastClaimTimestamp = System.currentTimeMillis() - 1800000L,
                totalMinedPointsClaimed = 25.0,
                tempPowerBonusGhs = 50.0,
                bonusExpiryTimestamp = System.currentTimeMillis() + 3600000L,
                miniGameHighScore = 120
            )
            dao.insertGameRoomState(initialRoomState)
        }

        suspend fun populateDatabase(dao: AppDao) {
            // Default users for peer transfer testing
            val initialUsers = listOf(
                UserEntity(
                    id = "user_001",
                    name = "Hendra Wijaya",
                    email = "hendra.affiliate@gmail.com",
                    phone = "+62 812-3456-7890",
                    tier = "FREE",
                    commissionRateMultiplier = 1.0,
                    balance = 3850000.0,
                    pendingBalance = 620000.0,
                    totalPaidOut = 8400000.0,
                    points = 2450,
                    is2FAEnabled = true,
                    regionZone = "ID",
                    referralCode = "PRO8892",
                    referredCount = 18,
                    referralEarnings = 540000.0,
                    todaySteps = 2480,
                    unclaimedSteps = 2480,
                    convertedStepsToday = 0,
                    dailyStepGoal = 5000
                ),
                UserEntity(
                    id = "user_002",
                    name = "Siti Rahmawati",
                    email = "siti.rahma@gmail.com",
                    phone = "+62 813-9876-5432",
                    tier = "PREMIUM",
                    commissionRateMultiplier = 2.5,
                    balance = 1250000.0,
                    pendingBalance = 210000.0,
                    totalPaidOut = 4500000.0,
                    points = 950,
                    is2FAEnabled = false,
                    regionZone = "ID",
                    referralCode = "SITI2024",
                    referredCount = 9,
                    referralEarnings = 270000.0,
                    todaySteps = 3120,
                    unclaimedSteps = 1200,
                    convertedStepsToday = 2000,
                    dailyStepGoal = 5000
                ),
                UserEntity(
                    id = "user_003",
                    name = "Budi Santoso",
                    email = "budi.santoso@gmail.com",
                    phone = "+62 857-1122-3344",
                    tier = "FREE",
                    commissionRateMultiplier = 1.0,
                    balance = 800000.0,
                    pendingBalance = 150000.0,
                    totalPaidOut = 2100000.0,
                    points = 600,
                    is2FAEnabled = false,
                    regionZone = "ID",
                    referralCode = "BUDI99",
                    referredCount = 5,
                    referralEarnings = 150000.0,
                    todaySteps = 1450,
                    unclaimedSteps = 1450,
                    convertedStepsToday = 0,
                    dailyStepGoal = 5000
                ),
                UserEntity(
                    id = "user_004",
                    name = "Dewi Lestari",
                    email = "dewi.lestari@gmail.com",
                    phone = "+62 878-5544-3322",
                    tier = "PREMIUM",
                    commissionRateMultiplier = 2.5,
                    balance = 2400000.0,
                    pendingBalance = 400000.0,
                    totalPaidOut = 7800000.0,
                    points = 1500,
                    is2FAEnabled = true,
                    regionZone = "ID",
                    referralCode = "DEWI77",
                    referredCount = 14,
                    referralEarnings = 420000.0,
                    todaySteps = 4200,
                    unclaimedSteps = 800,
                    convertedStepsToday = 3400,
                    dailyStepGoal = 5000
                ),
                UserEntity(
                    id = "user_005",
                    name = "Rian Pratama",
                    email = "rian.pratama@gmail.com",
                    phone = "+62 821-4433-2211",
                    tier = "FREE",
                    commissionRateMultiplier = 1.0,
                    balance = 500000.0,
                    pendingBalance = 90000.0,
                    totalPaidOut = 1200000.0,
                    points = 400,
                    is2FAEnabled = false,
                    regionZone = "ID",
                    referralCode = "RIAN88",
                    referredCount = 3,
                    referralEarnings = 90000.0,
                    todaySteps = 980,
                    unclaimedSteps = 980,
                    convertedStepsToday = 0,
                    dailyStepGoal = 5000
                )
            )
            initialUsers.forEach { dao.insertUser(it) }

            // Default campaigns
            val defaultCampaigns = listOf(
                CampaignEntity(
                    id = "camp_01",
                    title = "Shopee Mega Sale Brand Ambassador",
                    category = "E-Commerce",
                    merchantName = "Shopee Official",
                    commissionDisplay = "Up to 15% / Rp 120.000 CPS",
                    payoutType = "CPS",
                    baseCommissionRate = 15.0,
                    clicksCount = 1420,
                    conversionsCount = 138,
                    totalEarned = 1656000.0,
                    status = "ACTIVE",
                    isHighTicket = true
                ),
                CampaignEntity(
                    id = "camp_02",
                    title = "Binance Crypto Partner Referral",
                    category = "FinTech & Crypto",
                    merchantName = "Binance Global",
                    commissionDisplay = "35% Trading Fee Rebate",
                    payoutType = "CPA",
                    baseCommissionRate = 35.0,
                    clicksCount = 890,
                    conversionsCount = 64,
                    totalEarned = 2240000.0,
                    status = "ACTIVE",
                    isHighTicket = true
                ),
                CampaignEntity(
                    id = "camp_03",
                    title = "Tokopedia Gadget Fest Promo",
                    category = "Electronics",
                    merchantName = "Tokopedia Mall",
                    commissionDisplay = "10% per Verified Order",
                    payoutType = "CPS",
                    baseCommissionRate = 10.0,
                    clicksCount = 670,
                    conversionsCount = 42,
                    totalEarned = 840000.0,
                    status = "ACTIVE"
                ),
                CampaignEntity(
                    id = "camp_04",
                    title = "Hostinger Cloud VPS Hosting",
                    category = "Cloud & Tech",
                    merchantName = "Hostinger Inc",
                    commissionDisplay = "Rp 350.000 flat CPA",
                    payoutType = "CPA",
                    baseCommissionRate = 25.0,
                    clicksCount = 310,
                    conversionsCount = 19,
                    totalEarned = 1330000.0,
                    status = "ACTIVE",
                    isHighTicket = true
                ),
                CampaignEntity(
                    id = "camp_05",
                    title = "Traveloka Flight & Staycation",
                    category = "Travel",
                    merchantName = "Traveloka",
                    commissionDisplay = "8% Booking Commission",
                    payoutType = "CPS",
                    baseCommissionRate = 8.0,
                    clicksCount = 520,
                    conversionsCount = 28,
                    totalEarned = 560000.0,
                    status = "ACTIVE"
                )
            )
            dao.insertCampaigns(defaultCampaigns)

            // Default links
            dao.insertLink(
                AffiliateLinkEntity(
                    id = "link_01",
                    campaignId = "camp_01",
                    campaignTitle = "Shopee Mega Sale",
                    customSlug = "shopee-hendra-sale",
                    shortUrl = "https://affl.pro/s/shopee-hendra-sale",
                    targetUrl = "https://shopee.co.id/flash-sale",
                    subId = "ig_bio",
                    clicks = 840,
                    conversions = 89,
                    totalEarnings = 1068000.0
                )
            )
            dao.insertLink(
                AffiliateLinkEntity(
                    id = "link_02",
                    campaignId = "camp_02",
                    campaignTitle = "Binance Crypto Referral",
                    customSlug = "binance-vip-bonus",
                    shortUrl = "https://affl.pro/s/binance-vip-bonus",
                    targetUrl = "https://binance.com/ref/pro8892",
                    subId = "telegram_group",
                    clicks = 620,
                    conversions = 48,
                    totalEarnings = 1680000.0
                )
            )

            // Default transactions
            val defaultTxs = listOf(
                TransactionEntity(
                    id = "tx_101",
                    type = "COMMISSION",
                    title = "Komisi Penjualan Shopee",
                    description = "Konversi campaign Mega Sale subID: ig_bio",
                    amount = 120000.0,
                    isCredit = true,
                    status = "SUCCESS",
                    timestamp = System.currentTimeMillis() - 3600000L * 3,
                    referenceId = "ORD-SP-99318"
                ),
                TransactionEntity(
                    id = "tx_102",
                    type = "COMMISSION",
                    title = "Rebate Trading Binance",
                    description = "CPA Referral Trading Fee (Kripto)",
                    amount = 350000.0,
                    isCredit = true,
                    status = "SUCCESS",
                    timestamp = System.currentTimeMillis() - 3600000L * 14,
                    referenceId = "REF-BN-20239"
                ),
                TransactionEntity(
                    id = "tx_103",
                    type = "WITHDRAWAL",
                    title = "Penarikan Dana Bank BCA",
                    description = "Rekening BCA 8291028472 (Admin Validated)",
                    amount = 1500000.0,
                    isCredit = false,
                    status = "SUCCESS",
                    timestamp = System.currentTimeMillis() - 86400000L * 2,
                    referenceId = "WD-BCA-77192"
                ),
                TransactionEntity(
                    id = "tx_104",
                    type = "COUPON_YIELD",
                    title = "Return Hasil Investasi Kupon",
                    description = "Yield harian Kupon Green Energy Retail",
                    amount = 45000.0,
                    isCredit = true,
                    status = "SUCCESS",
                    timestamp = System.currentTimeMillis() - 86400000L,
                    referenceId = "YLD-GRE-001"
                )
            )
            for (tx in defaultTxs) {
                dao.insertTransaction(tx)
            }

            // Default Withdrawals (including pending for admin approval testing)
            dao.insertWithdrawal(
                WithdrawalEntity(
                    id = "wd_001",
                    userId = "user_001",
                    amount = 1500000.0,
                    currency = "IDR",
                    channelType = "BANK",
                    providerName = "Bank Central Asia (BCA)",
                    accountDestination = "8291028472",
                    accountHolderName = "HENDRA WIJAYA",
                    status = "PAID",
                    fee = 4500.0,
                    netAmount = 1495500.0,
                    requestedAt = System.currentTimeMillis() - 86400000L * 2,
                    processedAt = System.currentTimeMillis() - 86400000L * 2 + 1800000L,
                    txRef = "TRX-BCA-9812401",
                    adminNotes = "Terverifikasi & Sukses via RTGS BCA Gateway"
                )
            )
            dao.insertWithdrawal(
                WithdrawalEntity(
                    id = "wd_002",
                    userId = "user_001",
                    amount = 500000.0,
                    currency = "IDR",
                    channelType = "CRYPTO",
                    providerName = "USDT (TRC-20)",
                    accountDestination = "TYG91oAkX18sQxV4vHsm2z87Kp9",
                    accountHolderName = "Hendra Wallet",
                    status = "PENDING",
                    fee = 15000.0,
                    netAmount = 485000.0,
                    requestedAt = System.currentTimeMillis() - 1800000L,
                    txRef = "TRX-USDT-294812",
                    adminNotes = "Menunggu verifikasi admin untuk transfer manual on-chain"
                )
            )

            // Investment coupons
            val defaultCoupons = listOf(
                InvestmentCouponEntity(
                    id = "coup_01",
                    projectTitle = "E-Commerce Tech Logistics Hub",
                    projectCategory = "Infrastruktur Digital",
                    unitPrice = 250000.0,
                    expectedApyPercent = 16.5,
                    durationDays = 90,
                    riskRating = "Rendah",
                    totalUnitsAvailable = 200,
                    ownedUnits = 2,
                    isListedOnSecondaryMarket = false,
                    accumulatedYield = 18500.0
                ),
                InvestmentCouponEntity(
                    id = "coup_02",
                    projectTitle = "Solar Power Green Retail Network",
                    projectCategory = "Energi Terbarukan",
                    unitPrice = 150000.0,
                    expectedApyPercent = 14.0,
                    durationDays = 60,
                    riskRating = "Rendah",
                    totalUnitsAvailable = 350,
                    ownedUnits = 1,
                    isListedOnSecondaryMarket = true,
                    secondaryMarketPrice = 158000.0,
                    sellerNote = "Kupon terverifikasi yield aktif 14% p.a.",
                    accumulatedYield = 12000.0
                ),
                InvestmentCouponEntity(
                    id = "coup_03",
                    projectTitle = "SaaS AI Automation Micro-Fund",
                    projectCategory = "Teknologi AI",
                    unitPrice = 500000.0,
                    expectedApyPercent = 21.0,
                    durationDays = 120,
                    riskRating = "Moderat",
                    totalUnitsAvailable = 100,
                    ownedUnits = 0,
                    isListedOnSecondaryMarket = true,
                    secondaryMarketPrice = 525000.0,
                    sellerNote = "Dijual di pasar sekunder diskon khusus"
                )
            )
            dao.insertInvestmentCoupons(defaultCoupons)

            // Default Notifications
            dao.insertNotification(
                NotificationEntity(
                    id = "notif_01",
                    title = "Komisi Masuk! +Rp 120.000",
                    message = "Selamat! Pembelian baru diverifikasi dari Shopee Mega Sale.",
                    type = "COMMISSION",
                    timestamp = System.currentTimeMillis() - 3600000L * 2,
                    isRead = false
                )
            )
            dao.insertNotification(
                NotificationEntity(
                    id = "notif_02",
                    title = "Penarikan Dana Rp 1.500.000 Berhasil!",
                    message = "Admin telah memproses transfer manual ke rekening BCA Anda.",
                    type = "WITHDRAWAL",
                    timestamp = System.currentTimeMillis() - 86400000L * 2,
                    isRead = true
                )
            )
            dao.insertNotification(
                NotificationEntity(
                    id = "notif_03",
                    title = "Hasil Investasi Kupon Masuk +Rp 45.000",
                    message = "Dividen harian dari Kupon Green Energy berhasil ditambahkan ke saldo.",
                    type = "INVESTMENT",
                    timestamp = System.currentTimeMillis() - 86400000L,
                    isRead = false
                )
            )

            // Default Tasks & Missions for RTP Rewards
            val defaultMissions = listOf(
                TaskMissionEntity(
                    id = "task_daily_login",
                    title = "Check-in Login Harian Royaltree",
                    description = "Login setiap hari dan klaim bonus RTP untuk membangun streak beruntun.",
                    rtpReward = 30,
                    type = "DAILY_TASK",
                    category = "LOGIN",
                    currentProgress = 1,
                    maxProgress = 1,
                    isCompleted = true,
                    isClaimed = false,
                    iconKey = "calendar"
                ),
                TaskMissionEntity(
                    id = "task_share_link",
                    title = "Bagikan Link Promosi Royaltree",
                    description = "Sebarkan minimal 1 tautan promosi aktif ke media sosial atau teman.",
                    rtpReward = 25,
                    type = "DAILY_TASK",
                    category = "SHARE",
                    currentProgress = 1,
                    maxProgress = 1,
                    isCompleted = true,
                    isClaimed = false,
                    iconKey = "share"
                ),
                TaskMissionEntity(
                    id = "task_clicks_target",
                    title = "Dapatkan 5 Klik Tautan Hari Ini",
                    description = "Ajak calon pembeli mengklik tautan promosi afiliasi Anda.",
                    rtpReward = 40,
                    type = "DAILY_TASK",
                    category = "CLICKS",
                    currentProgress = 3,
                    maxProgress = 5,
                    isCompleted = false,
                    isClaimed = false,
                    iconKey = "mouse"
                ),
                TaskMissionEntity(
                    id = "task_watch_sponsor_ad",
                    title = "Tonton Video Sponsor Royaltree",
                    description = "Selesaikan video interaktif sponsor developer untuk klaim instan RTP.",
                    rtpReward = 50,
                    type = "DAILY_TASK",
                    category = "AD",
                    currentProgress = 0,
                    maxProgress = 1,
                    isCompleted = false,
                    isClaimed = false,
                    iconKey = "play"
                ),
                TaskMissionEntity(
                    id = "ms_first_conversion",
                    title = "Misi: Raih Konversi Penjualan Pertama",
                    description = "Dapatkan transaksi sukses pertama dari kampanye afiliasi mana pun.",
                    rtpReward = 100,
                    type = "MILESTONE_MISSION",
                    category = "CONVERSION",
                    currentProgress = 1,
                    maxProgress = 1,
                    isCompleted = true,
                    isClaimed = false,
                    iconKey = "trophy"
                ),
                TaskMissionEntity(
                    id = "ms_security_2fa",
                    title = "Misi: Amankan Akun dengan 2FA",
                    description = "Aktifkan autentikasi 2-faktor (TOTP) untuk proteksi saldo dompet.",
                    rtpReward = 80,
                    type = "MILESTONE_MISSION",
                    category = "SECURITY",
                    currentProgress = 1,
                    maxProgress = 1,
                    isCompleted = true,
                    isClaimed = false,
                    iconKey = "shield"
                ),
                TaskMissionEntity(
                    id = "ms_payout_first",
                    title = "Misi: Lakukan Pencairan Saldo Pertama",
                    description = "Tarik komisi afiliasi ke rekening bank atau dompet kripto.",
                    rtpReward = 150,
                    type = "MILESTONE_MISSION",
                    category = "WITHDRAWAL",
                    currentProgress = 1,
                    maxProgress = 1,
                    isCompleted = true,
                    isClaimed = false,
                    iconKey = "wallet"
                ),
                TaskMissionEntity(
                    id = "ms_referral_recruit",
                    title = "Misi: Ajak 3 Affiliator Baru",
                    description = "Bagikan kode referral Royaltree dan dapatkan komisi jaringan + bonus RTP.",
                    rtpReward = 200,
                    type = "MILESTONE_MISSION",
                    category = "REFERRAL",
                    currentProgress = 2,
                    maxProgress = 3,
                    isCompleted = false,
                    isClaimed = false,
                    iconKey = "users"
                ),
                TaskMissionEntity(
                    id = "ms_vip_upgrade",
                    title = "Misi: Upgrade ke Royaltree VIP",
                    description = "Buka komisi 2.5x lipat dan lencana VIP emas Royaltree eksklusif.",
                    rtpReward = 500,
                    type = "MILESTONE_MISSION",
                    category = "VIP",
                    currentProgress = 0,
                    maxProgress = 1,
                    isCompleted = false,
                    isClaimed = false,
                    iconKey = "crown"
                ),
                // 1. LIKE Missions
                TaskMissionEntity(
                    id = "ms_like_shopee",
                    title = "Like Postingan Promo Shopee Partner",
                    description = "Beri suka pada postingan merchant Shopee untuk mendukung kampanye promo komisi.",
                    rtpReward = 25,
                    type = "SOCIAL_MISSION",
                    category = "LIKE",
                    currentProgress = 0,
                    maxProgress = 1,
                    isCompleted = false,
                    isClaimed = false,
                    iconKey = "heart",
                    targetPlatform = "Instagram",
                    actionUrl = "https://instagram.com/p/shopee_promo_cuan"
                ),
                TaskMissionEntity(
                    id = "ms_like_tiktok_tips",
                    title = "Like Video TikTok Tips Cuan Afiliasi",
                    description = "Beri Like pada video panduan tips promosi afiliasi dari kreator mitra Royaltree.",
                    rtpReward = 20,
                    type = "SOCIAL_MISSION",
                    category = "LIKE",
                    currentProgress = 0,
                    maxProgress = 1,
                    isCompleted = false,
                    isClaimed = false,
                    iconKey = "heart",
                    targetPlatform = "TikTok",
                    actionUrl = "https://tiktok.com/@royaltree_cuan/video/1"
                ),
                // 2. SUBSCRIBE Missions
                TaskMissionEntity(
                    id = "ms_sub_youtube",
                    title = "Subscribe Channel YouTube Royaltree",
                    description = "Berlangganan channel resmi Royaltree Indonesia untuk update tips cuan & strategi afiliasi.",
                    rtpReward = 50,
                    type = "SOCIAL_MISSION",
                    category = "SUBSCRIBE",
                    currentProgress = 0,
                    maxProgress = 1,
                    isCompleted = false,
                    isClaimed = false,
                    iconKey = "youtube",
                    targetPlatform = "YouTube",
                    actionUrl = "https://youtube.com/@royaltree_official"
                ),
                TaskMissionEntity(
                    id = "ms_sub_telegram",
                    title = "Subscribe Saluran Komunitas Telegram",
                    description = "Gabung ke channel Telegram resmi untuk mendapatkan bocoran kampanye komisi tertinggi.",
                    rtpReward = 40,
                    type = "SOCIAL_MISSION",
                    category = "SUBSCRIBE",
                    currentProgress = 0,
                    maxProgress = 1,
                    isCompleted = false,
                    isClaimed = false,
                    iconKey = "telegram",
                    targetPlatform = "Telegram",
                    actionUrl = "https://t.me/royaltree_id"
                ),
                // 3. VIEW Missions
                TaskMissionEntity(
                    id = "ms_view_catalog",
                    title = "Lihat Katalog Produk Populer (10 Detik)",
                    description = "Buka dan amati showcase produk marketplace dengan komisi tertinggi selama 10 detik.",
                    rtpReward = 20,
                    type = "CONTENT_MISSION",
                    category = "VIEW",
                    currentProgress = 0,
                    maxProgress = 1,
                    isCompleted = false,
                    isClaimed = false,
                    iconKey = "eye",
                    targetPlatform = "Katalog Royaltree",
                    durationSeconds = 10
                ),
                TaskMissionEntity(
                    id = "ms_view_promo_deal",
                    title = "Lihat Banner Promo Diskon Mitra",
                    description = "Periksa detail cashback & diskon eksklusif merchant mitra afiliasi.",
                    rtpReward = 15,
                    type = "CONTENT_MISSION",
                    category = "VIEW",
                    currentProgress = 0,
                    maxProgress = 1,
                    isCompleted = false,
                    isClaimed = false,
                    iconKey = "eye",
                    targetPlatform = "Showcase Promo",
                    durationSeconds = 8
                ),
                // 4. WATCH (MENONTON) Missions
                TaskMissionEntity(
                    id = "ms_watch_partner_video",
                    title = "Tonton Video Promo Aplikasi (15 Detik)",
                    description = "Tonton tayangan video sponsor hingga selesai untuk memperoleh poin reward instan.",
                    rtpReward = 35,
                    type = "CONTENT_MISSION",
                    category = "WATCH",
                    currentProgress = 0,
                    maxProgress = 1,
                    isCompleted = false,
                    isClaimed = false,
                    iconKey = "play",
                    targetPlatform = "Video Sponsor",
                    durationSeconds = 15
                ),
                TaskMissionEntity(
                    id = "ms_watch_tutorial_cuan",
                    title = "Tonton Video Edukasi Afiliasi (20 Detik)",
                    description = "Tonton video tutorial cara mudah menghasilkan 5 juta pertama dari tautan afiliasi.",
                    rtpReward = 45,
                    type = "CONTENT_MISSION",
                    category = "WATCH",
                    currentProgress = 0,
                    maxProgress = 1,
                    isCompleted = false,
                    isClaimed = false,
                    iconKey = "play",
                    targetPlatform = "Video Edukasi",
                    durationSeconds = 20
                ),
                // 5. WEB (MELAYARI WEB) Missions
                TaskMissionEntity(
                    id = "ms_browse_fintech_web",
                    title = "Melayari Web Portal Bank Digital (15 Detik)",
                    description = "Jelajahi halaman landing page partner perbankan digital resmi selama 15 detik.",
                    rtpReward = 35,
                    type = "WEB_MISSION",
                    category = "WEB",
                    currentProgress = 0,
                    maxProgress = 1,
                    isCompleted = false,
                    isClaimed = false,
                    iconKey = "globe",
                    targetPlatform = "Web Mitra",
                    durationSeconds = 15,
                    actionUrl = "https://partner-bank.royaltree.co.id/promo"
                ),
                TaskMissionEntity(
                    id = "ms_browse_ecommerce_web",
                    title = "Melayari Web Marketplace Pilihan (20 Detik)",
                    description = "Kunjungi dan jelajahi etalase online store merchant partner untuk klaim poin.",
                    rtpReward = 40,
                    type = "WEB_MISSION",
                    category = "WEB",
                    currentProgress = 0,
                    maxProgress = 1,
                    isCompleted = false,
                    isClaimed = false,
                    iconKey = "globe",
                    targetPlatform = "Web Mitra",
                    durationSeconds = 20,
                    actionUrl = "https://toko-mitra.royaltree.co.id/diskon-spesial"
                ),
                // 6. GAME (MEMAINKAN GAME) Missions
                TaskMissionEntity(
                    id = "ms_play_roller_game",
                    title = "Mainkan Mini-Game di Ruang Game",
                    description = "Mainkan mini-game Coin Tap / Memory Match di Ruang Game RollerCoin untuk power boost & poin.",
                    rtpReward = 30,
                    type = "GAME_MISSION",
                    category = "GAME",
                    currentProgress = 0,
                    maxProgress = 1,
                    isCompleted = false,
                    isClaimed = false,
                    iconKey = "gamepad",
                    targetPlatform = "Ruang Game"
                ),
                TaskMissionEntity(
                    id = "ms_place_miner_item",
                    title = "Pasang Item Miner di Ruang Game",
                    description = "Beli item penambang dengan poin dan pasang di rak untuk menghasilkan poin pasif!",
                    rtpReward = 50,
                    type = "GAME_MISSION",
                    category = "GAME",
                    currentProgress = 1,
                    maxProgress = 1,
                    isCompleted = true,
                    isClaimed = false,
                    iconKey = "gamepad",
                    targetPlatform = "Ruang Game"
                ),
                TaskMissionEntity(
                    id = "ms_claim_mining_points",
                    title = "Klaim Hasil Mining Pasif Pertama",
                    description = "Panen poin yang di-generate oleh item penambang di ruang game Anda.",
                    rtpReward = 35,
                    type = "GAME_MISSION",
                    category = "GAME",
                    currentProgress = 0,
                    maxProgress = 1,
                    isCompleted = false,
                    isClaimed = false,
                    iconKey = "gamepad",
                    targetPlatform = "Ruang Game"
                )
            )
            dao.insertMissions(defaultMissions)

            // Default Game Miner Items (RollerCoin-style mining room)
            val defaultMiners = listOf(
                GameMinerItemEntity(
                    id = "miner_mini_01",
                    name = "Roller Mini Rig V1",
                    tier = "COMMON",
                    iconEmoji = "🖥️",
                    pricePoints = 60,
                    powerGhs = 120.0,
                    pointsPerMinute = 0.10,
                    isOwned = true,
                    isPlacedInRoom = true,
                    placedSlotIndex = 0,
                    description = "Rig penambang pemula hemat daya dengan lampu indikator hijau."
                ),
                GameMinerItemEntity(
                    id = "miner_spark_02",
                    name = "Dual GPU Spark 200",
                    tier = "RARE",
                    iconEmoji = "⚡",
                    pricePoints = 150,
                    powerGhs = 300.0,
                    pointsPerMinute = 0.25,
                    isOwned = true,
                    isPlacedInRoom = true,
                    placedSlotIndex = 1,
                    description = "Pendingin kipas ganda dengan hash rate stabil 24 jam non-stop."
                ),
                GameMinerItemEntity(
                    id = "miner_cyber_03",
                    name = "Neon Cyber Core 3000",
                    tier = "EPIC",
                    iconEmoji = "🔮",
                    pricePoints = 350,
                    powerGhs = 750.0,
                    pointsPerMinute = 0.60,
                    isOwned = false,
                    isPlacedInRoom = false,
                    placedSlotIndex = -1,
                    description = "Dilengkapi pencahayaan RGB neon dan prosesor quantum micro canggih."
                ),
                GameMinerItemEntity(
                    id = "miner_solar_04",
                    name = "Solar Bit Box Extreme",
                    tier = "EPIC",
                    iconEmoji = "☀️",
                    pricePoints = 600,
                    powerGhs = 1400.0,
                    pointsPerMinute = 1.10,
                    isOwned = false,
                    isPlacedInRoom = false,
                    placedSlotIndex = -1,
                    description = "Didukung panel surya hybrid dengan efisiensi mining poin maksimal."
                ),
                GameMinerItemEntity(
                    id = "miner_hydro_05",
                    name = "Royaltree Hydro Server",
                    tier = "LEGENDARY",
                    iconEmoji = "🚀",
                    pricePoints = 1200,
                    powerGhs = 3200.0,
                    pointsPerMinute = 2.50,
                    isOwned = false,
                    isPlacedInRoom = false,
                    placedSlotIndex = -1,
                    description = "Server industri water-cooled berkecepatan tinggi penghasil cuan pasif."
                ),
                GameMinerItemEntity(
                    id = "miner_singularity_06",
                    name = "Quantum Singularity Core",
                    tier = "MYTHIC",
                    iconEmoji = "💎",
                    pricePoints = 2500,
                    powerGhs = 7500.0,
                    pointsPerMinute = 5.50,
                    isOwned = false,
                    isPlacedInRoom = false,
                    placedSlotIndex = -1,
                    description = "Pembangkit poin revolusioner dengan akselerasi partikel & super AI."
                )
            )
            dao.insertGameMinerItems(defaultMiners)

            val initialRoomState = GameRoomStateEntity(
                id = "default_room",
                unclaimedMiningPoints = 4.25,
                lastClaimTimestamp = System.currentTimeMillis() - 1800000L,
                totalMinedPointsClaimed = 25.0,
                tempPowerBonusGhs = 50.0,
                bonusExpiryTimestamp = System.currentTimeMillis() + 3600000L,
                miniGameHighScore = 120
            )
            dao.insertGameRoomState(initialRoomState)

            // Initial System Settings
            dao.insertSystemSettings(
                SystemSettingsEntity(
                    id = "default_config",
                    minWithdrawalEWallet = 50000.0,
                    minWithdrawalBank = 100000.0,
                    minWithdrawalCrypto = 250000.0,
                    freeTierCommissionRate = 12.0,
                    vipTierCommissionRate = 30.0,
                    pointsPerUsdRate = 100,
                    idrPerHundredPoints = 16000.0,
                    qrTransferFeePoints = 5,
                    qrTransferDailyLimitPoints = 5000,
                    antiFraudVelocitySeconds = 10,
                    isLocationTrackingEnabled = true,
                    autoApproveWithdrawalMaxAmount = 0.0,
                    maintenanceMode = false
                )
            )

            // Seed sample user location pins for provider analytics
            val now = System.currentTimeMillis()
            val sampleLocations = listOf(
                UserLocationLogEntity(
                    id = "loc_01",
                    userId = "user_001",
                    userName = "Hendra Wijaya",
                    userTier = "FREE",
                    latitude = -6.2088,
                    longitude = 106.8456,
                    cityName = "Jakarta Pusat",
                    provinceOrRegion = "DKI Jakarta",
                    accuracyMeters = 12.0f,
                    timestamp = now - 180000L,
                    activityStatus = "ACTIVE"
                ),
                UserLocationLogEntity(
                    id = "loc_02",
                    userId = "usr_siti_88",
                    userName = "Siti Rahma",
                    userTier = "PREMIUM",
                    latitude = -6.1754,
                    longitude = 106.8272,
                    cityName = "Jakarta Barat",
                    provinceOrRegion = "DKI Jakarta",
                    accuracyMeters = 8.5f,
                    timestamp = now - 360000L,
                    activityStatus = "ACTIVE"
                ),
                UserLocationLogEntity(
                    id = "loc_03",
                    userId = "usr_budi_99",
                    userName = "Budi Santoso",
                    userTier = "PREMIUM",
                    latitude = -6.9175,
                    longitude = 107.6191,
                    cityName = "Bandung",
                    provinceOrRegion = "Jawa Barat",
                    accuracyMeters = 15.0f,
                    timestamp = now - 900000L,
                    activityStatus = "ACTIVE"
                ),
                UserLocationLogEntity(
                    id = "loc_04",
                    userId = "usr_dewi_77",
                    userName = "Dewi Lestari",
                    userTier = "PREMIUM",
                    latitude = -7.2575,
                    longitude = 112.7521,
                    cityName = "Surabaya",
                    provinceOrRegion = "Jawa Timur",
                    accuracyMeters = 20.0f,
                    timestamp = now - 1800000L,
                    activityStatus = "ACTIVE"
                ),
                UserLocationLogEntity(
                    id = "loc_05",
                    userId = "usr_alex_55",
                    userName = "Alex Pratama",
                    userTier = "FREE",
                    latitude = -7.7956,
                    longitude = 110.3695,
                    cityName = "Yogyakarta",
                    provinceOrRegion = "DI Yogyakarta",
                    accuracyMeters = 10.0f,
                    timestamp = now - 2700000L,
                    activityStatus = "IDLE"
                ),
                UserLocationLogEntity(
                    id = "loc_06",
                    userId = "usr_eka_12",
                    userName = "Eka Saputra",
                    userTier = "FREE",
                    latitude = 3.5952,
                    longitude = 98.6722,
                    cityName = "Medan",
                    provinceOrRegion = "Sumatera Utara",
                    accuracyMeters = 25.0f,
                    timestamp = now - 3600000L * 2,
                    activityStatus = "ACTIVE"
                ),
                UserLocationLogEntity(
                    id = "loc_07",
                    userId = "usr_made_99",
                    userName = "I Made Arnawa",
                    userTier = "PREMIUM",
                    latitude = -8.6705,
                    longitude = 115.2126,
                    cityName = "Denpasar",
                    provinceOrRegion = "Bali",
                    accuracyMeters = 14.0f,
                    timestamp = now - 3600000L * 3,
                    activityStatus = "ACTIVE"
                ),
                UserLocationLogEntity(
                    id = "loc_08",
                    userId = "usr_rina_44",
                    userName = "Rina Marlina",
                    userTier = "FREE",
                    latitude = -5.1477,
                    longitude = 119.4327,
                    cityName = "Makassar",
                    provinceOrRegion = "Sulawesi Selatan",
                    accuracyMeters = 18.0f,
                    timestamp = now - 3600000L * 4,
                    activityStatus = "IDLE"
                ),
                UserLocationLogEntity(
                    id = "loc_09",
                    userId = "usr_agus_31",
                    userName = "Agus Haryanto",
                    userTier = "FREE",
                    latitude = -0.0263,
                    longitude = 109.3425,
                    cityName = "Pontianak",
                    provinceOrRegion = "Kalimantan Barat",
                    accuracyMeters = 30.0f,
                    timestamp = now - 3600000L * 6,
                    activityStatus = "OFFLINE"
                ),
                UserLocationLogEntity(
                    id = "loc_10",
                    userId = "usr_nita_50",
                    userName = "Nita Wijayanti",
                    userTier = "FREE",
                    latitude = -6.2415,
                    longitude = 106.9924,
                    cityName = "Bekasi",
                    provinceOrRegion = "Jawa Barat",
                    accuracyMeters = 11.0f,
                    timestamp = now - 450000L,
                    activityStatus = "ACTIVE"
                )
            )
            dao.insertLocationLogs(sampleLocations)

            // Default App Download Ads (Indonesia Sponsor Offers)
            val defaultAppDownloadAds = listOf(
                AppDownloadAdEntity(
                    id = "app_shopee",
                    appName = "Shopee Indonesia",
                    developer = "Shopee Official",
                    category = "Belanja & E-Commerce",
                    tagline = "Belanja Online No. 1 Gratis Ongkir",
                    description = "Install dan buka aplikasi Shopee, klaim voucher belanja gratis ongkir se-Indonesia dan dapatkan koin reward langsung.",
                    packageName = "com.shopee.id",
                    appSizeMb = 48,
                    rating = 4.8,
                    totalDownloads = "100JT+",
                    rewardCoins = 500,
                    rewardRupiah = 5000.0,
                    iconEmoji = "🛍️",
                    themeColorHex = 0xFFEE4D2D,
                    badgeLabel = "HOT DEAL"
                ),
                AppDownloadAdEntity(
                    id = "app_dana",
                    appName = "DANA Dompet Digital",
                    developer = "PT Espay Debit Indonesia",
                    category = "Finansial & E-Wallet",
                    tagline = "Bebas Transfer & Top Up Praktis",
                    description = "Download DANA sekarang. Nikmati kemudahan bayar QRIS, kirim saldo gratis biaya admin ke semua bank di Indonesia.",
                    packageName = "id.dana",
                    appSizeMb = 35,
                    rating = 4.7,
                    totalDownloads = "50JT+",
                    rewardCoins = 750,
                    rewardRupiah = 7500.0,
                    iconEmoji = "💙",
                    themeColorHex = 0xFF118EEA,
                    badgeLabel = "BONUS BESAR"
                ),
                AppDownloadAdEntity(
                    id = "app_tokopedia",
                    appName = "Tokopedia",
                    developer = "Tokopedia",
                    category = "Belanja Online",
                    tagline = "Kebutuhan Lengkap & Cashback WIB",
                    description = "Install aplikasi Tokopedia dan jelajahi promo Waktu Indonesia Belanja (WIB) dengan diskon hingga 90% setiap akhir bulan.",
                    packageName = "com.tokopedia.tkpd",
                    appSizeMb = 52,
                    rating = 4.8,
                    totalDownloads = "100JT+",
                    rewardCoins = 400,
                    rewardRupiah = 4000.0,
                    iconEmoji = "🟢",
                    themeColorHex = 0xFF03AC0E,
                    badgeLabel = "OFFICIAL"
                ),
                AppDownloadAdEntity(
                    id = "app_jago",
                    appName = "Bank Jago Digital",
                    developer = "PT Bank Jago Tbk",
                    category = "Perbankan Digital",
                    tagline = "Bunga Deposito Tinggi & Atur Kantong",
                    description = "Unduh aplikasi Bank Jago, buka rekening digital dalam hitungan menit tanpa ribet tanpa saldo mengendap.",
                    packageName = "com.jago.digitalbank",
                    appSizeMb = 28,
                    rating = 4.9,
                    totalDownloads = "10JT+",
                    rewardCoins = 1000,
                    rewardRupiah = 10000.0,
                    iconEmoji = "⭐",
                    themeColorHex = 0xFFFF7A00,
                    badgeLabel = "CUAN TERTINGGI"
                ),
                AppDownloadAdEntity(
                    id = "app_gopay",
                    appName = "GoPay App",
                    developer = "Gojek Indonesia",
                    category = "Keuangan & Pembayaran",
                    tagline = "Kirim Uang Gratis Transfer 100x",
                    description = "Aplikasi GoPay serba bisa untuk bayar tagihan, beli pulsa, dan transfer saldo tanpa ribet.",
                    packageName = "com.gojek.app",
                    appSizeMb = 42,
                    rating = 4.8,
                    totalDownloads = "50JT+",
                    rewardCoins = 600,
                    rewardRupiah = 6000.0,
                    iconEmoji = "💳",
                    themeColorHex = 0xFF00AED6,
                    badgeLabel = "RECOMMENDED"
                ),
                AppDownloadAdEntity(
                    id = "app_mlbb",
                    appName = "Mobile Legends: Bang Bang",
                    developer = "Moonton Games",
                    category = "Game MOBA",
                    tagline = "Main Bareng Teman & Raih Rank Mythic",
                    description = "Unduh dan mainkan game MOBA 5v5 terpopuler di Indonesia. Raih rank dan tantang temanmu sekarang!",
                    packageName = "com.mobile.legends",
                    appSizeMb = 120,
                    rating = 4.6,
                    totalDownloads = "500JT+",
                    rewardCoins = 850,
                    rewardRupiah = 8500.0,
                    iconEmoji = "⚔️",
                    themeColorHex = 0xFF3B82F6,
                    badgeLabel = "GAME POPULER"
                ),
                AppDownloadAdEntity(
                    id = "app_tiktok",
                    appName = "TikTok Lite",
                    developer = "TikTok Pte. Ltd.",
                    category = "Video & Hiburan",
                    tagline = "Nonton Video Pendek Hemat Kuota",
                    description = "Versi ringan TikTok, nonton jutaan konten video menarik dan dapatkan bonus tontonan setiap hari.",
                    packageName = "com.zhiliaoapp.musically.go",
                    appSizeMb = 18,
                    rating = 4.7,
                    totalDownloads = "100JT+",
                    rewardCoins = 350,
                    rewardRupiah = 3500.0,
                    iconEmoji = "🎵",
                    themeColorHex = 0xFF1E293B,
                    badgeLabel = "HEMAT KUOTA"
                )
            )
            dao.insertAppDownloadAds(defaultAppDownloadAds)
        }
    }
}
