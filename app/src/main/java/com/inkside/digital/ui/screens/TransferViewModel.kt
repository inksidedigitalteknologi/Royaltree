package com.inkside.digital.ui.screens

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.inkside.digital.data.network.ApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject

// ============================================================
// TransferViewModel — State + API untuk fitur Transfer RTP
// ============================================================
class TransferViewModel(application: Application) : AndroidViewModel(application) {

    // ============ STATE ============
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMsg = MutableStateFlow<String?>(null)
    val errorMsg: StateFlow<String?> = _errorMsg.asStateFlow()

    private val _searchResults = MutableStateFlow<List<TransferUser>>(emptyList())
    val searchResults: StateFlow<List<TransferUser>> = _searchResults.asStateFlow()

    private val _selectedUser = MutableStateFlow<TransferUser?>(null)
    val selectedUser: StateFlow<TransferUser?> = _selectedUser.asStateFlow()

    private val _history = MutableStateFlow<List<TransferHistoryItem>>(emptyList())
    val history: StateFlow<List<TransferHistoryItem>> = _history.asStateFlow()

    private val _userBalance = MutableStateFlow(0)
    val userBalance: StateFlow<Int> = _userBalance.asStateFlow()

    // ============ DATA CLASSES ============
    data class TransferUser(
        val uid: String,
        val name: String,
        val username: String,
        val referralCode: String,
        val tier: String,
        val avatar: String
    )

    data class TransferHistoryItem(
        val id: String,
        val type: String,        // SENT / RECEIVED
        val amount: Int,
        val counterpartyName: String,
        val note: String,
        val timestamp: Long
    )

    // ============ INIT ============
    init {
        loadHistory()
        loadBalance()
    }

    // ============ LOAD BALANCE ============
    private fun loadBalance() {
        viewModelScope.launch {
            ApiClient.getGameState()
                .onSuccess { json ->
                    if (json.optBoolean("success", false)) {
                        val data = json.optJSONObject("data") ?: return@onSuccess
                        val userData = data.optJSONObject("user") ?: return@onSuccess
                        _userBalance.value = userData.optInt("points", 0)
                    }
                }
        }
    }

    // ============ SEARCH USER ============
    fun searchUser(query: String) {
        if (query.length < 3) {
            _errorMsg.value = "Minimal 3 karakter"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMsg.value = null

            ApiClient.searchTransferUser(query)
                .onSuccess { json ->
                    if (json.optBoolean("success", false)) {
                        val data = json.optJSONObject("data") ?: return@onSuccess
                        val arr = data.optJSONArray("users") ?: return@onSuccess
                        val list = mutableListOf<TransferUser>()
                        for (i in 0 until arr.length()) {
                            val u = arr.optJSONObject(i) ?: continue
                            list.add(
                                TransferUser(
                                    uid = u.optString("uid", ""),
                                    name = u.optString("name", "Anonim"),
                                    username = u.optString("username", ""),
                                    referralCode = u.optString("referralCode", ""),
                                    tier = u.optString("tier", "FREE"),
                                    avatar = u.optString("avatar", "👤")
                                )
                            )
                        }
                        _searchResults.value = list
                        if (list.isEmpty()) {
                            _errorMsg.value = "User tidak ditemukan"
                        }
                    } else {
                        _errorMsg.value = json.optString("message", "Gagal cari user")
                    }
                }
                .onFailure { error ->
                    _errorMsg.value = error.message ?: "Network error"
                }

            _isLoading.value = false
        }
    }

    // ============ SELECT USER ============
    fun selectUser(user: TransferUser) {
        _selectedUser.value = user
    }

    // ============ SEND TRANSFER ============
    fun sendTransfer(
        recipientId: String,
        amount: Int,
        note: String,
        onResult: (Boolean, String) -> Unit
    ) {
        if (amount < 20) {
            _errorMsg.value = "Minimal 20 RTP"
            onResult(false, "Minimal 20 RTP")
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMsg.value = null

            ApiClient.sendTransfer(recipientId, amount, note)
                .onSuccess { json ->
                    if (json.optBoolean("success", false)) {
                        val message = json.optString("message", "Transfer berhasil")
                        val data = json.optJSONObject("data")
                        if (data != null) {
                            _userBalance.value = data.optInt("remainingBalance", _userBalance.value)
                        }
                        onResult(true, message)
                        _selectedUser.value = null
                        _searchResults.value = emptyList()
                    } else {
                        val message = json.optString("message", "Gagal transfer")
                        _errorMsg.value = message
                        onResult(false, message)
                    }
                }
                .onFailure { error ->
                    val message = error.message ?: "Network error"
                    _errorMsg.value = message
                    onResult(false, message)
                }

            _isLoading.value = false
        }
    }

    // ============ LOAD HISTORY ============
    fun loadHistory() {
        viewModelScope.launch {
            ApiClient.getTransferHistory()
                .onSuccess { json ->
                    if (json.optBoolean("success", false)) {
                        val data = json.optJSONObject("data") ?: return@onSuccess
                        val arr = data.optJSONArray("transfers") ?: return@onSuccess
                        val list = mutableListOf<TransferHistoryItem>()
                        for (i in 0 until arr.length()) {
                            val t = arr.optJSONObject(i) ?: continue
                            list.add(
                                TransferHistoryItem(
                                    id = t.optString("id", ""),
                                    type = t.optString("type", "SENT"),
                                    amount = t.optInt("amount", 0),
                                    counterpartyName = t.optString("counterpartyName", "Anonim"),
                                    note = t.optString("note", ""),
                                    timestamp = t.optLong("timestamp", 0L)
                                )
                            )
                        }
                        _history.value = list
                    }
                }
        }
    }

    // ============ CLEAR ERROR ============
    fun clearError() {
        _errorMsg.value = null
    }
}
