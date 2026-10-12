package com.inkside.digital.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.auth.FirebaseAuth
import com.inkside.digital.data.database.AppDatabase
import com.inkside.digital.data.repository.AffiliateRepository
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

            // 4. Cek unclaimed
            val unclaimed = room.unclaimedMiningPoints
            if (unclaimed < MIN_CLAIM_THRESHOLD) {
                // Unclaimed kecil — skip
                return@withContext Result.success()
            }

            // 5. Claim via repository
            val result = repository.claimGameMiningPoints()
            if (result.isSuccess) {
                android.util.Log.d("MiningWorker", "✅ Auto-claim sukses: ${result.getOrNull()}")
                Result.success()
            } else {
                android.util.Log.w("MiningWorker", "❌ Auto-claim gagal: ${result.exceptionOrNull()?.message}")
                Result.retry()
            }
        } catch (e: Exception) {
            android.util.Log.e("MiningWorker", "❌ Exception: ${e.message}", e)
            Result.retry()
        }
    }

    companion object {
        const val WORK_NAME = "mining_auto_claim"
        const val MIN_CLAIM_THRESHOLD = 0.01
        const val INTERVAL_MINUTES = 15L
    }
}
