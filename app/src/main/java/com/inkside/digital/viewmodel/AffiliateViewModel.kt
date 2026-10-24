package com.inkside.digital.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.inkside.digital.localization.LanguageManager
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
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.delay
import org.json.JSONObject
import com.google.firebase.auth.FirebaseAuth
import com.inkside.digital.widget.WidgetUpdater
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import com.inkside.digital.sensor.BatteryTempHelper
import com.inkside.digital.util.AnalyticsHelper
import com.inkside.digital.sensor.LocationHelper
import com.inkside.digital.util.AppLogger

enum class AppScreen {
    HOME,
    STEP_COUNTER,
    APP_OFFERS,
    CAMPAIGNS,
    MISSIONS,
    GAME_ROOM,
    GAME_MINING,
    GAME_COIN_RUSH,
    LEADERBOARD,
    ANALYTICS,
    WITHDRAW,
    INVEST,
    HISTORY,
    REFERRAL,
    SETTINGS,
    FAQ,
    INBOX,
    DAILY_CHECK_IN,
    PROFILE,
    NOTIFICATIONS,
    SWEEPSTAKES,
    TRANSFER,
    WHITEPAPER,
    GLOBAL_CHAT,
    ACHIEVEMENT,
    PURCHASE
}

// ==== Ad Button State ====
enum class AdButtonState {
    LOADING,        // sedang cek / load
    READY,          // siap nonton
    LOADING_AD,     // iklan sedang di-load
    NO_AD,          // iklan tidak tersedia
    LIMIT,          // limit harian habis
    COOLDOWN        // tunggu 30 detik
}

class AffiliateViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: AffiliateRepository

    init {
        val database = AppDatabase.getDatabase(application, viewModelScope)
        repository = AffiliateRepository(database.appDao())

        // ============ AUTO WIDGET SYNC (SEMUA transaksi) ============
        // Observe user state — SELEPAS repository diinisialisasi
        // (elak NullPointerException: repository.user adalah null dalam init awal)
        viewModelScope.launch {
            repository.user
                .filterNotNull()
                .distinctUntilChanged { old, new ->
                    old.points == new.points && old.balance == new.balance
                }
                .debounce(500L)
                .collect { currentUser ->
                    try {
                        WidgetUpdater.refreshFromRoom(getApplication(), currentUser.id)
                        AppLogger.d("AffiliateViewModel", "✅ Widget auto-sync (points=${currentUser.points}, balance=${currentUser.balance})")
                    } catch (e: Exception) {
                        AppLogger.w("AffiliateViewModel", "Widget auto-sync gagal: ${e.message}")
                    }
                }
        }
        // Tidak ada seed data dummy — user di-load dari backend.
        // Set active user ID dari Firebase Auth
        viewModelScope.launch {
            val firebaseUid = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid
            if (firebaseUid != null) {
                repository.switchUser(firebaseUid)
                // Defer 300ms — elak NPE StateFlow property initializer
                // (_userState, _historyItems, _notificationItems, dll)
                // Property initializer Kotlin jalan LEPAS init block
                kotlinx.coroutines.delay(300L)
                loadUserFromBackend()
                loadHistory()
                loadNotifications()
            }
        }
        ApiClient.init(application)

        // ============ LOAD MINER CATALOG SELEPAS USER READY ============
        // Obser user state — bila user ready (Firebase + backend), load catalog
        // (elak 401 bila token belum ready masa first launch)
        viewModelScope.launch {
            repository.user
                .filterNotNull()
                .first()  // tunggu user ready
            kotlinx.coroutines.delay(500L)  // defer sikit
            loadMinerCatalog()
            AppLogger.d("AffiliateViewModel", "✅ Auto-load miner catalog (user ready)")
        }
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

    // ==== Language State (dipindah ke atas supaya bisa di-referensi) ====
    private val _currentLanguage = MutableStateFlow(
        com.inkside.digital.data.preferences.AppThemePreferences.currentLanguage
    )
    val currentLanguage: StateFlow<AppLanguage> = _currentLanguage.asStateFlow()

    // Portal Integration States
    val portalBaseUrl = MutableStateFlow(com.inkside.digital.data.network.ApiClient.getStoredBaseUrl(application))
    val portalApiKey = MutableStateFlow(com.inkside.digital.data.network.ApiClient.getStoredApiKey(application))
    val portalConnectionStatus = MutableStateFlow("IDLE") // IDLE, TESTING, ONLINE, ERROR
    val portalStatusMessage = MutableStateFlow(LanguageManager.translate("vm_portal_untested", _currentLanguage.value, "Belum diuji"))
    val isSyncingWithPortal = MutableStateFlow(false)

    fun testPortalConnection(url: String, apiKey: String) {
        viewModelScope.launch {
            portalConnectionStatus.value = "TESTING"
            portalStatusMessage.value = LanguageManager.translate("vm_portal_connecting", _currentLanguage.value, "Menghubungkan ke $url...")
            val result = com.inkside.digital.data.network.ApiClient.testConnection(url)
            result.onSuccess { res ->
                portalConnectionStatus.value = "ONLINE"
                portalStatusMessage.value = LanguageManager.translate("vm_portal_connected_detail", _currentLanguage.value, "Tersambung (HTTP 200 OK) - ${res.message}")
                showSnackbar(LanguageManager.translate("snackbar_portal_connected", _currentLanguage.value, "Berhasil terhubung ke portal Anda! 🟢"))
            }.onFailure { err ->
                portalConnectionStatus.value = "ERROR"
                portalStatusMessage.value = "Gagal: ${err.localizedMessage ?: LanguageManager.translate("vm_server_unreachable", _currentLanguage.value, "Tidak dapat menjangkau server")}"
                showSnackbar(String.format(LanguageManager.translate("snackbar_portal_failed", _currentLanguage.value, "Koneksi gagal: %s 🔴"), err.message))
            }
        }
    }

    fun savePortalConfiguration(url: String, apiKey: String) {
        com.inkside.digital.data.network.ApiClient.updateBaseUrl(getApplication(), url, apiKey)
        portalBaseUrl.value = com.inkside.digital.data.network.ApiClient.getStoredBaseUrl(getApplication())
        portalApiKey.value = com.inkside.digital.data.network.ApiClient.getStoredApiKey(getApplication())
        showSnackbar(LanguageManager.translate("snackbar_config_saved", _currentLanguage.value, "Konfigurasi berhasil disimpan!"))
    }

    fun syncDataWithPortal() {
        val currentUser = user.value ?: return
        viewModelScope.launch {
            isSyncingWithPortal.value = true
            showSnackbar(LanguageManager.translate("snackbar_sync_started", _currentLanguage.value, "Memulai sinkronisasi..."))
            val req = com.inkside.digital.data.network.model.ApiUserSyncRequest(
                id = currentUser.id,
                name = currentUser.name,
                email = currentUser.email,
                phone = currentUser.phone,
                tier = currentUser.tier,
                balance = currentUser.balance,
                points = currentUser.points.toInt(),
                todaySteps = currentUser.todaySteps,
                referralCode = currentUser.referralCode,
                city = currentUser.locationCity
            )
            val result = com.inkside.digital.data.network.ApiClient.syncUserToPortal(req)
            isSyncingWithPortal.value = false
            result.onSuccess { res ->
                showSnackbar(LanguageManager.translate("snackbar_sync_success", _currentLanguage.value, "Sinkronisasi sukses!"))
            }.onFailure { err ->
                showSnackbar(String.format(LanguageManager.translate("snackbar_sync_failed", _currentLanguage.value, "Sinkronisasi gagal: %s"), err.message))
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
        _snackbarMessage.value = LanguageManager.translate("vm_switch_account", _currentLanguage.value, "Beralih akun: ${targetUser?.name ?: userId}")
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
                _snackbarMessage.value = LanguageManager.translate("vm_step_exchange_success", _currentLanguage.value, "Sukses menukar langkah! +$coins Koin (Rp ${String.format(java.util.Locale.US, "%,.0f", rupiah)})")
                onComplete?.invoke(true, "+$coins Koin")
            } else {
                val msg = result.exceptionOrNull()?.message ?: LanguageManager.translate("vm_step_exchange_failed", _currentLanguage.value, "Gagal menukar langkah")
                _snackbarMessage.value = msg
                onComplete?.invoke(false, msg)
            }
        }
    }

    fun downloadApp(adId: String) {
        viewModelScope.launch {
            repository.simulateDownloadApp(adId)
            _snackbarMessage.value = LanguageManager.translate("vm_app_installed", _currentLanguage.value, "Aplikasi berhasil diunduh & dipasang! Klaim bonus sekarang.")
        }
    }

    fun claimAppDownloadReward(adId: String, onComplete: ((Boolean, String) -> Unit)? = null) {
        viewModelScope.launch {
            val result = repository.claimAppDownloadReward(adId)
            if (result.isSuccess) {
                val (coins, rupiah) = result.getOrThrow()
                _snackbarMessage.value = LanguageManager.translate("vm_claim_success", _currentLanguage.value, "Klaim berhasil! +$coins Koin (+Rp ${String.format(java.util.Locale.US, "%,.0f", rupiah)})")
                onComplete?.invoke(true, "+$coins Koin")
            } else {
                val msg = result.exceptionOrNull()?.message ?: LanguageManager.translate("vm_claim_failed", _currentLanguage.value, "Gagal klaim hadiah")
                _snackbarMessage.value = msg
                onComplete?.invoke(false, msg)
            }
        }
    }

    fun claimBannerAdBonus(bannerId: String, sponsorName: String, coins: Int) {
        viewModelScope.launch {
            val result = repository.claimBannerAdBonus(bannerId, sponsorName, coins)
            if (result.isSuccess) {
                _snackbarMessage.value = LanguageManager.translate("vm_sponsor_bonus", _currentLanguage.value, "Bonus sponsor $sponsorName! +$coins Koin didapatkan 🎉")
            }
        }
    }

    fun toggleFavoriteContact(contactId: String) {
        repository.toggleFavoriteContact(contactId)
    }

    // Navigation and UI State
    private val _currentScreen = MutableStateFlow(AppScreen.HOME)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    // ==== Ad Button State ====
    private val _adButtonState = MutableStateFlow(AdButtonState.LOADING)
    val adButtonState: StateFlow<AdButtonState> = _adButtonState.asStateFlow()

    private val _todayAdsWatched = MutableStateFlow(0)
    val todayAdsWatched: StateFlow<Int> = _todayAdsWatched.asStateFlow()

    private val _maxAdsPerDay = MutableStateFlow(10)
    val maxAdsPerDay: StateFlow<Int> = _maxAdsPerDay.asStateFlow()

    private var lastAdTimestamp: Long = 0
    private val COOLDOWN_MS = 30_000L

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

    fun setLanguage(lang: AppLanguage) {
        AppLogger.e("LANG_DEBUG", "🔵 setLanguage dipanggil: ${lang.code} (${lang.displayName})")
        _currentLanguage.value = lang
        // Simpan ke SharedPreferences biar persist
        val ctx = getApplication<android.app.Application>()
        com.inkside.digital.data.preferences.AppThemePreferences.setLanguage(ctx, lang)
        showSnackbar(String.format(LanguageManager.translate("snackbar_language_changed", lang, "Bahasa diubah ke %s %s"), lang.displayName, lang.flag))
    }

    fun showSnackbar(message: String) {
        _snackbarMessage.value = message
    }

    /**
     * Refresh status tombol iklan — cek limit dari backend + AdMob state + cooldown.
     */
    fun refreshAdButtonState() {
        viewModelScope.launch {
            // 1. Cek cooldown lokal
            val now = System.currentTimeMillis()
            if (now - lastAdTimestamp < COOLDOWN_MS) {
                _adButtonState.value = AdButtonState.COOLDOWN
                return@launch
            }

            // 2. Fetch status dari backend
            _adButtonState.value = AdButtonState.LOADING
            ApiClient.getAdStatus().onSuccess { json ->
                if (json.optBoolean("success", false)) {
                    val data = json.optJSONObject("data") ?: return@onSuccess
                    val today = data.optInt("todayAdsWatched", 0)
                    val max = data.optInt("maxAdsPerDay", 10)
                    val canWatch = data.optBoolean("canWatch", true)

                    _todayAdsWatched.value = today
                    _maxAdsPerDay.value = max

                    if (!canWatch) {
                        _adButtonState.value = AdButtonState.LIMIT
                        return@onSuccess
                    }

                    // 3. Cek AdMob state
                    val adState = com.inkside.digital.data.ads.AdMobProvider.adState.value
                    _adButtonState.value = when (adState) {
                        true -> AdButtonState.READY
                        false -> AdButtonState.NO_AD
                        null -> AdButtonState.LOADING_AD
                    }
                }
            }.onFailure {
                // Kalau backend gagal, cek AdMob saja
                val adState = com.inkside.digital.data.ads.AdMobProvider.adState.value
                _adButtonState.value = when (adState) {
                    true -> AdButtonState.READY
                    false -> AdButtonState.NO_AD
                    null -> AdButtonState.LOADING_AD
                }
            }
        }
    }

    /**
     * Pre-load iklan + refresh state. Panggil saat HomeScreen masuk.
     */
    fun preloadAdForHome() {
        viewModelScope.launch {
            // 1. Load iklan di background
            val ctx = getApplication<Application>()
            com.inkside.digital.data.ads.AdMobProvider.loadRewardedAd(ctx, null)

            // 2. Refresh state
            refreshAdButtonState()
        }
    }

    /**
     * Retry manual — dipanggil kalau user tekan "Coba Lagi".
     */
    fun retryAdLoad() {
        preloadAdForHome()
    }

    /**
     * Panggil setelah user selesai nonton iklan — update cooldown + state.
     */
    fun onAdWatched() {
        lastAdTimestamp = System.currentTimeMillis()
        refreshAdButtonState()
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
                showSnackbar(LanguageManager.translate("snackbar_withdraw_sent", _currentLanguage.value, "Pengajuan berhasil!"))

                // Kirim ke backend
                val req = com.inkside.digital.data.network.model.ApiWithdrawalRequest(
                    id = withdrawal.id,
                    userId = withdrawal.userId,
                    userName = user.value?.name ?: "",
                    amount = withdrawal.amount,
                    currency = withdrawal.currency,
                    channelType = withdrawal.channelType,
                    providerName = withdrawal.providerName,
                    accountDestination = withdrawal.accountDestination,
                    accountHolderName = withdrawal.accountHolderName,
                    status = withdrawal.status,
                    fee = withdrawal.fee,
                    netAmount = withdrawal.netAmount,
                    txRef = withdrawal.txRef
                )
                ApiClient.createWithdrawal(req)
                    .onSuccess { response ->
                        if (!response.success) {
                            showSnackbar("Backend: " + (response.message ?: LanguageManager.translate("vm_failed", _currentLanguage.value, "Gagal")))
                        }
                    }
                    .onFailure { error ->
                        showSnackbar(String.format(LanguageManager.translate("snackbar_backend_error", _currentLanguage.value, "Backend error: %s"), error.message ?: LanguageManager.translate("vm_network_error", _currentLanguage.value, "Network error")))
                    }
            }.onFailure { error ->
                showSnackbar(String.format(LanguageManager.translate("snackbar_generic_failed", _currentLanguage.value, "Gagal: %s"), error.message ?: "Unknown"))
            }
        }
    }

    fun adminProcessWithdrawal(withdrawal: WithdrawalEntity, isApprove: Boolean, note: String) {
        viewModelScope.launch {
            val newStatus = if (isApprove) "PAID" else "REJECTED"
            repository.updateWithdrawalStatusByAdmin(withdrawal, newStatus, note)
            showAdminReviewModal.value = null
            showSnackbar(if (isApprove) LanguageManager.translate("vm_withdraw_approved", _currentLanguage.value, "Penarikan berhasil disetujui & dicairkan manual!") else "Penarikan telah ditolak dan saldo dikembalikan.")
        }
    }

    fun createLink(campaign: CampaignEntity, customSlug: String, subId: String) {
        viewModelScope.launch {
            val created = repository.createAffiliateLink(campaign, customSlug, subId)
            showNewLinkModal.value = false
            selectedCampaignForLink.value = null
            showSnackbar(String.format(LanguageManager.translate("snackbar_affiliate_link_ready", _currentLanguage.value, "Tautan siap: %s"), created.shortUrl))
        }
    }

    fun simulateConversion(link: AffiliateLinkEntity) {
        viewModelScope.launch {
            repository.simulateLiveConversion(link)
            showSnackbar("⚡ Konversi real-time berhasil! Komisi & Poin masuk.")
        }
    }

    // ==== TIER / UPGRADE (Play Billing belum ada — baca dari backend) ====
    private val _tiers = MutableStateFlow<List<org.json.JSONObject>>(emptyList())
    val tiers: StateFlow<List<org.json.JSONObject>> = _tiers.asStateFlow()

    // ==== AFFILIATE STATE ====
    data class AffiliateBalanceData(
        val balance: Double = 0.0,
        val available: Double = 0.0,
        val pending: Double = 0.0,
        val totalEarned: Double = 0.0,
        val totalWithdrawn: Double = 0.0,
        val minWithdraw: Double = 10.0
    )

    private val _affiliateBalance = MutableStateFlow(AffiliateBalanceData())
    val affiliateBalance: StateFlow<AffiliateBalanceData> = _affiliateBalance.asStateFlow()

    private val _affiliateTransactions = MutableStateFlow<List<org.json.JSONObject>>(emptyList())
    val affiliateTransactions: StateFlow<List<org.json.JSONObject>> = _affiliateTransactions.asStateFlow()

    private val _affiliateWithdrawals = MutableStateFlow<List<org.json.JSONObject>>(emptyList())
    val affiliateWithdrawals: StateFlow<List<org.json.JSONObject>> = _affiliateWithdrawals.asStateFlow()

    private val _affiliateLoading = MutableStateFlow(false)
    val affiliateLoading: StateFlow<Boolean> = _affiliateLoading.asStateFlow()

    // ============ LOADING STATE (Preload Gate) ============
    data class LoadingState(
        val firebaseAuth: Boolean = false,
        val userProfile: Boolean = false,
        val minerCatalog: Boolean = false,
        val gameState: Boolean = false,
        val affiliateBalance: Boolean = false,
        val adMobReady: Boolean = false,
        val fcmToken: Boolean = false
    ) {
        val totalItems: Int get() = 7
        val readyCount: Int get() = listOf(
            firebaseAuth, userProfile, minerCatalog, gameState,
            affiliateBalance, adMobReady, fcmToken
        ).count { it }
        val progress: Float get() = readyCount.toFloat() / totalItems
        val isComplete: Boolean get() = readyCount == totalItems

        fun itemList(): List<Pair<String, Boolean>> = listOf(
            "vm_stage_load_account" to firebaseAuth,
            "vm_stage_sync_profile" to userProfile,
            "vm_stage_load_miner" to minerCatalog,
            "vm_stage_prepare_game" to gameState,
            "vm_stage_load_affiliate" to affiliateBalance,
            "vm_stage_prepare_ads" to adMobReady,
            "vm_stage_connect_notif" to fcmToken
        )
    }

    private val _loadingState = MutableStateFlow(LoadingState())
    val loadingState: StateFlow<LoadingState> = _loadingState.asStateFlow()

    /**
     * preloadAll — panggil semua komponen penting sebelum masuk app.
     * Setiap komponen ditandai ready bila selesai.
     */
    fun preloadAll() {
        viewModelScope.launch {
            // 1. Firebase Auth
            try {
                val fbUser = FirebaseAuth.getInstance().currentUser
                _loadingState.value = _loadingState.value.copy(firebaseAuth = fbUser != null)
                AppLogger.d("Preload", "1/7 Firebase Auth: ${fbUser?.uid?.take(8)}")
            } catch (e: Exception) {
                _loadingState.value = _loadingState.value.copy(firebaseAuth = true)
            }

            // 2. User Profile
            try {
                loadUserFromBackend()
                delay(300)
                _loadingState.value = _loadingState.value.copy(userProfile = true)
                AppLogger.d("Preload", "2/7 User Profile ✅")
            } catch (e: Exception) {
                _loadingState.value = _loadingState.value.copy(userProfile = true)
            }

            // 3. Miner Catalog
            try {
                loadMinerCatalog()
                delay(300)
                _loadingState.value = _loadingState.value.copy(minerCatalog = true)
                AppLogger.d("Preload", "3/7 Miner Catalog ✅")
            } catch (e: Exception) {
                _loadingState.value = _loadingState.value.copy(minerCatalog = true)
            }

            // 4. Game State
            try {
                loadGameState()
                delay(300)
                _loadingState.value = _loadingState.value.copy(gameState = true)
                AppLogger.d("Preload", "4/7 Game State ✅")
            } catch (e: Exception) {
                _loadingState.value = _loadingState.value.copy(gameState = true)
            }

            // 5. Affiliate Balance
            try {
                loadAffiliateBalance()
                delay(300)
                _loadingState.value = _loadingState.value.copy(affiliateBalance = true)
                AppLogger.d("Preload", "5/7 Affiliate Balance ✅")
            } catch (e: Exception) {
                _loadingState.value = _loadingState.value.copy(affiliateBalance = true)
            }

            // 6. Tiers (bonus — digabung ke affiliateBalance kalau gagal)
            try {
                loadTiers()
            } catch (e: Exception) { }

            // 7. AdMob Ready — callback
            try {
                // AdMob biasanya ready bila AdManager.init selesai
                // Tandai ready lepas 500ms
                delay(500)
                _loadingState.value = _loadingState.value.copy(adMobReady = true)
                AppLogger.d("Preload", "6/7 AdMob ✅")
            } catch (e: Exception) {
                _loadingState.value = _loadingState.value.copy(adMobReady = true)
            }

            // 8. FCM Token (M54: guna addOnCompleteListener)
            try {
                com.google.firebase.messaging.FirebaseMessaging.getInstance().token
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            registerFcmToken(task.result)
                            _loadingState.value = _loadingState.value.copy(fcmToken = true)
                            AppLogger.d("Preload", "7/7 FCM ✅")
                        } else {
                            _loadingState.value = _loadingState.value.copy(fcmToken = true)
                            AppLogger.w("Preload", "FCM gagal: ${task.exception?.message}")
                        }
                    }
            } catch (e: Exception) {
                _loadingState.value = _loadingState.value.copy(fcmToken = true)
                AppLogger.w("Preload", "FCM gagal: ${e.message}")
            }

            AppLogger.d("Preload", "🎉 PRELOAD COMPLETE")
        }
    }

    fun resetLoadingState() {
        _loadingState.value = LoadingState()
    }

    fun loadTiers() {
        viewModelScope.launch {
            ApiClient.getTiersPublic()
                .onSuccess { json ->
                    if (json.optBoolean("success", false)) {
                        val arr = json.optJSONArray("data") ?: return@onSuccess
                        val list = mutableListOf<org.json.JSONObject>()
                        for (i in 0 until arr.length()) {
                            arr.optJSONObject(i)?.let { list.add(it) }
                        }
                        _tiers.value = list
                        AppLogger.d("AffiliateViewModel", "✅ Tiers loaded: ${list.size}")
                    }
                }
                .onFailure {
                    AppLogger.w("AffiliateViewModel", "Gagal load tiers: ${it.message}")
                }
        }
    }

    // ============ AFFILIATE METHODS ============
    fun loadAffiliateBalance() {
        viewModelScope.launch {
            _affiliateLoading.value = true
            ApiClient.getAffiliateBalance()
                .onSuccess { json ->
                    if (json.optBoolean("success", false)) {
                        val data = json.optJSONObject("data") ?: return@onSuccess
                        _affiliateBalance.value = AffiliateBalanceData(
                            balance = data.optDouble("balance", 0.0),
                            available = data.optDouble("available", 0.0),
                            pending = data.optDouble("pending", 0.0),
                            totalEarned = data.optDouble("totalEarned", 0.0),
                            totalWithdrawn = data.optDouble("totalWithdrawn", 0.0),
                            minWithdraw = data.optDouble("minWithdraw", 10.0)
                        )
                        val uid = FirebaseAuth.getInstance().currentUser?.uid
                        if (uid != null) {
                            repository.updateUserAffiliateFromBackend(
                                userId = uid,
                                affiliateBalance = data.optDouble("balance", 0.0),
                                affiliateBalanceAvailable = data.optDouble("available", 0.0),
                                affiliateBalancePending = data.optDouble("pending", 0.0),
                                affiliateTotalEarned = data.optDouble("totalEarned", 0.0),
                                affiliateTotalWithdrawn = data.optDouble("totalWithdrawn", 0.0)
                            )
                        }
                    }
                }
                .onFailure {
                    AppLogger.w("AffiliateViewModel", "Gagal load affiliate balance: ${it.message}")
                }
            _affiliateLoading.value = false
        }
    }

    fun loadAffiliateTransactions(limit: Int = 50) {
        viewModelScope.launch {
            ApiClient.getAffiliateTransactions(limit)
                .onSuccess { json ->
                    if (json.optBoolean("success", false)) {
                        val arr = json.optJSONArray("data") ?: return@onSuccess
                        val list = mutableListOf<org.json.JSONObject>()
                        for (i in 0 until arr.length()) {
                            arr.optJSONObject(i)?.let { list.add(it) }
                        }
                        _affiliateTransactions.value = list
                    }
                }
                .onFailure {
                    AppLogger.w("AffiliateViewModel", "Gagal load affiliate transactions: ${it.message}")
                }
        }
    }

    fun requestAffiliateWithdraw(amount: Double, destination: String, method: String = "PAYPAL") {
        viewModelScope.launch {
            _affiliateLoading.value = true
            ApiClient.requestAffiliateWithdraw(amount, destination, method)
                .onSuccess { json ->
                    if (json.optBoolean("success", false)) {
                        showSnackbar(json.optString("message", LanguageManager.translate("vm_withdraw_submitted", _currentLanguage.value, "Withdraw berhasil diajukan!")))
                        loadAffiliateBalance()
                        loadAffiliateWithdrawals()
                    } else {
                        showSnackbar(json.optString("message", LanguageManager.translate("vm_withdraw_failed", _currentLanguage.value, "Gagal withdraw")))
                    }
                }
                .onFailure {
                    showSnackbar(String.format(LanguageManager.translate("snackbar_network_error", _currentLanguage.value, "Error: %s"), it.message ?: "Unknown"))
                }
            _affiliateLoading.value = false
        }
    }

    fun loadAffiliateWithdrawals(limit: Int = 20) {
        viewModelScope.launch {
            ApiClient.getAffiliateWithdrawals(limit)
                .onSuccess { json ->
                    if (json.optBoolean("success", false)) {
                        val arr = json.optJSONArray("data") ?: return@onSuccess
                        val list = mutableListOf<org.json.JSONObject>()
                        for (i in 0 until arr.length()) {
                            arr.optJSONObject(i)?.let { list.add(it) }
                        }
                        _affiliateWithdrawals.value = list
                    }
                }
                .onFailure {
                    AppLogger.w("AffiliateViewModel", "Gagal load withdrawals: ${it.message}")
                }
        }
    }

    fun registerFcmToken(token: String, deviceId: String = "") {
        viewModelScope.launch {
            ApiClient.registerFcmToken(token, deviceId)
                .onSuccess {
                    AppLogger.d("AffiliateViewModel", "✅ FCM token registered")
                }
                .onFailure {
                    AppLogger.w("AffiliateViewModel", "Gagal register FCM: ${it.message}")
                }
        }
    }

    fun upgradeToPremium() {
        viewModelScope.launch {
            val result = repository.upgradeToPremium()
            result.onSuccess {
                showUpgradeModal.value = false
                showSnackbar("👑 Selamat! Akun Anda kini berstatus VIP Premium.")
            }.onFailure {
                showSnackbar(it.message ?: LanguageManager.translate("vm_upgrade_failed", _currentLanguage.value, "Upgrade gagal"))
            }
        }
    }

    fun redeemPoints(points: Int, walletName: String, phone: String, currency: String = "USD") {
        viewModelScope.launch {
            val res = repository.redeemPointsToWallet(points, walletName, phone, currency)
            res.onSuccess { earned ->
                showRedeemPointsModal.value = false
                showSnackbar(String.format(LanguageManager.translate("snackbar_redeem_success", _currentLanguage.value, "Sukses tukar poin! +Rp %s"), String.format(java.util.Locale.US, "%,.0f", earned)))
            }.onFailure {
                showSnackbar(it.message ?: LanguageManager.translate("vm_redeem_failed", _currentLanguage.value, "Gagal tukar poin"))
            }
        }
    }

    fun buyCoupon(coupon: InvestmentCouponEntity, quantity: Int) {
        viewModelScope.launch {
            val res = repository.buyInvestmentCoupon(coupon, quantity)
            res.onSuccess {
                showBuyCouponModal.value = null
                showSnackbar(LanguageManager.translate("snackbar_coupon_bought", _currentLanguage.value, "Kupon berhasil dibeli!"))
            }.onFailure {
                showSnackbar(it.message ?: LanguageManager.translate("vm_coupon_buy_failed", _currentLanguage.value, "Gagal membeli kupon"))
            }
        }
    }

    fun toggleListingSecondaryMarket(coupon: InvestmentCouponEntity, price: Double, isListing: Boolean) {
        viewModelScope.launch {
            repository.toggleListCouponSecondaryMarket(coupon, price, isListing)
            showListCouponModal.value = null
            showSnackbar(if (isListing) LanguageManager.translate("vm_coupon_listed", _currentLanguage.value, "Kupon aktif didaftarkan di Pasar Sekunder!") else "Kupon ditarik dari Pasar Sekunder.")
        }
    }

    fun claimYield(coupon: InvestmentCouponEntity) {
        viewModelScope.launch {
            val res = repository.claimCouponYield(coupon)
            res.onSuccess { amount ->
                showSnackbar(String.format(LanguageManager.translate("snackbar_dividend_paid", _currentLanguage.value, "Dividen +Rp %s dicairkan!"), String.format(java.util.Locale.US, "%,.0f", amount)))
            }.onFailure {
                showSnackbar(it.message ?: LanguageManager.translate("vm_yield_failed", _currentLanguage.value, "Gagal klaim yield"))
            }
        }
    }

    /**
     * Dipanggil oleh MainActivity SETELAH AdManager berhasil klaim reward ke backend.
     * Backend sudah credit points ke Firestore. Tinggal update Room lokal
     * supaya UI langsung sinkron.
     */
    fun onAdRewardEarned(points: Int) {

        // Analytics
        try {
            AnalyticsHelper.logAdReward("admob", points)
        } catch (e: Exception) {}
        viewModelScope.launch {
            try {
                showAdRewardModal.value = false
                // Refresh user dari backend supaya points di Room sinkron
                loadUserFromBackend()
                // Update widget dengan data terbaru dari Room
                user.value?.let { u ->
                    WidgetUpdater.refreshFromRoom(getApplication(), u.id)
                }
                // Update cooldown + state tombol
                onAdWatched()
                showSnackbar(String.format(LanguageManager.translate("snackbar_ad_reward", _currentLanguage.value, "🎉 Selamat! +%s RTP dari iklan sponsor!"), points))
            } catch (e: Exception) {
                showSnackbar(String.format(LanguageManager.translate("snackbar_reward_success", _currentLanguage.value, "Reward berhasil, tapi gagal refresh: %s"), e.message ?: "Unknown"))
            }
        }
    }

    fun claimGameMiningPoints() {
        viewModelScope.launch {
            val result = repository.claimGameMiningPoints()
            result.onSuccess { points ->
                showSnackbar(String.format(LanguageManager.translate("snackbar_mining_harvest", _currentLanguage.value, "Panen Berhasil! +%s Poin 🎉"), points.toInt()))
            }.onFailure { error ->
                showSnackbar(error.message ?: LanguageManager.translate("vm_mining_claim_failed", _currentLanguage.value, "Gagal mengklaim hasil mining"))
            }
        }
    }

    fun buyGameMinerItem(itemId: String) {
        viewModelScope.launch {
            try {
                AppLogger.d("BuyMiner", "🛒 buyGameMinerItem START: $itemId")
                
                // 1. Beli lokal (Room DB)
                val result = repository.buyGameMinerItem(itemId)
                result.onSuccess { msg ->
                    AppLogger.d("BuyMiner", "✅ Lokal OK: $msg")
                    showSnackbar(msg)

                    // 2. Sync ke backend
                    try {
                        val item = repository.getGameMinerItemByIdSync(itemId)
                        if (item != null) {
                            val body = org.json.JSONObject().apply {
                                put("catalogId", item.catalogId)
                                put("instanceId", item.id)
                                put("tier", item.tier)
                                put("name", item.name)
                                put("tokenCost", item.tokenCost)
                                put("powerHs", item.powerHs)
                                put("pointsPerMinute", item.pointsPerMinute)
                                put("iconEmoji", item.iconEmoji)
                            }
                            AppLogger.d("BuyMiner", "📡 Sync backend: catalogId=${item.catalogId}")
                            ApiClient.buyMiner(body)
                                .onSuccess { resp ->
                                    val success = resp.optBoolean("success", false)
                                    AppLogger.d("BuyMiner", "✅ Sync response: $resp")
                                    if (success) {
                                        loadGameState()
                                    } else {
                                        showSnackbar(resp.optString("message", LanguageManager.translate("vm_backend_reject", _currentLanguage.value, "Backend reject")))
                                    }
                                }
                                .onFailure { err ->
                                    AppLogger.e("BuyMiner", "❌ Sync failed: ${err.message}", err)
                                    showSnackbar("⚠️ Gagal sync ke server: ${err.message}")
                                }
                        } else {
                            AppLogger.e("BuyMiner", "❌ Item null selepas beli!")
                            showSnackbar("⚠️ Item tak jumpa")
                        }
                    } catch (e: Exception) {
                        AppLogger.e("BuyMiner", "❌ Sync exception: ${e.message}", e)
                        showSnackbar("⚠️ Sync error: ${e.message}")
                    }
                }.onFailure { error ->
                    AppLogger.e("BuyMiner", "❌ Lokal fail: ${error.message}", error)
                    showSnackbar(error.message ?: LanguageManager.translate("vm_miner_buy_failed", _currentLanguage.value, "Gagal membeli item penambang"))
                }
            } catch (e: Exception) {
                AppLogger.e("BuyMiner", "❌ Fatal error: ${e.message}", e)
                showSnackbar(LanguageManager.translate("vm_error_prefix", _currentLanguage.value, "Error: ") + e.message)
            }
        }
    }

    /**
     * Beli miner & auto-pasang ke slot kosong pertama.
     * Kalau takde slot kosong → beli sahaja (masuk inventory).
     */
    fun buyAndPlaceGameMinerItem(itemId: String) {
        viewModelScope.launch {
            try {
                AppLogger.d("BuyMiner", "🛒 buyAndPlaceGameMinerItem START: $itemId")

                val result = repository.buyGameMinerItem(itemId)
                result.onSuccess { msg ->
                    AppLogger.d("BuyMiner", "✅ Lokal OK: $msg")
                    showSnackbar(msg)

                    try {
                        val item = repository.getGameMinerItemByIdSync(itemId)
                        if (item != null) {
                            AppLogger.d("BuyMiner", "📡 Sync backend: catalogId=${item.catalogId}")
                            val body = org.json.JSONObject().apply {
                                put("catalogId", item.catalogId)
                                put("instanceId", item.id)
                                put("tier", item.tier)
                                put("name", item.name)
                                put("tokenCost", item.tokenCost)
                                put("powerHs", item.powerHs)
                                put("pointsPerMinute", item.pointsPerMinute)
                                put("iconEmoji", item.iconEmoji)
                            }
                            ApiClient.buyMiner(body)
                                .onSuccess { resp ->
                                    val success = resp.optBoolean("success", false)
                                    AppLogger.d("BuyMiner", "✅ Sync response: $resp")
                                    if (!success) {
                                        showSnackbar(LanguageManager.translate("vm_backend_reject_prefix", _currentLanguage.value, "Backend reject: ") + resp.optString("message"))
                                        return@onSuccess
                                    }

                                    val roomState = gameRoomState.value
                                    val unlockedSlots = roomState?.unlockedSlots ?: 2
                                    val usedSlots = gameMinerItems.value
                                        .filter { it.isPlacedInRoom && it.id != itemId }
                                        .map { it.placedSlotIndex }
                                        .toSet()
                                    val emptySlot = (0 until unlockedSlots).firstOrNull { it !in usedSlots }

                                    if (emptySlot != null) {
                                        repository.toggleMinerSlot(itemId, emptySlot)
                                        val placeBody = org.json.JSONObject().apply {
                                            put("minerId", itemId)
                                            put("slotIndex", emptySlot)
                                        }
                                        ApiClient.placeMiner(placeBody)
                                        showSnackbar("${item.name} dipasang di Slot ${emptySlot + 1}")
                                    } else {
                                        showSnackbar("${item.name} disimpan — slot penuh")
                                    }

                                    loadGameState()
                                }
                                .onFailure { err ->
                                    AppLogger.e("BuyMiner", "❌ Sync failed: ${err.message}", err)
                                    showSnackbar("⚠️ Gagal sync ke server: ${err.message}")
                                }
                        } else {
                            AppLogger.e("BuyMiner", "❌ Item null selepas beli!")
                            showSnackbar("⚠️ Item tak jumpa")
                        }
                    } catch (e: Exception) {
                        AppLogger.e("BuyMiner", "❌ Sync exception: ${e.message}", e)
                        showSnackbar("⚠️ Sync error: ${e.message}")
                    }
                }.onFailure { error ->
                    AppLogger.e("BuyMiner", "❌ Lokal fail: ${error.message}", error)
                    showSnackbar(error.message ?: LanguageManager.translate("vm_miner_buy_failed", _currentLanguage.value, "Gagal membeli item penambang"))
                }
            } catch (e: Exception) {
                AppLogger.e("BuyMiner", "❌ Fatal error: ${e.message}", e)
                showSnackbar(LanguageManager.translate("vm_error_prefix", _currentLanguage.value, "Error: ") + e.message)
            }
        }
    }

    // Merge state
    private val _isMerging = MutableStateFlow(false)
    val isMerging: StateFlow<Boolean> = _isMerging
    private val _mergeError = MutableStateFlow<String?>(null)
    val mergeError: StateFlow<String?> = _mergeError
    private val _mergeSuccess = MutableStateFlow<org.json.JSONObject?>(null)
    val mergeSuccess: StateFlow<org.json.JSONObject?> = _mergeSuccess

    /**
     * Merge 5 miner tier sama → 1 miner tier+1.
     * Setelah sukses, refresh game state otomatis.
     */
    fun mergeMiners(minerIds: List<String>) {
        if (minerIds.size != 5) {
            _mergeError.value = "Harus 5 miner"
            return
        }
        if (_isMerging.value) return
        
        viewModelScope.launch {
            _isMerging.value = true
            _mergeError.value = null
            _mergeSuccess.value = null
            
            val result = repository.mergeMiners(minerIds)
            
            result.onSuccess { data ->
                AppLogger.d("AffiliateViewModel", "✅ Merge sukses: ${data.optJSONObject("newMiner")?.optString("name")}")
                _mergeSuccess.value = data
                // Refresh state dari backend
                loadGameState()
            }.onFailure { e ->
                AppLogger.e("AffiliateViewModel", "❌ Merge gagal: ${e.message}")
                _mergeError.value = e.message ?: "Merge gagal"
            }
            
            _isMerging.value = false
        }
    }
    
    fun clearMergeState() {
        _mergeError.value = null
        _mergeSuccess.value = null
    }

    fun toggleMinerSlot(itemId: String, targetSlot: Int) {
        viewModelScope.launch {
            // Check condition: place atau unplace?
            val currentItem = repository.getGameMinerItemByIdSync(itemId)
            val isUnplacing = currentItem?.isPlacedInRoom == true && currentItem.placedSlotIndex == targetSlot

            // 1. Update lokal (Room DB)
            val result = repository.toggleMinerSlot(itemId, targetSlot)
            result.onSuccess { msg ->
                showSnackbar(msg)

                // 2. Sync ke backend — conditional endpoint
                if (isUnplacing) {
                    // UNPLACE — guna endpoint /mining/unplace
                    val body = org.json.JSONObject().apply {
                        put("minerId", itemId)
                    }
                    ApiClient.unplaceMiner(body)
                        .onSuccess {
                            AppLogger.d("UnplaceMiner", "✅ Unplace synced to backend")
                            kotlinx.coroutines.delay(300L)  // defer sync
                            loadGameState()
                        }
                        .onFailure { err ->
                            AppLogger.e("UnplaceMiner", "❌ Sync failed: ${err.message}")
                        }
                } else {
                    // PLACE — guna endpoint /mining/place
                    val body = org.json.JSONObject().apply {
                        put("minerId", itemId)
                        put("slotIndex", targetSlot)
                    }
                    ApiClient.placeMiner(body)
                        .onSuccess {
                            AppLogger.d("PlaceMiner", "✅ Place synced to backend")
                            kotlinx.coroutines.delay(300L)  // defer sync
                            loadGameState()
                        }
                        .onFailure { err ->
                            AppLogger.e("PlaceMiner", "❌ Sync failed: ${err.message}")
                        }
                }
            }.onFailure { error ->
                showSnackbar(error.message ?: LanguageManager.translate("vm_move_item_failed", _currentLanguage.value, "Gagal memindahkan item"))
            }
        }
    }

    fun finishMiniGame(score: Int, durationMs: Long = 15000, gameNonce: String = "") {
        viewModelScope.launch {
            // 1. Update lokal (Room DB)
            val result = repository.playMiniGameFinish(score)
            result.onSuccess { (bonusPoints, boostGhs) ->
                showSnackbar(String.format(LanguageManager.translate("snackbar_minigame_finished", _currentLanguage.value, "Game Selesai! Skor: %s"), score, bonusPoints, boostGhs.toInt()))

                // 2. Sync ke backend (CRITICAL FIX)
                ApiClient.finishMiniGame(score, durationMs, gameNonce)
                    .onSuccess {
                        AppLogger.d("MiniGame", "✅ MiniGame reward synced to backend")
                        loadGameState()
                    }
                    .onFailure { err ->
                        AppLogger.e("MiniGame", "❌ Sync failed: ${err.message}")
                    }
            }.onFailure { error ->
                showSnackbar(error.message ?: LanguageManager.translate("vm_game_reward_failed", _currentLanguage.value, "Gagal memproses reward game"))
            }
        }
    }

    /**
     * M36 + M37 — Unlock slot tambahan (slot 3-6).
     * @param slotIndex 0-indexed slot (2=slot3, 3=slot4, 4=slot5, 5=slot6)
     */
    fun unlockSlot(slotIndex: Int) {
        viewModelScope.launch {
            ApiClient.unlockSlot(slotIndex)
                .onSuccess { json ->
                    if (json.optBoolean("success", false)) {
                        val dataObj = json.optJSONObject("data")
                        val unlockedSlots = dataObj?.optInt("unlockedSlots", 0) ?: 0
                        val pricePaid = dataObj?.optInt("pricePaid", 0) ?: 0

                        showSnackbar("🔓 Slot ${slotIndex + 1} terbuka! (-${pricePaid} RTP)")
                        AppLogger.d("UnlockSlot", "✅ Slot ${slotIndex + 1} unlocked")

                        // Refresh game state
                        loadGameState()
                        loadUserFromBackend()
                    } else {
                        val msg = json.optString("message", LanguageManager.translate("vm_open_slot_failed", _currentLanguage.value, "Gagal membuka slot"))
                        val httpCode = json.optInt("httpCode", 0)
                        AppLogger.w("UnlockSlot", "Failed: HTTP $httpCode — $msg")
                        showSnackbar("⚠️ $msg")
                    }
                }
                .onFailure { err ->
                    AppLogger.e("UnlockSlot", "❌ Error: ${err.message}")
                    showSnackbar(String.format(LanguageManager.translate("snackbar_network_error", _currentLanguage.value, "Error: %s"), err.message ?: LanguageManager.translate("vm_network_error", _currentLanguage.value, "Network error")))
                }
        }
    }

    fun updateProfile(name: String, email: String, phone: String, region: String) {
        viewModelScope.launch {
            val currentUser = user.value
            if (currentUser == null) {
                showSnackbar(LanguageManager.translate("vm_user_not_found", _currentLanguage.value, "User tidak ditemukan"))
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
                            phone = currentUser.phone,
                            tier = currentUser.tier,
                            balance = currentUser.balance,
                            points = currentUser.points.toInt(),
                            referralCode = currentUser.referralCode,
                            checkInStreak = currentUser.checkInStreak,
                            lastCheckInDate = currentUser.lastCheckInDate,
                            todaySteps = currentUser.todaySteps
                        )
                        showSnackbar("✅ Profil berhasil disimpan")
                    } else {
                        showSnackbar(LanguageManager.translate("vm_failed_prefix", _currentLanguage.value, "❌ Gagal: ") + (response.message ?: LanguageManager.translate("vm_unknown_error", _currentLanguage.value, "Unknown error")))
                    }
                }
                .onFailure { error ->
                    showSnackbar(LanguageManager.translate("vm_save_failed_prefix", _currentLanguage.value, "❌ Gagal simpan: ") + (error.message ?: LanguageManager.translate("vm_network_error", _currentLanguage.value, "Network error")))
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
            showSnackbar(LanguageManager.translate("vm_all_notif_read", _currentLanguage.value, "Semua notifikasi ditandai telah dibaca."))
        }
    }

    fun clearAllNotifications() {
        viewModelScope.launch {
            repository.deleteAllNotifications()
            showSnackbar(LanguageManager.translate("vm_all_notif_deleted", _currentLanguage.value, "Semua notifikasi dihapus."))
        }
    }

    fun performDailyCheckIn() {
        viewModelScope.launch {
            // ✅ FIX: Panggil server DULU, baru update lokal
            ApiClient.dailyCheckIn()
                .onSuccess { json ->
                    if (json.optBoolean("success", true)) {
                        // Server setuju — baru update lokal
                        val res = repository.performDailyCheckIn()
                        res.onSuccess { (reward, streak) ->
                            showDailyCheckInModal.value = false
                            showSnackbar(LanguageManager.translate("vm_checkin_success", _currentLanguage.value, "Check-in Hari ke-$streak Berhasil! +$reward RTP 🌟"))
                            user.value?.let { u -> WidgetUpdater.refreshFromRoom(getApplication(), u.id) }
                            loadUserFromBackend()
                        }.onFailure {
                            showSnackbar(it.message ?: LanguageManager.translate("vm_checkin_failed", _currentLanguage.value, "Gagal update lokal"))
                        }
                    } else {
                        // Server menolak (misal: sudah check-in hari ini)
                        showSnackbar(json.optString("message", "Anda sudah check-in hari ini."))
                    }
                }
                .onFailure { error ->
                    showSnackbar(String.format(LanguageManager.translate("snackbar_backend_error", _currentLanguage.value, "Backend error: %s"), error.message ?: "Network error"))
                }
        }
    }

    /**
     * Check-in WAJIB nonton iklan dulu.
     * Dipanggil SETELAH AdMob callback onUserEarnedReward.
     */
    fun performDailyCheckInWithAd(transactionId: String, vendor: String) {
        viewModelScope.launch {
            try {
                showDailyCheckInModal.value = false

                val body = org.json.JSONObject().apply {
                    put("transactionId", transactionId)
                    put("vendor", vendor)
                }

                ApiClient.dailyCheckInWithAd(body)
                    .onSuccess { json ->
                        val code = json.optString("code", "")
                        if (code == "ALREADY_CHECKED_IN") {
                            showSnackbar("✅ Anda sudah check-in hari ini. Kembali besok!")
                        } else if (json.optBoolean("success", false)) {
                            val points = json.optInt("points", 0)
                            val streak = json.optInt("streak", 0)
                            showSnackbar(String.format(LanguageManager.translate("snackbar_checkin_success", _currentLanguage.value, "🎉 Check-in hari ke-%s! +%s RTP"), streak, points))
                            loadUserFromBackend()
                            user.value?.let { u ->
                                WidgetUpdater.refreshFromRoom(getApplication(), u.id)
                            }
                        } else {
                            showSnackbar(json.optString("message", LanguageManager.translate("vm_checkin_failed", _currentLanguage.value, "Gagal check-in")))
                        }
                    }
                    .onFailure { error ->
                        showSnackbar("Error check-in: ${error.message ?: LanguageManager.translate("vm_network_error", _currentLanguage.value, "Network error")}")
                    }
            } catch (e: Exception) {
                showSnackbar(LanguageManager.translate("vm_error_prefix", _currentLanguage.value, "Error: ") + e.message)
            }
        }
    }

    // ============ GAME — MINING TYCOON ============

    /**
     * Load katalog miner dari backend.
     */
    private var minerCatalogRetryCount = 0
    private val MINER_CATALOG_MAX_RETRY = 5

    fun loadMinerCatalog() {
        viewModelScope.launch {
            try {
                ApiClient.getMinerCatalog().onSuccess { json ->
                    if (json.optBoolean("success", false)) {
                        repository.syncMinerCatalogFromBackend(json)
                        minerCatalogRetryCount = 0  // reset retry
                        AppLogger.d("AffiliateViewModel", "✅ Miner catalog loaded")
                    }
                }.onFailure { error ->
                    val msg = error.message ?: ""
                    AppLogger.w("AffiliateViewModel", "Gagal load catalog: $msg")
                    
                    // Retry bila 401 (token belum ready) — max 5x dengan exponential backoff
                    if (msg.contains("401") && minerCatalogRetryCount < MINER_CATALOG_MAX_RETRY) {
                        minerCatalogRetryCount++
                        val delayMs = 1000L * minerCatalogRetryCount  // 1s, 2s, 3s, 4s, 5s
                        AppLogger.d("AffiliateViewModel", "🔄 Retry loadMinerCatalog ($minerCatalogRetryCount/$MINER_CATALOG_MAX_RETRY) dalam ${delayMs}ms")
                        kotlinx.coroutines.delay(delayMs)
                        loadMinerCatalog()
                    }
                }
            } catch (e: Exception) {
                AppLogger.e("AffiliateViewModel", "loadMinerCatalog error: ${e.message}")
            }
        }
    }

    /**
     * Claim Miner Token dari aktivitas (iklan, check-in, misi).
     */
    fun claimMinerToken(source: String) {
        viewModelScope.launch {
            try {
                val txId = java.util.UUID.randomUUID().toString()
                val body = org.json.JSONObject().apply {
                    put("source", source)
                    put("transactionId", txId)
                }

                ApiClient.claimMinerToken(body).onSuccess { json ->
                    if (json.optBoolean("success", false)) {
                        val tokens = json.optInt("tokens", 0)
                        showSnackbar("+$tokens Miner Token! ⛏️")
                        loadGameState()
                    } else {
                        showSnackbar(json.optString("message", LanguageManager.translate("vm_claim_token_failed", _currentLanguage.value, "Gagal claim token")))
                    }
                }.onFailure {
                    showSnackbar(String.format(LanguageManager.translate("snackbar_network_error", _currentLanguage.value, "Error: %s"), it.message ?: "Unknown"))
                }
            } catch (e: Exception) {
                showSnackbar(LanguageManager.translate("vm_error_prefix", _currentLanguage.value, "Error: ") + e.message)
            }
        }
    }

    /**
     * Unlock miner pakai token.
     */
    fun unlockMiner(catalogId: String) {
        viewModelScope.launch {
            try {
                val body = org.json.JSONObject().apply {
                    put("catalogId", catalogId)
                }

                ApiClient.unlockMiner(body).onSuccess { json ->
                    if (json.optBoolean("success", false)) {
                        showSnackbar(json.optString("message", LanguageManager.translate("vm_miner_unlocked", _currentLanguage.value, "Miner berhasil di-unlock!")))
                        loadGameState()
                        loadMinerCatalog()
                    } else {
                        showSnackbar(json.optString("message", LanguageManager.translate("vm_unlock_miner_failed", _currentLanguage.value, "Gagal unlock miner")))
                    }
                }.onFailure {
                    showSnackbar(String.format(LanguageManager.translate("snackbar_network_error", _currentLanguage.value, "Error: %s"), it.message ?: "Unknown"))
                }
            } catch (e: Exception) {
                showSnackbar(LanguageManager.translate("vm_error_prefix", _currentLanguage.value, "Error: ") + e.message)
            }
        }
    }

    /**
     * Load game state dari backend.
     * Panggil saat user buka GameHub.
     */
    fun loadGameState() {
        viewModelScope.launch {
            try {
                ApiClient.getGameState().onSuccess { json ->
                    if (json.optBoolean("success", false)) {
                        repository.syncGameStateFromBackend(json)
                        AppLogger.d("AffiliateViewModel", "✅ Game state loaded")
                    }
                }.onFailure { error ->
                    AppLogger.w("AffiliateViewModel", "Gagal load game state: ${error.message}")
                    // AdGuard detection sudah di-set dalam ApiClient
                    if (error is java.net.UnknownHostException ||
                        error is java.net.SocketTimeoutException) {
                        AppLogger.w("AffiliateViewModel", "⚠️ Kemungkinan AdGuard block!")
                    }
                }
            } catch (e: Exception) {
                AppLogger.e("AffiliateViewModel", "loadGameState error: ${e.message}")
            }
        }
    }

    /**
     * FIX RACE CONDITION: Load game state DULU, baru refresh miner active.
     */
    fun loadGameStateThenRefreshMiner() {
        viewModelScope.launch {
            
            // STEP 1: Fetch API
            try {
                val result = ApiClient.getGameState()
                
                result.onSuccess { json ->
                    
                    val success = json.optBoolean("success", false)
                    
                    if (success) {
                        // Cek room & minerActiveUntil di response
                        val data = json.optJSONObject("data")
                        val room = data?.optJSONObject("room")
                        val minerActiveUntilFromApi = room?.optLong("minerActiveUntil", -1L)
                        
                        // Sync ke Room DB
                        repository.syncGameStateFromBackend(json)
                    }
                }.onFailure { error ->
                }
            } catch (e: Exception) {
            }
            
            // STEP 2: Baca dari Room DB
            try {
                val room = repository.getGameRoomStateOnce()
                
                if (room == null) {
                    return@launch
                }
                
                
                val now = System.currentTimeMillis()
                if (room.minerActiveUntil > _minerActiveUntil.value) { _minerActiveUntil.value = room.minerActiveUntil }
                val remaining = (_minerActiveUntil.value - now).coerceAtLeast(0L)
                _minerActiveRemainingMs.value = remaining
                
                
                if (remaining > 0L) {
                    startMinerCountdownTicker()
                } else {
                    stopMinerCountdownTicker()
                }
            } catch (e: Exception) {
            }
            
        }
    }

    /**
     * Claim mining points — sync ke backend.
     */
    // ============ MINING CLAIM (Wajib Iklan + Cooldown 1 Jam) ============
    private val _miningCooldownRemainingMs = MutableStateFlow(0L)
    val miningCooldownRemainingMs: StateFlow<Long> = _miningCooldownRemainingMs.asStateFlow()

    private val _miningClaimInProgress = MutableStateFlow(false)
    val miningClaimInProgress: StateFlow<Boolean> = _miningClaimInProgress.asStateFlow()

    // Flag: user trigger claim dari mining → tunggu callback AdMob
    private val _pendingMiningClaim = MutableStateFlow(false)
    val pendingMiningClaim: StateFlow<Boolean> = _pendingMiningClaim.asStateFlow()

    fun setPendingMiningClaim(v: Boolean) { _pendingMiningClaim.value = v }

    /**
     * Refresh countdown cooldown dari room state.
     */
    fun refreshMiningCooldown() {
        viewModelScope.launch {
            try {
                val room = repository.getGameRoomStateOnce() ?: return@launch
                val last = room.lastMiningClaimAt
                if (last <= 0L) {
                    _miningCooldownRemainingMs.value = 0L
                    return@launch
                }
                val next = last + 60L * 60L * 1000L
                val remaining = (next - System.currentTimeMillis()).coerceAtLeast(0L)
                _miningCooldownRemainingMs.value = remaining
            } catch (e: Exception) {
                AppLogger.w("AffiliateViewModel", "refreshMiningCooldown error: ${e.message}")
            }
        }
    }

    /**
     * Claim mining dengan iklan. transactionId dari AdMob callback.
     */
    // ============ THERMAL STATE ============
    // ============ ACHIEVEMENT ============
    private val _achievements = MutableStateFlow<List<com.inkside.digital.data.model.AchievementEntity>>(emptyList())
    val achievements: StateFlow<List<com.inkside.digital.data.model.AchievementEntity>> = _achievements.asStateFlow()

    private val _achievementStats = MutableStateFlow(com.inkside.digital.data.model.AchievementStats())
    val achievementStats: StateFlow<com.inkside.digital.data.model.AchievementStats> = _achievementStats.asStateFlow()

    private val _achievementLoading = MutableStateFlow(false)
    val achievementLoading: StateFlow<Boolean> = _achievementLoading.asStateFlow()

    fun loadAchievements() {
        viewModelScope.launch {
            _achievementLoading.value = true
            try {
                val result = com.inkside.digital.data.network.ApiClient.getMyAchievements()
                result.getOrNull()?.let { json ->
                    val dataArr = json.optJSONArray("data")
                    if (dataArr != null) {
                        val list = mutableListOf<com.inkside.digital.data.model.AchievementEntity>()
                        for (i in 0 until dataArr.length()) {
                            val obj = dataArr.getJSONObject(i)
                            val rewardObj = obj.optJSONObject("reward")
                            list.add(
                                com.inkside.digital.data.model.AchievementEntity(
                                    id = obj.optString("id", ""),
                                    name = obj.optString("name", ""),
                                    description = obj.optString("description", ""),
                                    category = obj.optString("category", ""),
                                    tier = obj.optString("tier", "BRONZE"),
                                    icon = obj.optString("icon", "🏆"),
                                    progress = obj.optInt("progress", 0),
                                    target = obj.optInt("target", 1),
                                    unlocked = obj.optBoolean("unlocked", false),
                                    claimed = obj.optBoolean("claimed", false),
                                    reward = rewardObj?.let { r ->
                                        com.inkside.digital.data.model.AchievementReward(
                                            rtp = r.optDouble("rtp", 0.0),
                                            badge = r.optString("badge").takeIf { it.isNotBlank() && it != "null" },
                                            frame = r.optString("frame").takeIf { it.isNotBlank() && it != "null" }
                                        )
                                    }
                                )
                            )
                        }
                        _achievements.value = list
                    }

                    val statsObj = json.optJSONObject("stats")
                    if (statsObj != null) {
                        _achievementStats.value = com.inkside.digital.data.model.AchievementStats(
                            total = statsObj.optInt("total", 0),
                            unlocked = statsObj.optInt("unlocked", 0),
                            claimed = statsObj.optInt("claimed", 0),
                            claimable = statsObj.optInt("claimable", 0)
                        )
                    }
                }
            } catch (e: Exception) {
                AppLogger.e("Achievement", "load error: ${e.message}", e)
            } finally {
                _achievementLoading.value = false
            }
        }
    }

    fun claimAchievement(id: String, onResult: (Boolean, String) -> Unit = { _, _ -> }) {
        viewModelScope.launch {
            try {
                val result = com.inkside.digital.data.network.ApiClient.claimAchievement(id)
                result.getOrNull()?.let { json ->
                    if (json.optBoolean("success", false)) {
                        loadAchievements()
                        onResult(true, json.optString("message", LanguageManager.translate("vm_reward_claimed", _currentLanguage.value, "Reward berhasil diklaim")))
                    } else {
                        onResult(false, json.optString("message", LanguageManager.translate("vm_claim_failed", _currentLanguage.value, "Gagal klaim")))
                    }
                } ?: run {
                    onResult(false, LanguageManager.translate("vm_connection_failed", _currentLanguage.value, "Koneksi gagal"))
                }
            } catch (e: Exception) {
                onResult(false, LanguageManager.translate("vm_error_prefix", _currentLanguage.value, "Error: ") + e.message)
            }
        }
    }

    // ============ PURCHASE / IAP ============
    private val _purchaseTiers = MutableStateFlow<List<com.inkside.digital.data.model.PurchaseTier>>(emptyList())
    val purchaseTiers: StateFlow<List<com.inkside.digital.data.model.PurchaseTier>> = _purchaseTiers.asStateFlow()

    private val _purchaseLoading = MutableStateFlow(false)
    val purchaseLoading: StateFlow<Boolean> = _purchaseLoading.asStateFlow()

    private val _purchaseMessage = MutableStateFlow("")
    val purchaseMessage: StateFlow<String> = _purchaseMessage.asStateFlow()

    // ============================================================
    // RTP PACK / MINER PACK / BUNDLE (v1.16)
    // ============================================================
    private val _rtpPacks = MutableStateFlow<List<org.json.JSONObject>>(emptyList())
    val rtpPacks: StateFlow<List<org.json.JSONObject>> = _rtpPacks.asStateFlow()

    private val _minerPacks = MutableStateFlow<List<org.json.JSONObject>>(emptyList())
    val minerPacks: StateFlow<List<org.json.JSONObject>> = _minerPacks.asStateFlow()

    private val _bundles = MutableStateFlow<List<org.json.JSONObject>>(emptyList())
    val bundles: StateFlow<List<org.json.JSONObject>> = _bundles.asStateFlow()

    fun loadRtpPacks() {
        viewModelScope.launch {
            try {
                val response = com.inkside.digital.data.network.ApiClient.apiService.getRtpPacks()
                val json = org.json.JSONObject(response.string())
                val arr = json.optJSONArray("data") ?: org.json.JSONArray()
                val list = mutableListOf<org.json.JSONObject>()
                for (i in 0 until arr.length()) {
                    list.add(arr.getJSONObject(i))
                }
                _rtpPacks.value = list
                AppLogger.d("ReferralVM", "✅ ${list.size} RTP packs loaded")
            } catch (e: Exception) {
                AppLogger.e("ReferralVM", "❌ loadRtpPacks error: ${e.message}", e)
            }
        }
    }

    fun loadMinerPacks() {
        viewModelScope.launch {
            try {
                val response = com.inkside.digital.data.network.ApiClient.apiService.getMinerPacks()
                val json = org.json.JSONObject(response.string())
                val arr = json.optJSONArray("data") ?: org.json.JSONArray()
                val list = mutableListOf<org.json.JSONObject>()
                for (i in 0 until arr.length()) {
                    list.add(arr.getJSONObject(i))
                }
                _minerPacks.value = list
                AppLogger.d("ReferralVM", "✅ ${list.size} Miner packs loaded")
            } catch (e: Exception) {
                AppLogger.e("ReferralVM", "❌ loadMinerPacks error: ${e.message}", e)
            }
        }
    }

    fun loadBundles() {
        viewModelScope.launch {
            try {
                val response = com.inkside.digital.data.network.ApiClient.apiService.getBundles()
                val json = org.json.JSONObject(response.string())
                val arr = json.optJSONArray("data") ?: org.json.JSONArray()
                val list = mutableListOf<org.json.JSONObject>()
                for (i in 0 until arr.length()) {
                    list.add(arr.getJSONObject(i))
                }
                _bundles.value = list
                AppLogger.d("ReferralVM", "✅ ${list.size} Bundles loaded")
            } catch (e: Exception) {
                AppLogger.e("ReferralVM", "❌ loadBundles error: ${e.message}", e)
            }
        }
    }

    fun loadPurchaseTiers() {
        viewModelScope.launch {
            _purchaseLoading.value = true
            try {
                val result = com.inkside.digital.data.network.ApiClient.getPurchaseTiersPublic()
                result.getOrNull()?.let { json ->
                    val arr = json.optJSONArray("data")
                    if (arr != null) {
                        val list = mutableListOf<com.inkside.digital.data.model.PurchaseTier>()
                        for (i in 0 until arr.length()) {
                            val o = arr.getJSONObject(i)
                            val benefitsArr = o.optJSONArray("benefits")
                            val benefits = mutableListOf<String>()
                            if (benefitsArr != null) {
                                for (j in 0 until benefitsArr.length()) {
                                    benefits.add(benefitsArr.getString(j))
                                }
                            }
                            list.add(
                                com.inkside.digital.data.model.PurchaseTier(
                                    productId = o.optString("productId", ""),
                                    tier = o.optString("tier", "FREE"),
                                    displayName = o.optString("displayName", ""),
                                    priceUsd = o.optDouble("priceUsd", 0.0),
                                    maxMinerSlots = o.optInt("maxMinerSlots", 0),
                                    affiliateRate = o.optDouble("affiliateRate", 0.0),
                                    tierOrder = o.optInt("tierOrder", 0),
                                    benefits = benefits
                                )
                            )
                        }
                        _purchaseTiers.value = list.sortedBy { it.tierOrder }
                    }
                }
            } catch (e: Exception) {
                AppLogger.e("Purchase", "load error: ${e.message}", e)
            } finally {
                _purchaseLoading.value = false
            }
        }
    }

    fun handlePurchaseSuccess(productId: String, purchaseToken: String, packageName: String) {
        viewModelScope.launch {
            _purchaseMessage.value = LanguageManager.translate("vm_verifying_purchase", _currentLanguage.value, "Memverifikasi pembelian...")
            try {
                val body = org.json.JSONObject().apply {
                    put("purchaseToken", purchaseToken)
                    put("productId", productId)
                    put("packageName", packageName)
                }
                val result = com.inkside.digital.data.network.ApiClient.verifyPurchase(body)
                result.getOrNull()?.let { json ->
                    if (json.optBoolean("success", false)) {
                        _purchaseMessage.value = "✅ " + json.optString("message", LanguageManager.translate("vm_upgrade_success", _currentLanguage.value, "Upgrade berhasil!"))
                        // Refresh profile
                        loadProfileFromBackend()
                    } else {
                        _purchaseMessage.value = "❌ " + json.optString("message", LanguageManager.translate("vm_verification_failed", _currentLanguage.value, "Verifikasi gagal"))
                    }
                } ?: run {
                    _purchaseMessage.value = "❌ Koneksi gagal"
                }
            } catch (e: Exception) {
                _purchaseMessage.value = "❌ Error: ${e.message}"
            }
        }
    }

    fun clearPurchaseMessage() {
        _purchaseMessage.value = ""
    }

    private val _batteryTemp = MutableStateFlow(0f)
    val batteryTemp: StateFlow<Float> = _batteryTemp.asStateFlow()

    private val _batteryLevel = MutableStateFlow(0)
    val batteryLevel: StateFlow<Int> = _batteryLevel.asStateFlow()

    private val _isCharging = MutableStateFlow(false)
    val isCharging: StateFlow<Boolean> = _isCharging.asStateFlow()

    private val _tempStatus = MutableStateFlow(BatteryTempHelper.TempStatus.NORMAL)
    val tempStatus: StateFlow<BatteryTempHelper.TempStatus> = _tempStatus.asStateFlow()

    private val _userRegion = MutableStateFlow(LanguageManager.translate("vm_unknown", _currentLanguage.value, "Unknown"))
    val userRegion: StateFlow<String> = _userRegion.asStateFlow()

    // ============ MINING PAUSE STATE (dari backend) ============
    private val _miningPaused = MutableStateFlow(false)
    val miningPaused: StateFlow<Boolean> = _miningPaused.asStateFlow()

    private val _pauseReason = MutableStateFlow("")
    val pauseReason: StateFlow<String> = _pauseReason.asStateFlow()

    // Alert dialog "HP terlalu panas, mining dijeda"
    val showThermalPauseAlert = androidx.compose.runtime.mutableStateOf(false)

    private val _hashrateFactor = MutableStateFlow(1.0f)
    val hashrateFactor: StateFlow<Float> = _hashrateFactor.asStateFlow()

    private var thermalMonitorJob: kotlinx.coroutines.Job? = null

    // ============ MINER ACTIVE STATE (6 jam) ============
    private val _minerActiveUntil = MutableStateFlow(0L)
    val minerActiveUntil: StateFlow<Long> = _minerActiveUntil.asStateFlow()

    private val _minerActiveRemainingMs = MutableStateFlow(0L)
    val minerActiveRemainingMs: StateFlow<Long> = _minerActiveRemainingMs.asStateFlow()

    private val _pendingMinerActivate = MutableStateFlow(false)
    val pendingMinerActivate: StateFlow<Boolean> = _pendingMinerActivate.asStateFlow()
    fun setPendingMinerActivate(v: Boolean) { _pendingMinerActivate.value = v }

    fun isMinerActive(): Boolean = _minerActiveUntil.value > System.currentTimeMillis()

    // === COUNTDOWN TICKER — update minerActiveRemainingMs setiap detik ===
    private var minerCountdownJob: kotlinx.coroutines.Job? = null

    fun startMinerCountdownTicker() {
        minerCountdownJob?.cancel()
        minerCountdownJob = viewModelScope.launch {
            while (true) {
                val now = System.currentTimeMillis()
                val remaining = (_minerActiveUntil.value - now).coerceAtLeast(0L)
                _minerActiveRemainingMs.value = remaining
                if (remaining <= 0L) break
                kotlinx.coroutines.delay(1000L)   // tick tiap detik
            }
            _minerActiveRemainingMs.value = 0L
        }
    }

    fun stopMinerCountdownTicker() {
        minerCountdownJob?.cancel()
        minerCountdownJob = null
    }

    fun refreshMinerActiveState() {
        viewModelScope.launch {
            try {
                val room = repository.getGameRoomStateOnce() ?: return@launch
                val now = System.currentTimeMillis()
                _minerActiveUntil.value = room.minerActiveUntil
                val remaining = (room.minerActiveUntil - now).coerceAtLeast(0L)
                _minerActiveRemainingMs.value = remaining
                // Start ticker kalau miner aktif
                if (remaining > 0L) {
                    startMinerCountdownTicker()
                }
            } catch (e: Exception) {
                AppLogger.w("AffiliateViewModel", "refreshMinerActiveState error: ${e.message}")
            }
        }
    }

    fun activateMinerWithAd(transactionId: String, vendor: String) {
        viewModelScope.launch {
            try {
                ApiClient.activateMiner(transactionId, vendor)
                    .onSuccess { json ->
                        if (json.optBoolean("success", false)) {
                            val activeUntil = json.optLong("minerActiveUntil", 0L)
                            _minerActiveUntil.value = activeUntil
                            _minerActiveRemainingMs.value = (activeUntil - System.currentTimeMillis()).coerceAtLeast(0L)
                            startMinerCountdownTicker()   // ← Start ticker
                            showSnackbar("⛏️ Miner aktif 6 jam!")
                            // ❌ JANGAN panggil loadGameState() di sini
                            // karena akan re-fetch dan reset _minerActiveUntil
                            // loadGameState()
                            // loadUserFromBackend()
                        } else {
                            val errMsg = json.optString("message", LanguageManager.translate("vm_activate_miner_failed", _currentLanguage.value, "Gagal aktifkan miner"))
                            showSnackbar(errMsg)
                        }
                    }
                    .onFailure {
                        showSnackbar(LanguageManager.translate("vm_error_prefix", _currentLanguage.value, "Error: ") + it.message)
                    }
            } catch (e: Exception) {
                showSnackbar(LanguageManager.translate("vm_activation_error_prefix", _currentLanguage.value, "Aktivasi error: ") + e.message)
            }
        }
    }

    // ============ THERMAL MONITORING ============
    // ============ Observe Config Refresh (dari FCM) ============
    private var configRefreshObserverJob: kotlinx.coroutines.Job? = null

    fun startConfigRefreshObserver() {
        configRefreshObserverJob?.cancel()
        configRefreshObserverJob = viewModelScope.launch {
            androidx.compose.runtime.snapshotFlow {
                com.inkside.digital.data.network.ApiClient.configRefreshEvent
            }
                .filterNotNull()
                .collect { event ->
                    AppLogger.d("ViewModel", "Config refresh: ${event.configType}")

                    when (event.configType) {
                        "game" -> loadGameState()
                        "ads" -> loadGameState()
                        "daily" -> { /* next check-in */ }
                        "thermal" -> {
                            try {
                                if (_batteryTemp.value > 0f) {
                                    val body = org.json.JSONObject().apply {
                                        put("phone_temp", _batteryTemp.value)
                                        put("battery_level", _batteryLevel.value)
                                        put("is_charging", _isCharging.value)
                                    }
                                    com.inkside.digital.data.network.ApiClient.updateThermal(body)
                                }
                            } catch (e: Exception) {}
                        }
                    }

                    com.inkside.digital.data.network.ApiClient.consumeConfigRefresh()
                }
        }
    }

    fun stopConfigRefreshObserver() {
        configRefreshObserverJob?.cancel()
        configRefreshObserverJob = null
    }

    // ============ MAINTENANCE MODE ============
    private val _maintenanceMode = MutableStateFlow(false)
    val maintenanceMode: StateFlow<Boolean> = _maintenanceMode.asStateFlow()

    private val _maintenanceMessage = MutableStateFlow("")
    val maintenanceMessage: StateFlow<String> = _maintenanceMessage.asStateFlow()

    private var maintenanceCheckJob: kotlinx.coroutines.Job? = null

    fun startMaintenanceCheck() {
        maintenanceCheckJob?.cancel()
        maintenanceCheckJob = viewModelScope.launch {
            while (true) {
                try {
                    val result = com.inkside.digital.data.network.ApiClient.checkHealth()
                    result.getOrNull()?.let { json ->
                        val isMaint = json.optBoolean("maintenance", false)
                        val msg = json.optString("maintenanceMessage", "")
                        _maintenanceMode.value = isMaint
                        _maintenanceMessage.value = msg
                        com.inkside.digital.data.network.ApiClient.setMaintenanceMode(isMaint, msg)
                    }
                } catch (e: Exception) {
                    // Silent
                }
                kotlinx.coroutines.delay(30_000L)
            }
        }
    }

    fun stopMaintenanceCheck() {
        maintenanceCheckJob?.cancel()
        maintenanceCheckJob = null
    }

    fun startThermalMonitoring() {
        thermalMonitorJob?.cancel()
        thermalMonitorJob = viewModelScope.launch {
            BatteryTempHelper.startMonitoring(getApplication(), this) { temp, level, charging ->
                _batteryTemp.value = temp
                _batteryLevel.value = level
                _isCharging.value = charging
                _tempStatus.value = BatteryTempHelper.getTempStatus(temp)

                // ============================================================
                // A. LOCAL — Hitung hashrate factor + auto-pause (TIDAK kirim backend)
                // ============================================================
                val factor = calculateLocalHashrateFactor(temp, level, charging)
                _hashrateFactor.value = factor

                val (shouldPause, pauseReason) = calculatePauseState(temp, level)
                _miningPaused.value = shouldPause
                _pauseReason.value = pauseReason

                // ============================================================
                // B. SYNC ke BACKEND — 1 jam atau event penting
                // ============================================================
                val now = System.currentTimeMillis()
                val timeSinceSync = now - lastThermalSync
                val tempChanged = kotlin.math.abs(temp - lastTempSync) > 5f
                val chargingChanged = charging != lastChargingSync
                val batteryCritical = level < 15 && lastLevelSync >= 15

                val shouldSync = timeSinceSync > 3600000L  // 1 jam
                    || tempChanged
                    || chargingChanged
                    || batteryCritical

                if (shouldSync) {
                    lastThermalSync = now
                    lastTempSync = temp
                    lastChargingSync = charging
                    lastLevelSync = level
                    syncThermalToBackend(temp, level, charging)
                }
            }
        }
    }

    fun stopThermalMonitoring() {
        BatteryTempHelper.stopMonitoring()
        thermalMonitorJob?.cancel()
        thermalMonitorJob = null
    }

    private var lastThermalSync = 0L
    private var lastTempSync = 0f
    private var lastChargingSync = false
    private var lastLevelSync = 100

    /**
     * Hitung hashrate factor LOCAL — tidak butuh backend.
     * Multi-factor: thermal penalty + battery penalty + charging boost.
     */
    private fun calculateLocalHashrateFactor(temp: Float, level: Int, charging: Boolean): Float {
        var factor = 1.0f

        // Thermal penalty
        factor *= when {
            temp < 35f -> 1.0f
            temp < 45f -> 0.9f
            temp < 55f -> 0.7f
            temp < 60f -> 0.5f
            else -> 0.25f
        }

        // Battery penalty
        factor *= when {
            level >= 50 -> 1.0f
            level >= 30 -> 0.9f
            level >= 15 -> 0.75f
            level >= 5 -> 0.5f
            else -> 0f
        }

        // Charging boost
        if (charging) factor *= 1.2f

        return factor.coerceIn(0f, 1.5f)
    }

    /**
     * Hitung auto-pause state LOCAL.
     * Pause kalau suhu >= 60°C atau battery < 5%.
     */
    private fun calculatePauseState(temp: Float, level: Int): Pair<Boolean, String> {
        return when {
            level < 5 -> true to "battery_critical"
            temp >= 60f -> true to "temp_critical"
            else -> false to ""
        }
    }

    private fun syncThermalToBackend(temp: Float, level: Int, charging: Boolean) {
        viewModelScope.launch {
            try {
                val body = org.json.JSONObject().apply {
                    put("phone_temp", temp)
                    put("battery_level", level)
                    put("is_charging", charging)
                }
                val result = ApiClient.updateThermal(body)
                result.getOrNull()?.let {
                    // Sync sukses — record ke backend untuk analytics
                    // Hashrate factor + pause state DIHITUNG LOCAL, tidak dari backend
                    AppLogger.d("Thermal", "Sync OK: temp=$temp, level=$level, charging=$charging")
                } ?: run {
                    AppLogger.w("Thermal", "Sync failed: ${result.exceptionOrNull()?.message}")
                }
            } catch (e: Exception) {
                AppLogger.w("Thermal", "Sync error: ${e.message}")
            }
        }
    }

    // ============ LOCATION ============
    fun fetchUserRegion() {
        viewModelScope.launch {
            try {
                val location = LocationHelper.getLastLocation(getApplication())
                    ?: LocationHelper.getCurrentLocation(getApplication())
                val summary = LocationHelper.getLocationSummary(location)
                _userRegion.value = summary.region
                AppLogger.d("Location", "Region: ${summary.region}, Lat: ${summary.latitude}, Lon: ${summary.longitude}")

                // Kirim ke backend
                val body = org.json.JSONObject().apply {
                    put("region", summary.region)
                    put("latitude", summary.latitude)
                    put("longitude", summary.longitude)
                }
                ApiClient.updateLocation(body)
            } catch (e: Exception) {
                AppLogger.w("Location", "Error: ${e.message}")
            }
        }
    }

    // ============ AUTO-CLAIM MINING ============
    private var autoClaimJob: kotlinx.coroutines.Job? = null

    private val _autoClaimActive = MutableStateFlow(false)
    val autoClaimActive: StateFlow<Boolean> = _autoClaimActive.asStateFlow()

    fun startAutoClaim() {
        if (autoClaimJob?.isActive == true) return
        _autoClaimActive.value = true
        autoClaimJob = viewModelScope.launch {
            AppLogger.d("AffiliateViewModel", "▶️ Auto-claim started (3h interval)")
            while (isActive) {
                delay(3 * 60 * 60 * 1000L)  // 3 jam
                try {
                    // Check miner active dulu — skip kalau tamat
                    if (!isMinerActive()) {
                        AppLogger.d("AffiliateViewModel", "⏸️ Miner inactive — auto-claim skip")
                        refreshMinerActiveState()
                        continue
                    }
                    val result = ApiClient.claimMining()
                    result.onSuccess { json ->
                        if (json.optBoolean("success", false)) {
                            val points = json.optDouble("points", 0.0)
                            if (points > 0) {
                                AppLogger.d("AffiliateViewModel", "✅ Auto-claim: +$points RTP")
                                loadUserFromBackend()
                                loadGameState()
                            }
                        } else if (json.optString("code") == "MINER_INACTIVE") {
                            AppLogger.d("AffiliateViewModel", "⏸️ Miner inactive (backend)")
                            refreshMinerActiveState()
                        }
                    }
                } catch (e: Exception) {
                    AppLogger.w("AffiliateViewModel", "Auto-claim error: ${e.message}")
                }
            }
        }
    }

    fun stopAutoClaim() {
        autoClaimJob?.cancel()
        autoClaimJob = null
        _autoClaimActive.value = false
        AppLogger.d("AffiliateViewModel", "⏸️ Auto-claim stopped")
    }

    fun claimMiningWithAd(transactionId: String, vendor: String) {
        viewModelScope.launch {
            _miningClaimInProgress.value = true

            val result = ApiClient.claimMiningWithAd(transactionId, vendor)

            result.onSuccess { json ->
                if (json.optBoolean("success", false)) {
                    val points = json.optDouble("points", 0.0)  // FIX: guna optDouble untuk 8 decimals
                    val nextClaimAt = json.optLong("nextClaimAt", 0L)
                    val remaining = (nextClaimAt - System.currentTimeMillis()).coerceAtLeast(0L)
                    _miningCooldownRemainingMs.value = remaining
                    showSnackbar(String.format(LanguageManager.translate("snackbar_mining_claim_success", _currentLanguage.value, "🎉 Berhasil klaim %s RTP!"), String.format(java.util.Locale.US, "%.8f", points)))

                    // CRITICAL FIX: Reset Room DB DULU (sebelum refresh)
                    repository.resetMiningAfterClaim(nextClaimAt)

                    // Kemudian refresh user + game state dari backend
                    loadUserFromBackend()
                    loadGameState()
                } else {
                    val msg = json.optString("message", LanguageManager.translate("vm_claim_failed", _currentLanguage.value, "Gagal klaim"))
                    val remaining = json.optLong("cooldownRemainingMs", 0L)
                    val httpCode = json.optInt("httpCode", 0)
                    
                    AppLogger.w("ClaimMining", "Failed: HTTP $httpCode — $msg")
                    
                    if (remaining > 0) {
                        _miningCooldownRemainingMs.value = remaining
                    }
                    
                    showSnackbar("⚠️ $msg")
                }
            }.onFailure {
                AppLogger.e("ClaimMining", "Network error: ${it.message}")
                showSnackbar("Error: ${it.message ?: LanguageManager.translate("vm_network_error", _currentLanguage.value, "Network error")}")
            }

            _miningClaimInProgress.value = false
        }
    }

    fun claimMiningFromBackend() {
        viewModelScope.launch {
            try {
                // ✅ FIX: HANYA backend claim (bukan local)
                // Backend hitung fresh dari unclaimedMiningPoints + elapsed
                ApiClient.claimMining().onSuccess { json ->
                    if (json.optBoolean("success", false)) {
                        // 1. Reset Room DB state (unclaimed = 0)
                        val nextClaimAt = json.optLong("nextClaimAt", System.currentTimeMillis() + 3600000L)
                        repository.resetMiningAfterClaim(nextClaimAt)
                        
                        // 2. Sync user + game state dari backend
                        loadGameState()
                        loadUserFromBackend()
                        
                        // 3. Snackbar sukses
                        val points = json.optDouble("pointsAwarded", 0.0)
                        showSnackbar(String.format(LanguageManager.translate("snackbar_mining_claim_success", _currentLanguage.value, "Berhasil klaim %s RTP! ⛏️"), points.toInt()))
                    } else {
                        showSnackbar(json.optString("message", LanguageManager.translate("vm_claim_failed", _currentLanguage.value, "Gagal claim")))
                    }
                }.onFailure {
                    showSnackbar(it.message ?: LanguageManager.translate("vm_claim_failed", _currentLanguage.value, "Gagal claim"))
                }
            } catch (e: Exception) {
                showSnackbar(LanguageManager.translate("vm_error_prefix", _currentLanguage.value, "Error: ") + e.message)
            }
        }
    }

    /**
     * Beli miner — sync ke backend.
     */
    fun buyMinerFromBackend(itemId: String) {
        viewModelScope.launch {
            try {
                // 1. Beli lokal
                val localResult = repository.buyGameMinerItem(itemId)
                localResult.onSuccess { msg ->
                    showSnackbar(msg)
                }.onFailure {
                    showSnackbar(it.message ?: LanguageManager.translate("vm_buy_failed", _currentLanguage.value, "Gagal beli"))
                    return@launch
                }

                // 2. Sync ke backend — ambil item dari Room
                val item = repository.getGameMinerItemByIdSync(itemId)
                if (item != null) {
                    val body = org.json.JSONObject().apply {
                        put("catalogId", item.catalogId)
                        put("instanceId", item.id)
                        put("tier", item.tier)
                        put("name", item.name)
                        put("tokenCost", item.tokenCost)
                        put("powerHs", item.powerHs)
                        put("pointsPerMinute", item.pointsPerMinute)
                        put("iconEmoji", item.iconEmoji)
                    }
                    ApiClient.buyMiner(body)
                }

                loadGameState()
                loadUserFromBackend()
            } catch (e: Exception) {
                showSnackbar(LanguageManager.translate("vm_error_prefix", _currentLanguage.value, "Error: ") + e.message)
            }
        }
    }

    /**
     * Pasang miner — sync ke backend.
     */
    fun placeMinerFromBackend(itemId: String, slotIndex: Int) {
        viewModelScope.launch {
            try {
                // 1. Pasang lokal
                repository.toggleMinerSlot(itemId, slotIndex)

                // 2. Sync ke backend
                val body = org.json.JSONObject().apply {
                    put("minerId", itemId)
                    put("slotIndex", slotIndex)
                }
                ApiClient.placeMiner(body)

                loadGameState()
            } catch (e: Exception) {
                showSnackbar(LanguageManager.translate("vm_error_prefix", _currentLanguage.value, "Error: ") + e.message)
            }
        }
    }

    /**
     * Lepas miner — sync ke backend.
     */
    fun unplaceMinerFromBackend(itemId: String) {
        viewModelScope.launch {
            try {
                val body = org.json.JSONObject().apply {
                    put("minerId", itemId)
                }
                ApiClient.unplaceMiner(body)

                // Update lokal — set isPlacedInRoom = false
                repository.toggleMinerSlot(itemId, -1)

                loadGameState()
            } catch (e: Exception) {
                showSnackbar(LanguageManager.translate("vm_error_prefix", _currentLanguage.value, "Error: ") + e.message)
            }
        }
    }

    fun claimMissionReward(missionId: String) {
        viewModelScope.launch {
            val currentUser = user.value ?: return@launch
            val res = repository.claimMissionReward(missionId)
            res.onSuccess { reward ->
                showSnackbar(String.format(LanguageManager.translate("snackbar_mission_reward", _currentLanguage.value, "Berhasil klaim hadiah misi! +%s RTP 🏆"), reward))

                // Kirim ke backend
                ApiClient.claimMission(missionId, currentUser.id)
                    .onSuccess { json ->
                        if (!json.optBoolean("success", false)) {
                            showSnackbar("Backend: " + json.optString("message", LanguageManager.translate("vm_failed", _currentLanguage.value, "Gagal")))
                        } else {
                            loadUserFromBackend()
                        }
                    }
                    .onFailure { error ->
                        showSnackbar(String.format(LanguageManager.translate("snackbar_backend_error", _currentLanguage.value, "Backend error: %s"), error.message ?: LanguageManager.translate("vm_network_error", _currentLanguage.value, "Network error")))
                    }
            }.onFailure {
                showSnackbar(it.message ?: LanguageManager.translate("vm_mission_claim_failed", _currentLanguage.value, "Gagal klaim hadiah misi"))
            }
        }
    }

    fun completeTaskAction(missionId: String) {
        viewModelScope.launch {
            val currentUser = user.value ?: return@launch
            val res = repository.completeTaskAction(missionId)
            res.onSuccess { msg ->
                showSnackbar(msg)

                // Kirim ke backend
                ApiClient.completeMission(missionId, currentUser.id)
                    .onSuccess { json ->
                        if (!json.optBoolean("success", false)) {
                            showSnackbar("Backend: " + json.optString("message", LanguageManager.translate("vm_failed", _currentLanguage.value, "Gagal")))
                        }
                    }
                    .onFailure { error ->
                        showSnackbar(String.format(LanguageManager.translate("snackbar_backend_error", _currentLanguage.value, "Backend error: %s"), error.message ?: LanguageManager.translate("vm_network_error", _currentLanguage.value, "Network error")))
                    }
            }.onFailure {
                showSnackbar(it.message ?: LanguageManager.translate("vm_task_failed", _currentLanguage.value, "Gagal memproses tugas"))
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
                showSnackbar(String.format(LanguageManager.translate("snackbar_transfer_success", _currentLanguage.value, "Berhasil transfer %s RTP ke %s!"), result.sentPoints, result.recipientName))
            }.onFailure { err ->
                showSnackbar(LanguageManager.translate("vm_transfer_failed_prefix", _currentLanguage.value, "Transfer gagal: ") + (err.message ?: ""))
            }
        }
    }

    fun dismissTransferReceipt() {
        transferSuccessReceipt.value = null
    }

    /**
     * Sync data user dari Firebase Auth (photoUrl, name, email) ke Room DB.
     * Dipanggil bila user login pertama kali / profile berubah.
     */
    fun syncUserFromFirebase(photoUrl: String?, name: String?, email: String?) {
        viewModelScope.launch {
            try {
                repository.updateUserPhotoAndProfile(photoUrl, name, email)
                AppLogger.d("AffiliateViewModel", "✅ User synced from Firebase: photoUrl=$photoUrl")
            } catch (e: Exception) {
                AppLogger.e("AffiliateViewModel", "syncUserFromFirebase error: ${e.message}", e)
            }
        }
    }

    fun updateUserLocation(lat: Double, lng: Double, city: String, province: String, accuracyMeters: Float = 10f) {
        viewModelScope.launch {
            repository.updateUserLocation(lat, lng, city, province, accuracyMeters)
            showSnackbar(LanguageManager.translate("vm_location_updated_prefix", _currentLanguage.value, "Lokasi diperbarui: ") + "$city, $province 📍")
        }
    }

    fun toggleLocationTracking(allowed: Boolean) {
        viewModelScope.launch {
            repository.toggleUserLocationTracking(allowed)
            showSnackbar(if (allowed) LanguageManager.translate("vm_location_tracking_on", _currentLanguage.value, "Pelacakan lokasi aktif untuk analitik provider.") else LanguageManager.translate("vm_location_tracking_off", _currentLanguage.value, "Pelacakan lokasi dinonaktifkan."))
        }
    }

    fun saveSystemSettings(settings: SystemSettingsEntity) {
        viewModelScope.launch {
            repository.saveSystemSettings(settings)
            showSnackbar(LanguageManager.translate("vm_system_settings_saved", _currentLanguage.value, "Pengaturan sistem berhasil disimpan & disinkronkan!"))
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
            showSnackbar(LanguageManager.translate("vm_new_pin_prefix", _currentLanguage.value, "Pin pengguna baru ditambahkan di ") + "$city!")
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
                if (response.success) {
                    _historyItems.value = response.data
                    // M56: Sync API history → Room DB (untuk HistoryScreen)
                    try {
                        val entityList = response.data.map { item ->
                            com.inkside.digital.data.model.TransactionEntity(
                                id = item.id,
                                type = item.type,
                                title = item.title.ifBlank { item.description.take(40) },
                                description = item.description,
                                amount = item.amount,
                                currency = "IDR",
                                isCredit = item.isCredit,
                                status = "SUCCESS",
                                timestamp = item.timestamp,
                                referenceId = ""
                            )
                        }
                        repository.insertTransactions(entityList)
                        AppLogger.d("History", "Synced ${'$'}{entityList.size} transactions to Room DB")
                    } catch (e: Exception) {
                        AppLogger.w("History", "Failed to sync to Room DB: ${'$'}{e.message}")
                    }
                }
            }.onFailure { /* offline, pakai data lokal */ }
        }
    }

    fun loadNotifications() {
        viewModelScope.launch {
            val currentUser = user.value ?: return@launch
            ApiClient.getNotifications(currentUser.id).onSuccess { response ->
                if (response.success) {
                    _notificationItems.value = response.data
                    // M57: Sync API notifications → Room DB
                    try {
                        val entityList = response.data.map { item ->
                            com.inkside.digital.data.model.NotificationEntity(
                                id = item.id,
                                title = item.title,
                                message = item.message,
                                type = item.type,
                                timestamp = item.timestamp,
                                isRead = item.read,
                                actionDeepLink = ""
                            )
                        }
                        repository.insertNotifications(entityList)
                        AppLogger.d("Notifications", "Synced ${'$'}{entityList.size} notifications to Room DB")
                    } catch (e: Exception) {
                        AppLogger.w("Notifications", "Failed to sync: ${'$'}{e.message}")
                    }
                }
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
                        phone = "",
                        tier = profile.tier,
                        balance = profile.balance,
                        points = profile.points,
                        referralCode = "",
                        checkInStreak = 0,
                        lastCheckInDate = "",
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
                        userId = data.userId, name = "", email = "", phone = "",
                        tier = data.tier, balance = data.balance,
                        points = data.points,
                        referralCode = "", checkInStreak = 0, lastCheckInDate = "",
                        todaySteps = data.todaySteps
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
                        userId = profile.id, name = profile.name, email = profile.email, phone = "",
                        tier = profile.tier, balance = profile.balance,
                        points = profile.points,
                        referralCode = "", checkInStreak = 0, lastCheckInDate = "",
                        todaySteps = profile.todaySteps
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
                            userId = current.id, name = "", email = "", phone = "",
                            tier = current.tier, balance = current.balance,
                            points = (current.points + spin.pointsWon).toInt(),
                            referralCode = current.referralCode,
                            checkInStreak = current.checkInStreak,
                            lastCheckInDate = current.lastCheckInDate,
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
                    AppLogger.w("AffiliateViewModel", "Firebase UID null — belum login")
                    return@launch
                }

                // Pastikan active user ID = Firebase UID
                repository.switchUser(firebaseUid)

                // Ambil dari backend
                val result = ApiClient.getMyProfile()
                result.onSuccess { json ->
                    try {
                        if (!json.optBoolean("success", false)) {
                            AppLogger.w("AffiliateViewModel", "Backend: user tidak ditemukan")
                            return@onSuccess
                        }
                        val data = json.optJSONObject("data") ?: return@onSuccess

                        repository.updateUserFromBackend(
                            userId = firebaseUid,
                            name = data.optString("name", ""),
                            email = data.optString("email", ""),
                            phone = data.optString("phone", ""),
                            tier = data.optString("tier", "FREE"),
                            balance = data.optDouble("balance", 0.0),
                            points = data.optInt("points", 0),
                            referralCode = data.optString("referralCode", ""),
                            referredCount = data.optInt("referredCount", 0),
                            checkInStreak = data.optInt("checkInStreak", 0),
                            lastCheckInDate = data.optString("lastCheckInDate", ""),
                            todaySteps = data.optInt("todaySteps", 0),
                            // === 14 field local-only ===
                            pendingBalance = data.optDouble("pendingBalance", 0.0),
                            totalPaidOut = data.optDouble("totalPaidOut", 0.0),
                            unclaimedSteps = data.optInt("unclaimedSteps", 0),
                            dailyStepGoal = data.optInt("dailyStepGoal", 5000),
                            isLocationTrackingAllowed = data.optBoolean("isLocationTrackingAllowed", true),
                            latitude = data.optDouble("latitude", 0.0),
                            longitude = data.optDouble("longitude", 0.0),
                            locationCity = data.optString("locationCity", ""),
                            locationProvince = data.optString("locationProvince", ""),
                            regionZone = data.optString("regionZone", "ID"),
                            is2FAEnabled = data.optBoolean("is2FAEnabled", false),
                            twoFactorSecret = data.optString("twoFactorSecret", ""),
                            commissionRateMultiplier = data.optDouble("commissionRateMultiplier", 1.0),
                            convertedStepsToday = data.optInt("convertedStepsToday", 0)
                        )
                        AppLogger.d("AffiliateViewModel", "✅ User loaded from backend")

                        // Update widget setelah user sync
                        user.value?.let { u ->
                            WidgetUpdater.refreshFromRoom(getApplication(), u.id)
                        }
                    } catch (e: Exception) {
                        AppLogger.e("AffiliateViewModel", "Parse user error: ${e.message}")
                    }
                }.onFailure {
                    AppLogger.w("AffiliateViewModel", "Gagal load user: ${it.message}")
                }
            } catch (e: Exception) {
                AppLogger.e("AffiliateViewModel", "loadUserFromBackend error: ${e.message}")
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
                    put("name", firebaseUser.displayName ?: LanguageManager.translate("vm_new_user", _currentLanguage.value, "User Baru"))
                }
                ApiClient.syncFirebaseUser(body).onSuccess {
                    AppLogger.d("AffiliateViewModel", "✅ User synced to backend")
                    loadUserFromBackend()
                }
            } catch (e: Exception) {
                AppLogger.e("AffiliateViewModel", "syncUserToBackend error: ${e.message}")
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
