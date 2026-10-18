package com.inkside.digital.util

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.inkside.digital.util.AppLogger

/**
 * IapManager — wrapper Google Play Billing dengan mock fallback.
 *
 * Kalau Play Billing tidak available (belum publish / device tanpa Play),
 * otomatis pakai MOCK MODE — untuk testing perilaku.
 */
object IapManager {

    private const val TAG = "IapManager"

    // ============================================================
    // STATE
    // ============================================================
    private val _isReady = MutableStateFlow(false)
    val isReady: StateFlow<Boolean> = _isReady.asStateFlow()

    private val _isMockMode = MutableStateFlow(false)
    val isMockMode: StateFlow<Boolean> = _isMockMode.asStateFlow()

    private val _productDetails = MutableStateFlow<Map<String, ProductDetails>>(emptyMap())
    val productDetails: StateFlow<Map<String, ProductDetails>> = _productDetails.asStateFlow()

    private var billingClient: BillingClient? = null
    private var currentActivity: Activity? = null

    // Product IDs — harus sama dengan backend TIER_CATALOG
    val PRODUCT_IDS = listOf(
        "tier_starter_lifetime",
        "tier_premium_lifetime",
        "tier_vip_lifetime",
        "tier_royal_lifetime"
    )

    // ============================================================
    // CALLBACK
    // ============================================================
    var onPurchaseSuccess: ((productId: String, purchaseToken: String, packageName: String) -> Unit)? = null
    var onPurchaseError: ((message: String) -> Unit)? = null

    // ============================================================
    // CONNECT
    // ============================================================
    fun connect(context: Context, activity: Activity) {
        currentActivity = activity

        try {
            billingClient = BillingClient.newBuilder(context)
                .setListener(purchasesUpdatedListener)
                .enablePendingPurchases()
                .build()

            billingClient?.startConnection(object : BillingClientStateListener {
                override fun onBillingSetupFinished(result: BillingResult) {
                    if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                        AppLogger.d(TAG, "✅ Play Billing connected")
                        _isReady.value = true
                        _isMockMode.value = false
                        queryProducts()
                    } else {
                        AppLogger.w(TAG, "⚠️ Play Billing gagal connect: ${result.debugMessage}")
                        fallbackToMock()
                    }
                }

                override fun onBillingServiceDisconnected() {
                    AppLogger.w(TAG, "❌ Play Billing disconnected")
                    _isReady.value = false
                }
            })
        } catch (e: Exception) {
            AppLogger.e(TAG, "❌ Connect error: ${e.message}", e)
            fallbackToMock()
        }
    }

    private fun fallbackToMock() {
        AppLogger.w(TAG, "⚠️ Fallback ke MOCK MODE")
        _isReady.value = true
        _isMockMode.value = true
    }

    // ============================================================
    // QUERY PRODUCTS
    // ============================================================
    private fun queryProducts() {
        val client = billingClient ?: return
        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(
                PRODUCT_IDS.map { id ->
                    QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(id)
                        .setProductType(BillingClient.ProductType.INAPP)
                        .build()
                }
            )
            .build()

        client.queryProductDetailsAsync(params) { result, productDetailsList ->
            if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                val map = mutableMapOf<String, ProductDetails>()
                productDetailsList.forEach { pd ->
                    map[pd.productId] = pd
                    AppLogger.d(TAG, "Product: ${pd.productId} — ${pd.oneTimePurchaseOfferDetails?.formattedPrice}")
                }
                _productDetails.value = map
                AppLogger.d(TAG, "✅ ${map.size} produk loaded")
            } else {
                AppLogger.w(TAG, "⚠️ Query products gagal: ${result.debugMessage}")
                fallbackToMock()
            }
        }
    }

    // ============================================================
    // PURCHASE
    // ============================================================
    fun purchase(productId: String) {
        if (_isMockMode.value) {
            // Mock mode — callback langsung dengan mock token
            AppLogger.d(TAG, "🟡 MOCK PURCHASE: $productId")
            val mockToken = "mock_${System.currentTimeMillis()}_${productId}"
            onPurchaseSuccess?.invoke(productId, mockToken, currentActivity?.packageName ?: "com.inkside.digital")
            return
        }

        val client = billingClient
        val activity = currentActivity

        if (client == null || activity == null) {
            onPurchaseError?.invoke("Billing client tidak siap")
            return
        }

        val productDetails = _productDetails.value[productId]
        if (productDetails == null) {
            onPurchaseError?.invoke("Produk tidak ditemukan. Coba refresh.")
            return
        }

        val flowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(
                listOf(
                    BillingFlowParams.ProductDetailsParams.newBuilder()
                        .setProductDetails(productDetails)
                        .build()
                )
            )
            .build()

        client.launchBillingFlow(activity, flowParams)
    }

    // ============================================================
    // PURCHASE LISTENER
    // ============================================================
    private val purchasesUpdatedListener = PurchasesUpdatedListener { billingResult, purchases ->
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) {
                handlePurchase(purchase)
            }
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            AppLogger.d(TAG, "User cancelled purchase")
        } else {
            AppLogger.w(TAG, "Purchase error: ${billingResult.debugMessage}")
            onPurchaseError?.invoke("Pembelian gagal: ${billingResult.debugMessage}")
        }
    }

    private fun handlePurchase(purchase: Purchase) {
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            AppLogger.d(TAG, "✅ Purchase success: ${purchase.products}")
            purchase.products.forEach { productId ->
                onPurchaseSuccess?.invoke(productId, purchase.purchaseToken, purchase.packageName)
            }
            // Acknowledge
            acknowledgePurchase(purchase)
        } else if (purchase.purchaseState == Purchase.PurchaseState.PENDING) {
            AppLogger.d(TAG, "⏳ Purchase pending")
        }
    }

    private fun acknowledgePurchase(purchase: Purchase) {
        val client = billingClient ?: return
        if (purchase.isAcknowledged) return

        val params = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()

        client.acknowledgePurchase(params) { result ->
            if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                AppLogger.d(TAG, "✅ Purchase acknowledged")
            } else {
                AppLogger.w(TAG, "⚠️ Acknowledge failed: ${result.debugMessage}")
            }
        }
    }

    // ============================================================
    // DISCONNECT
    // ============================================================
    fun disconnect() {
        try {
            billingClient?.endConnection()
        } catch (_: Exception) {}
        billingClient = null
        _isReady.value = false
        _productDetails.value = emptyMap()
    }

    // ============================================================
    // HELPERS
    // ============================================================
    fun getPrice(productId: String): String {
        return _productDetails.value[productId]?.oneTimePurchaseOfferDetails?.formattedPrice
            ?: "—"
    }

    fun isProductLoaded(productId: String): Boolean {
        return _productDetails.value.containsKey(productId)
    }
}
