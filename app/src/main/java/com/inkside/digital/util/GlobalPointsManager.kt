package com.inkside.digital.util

import java.text.NumberFormat
import java.util.Locale

object GlobalPointsManager {
    const val POINT_NAME = "RTP"
    const val POINT_FULL_NAME = "Royaltree Point (RTP)"

    // Universal Global Baseline: 100 RTP = $1.00 USD / 1.00 USDT (1 RTP = 1 US Cent)
    const val USD_PER_100_POINTS = 1.00
    const val USD_PER_POINT = 0.01

    // Global Regional Exchange Rates relative to 1 USD
    const val RATE_USD_TO_IDR = 16000.0
    const val RATE_USD_TO_EUR = 0.92
    const val RATE_USD_TO_GBP = 0.78
    const val RATE_USD_TO_JPY = 155.0
    const val RATE_USD_TO_CNY = 7.25
    const val RATE_USD_TO_SAR = 3.75
    const val RATE_USD_TO_USDT = 1.00

    // Daily Login Rewards by Streak Day (1 to 7)
    val DAILY_STREAK_REWARDS = listOf(15, 25, 35, 50, 75, 100, 150)

    // RTP Transfer Rules & Anti-Fraud Security Constants
    const val MIN_TRANSFER_RTP = 20
    const val MAX_TRANSFER_PER_TX_RTP = 1000 // Anti-drain limit per single transaction
    const val DAILY_TRANSFER_LIMIT_RTP = 2500 // Anti-drain limit per 24 hours rolling
    const val TRANSFER_ADMIN_FEE_RTP = 5 // Fixed price admin fee in RTP, deducted from recipient's received balance
    const val TRANSFER_COOLDOWN_MS = 3000L // 3-second velocity cooldown between transfers to prevent botting/rapid-clicks
    const val DEFAULT_SECURITY_PIN = "123456" // 6-digit transaction PIN

    fun calculateNetReceived(amount: Int): Int {
        return (amount - TRANSFER_ADMIN_FEE_RTP).coerceAtLeast(0)
    }

    /**
     * Generates a cryptographic SHA-256 verification hash to prevent transaction tampering or forgery.
     */
    fun generateSecuritySignature(
        txId: String,
        senderId: String,
        recipientId: String,
        amount: Int,
        timestamp: Long
    ): String {
        val payload = "RT-SEC:$txId|$senderId->$recipientId:$amount@$timestamp#ROYALTREE_SALT_9821"
        return try {
            val md = java.security.MessageDigest.getInstance("SHA-256")
            val digest = md.digest(payload.toByteArray(Charsets.UTF_8))
            digest.joinToString("") { "%02x".format(it) }.take(16).uppercase()
        } catch (e: Exception) {
            "SIG-" + java.util.UUID.randomUUID().toString().take(8).uppercase()
        }
    }

    /**
     * Sanitizes recipient identifiers to prevent injection or malformed strings.
     */
    fun sanitizeIdentifier(input: String): String {
        return input.trim().replace(Regex("[^a-zA-Z0-9_@.-]"), "")
    }

    fun getLoginRewardForStreak(streakDay: Int): Int {
        val index = (streakDay - 1).coerceIn(0, DAILY_STREAK_REWARDS.size - 1)
        return DAILY_STREAK_REWARDS[index]
    }

    fun getUsdValue(points: Int): Double {
        return points * USD_PER_POINT
    }

    fun getIdrValue(points: Int): Double {
        return getUsdValue(points) * RATE_USD_TO_IDR
    }

    fun getValueInCurrency(points: Int, currencyCode: String): Double {
        val usd = getUsdValue(points)
        return when (currencyCode.uppercase()) {
            "USD" -> usd
            "USDT" -> usd * RATE_USD_TO_USDT
            "EUR" -> usd * RATE_USD_TO_EUR
            "GBP" -> usd * RATE_USD_TO_GBP
            "JPY" -> usd * RATE_USD_TO_JPY
            "CNY" -> usd * RATE_USD_TO_CNY
            "SAR" -> usd * RATE_USD_TO_SAR
            "IDR" -> usd * RATE_USD_TO_IDR
            else -> usd * RATE_USD_TO_IDR
        }
    }

    fun formatFormattedDualValue(points: Int): String {
        val usd = getUsdValue(points)
        val idr = getIdrValue(points)
        return "$points RTP ($${String.format(Locale.US, "%.2f", usd)} USD ≈ Rp ${String.format("%,.0f", idr)})"
    }

    fun formatCurrencyDisplay(amount: Double, currencyCode: String): String {
        return when (currencyCode.uppercase()) {
            "USD" -> "$${String.format(Locale.US, "%.2f", amount)} USD"
            "USDT" -> "${String.format(Locale.US, "%.2f", amount)} USDT"
            "EUR" -> "€${String.format(Locale.US, "%.2f", amount)} EUR"
            "GBP" -> "£${String.format(Locale.US, "%.2f", amount)} GBP"
            "JPY" -> "¥${String.format(Locale.US, "%,.0f", amount)} JPY"
            "CNY" -> "¥${String.format(Locale.US, "%.2f", amount)} CNY"
            "SAR" -> "${String.format(Locale.US, "%.2f", amount)} SAR"
            "IDR" -> "Rp ${String.format("%,.0f", amount)}"
            else -> "Rp ${String.format("%,.0f", amount)}"
        }
    }
}
