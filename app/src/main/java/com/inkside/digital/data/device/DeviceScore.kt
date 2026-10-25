package com.inkside.digital.data.device

object DeviceScore {
    fun compute(fp: DeviceFingerprint): Int {
        val ramScore = when {
            fp.totalRamMb >= 8192 -> 40
            fp.totalRamMb >= 6144 -> 32
            fp.totalRamMb >= 4096 -> 24
            fp.totalRamMb >= 3072 -> 16
            fp.totalRamMb >= 2048 -> 8
            else -> 0
        }
        val cpuScore = fp.cpuCores.coerceIn(1, 8) * 5
        val modelScore = if (fp.model.lowercase().contains("vivo")) 10 else 20
        return (ramScore + cpuScore + modelScore).coerceIn(0, 100)
    }
}
