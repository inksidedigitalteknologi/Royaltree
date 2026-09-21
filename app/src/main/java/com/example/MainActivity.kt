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
import com.example.ui.screens.AppOffersScreen
import com.example.ui.screens.CampaignsScreen
import com.example.ui.screens.GameRoomScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.InvestmentCouponScreen
import com.example.ui.screens.MissionsScreen
import com.example.ui.screens.NotificationsScreen
import com.example.ui.screens.ProfileSecurityScreen
import com.example.ui.screens.StepCounterScreen
import com.example.ui.screens.WithdrawalScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.AffiliateViewModel
import com.example.viewmodel.AppScreen

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

    // Dialog state collections
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

    var showLanguageModal by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(snackbarMsg) {
        snackbarMsg?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.dismissSnackbar()
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
                onToggleRole = { viewModel.setAdminMode(!isAdminMode) },
                onOpenLanguage = { showLanguageModal = true },
                onOpenNotifs = { viewModel.navigateTo(AppScreen.NOTIFICATIONS) },
                onOpenSecurity = { viewModel.navigateTo(AppScreen.PROFILE) }
            )
        },
        bottomBar = {
            AppBottomNavigationBar(
                currentScreen = currentScreen,
                isAdminMode = isAdminMode,
                currentLanguage = currentLanguage,
                onNavigate = { viewModel.navigateTo(it) }
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
                        onNavigate = { viewModel.navigateTo(it) },
                        onOpenWithdraw = { viewModel.showWithdrawModal.value = true },
                        onOpenNewLink = { viewModel.showNewLinkModal.value = true },
                        onOpenUpgrade = { viewModel.showUpgradeModal.value = true },
                        onOpenRedeemPoints = { viewModel.showRedeemPointsModal.value = true },
                        onOpenTransferQr = { viewModel.showQrTransferModal.value = true },
                        onSimulateConversion = { viewModel.simulateConversion(it) },
                        onOpenAdReward = { viewModel.showAdRewardModal.value = true }
                    )
                }

                AppScreen.STEP_COUNTER -> {
                    StepCounterScreen(
                        viewModel = viewModel
                    )
                }

                AppScreen.APP_OFFERS -> {
                    AppOffersScreen(
                        viewModel = viewModel
                    )
                }

                AppScreen.CAMPAIGNS -> {
                    CampaignsScreen(
                        campaigns = campaigns,
                        links = links,
                        onSelectCampaignForLink = { campaign ->
                            viewModel.selectedCampaignForLink.value = campaign
                            viewModel.showNewLinkModal.value = true
                        },
                        onSimulateConversion = { viewModel.simulateConversion(it) },
                        onOpenNewLinkModal = { viewModel.showNewLinkModal.value = true }
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
                        onOpenWithdrawModal = { viewModel.showWithdrawModal.value = true }
                    )
                }

                AppScreen.INVEST -> {
                    InvestmentCouponScreen(
                        user = user,
                        coupons = coupons,
                        onBuyCoupon = { viewModel.showBuyCouponModal.value = it },
                        onListCoupon = { viewModel.showListCouponModal.value = it },
                        onClaimYield = { viewModel.claimYield(it) }
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
                        onReviewWithdrawal = { viewModel.showAdminReviewModal.value = it },
                        onSaveSystemSettings = { viewModel.saveSystemSettings(it) },
                        onAddSimulatedPin = { userId, name, tier, lat, lng, city, prov ->
                            viewModel.addProviderSimulatedPin(userId, name, tier, lat, lng, city, prov)
                        },
                        onTestConnection = { url, key -> viewModel.testPortalConnection(url, key) },
                        onSavePortalConfig = { url, key -> viewModel.savePortalConfiguration(url, key) },
                        onSyncPortal = { viewModel.syncDataWithPortal() },
                        onBackToUserMode = { viewModel.setAdminMode(false) }
                    )
                }

                AppScreen.PROFILE -> {
                    ProfileSecurityScreen(
                        user = user,
                        currentLanguage = currentLanguage,
                        onOpenLanguage = { showLanguageModal = true },
                        onOpenUpgrade = { viewModel.showUpgradeModal.value = true },
                        onOpen2FA = { viewModel.show2FAModal.value = true },
                        onOpenAdReward = { viewModel.showAdRewardModal.value = true },
                        onToggleLocationTracking = { viewModel.toggleLocationTracking(it) },
                        onSaveProfile = { name, email, phone, region ->
                            viewModel.updateProfile(name, email, phone, region)
                        }
                    )
                }

                AppScreen.MISSIONS -> {
                    MissionsScreen(
                        user = user,
                        missions = missions,
                        currentLanguage = currentLanguage,
                        onBack = { viewModel.navigateTo(AppScreen.HOME) },
                        onCheckIn = { viewModel.performDailyCheckIn() },
                        onClaimMission = { viewModel.claimMissionReward(it) },
                        onCompleteTaskAction = { viewModel.completeTaskAction(it) },
                        onOpenRedeemPoints = { viewModel.showRedeemPointsModal.value = true },
                        onOpenTransferQr = { viewModel.showQrTransferModal.value = true },
                        onOpenWatchAd = { viewModel.showAdRewardModal.value = true },
                        onNavigateToGameRoom = { viewModel.navigateTo(AppScreen.GAME_ROOM) }
                    )
                }

                AppScreen.GAME_ROOM -> {
                    GameRoomScreen(
                        viewModel = viewModel,
                        user = user,
                        currentLanguage = currentLanguage,
                        onNavigateToMissions = { viewModel.navigateTo(AppScreen.MISSIONS) },
                        onBack = { viewModel.navigateTo(AppScreen.HOME) }
                    )
                }

                AppScreen.NOTIFICATIONS -> {
                    NotificationsScreen(
                        notifications = notifications,
                        onMarkAllRead = { viewModel.markAllNotificationsRead() }
                    )
                }
            }
        }
    }

    // Modal Overlays
    if (showWithdraw) {
        WithdrawDialog(
            user = user,
            onDismiss = { viewModel.showWithdrawModal.value = false },
            onSubmit = { amount, currency, channel, provider, accountDest, accountHolder ->
                viewModel.requestWithdrawal(amount, currency, channel, provider, accountDest, accountHolder)
            }
        )
    }

    paymentGatewaySim?.let { withdrawal ->
        PaymentGatewaySimulatorModal(
            withdrawal = withdrawal,
            onDismiss = { viewModel.showPaymentGatewaySimulator.value = null }
        )
    }

    if (showNewLink) {
        NewLinkModal(
            campaigns = campaigns,
            preselectedCampaign = selectedCampForLink,
            onDismiss = {
                viewModel.showNewLinkModal.value = false
                viewModel.selectedCampaignForLink.value = null
            },
            onCreateLink = { camp, slug, subId ->
                viewModel.createLink(camp, slug, subId)
            }
        )
    }

    if (showUpgrade) {
        UpgradeVipModal(
            user = user,
            onDismiss = { viewModel.showUpgradeModal.value = false },
            onUpgrade = { viewModel.upgradeToPremium() }
        )
    }

    if (showRedeemPoints) {
        RedeemPointsModal(
            user = user,
            onDismiss = { viewModel.showRedeemPointsModal.value = false },
            onRedeem = { pts, wallet, phoneOrDest, currency ->
                viewModel.redeemPoints(pts, wallet, phoneOrDest, currency)
            },
            onOpenTransferQr = { viewModel.showQrTransferModal.value = true }
        )
    }

    if (showDailyCheckIn) {
        DailyCheckInModal(
            user = user,
            onDismiss = { viewModel.showDailyCheckInModal.value = false },
            onClaim = { viewModel.performDailyCheckIn() }
        )
    }

    buyCouponTarget?.let { coupon ->
        BuyCouponModal(
            coupon = coupon,
            user = user,
            onDismiss = { viewModel.showBuyCouponModal.value = null },
            onBuy = { qty ->
                viewModel.buyCoupon(coupon, qty)
            }
        )
    }

    listCouponTarget?.let { coupon ->
        ListCouponModal(
            coupon = coupon,
            onDismiss = { viewModel.showListCouponModal.value = null },
            onConfirm = { price, isListing ->
                viewModel.toggleListingSecondaryMarket(coupon, price, isListing)
            }
        )
    }

    adminReviewTarget?.let { withdrawal ->
        AdminReviewModal(
            withdrawal = withdrawal,
            onDismiss = { viewModel.showAdminReviewModal.value = null },
            onProcess = { isApprove, note ->
                viewModel.adminProcessWithdrawal(withdrawal, isApprove, note)
            }
        )
    }

    if (show2FA) {
        TwoFactorModal(
            user = user,
            onDismiss = { viewModel.show2FAModal.value = false },
            onVerifyToggle = { enable ->
                viewModel.toggle2FA(enable)
            }
        )
    }

    if (showAdReward) {
        AdRewardModal(
            onDismiss = { viewModel.showAdRewardModal.value = false },
            onRewardEarned = { viewModel.claimAdReward() }
        )
    }

    if (showLanguageModal) {
        LanguageModal(
            currentLanguage = currentLanguage,
            onDismiss = { showLanguageModal = false },
            onSelectLanguage = { lang ->
                viewModel.setLanguage(lang)
            }
        )
    }

    if (showQrTransfer) {
        QrTransferModal(
            user = user,
            contacts = contacts,
            onToggleFavorite = { contactId ->
                viewModel.toggleFavoriteContact(contactId)
            },
            currentLanguage = currentLanguage,
            onDismiss = { viewModel.showQrTransferModal.value = false },
            onTransfer = { recipientId, recipientName, amount, note ->
                viewModel.transferPoints(recipientId, recipientName, amount, note)
            }
        )
    }

    transferSuccessReceipt?.let { receipt ->
        TransferReceiptDialog(
            receipt = receipt,
            onDismiss = { viewModel.dismissTransferReceipt() }
        )
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    androidx.compose.material3.Text(text = "Hello $name!", modifier = modifier)
}

