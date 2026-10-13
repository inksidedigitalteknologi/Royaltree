package com.inkside.digital.sensor

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * BatteryTempHelper — baca suhu baterai HP.
 * TIDAK PERLU IZIN — pakai BatteryManager.
 */
object BatteryTempHelper {

    private var monitorJob: Job? = null

    /**
     * Baca suhu baterai sekali.
     * @return suhu dalam Celsius (misal 35.2)
     */
    fun getBatteryTemperature(context: Context): Float {
        return try {
            val intent = context.registerReceiver(
                null,
                IntentFilter(Intent.ACTION_BATTERY_CHANGED)
            )
            val tempTenths = intent?.getIntExtra(
                BatteryManager.EXTRA_TEMPERATURE, 0
            ) ?: 0
            tempTenths / 10f
        } catch (e: Exception) {
            0f
        }
    }

    /**
     * Baca level baterai (0-100).
     */
    fun getBatteryLevel(context: Context): Int {
        return try {
            val intent = context.registerReceiver(
                null,
                IntentFilter(Intent.ACTION_BATTERY_CHANGED)
            )
            val level = intent?.getIntExtra(BatteryManager.EXTRA_LEVEL, 0) ?: 0
            val scale = intent?.getIntExtra(BatteryManager.EXTRA_SCALE, 100) ?: 100
            (level * 100 / scale)
        } catch (e: Exception) {
            0
        }
    }

    /**
     * Cek apakah HP sedang charging.
     */
    fun isCharging(context: Context): Boolean {
        return try {
            val intent = context.registerReceiver(
                null,
                IntentFilter(Intent.ACTION_BATTERY_CHANGED)
            )
            val status = intent?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
            status == BatteryManager.BATTERY_STATUS_CHARGING ||
                    status == BatteryManager.BATTERY_STATUS_FULL
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Monitor suhu secara real-time (setiap 5 detik).
     */
    fun startMonitoring(
        context: Context,
        scope: CoroutineScope,
        onUpdate: (temp: Float, level: Int, charging: Boolean) -> Unit
    ) {
        stopMonitoring()
        monitorJob = scope.launch(Dispatchers.IO) {
            while (isActive) {
                val temp = getBatteryTemperature(context)
                val level = getBatteryLevel(context)
                val charging = isCharging(context)
                launch(Dispatchers.Main) {
                    onUpdate(temp, level, charging)
                }
                delay(5000L)
            }
        }
    }

    fun stopMonitoring() {
        monitorJob?.cancel()
        monitorJob = null
    }

    /**
     * Get status suhu (untuk UI indicator).
     */
    fun getTempStatus(tempCelsius: Float): TempStatus {
        return when {
            tempCelsius < 25f -> TempStatus.COLD
            tempCelsius < 30f -> TempStatus.NORMAL
            tempCelsius < 35f -> TempStatus.WARM
            tempCelsius < 40f -> TempStatus.HOT
            tempCelsius < 45f -> TempStatus.VERY_HOT
            tempCelsius < 50f -> TempStatus.CRITICAL
            else -> TempStatus.THROTTLE
        }
    }

    enum class TempStatus(val emoji: String, val label: String, val hashrateFactor: Float) {
        COLD("❄️", "Cold", 1.05f),
        NORMAL("✅", "Normal", 1.00f),
        WARM("🟡", "Warm", 0.95f),
        HOT("🟠", "Hot", 0.85f),
        VERY_HOT("🔴", "Very Hot", 0.70f),
        CRITICAL("🚨", "Critical", 0.50f),
        THROTTLE("⛔", "Throttle", 0.30f)
    }
}
