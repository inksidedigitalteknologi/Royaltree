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
// SweepstakesViewModel — State + API untuk fitur Sweepstakes
// ============================================================
class SweepstakesViewModel(application: Application) : AndroidViewModel(application) {

    // ============ STATE ============
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMsg = MutableStateFlow<String?>(null)
    val errorMsg: StateFlow<String?> = _errorMsg.asStateFlow()

    private val _sweepstakesId = MutableStateFlow<String?>(null)
    val sweepstakesId: StateFlow<String?> = _sweepstakesId.asStateFlow()

    private val _endTime = MutableStateFlow(0L)
    val endTime: StateFlow<Long> = _endTime.asStateFlow()

    private val _ticketPrice = MutableStateFlow(50)
    val ticketPrice: StateFlow<Int> = _ticketPrice.asStateFlow()

    private val _totalTickets = MutableStateFlow(0)
    val totalTickets: StateFlow<Int> = _totalTickets.asStateFlow()

    private val _totalPool = MutableStateFlow(0)
    val totalPool: StateFlow<Int> = _totalPool.asStateFlow()

    private val _userTickets = MutableStateFlow(0)
    val userTickets: StateFlow<Int> = _userTickets.asStateFlow()

    private val _userChance = MutableStateFlow("0.0000%")
    val userChance: StateFlow<String> = _userChance.asStateFlow()

    private val _minTickets = MutableStateFlow(6624)
    val minTickets: StateFlow<Int> = _minTickets.asStateFlow()

    private val _maxTicketsPerUser = MutableStateFlow(200)
    val maxTicketsPerUser: StateFlow<Int> = _maxTicketsPerUser.asStateFlow()

    private val _topHolders = MutableStateFlow<List<TopHolder>>(emptyList())
    val topHolders: StateFlow<List<TopHolder>> = _topHolders.asStateFlow()

    private val _rewards = MutableStateFlow<List<RewardTier>>(emptyList())
    val rewards: StateFlow<List<RewardTier>> = _rewards.asStateFlow()

    private val _history = MutableStateFlow<List<HistoryItem>>(emptyList())
    val history: StateFlow<List<HistoryItem>> = _history.asStateFlow()

    // ============ DATA CLASSES ============
    data class TopHolder(val uid: String, val name: String, val tickets: Int)
    data class RewardTier(val rank: Int, val count: Int, val amount: Int)
    data class TopWinner(
        val rank: Int,
        val uid: String,
        val name: String,
        val amount: Int,
        val ticketNumber: String? = null
    )
    data class HistoryItem(
        val id: String,
        val endTime: Long,
        val totalTickets: Int,
        val totalPool: Int,
        val winnerCount: Int,
        val userWon: Boolean,
        val topWinners: List<TopWinner> = emptyList()
    )

    // ============ INIT ============
    init {
        loadCurrent()
        loadHistory()
    }

    // ============ LOAD CURRENT ============
    fun loadCurrent() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMsg.value = null

            ApiClient.getSweepstakesCurrent()
                .onSuccess { json ->
                    if (json.optBoolean("success", false)) {
                        val data = json.optJSONObject("data") ?: return@onSuccess
                        parseCurrent(data)
                    } else {
                        _errorMsg.value = json.optString("message", "Gagal memuat")
                    }
                }
                .onFailure { error ->
                    _errorMsg.value = error.message ?: "Network error"
                }

            _isLoading.value = false
        }
    }

    private fun parseCurrent(data: JSONObject) {
        _sweepstakesId.value = data.optString("id", "")
        _endTime.value = data.optLong("endTime", 0L)
        _ticketPrice.value = data.optInt("ticketPrice", 50)
        _totalTickets.value = data.optInt("totalTickets", 0)
        _totalPool.value = data.optInt("totalPool", 0)
        _userTickets.value = data.optInt("userTickets", 0)
        _userChance.value = data.optString("userChance", "0.0000%")
        _minTickets.value = data.optInt("minTickets", 6624)
        _maxTicketsPerUser.value = data.optInt("maxTicketsPerUser", 200)

        // Rewards
        val rewardsArr = data.optJSONArray("rewards") ?: return
        val rewardList = mutableListOf<RewardTier>()
        for (i in 0 until rewardsArr.length()) {
            val r = rewardsArr.optJSONObject(i) ?: continue
            rewardList.add(
                RewardTier(
                    rank = r.optInt("rank", 1),
                    count = r.optInt("count", 1),
                    amount = r.optInt("amount", 0)
                )
            )
        }
        _rewards.value = rewardList

        // Top holders
        val topArr = data.optJSONArray("topHolders") ?: return
        val holderList = mutableListOf<TopHolder>()
        for (i in 0 until topArr.length()) {
            val h = topArr.optJSONObject(i) ?: continue
            holderList.add(
                TopHolder(
                    uid = h.optString("uid", ""),
                    name = h.optString("name", "Anonim"),
                    tickets = h.optInt("tickets", 0)
                )
            )
        }
        _topHolders.value = holderList
    }

    // ============ LOAD HISTORY ============
    fun loadHistory() {
        viewModelScope.launch {
            ApiClient.getSweepstakesHistory()
                .onSuccess { json ->
                    if (json.optBoolean("success", false)) {
                        val arr = json.optJSONArray("data") ?: return@onSuccess
                        val list = mutableListOf<HistoryItem>()
                        for (i in 0 until arr.length()) {
                            val h = arr.optJSONObject(i) ?: continue

                            // Parse topWinners (M49)
                            val topWinners = mutableListOf<TopWinner>()
                            val twArr = h.optJSONArray("topWinners")
                            if (twArr != null) {
                                for (j in 0 until twArr.length()) {
                                    val w = twArr.optJSONObject(j) ?: continue
                                    topWinners.add(
                                        TopWinner(
                                            rank = w.optInt("rank", 0),
                                            uid = w.optString("uid", ""),
                                            name = w.optString("name", "Anonim"),
                                            amount = w.optInt("amount", 0),
                                            ticketNumber = if (w.has("ticketNumber")) w.optString("ticketNumber") else null
                                        )
                                    )
                                }
                            }

                            list.add(
                                HistoryItem(
                                    id = h.optString("id", ""),
                                    endTime = h.optLong("endTime", 0L),
                                    totalTickets = h.optInt("totalTickets", 0),
                                    totalPool = h.optInt("totalPool", 0),
                                    winnerCount = h.optInt("winnerCount", 0),
                                    userWon = h.optJSONObject("userWon") != null,
                                    topWinners = topWinners
                                )
                            )
                        }
                        _history.value = list
                    }
                }
        }
    }

    // ============ BUY TICKETS ============
    fun buyTickets(quantity: Int, onSuccess: (Int, Int) -> Unit = { _, _ -> }) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMsg.value = null

            ApiClient.buySweepstakesTickets(quantity)
                .onSuccess { json ->
                    if (json.optBoolean("success", false)) {
                        val data = json.optJSONObject("data") ?: return@onSuccess
                        val totalTickets = data.optInt("totalTickets", quantity)
                        val bonus = data.optInt("bonus", 0)
                        onSuccess(totalTickets, bonus)
                        // Refresh
                        loadCurrent()
                    } else {
                        _errorMsg.value = json.optString("message", "Gagal beli kupon")
                    }
                }
                .onFailure { error ->
                    _errorMsg.value = error.message ?: "Network error"
                }

            _isLoading.value = false
        }
    }

    // ============ CLEAR ERROR ============
    fun clearError() {
        _errorMsg.value = null
    }
}
