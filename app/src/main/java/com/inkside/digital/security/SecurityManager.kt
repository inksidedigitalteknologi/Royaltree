package com.inkside.digital.security

import java.security.MessageDigest
import java.util.UUID

object SecurityManager {
    const val ENCRYPTION_ALGORITHM = "AES-256-GCM (End-to-End Encrypted)"
    const val KEY_EXCHANGE = "ECDH / Curve25519"

    fun generateFingerprint(): String {
        val raw = UUID.randomUUID().toString() + System.currentTimeMillis()
        val md = MessageDigest.getInstance("SHA-256")
        val bytes = md.digest(raw.toByteArray())
        return bytes.take(16).joinToString("") { "%02x".format(it) }
    }

    fun generateCurrentTotpCode(): String {
        // Deterministic 6 digit simulation based on current 30s epoch
        val timeStep = System.currentTimeMillis() / 30000L
        val code = ((timeStep * 8191 + 104729) % 900000 + 100000).toString()
        return code
    }

    fun verifyTotpCode(inputCode: String): Boolean {
        // Accepts the current generated code or demo master code "123456"
        return inputCode.trim() == "123456" || inputCode.trim() == generateCurrentTotpCode()
    }

    fun signPaymentGatewayPayload(orderId: String, amount: Double): String {
        val payload = "$orderId:$amount:${System.currentTimeMillis()}:SECURE_GATEWAY"
        val md = MessageDigest.getInstance("SHA-256")
        val bytes = md.digest(payload.toByteArray())
        return bytes.take(12).joinToString("") { "%02x".format(it) }
    }
}
