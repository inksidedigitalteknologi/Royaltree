package com.inkside.digital.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.auth.FirebaseAuth
import com.inkside.digital.data.database.AppDatabase
import com.inkside.digital.data.network.ApiClient
import com.inkside.digital.data.repository.AffiliateRepository
import com.inkside.digital.util.AppLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.withContext

/**
 * MiningWorker — background auto-claim untuk mining.
 *
 * Dijalankan oleh WorkManager setiap 15 menit.
 * Hanya claim kalau:
 * - User login (Firebase Auth)
 * - Miner aktif (minerActiveUntil > now)
 * - Unclaimed > threshold
 *
 * Kalau salah satu kondisi tidak terpenuhi, skip (Result.success).
 * Kalau gagal, retry (Result.retry).
 */
class MiningWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            // 1. Ambil userId dari Firebase Auth
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            if (userId.isNullOrEmpty()) {
                // Tidak login — skip
                return@withContext Result.success()
            }

            // 2. Ambil Room DB
            val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
            val db = AppDatabase.getDatabase(applicationContext, scope)
            val dao = db.appDao()
            val repository = AffiliateRepository(dao)
            repository.switchUser(userId)

            // 3. Cek miner aktif dari Room DB
            val room = dao.getGameRoomStateSync()
            if (room == null) {
                // Belum ada room state — skip
                return@withContext Result.success()
            }

            val now = System.currentTimeMillis()
            if (room.minerActiveUntil <= now) {
                // Miner mati — skip
                return@withContext Result.success()
            }

            // 4. SYNC dari backend dulu (unclaimed fresh)
            ApiClient.getGameState().onSuccess { json ->
                if (json.optBoolean("success", false)) {
                    repository.syncGameStateFromBackend(json)
                }
            }.onFailure { error ->
                AppLogger.w("MiningWorker", "⚠️ Sync gagal: ${error.message}")
                return@withContext Result.retry()
            }

            // 5. Baca room state fresh (setelah sync)
            val freshRoom = dao.getGameRoomStateSync()
            if (freshRoom == null) {
                return@withContext Result.success()
            }

            // 6. Cek cooldown (3 jam) — match backend
            val nowFresh = System.currentTimeMillis()
            val lastClaim = freshRoom.lastMiningClaimAt
            val cooldownMs = 3 * 60 * 60 * 1000L
            if (lastClaim > 0 && nowFresh - lastClaim < cooldownMs) {
                val remainingMin = (cooldownMs - (nowFresh - lastClaim)) / 60000
                AppLogger.d("MiningWorker", "⏸️ Cooldown $remainingMin menit lagi — skip")
                return@withContext Result.success()
            }

            // 7. Cek unclaimed (fresh)
            val unclaimed = freshRoom.unclaimedMiningPoints
            if (unclaimed < MIN_CLAIM_THRESHOLD) {
                // Unclaimed kecil — skip
                return@withContext Result.success()
            }

            // 5. Claim via repository
            val result = repository.claimGameMiningPoints()
            if (result.isSuccess) {
                AppLogger.d("MiningWorker", "✅ Auto-claim sukses: ${result.getOrNull()}")
                Result.success()
            } else {
                AppLogger.w("MiningWorker", "❌ Auto-claim gagal: ${result.exceptionOrNull()?.message}")
                Result.retry()
            }
        } catch (e: Exception) {
            AppLogger.e("MiningWorker", "❌ Exception: ${e.message}", e)
            Result.retry()
        }
    }

    companion object {
        const val WORK_NAME = "mining_auto_claim"
        const val MIN_CLAIM_THRESHOLD = 0.01
        const val INTERVAL_MINUTES = 180L  // 3 jam
    }
}
