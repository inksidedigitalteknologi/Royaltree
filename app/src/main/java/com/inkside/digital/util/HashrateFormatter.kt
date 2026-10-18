package com.inkside.digital.util

import java.util.Locale

/**
 * HashrateFormatter — format powerGhs (dalam GH/s) jadi string human-readable.
 *
 * powerGhs = 10     → 10 GH/s → "10 GH/s"
 * powerGhs = 0.005  → 5 MH/s  → "5 MH/s"
 * powerGhs = 1500   → 1.5 TH/s → "1.50 TH/s"
 * powerGhs = 0.0001 → 100 KH/s → "100 KH/s"
 */
object HashrateFormatter {

    private const val H = 1.0
    private const val KH = 1_000.0
    private const val MH = 1_000_000.0
    private const val GH = 1_000_000_000.0
    private const val TH = 1_000_000_000_000.0

    /**
     * Convert powerGhs (GH/s) ke H/s (base unit).
     * 1 GH/s = 1_000_000_000 H/s
     */
    private fun toHashPerSec(powerGhs: Double): Double {
        return powerGhs * GH
    }

    /**
     * Format powerGhs jadi string dengan unit auto-scale + desimal.
     *
     * Contoh:
     * - 0.0001 → "100 KH/s"
     * - 0.005  → "5 MH/s"
     * - 0.5    → "500 MH/s"
     * - 10     → "10 GH/s"
     * - 1500   → "1.50 TH/s"
     */
    fun format(powerGhs: Double): String {
        if (powerGhs <= 0) return "—"

        val hps = toHashPerSec(powerGhs)

        return when {
            hps < KH -> "${formatNumber(hps)} H/s"
            hps < MH -> "${formatNumber(hps / KH)} KH/s"
            hps < GH -> "${formatNumber(hps / MH)} MH/s"
            hps < TH -> "${formatNumber(hps / GH)} GH/s"
            else -> "${formatNumber(hps / TH)} TH/s"
        }
    }

    /**
     * Versi short tanpa unit (untuk space terbatas).
     */
    fun formatShort(powerGhs: Double): String {
        if (powerGhs <= 0) return "0"
        val hps = toHashPerSec(powerGhs)
        return when {
            hps < KH -> "${hps.toInt()}"
            hps < MH -> "${formatNumber(hps / KH)}K"
            hps < GH -> "${formatNumber(hps / MH)}M"
            hps < TH -> "${formatNumber(hps / GH)}G"
            else -> "${formatNumber(hps / TH)}T"
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
