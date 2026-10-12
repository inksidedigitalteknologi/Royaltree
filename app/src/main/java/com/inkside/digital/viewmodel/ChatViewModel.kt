package com.inkside.digital.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.inkside.digital.data.network.ApiClient
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

data class ChatMessage(
    val id: String = "",
    val userId: String = "",
    val userName: String = "Anonymous",
    val message: String = "",
    val timestamp: Long = 0L,
    val isOwn: Boolean = false
)

class ChatViewModel(application: Application) : AndroidViewModel(application) {

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isSending = MutableStateFlow(false)
    val isSending: StateFlow<Boolean> = _isSending.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private var pollJob: Job? = null
    private var currentUserId: String = ""

    fun setCurrentUser(userId: String) {
        currentUserId = userId
    }

    fun clearError() {
        _errorMessage.value = null
    }

    // ==== START POLLING (setiap 10 detik) ====
    fun startPolling() {
        if (pollJob?.isActive == true) return
        pollJob = viewModelScope.launch {
            while (isActive) {
                fetchMessages()
                delay(10_000L)
            }
        }
    }

    fun stopPolling() {
        pollJob?.cancel()
        pollJob = null
    }

    // ==== FETCH MESSAGES ====
    fun fetchMessages() {
        viewModelScope.launch {
            try {
                if (_messages.value.isEmpty()) _isLoading.value = true
                val result = ApiClient.getChatMessages()
                result.onSuccess { json ->
                    if (json.optBoolean("success", false)) {
                        val arr = json.optJSONArray("messages") ?: JSONArray()
                        val list = mutableListOf<ChatMessage>()
                        for (i in 0 until arr.length()) {
                            val obj = arr.optJSONObject(i) ?: continue
                            val userId = obj.optString("userId", "")
                            list.add(
                                ChatMessage(
                                    id = obj.optString("id", ""),
                                    userId = userId,
                                    userName = obj.optString("userName", "Anonymous"),
                                    message = obj.optString("message", ""),
                                    timestamp = obj.optLong("timestamp", 0L),
                                    isOwn = userId == currentUserId
                                )
                            )
                        }
                        _messages.value = list
                        _errorMessage.value = null
                    } else {
                        _errorMessage.value = json.optString("message", "Gagal memuat chat")
                    }
                }.onFailure { e ->
                    _errorMessage.value = "Error: ${e.message}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // ==== SEND MESSAGE ====
    fun sendMessage(message: String) {
        if (message.trim().isEmpty()) return
        viewModelScope.launch {
            try {
                _isSending.value = true
                val result = ApiClient.sendChatMessage(message.trim())
                result.onSuccess { json ->
                    if (json.optBoolean("success", false)) {
                        // Fetch ulang untuk update
                        fetchMessages()
                    } else {
                        _errorMessage.value = json.optString("message", "Gagal kirim pesan")
                    }
                }.onFailure { e ->
                    _errorMessage.value = "Error: ${e.message}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
            } finally {
                _isSending.value = false
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopPolling()
    }
}
