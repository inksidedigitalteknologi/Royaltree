package com.inkside.digital.util

import java.util.Locale

/**
 * HashrateFormatter — format powerHs (dalam H/s) jadi string human-readable.
 *
 * powerHs = 1_000       → 1 KH/s   → "1 KH/s"
 * powerHs = 5_000       → 5 KH/s   → "5 KH/s"
 * powerHs = 20_000      → 20 KH/s  → "20 KH/s"
 * powerHs = 100_000     → 100 KH/s → "100 KH/s"
 * powerHs = 500_000     → 500 KH/s → "500 KH/s"
 * powerHs = 1_000_000   → 1 MH/s   → "1 MH/s"
 * powerHs = 1_000_000_000 → 1 GH/s → "1 GH/s"
 */
object HashrateFormatter {

    private const val KH = 1_000L
    private const val MH = 1_000_000L
    private const val GH = 1_000_000_000L
    private const val TH = 1_000_000_000_000L

    /**
     * Format powerHs (H/s) jadi string dengan unit auto-scale + desimal.
     */
    fun format(powerHs: Long): String {
        if (powerHs <= 0) return "—"
        return when {
            powerHs < KH -> "${formatNumber(powerHs.toDouble())} H/s"
            powerHs < MH -> "${formatNumber(powerHs.toDouble() / KH)} KH/s"
            powerHs < GH -> "${formatNumber(powerHs.toDouble() / MH)} MH/s"
            powerHs < TH -> "${formatNumber(powerHs.toDouble() / GH)} GH/s"
            else -> "${formatNumber(powerHs.toDouble() / TH)} TH/s"
        }
    }

    /**
     * Versi short tanpa unit (untuk space terbatas).
     */
    fun formatShort(powerHs: Long): String {
        if (powerHs <= 0) return "0"
        return when {
            powerHs < KH -> "$powerHs"
            powerHs < MH -> "${formatNumber(powerHs.toDouble() / KH)}K"
            powerHs < GH -> "${formatNumber(powerHs.toDouble() / MH)}M"
            powerHs < TH -> "${formatNumber(powerHs.toDouble() / GH)}G"
            else -> "${formatNumber(powerHs.toDouble() / TH)}T"
        }
    }

    /**
     * Format angka: kalau integer tampil tanpa desimal, kalau pecahan tampil 2 desimal.
     */
    private fun formatNumber(value: Double): String {
        return if (value == value.toLong().toDouble()) {
            value.toLong().toString()
        } else {
            String.format(Locale.US, "%.2f", value)
                .trimEnd('0')
                .trimEnd('.')
        }
    }
}
