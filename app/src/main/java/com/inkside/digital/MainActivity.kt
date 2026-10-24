package com.inkside.digital

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.collect
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.inkside.digital.data.model.*
import com.inkside.digital.data.preferences.AppThemePreferences
import com.inkside.digital.data.ads.AdManager
import com.inkside.digital.data.repository.PeerContact
import com.inkside.digital.data.repository.TransferResult
import com.inkside.digital.localization.AppLanguage
import com.inkside.digital.ui.components.AdBlockedDialog
import com.inkside.digital.ui.components.BannedDialog
import com.inkside.digital.ui.components.MaintenanceBanner
import com.inkside.digital.data.network.ApiClient
import com.inkside.digital.ui.components.AdRewardModal
import com.inkside.digital.ui.components.AdminReviewModal
import com.inkside.digital.ui.components.AppTopBar
import com.inkside.digital.ui.components.BuyCouponModal
import com.inkside.digital.ui.components.DailyCheckInModal
import com.inkside.digital.ui.components.LanguageModal
import com.inkside.digital.ui.components.ListCouponModal
import com.inkside.digital.ui.components.NewLinkModal
import com.inkside.digital.ui.components.PaymentGatewaySimulatorModal
import com.inkside.digital.ui.components.QrTransferModal
import com.inkside.digital.ui.components.RedeemPointsModal
import com.inkside.digital.ui.components.TransferReceiptDialog
import com.inkside.digital.ui.components.TwoFactorModal
import com.inkside.digital.ui.components.WithdrawDialog
import com.inkside.digital.ui.components.AffiliateWithdrawDialog
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.rememberCoroutineScope
import com.inkside.digital.ui.components.AppDrawer
import com.inkside.digital.ui.screens.ReferralScreen
import com.inkside.digital.ui.screens.SettingsScreen
import com.inkside.digital.ui.screens.FaqScreen
import com.inkside.digital.ui.screens.AchievementScreen
import com.inkside.digital.ui.screens.PurchaseScreen
import com.inkside.digital.util.IapManager
import kotlinx.coroutines.launch
import com.inkside.digital.ui.screens.InboxScreen
import com.inkside.digital.ui.screens.OnboardingScreen
import com.inkside.digital.ui.screens.DailyCheckInScreen
import com.inkside.digital.ui.screens.AnalyticsScreen
import com.inkside.digital.ui.screens.CampaignsScreen
import com.inkside.digital.ui.screens.HistoryScreen
import com.inkside.digital.ui.screens.HomeScreen
import com.inkside.digital.ui.screens.CoinRushScreen
import com.inkside.digital.ui.screens.InvestmentCouponScreen
import com.inkside.digital.ui.screens.MissionsScreen
import com.inkside.digital.ui.screens.NotificationsScreen
import com.inkside.digital.ui.screens.ProfileScreen
import com.inkside.digital.ui.screens.StepCounterScreenContent
import com.inkside.digital.ui.screens.AppOffersScreenContent
import com.inkside.digital.ui.screens.GameRoomScreenContent
import com.inkside.digital.ui.screens.GameHubScreen
import com.inkside.digital.ui.screens.LeaderboardScreen
import com.inkside.digital.ui.screens.WithdrawalScreen
import com.inkside.digital.ui.screens.SweepstakesScreen
import com.inkside.digital.ui.screens.TransferSimpleScreen
import com.inkside.digital.ui.theme.MyApplicationTheme
import com.inkside.digital.ui.screens.auth.AuthScreen
import com.inkside.digital.ui.screens.auth.AuthViewModel
import com.inkside.digital.ui.screens.auth.LoginScreen
import com.inkside.digital.ui.screens.auth.ProfileSetupScreen
import com.inkside.digital.ui.screens.auth.SplashScreen
import com.inkside.digital.ui.screens.SplashSequenceScreen
import com.inkside.digital.viewmodel.AffiliateViewModel
import com.inkside.digital.viewmodel.AppScreen
import com.inkside.digital.util.AnalyticsHelper
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.Alignment
import com.inkside.digital.ui.screens.GameRoomScreen
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.inkside.digital.worker.MiningWorker
import java.util.concurrent.TimeUnit
import com.inkside.digital.ui.screens.WhitepaperScreen
import com.inkside.digital.ui.screens.GlobalChatScreen
import com.inkside.digital.util.AppLogger
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.compose.ui.unit.dp
import com.scottyab.rootbeer.RootBeer
import android.app.AlertDialog
import androidx.compose.foundation.layout.width

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        AppThemePreferences.init(this)   // Load dark mode & bahasa
        com.inkside.digital.localization.LanguageManager.init(this)  // Init i18n (load JSON)
        AdManager.init(this)              // Init AdMob SDK

        // ✅ Minta izin notifikasi (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    1001
                )
            }
        }

        // ✅ Jadwalkan MiningWorker (background auto-claim setiap 15 menit)
        try {
            val miningWorkRequest = PeriodicWorkRequestBuilder<MiningWorker>(
                MiningWorker.INTERVAL_MINUTES, TimeUnit.MINUTES
            )
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                )
                .build()

            WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                MiningWorker.WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                miningWorkRequest
            )
            AppLogger.d("MainActivity", "✅ MiningWorker scheduled (15 min)")
        } catch (e: Exception) {
            AppLogger.e("MainActivity", "❌ Failed to schedule MiningWorker: ${e.message}")
        }
        // Init IapManager — DISABLED untuk debug blank screen
        // try {
        //     com.inkside.digital.util.IapManager.connect(this, this)
        // } catch (e: Exception) {
        //     AppLogger.e("IAP", "connect failed: ${e.message}", e)
        // }

        setContent {
            MyApplicationTheme(
                darkTheme = true  // Force dark mode
            ) {
                com.inkside.digital.ui.components.AnimatedBackground(
                    accentColor = com.inkside.digital.ui.theme.EmeraldLight,
                    enableAurora = true,
                    enableStars = true,
                    enableGlow = true,
                    enableParticles = true,
                    starCount = 40,
                    particleCount = 20,
                    enableWind = true
                ) {
                    androidx.compose.foundation.layout.Box(
                        modifier = androidx.compose.ui.Modifier
                            .fillMaxSize()
                            .systemBarsPadding()
                    ) {
                        RootNav()
                    }
                }
            }
        }
    }
    
    private fun checkRootStatus() {
        try {
            val rootBeer = RootBeer(this)
            if (rootBeer.isRooted) {
                AlertDialog.Builder(this)
                    .setTitle("🚫 Aplikasi Tidak Dapat Berjalan")
                    .setMessage("Aplikasi ini mendeteksi bahwa perangkat Anda telah di-root. " +
                            "Demi keamanan akun dan data Anda, aplikasi tidak dapat dijalankan di perangkat root. " +
                            "Silakan gunakan perangkat tanpa root.")
                    .setPositiveButton("Keluar") { _, _ ->
                        finishAffinity()
                        android.os.Process.killProcess(android.os.Process.myPid())
                    }
                    .setCancelable(false)
                    .show()
            }
        } catch (e: Exception) {
            // Abaikan jika error (misal library tidak tersedia)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        com.inkside.digital.data.ads.AdManager.cancelAll()  // ✅ FIX memory leak
    }

}

@Composable
fun RootNav(
    authViewModel: AuthViewModel = viewModel(),
    affiliateViewModel: AffiliateViewModel = viewModel()
) {
    // Onboarding flag
    val isOnboarded = com.inkside.digital.data.preferences.AppThemePreferences.isOnboarded

    // Sync user dari Firebase saat app dibuka
    androidx.compose.runtime.LaunchedEffect(Unit) {
        affiliateViewModel.syncFirebaseUser()
    }

    // Setup IAP callback
    androidx.compose.runtime.LaunchedEffect(Unit) {
        com.inkside.digital.util.IapManager.onPurchaseSuccess = { productId, token, packageName ->
            affiliateViewModel.handlePurchaseSuccess(productId, token, packageName)
        }
        com.inkside.digital.util.IapManager.onPurchaseError = { msg ->
            AppLogger.w("IAP", "Purchase error: $msg")
        }
    }

    androidx.compose.runtime.DisposableEffect(Unit) {
        onDispose {
            com.inkside.digital.util.IapManager.disconnect()
        }
    }

    // Preload semua komponen + auto-navigate bila complete
    val loadingState by affiliateViewModel.loadingState.collectAsState()
    androidx.compose.runtime.LaunchedEffect(Unit) {
        affiliateViewModel.preloadAll()
    }
    // Minimum display time — supaya user sempat lihat visual
    var minimumTimeElapsed by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }
    androidx.compose.runtime.LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(6500)  // minimum 6.5 detik
        minimumTimeElapsed = true
    }

    // Navigate ke HOME hanya kalau: data loaded + minimum time + onboarded
    androidx.compose.runtime.LaunchedEffect(loadingState.isComplete, minimumTimeElapsed) {
        if (loadingState.isComplete && minimumTimeElapsed && isOnboarded) {
            // Extra delay kecil untuk smooth transition
            kotlinx.coroutines.delay(300)
            authViewModel.navigateTo(AuthScreen.HOME)
        }
    }

    // Register FCM token bila app dibuka (M54: guna addOnCompleteListener, tak deprecated)
    androidx.compose.runtime.LaunchedEffect(Unit) {
        try {
            FirebaseMessaging.getInstance().token
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val token = task.result
                        AppLogger.d("RootNav", "FCM token: ${token.take(20)}...")
                        affiliateViewModel.registerFcmToken(token)
                    } else {
                        AppLogger.w("RootNav", "Gagal ambil FCM token: ${task.exception?.message}")
                    }
                }
        } catch (e: Exception) {
            AppLogger.w("RootNav", "Error: ${e.message}")
        }
    }

    // Load affiliate balance bila app dibuka
    androidx.compose.runtime.LaunchedEffect(Unit) {
        affiliateViewModel.loadAffiliateBalance()
    }

    // Load game state + katalog miner bila app dibuka
    androidx.compose.runtime.LaunchedEffect(Unit) {
        affiliateViewModel.loadGameState()
        affiliateViewModel.loadMinerCatalog()
    }

    // ============================================================
    // THERMAL + LOCATION MONITORING (seluruh app, bukan cuma Game Room)
    // ============================================================
    androidx.compose.runtime.LaunchedEffect(Unit) {
        AppLogger.d("RootNav", "🔥 Start thermal + location monitoring (app-wide)")
        affiliateViewModel.startThermalMonitoring()
        affiliateViewModel.fetchUserRegion()
        affiliateViewModel.startConfigRefreshObserver()
        affiliateViewModel.startMaintenanceCheck()
    }

    // Stop saat app close / navigate away
    androidx.compose.runtime.DisposableEffect(Unit) {
        onDispose {
            AppLogger.d("RootNav", "🛑 Stop thermal monitoring")
            affiliateViewModel.stopThermalMonitoring()
            affiliateViewModel.stopConfigRefreshObserver()
            affiliateViewModel.stopMaintenanceCheck()
        }
    }

    val authScreen by authViewModel.currentScreen.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()
    val isLoading by authViewModel.isLoading.collectAsState()
    val errorMessage by authViewModel.errorMessage.collectAsState()
    val successMessage by authViewModel.successMessage.collectAsState()

    val context = androidx.compose.ui.platform.LocalContext.current
    val currentLanguage by affiliateViewModel.currentLanguage.collectAsState()
    var adBlockedReason by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf<String?>(null) }

    if (!isOnboarded) {
        // Tampil onboarding pertama kali
        OnboardingScreen(
            currentLanguage = currentLanguage,
            onLanguageChange = { lang ->
                affiliateViewModel.setLanguage(lang)
            },
            onFinish = {
                com.inkside.digital.data.preferences.AppThemePreferences.setOnboarded(context, true)
            }
        )
        return
    }

    when (authScreen) {
        AuthScreen.ONBOARDING -> {
            // Sudah ditangani di atas (isOnboarded check)
        }

        AuthScreen.SPLASH -> {
            val loadingState by affiliateViewModel.loadingState.collectAsState()
            SplashSequenceScreen(
                loadingState = loadingState,
                currentLanguage = currentLanguage,
                onComplete = {
                    // Navigate ke HOME setelah splash selesai
                    authViewModel.navigateTo(AuthScreen.HOME)
                }
            )
        }

        AuthScreen.LOGIN -> {
            val prefs = context.getSharedPreferences("royaltree_prefs", android.content.Context.MODE_PRIVATE)
            val isReturningUser = prefs.getBoolean("has_logged_in_before", false)

            LoginScreen(
                isLoading = isLoading,
                errorMessage = errorMessage,
                currentLanguage = currentLanguage,
                isReturningUser = isReturningUser,
                onLanguageChange = { lang ->
                    affiliateViewModel.setLanguage(lang)
                },
                onLoginGoogle = { idToken, marketingOptIn ->
                    prefs.edit().putBoolean("has_logged_in_before", true).apply()
                    authViewModel.loginWithGoogle(idToken, marketingOptIn)
                }
            )
        }


        AuthScreen.PROFILE_SETUP -> {
            ProfileSetupScreen(
                defaultName = currentUser?.displayName ?: "",
                isLoading = isLoading,
                errorMessage = errorMessage,
                currentLanguage = currentLanguage,
                onSave = { name, phone, city, referralCode ->
                    authViewModel.saveProfileSetup(name, phone, city, referralCode)
                },
                onLogout = { authViewModel.logout() }
            )
        }

        AuthScreen.HOME -> {
            // Load semua data dari backend saat masuk HOME
            androidx.compose.runtime.LaunchedEffect(Unit) {
                affiliateViewModel.loadAllFromBackend()
            }
            MainAffiliateApp(
                viewModel = affiliateViewModel,
                onLogout = { authViewModel.logout() }
            )
        }
    }
}

@Composable
fun MainAffiliateApp(
    viewModel: AffiliateViewModel = viewModel(),
    onLogout: () -> Unit = {}
) {
    // Refresh semua data dari backend saat app dibuka
    androidx.compose.runtime.LaunchedEffect(Unit) {
        viewModel.loadUserFromBackend()
        viewModel.loadCampaignsFromBackendV2()
        viewModel.loadMissionsFromBackendV2()
    }
    val user by viewModel.user.collectAsState()
    val gameRoomState by viewModel.gameRoomState.collectAsState()

    // Sync photoUrl dari Firebase ke UserEntity (Room DB)
    androidx.compose.runtime.LaunchedEffect(user?.id, user?.photoUrl) {
        val fbUser = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
        fbUser?.let {
            val fbPhotoUrl = it.photoUrl?.toString()
            val fbName = it.displayName
            val fbEmail = it.email

            // Kalau user ada tapi photoUrl kosong/berbeza → sync
            if (user != null && !fbPhotoUrl.isNullOrBlank() && user?.photoUrl != fbPhotoUrl) {
                AppLogger.d("MainActivity", "📷 Sync photoUrl: $fbPhotoUrl")
                viewModel.syncUserFromFirebase(
                    photoUrl = fbPhotoUrl,
                    name = fbName,
                    email = fbEmail
                )
            }
        }
    }
    val campaigns by viewModel.campaigns.collectAsState()
    val links by viewModel.links.collectAsState()
    val withdrawals by viewModel.withdrawals.collectAsState()
    val pendingWithdrawals by viewModel.pendingWithdrawals.collectAsState()
    val transactions by viewModel.transactions.collectAsState()
    val coupons by viewModel.investmentCoupons.collectAsState()
    val notifications by viewModel.notifications.collectAsState()
    val unreadNotifs by viewModel.unreadNotifCount.collectAsState()
    val missions by viewModel.missions.collectAsState()
    val allUsers by viewModel.allUsers.collectAsState()
    val locationLogs by viewModel.locationLogs.collectAsState()
    val systemSettings by viewModel.systemSettings.collectAsState()

    val portalBaseUrl by viewModel.portalBaseUrl.collectAsState()
    val portalApiKey by viewModel.portalApiKey.collectAsState()
    val portalStatus by viewModel.portalConnectionStatus.collectAsState()
    val portalStatusMessage by viewModel.portalStatusMessage.collectAsState()
    val isSyncingWithPortal by viewModel.isSyncingWithPortal.collectAsState()

    val currentScreen by viewModel.currentScreen.collectAsState()
    val adButtonState by viewModel.adButtonState.collectAsState()

    // Preload ad + refresh state saat HomeScreen dibuka
    androidx.compose.runtime.LaunchedEffect(currentScreen) {
        // Analytics — log screen view
        try {
            val screenName = when (currentScreen) {
                AppScreen.HOME -> "home"
                AppScreen.GAME_ROOM -> "game_room"
                AppScreen.WITHDRAW -> "withdraw"
                AppScreen.HISTORY -> "history"
                AppScreen.INBOX -> "inbox"
                AppScreen.NOTIFICATIONS -> "notifications"
                AppScreen.PROFILE -> "profile"
                AppScreen.MISSIONS -> "missions"
                AppScreen.GLOBAL_CHAT -> "chat"
                else -> currentScreen.name.lowercase()
            }
            AnalyticsHelper.logScreen(screenName, "MainActivity")
        } catch (e: Exception) {
            AppLogger.w("Analytics", "logScreen error: ${e.message}")
        }

        if (currentScreen == AppScreen.HOME) {
            viewModel.preloadAdForHome()
        }
    }
    val snackbarMsg by viewModel.snackbarMessage.collectAsState()

    val showWithdraw by viewModel.showWithdrawModal.collectAsState()
    val showNewLink by viewModel.showNewLinkModal.collectAsState()
    val showUpgrade by viewModel.showUpgradeModal.collectAsState()
    val showRedeemPoints by viewModel.showRedeemPointsModal.collectAsState()
    val showDailyCheckIn by viewModel.showDailyCheckInModal.collectAsState()
    val buyCouponTarget by viewModel.showBuyCouponModal.collectAsState()
    val listCouponTarget by viewModel.showListCouponModal.collectAsState()
    val adminReviewTarget by viewModel.showAdminReviewModal.collectAsState()
    val showAdReward by viewModel.showAdRewardModal.collectAsState()
    val pendingMiningClaim by viewModel.pendingMiningClaim.collectAsState()
    val pendingMinerActivate by viewModel.pendingMinerActivate.collectAsState()
    val show2FA by viewModel.show2FAModal.collectAsState()
    val showQrTransfer by viewModel.showQrTransferModal.collectAsState()
    val contacts by viewModel.contacts.collectAsState()
    val transferSuccessReceipt by viewModel.transferSuccessReceipt.collectAsState()
    val paymentGatewaySim by viewModel.showPaymentGatewaySimulator.collectAsState()
    val selectedCampForLink by viewModel.selectedCampaignForLink.collectAsState()

    val adsList by viewModel.appDownloadAds.collectAsState()
    val minerItems by viewModel.gameMinerItems.collectAsState()
    val placedMiners by viewModel.placedMinerItems.collectAsState()
    val roomState by viewModel.gameRoomState.collectAsState()
    val currentLanguage by viewModel.currentLanguage.collectAsState()
    val affiliateBalance by viewModel.affiliateBalance.collectAsState()
    val affiliateLoading by viewModel.affiliateLoading.collectAsState()
    var adBlockedReason by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf<String?>(null) }
    var showWithdrawAffiliateModal by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }

    // ============ Observe AdGuard detection ============
    // Bila ApiClient detect UnknownHostException / SocketTimeout (DNS block),
    // trigger AdBlockedDialog secara automatik.
    androidx.compose.runtime.LaunchedEffect(Unit) {
        androidx.compose.runtime.snapshotFlow<Boolean> { com.inkside.digital.data.network.ApiClient.adBlockDetected }
            .collect { detected: Boolean ->
                if (detected) {
                    AppLogger.w("MainActivity", "⚠️ AdGuard detected - trigger dialog")
                    adBlockedReason = "NO_FILL"
                }
            }
    }

    // ============ Start Maintenance Check ============
    androidx.compose.runtime.LaunchedEffect(Unit) {
        viewModel.startMaintenanceCheck()
    }

    // ============ Observe Banned User State ============
    // Bila ApiClient detect 403 USER_BANNED, trigger BannedDialog.
    var bannedInfo by androidx.compose.runtime.remember {
        androidx.compose.runtime.mutableStateOf(ApiClient.bannedState)
    }
    androidx.compose.runtime.LaunchedEffect(Unit) {
        androidx.compose.runtime.snapshotFlow { ApiClient.bannedState }
            .collect { info: ApiClient.BannedInfo ->
                if (info.banned) {
                    AppLogger.w("MainActivity", "🚫 User banned: ${info.reason}")
                    bannedInfo = info
                }
            }
    }

    val vmAlias: AffiliateViewModel = viewModel
    MainAffiliateAppContent(
        viewModel = viewModel,
        user = user,
        pendingMinerActivate = pendingMinerActivate,
        campaigns = campaigns,
        links = links,
        withdrawals = withdrawals,
        pendingWithdrawals = pendingWithdrawals,
        transactions = transactions,
        coupons = coupons,
        notifications = notifications,
        unreadNotifs = unreadNotifs,
        missions = missions,
        allUsers = allUsers,
        locationLogs = locationLogs,
        systemSettings = systemSettings,
        portalBaseUrl = portalBaseUrl,
        portalApiKey = portalApiKey,
        portalStatus = portalStatus,
        portalStatusMessage = portalStatusMessage,
        isSyncingWithPortal = isSyncingWithPortal,
        currentScreen = currentScreen,
        currentLanguage = currentLanguage,
        adButtonState = adButtonState,
        onRetryAdLoad = { viewModel.retryAdLoad() },
        onLanguageChange = { viewModel.setLanguage(it) },
        snackbarMsg = snackbarMsg,
        showWithdraw = showWithdraw,
        showNewLink = showNewLink,
        showUpgrade = showUpgrade,
        showRedeemPoints = showRedeemPoints,
        showDailyCheckIn = showDailyCheckIn,
        buyCouponTarget = buyCouponTarget,
        listCouponTarget = listCouponTarget,
        adminReviewTarget = adminReviewTarget,
        showAdReward = showAdReward,
        adBlockedReason = adBlockedReason,
        onAdBlockedDismiss = { 
            adBlockedReason = null
            com.inkside.digital.data.network.ApiClient.resetAdBlockFlag()
        },
        onAdBlockedRetry = {
            adBlockedReason = null
            viewModel.showAdRewardModal.value = true
        },
        onAdBlockedSetReason = { r -> adBlockedReason = r },
        affiliateBalance = affiliateBalance,
        affiliateLoading = affiliateLoading,
        showWithdrawAffiliateModal = showWithdrawAffiliateModal,
        onOpenWithdraw = { showWithdrawAffiliateModal = true },
        onOpenWithdrawHistory = { viewModel.navigateTo(AppScreen.HISTORY) },
        onWithdrawAffiliateDismiss = { showWithdrawAffiliateModal = false },
        onWithdrawAffiliateSubmit = { amount, dest ->
            viewModel.requestAffiliateWithdraw(amount, dest, "PAYPAL")
            showWithdrawAffiliateModal = false
        },
        show2FA = show2FA,
        showQrTransfer = showQrTransfer,
        contacts = contacts,
        transferSuccessReceipt = transferSuccessReceipt,
        paymentGatewaySim = paymentGatewaySim,
        selectedCampForLink = selectedCampForLink,
        adsList = adsList,
        minerItems = minerItems,
        placedMiners = placedMiners,
        roomState = roomState,
        onNavigate = { viewModel.navigateTo(it) },
        onDismissSnackbar = { viewModel.dismissSnackbar() },
        onWithdrawSubmit = { amount, currency, channel, provider, accountDest, accountHolder ->
            viewModel.requestWithdrawal(amount, currency, channel, provider, accountDest, accountHolder)
        },
        onWithdrawDismiss = { viewModel.showWithdrawModal.value = false },
        onPaymentGatewaySimDismiss = { viewModel.showPaymentGatewaySimulator.value = null },
        onNewLinkDismiss = {
            viewModel.showNewLinkModal.value = false
            viewModel.selectedCampaignForLink.value = null
        },
        onNewLinkCreate = { camp, slug, subId -> viewModel.createLink(camp, slug, subId) },
        onUpgradeDismiss = { viewModel.showUpgradeModal.value = false },
        onUpgradeConfirm = { viewModel.upgradeToPremium() },
        onFinishMiniGame = { score -> viewModel.finishMiniGame(score) },
        onRedeemDismiss = { viewModel.showRedeemPointsModal.value = false },
        onRedeemConfirm = { pts, wallet, phone, curr -> viewModel.redeemPoints(pts, wallet, phone, curr) },
        onRedeemOpenQr = { viewModel.showQrTransferModal.value = true },
        onDailyCheckInDismiss = { viewModel.showDailyCheckInModal.value = false },
        onDailyCheckInClaim = { viewModel.performDailyCheckIn() },
        onDailyCheckInWithAd = { txId, vendor -> viewModel.performDailyCheckInWithAd(txId, vendor) },
        onLoadGameState = { viewModel.loadGameState() },
        onUnlockSlot = { slotIndex -> vmAlias.unlockSlot(slotIndex) },
        onBuyCouponDismiss = { viewModel.showBuyCouponModal.value = null },
        onBuyCouponConfirm = { coupon, qty -> viewModel.buyCoupon(coupon, qty) },
        onListCouponDismiss = { viewModel.showListCouponModal.value = null },
        onListCouponConfirm = { coupon, price, isListing -> viewModel.toggleListingSecondaryMarket(coupon, price, isListing) },
        onAdminReviewDismiss = { viewModel.showAdminReviewModal.value = null },
        onAdminReviewProcess = { withdrawal, isApprove, note -> viewModel.adminProcessWithdrawal(withdrawal, isApprove, note) },
        on2FADismiss = { viewModel.show2FAModal.value = false },
        on2FAVerifyToggle = { viewModel.toggle2FA(it) },
        onAdRewardDismiss = { viewModel.showAdRewardModal.value = false },
        onAdRewardEarned = { points ->
            val txId = java.util.UUID.randomUUID().toString()
            when {
                pendingMinerActivate -> {
                    viewModel.setPendingMinerActivate(false)
                    viewModel.activateMinerWithAd(txId, "admob")
                }
                pendingMiningClaim -> {
                    viewModel.setPendingMiningClaim(false)
                    viewModel.claimMiningWithAd(txId, "admob")
                }
                else -> {
                    viewModel.onAdRewardEarned(points)
                }
            }
        },
        onQrTransferDismiss = { viewModel.showQrTransferModal.value = false },
        onQrTransferToggleFavorite = { viewModel.toggleFavoriteContact(it) },
        onQrTransferSubmit = { recipientId, recipientName, amount, note ->
            viewModel.transferPoints(recipientId, recipientName, amount, note)
        },
        onTransferReceiptDismiss = { viewModel.dismissTransferReceipt() },
        onClaimYield = { viewModel.claimYield(it) },
        onSimulateConversion = { viewModel.simulateConversion(it) },
        onSaveSystemSettings = { viewModel.saveSystemSettings(it) },
        onAddSimulatedPin = { userId, name, tier, lat, lng, city, prov ->
            viewModel.addProviderSimulatedPin(userId, name, tier, lat, lng, city, prov)
        },
        onTestConnection = { url, key -> viewModel.testPortalConnection(url, key) },
        onSavePortalConfig = { url, key -> viewModel.savePortalConfiguration(url, key) },
        onSyncPortal = { viewModel.syncDataWithPortal() },
        onUpdateProfile = { name, email, phone, region -> viewModel.updateProfile(name, email, phone, region) },
        onToggleLocationTracking = { viewModel.toggleLocationTracking(it) },
        onMarkAllNotifsRead = { viewModel.markAllNotificationsRead() },
        onClearAllNotifs = { viewModel.clearAllNotifications() },
        onLoadHistory = { viewModel.loadHistory() },
        onLoadNotifications = { viewModel.loadNotifications() },
        onSetWithdrawModalVisible = { viewModel.showWithdrawModal.value = it },
        onSetNewLinkModalVisible = { viewModel.showNewLinkModal.value = it },
        onSetUpgradeModalVisible = { viewModel.showUpgradeModal.value = it },
        onSetRedeemPointsModalVisible = { viewModel.showRedeemPointsModal.value = it },
        onSetQrTransferModalVisible = { viewModel.showQrTransferModal.value = it },
        onSetAdRewardModalVisible = { viewModel.showAdRewardModal.value = it },
        onSetBuyCouponModalVisible = { viewModel.showBuyCouponModal.value = it },
        onSetListCouponModalVisible = { viewModel.showListCouponModal.value = it },
        onSetAdminReviewModalVisible = { viewModel.showAdminReviewModal.value = it },
        onSetSelectedCampaignForLink = { viewModel.selectedCampaignForLink.value = it },
        onClaimBannerAdBonus = { id, name, coins -> viewModel.claimBannerAdBonus(id, name, coins) },
        onDownloadApp = { viewModel.downloadApp(it) },
        onClaimAppReward = { id -> viewModel.claimAppDownloadReward(id) },
        onClaimMission = { viewModel.claimMissionReward(it) },
        onCompleteTaskAction = { viewModel.completeTaskAction(it) },
        onAddSteps = { viewModel.addSteps(it) },
        onConvertSteps = { viewModel.convertStepsToCoins() },
        onClaimMining = { viewModel.setPendingMiningClaim(true); viewModel.showAdRewardModal.value = true },
        onActivateMiner = {
            viewModel.setPendingMinerActivate(true)
            viewModel.showAdRewardModal.value = true
        },
        onToggleMinerSlot = { id, slot -> viewModel.toggleMinerSlot(id, slot) },
        onBuyGameMinerItem = { id -> viewModel.buyGameMinerItem(id) },
        onBuyAndPlaceGameMinerItem = { id -> viewModel.buyAndPlaceGameMinerItem(id) },
        onFinishGame = { score, duration, nonce -> viewModel.finishMiniGame(score, duration, nonce) },
        onLogout = onLogout
    )
}

/**
 * Helper: Cari Activity dari Context (Compose context biasanya ContextWrapper).
 */
private fun android.content.Context.findActivity(): android.app.Activity? = when (this) {
    is android.app.Activity -> this
    is android.content.ContextWrapper -> baseContext.findActivity()
    else -> null
}

@Composable
fun MainAffiliateAppContent(
    viewModel: AffiliateViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    user: UserEntity?,
    pendingMinerActivate: Boolean = false,
    campaigns: List<CampaignEntity>,
    links: List<AffiliateLinkEntity>,
    withdrawals: List<WithdrawalEntity>,
    pendingWithdrawals: List<WithdrawalEntity>,
    transactions: List<TransactionEntity>,
    coupons: List<InvestmentCouponEntity>,
    notifications: List<NotificationEntity>,
    unreadNotifs: Int,
    missions: List<TaskMissionEntity>,
    allUsers: List<UserEntity>,
    locationLogs: List<UserLocationLogEntity>,
    systemSettings: SystemSettingsEntity?,
    portalBaseUrl: String,
    portalApiKey: String,
    portalStatus: String,
    portalStatusMessage: String,
    isSyncingWithPortal: Boolean,
    currentScreen: AppScreen,
    currentLanguage: AppLanguage,
    adButtonState: com.inkside.digital.viewmodel.AdButtonState = com.inkside.digital.viewmodel.AdButtonState.LOADING,
    onRetryAdLoad: () -> Unit = {},
    onLanguageChange: (AppLanguage) -> Unit,
    snackbarMsg: String?,
    showWithdraw: Boolean,
    showNewLink: Boolean,
    showUpgrade: Boolean,
    showRedeemPoints: Boolean,
    showDailyCheckIn: Boolean,
    buyCouponTarget: InvestmentCouponEntity?,
    listCouponTarget: InvestmentCouponEntity?,
    adminReviewTarget: WithdrawalEntity?,
    showAdReward: Boolean,
    onFinishMiniGame: (Int) -> Unit = {},
    adBlockedReason: String? = null,
    onAdBlockedDismiss: () -> Unit = {},
    onAdBlockedRetry: () -> Unit = {},
    onAdBlockedSetReason: (String) -> Unit = {},
    // === Affiliate ===
    affiliateBalance: AffiliateViewModel.AffiliateBalanceData = AffiliateViewModel.AffiliateBalanceData(),
    affiliateLoading: Boolean = false,
    showWithdrawAffiliateModal: Boolean = false,
    onOpenWithdraw: () -> Unit = {},
    onOpenWithdrawHistory: () -> Unit = {},
    onWithdrawAffiliateDismiss: () -> Unit = {},
    onWithdrawAffiliateSubmit: (amount: Double, destination: String) -> Unit = { _, _ -> },
    show2FA: Boolean,
    showQrTransfer: Boolean,
    contacts: List<PeerContact>,
    transferSuccessReceipt: TransferResult?,
    paymentGatewaySim: WithdrawalEntity?,
    selectedCampForLink: CampaignEntity?,
    adsList: List<AppDownloadAdEntity>,
    minerItems: List<GameMinerItemEntity>,
    placedMiners: List<GameMinerItemEntity>,
    roomState: GameRoomStateEntity?,
    onNavigate: (AppScreen) -> Unit,
    onDismissSnackbar: () -> Unit,
    onWithdrawSubmit: (Double, String, String, String, String, String) -> Unit,
    onWithdrawDismiss: () -> Unit,
    onPaymentGatewaySimDismiss: () -> Unit,
    onNewLinkDismiss: () -> Unit,
    onNewLinkCreate: (CampaignEntity, String, String) -> Unit,
    onUpgradeDismiss: () -> Unit,
    onUpgradeConfirm: () -> Unit,
    onRedeemDismiss: () -> Unit,
    onRedeemConfirm: (Int, String, String, String) -> Unit,
    onRedeemOpenQr: () -> Unit,
    onDailyCheckInDismiss: () -> Unit,
    onDailyCheckInClaim: () -> Unit,
    onDailyCheckInWithAd: (String, String) -> Unit = { _, _ -> },
    onLoadGameState: () -> Unit = {},
    onBuyCouponDismiss: () -> Unit,
    onBuyCouponConfirm: (InvestmentCouponEntity, Int) -> Unit,
    onListCouponDismiss: () -> Unit,
    onListCouponConfirm: (InvestmentCouponEntity, Double, Boolean) -> Unit,
    onAdminReviewDismiss: () -> Unit,
    onAdminReviewProcess: (WithdrawalEntity, Boolean, String) -> Unit,
    on2FADismiss: () -> Unit,
    on2FAVerifyToggle: (Boolean) -> Unit,
    onAdRewardDismiss: () -> Unit,
    onAdRewardEarned: (Int) -> Unit,
    onQrTransferDismiss: () -> Unit,
    onQrTransferToggleFavorite: (String) -> Unit,
    onQrTransferSubmit: (String, String, Int, String) -> Unit,
    onTransferReceiptDismiss: () -> Unit,
    onClaimYield: (InvestmentCouponEntity) -> Unit,
    onSimulateConversion: (AffiliateLinkEntity) -> Unit,
    onSaveSystemSettings: (SystemSettingsEntity) -> Unit,
    onAddSimulatedPin: (String, String, String, Double, Double, String, String) -> Unit,
    onTestConnection: (String, String) -> Unit,
    onSavePortalConfig: (String, String) -> Unit,
    onSyncPortal: () -> Unit,
    onUpdateProfile: (String, String, String, String) -> Unit,
    onToggleLocationTracking: (Boolean) -> Unit,
    onMarkAllNotifsRead: () -> Unit,
    onClearAllNotifs: () -> Unit,
    onLoadHistory: () -> Unit,
    onLoadNotifications: () -> Unit,
    onSetWithdrawModalVisible: (Boolean) -> Unit,
    onSetNewLinkModalVisible: (Boolean) -> Unit,
    onSetUpgradeModalVisible: (Boolean) -> Unit,
    onSetRedeemPointsModalVisible: (Boolean) -> Unit,
    onSetQrTransferModalVisible: (Boolean) -> Unit,
    onSetAdRewardModalVisible: (Boolean) -> Unit,
    onSetBuyCouponModalVisible: (InvestmentCouponEntity?) -> Unit,
    onSetListCouponModalVisible: (InvestmentCouponEntity?) -> Unit,
    onSetAdminReviewModalVisible: (WithdrawalEntity?) -> Unit,
    onSetSelectedCampaignForLink: (CampaignEntity?) -> Unit,
    onClaimBannerAdBonus: (String, String, Int) -> Unit,
    onDownloadApp: (String) -> Unit,
    onClaimAppReward: (String) -> Unit,
    onClaimMission: (String) -> Unit,
    onCompleteTaskAction: (String) -> Unit,
    onAddSteps: (Int) -> Unit,
    onConvertSteps: () -> Unit,
    onClaimMining: () -> Unit,
    onActivateMiner: () -> Unit = {},
    onToggleMinerSlot: (String, Int) -> Unit,
    onBuyGameMinerItem: (String) -> Unit,
    onBuyAndPlaceGameMinerItem: (String) -> Unit = {},
    onFinishGame: (Int, Long, String) -> Unit,
    onUnlockSlot: (Int) -> Unit = {},
    onLogout: () -> Unit = {}
) {
    val context = LocalContext.current
    var showLanguageModal by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(snackbarMsg) {
        snackbarMsg?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            onDismissSnackbar()
        }
    }

    // Baca maintenance state
    val maintenanceMode by viewModel.maintenanceMode.collectAsState()
    val maintenanceMessage by viewModel.maintenanceMessage.collectAsState()

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = true,   // ← FIX: enable swipe untuk tutup drawer
        drawerContent = {
            ModalDrawerSheet(modifier = Modifier.width(280.dp)) {
                AppDrawer(
                    user = user,
                    currentScreen = currentScreen,
                    currentLanguage = currentLanguage,
                    roomState = roomState,
                    onNavigate = { screen -> onNavigate(screen) },
                    onCloseDrawer = { coroutineScope.launch { drawerState.close() } },
                    onLogout = onLogout
                )
            }
        }
    ) {
    val navDensity = androidx.compose.ui.platform.LocalDensity.current
    val bottomNavHeightState = androidx.compose.runtime.remember {
        androidx.compose.runtime.mutableStateOf(0.dp)
    }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            androidx.compose.foundation.layout.Column {
                // Maintenance banner di atas topbar
                if (maintenanceMode) {
                    MaintenanceBanner(message = maintenanceMessage)
                }
                AppTopBar(
                    user = user,
                    unreadNotifs = unreadNotifs,
                    currentLanguage = currentLanguage,
                    onOpenDrawer = { coroutineScope.launch { drawerState.open() } },
                    onOpenLanguage = { showLanguageModal = true },
                    onOpenInbox = { onNavigate(AppScreen.INBOX) },
                    onOpenNotifs = { onNavigate(AppScreen.NOTIFICATIONS) }
                )
            }
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                androidx.compose.material3.Snackbar(
                    snackbarData = data,
                    containerColor = androidx.compose.ui.graphics.Color(0xFF10B981),  // emerald
                    contentColor = androidx.compose.ui.graphics.Color.White,
                    actionColor = androidx.compose.ui.graphics.Color(0xFFFFD700)  // gold
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // ============ KONTEN UTAMA — padding top + bottom (fix ketutup nav) ============
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        top = innerPadding.calculateTopPadding(),
                        bottom = if (bottomNavHeightState.value > 0.dp) {
                            bottomNavHeightState.value + 24.dp
                        } else {
                            108.dp
                        }
                    )
            ) {
            com.inkside.digital.ui.components.AnimatedBackground {
                when (currentScreen) {
                AppScreen.HOME -> {
                    HomeScreen(
                        user = user,
                        links = links,
                        recentTransactions = transactions,
                        currentLanguage = currentLanguage,
                        roomState = roomState,
                        onNavigate = { onNavigate(it) },
                        onOpenWithdraw = { onSetWithdrawModalVisible(true) },
                        onOpenNewLink = { onSetNewLinkModalVisible(true) },
                        onOpenUpgrade = { onNavigate(AppScreen.PURCHASE) },
                        onOpenRedeemPoints = { onSetRedeemPointsModalVisible(true) },
                        onOpenTransferQr = { onSetQrTransferModalVisible(true) },
                        onSimulateConversion = { onSimulateConversion(it) },
                        onOpenAdReward = { onSetAdRewardModalVisible(true) },
                        adButtonState = adButtonState,
                        onRetryAdLoad = onRetryAdLoad
                    )
                }





                AppScreen.WITHDRAW -> {
                    WithdrawalScreen(
                        user = user,
                        withdrawals = withdrawals,
                        currentLanguage = currentLanguage,
                        onSubmitWithdrawal = { amount, channelType, providerName, accountDestination, accountHolderName ->
                            onWithdrawSubmit(amount, channelType, providerName, accountDestination, accountHolderName, "")
                        }
                    )
                }


                AppScreen.HISTORY -> {
                    LaunchedEffect(Unit) { onLoadHistory() }
                    HistoryScreen(
                        transactions = transactions,
                        currentLanguage = currentLanguage
                    )
                }

                AppScreen.PROFILE -> {
                    ProfileScreen(
                        user = user,
                        currentLanguage = currentLanguage,
                        onOpenLanguage = { showLanguageModal = true },
                        onSaveProfile = { name, email, phone ->
                            onUpdateProfile(name, email, phone, "")
                        },
                        onLogout = { onLogout() },
                        affiliateBalance = affiliateBalance,
                        onOpenWithdraw = onOpenWithdraw,
                        onOpenWithdrawHistory = onOpenWithdrawHistory,
                        onOpenUpgrade = { onNavigate(AppScreen.PURCHASE) },
                        onNavigateToDailyCheckIn = { onNavigate(AppScreen.DAILY_CHECK_IN) },
                        onNavigateToMining = { onNavigate(AppScreen.GAME_MINING) },
                        onNavigateToCoinRush = { onNavigate(AppScreen.GAME_COIN_RUSH) },
                        onNavigateToMissions = { onNavigate(AppScreen.MISSIONS) },
                        minerSlotsUsed = placedMiners.size,
                        minerSlotsMax = when (user?.tier) {
                            "ROYAL" -> 10
                            "VIP" -> 8
                            "PREMIUM" -> 5
                            "STARTER" -> 3
                            else -> 6
                        },
                        minerPowerHs = placedMiners.sumOf { it.powerHs },
                        // minerTokens dibuang — guna points (RTP)
                        roomState = roomState
                    )
                }

                AppScreen.MISSIONS -> {
                    MissionsScreen(
                        user = user,
                        missions = missions,
                        currentLanguage = currentLanguage,
                        onBack = { onNavigate(AppScreen.HOME) },
                        onClaimMission = { onClaimMission(it) },
                        onCompleteTaskAction = { onCompleteTaskAction(it) },
                        onOpenRedeemPoints = { onSetRedeemPointsModalVisible(true) },
                        onOpenTransferQr = { onSetQrTransferModalVisible(true) },
                        onOpenWatchAd = { onSetAdRewardModalVisible(true) }
                    )
                }


                AppScreen.REFERRAL -> {
                    ReferralScreen(user = user, currentLanguage = currentLanguage)
                }

                AppScreen.WHITEPAPER -> {
                    WhitepaperScreen(
                        currentLanguage = currentLanguage,
                        onBack = { onNavigate(AppScreen.HOME) }
                    )
                }

                AppScreen.GLOBAL_CHAT -> {
                    GlobalChatScreen(
                        currentUserId = user?.id ?: "",
                        currentLanguage = currentLanguage,
                        onBack = { onNavigate(AppScreen.HOME) }
                    )
                }

                AppScreen.SETTINGS -> {
                    SettingsScreen(
                        currentLanguage = currentLanguage,
                        onOpenLanguage = { showLanguageModal = true },
                        onLogout = { onLogout() }
                    )
                }

                AppScreen.FAQ -> {
                    FaqScreen(currentLanguage = currentLanguage)
                }

                AppScreen.ACHIEVEMENT -> {
                    AchievementScreen(
                        viewModel = viewModel,
                        currentLanguage = currentLanguage,
                        onBack = { onNavigate(AppScreen.PROFILE) }
                    )
                }

                AppScreen.PURCHASE -> {
                    PurchaseScreen(
                        viewModel = viewModel,
                        currentLanguage = currentLanguage,
                        onBack = { onNavigate(AppScreen.PROFILE) }
                    )
                }

                AppScreen.DAILY_CHECK_IN -> {
                    val today = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US).format(java.util.Date())
                    val isCheckedInToday = user?.lastCheckInDate == today

                    DailyCheckInScreen(
                        currentLanguage = currentLanguage,
                        onBack = { onNavigate(AppScreen.HOME) },
                        onClaimCheckIn = { onDailyCheckInClaim() },
                        onWatchAdAndClaim = { txId, vendor ->
                            onDailyCheckInWithAd(txId, vendor)
                        },
                        onWatchAdForRecovery = { /* nanti */ },
                        onPayPointsForRecovery = { /* nanti */ },
                        streak = user?.checkInStreak ?: 0,
                        checkedInToday = isCheckedInToday,
                        lastCheckInDate = user?.lastCheckInDate ?: "",
                        userId = user?.id ?: "",
                        isLoading = false,
                        errorMessage = null,
                        successMessage = null
                    )
                }

                AppScreen.INBOX -> {
                    InboxScreen(
                        currentLanguage = currentLanguage,
                        onBack = { onNavigate(AppScreen.HOME) }
                    )
                }

                AppScreen.NOTIFICATIONS -> {
                    LaunchedEffect(Unit) { onLoadNotifications() }
                    NotificationsScreen(
                        notifications = notifications,
                        currentLanguage = currentLanguage,
                        onMarkAllRead = onMarkAllNotifsRead,
                        onClearAll = onClearAllNotifs
                    )
                }

                AppScreen.SWEEPSTAKES -> {
                    SweepstakesScreen(
                        currentLanguage = currentLanguage,
                        onBack = { onNavigate(AppScreen.HOME) }
                    )
                }

                AppScreen.TRANSFER -> {
                    TransferSimpleScreen(
                        currentLanguage = currentLanguage,
                        onBack = { onNavigate(AppScreen.HOME) }
                    )
                }

                AppScreen.GAME_ROOM -> {
                    GameHubScreen(
                        user = user,
                        currentLanguage = currentLanguage,
                        onNavigate = { onNavigate(it) },
                        onBack = { onNavigate(AppScreen.HOME) },
                        onLoadGameState = { onLoadGameState() }
                    )
                }

                AppScreen.GAME_MINING -> {
                    GameRoomScreen(
                        viewModel = viewModel,
                        user = user,
                        currentLanguage = currentLanguage,
                        onNavigateToMissions = { onNavigate(AppScreen.MISSIONS) },
                        onBack = { onNavigate(AppScreen.GAME_ROOM) }
                    )
                }

                AppScreen.LEADERBOARD -> {
                    LeaderboardScreen(
                        currentLanguage = currentLanguage,
                        currentUserId = user?.id,
                        onBack = { onNavigate(AppScreen.GAME_ROOM) }
                    )
                }

                AppScreen.GAME_COIN_RUSH -> {
                    CoinRushScreen(
                        user = user,
                        currentLanguage = currentLanguage,
                        onBack = { onNavigate(AppScreen.GAME_ROOM) },
                        onFinish = { score -> onFinishMiniGame(score) }
                    )
                }

                AppScreen.INVEST -> {
                    val coupons by viewModel.investmentCoupons.collectAsState()
                    InvestmentCouponScreen(
                        currentLanguage = currentLanguage,
                        user = user,
                        coupons = coupons,
                        onBuyCoupon = { coupon ->
                            viewModel.showBuyCouponModal.value = coupon
                        },
                        onListCoupon = { coupon ->
                            viewModel.showListCouponModal.value = coupon
                        },
                        onClaimYield = { coupon ->
                            viewModel.claimYield(coupon)
                        }
                    )
                }

                else -> {
                    // Screen yang dinonaktifkan (tidak ada backend support)
                    // STEP_COUNTER, APP_OFFERS, CAMPAIGNS, ANALYTICS
                }
            }  // close when (currentScreen)
            }  // close AnimatedBackground
            }  // close Box konten

            // ✅ RevolverBottomNav sebagai overlay floating di atas konten
            com.inkside.digital.ui.components.RevolverBottomNav(
                currentScreen = currentScreen,
                onNavigate = { onNavigate(it) },
                currentLanguage = currentLanguage,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .onSizeChanged {
                        bottomNavHeightState.value = with(navDensity) { it.height.toDp() }
                    }
            )
        }  // close Box utama
    }  // close Scaffold content
    }  // penutup ModalNavigationDrawer

    // Modal Overlays
    if (showWithdraw) {
        WithdrawDialog(
            user = user,
            currentLanguage = currentLanguage,
            onDismiss = onWithdrawDismiss,
            onSubmit = onWithdrawSubmit
        )
    }

    paymentGatewaySim?.let { withdrawal ->
        PaymentGatewaySimulatorModal(
            withdrawal = withdrawal,
            onDismiss = onPaymentGatewaySimDismiss
        )
    }

    if (showNewLink) {
        NewLinkModal(
            campaigns = campaigns,
            preselectedCampaign = selectedCampForLink,
            currentLanguage = currentLanguage,
            onDismiss = onNewLinkDismiss,
            onCreateLink = onNewLinkCreate
        )
    }


    if (showRedeemPoints) {
        RedeemPointsModal(
            user = user,
            currentLanguage = currentLanguage,
            onDismiss = onRedeemDismiss,
            onRedeem = onRedeemConfirm,
            onOpenTransferQr = onRedeemOpenQr
        )
    }

    if (showDailyCheckIn) {
        DailyCheckInModal(
            user = user,
            onDismiss = onDailyCheckInDismiss,
            onClaim = onDailyCheckInClaim
        )
    }

    buyCouponTarget?.let { coupon ->
        BuyCouponModal(
            coupon = coupon,
            user = user,
            currentLanguage = currentLanguage,
            onDismiss = onBuyCouponDismiss,
            onBuy = { qty -> onBuyCouponConfirm(coupon, qty) }
        )
    }

    listCouponTarget?.let { coupon ->
        ListCouponModal(
            coupon = coupon,
            onDismiss = onListCouponDismiss,
            onConfirm = { price, isListing -> onListCouponConfirm(coupon, price, isListing) }
        )
    }

    adminReviewTarget?.let { withdrawal ->
        AdminReviewModal(
            withdrawal = withdrawal,
            onDismiss = onAdminReviewDismiss,
            onProcess = { isApprove, note -> onAdminReviewProcess(withdrawal, isApprove, note) }
        )
    }

    if (show2FA) {
        TwoFactorModal(
            user = user,
            currentLanguage = currentLanguage,
            onDismiss = on2FADismiss,
            onVerifyToggle = on2FAVerifyToggle
        )
    }

    // AdReward Modal — tunjuk modal dulu, user tekan "Tonton Iklan", baru AdMob
    // Terus tunjuk AdMob rewarded video (skip modal)
    androidx.compose.runtime.LaunchedEffect(showAdReward) {
        if (showAdReward) {
            coroutineScope.launch {
                try {
                    val activity = context.findActivity()
                    if (activity != null) {
                        // ✅ FIX: Pilih mode berdasarkan pendingMinerActivate
                        val adMode = if (pendingMinerActivate) {
                            AdManager.AdMode.MINER_ACTIVATE
                        } else {
                            AdManager.AdMode.REWARD
                        }
                        AdManager.showRewardedAdForReward(
                            activity = activity,
                            userId = user?.id ?: "",
                            onSuccess = { points ->
                                onAdRewardDismiss()
                                // Trigger activate miner (RTP akan generate otomatik)
                                onAdRewardEarned(points)
                            },
                            onFailure = { reason, message ->
                                onAdRewardDismiss()
                                onAdBlockedSetReason(reason.name)
                            },
                            mode = adMode   // ✅ Pass mode
                        )
                    } else {
                        onAdRewardDismiss()
                    }
                } catch (e: Exception) {
                    AppLogger.e("AdReward", "Launch error: ${e.message}", e)
                    onAdRewardDismiss()
                }
            }
        }
    }

    if (adBlockedReason != null) {
        AdBlockedDialog(
            reason = adBlockedReason,
            onRetry = { onAdBlockedRetry() },
            onDismiss = { onAdBlockedDismiss() }
        )
    }

    // === Banned User Dialog ===
    // Baca langsung dari ApiClient.bannedState (global observable)
    // Tidak pakai bannedInfo state karena scope beda (outer vs inner function)
    val currentBanned = ApiClient.bannedState
    if (currentBanned.banned) {
        BannedDialog(
            bannedInfo = currentBanned,
            onLogout = {
                AppLogger.d("MainActivity", "🚫 User logout due to banned")
                com.google.firebase.auth.FirebaseAuth.getInstance().signOut()
                ApiClient.resetBanned()
                onLogout()
            }
        )
    }

    // === Affiliate Withdraw Dialog ===
    if (showWithdrawAffiliateModal) {
        AffiliateWithdrawDialog(
            availableBalance = affiliateBalance.available,
            minWithdraw = affiliateBalance.minWithdraw,
            userEmail = user?.email ?: "",
            isLoading = affiliateLoading,
            currentLanguage = currentLanguage,
            onSubmit = { amount, dest -> onWithdrawAffiliateSubmit(amount, dest) },
            onDismiss = { onWithdrawAffiliateDismiss() }
        )
    }

    if (showLanguageModal) {
        LanguageModal(
            currentLanguage = currentLanguage,
            onDismiss = { showLanguageModal = false },
            onSelectLanguage = { lang ->
                AppLogger.e("LANG_DEBUG", "🔵 onSelectLanguage: ${lang.code}")
                onLanguageChange(lang)
                AppLogger.e("LANG_DEBUG", "🔵 onLanguageChange selesai")
            }
        )
    }

    if (showQrTransfer) {
        QrTransferModal(
            user = user,
            contacts = contacts,
            onToggleFavorite = onQrTransferToggleFavorite,
            currentLanguage = currentLanguage,
            onDismiss = onQrTransferDismiss,
            onTransfer = onQrTransferSubmit
        )
    }

    transferSuccessReceipt?.let { receipt ->
        TransferReceiptDialog(
            receipt = receipt,
            onDismiss = onTransferReceiptDismiss
        )
    }
}


@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    androidx.compose.material3.Text(text = "Hello $name!", modifier = modifier)
}

@Preview(showBackground = true)
@Composable
fun MainAffiliateAppPreview() {
    MyApplicationTheme {
        MainAffiliateAppContent(
            user = UserEntity(
                id = "preview_user",
                name = "Preview User",
                balance = 5000000.0,
                points = 1200.0,
                // minerTokens dibuang — guna points (RTP)
            ),
            campaigns = emptyList(),
            links = emptyList(),
            withdrawals = emptyList(),
            pendingWithdrawals = emptyList(),
            transactions = emptyList(),
            coupons = emptyList(),
            notifications = emptyList(),
            unreadNotifs = 5,
            missions = emptyList(),
            allUsers = emptyList(),
            locationLogs = emptyList(),
            systemSettings = null,
            portalBaseUrl = "",
            portalApiKey = "",
            portalStatus = "IDLE",
            portalStatusMessage = "Preview Mode",
            isSyncingWithPortal = false,
            currentScreen = AppScreen.HOME,
            currentLanguage = AppLanguage.INDONESIAN,
            onLanguageChange = { },
            snackbarMsg = null,
            showWithdraw = false,
            showNewLink = false,
            showUpgrade = false,
            showRedeemPoints = false,
            showDailyCheckIn = false,
            buyCouponTarget = null,
            listCouponTarget = null,
            adminReviewTarget = null,
            showAdReward = false,
            show2FA = false,
            showQrTransfer = false,
            contacts = emptyList(),
            transferSuccessReceipt = null,
            paymentGatewaySim = null,
            selectedCampForLink = null,
            adsList = emptyList(),
            minerItems = emptyList(),
            placedMiners = emptyList(),
            roomState = null,
            onNavigate = {},
            onDismissSnackbar = {},
            onWithdrawSubmit = { _, _, _, _, _, _ -> },
            onWithdrawDismiss = {},
            onPaymentGatewaySimDismiss = {},
            onNewLinkDismiss = {},
            onNewLinkCreate = { _, _, _ -> },
            onUpgradeDismiss = {},
            onUpgradeConfirm = {},
            onRedeemDismiss = {},
            onRedeemConfirm = { _, _, _, _ -> },
            onRedeemOpenQr = {},
            onDailyCheckInDismiss = {},
            onDailyCheckInClaim = {},
            onBuyCouponDismiss = {},
            onBuyCouponConfirm = { _, _ -> },
            onListCouponDismiss = {},
            onListCouponConfirm = { _, _, _ -> },
            onAdminReviewDismiss = {},
            onAdminReviewProcess = { _, _, _ -> },
            on2FADismiss = {},
            on2FAVerifyToggle = {},
            onAdRewardDismiss = {},
            onAdRewardEarned = { _ -> },
            onQrTransferDismiss = {},
            onQrTransferToggleFavorite = {},
            onQrTransferSubmit = { _, _, _, _ -> },
            onTransferReceiptDismiss = {},
            onClaimYield = {},
            onSimulateConversion = {},
            onSaveSystemSettings = {},
            onAddSimulatedPin = { _, _, _, _, _, _, _ -> },
            onTestConnection = { _, _ -> },
            onSavePortalConfig = { _, _ -> },
            onSyncPortal = {},
            onUpdateProfile = { _, _, _, _ -> },
            onToggleLocationTracking = {},
            onMarkAllNotifsRead = {},
            onClearAllNotifs = {},
            onLoadHistory = {},
            onLoadNotifications = {},
            onSetWithdrawModalVisible = {},
            onSetNewLinkModalVisible = {},
            onSetUpgradeModalVisible = {},
            onSetRedeemPointsModalVisible = {},
            onSetQrTransferModalVisible = {},
            onSetAdRewardModalVisible = {},
            onSetBuyCouponModalVisible = {},
            onSetListCouponModalVisible = {},
            onSetAdminReviewModalVisible = {},
            onSetSelectedCampaignForLink = {},
            onClaimBannerAdBonus = { _, _, _ -> },
            onDownloadApp = {},
            onClaimAppReward = {},
            onClaimMission = {},
            onCompleteTaskAction = {},
            onAddSteps = {},
            onConvertSteps = {},
            onClaimMining = {},
            onToggleMinerSlot = { _, _ -> },
            onBuyGameMinerItem = {},
            onFinishGame = { _, _, _ -> }
        )
    }
}

