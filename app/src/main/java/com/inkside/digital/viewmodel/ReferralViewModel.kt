package com.inkside.digital.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inkside.digital.data.network.ApiClient
import com.inkside.digital.data.network.model.ReferralItem
import com.inkside.digital.data.network.model.ReferralStats
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ReferralUiState(
    val loadingStats: Boolean = false,
    val loadingList: Boolean = false,
    val refreshing: Boolean = false,
    val stats: ReferralStats? = null,
    val items: List<ReferralItem> = emptyList(),
    val page: Int = 1,
    val hasMore: Boolean = false,
    val error: String? = null
)

class ReferralViewModel : ViewModel() {

    private val _state = MutableStateFlow(ReferralUiState())
    val state: StateFlow<ReferralUiState> = _state.asStateFlow()

    private val apiService = ApiClient.apiService

    private fun authHeader(): String? {
        val token = FirebaseAuth.getInstance().currentUser?.getIdToken(false)?.result?.token
        return token?.let { "Bearer $it" }
    }

    fun loadAll() {
        loadStats()
        loadList(reset = true)
    }

    fun loadStats() {
        val token = authHeader() ?: run {
            _state.value = _state.value.copy(error = "Belum login")
            return
        }
        _state.value = _state.value.copy(loadingStats = true, error = null)
        viewModelScope.launch {
            try {
                val res = apiService.getReferralStats(token)
                if (res.success && res.data != null) {
                    _state.value = _state.value.copy(loadingStats = false, stats = res.data)
                } else {
                    _state.value = _state.value.copy(loadingStats = false, error = res.message ?: "Gagal ambil stats")
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(loadingStats = false, error = e.message ?: "Network error")
            }
        }
    }

    fun loadList(reset: Boolean = false) {
        val token = authHeader() ?: return
        val currentPage = if (reset) 1 else _state.value.page
        _state.value = _state.value.copy(loadingList = true, refreshing = reset && _state.value.items.isNotEmpty())
        viewModelScope.launch {
            try {
                val res = apiService.getReferralList(token, page = currentPage, limit = 20)
                if (res.success && res.data != null) {
                    val newItems = if (reset) res.data.items else _state.value.items + res.data.items
                    _state.value = _state.value.copy(
                        loadingList = false, refreshing = false,
                        items = newItems, page = currentPage + 1, hasMore = res.data.hasMore
                    )
                } else {
                    _state.value = _state.value.copy(loadingList = false, refreshing = false, error = res.message ?: "Gagal ambil list")
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(loadingList = false, refreshing = false, error = e.message ?: "Network error")
            }
        }
    }

    fun refresh() {
        _state.value = _state.value.copy(page = 1, hasMore = false)
        loadStats()
        loadList(reset = true)
    }

    fun loadMore() {
        if (!_state.value.loadingList && _state.value.hasMore) loadList(reset = false)
    }

    fun clearError() { _state.value = _state.value.copy(error = null) }
}
