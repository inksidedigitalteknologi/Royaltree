package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.*
import com.example.data.repository.PeerContact
import com.example.data.repository.TransferResult
import com.example.localization.AppLanguage
import com.example.ui.components.AdRewardModal
import com.example.ui.components.AdminReviewModal
import com.example.ui.components.AppBottomNavigationBar
import com.example.ui.components.AppTopBar
import com.example.ui.components.BuyCouponModal
import com.example.ui.components.DailyCheckInModal
import com.example.ui.components.LanguageModal
import com.example.ui.components.ListCouponModal
import com.example.ui.components.NewLinkModal
import com.example.ui.components.PaymentGatewaySimulatorModal
import com.example.ui.components.QrTransferModal
import com.example.ui.components.RedeemPointsModal
import com.example.ui.components.TransferReceiptDialog
import com.example.ui.components.TwoFactorModal
import com.example.ui.components.UpgradeVipModal
import com.example.ui.components.WithdrawDialog
import com.example.ui.screens.AdminScreen
import com.example.ui.screens.AnalyticsScreen
import com.example.ui.screens.CampaignsScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.InvestmentCouponScreen
import com.example.ui.screens.MissionsScreen
import com.example.ui.screens.NotificationsScreen
import com.example.ui.screens.ProfileSecurityScreen
import com.example.ui.screens.StepCounterScreenContent
import com.example.ui.screens.AppOffersScreenContent
import com.example.ui.screens.GameRoomScreenContent
import com.example.ui.screens.WithdrawalScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.AffiliateViewModel
import com.example.viewmodel.AppScreen
import androidx.compose.ui.tooling.preview.Preview

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainAffiliateApp()
            }
        }
    }
}

@Composable
fun MainAffiliateApp(viewModel: AffiliateViewModel = viewModel()) {
    val user by viewModel.user.collectAsState()
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
    val isAdminMode by viewModel.isAdminMode.collectAsState()
    val currentLanguage by viewModel.currentLanguage.collectAsState()
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

    MainAffiliateAppContent(
        user = user,
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
        isAdminMode = isAdminMode,
        currentLanguage = currentLanguage,
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
        onSetAdminMode = { viewModel.setAdminMode(it) },
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
        onRedeemDismiss = { viewModel.showRedeemPointsModal.value = false },
        onRedeemConfirm = { pts, wallet, phone, curr -> viewModel.redeemPoints(pts, wallet, phone, curr) },
        onRedeemOpenQr = { viewModel.showQrTransferModal.value = true },
        onDailyCheckInDismiss = { viewModel.showDailyCheckInModal.value = false },
        onDailyCheckInClaim = { viewModel.performDailyCheckIn() },
        onBuyCouponDismiss = { viewModel.showBuyCouponModal.value = null },
        onBuyCouponConfirm = { coupon, qty -> viewModel.buyCoupon(coupon, qty) },
        onListCouponDismiss = { viewModel.showListCouponModal.value = null },
        onListCouponConfirm = { coupon, price, isListing -> viewModel.toggleListingSecondaryMarket(coupon, price, isListing) },
        onAdminReviewDismiss = { viewModel.showAdminReviewModal.value = null },
        onAdminReviewProcess = { withdrawal, isApprove, note -> viewModel.adminProcessWithdrawal(withdrawal, isApprove, note) },
        on2FADismiss = { viewModel.show2FAModal.value = false },
        on2FAVerifyToggle = { viewModel.toggle2FA(it) },
        onAdRewardDismiss = { viewModel.showAdRewardModal.value = false },
        onAdRewardEarned = { viewModel.claimAdReward() },
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
        onClaimMining = { viewModel.claimGameMiningPoints() },
        onToggleMinerSlot = { id, slot -> viewModel.toggleMinerSlot(id, slot) },
        onBuyGameMinerItem = { id -> viewModel.buyGameMinerItem(id) },
        onFinishGame = { score -> viewModel.finishMiniGame(score) }
    )
}

@Composable
fun MainAffiliateAppContent(
    user: UserEntity?,
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
    isAdminMode: Boolean,
    currentLanguage: AppLanguage,
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
    onSetAdminMode: (Boolean) -> Unit,
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
    onBuyCouponDismiss: () -> Unit,
    onBuyCouponConfirm: (InvestmentCouponEntity, Int) -> Unit,
    onListCouponDismiss: () -> Unit,
    onListCouponConfirm: (InvestmentCouponEntity, Double, Boolean) -> Unit,
    onAdminReviewDismiss: () -> Unit,
    onAdminReviewProcess: (WithdrawalEntity, Boolean, String) -> Unit,
    on2FADismiss: () -> Unit,
    on2FAVerifyToggle: (Boolean) -> Unit,
    onAdRewardDismiss: () -> Unit,
    onAdRewardEarned: () -> Unit,
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
    onToggleMinerSlot: (String, Int) -> Unit,
    onBuyGameMinerItem: (String) -> Unit,
    onFinishGame: (Int) -> Unit
) {
    var showLanguageModal by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(snackbarMsg) {
        snackbarMsg?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            onDismissSnackbar()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            AppTopBar(
                user = user,
                isAdminMode = isAdminMode,
                unreadNotifs = unreadNotifs,
                currentLanguage = currentLanguage,
                onToggleRole = { onSetAdminMode(!isAdminMode) },
                onOpenLanguage = { showLanguageModal = true },
                onOpenNotifs = { onNavigate(AppScreen.NOTIFICATIONS) },
                onOpenSecurity = { onNavigate(AppScreen.PROFILE) }
            )
        },
        bottomBar = {
            AppBottomNavigationBar(
                currentScreen = currentScreen,
                isAdminMode = isAdminMode,
                currentLanguage = currentLanguage,
                onNavigate = { onNavigate(it) }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                AppScreen.HOME -> {
                    HomeScreen(
                        user = user,
                        links = links,
                        recentTransactions = transactions,
                        currentLanguage = currentLanguage,
                        onNavigate = { onNavigate(it) },
                        onOpenWithdraw = { onSetWithdrawModalVisible(true) },
                        onOpenNewLink = { onSetNewLinkModalVisible(true) },
                        onOpenUpgrade = { onSetUpgradeModalVisible(true) },
                        onOpenRedeemPoints = { onSetRedeemPointsModalVisible(true) },
                        onOpenTransferQr = { onSetQrTransferModalVisible(true) },
                        onSimulateConversion = { onSimulateConversion(it) },
                        onOpenAdReward = { onSetAdRewardModalVisible(true) }
                    )
                }

                AppScreen.STEP_COUNTER -> {
                    StepCounterScreenContent(
                        user = user,
                        transactions = transactions,
                        onAddSteps = onAddSteps,
                        onConvertSteps = onConvertSteps
                    )
                }

                AppScreen.APP_OFFERS -> {
                    AppOffersScreenContent(
                        adsList = adsList,
                        onClaimBannerBonus = onClaimBannerAdBonus,
                        onDownloadApp = onDownloadApp,
                        onClaimAppReward = onClaimAppReward
                    )
                }

                AppScreen.CAMPAIGNS -> {
                    CampaignsScreen(
                        campaigns = campaigns,
                        links = links,
                        onSelectCampaignForLink = { campaign ->
                            onSetSelectedCampaignForLink(campaign)
                            onSetNewLinkModalVisible(true)
                        },
                        onSimulateConversion = { onSimulateConversion(it) },
                        onOpenNewLinkModal = { onSetNewLinkModalVisible(true) }
                    )
                }

                AppScreen.ANALYTICS -> {
                    AnalyticsScreen(
                        campaigns = campaigns,
                        links = links
                    )
                }

                AppScreen.WITHDRAW -> {
                    WithdrawalScreen(
                        user = user,
                        withdrawals = withdrawals,
                        onOpenWithdrawModal = { onSetWithdrawModalVisible(true) }
                    )
                }

                AppScreen.INVEST -> {
                    InvestmentCouponScreen(
                        user = user,
                        coupons = coupons,
                        onBuyCoupon = { onSetBuyCouponModalVisible(it) },
                        onListCoupon = { onSetListCouponModalVisible(it) },
                        onClaimYield = { onClaimYield(it) }
                    )
                }

                AppScreen.HISTORY -> {
                    HistoryScreen(
                        transactions = transactions
                    )
                }

                AppScreen.ADMIN -> {
                    AdminScreen(
                        user = user,
                        allUsers = allUsers,
                        pendingWithdrawals = pendingWithdrawals,
                        allWithdrawals = withdrawals,
                        locationLogs = locationLogs,
                        systemSettings = systemSettings,
                        portalBaseUrl = portalBaseUrl,
                        portalApiKey = portalApiKey,
                        portalStatus = portalStatus,
                        portalStatusMessage = portalStatusMessage,
                        isSyncingWithPortal = isSyncingWithPortal,
                        onReviewWithdrawal = { onSetAdminReviewModalVisible(it) },
                        onSaveSystemSettings = { onSaveSystemSettings(it) },
                        onAddSimulatedPin = onAddSimulatedPin,
                        onTestConnection = onTestConnection,
                        onSavePortalConfig = onSavePortalConfig,
                        onSyncPortal = onSyncPortal,
                        onBackToUserMode = { onSetAdminMode(false) }
                    )
                }

                AppScreen.PROFILE -> {
                    ProfileSecurityScreen(
                        user = user,
                        currentLanguage = currentLanguage,
                        onOpenLanguage = { showLanguageModal = true },
                        onOpenUpgrade = { onSetUpgradeModalVisible(true) },
                        onOpen2FA = { onSetAdRewardModalVisible(true) },
                        onOpenAdReward = { onSetAdRewardModalVisible(true) },
                        onToggleLocationTracking = onToggleLocationTracking,
                        onSaveProfile = onUpdateProfile
                    )
                }

                AppScreen.MISSIONS -> {
                    MissionsScreen(
                        user = user,
                        missions = missions,
                        currentLanguage = currentLanguage,
                        onBack = { onNavigate(AppScreen.HOME) },
                        onCheckIn = { onDailyCheckInClaim() },
                        onClaimMission = { onClaimMission(it) },
                        onCompleteTaskAction = { onCompleteTaskAction(it) },
                        onOpenRedeemPoints = { onSetRedeemPointsModalVisible(true) },
                        onOpenTransferQr = { onSetQrTransferModalVisible(true) },
                        onOpenWatchAd = { onSetAdRewardModalVisible(true) },
                        onNavigateToGameRoom = { onNavigate(AppScreen.GAME_ROOM) }
                    )
                }

                AppScreen.GAME_ROOM -> {
                    GameRoomScreenContent(
                        user = user,
                        minerItems = minerItems,
                        placedMiners = placedMiners,
                        roomState = roomState,
                        onClaimMining = onClaimMining,
                        onToggleMinerSlot = onToggleMinerSlot,
                        onBuyGameMinerItem = onBuyGameMinerItem,
                        onFinishGame = onFinishGame,
                        onNavigateToMissions = { onNavigate(AppScreen.MISSIONS) },
                        onBack = { onNavigate(AppScreen.HOME) },
                        currentLanguage = currentLanguage
                    )
                }

                AppScreen.NOTIFICATIONS -> {
                    NotificationsScreen(
                        notifications = notifications,
                        onMarkAllRead = onMarkAllNotifsRead
                    )
                }
            }
        }
    }

    // Modal Overlays
    if (showWithdraw) {
        WithdrawDialog(
            user = user,
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
            onDismiss = onNewLinkDismiss,
            onCreateLink = onNewLinkCreate
        )
    }

    if (showUpgrade) {
        UpgradeVipModal(
            user = user,
            onDismiss = onUpgradeDismiss,
            onUpgrade = onUpgradeConfirm
        )
    }

    if (showRedeemPoints) {
        RedeemPointsModal(
            user = user,
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
            onDismiss = on2FADismiss,
            onVerifyToggle = on2FAVerifyToggle
        )
    }

    if (showAdReward) {
        AdRewardModal(
            onDismiss = onAdRewardDismiss,
            onRewardEarned = onAdRewardEarned
        )
    }

    if (showLanguageModal) {
        LanguageModal(
            currentLanguage = currentLanguage,
            onDismiss = { showLanguageModal = false },
            onSelectLanguage = { _ ->
                // This would normally go back to VM
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
                name = "Preview User",
                balance = 5000000.0,
                points = 1200
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
            isAdminMode = false,
            currentLanguage = AppLanguage.INDONESIAN,
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
            onSetAdminMode = {},
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
            onAdRewardEarned = {},
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
            onFinishGame = {}
        )
    }
}

