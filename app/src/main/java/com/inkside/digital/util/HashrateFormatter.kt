package com.inkside.digital.util

import java.util.Locale

/**
 * HashrateFormatter — format RTP/day untuk display.
 *
 * CATATAN PENTING:
 * File ini dulu bernama HashrateFormatter dan mengonversi powerHs (H/s)
 * ke "KRTP/s, MRTP/s, GRTP/s" — itu SALAH KAPRAH. RTP adalah satuan poin,
 * bukan satuan hashrate. powerHs (H/s) dan RTP/day adalah dua besaran
 * yang berbeda dan tidak bisa dikonversi satu sama lain.
 *
 * Sekarang formatter ini HANYA memformat RTP/day dari:
 *   - pointsPerDay (Double), atau
 *   - pointsPerMinute (Double) × 1440
 *
 * powerHs (H/s) tetap disimpan di entity untuk keperluan teknis
 * (totalPowerGhs, weighted breakdown, dll) tapi TIDAK ditampilkan
 * sebagai "RTP/day".
 */
object HashrateFormatter {

    /**
     * Format RTP/day langsung dari pointsPerDay.
     * 2.88 → "2.88 RTP/day"
     * 72.0 → "72 RTP/day"
     * 0.144 → "0.14 RTP/day"
     */
    fun formatRtpPerDay(pointsPerDay: Double): String {
        if (pointsPerDay <= 0.0) return "0 RTP/day"
        return "${formatNumber(pointsPerDay)} RTP/day"
    }

    /**
     * Format RTP/day dari pointsPerMinute (× 1440 menit/hari).
     */
    fun formatRtpPerDayFromPerMinute(pointsPerMinute: Double): String {
        return formatRtpPerDay(pointsPerMinute * 1440.0)
    }

    /**
     * Versi tanpa unit "RTP/day" (untuk space terbatas).
     */
    fun formatRtpShort(pointsPerDay: Double): String {
        if (pointsPerDay <= 0.0) return "0"
        return formatNumber(pointsPerDay)
    }

    /**
     * Format powerHs (H/s) jadi string hashrate human-readable.
     * TANPA label "RTP" — ini H/s, bukan RTP/day.
     *
     * 1_000        → "1.0 KH/s"
     * 5_000        → "5.0 KH/s"
     * 20_000       → "20 KH/s"
     * 100_000      → "100 KH/s"
     * 500_000      → "500 KH/s"
     * 1_000_000    → "1.0 MH/s"
     * 1_000_000_000 → "1.0 GH/s"
     *
     * Gunakan untuk display TEKNIS hashrate (mis. "Capacity" di Profile).
     * Untuk display rate reward, pakai formatRtpPerDayFromPerMinute().
     */
    fun formatPowerHs(powerHs: Long): String {
        if (powerHs <= 0L) return "0 H/s"
        return when {
            powerHs >= 1_000_000_000L -> String.format(Locale.US, "%.1f GH/s", powerHs / 1_000_000_000.0)
            powerHs >= 1_000_000L     -> String.format(Locale.US, "%.1f MH/s", powerHs / 1_000_000.0)
            powerHs >= 1_000L         -> String.format(Locale.US, "%.1f KH/s", powerHs / 1_000.0)
            else                      -> "$powerHs H/s"
        }.replace(".0 ", " ")
    }

    /**
     * Versi short tanpa spasi untuk space terbatas.
     * 20_000 → "20K", 1_000_000 → "1M", 1_000_000_000 → "1G"
     */
    fun formatPowerShort(powerHs: Long): String {
        if (powerHs <= 0L) return "0"
        return when {
            powerHs >= 1_000_000_000L -> String.format(Locale.US, "%.1fG", powerHs / 1_000_000_000.0)
            powerHs >= 1_000_000L     -> String.format(Locale.US, "%.1fM", powerHs / 1_000_000.0)
            powerHs >= 1_000L         -> String.format(Locale.US, "%.1fK", powerHs / 1_000.0)
            else                      -> "$powerHs"
        }.replace(".0K", "K").replace(".0M", "M").replace(".0G", "G")
    }

    /**
     * Format angka adaptif:
     *   ≥ 100    → 2 desimal   (72.50 → "72.5")
     *   ≥ 1      → 2 desimal   (2.88 → "2.88")
     *   ≥ 0.01   → 2 desimal   (0.14 → "0.14")
     *   < 0.01   → 4 desimal   (0.0001 → "0.0001")
     * Integer → tanpa desimal.
     */
    private fun formatNumber(value: Double): String {
        if (value == value.toLong().toDouble()) {
            return value.toLong().toString()
        }
        val formatted = when {
            value >= 1.0  -> String.format(Locale.US, "%.2f", value)
            value >= 0.01 -> String.format(Locale.US, "%.2f", value)
            else          -> String.format(Locale.US, "%.4f", value)
        }
        return formatted.trimEnd('0').trimEnd('.')
    }
}
