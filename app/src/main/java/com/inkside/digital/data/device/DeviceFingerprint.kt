package com.inkside.digital.data.device

import android.app.ActivityManager
import android.content.Context
import android.os.Build

data class DeviceFingerprint(val model: String, val totalRamMb: Long, val cpuCores: Int) {
    companion object {
        fun collect(ctx: Context): DeviceFingerprint {
            val am = ctx.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val mi = ActivityManager.MemoryInfo().also { am.getMemoryInfo(it) }
            return DeviceFingerprint(
                Build.MODEL ?: "unknown",
                mi.totalMem / (1024 * 1024),
                Runtime.getRuntime().availableProcessors()
            )
        }
    }
    fun headerValue() = "$model|$totalRamMb|$cpuCores"
}
