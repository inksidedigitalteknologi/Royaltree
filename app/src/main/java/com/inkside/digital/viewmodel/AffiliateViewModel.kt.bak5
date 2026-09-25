package com.inkside.digital.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.inkside.digital.data.database.AppDatabase
import com.inkside.digital.data.model.AffiliateLinkEntity
import com.inkside.digital.data.model.CampaignEntity
import com.inkside.digital.data.model.TaskMissionEntity
import com.inkside.digital.data.model.InvestmentCouponEntity
import com.inkside.digital.data.model.NotificationEntity
import com.inkside.digital.data.model.SystemSettingsEntity
import com.inkside.digital.data.model.TransactionEntity
import com.inkside.digital.data.model.UserEntity
import com.inkside.digital.data.model.UserLocationLogEntity
import com.inkside.digital.data.model.WithdrawalEntity
import com.inkside.digital.data.network.ApiClient
import com.inkside.digital.data.repository.AffiliateRepository
import com.inkside.digital.data.repository.PeerContact
import com.inkside.digital.data.repository.TransferResult
import com.inkside.digital.localization.AppLanguage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.json.JSONObject
import com.google.firebase.auth.FirebaseAuth

enum class AppScreen {
    HOME,
    STEP_COUNTER,
    APP_OFFERS,
    CAMPAIGNS,
    MISSIONS,
    GAME_ROOM,
    ANALYTICS,
    WITHDRAW,
    INVEST,
    HISTORY,
    ADMIN,
    REFERRAL,
    SETTINGS,
    FAQ,
    INBOX,
    DAILY_CHECK_IN,
    PROFILE,
    NOTIFICATIONS
}

class AffiliateViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: AffiliateRepository

    init {
        val database = AppDatabase.getDatabase(application, viewModelScope)
        repository = AffiliateRepository(database.appDao())
        // Tidak ada seed data dummy — user di-load dari backend.
        // Set active user ID dari Firebase Auth
        viewModelScope.launch {
            val firebaseUid = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid
            if (firebaseUid != null) {
                repository.switchUser(firebaseUid)
                loadUserFromBackend()
            }
        }
        com.inkside.digital.data.network.ApiClient.init(application)
    }

    /**
     * Panggil ulang untuk refresh user dari Firebase + backend.
     * Dipakai saat app dibuka dari background.
     */
    fun syncFirebaseUser() {
        viewModelScope.launch {
            val firebaseUid = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid
            if (firebaseUid != null) {
                repository.switchUser(firebaseUid)
                loadUserFromBackend()
            }
        }
    }

    // Portal Integration States
    val portalBaseUrl = MutableStateFlow(com.inkside.digital.data.network.ApiClient.getStoredBaseUrl(application))
    val portalApiKey = MutableStateFlow(com.inkside.digital.data.network.ApiClient.getStoredApiKey(application))
    val portalConnectionStatus = MutableStateFlow("IDLE") // IDLE, TESTING, ONLINE, ERROR
    val portalStatusMessage = MutableStateFlow("Belum diuji")
    val isSyncingWithPortal = MutableStateFlow(false)

    fun testPortalConnection(url: String, apiKey: String) {
        viewModelScope.launch {
            portalConnectionStatus.value = "TESTING"
            portalStatusMessage.value = "Menghubungkan ke $url..."
            val result = com.inkside.digital.data.network.ApiClient.testConnection(url)
            result.onSuccess { res ->
                portalConnectionStatus.value = "ONLINE"
                portalStatusMessage.value = "Tersambung (HTTP 200 OK) - ${res.message}"
                showSnackbar("Berhasil terhubung ke portal Anda! 🟢")
            }.onFailure { err ->
                portalConnectionStatus.value = "ERROR"
                portalStatusMessage.value = "Gagal: ${err.localizedMessage ?: "Tidak dapat menjangkau server"}"
                showSnackbar("Koneksi gagal: ${err.message} 🔴")
            }
        }
    }

    fun savePortalConfiguration(url: String, apiKey: String) {
        com.inkside.digital.data.network.ApiClient.updateBaseUrl(getApplication(), url, apiKey)
        portalBaseUrl.value = com.inkside.digital.data.network.ApiClient.getStoredBaseUrl(getApplication())
        portalApiKey.value = com.inkside.digital.data.network.ApiClient.getStoredApiKey(getApplication())
        showSnackbar("Konfigurasi URL & API Key Portal berhasil disimpan!")
    }

    fun syncDataWithPortal() {
        val currentUser = user.value ?: return
        viewModelScope.launch {
            isSyncingWithPortal.value = true
            showSnackbar("Memulai sinkronisasi data ke portal...")
            val req = com.inkside.digital.data.network.model.ApiUserSyncRequest(
                id = currentUser.id,
                name = currentUser.name,
                email = currentUser.email,
                phone = currentUser.phone,
                tier = currentUser.tier,
                balance = currentUser.balance,
                points = currentUser.points,
                todaySteps = currentUser.todaySteps,
                referralCode = currentUser.referralCode,
                city = currentUser.locationCity
            )
            val result = com.inkside.digital.data.network.ApiClient.syncUserToPortal(req)
            isSyncingWithPortal.value = false
            result.onSuccess { res ->
                showSnackbar("Sinkronisasi sukses! Data terkirim ke portal. ✓")
            }.onFailure { err ->
                showSnackbar("Sinkronisasi gagal: ${err.message} (Data tetap tersimpan offline)")
            }
        }
    }

    // Reactive data flows
    val user: StateFlow<UserEntity?> = repository.user
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val campaigns: StateFlow<List<CampaignEntity>> = repository.campaigns
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val links: StateFlow<List<AffiliateLinkEntity>> = repository.links
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val withdrawals: StateFlow<List<WithdrawalEntity>> = repository.withdrawals
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val pendingWithdrawals: StateFlow<List<WithdrawalEntity>> = repository.pendingWithdrawals
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val transactions: StateFlow<List<TransactionEntity>> = repository.transactions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val investmentCoupons: StateFlow<List<InvestmentCouponEntity>> = repository.investmentCoupons
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notifications: StateFlow<List<NotificationEntity>> = repository.notifications
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val unreadNotifCount: StateFlow<Int> = repository.unreadCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val missions: StateFlow<List<TaskMissionEntity>> = repository.allMissions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val contacts: StateFlow<List<PeerContact>> = repository.contacts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allUsers: StateFlow<List<UserEntity>> = repository.allUsers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val locationLogs: StateFlow<List<UserLocationLogEntity>> = repository.locationLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val systemSettings: StateFlow<SystemSettingsEntity?> = repository.systemSettings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val appDownloadAds: StateFlow<List<com.inkside.digital.data.model.AppDownloadAdEntity>> = repository.appDownloadAds
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val gameMinerItems: StateFlow<List<com.inkside.digital.data.model.GameMinerItemEntity>> = repository.gameMinerItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val placedMinerItems: StateFlow<List<com.inkside.digital.data.model.GameMinerItemEntity>> = repository.placedMinerItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val gameRoomState: StateFlow<com.inkside.digital.data.model.GameRoomStateEntity?> = repository.gameRoomState
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val activeUserId: StateFlow<String> = repository.activeUserId
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    fun switchUser(userId: String) {
        repository.switchUser(userId)
        val targetUser = allUsers.value.find { it.id == userId }
        _snackbarMessage.value = "Beralih akun: ${targetUser?.name ?: userId}"
    }

    fun addSteps(steps: Int) {
        viewModelScope.launch {
            repository.addSteps(steps)
            val currentUser = user.value
            if (currentUser != null) {
                ApiClient.syncStepsToPortal(currentUser.id, steps)
            }
        }
    }

    fun convertStepsToCoins(onComplete: ((Boolean, String) -> Unit)? = null) {
        viewModelScope.launch {
            val result = repository.convertStepsToCoins()
            if (result.isSuccess) {
                val (coins, rupiah) = result.getOrThrow()
                _snackbarMessage.value = "Sukses menukar langkah! +$coins Koin (Rp ${String.format("%,.0f", rupiah)})"
                onComplete?.invoke(true, "+$coins Koin")
            } else {
                val msg = result.exceptionOrNull()?.message ?: "Gagal menukar langkah"
                _snackbarMessage.value = msg
                onComplete?.invoke(false, msg)
            }
        }
    }

    fun downloadApp(adId: String) {
        viewModelScope.launch {
            repository.simulateDownloadApp(adId)
            _snackbarMessage.value = "Aplikasi berhasil diunduh & dipasang! Klaim bonus sekarang."
        }
    }

    fun claimAppDownloadReward(adId: String, onComplete: ((Boolean, String) -> Unit)? = null) {
        viewModelScope.launch {
            val result = repository.claimAppDownloadReward(adId)
            if (result.isSuccess) {
                val (coins, rupiah) = result.getOrThrow()
                _snackbarMessage.value = "Klaim berhasil! +$coins Koin (+Rp ${String.format("%,.0f", rupiah)})"
                onComplete?.invoke(true, "+$coins Koin")
            } else {
                val msg = result.exceptionOrNull()?.message ?: "Gagal klaim hadiah"
                _snackbarMessage.value = msg
                onComplete?.invoke(false, msg)
            }
        }
    }

    fun claimBannerAdBonus(bannerId: String, sponsorName: String, coins: Int) {
        viewModelScope.launch {
            val result = repository.claimBannerAdBonus(bannerId, sponsorName, coins)
            if (result.isSuccess) {
                _snackbarMessage.value = "Bonus sponsor $sponsorName! +$coins Koin didapatkan 🎉"
            }
        }
    }

    fun toggleFavoriteContact(contactId: String) {
        repository.toggleFavoriteContact(contactId)
    }

    // Navigation and UI State
    private val _currentScreen = MutableStateFlow(AppScreen.HOME)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val _isAdminMode = MutableStateFlow(false)
    val isAdminMode: StateFlow<Boolean> = _isAdminMode.asStateFlow()

    private val _currentLanguage = MutableStateFlow(
        com.inkside.digital.data.preferences.AppThemePreferences.currentLanguage
    )
    val currentLanguage: StateFlow<AppLanguage> = _currentLanguage.asStateFlow()

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    // Dialog & Flow States
    val showWithdrawModal = MutableStateFlow(false)
    val showNewLinkModal = MutableStateFlow(false)
    val showUpgradeModal = MutableStateFlow(false)
    val showRedeemPointsModal = MutableStateFlow(false)
    val showMissionsModal = MutableStateFlow(false)
    val showDailyCheckInModal = MutableStateFlow(false)
    val showQrTransferModal = MutableStateFlow(false)
    val transferSuccessReceipt = MutableStateFlow<TransferResult?>(null)
    val showBuyCouponModal = MutableStateFlow<InvestmentCouponEntity?>(null)
    val showListCouponModal = MutableStateFlow<InvestmentCouponEntity?>(null)
    val showAdminReviewModal = MutableStateFlow<WithdrawalEntity?>(null)
    val showAdRewardModal = MutableStateFlow(false)
    val show2FAModal = MutableStateFlow(false)
    val showPaymentGatewaySimulator = MutableStateFlow<WithdrawalEntity?>(null)
    val selectedCampaignForLink = MutableStateFlow<CampaignEntity?>(null)

    fun navigateTo(screen: AppScreen) {
        _currentScreen.value = screen
    }

    fun setAdminMode(isAdmin: Boolean) {
        _isAdminMode.value = isAdmin
        if (isAdmin) {
            _currentScreen.value = AppScreen.ADMIN
        } else if (_currentScreen.value == AppScreen.ADMIN) {
            _currentScreen.value = AppScreen.HOME
        }
    }

    fun setLanguage(lang: AppLanguage) {
        _currentLanguage.value = lang
        // Simpan ke SharedPreferences biar persist
        val ctx = getApplication<android.app.Application>()
        com.inkside.digital.data.preferences.AppThemePreferences.setLanguage(ctx, lang)
        showSnackbar("Bahasa diubah ke ${lang.displayName} ${lang.flag}")
    }

    fun showSnackbar(message: String) {
        _snackbarMessage.value = message
    }

    fun dismissSnackbar() {
        _snackbarMessage.value = null
    }

    // Actions
    fun requestWithdrawal(
        amount: Double,
        currency: String,
        channelType: String,
        providerName: String,
        accountDest: String,
        accountHolder: String
    ) {
        viewModelScope.launch {
            val result = repository.requestWithdrawal(
                amount = amount,
                currency = currency,
                channelType = channelType,
                providerName = providerName,
                accountDestination = accountDest,
                accountHolderName = accountHolder
            )
            result.onSuccess { withdrawal ->
                showWithdrawModal.value = false
                showPaymentGatewaySimulator.value = withdrawal
                showSnackbar("Pengajuan penarikan dana berhasil dikirim ke Admin!")
            }.onFailure { error ->
                showSnackbar("Gagal: ${error.message}")
            }
        }
    }

    fun adminProcessWithdrawal(withdrawal: WithdrawalEntity, isApprove: Boolean, note: String) {
        viewModelScope.launch {
            val newStatus = if (isApprove) "PAID" else "REJECTED"
            repository.updateWithdrawalStatusByAdmin(withdrawal, newStatus, note)
            showAdminReviewModal.value = null
            showSnackbar(if (isApprove) "Penarikan berhasil disetujui & dicairkan manual!" else "Penarikan telah ditolak dan saldo dikembalikan.")
        }
    }

    fun createLink(campaign: CampaignEntity, customSlug: String, subId: String) {
        viewModelScope.launch {
            val created = repository.createAffiliateLink(campaign, customSlug, subId)
            showNewLinkModal.value = false
            selectedCampaignForLink.value = null
            showSnackbar("Tautan affiliasi siap: ${created.shortUrl}")
        }
    }

    fun simulateConversion(link: AffiliateLinkEntity) {
        viewModelScope.launch {
            repository.simulateLiveConversion(link)
            showSnackbar("⚡ Konversi real-time berhasil! Komisi & Poin masuk.")
        }
    }

    fun upgradeToPremium() {
        viewModelScope.launch {
            val result = repository.upgradeToPremium()
            result.onSuccess {
                showUpgradeModal.value = false
                showSnackbar("👑 Selamat! Akun Anda kini berstatus VIP Premium.")
            }.onFailure {
                showSnackbar(it.message ?: "Upgrade gagal")
            }
        }
    }

    fun redeemPoints(points: Int, walletName: String, phone: String, currency: String = "USD") {
        viewModelScope.launch {
            val res = repository.redeemPointsToWallet(points, walletName, phone, currency)
            res.onSuccess { earned ->
                showRedeemPointsModal.value = false
                showSnackbar("Sukses tukar poin global! +Rp ${String.format("%,.0f", earned)} masuk ke saldo.")
            }.onFailure {
                showSnackbar(it.message ?: "Gagal tukar poin")
            }
        }
    }

    fun buyCoupon(coupon: InvestmentCouponEntity, quantity: Int) {
        viewModelScope.launch {
            val res = repository.buyInvestmentCoupon(coupon, quantity)
            res.onSuccess {
                showBuyCouponModal.value = null
                showSnackbar("Kupon investasi berhasil dibeli! Cek portofolio Anda.")
            }.onFailure {
                showSnackbar(it.message ?: "Gagal membeli kupon")
            }
        }
    }

    fun toggleListingSecondaryMarket(coupon: InvestmentCouponEntity, price: Double, isListing: Boolean) {
        viewModelScope.launch {
            repository.toggleListCouponSecondaryMarket(coupon, price, isListing)
            showListCouponModal.value = null
            showSnackbar(if (isListing) "Kupon aktif didaftarkan di Pasar Sekunder!" else "Kupon ditarik dari Pasar Sekunder.")
        }
    }

    fun claimYield(coupon: InvestmentCouponEntity) {
        viewModelScope.launch {
            val res = repository.claimCouponYield(coupon)
            res.onSuccess { amount ->
                showSnackbar("Hasil return dividen +Rp ${String.format("%,.0f", amount)} dicairkan ke saldo!")
            }.onFailure {
                showSnackbar(it.message ?: "Gagal klaim yield")
            }
        }
    }

    fun claimAdReward() {
        viewModelScope.launch {
            val points = repository.earnAdPoints()
            showAdRewardModal.value = false
            showSnackbar("Terima kasih telah menonton! +$points Poin aktivitas didapatkan.")
        }
    }

    fun claimGameMiningPoints() {
        viewModelScope.launch {
            val result = repository.claimGameMiningPoints()
            result.onSuccess { points ->
                showSnackbar("Panen Berhasil! +${points.toInt()} Poin masuk ke saldo Anda 🎉")
            }.onFailure { error ->
                showSnackbar(error.message ?: "Gagal mengklaim hasil mining")
            }
        }
    }

    fun buyGameMinerItem(itemId: String) {
        viewModelScope.launch {
            val result = repository.buyGameMinerItem(itemId)
            result.onSuccess { msg ->
                showSnackbar(msg)
            }.onFailure { error ->
                showSnackbar(error.message ?: "Gagal membeli item penambang")
            }
        }
    }

    fun toggleMinerSlot(itemId: String, targetSlot: Int) {
        viewModelScope.launch {
            val result = repository.toggleMinerSlot(itemId, targetSlot)
            result.onSuccess { msg ->
                showSnackbar(msg)
            }.onFailure { error ->
                showSnackbar(error.message ?: "Gagal memindahkan item")
            }
        }
    }

    fun finishMiniGame(score: Int) {
        viewModelScope.launch {
            val result = repository.playMiniGameFinish(score)
            result.onSuccess { (bonusPoints, boostGhs) ->
                showSnackbar("Game Selesai! Skor: $score (+${bonusPoints} Poin & Power Boost +${boostGhs.toInt()} GH/s) 🎮")
            }.onFailure { error ->
                showSnackbar(error.message ?: "Gagal memproses reward game")
            }
        }
    }

    fun updateProfile(name: String, email: String, phone: String, region: String) {
        viewModelScope.launch {
            val currentUser = user.value
            if (currentUser == null) {
                showSnackbar("User tidak ditemukan")
                return@launch
            }

            // 1. Update Room DB (biar UI langsung berubah)
            repository.updateUserProfile(name, email, phone, region)

            // 2. Kirim ke backend
            val body = mapOf(
                "name" to name,
                "email" to email,
                "phone" to phone,
                "city" to region
            )

            ApiClient.updateProfile(currentUser.id, body)
                .onSuccess { response ->
                    if (response.success) {
                        // Update Room dengan data dari backend
                        repository.updateUserFromBackend(
                            userId = currentUser.id,
                            name = name,
                            email = email,
                            tier = currentUser.tier,
                            balance = currentUser.balance,
                            points = currentUser.points,
                            todaySteps = currentUser.todaySteps
                        )
                        showSnackbar("✅ Profil berhasil disimpan")
                    } else {
                        showSnackbar("❌ Gagal: " + (response.message ?: "Unknown error"))
                    }
                }
                .onFailure { error ->
                    showSnackbar("❌ Gagal simpan: " + (error.message ?: "Network error"))
                }
        }
    }

    fun toggle2FA(enable: Boolean) {
        viewModelScope.launch {
            repository.toggle2FA(enable)
            show2FAModal.value = false
            showSnackbar(if (enable) "2-Factor Authentication Aktif 🔒" else "2FA Dinonaktifkan")
        }
    }

    fun markAllNotificationsRead() {
        viewModelScope.launch {
            repository.markAllNotificationsAsRead()
            showSnackbar("Semua notifikasi ditandai telah dibaca.")
        }
    }

    fun performDailyCheckIn() {
        viewModelScope.launch {
            val res = repository.performDailyCheckIn()
            res.onSuccess { (reward, streak) ->
                showDailyCheckInModal.value = false
                showSnackbar("Check-in Hari ke-$streak Berhasil! +$reward RTP 🌟")
            }.onFailure {
                showSnackbar(it.message ?: "Gagal check-in")
            }
        }
    }

    fun claimMissionReward(missionId: String) {
        viewModelScope.launch {
            val res = repository.claimMissionReward(missionId)
            res.onSuccess { reward ->
                showSnackbar("Berhasil klaim hadiah misi! +$reward RTP 🏆")
            }.onFailure {
                showSnackbar(it.message ?: "Gagal klaim hadiah misi")
            }
        }
    }

    fun completeTaskAction(missionId: String) {
        viewModelScope.launch {
            val res = repository.completeTaskAction(missionId)
            res.onSuccess { msg ->
                showSnackbar(msg)
            }.onFailure {
                showSnackbar(it.message ?: "Gagal memproses tugas")
            }
        }
    }

    fun transferPoints(
        recipientId: String,
        recipientName: String,
        amount: Int,
        note: String
    ) {
        viewModelScope.launch {
            val res = repository.transferPoints(recipientId, recipientName, amount, note)
            res.onSuccess { result ->
                transferSuccessReceipt.value = result
                showSnackbar("Berhasil transfer ${result.sentPoints} RTP ke ${result.recipientName}!")
            }.onFailure { err ->
                showSnackbar("Transfer gagal: ${err.message}")
            }
        }
    }

    fun dismissTransferReceipt() {
        transferSuccessReceipt.value = null
    }

    fun updateUserLocation(lat: Double, lng: Double, city: String, province: String, accuracyMeters: Float = 10f) {
        viewModelScope.launch {
            repository.updateUserLocation(lat, lng, city, province, accuracyMeters)
            showSnackbar("Lokasi diperbarui: $city, $province 📍")
        }
    }

    fun toggleLocationTracking(allowed: Boolean) {
        viewModelScope.launch {
            repository.toggleUserLocationTracking(allowed)
            showSnackbar(if (allowed) "Pelacakan lokasi aktif untuk analitik provider." else "Pelacakan lokasi dinonaktifkan.")
        }
    }

    fun saveSystemSettings(settings: SystemSettingsEntity) {
        viewModelScope.launch {
            repository.saveSystemSettings(settings)
            showSnackbar("Pengaturan sistem berhasil disimpan & disinkronkan!")
        }
    }

    fun addProviderSimulatedPin(
        userId: String,
        userName: String,
        tier: String,
        lat: Double,
        lng: Double,
        city: String,
        province: String
    ) {
        viewModelScope.launch {
            repository.recordSimulatedProviderPing(userId, userName, tier, lat, lng, city, province)
            showSnackbar("Pin pengguna baru ditambahkan di $city!")
        }
    }

    // ============ HISTORY & NOTIFICATIONS ============
    private val _historyItems = MutableStateFlow<List<com.inkside.digital.data.network.model.HistoryItem>>(emptyList())
    val historyItems: StateFlow<List<com.inkside.digital.data.network.model.HistoryItem>> = _historyItems.asStateFlow()

    private val _notificationItems = MutableStateFlow<List<com.inkside.digital.data.network.model.NotificationItem>>(emptyList())
    val notificationItems: StateFlow<List<com.inkside.digital.data.network.model.NotificationItem>> = _notificationItems.asStateFlow()

    private val _unreadCountRemote = MutableStateFlow(0)
    val unreadCountRemote: StateFlow<Int> = _unreadCountRemote.asStateFlow()

    fun loadHistory() {
        viewModelScope.launch {
            val currentUser = user.value ?: return@launch
            ApiClient.getHistory(currentUser.id).onSuccess { response ->
                if (response.success) _historyItems.value = response.data
            }.onFailure { /* offline, pakai data lokal */ }
        }
    }

    fun loadNotifications() {
        viewModelScope.launch {
            val currentUser = user.value ?: return@launch
            ApiClient.getNotifications(currentUser.id).onSuccess { response ->
                if (response.success) _notificationItems.value = response.data
            }.onFailure { /* offline */ }
            ApiClient.getUnreadCount(currentUser.id).onSuccess { response ->
                if (response.success) _unreadCountRemote.value = response.unread
            }
        }
    }

    fun refreshUserFromBackend() {
        viewModelScope.launch {
            val currentUser = user.value ?: return@launch
            ApiClient.getProfile(currentUser.id).onSuccess { response ->
                response.data?.let { profile ->
                    repository.updateUserFromBackend(
                        userId = profile.id,
                        name = profile.name,
                        email = profile.email,
                        tier = profile.tier,
                        balance = profile.balance,
                        points = profile.points,
                        todaySteps = profile.todaySteps
                    )

            }
        }
    }
    }

    // ============ SYNC DARI BACKEND ============
    fun loadCampaignsFromBackend() {
        viewModelScope.launch {
            ApiClient.getCampaigns().onSuccess { response ->
                if (response.success) {
                    response.data.forEach { item ->
                        repository.syncCampaignsFromBackend(listOf(
                            CampaignEntity(
                                id = item.id, title = item.title, category = item.category,
                                merchantName = item.merchantName,
                                commissionDisplay = item.commissionDisplay,
                                payoutType = "CPS", baseCommissionRate = item.baseCommissionRate,
                                clicksCount = 0, conversionsCount = 0, totalEarned = 0.0,
                                status = item.status
                            )
                        ))
                    }
                }
            }
        }
    }

    fun loadMissionsFromBackend() {
        viewModelScope.launch {
            ApiClient.getMissions().onSuccess { response ->
                if (response.success) {
                    response.data.forEach { item ->
                        repository.syncMissionsFromBackend(listOf(
                            TaskMissionEntity(
                                id = item.id, title = item.title, description = "",
                                rtpReward = item.rtpReward, type = "ENGAGEMENT_MISSION",
                                category = item.category, currentProgress = 0, maxProgress = 1,
                                isCompleted = false, isClaimed = false, iconKey = "star",
                                targetPlatform = item.targetPlatform,
                                durationSeconds = item.durationSeconds,
                                actionUrl = item.actionUrl
                            )
                        ))
                    }
                }
            }
        }
    }

    fun loadWithdrawalsFromBackend() {
        viewModelScope.launch {
            ApiClient.getWithdrawals().onSuccess { response ->
                if (response.success) {
                    response.data.forEach { wd ->
                        repository.syncWithdrawalsFromBackend(listOf(
                            WithdrawalEntity(
                                id = wd.id, userId = wd.userId, amount = wd.amount,
                                currency = wd.currency, channelType = wd.channelType,
                                providerName = wd.providerName,
                                accountDestination = wd.accountDestination,
                                accountHolderName = "",
                                status = wd.status, fee = wd.fee, netAmount = wd.netAmount,
                                txRef = ""
                            )
                        ))
                    }
                }
            }
        }
    }

    fun loadAnalyticsFromBackend() {
        viewModelScope.launch {
            val currentUser = user.value ?: return@launch
            ApiClient.getAnalyticsSummary(currentUser.id).onSuccess { response ->
                response.data?.let { data ->
                    repository.updateUserFromBackend(
                        userId = data.userId, name = "", email = "",
                        tier = data.tier, balance = data.balance,
                        points = data.points, todaySteps = data.todaySteps
                    )
                }
            }
        }
    }

    fun loadProfileFromBackend() {
        viewModelScope.launch {
            val currentUser = user.value ?: return@launch
            ApiClient.getProfile(currentUser.id).onSuccess { response ->
                response.data?.let { profile ->
                    repository.updateUserFromBackend(
                        userId = profile.id, name = profile.name, email = profile.email,
                        tier = profile.tier, balance = profile.balance,
                        points = profile.points, todaySteps = profile.todaySteps
                    )
                }
            }
        }
    }

    fun spinWheelFromBackend() {
        viewModelScope.launch {
            val currentUser = user.value ?: return@launch
            ApiClient.spinWheel(currentUser.id).onSuccess { response ->
                if (response.success) {
                    response.data?.let { spin ->
                        val current = user.value ?: return@launch
                        repository.updateUserFromBackend(
                            userId = current.id, name = "", email = "",
                            tier = current.tier, balance = current.balance,
                            points = current.points + spin.pointsWon,
                            todaySteps = current.todaySteps
                        )
                    }
                }
            }
        }
    }

    // ============ LOAD DARI BACKEND (Firestore) ============

    /**
     * Load user dari backend berdasarkan Firebase UID.
     * Data disimpan ke Room DB -> UI auto-update.
     */
    fun loadUserFromBackend() {
        viewModelScope.launch {
            try {
                val firebaseUid = FirebaseAuth.getInstance().currentUser?.uid
                if (firebaseUid == null) {
                    android.util.Log.w("AffiliateViewModel", "Firebase UID null — belum login")
                    return@launch
                }

                // Pastikan active user ID = Firebase UID
                repository.switchUser(firebaseUid)

                // Ambil dari backend
                val result = ApiClient.getMyProfile()
                result.onSuccess { json ->
                    try {
                        if (!json.optBoolean("success", false)) {
                            android.util.Log.w("AffiliateViewModel", "Backend: user tidak ditemukan")
                            return@onSuccess
                        }
                        val data = json.optJSONObject("data") ?: return@onSuccess

                        repository.updateUserFromBackend(
                            userId = firebaseUid,
                            name = data.optString("name", ""),
                            email = data.optString("email", ""),
                            tier = data.optString("tier", "FREE"),
                            balance = data.optDouble("balance", 0.0),
                            points = data.optInt("points", 0),
                            todaySteps = data.optInt("todaySteps", 0)
                        )
                        android.util.Log.d("AffiliateViewModel", "✅ User loaded from backend")
                    } catch (e: Exception) {
                        android.util.Log.e("AffiliateViewModel", "Parse user error: ${e.message}")
                    }
                }.onFailure {
                    android.util.Log.w("AffiliateViewModel", "Gagal load user: ${it.message}")
                }
            } catch (e: Exception) {
                android.util.Log.e("AffiliateViewModel", "loadUserFromBackend error: ${e.message}")
            }
        }
    }

    /**
     * Sync user ke backend (kalau belum ada di Firestore).
     */
    fun syncUserToBackend() {
        viewModelScope.launch {
            try {
                val firebaseUser = FirebaseAuth.getInstance().currentUser ?: return@launch
                val body = org.json.JSONObject().apply {
                    put("name", firebaseUser.displayName ?: "User Baru")
                }
                ApiClient.syncFirebaseUser(body).onSuccess {
                    android.util.Log.d("AffiliateViewModel", "✅ User synced to backend")
                    loadUserFromBackend()
                }
            } catch (e: Exception) {
                android.util.Log.e("AffiliateViewModel", "syncUserToBackend error: ${e.message}")
            }
        }
    }

    /**
     * Load campaigns dari backend.
     */
    fun loadCampaignsFromBackendV2() {
        viewModelScope.launch {
            ApiClient.getCampaigns().onSuccess { response ->
                if (response.success) {
                    val entities = response.data.map { item ->
                        com.inkside.digital.data.model.CampaignEntity(
                            id = item.id,
                            title = item.title,
                            category = item.category,
                            merchantName = item.merchantName,
                            commissionDisplay = item.commissionDisplay,
                            payoutType = "CPS",
                            baseCommissionRate = item.baseCommissionRate,
                            clicksCount = 0,
                            conversionsCount = 0,
                            totalEarned = 0.0,
                            status = item.status
                        )
                    }
                    repository.syncCampaignsFromBackend(entities)
                }
            }
        }
    }

    /**
     * Load missions dari backend.
     */
    fun loadMissionsFromBackendV2() {
        viewModelScope.launch {
            ApiClient.getMissions().onSuccess { response ->
                if (response.success) {
                    val entities = response.data.map { item ->
                        com.inkside.digital.data.model.TaskMissionEntity(
                            id = item.id,
                            title = item.title,
                            description = "",
                            rtpReward = item.rtpReward,
                            type = "ENGAGEMENT_MISSION",
                            category = item.category,
                            currentProgress = 0,
                            maxProgress = 1,
                            isCompleted = false,
                            isClaimed = false,
                            iconKey = "star",
                            targetPlatform = item.targetPlatform,
                            durationSeconds = item.durationSeconds,
                            actionUrl = item.actionUrl
                        )
                    }
                    repository.syncMissionsFromBackend(entities)
                }
            }
        }
    }

    /**
     * Load semua data dari backend — user + campaigns + missions.
     */
    fun loadAllFromBackend() {
        loadUserFromBackend()
        loadCampaignsFromBackendV2()
        loadMissionsFromBackendV2()
    }

}
