package com.inkside.digital.data.repository

import com.inkside.digital.data.dao.AppDao
import com.inkside.digital.data.model.AffiliateLinkEntity
import com.inkside.digital.data.model.AppDownloadAdEntity
import com.inkside.digital.data.model.CampaignEntity
import com.inkside.digital.data.model.InvestmentCouponEntity
import com.inkside.digital.data.model.NotificationEntity
import com.inkside.digital.data.model.SystemSettingsEntity
import com.inkside.digital.data.model.TaskMissionEntity
import com.inkside.digital.data.model.TransactionEntity
import com.inkside.digital.data.model.UserEntity
import com.inkside.digital.data.model.UserLocationLogEntity
import com.inkside.digital.data.model.WithdrawalEntity
import com.inkside.digital.util.GlobalPointsManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.util.UUID

data class PeerContact(
    val id: String,
    val name: String,
    val username: String,
    val referralCode: String,
    val avatarEmoji: String,
    val role: String,
    val isFavorite: Boolean = false,
    val lastTransferredAt: Long = 0L
)

class AffiliateRepository(private val dao: AppDao) {

    private val transferMutex = Mutex()
    private var lastTransferTimestamp: Long = 0L

    private val _activeUserId = MutableStateFlow("user_001")
    val activeUserId: Flow<String> = _activeUserId.asStateFlow()

    fun switchUser(userId: String) {
        _activeUserId.value = userId
    }

    private val _contacts = MutableStateFlow<List<PeerContact>>(
        listOf(
            PeerContact("user_002", "Siti Rahmawati", "@siti_rtp", "SITI2024", "👩‍💼", "Affiliate Gold", isFavorite = true),
            PeerContact("user_003", "Budi Santoso", "@budi_affiliate", "BUDI99", "👨‍💻", "Top Referrer", isFavorite = true),
            PeerContact("user_004", "Dewi Lestari", "@dewi_vip", "DEWI77", "👑", "VIP Partner", isFavorite = true),
            PeerContact("user_005", "Rian Pratama", "@rian_pratama", "RIAN88", "⚡", "Member Aktif", isFavorite = false)
        )
    )
    val contacts: Flow<List<PeerContact>> = _contacts.asStateFlow()

    fun toggleFavoriteContact(contactId: String) {
        _contacts.value = _contacts.value.map { contact ->
            if (contact.id == contactId) {
                contact.copy(isFavorite = !contact.isFavorite)
            } else contact
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val user: Flow<UserEntity?> = _activeUserId.flatMapLatest { id -> dao.getUser(id) }

    val campaigns: Flow<List<CampaignEntity>> = dao.getAllCampaigns()
    val links: Flow<List<AffiliateLinkEntity>> = dao.getAllLinks()
    val withdrawals: Flow<List<WithdrawalEntity>> = dao.getAllWithdrawals()
    val pendingWithdrawals: Flow<List<WithdrawalEntity>> = dao.getPendingWithdrawals()
    val transactions: Flow<List<TransactionEntity>> = dao.getAllTransactions()
    val investmentCoupons: Flow<List<InvestmentCouponEntity>> = dao.getAllInvestmentCoupons()
    val notifications: Flow<List<NotificationEntity>> = dao.getAllNotifications()
    val unreadCount: Flow<Int> = dao.getUnreadNotificationCount()
    val allMissions: Flow<List<TaskMissionEntity>> = dao.getAllMissions()
    val allUsers: Flow<List<UserEntity>> = dao.getAllUsers()
    val locationLogs: Flow<List<UserLocationLogEntity>> = dao.getAllLocationLogs()
    val systemSettings: Flow<SystemSettingsEntity?> = dao.getSystemSettings()
    val appDownloadAds: Flow<List<AppDownloadAdEntity>> = dao.getAllAppDownloadAds()
    val gameMinerItems: Flow<List<com.inkside.digital.data.model.GameMinerItemEntity>> = dao.getAllGameMinerItems()
    val placedMinerItems: Flow<List<com.inkside.digital.data.model.GameMinerItemEntity>> = dao.getPlacedMinerItems()
    val gameRoomState: Flow<com.inkside.digital.data.model.GameRoomStateEntity?> = dao.getGameRoomState()

    suspend fun ensureDefaultUser() = withContext(Dispatchers.IO) {
        val current = dao.getUserSync()
        if (current == null) {
            dao.insertUser(
                UserEntity(
                    id = "user_001",
                    name = "Hendra Wijaya",
                    email = "hendra.affiliate@gmail.com",
                    phone = "+62 812-3456-7890",
                    tier = "FREE",
                    commissionRateMultiplier = 1.0,
                    balance = 3850000.0,
                    pendingBalance = 500000.0,
                    totalPaidOut = 8400000.0,
                    points = 2450,
                    is2FAEnabled = true,
                    regionZone = "ID",
                    referralCode = "PRO8892",
                    referredCount = 18,
                    referralEarnings = 540000.0
                )
            )
        }
    }

    suspend fun requestWithdrawal(
        amount: Double,
        currency: String,
        channelType: String,
        providerName: String,
        accountDestination: String,
        accountHolderName: String
    ): Result<WithdrawalEntity> = withContext(Dispatchers.IO) {
        val user = dao.getUserSync() ?: return@withContext Result.failure(Exception("Pengguna tidak ditemukan"))

        // Minimum threshold check
        val minLimit = when (channelType) {
            "E_WALLET" -> 50000.0
            "BANK" -> 100000.0
            "CRYPTO" -> 250000.0 // approx $16
            else -> 100000.0
        }

        if (amount < minLimit) {
            return@withContext Result.failure(Exception("Jumlah penarikan di bawah batas minimum (Min: Rp ${String.format("%,.0f", minLimit)})"))
        }

        if (amount > user.balance) {
            return@withContext Result.failure(Exception("Saldo komisi Anda tidak mencukupi"))
        }

        val fee = if (user.tier == "PREMIUM") 0.0 else when (channelType) {
            "CRYPTO" -> 15000.0
            "BANK" -> 4500.0
            else -> 2000.0
        }

        val netAmount = amount - fee
        val txId = "WD-" + UUID.randomUUID().toString().take(8).uppercase()
        val withdrawal = WithdrawalEntity(
            id = txId,
            userId = user.id,
            amount = amount,
            currency = currency,
            channelType = channelType,
            providerName = providerName,
            accountDestination = accountDestination,
            accountHolderName = accountHolderName,
            status = "PENDING",
            fee = fee,
            netAmount = netAmount,
            requestedAt = System.currentTimeMillis(),
            txRef = "REF-" + UUID.randomUUID().toString().take(10).uppercase(),
            adminNotes = "Permintaan baru, menunggu verifikasi manual admin"
        )

        // Deduct from available balance, add to pending balance
        val updatedUser = user.copy(
            balance = user.balance - amount,
            pendingBalance = user.pendingBalance + amount
        )
        dao.updateUser(updatedUser)
        dao.insertWithdrawal(withdrawal)

        // Ledger transaction
        dao.insertTransaction(
            TransactionEntity(
                id = "tx_" + UUID.randomUUID().toString().take(8),
                type = "WITHDRAWAL",
                title = "Pengajuan Penarikan Dana",
                description = "Ke $providerName ($accountDestination) - Menunggu Verifikasi Admin",
                amount = amount,
                isCredit = false,
                status = "PENDING",
                timestamp = System.currentTimeMillis(),
                referenceId = txId
            )
        )

        // Notification
        dao.insertNotification(
            NotificationEntity(
                id = "notif_" + UUID.randomUUID().toString().take(8),
                title = "Pengajuan Penarikan Terkirim",
                message = "Penarikan Rp ${String.format("%,.0f", amount)} ke $providerName sedang diverifikasi oleh admin.",
                type = "WITHDRAWAL",
                timestamp = System.currentTimeMillis()
            )
        )

        Result.success(withdrawal)
    }

    // Admin action: Approve & Mark Paid (manual payout validation)
    suspend fun adminApproveWithdrawal(withdrawalId: String, adminNotes: String = "Disetujui & telah ditransfer via Gateway") = withContext(Dispatchers.IO) {
        val user = dao.getUserSync() ?: return@withContext
        val withdrawals = dao.getAllWithdrawals()
        // Find withdrawal
        // Since we need to update this specific withdrawal:
        val wd = WithdrawalEntity(
            id = withdrawalId,
            userId = user.id,
            amount = 0.0,
            currency = "IDR",
            channelType = "BANK",
            providerName = "BCA",
            accountDestination = "",
            accountHolderName = "",
            status = "PAID",
            fee = 0.0,
            netAmount = 0.0,
            txRef = ""
        )
        // We'll update the withdrawal through standard DAO or query
    }

    suspend fun updateWithdrawalStatusByAdmin(
        withdrawal: WithdrawalEntity,
        newStatus: String, // PAID or REJECTED
        note: String
    ) = withContext(Dispatchers.IO) {
        val user = dao.getUserSync() ?: return@withContext
        val isApprove = newStatus == "PAID"

        val updatedWd = withdrawal.copy(
            status = newStatus,
            processedAt = System.currentTimeMillis(),
            adminNotes = note
        )
        dao.updateWithdrawal(updatedWd)

        if (isApprove) {
            // Money successfully sent out
            val updatedUser = user.copy(
                pendingBalance = (user.pendingBalance - withdrawal.amount).coerceAtLeast(0.0),
                totalPaidOut = user.totalPaidOut + withdrawal.amount
            )
            dao.updateUser(updatedUser)

            dao.insertNotification(
                NotificationEntity(
                    id = "notif_" + UUID.randomUUID().toString().take(8),
                    title = "Dana Telah Dicairkan! 💸",
                    message = "Admin telah memvalidasi dan mengirim Rp ${String.format("%,.0f", withdrawal.netAmount)} ke ${withdrawal.providerName} Anda.",
                    type = "WITHDRAWAL",
                    timestamp = System.currentTimeMillis()
                )
            )

            dao.insertTransaction(
                TransactionEntity(
                    id = "tx_" + UUID.randomUUID().toString().take(8),
                    type = "WITHDRAWAL",
                    title = "Pencairan Dana Selesai",
                    description = "Pembayaran manual admin tervalidasi via ${withdrawal.providerName}",
                    amount = withdrawal.netAmount,
                    isCredit = false,
                    status = "SUCCESS",
                    timestamp = System.currentTimeMillis(),
                    referenceId = withdrawal.id
                )
            )
        } else {
            // Rejected: return funds to available balance
            val updatedUser = user.copy(
                balance = user.balance + withdrawal.amount,
                pendingBalance = (user.pendingBalance - withdrawal.amount).coerceAtLeast(0.0)
            )
            dao.updateUser(updatedUser)

            dao.insertNotification(
                NotificationEntity(
                    id = "notif_" + UUID.randomUUID().toString().take(8),
                    title = "Penarikan Dana Ditolak Admin",
                    message = "Alasan: $note. Saldo sebesar Rp ${String.format("%,.0f", withdrawal.amount)} telah dikembalikan ke akun Anda.",
                    type = "WITHDRAWAL",
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }

    suspend fun createAffiliateLink(
        campaign: CampaignEntity,
        customSlug: String,
        subId: String
    ): AffiliateLinkEntity = withContext(Dispatchers.IO) {
        val cleanSlug = if (customSlug.isBlank()) "link-${UUID.randomUUID().toString().take(6)}" else customSlug.trim().replace(" ", "-").lowercase()
        val shortUrl = "https://affl.pro/s/$cleanSlug"
        val newLink = AffiliateLinkEntity(
            id = "link_" + UUID.randomUUID().toString().take(8),
            campaignId = campaign.id,
            campaignTitle = campaign.title,
            customSlug = cleanSlug,
            shortUrl = shortUrl,
            targetUrl = "https://partner.brand.com/track?aff=user001&subid=${subId.ifBlank { "app" }}",
            subId = subId.ifBlank { "default" },
            clicks = 0,
            conversions = 0,
            totalEarnings = 0.0,
            createdAt = System.currentTimeMillis()
        )
        dao.insertLink(newLink)
        dao.insertNotification(
            NotificationEntity(
                id = "notif_" + UUID.randomUUID().toString().take(8),
                title = "Tautan Baru Siap Dipromosikan",
                message = "Link promosi untuk ${campaign.title} berhasil dibuat: $shortUrl",
                type = "COMMISSION",
                timestamp = System.currentTimeMillis()
            )
        )
        newLink
    }

    suspend fun simulateLiveConversion(link: AffiliateLinkEntity) = withContext(Dispatchers.IO) {
        val user = dao.getUserSync() ?: return@withContext
        val baseCommission = 85000.0 * user.commissionRateMultiplier
        val isPremium = user.tier == "PREMIUM"
        val actualEarned = if (isPremium) baseCommission * 1.5 else baseCommission

        // Update link stats
        val updatedLink = link.copy(
            clicks = link.clicks + 3,
            conversions = link.conversions + 1,
            totalEarnings = link.totalEarnings + actualEarned
        )
        dao.updateLink(updatedLink)

        // Update user balance & points
        val pointsEarned = if (isPremium) 50 else 20
        val updatedUser = user.copy(
            balance = user.balance + actualEarned,
            points = user.points + pointsEarned
        )
        dao.updateUser(updatedUser)

        // Ledger
        dao.insertTransaction(
            TransactionEntity(
                id = "tx_" + UUID.randomUUID().toString().take(8),
                type = "COMMISSION",
                title = "Komisi Real-time Baru",
                description = "Konversi tervalidasi via link ${link.customSlug} (+$pointsEarned Poin)",
                amount = actualEarned,
                isCredit = true,
                status = "SUCCESS",
                timestamp = System.currentTimeMillis(),
                referenceId = link.id
            )
        )

        // Push Notification
        dao.insertNotification(
            NotificationEntity(
                id = "notif_" + UUID.randomUUID().toString().take(8),
                title = "🎉 Komisi Baru Masuk: +Rp ${String.format("%,.0f", actualEarned)}",
                message = "Ada konversi baru dari tautan '${link.campaignTitle}'. Saldo Anda langsung diperbarui!",
                type = "COMMISSION",
                timestamp = System.currentTimeMillis()
            )
        )
    }

    suspend fun upgradeToPremium(): Result<String> = withContext(Dispatchers.IO) {
        val user = dao.getUserSync() ?: return@withContext Result.failure(Exception("User tidak ditemukan"))
        val upgradeCost = 250000.0 // IDR

        if (user.balance < upgradeCost) {
            return@withContext Result.failure(Exception("Saldo tidak mencukupi untuk upgrade (Biaya: Rp 250.000). Silakan kumpulkan komisi atau tukar poin."))
        }

        val updatedUser = user.copy(
            tier = "PREMIUM",
            commissionRateMultiplier = 2.5,
            balance = user.balance - upgradeCost,
            points = user.points + 500
        )
        dao.updateUser(updatedUser)

        dao.insertTransaction(
            TransactionEntity(
                id = "tx_" + UUID.randomUUID().toString().take(8),
                type = "UPGRADE_FEE",
                title = "Upgrade VIP Premium Member",
                description = "Komisi 2.5x lebih tinggi, prioritas penarikan, & bonus 500 Poin",
                amount = upgradeCost,
                isCredit = false,
                status = "SUCCESS",
                timestamp = System.currentTimeMillis()
            )
        )

        dao.insertNotification(
            NotificationEntity(
                id = "notif_" + UUID.randomUUID().toString().take(8),
                title = "👑 Selamat! Akun Anda Resmi PREMIUM VIP",
                message = "Tingkat komisi Anda melonjak hingga 2.5x lebih besar dengan bebas biaya admin!",
                type = "REWARD",
                timestamp = System.currentTimeMillis()
            )
        )

        Result.success("Berhasil upgrade ke level Premium VIP!")
    }

    suspend fun redeemPointsToWallet(
        pointsToRedeem: Int,
        targetEwallet: String,
        phone: String,
        payoutCurrency: String = "USD"
    ): Result<Double> = withContext(Dispatchers.IO) {
        val user = dao.getUserSync() ?: return@withContext Result.failure(Exception("User not found"))
        if (pointsToRedeem > user.points) {
            return@withContext Result.failure(Exception("Poin tidak mencukupi"))
        }

        val usdValue = GlobalPointsManager.getUsdValue(pointsToRedeem)
        val idrEquivalent = GlobalPointsManager.getIdrValue(pointsToRedeem)
        val payoutAmount = GlobalPointsManager.getValueInCurrency(pointsToRedeem, payoutCurrency)
        val formattedPayout = GlobalPointsManager.formatCurrencyDisplay(payoutAmount, payoutCurrency)

        val updatedUser = user.copy(
            points = user.points - pointsToRedeem,
            balance = user.balance + idrEquivalent
        )
        dao.updateUser(updatedUser)

        dao.insertTransaction(
            TransactionEntity(
                id = "tx_" + UUID.randomUUID().toString().take(8),
                type = "POINT_REWARD",
                title = "Tukar Poin Global (Universal Rate)",
                description = "$pointsToRedeem Poin ditukar ke $targetEwallet ($phone) • Nilai: $formattedPayout ($${String.format(java.util.Locale.US, "%.2f", usdValue)} USD)",
                amount = idrEquivalent,
                isCredit = true,
                status = "SUCCESS",
                timestamp = System.currentTimeMillis()
            )
        )

        dao.insertNotification(
            NotificationEntity(
                id = "notif_" + UUID.randomUUID().toString().take(8),
                title = "Poin Global Berhasil Dicairkan! 🎁",
                message = "Penukaran $pointsToRedeem Poin berhasil! Bernilai $formattedPayout ($${String.format(java.util.Locale.US, "%.2f", usdValue)} USD) dikreditkan.",
                type = "REWARD",
                timestamp = System.currentTimeMillis()
            )
        )

        Result.success(idrEquivalent)
    }

    suspend fun buyInvestmentCoupon(coupon: InvestmentCouponEntity, quantity: Int = 1): Result<String> = withContext(Dispatchers.IO) {
        val user = dao.getUserSync() ?: return@withContext Result.failure(Exception("User not found"))
        val totalCost = coupon.unitPrice * quantity

        if (user.balance < totalCost) {
            return@withContext Result.failure(Exception("Saldo komisi Anda tidak mencukupi untuk membeli kupon investasi ini"))
        }

        val updatedUser = user.copy(
            balance = user.balance - totalCost,
            points = user.points + (quantity * 50)
        )
        dao.updateUser(updatedUser)

        val updatedCoupon = coupon.copy(
            ownedUnits = coupon.ownedUnits + quantity,
            totalUnitsAvailable = (coupon.totalUnitsAvailable - quantity).coerceAtLeast(0)
        )
        dao.updateInvestmentCoupon(updatedCoupon)

        dao.insertTransaction(
            TransactionEntity(
                id = "tx_" + UUID.randomUUID().toString().take(8),
                type = "COUPON_PURCHASE",
                title = "Beli Kupon Investasi Proyek",
                description = "${coupon.projectTitle} ($quantity unit @ Rp ${String.format("%,.0f", coupon.unitPrice)})",
                amount = totalCost,
                isCredit = false,
                status = "SUCCESS",
                timestamp = System.currentTimeMillis()
            )
        )

        dao.insertNotification(
            NotificationEntity(
                id = "notif_" + UUID.randomUUID().toString().take(8),
                title = "Kupon Investasi Berhasil Dimiliki 📈",
                message = "Anda kini memiliki ${updatedCoupon.ownedUnits} unit pada proyek '${coupon.projectTitle}' dengan estimasi return ${coupon.expectedApyPercent}% p.a.",
                type = "INVESTMENT",
                timestamp = System.currentTimeMillis()
            )
        )

        Result.success("Sukses membeli $quantity unit kupon investasi!")
    }

    suspend fun toggleListCouponSecondaryMarket(coupon: InvestmentCouponEntity, sellPrice: Double, isListing: Boolean) = withContext(Dispatchers.IO) {
        val updatedCoupon = coupon.copy(
            isListedOnSecondaryMarket = isListing,
            secondaryMarketPrice = if (isListing) sellPrice else 0.0,
            sellerNote = if (isListing) "Kupon aktif terverifikasi pasar sekunder" else ""
        )
        dao.updateInvestmentCoupon(updatedCoupon)

        val msg = if (isListing) "Kupon '${coupon.projectTitle}' berhasil didaftarkan ke Pasar Sekunder seharga Rp ${String.format("%,.0f", sellPrice)}" else "Kupon ditarik dari Pasar Sekunder"
        dao.insertNotification(
            NotificationEntity(
                id = "notif_" + UUID.randomUUID().toString().take(8),
                title = if (isListing) "Kupon Masuk Pasar Sekunder 🏷️" else "Kupon Batal Dijual",
                message = msg,
                type = "INVESTMENT",
                timestamp = System.currentTimeMillis()
            )
        )
    }

    suspend fun claimCouponYield(coupon: InvestmentCouponEntity): Result<Double> = withContext(Dispatchers.IO) {
        val user = dao.getUserSync() ?: return@withContext Result.failure(Exception("User not found"))
        if (coupon.ownedUnits <= 0) {
            return@withContext Result.failure(Exception("Anda belum memiliki kupon pada proyek ini"))
        }

        val dailyYield = (coupon.unitPrice * coupon.ownedUnits * (coupon.expectedApyPercent / 100.0)) / 365.0 * 7.0 // 7 days yield claim
        val yieldAmount = dailyYield.coerceAtLeast(15000.0)

        val updatedUser = user.copy(
            balance = user.balance + yieldAmount
        )
        dao.updateUser(updatedUser)

        val updatedCoupon = coupon.copy(
            accumulatedYield = coupon.accumulatedYield + yieldAmount,
            lastYieldClaimAt = System.currentTimeMillis()
        )
        dao.updateInvestmentCoupon(updatedCoupon)

        dao.insertTransaction(
            TransactionEntity(
                id = "tx_" + UUID.randomUUID().toString().take(8),
                type = "COUPON_YIELD",
                title = "Klaim Hasil Return Kupon",
                description = "Yield investasi proyek ${coupon.projectTitle}",
                amount = yieldAmount,
                isCredit = true,
                status = "SUCCESS",
                timestamp = System.currentTimeMillis()
            )
        )

        dao.insertNotification(
            NotificationEntity(
                id = "notif_" + UUID.randomUUID().toString().take(8),
                title = "Dividen Hasil Investasi Diterima! 💵",
                message = "+Rp ${String.format("%,.0f", yieldAmount)} hasil dividen kupon '${coupon.projectTitle}' langsung masuk ke saldo Anda.",
                type = "INVESTMENT",
                timestamp = System.currentTimeMillis()
            )
        )

        Result.success(yieldAmount)
    }

    suspend fun earnAdPoints(): Int = withContext(Dispatchers.IO) {
        val user = dao.getUserSync() ?: return@withContext 0
        val bonus = 50
        val updated = user.copy(points = user.points + bonus)
        dao.updateUser(updated)

        dao.insertTransaction(
            TransactionEntity(
                id = "tx_" + UUID.randomUUID().toString().take(8),
                type = "POINT_REWARD",
                title = "Bonus Reward Iklan Sponsor",
                description = "Menyelesaikan tayangan iklan video sponsor pengembang",
                amount = bonus.toDouble(),
                currency = "POIN",
                isCredit = true,
                status = "SUCCESS",
                timestamp = System.currentTimeMillis()
            )
        )

        dao.insertNotification(
            NotificationEntity(
                id = "notif_" + UUID.randomUUID().toString().take(8),
                title = "Poin Iklan Berhasil Didapat! 📺",
                message = "Selamat! +50 Poin aktivitas berhasil ditambahkan ke akun Anda.",
                type = "REWARD",
                timestamp = System.currentTimeMillis()
            )
        )

        bonus
    }

    suspend fun updateUserProfile(
        name: String,
        email: String,
        phone: String,
        regionZone: String
    ) = withContext(Dispatchers.IO) {
        val user = dao.getUserSync() ?: return@withContext
        val updated = user.copy(
            name = name,
            email = email,
            phone = phone,
            regionZone = regionZone
        )
        dao.updateUser(updated)
    }

    suspend fun updateUserLocation(
        lat: Double,
        lng: Double,
        cityName: String,
        province: String,
        accuracyMeters: Float = 10f
    ) = withContext(Dispatchers.IO) {
        val user = dao.getUserSync() ?: return@withContext
        val now = System.currentTimeMillis()
        val updatedUser = user.copy(
            latitude = lat,
            longitude = lng,
            locationCity = cityName,
            locationProvince = province,
            lastLocationUpdate = now
        )
        dao.updateUser(updatedUser)

        val log = UserLocationLogEntity(
            id = "loc_" + UUID.randomUUID().toString().take(8),
            userId = user.id,
            userName = user.name,
            userTier = user.tier,
            latitude = lat,
            longitude = lng,
            cityName = cityName,
            provinceOrRegion = province,
            accuracyMeters = accuracyMeters,
            timestamp = now,
            activityStatus = "ACTIVE"
        )
        dao.insertLocationLog(log)
    }

    suspend fun toggleUserLocationTracking(allowed: Boolean) = withContext(Dispatchers.IO) {
        val user = dao.getUserSync() ?: return@withContext
        dao.updateUser(user.copy(isLocationTrackingAllowed = allowed))
    }

    suspend fun saveSystemSettings(settings: SystemSettingsEntity) = withContext(Dispatchers.IO) {
        dao.insertSystemSettings(settings.copy(updatedTimestamp = System.currentTimeMillis()))
        dao.insertNotification(
            NotificationEntity(
                id = "notif_" + UUID.randomUUID().toString().take(8),
                title = "Pengaturan Sistem Diperbarui ⚙️",
                message = "Konfigurasi platform berhasil disinkronisasi oleh Admin Pusat.",
                type = "SYSTEM",
                timestamp = System.currentTimeMillis()
            )
        )
    }

    suspend fun recordSimulatedProviderPing(
        userId: String,
        userName: String,
        userTier: String,
        lat: Double,
        lng: Double,
        city: String,
        province: String
    ) = withContext(Dispatchers.IO) {
        val log = UserLocationLogEntity(
            id = "loc_" + UUID.randomUUID().toString().take(8),
            userId = userId,
            userName = userName,
            userTier = userTier,
            latitude = lat,
            longitude = lng,
            cityName = city,
            provinceOrRegion = province,
            timestamp = System.currentTimeMillis(),
            activityStatus = "ACTIVE"
        )
        dao.insertLocationLog(log)
    }

    suspend fun toggle2FA(enable: Boolean) = withContext(Dispatchers.IO) {
        val user = dao.getUserSync() ?: return@withContext
        val updated = user.copy(is2FAEnabled = enable)
        dao.updateUser(updated)

        dao.insertNotification(
            NotificationEntity(
                id = "notif_" + UUID.randomUUID().toString().take(8),
                title = if (enable) "Autentikasi 2FA Diaktifkan 🔒" else "2FA Dinonaktifkan ⚠️",
                message = if (enable) "Akun Anda sekarang dilindungi 2-Factor Authentication dan enkripsi end-to-end." else "2FA telah dimatikan.",
                type = "SECURITY",
                timestamp = System.currentTimeMillis()
            )
        )
    }

    suspend fun markAllNotificationsAsRead() = withContext(Dispatchers.IO) {
        dao.markAllNotificationsAsRead()
    }

    suspend fun performDailyCheckIn(): Result<Pair<Int, Int>> = withContext(Dispatchers.IO) {
        val user = dao.getUserSync() ?: return@withContext Result.failure(Exception("User tidak ditemukan"))
        val now = System.currentTimeMillis()
        val todayEpochDay = now / 86400000L
        val lastEpochDay = user.lastCheckInDate / 86400000L

        if (user.lastCheckInDate > 0 && todayEpochDay == lastEpochDay) {
            return@withContext Result.failure(Exception("Anda sudah check-in hari ini! Kembali lagi besok untuk mempertahankan streak."))
        }

        val newStreak = if (user.lastCheckInDate > 0 && todayEpochDay - lastEpochDay == 1L) {
            (user.checkInStreak % 7) + 1
        } else {
            1
        }

        val rewardRtp = GlobalPointsManager.getLoginRewardForStreak(newStreak)
        val updatedUser = user.copy(
            points = user.points + rewardRtp,
            checkInStreak = newStreak,
            lastCheckInDate = now
        )
        dao.updateUser(updatedUser)

        // Mark daily login mission as completed & ready to claim if present
        val loginMission = dao.getMissionById("task_daily_login")
        if (loginMission != null) {
            dao.updateMission(
                loginMission.copy(
                    currentProgress = 1,
                    isCompleted = true
                )
            )
        }

        dao.insertTransaction(
            TransactionEntity(
                id = "tx_" + UUID.randomUUID().toString().take(8),
                type = "DAILY_CHECKIN",
                title = "Check-in Harian Royaltree (Streak Hari ke-$newStreak) 🌟",
                description = "Bonus login harian +$rewardRtp RTP berhasil dikreditkan ke saldo poin.",
                amount = GlobalPointsManager.getIdrValue(rewardRtp),
                isCredit = true,
                status = "SUCCESS",
                timestamp = now
            )
        )

        dao.insertNotification(
            NotificationEntity(
                id = "notif_" + UUID.randomUUID().toString().take(8),
                title = "Check-in Berhasil! +$rewardRtp RTP 🎁",
                message = "Selamat! Anda telah check-in beruntun hari ke-$newStreak di Royaltree.",
                type = "REWARD",
                timestamp = now
            )
        )

        Result.success(Pair(rewardRtp, newStreak))
    }

    suspend fun claimMissionReward(missionId: String): Result<Int> = withContext(Dispatchers.IO) {
        val user = dao.getUserSync() ?: return@withContext Result.failure(Exception("User tidak ditemukan"))
        val mission = dao.getMissionById(missionId) ?: return@withContext Result.failure(Exception("Misi tidak ditemukan"))

        if (!mission.isCompleted) {
            return@withContext Result.failure(Exception("Misi belum selesai"))
        }
        if (mission.isClaimed) {
            return@withContext Result.failure(Exception("Hadiah misi ini sudah pernah diklaim"))
        }

        val updatedMission = mission.copy(isClaimed = true)
        dao.updateMission(updatedMission)

        val updatedUser = user.copy(points = user.points + mission.rtpReward)
        dao.updateUser(updatedUser)

        val now = System.currentTimeMillis()
        dao.insertTransaction(
            TransactionEntity(
                id = "tx_" + UUID.randomUUID().toString().take(8),
                type = "MISSION_REWARD",
                title = "Klaim Misi Royaltree: ${mission.title}",
                description = "Hadiah penyelesaian misi +${mission.rtpReward} RTP",
                amount = GlobalPointsManager.getIdrValue(mission.rtpReward),
                isCredit = true,
                status = "SUCCESS",
                timestamp = now
            )
        )

        dao.insertNotification(
            NotificationEntity(
                id = "notif_" + UUID.randomUUID().toString().take(8),
                title = "Hadiah Misi Diklaim! +${mission.rtpReward} RTP 🏆",
                message = "Misi '${mission.title}' berhasil diselesaikan dan ${mission.rtpReward} RTP telah ditambahkan!",
                type = "REWARD",
                timestamp = now
            )
        )

        Result.success(mission.rtpReward)
    }

    suspend fun completeTaskAction(missionId: String): Result<String> = withContext(Dispatchers.IO) {
        val mission = dao.getMissionById(missionId) ?: return@withContext Result.failure(Exception("Tugas tidak ditemukan"))
        val newProgress = (mission.currentProgress + 1).coerceAtMost(mission.maxProgress)
        val isNowCompleted = newProgress >= mission.maxProgress
        val updated = mission.copy(
            currentProgress = newProgress,
            isCompleted = isNowCompleted
        )
        dao.updateMission(updated)
        Result.success(if (isNowCompleted) "Tugas selesai! Silakan klaim reward RTP." else "Progress bertambah: $newProgress/${mission.maxProgress}")
    }

    suspend fun transferPoints(
        recipientIdentifier: String,
        recipientName: String,
        pointsAmount: Int,
        note: String = ""
    ): Result<TransferResult> = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()

        // 1. Anti-Bot / Flood Rate Limiting (Velocity Check)
        if (now - lastTransferTimestamp < GlobalPointsManager.TRANSFER_COOLDOWN_MS) {
            val waitSec = ((GlobalPointsManager.TRANSFER_COOLDOWN_MS - (now - lastTransferTimestamp)) / 1000) + 1
            return@withContext Result.failure(Exception("Proteksi Anti-Spam: Tunggu $waitSec detik sebelum transaksi berikutnya."))
        }

        // 2. Strict Input Sanitization & Bounds
        val cleanId = GlobalPointsManager.sanitizeIdentifier(recipientIdentifier)
        if (cleanId.length < 3) {
            return@withContext Result.failure(Exception("ID atau kode penerima tidak valid."))
        }

        val minTransfer = GlobalPointsManager.MIN_TRANSFER_RTP
        if (pointsAmount < minTransfer) {
            return@withContext Result.failure(Exception("Minimum transfer adalah $minTransfer RTP."))
        }

        val maxTransfer = GlobalPointsManager.MAX_TRANSFER_PER_TX_RTP
        if (pointsAmount > maxTransfer) {
            return@withContext Result.failure(Exception("Jumlah melebihi batas proteksi per transaksi (Maksimal $maxTransfer RTP)."))
        }

        // 3. Thread-Safe Mutex Lock (Anti Double-Spending & Race Condition Guard)
        transferMutex.withLock {
            val currentId = _activeUserId.value
            val user = dao.getUserSync(currentId) ?: return@withLock Result.failure(Exception("Pengguna tidak ditemukan."))

            // Anti Self-Transfer (Check by ID, Referral Code, and Email)
            if (cleanId.equals(user.id, ignoreCase = true) ||
                cleanId.equals(user.referralCode, ignoreCase = true) ||
                cleanId.equals(user.email, ignoreCase = true)
            ) {
                return@withLock Result.failure(Exception("Pencegahan Fraud: Tidak dapat mentransfer ke akun Anda sendiri."))
            }

            // Atomic Balance Verification inside Mutex Lock
            if (pointsAmount > user.points) {
                return@withLock Result.failure(Exception("Saldo RTP tidak mencukupi (Saldo Anda: ${user.points} RTP)."))
            }

            val adminFee = GlobalPointsManager.TRANSFER_ADMIN_FEE_RTP
            val netReceived = GlobalPointsManager.calculateNetReceived(pointsAmount)
            val txId = "tx_trf_" + UUID.randomUUID().toString().take(8)

            // 4. Cryptographic Security Token Generation (SHA-256 Signature)
            val signature = GlobalPointsManager.generateSecuritySignature(
                txId = txId,
                senderId = user.id,
                recipientId = cleanId,
                amount = pointsAmount,
                timestamp = now
            )

            // Deduct sender's points (fee is deducted from recipient's balance)
            val updatedUser = user.copy(points = user.points - pointsAmount)
            dao.updateUser(updatedUser)

            // Real Mutual Transfer: Credit recipient user in database if registered
            val allUsers = dao.getAllUsersSync()
            val cleanPhone = cleanId.replace(" ", "").replace("-", "")
            val recipientUser = allUsers.find {
                it.id.equals(cleanId, ignoreCase = true) ||
                it.referralCode.equals(cleanId, ignoreCase = true) ||
                it.phone.replace(" ", "").replace("-", "") == cleanPhone
            }
            if (recipientUser != null) {
                val updatedRecipient = recipientUser.copy(
                    points = recipientUser.points + netReceived,
                    balance = recipientUser.balance + GlobalPointsManager.getIdrValue(netReceived)
                )
                dao.updateUser(updatedRecipient)

                dao.insertTransaction(
                    TransactionEntity(
                        id = "tx_in_" + UUID.randomUUID().toString().take(8),
                        type = "POINT_TRANSFER_IN",
                        title = "Terima Transfer RTP dari ${user.name}",
                        description = "Menerima $netReceived RTP dari ${user.name} (${user.referralCode}). ${if (note.isNotBlank()) "Pesan: $note" else ""}",
                        amount = GlobalPointsManager.getIdrValue(netReceived),
                        currency = "IDR",
                        isCredit = true,
                        status = "SUCCESS",
                        timestamp = now,
                        referenceId = "${user.id}#$signature"
                    )
                )

                dao.insertNotification(
                    NotificationEntity(
                        id = "notif_" + UUID.randomUUID().toString().take(8),
                        title = "Transfer Masuk Diterima! 📥",
                        message = "Kamu menerima transfer $netReceived RTP dari ${user.name}.",
                        type = "REWARD",
                        timestamp = now
                    )
                )
            }

            // Record transaction with cryptographic reference token
            dao.insertTransaction(
                TransactionEntity(
                    id = txId,
                    type = "POINT_TRANSFER_OUT",
                    title = "Transfer RTP ke $recipientName",
                    description = "Kirim $pointsAmount RTP (Biaya admin $adminFee RTP dipotong dr saldo penerima, bersih: $netReceived RTP). [Sig: $signature] ${if (note.isNotBlank()) "Catatan: $note" else ""}",
                    amount = GlobalPointsManager.getIdrValue(pointsAmount),
                    currency = "IDR",
                    isCredit = false,
                    status = "SUCCESS",
                    timestamp = now,
                    referenceId = "$cleanId#$signature"
                )
            )

            // Send notification
            dao.insertNotification(
                NotificationEntity(
                    id = "notif_" + UUID.randomUUID().toString().take(8),
                    title = "Transfer RTP Berhasil Dikirim 📤",
                    message = "Berhasil transfer $pointsAmount RTP ke $recipientName ($cleanId). Bersih diterima: $netReceived RTP (Biaya admin $adminFee RTP). Sig: $signature",
                    type = "REWARD",
                    timestamp = now
                )
            )

            // Update velocity timestamp
            lastTransferTimestamp = now

            // Update or add recipient to Recent Contacts
            val currentList = _contacts.value.toMutableList()
            val existingIndex = currentList.indexOfFirst { it.id == cleanId || it.referralCode.equals(cleanId, ignoreCase = true) }
            if (existingIndex >= 0) {
                val existing = currentList[existingIndex]
                currentList[existingIndex] = existing.copy(lastTransferredAt = now)
            } else {
                currentList.add(
                    0,
                    PeerContact(
                        id = cleanId,
                        name = recipientName.ifBlank { "Pengguna $cleanId" },
                        username = "@$cleanId",
                        referralCode = cleanId,
                        avatarEmoji = "👤",
                        role = "Kontak Transfer",
                        isFavorite = false,
                        lastTransferredAt = now
                    )
                )
            }
            _contacts.value = currentList

            Result.success(
                TransferResult(
                    transferId = txId,
                    senderPointsRemaining = updatedUser.points,
                    sentPoints = pointsAmount,
                    adminFee = adminFee,
                    netPointsReceived = netReceived,
                    recipientName = recipientName,
                    recipientIdentifier = cleanId,
                    timestamp = now,
                    securitySignature = signature,
                    note = note
                )
            )
        }
    }

    // Step Counter & Pedometer conversion
    suspend fun addSteps(steps: Int) = withContext(Dispatchers.IO) {
        val currentId = _activeUserId.value
        val user = dao.getUserSync(currentId) ?: return@withContext
        val newTodaySteps = user.todaySteps + steps
        val newUnclaimedSteps = user.unclaimedSteps + steps
        dao.updateUser(user.copy(
            todaySteps = newTodaySteps,
            unclaimedSteps = newUnclaimedSteps,
            lastStepTimestamp = System.currentTimeMillis()
        ))
    }

    suspend fun convertStepsToCoins(): Result<Pair<Int, Double>> = withContext(Dispatchers.IO) {
        val currentId = _activeUserId.value
        val user = dao.getUserSync(currentId) ?: return@withContext Result.failure(Exception("Pengguna tidak ditemukan"))
        if (user.unclaimedSteps < 100) {
            return@withContext Result.failure(Exception("Langkah belum cukup untuk ditukar (Minimal 100 langkah = 10 Koin)."))
        }
        val stepsToConvert = (user.unclaimedSteps / 100) * 100
        val coinsEarned = (stepsToConvert / 100) * 10
        val rupiahBonus = coinsEarned * 10.0 // 10 Koin = Rp 100

        val updatedUser = user.copy(
            unclaimedSteps = user.unclaimedSteps - stepsToConvert,
            convertedStepsToday = user.convertedStepsToday + stepsToConvert,
            points = user.points + coinsEarned,
            balance = user.balance + rupiahBonus
        )
        dao.updateUser(updatedUser)

        dao.insertTransaction(
            TransactionEntity(
                id = "tx_step_" + UUID.randomUUID().toString().take(8),
                type = "POINT_REWARD",
                title = "Tukar Langkah Jadi Koin 👟",
                description = "Berhasil menukar $stepsToConvert langkah kaki menjadi $coinsEarned Koin (Rp ${String.format("%,.0f", rupiahBonus)})",
                amount = rupiahBonus,
                currency = "IDR",
                isCredit = true,
                status = "SUCCESS",
                timestamp = System.currentTimeMillis()
            )
        )

        dao.insertNotification(
            NotificationEntity(
                id = "notif_" + UUID.randomUUID().toString().take(8),
                title = "Langkah Ditukar Koin! 👟🎉",
                message = "Hebat! Kamu berhasil menukar $stepsToConvert langkah menjadi $coinsEarned Koin (+Rp ${String.format("%,.0f", rupiahBonus)}). Tetap aktif berjalan!",
                type = "REWARD",
                timestamp = System.currentTimeMillis()
            )
        )

        Result.success(Pair(coinsEarned, rupiahBonus))
    }

    // App Download Ads (Offers)
    suspend fun simulateDownloadApp(adId: String) = withContext(Dispatchers.IO) {
        val ad = dao.getAppDownloadAdById(adId) ?: return@withContext
        dao.updateAppDownloadAd(ad.copy(
            isDownloading = false,
            downloadProgressPercent = 100,
            isInstalled = true
        ))
    }

    suspend fun claimAppDownloadReward(adId: String): Result<Pair<Int, Double>> = withContext(Dispatchers.IO) {
        val ad = dao.getAppDownloadAdById(adId) ?: return@withContext Result.failure(Exception("Aplikasi tidak ditemukan"))
        if (!ad.isInstalled) {
            return@withContext Result.failure(Exception("Aplikasi belum diunduh."))
        }
        if (ad.isRewardClaimed) {
            return@withContext Result.failure(Exception("Hadiah koin aplikasi ini sudah pernah diklaim."))
        }

        val currentId = _activeUserId.value
        val user = dao.getUserSync(currentId) ?: return@withContext Result.failure(Exception("Pengguna tidak ditemukan"))

        dao.updateAppDownloadAd(ad.copy(isRewardClaimed = true, isDownloading = false))
        dao.updateUser(user.copy(
            points = user.points + ad.rewardCoins,
            balance = user.balance + ad.rewardRupiah
        ))

        dao.insertTransaction(
            TransactionEntity(
                id = "tx_app_" + UUID.randomUUID().toString().take(8),
                type = "POINT_REWARD",
                title = "Reward Unduh ${ad.appName}",
                description = "Misi instalasi ${ad.appName} selesai. +${ad.rewardCoins} Koin (+Rp ${String.format("%,.0f", ad.rewardRupiah)})",
                amount = ad.rewardRupiah,
                currency = "IDR",
                isCredit = true,
                status = "SUCCESS",
                timestamp = System.currentTimeMillis()
            )
        )

        dao.insertNotification(
            NotificationEntity(
                id = "notif_" + UUID.randomUUID().toString().take(8),
                title = "Klaim Reward Unduh Sukses! 📲🎁",
                message = "Kamu berhasil mendapatkan +${ad.rewardCoins} Koin (Rp ${String.format("%,.0f", ad.rewardRupiah)}) dari instalasi ${ad.appName}.",
                type = "REWARD",
                timestamp = System.currentTimeMillis()
            )
        )

        Result.success(Pair(ad.rewardCoins, ad.rewardRupiah))
    }

    suspend fun claimBannerAdBonus(bannerId: String, sponsorName: String, coins: Int): Result<Int> = withContext(Dispatchers.IO) {
        val currentId = _activeUserId.value
        val user = dao.getUserSync(currentId) ?: return@withContext Result.failure(Exception("Pengguna tidak ditemukan"))
        val rupiah = coins * 10.0
        dao.updateUser(user.copy(
            points = user.points + coins,
            balance = user.balance + rupiah
        ))

        dao.insertTransaction(
            TransactionEntity(
                id = "tx_ad_" + UUID.randomUUID().toString().take(8),
                type = "POINT_REWARD",
                title = "Bonus Interaksi Sponsor: $sponsorName",
                description = "Melihat penawaran promo sponsor $sponsorName. Diberikan +$coins Koin gratis.",
                amount = rupiah,
                currency = "IDR",
                isCredit = true,
                status = "SUCCESS",
                timestamp = System.currentTimeMillis()
            )
        )
        Result.success(coins)
    }

    suspend fun claimGameMiningPoints(): Result<Double> = withContext(Dispatchers.IO) {
        val currentId = _activeUserId.value
        val user = dao.getUserSync(currentId) ?: return@withContext Result.failure(Exception("Pengguna tidak ditemukan"))
        val roomState = dao.getGameRoomStateSync() ?: com.inkside.digital.data.model.GameRoomStateEntity()
        val placedMiners = dao.getPlacedMinerItemsSync()

        val now = System.currentTimeMillis()
        val elapsedMinutes = ((now - roomState.lastClaimTimestamp) / 60000.0).coerceAtLeast(0.0)
        val ratePerMinute = placedMiners.sumOf { it.pointsPerMinute }
        val generatedSinceLast = elapsedMinutes * ratePerMinute
        val totalPending = roomState.unclaimedMiningPoints + generatedSinceLast

        if (totalPending < 0.1 && placedMiners.isEmpty()) {
            return@withContext Result.failure(Exception("Belum ada hasil mining. Pasang item miner di rak untuk mulai menghasilkan poin!"))
        }

        val pointsAwarded = if (totalPending < 1.0) 1 else kotlin.math.round(totalPending).toInt().coerceAtLeast(1)

        val updatedUser = user.copy(points = user.points + pointsAwarded)
        dao.updateUser(updatedUser)

        val updatedRoomState = roomState.copy(
            unclaimedMiningPoints = 0.0,
            lastClaimTimestamp = now,
            totalMinedPointsClaimed = roomState.totalMinedPointsClaimed + pointsAwarded
        )
        dao.insertGameRoomState(updatedRoomState)

        dao.insertTransaction(
            TransactionEntity(
                id = "tx_game_" + UUID.randomUUID().toString().take(8),
                type = "POINT_REWARD",
                title = "Klaim Hasil Pasif Ruang Game 🎮",
                description = "Panen +$pointsAwarded Poin dari ${placedMiners.size} item penambang aktif di rak RollerCoin",
                amount = pointsAwarded.toDouble(),
                currency = "POIN",
                isCredit = true,
                status = "SUCCESS",
                timestamp = now
            )
        )

        dao.insertNotification(
            NotificationEntity(
                id = "notif_game_" + UUID.randomUUID().toString().take(8),
                title = "Poin Mining Ruang Game Diklaim! ⛏️",
                message = "Selamat! +$pointsAwarded Poin berhasil dipanen dari generator rak RollerCoin Anda.",
                type = "REWARD",
                timestamp = now
            )
        )

        val claimMission = dao.getMissionById("ms_claim_mining_points")
        if (claimMission != null && !claimMission.isCompleted) {
            dao.updateMission(claimMission.copy(currentProgress = 1, isCompleted = true))
        }

        Result.success(pointsAwarded.toDouble())
    }

    suspend fun buyGameMinerItem(itemId: String): Result<String> = withContext(Dispatchers.IO) {
        val currentId = _activeUserId.value
        val user = dao.getUserSync(currentId) ?: return@withContext Result.failure(Exception("Pengguna tidak ditemukan"))
        val item = dao.getGameMinerItemById(itemId) ?: return@withContext Result.failure(Exception("Item tidak ditemukan"))

        if (item.isOwned) {
            return@withContext Result.failure(Exception("Anda sudah memiliki item ini. Pasang item ini di rak ruang game."))
        }

        if (user.points < item.pricePoints) {
            val shortage = item.pricePoints - user.points
            return@withContext Result.failure(Exception("Poin tidak cukup! Anda memiliki ${user.points} Poin (Kurang $shortage Poin). Selesaikan misi Like, Subscribe, Nonton & Web untuk kumpulkan poin!"))
        }

        val placedMiners = dao.getPlacedMinerItemsSync()
        val occupiedSlots = placedMiners.map { it.placedSlotIndex }.toSet()
        val firstEmptySlot = (0..5).firstOrNull { it !in occupiedSlots }

        val updatedItem = if (firstEmptySlot != null) {
            item.copy(isOwned = true, isPlacedInRoom = true, placedSlotIndex = firstEmptySlot)
        } else {
            item.copy(isOwned = true, isPlacedInRoom = false, placedSlotIndex = -1)
        }

        val updatedUser = user.copy(points = user.points - item.pricePoints)
        dao.updateUser(updatedUser)
        dao.updateGameMinerItem(updatedItem)

        val now = System.currentTimeMillis()
        dao.insertTransaction(
            TransactionEntity(
                id = "tx_buy_" + UUID.randomUUID().toString().take(8),
                type = "POINT_REWARD",
                title = "Beli Item Penambang: ${item.name}",
                description = "Pembelian item penambang daya ${item.powerGhs} GH/s seharga ${item.pricePoints} Poin",
                amount = item.pricePoints.toDouble(),
                currency = "POIN",
                isCredit = false,
                status = "SUCCESS",
                timestamp = now
            )
        )

        dao.insertNotification(
            NotificationEntity(
                id = "notif_buy_" + UUID.randomUUID().toString().take(8),
                title = "Item Baru Terpasang! 🖥️",
                message = "Item '${item.name}' berhasil dibeli dan ${if (firstEmptySlot != null) "langsung aktif di Slot ${firstEmptySlot + 1}!" else "tersimpan di inventaris rak."}",
                type = "REWARD",
                timestamp = now
            )
        )

        val placeMission = dao.getMissionById("ms_place_miner_item")
        if (placeMission != null && !placeMission.isCompleted) {
            dao.updateMission(placeMission.copy(currentProgress = 1, isCompleted = true))
        }

        val successMsg = if (firstEmptySlot != null) {
            "Sukses beli ${item.name}! Langsung terpasang di Rak Slot ${firstEmptySlot + 1} (+${item.powerGhs} GH/s) 🎉"
        } else {
            "Sukses beli ${item.name}! Rak penuh, item disimpan di inventaris."
        }
        Result.success(successMsg)
    }

    suspend fun toggleMinerSlot(itemId: String, targetSlot: Int): Result<String> = withContext(Dispatchers.IO) {
        val item = dao.getGameMinerItemById(itemId) ?: return@withContext Result.failure(Exception("Item tidak ditemukan"))
        if (!item.isOwned) {
            return@withContext Result.failure(Exception("Anda belum memiliki item ini. Beli item terlebih dahulu di Toko Item."))
        }

        if (item.isPlacedInRoom && item.placedSlotIndex == targetSlot) {
            dao.updateGameMinerItem(item.copy(isPlacedInRoom = false, placedSlotIndex = -1))
            return@withContext Result.success("Item '${item.name}' dilepas dari rak dan kembali ke inventaris.")
        } else {
            val placedMiners = dao.getPlacedMinerItemsSync()
            val existingInSlot = placedMiners.find { it.placedSlotIndex == targetSlot }
            if (existingInSlot != null) {
                dao.updateGameMinerItem(existingInSlot.copy(isPlacedInRoom = false, placedSlotIndex = -1))
            }
            dao.updateGameMinerItem(item.copy(isPlacedInRoom = true, placedSlotIndex = targetSlot))
            return@withContext Result.success("Item '${item.name}' berhasil dipasang di Rak Slot ${targetSlot + 1}!")
        }
    }

    suspend fun playMiniGameFinish(score: Int): Result<Pair<Int, Double>> = withContext(Dispatchers.IO) {
        val currentId = _activeUserId.value
        val user = dao.getUserSync(currentId) ?: return@withContext Result.failure(Exception("Pengguna tidak ditemukan"))
        val bonusPoints = (score / 10).coerceIn(15, 60)
        val boostGhs = 80.0
        val now = System.currentTimeMillis()

        val updatedUser = user.copy(points = user.points + bonusPoints)
        dao.updateUser(updatedUser)

        val roomState = dao.getGameRoomStateSync() ?: com.inkside.digital.data.model.GameRoomStateEntity()
        val updatedRoomState = roomState.copy(
            tempPowerBonusGhs = roomState.tempPowerBonusGhs + boostGhs,
            bonusExpiryTimestamp = now + 7200000L,
            miniGameHighScore = maxOf(roomState.miniGameHighScore, score)
        )
        dao.insertGameRoomState(updatedRoomState)

        dao.insertTransaction(
            TransactionEntity(
                id = "tx_game_reward_" + UUID.randomUUID().toString().take(8),
                type = "POINT_REWARD",
                title = "Reward Mini-Game RollerCoin 🎯",
                description = "Skor $score: +$bonusPoints Poin & Boost Mining Power +$boostGhs GH/s (2 Jam)",
                amount = bonusPoints.toDouble(),
                currency = "POIN",
                isCredit = true,
                status = "SUCCESS",
                timestamp = now
            )
        )

        val gameMission = dao.getMissionById("ms_play_roller_game")
        if (gameMission != null && !gameMission.isCompleted) {
            dao.updateMission(gameMission.copy(currentProgress = 1, isCompleted = true))
        }

        Result.success(Pair(bonusPoints, boostGhs))
    }
}

data class TransferResult(
    val transferId: String,
    val senderPointsRemaining: Int,
    val sentPoints: Int,
    val adminFee: Int,
    val netPointsReceived: Int,
    val recipientName: String,
    val recipientIdentifier: String,
    val timestamp: Long,
    val securitySignature: String = "",
    val note: String = ""
)

