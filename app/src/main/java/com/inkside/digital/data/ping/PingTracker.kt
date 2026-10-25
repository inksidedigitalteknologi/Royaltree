package com.inkside.digital.data.ping

import com.inkside.digital.data.network.ApiClient
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object PingTracker {
    private val _lastRttMs = MutableStateFlow(100L)
    val lastRttMs: StateFlow<Long> = _lastRttMs
    private var job: Job? = null

    fun start(scope: CoroutineScope, intervalMs: Long = 30_000) {
        if (job?.isActive == true) return
        job = scope.launch {
            while (isActive) {
                val t0 = System.currentTimeMillis()
                runCatching { ApiClient.checkHealth() }
                    .onSuccess {
                        _lastRttMs.value = (System.currentTimeMillis() - t0).coerceIn(1, 5000)
                    }
                delay(intervalMs)
            }
        }
    }

    fun stop() { job?.cancel(); job = null }
}
