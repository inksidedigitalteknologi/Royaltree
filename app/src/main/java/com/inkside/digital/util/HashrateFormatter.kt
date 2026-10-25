package com.inkside.digital.util

import java.util.Locale

/**
 * HashrateFormatter — format powerHs (dalam H/s) jadi string human-readable.
 *
 * powerHs = 1_000       → 1 KRTP/s  → "1 KRTP/s"
 * powerHs = 5_000       → 5 KRTP/s  → "5 KRTP/s"
 * powerHs = 20_000      → 20 KRTP/s → "20 KRTP/s"
 * powerHs = 100_000     → 100 KRTP/s → "100 KRTP/s"
 * powerHs = 500_000     → 500 KRTP/s → "500 KRTP/s"
 * powerHs = 1_000_000   → 1 MRTP/s  → "1 MRTP/s"
 * powerHs = 1_000_000_000 → 1 GRTP/s → "1 GRTP/s"
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
        // Konversi hashrate → RTP/hari
        // 1 GH/s = 1_000_000_000 H/s → 0.024 RTP/hari
        val rtpPerDay = powerHs / 1_000_000_000.0 * 0.024
        return "${formatNumber(rtpPerDay)} RTP/day"
    }

    /**
     * Versi short tanpa unit (untuk space terbatas).
     */
    fun formatShort(powerHs: Long): String {
        if (powerHs <= 0) return "0"
        val rtpPerDay = powerHs / 1_000_000_000.0 * 0.024
        return formatNumber(rtpPerDay)
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
