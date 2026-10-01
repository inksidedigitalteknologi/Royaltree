package com.inkside.digital.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.room.migration.Migration
import com.inkside.digital.data.dao.AppDao
import com.inkside.digital.data.model.AffiliateLinkEntity
import com.inkside.digital.data.model.AppDownloadAdEntity
import com.inkside.digital.data.model.CampaignEntity
import com.inkside.digital.data.model.GameMinerItemEntity
import com.inkside.digital.data.model.GameRoomStateEntity
import com.inkside.digital.data.model.InvestmentCouponEntity
import com.inkside.digital.data.model.NotificationEntity
import com.inkside.digital.data.model.SystemSettingsEntity
import com.inkside.digital.data.model.TaskMissionEntity
import com.inkside.digital.data.model.TransactionEntity
import com.inkside.digital.data.model.UserEntity
import com.inkside.digital.data.model.UserLocationLogEntity
import com.inkside.digital.data.model.WithdrawalEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserEntity::class,
        CampaignEntity::class,
        AffiliateLinkEntity::class,
        WithdrawalEntity::class,
        TransactionEntity::class,
        InvestmentCouponEntity::class,
        NotificationEntity::class,
        TaskMissionEntity::class,
        UserLocationLogEntity::class,
        SystemSettingsEntity::class,
        AppDownloadAdEntity::class,
        GameMinerItemEntity::class,
        GameRoomStateEntity::class
    ],
    version = 8,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appDao(): AppDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Migration 5 -> 6: schema tidak berubah, tapi kita kekalkan
        // supaya user lama TIDAK kehilangan data (sebelum ini fallback
        // destructive akan drop semua table).
        // Migration 7 -> 8: tambah lastMiningClaimAt + lastMiningUpdateTimestamp
        private val MIGRATION_7_8 = object : Migration(7, 8) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE game_room_state ADD COLUMN lastMiningClaimAt INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE game_room_state ADD COLUMN lastMiningUpdateTimestamp INTEGER NOT NULL DEFAULT 0")
            }
        }

        // Migration 6 -> 7: tambah 5 field affiliate ke table users
        private val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE users ADD COLUMN affiliateBalance REAL NOT NULL DEFAULT 0.0")
                db.execSQL("ALTER TABLE users ADD COLUMN affiliateBalanceAvailable REAL NOT NULL DEFAULT 0.0")
                db.execSQL("ALTER TABLE users ADD COLUMN affiliateBalancePending REAL NOT NULL DEFAULT 0.0")
                db.execSQL("ALTER TABLE users ADD COLUMN affiliateTotalEarned REAL NOT NULL DEFAULT 0.0")
                db.execSQL("ALTER TABLE users ADD COLUMN affiliateTotalWithdrawn REAL NOT NULL DEFAULT 0.0")
            }
        }

        private val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Schema v5 -> v6:
                // - lastCheckInDate: INTEGER -> TEXT
                // - tambah referredCount (sudah ada di v5)
                // - buang lastLocationUpdate, lastStepTimestamp, encryptionKeyHash, referralEarnings

                // 1. Rename table lama
                db.execSQL("ALTER TABLE users RENAME TO users_old")

                // 2. Buat table baru (schema v6)
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `users` (
                        `id` TEXT NOT NULL,
                        `name` TEXT NOT NULL,
                        `email` TEXT NOT NULL,
                        `phone` TEXT NOT NULL,
                        `tier` TEXT NOT NULL,
                        `balance` REAL NOT NULL,
                        `points` INTEGER NOT NULL,
                        `referralCode` TEXT NOT NULL,
                        `referredCount` INTEGER NOT NULL,
                        `checkInStreak` INTEGER NOT NULL,
                        `lastCheckInDate` TEXT NOT NULL,
                        `todaySteps` INTEGER NOT NULL,
                        `pendingBalance` REAL NOT NULL,
                        `totalPaidOut` REAL NOT NULL,
                        `unclaimedSteps` INTEGER NOT NULL,
                        `dailyStepGoal` INTEGER NOT NULL,
                        `isLocationTrackingAllowed` INTEGER NOT NULL,
                        `latitude` REAL NOT NULL,
                        `longitude` REAL NOT NULL,
                        `locationCity` TEXT NOT NULL,
                        `locationProvince` TEXT NOT NULL,
                        `regionZone` TEXT NOT NULL,
                        `is2FAEnabled` INTEGER NOT NULL,
                        `twoFactorSecret` TEXT NOT NULL,
                        `commissionRateMultiplier` REAL NOT NULL,
                        `convertedStepsToday` INTEGER NOT NULL,
                        PRIMARY KEY(`id`)
                    )
                """.trimIndent())

                // 3. Copy data lama -> baru (cast lastCheckInDate ke TEXT, default kolum baru)
                db.execSQL("""
                    INSERT INTO users (
                        id, name, email, phone, tier, balance, points, referralCode,
                        referredCount, checkInStreak, lastCheckInDate, todaySteps,
                        pendingBalance, totalPaidOut, unclaimedSteps, dailyStepGoal,
                        isLocationTrackingAllowed, latitude, longitude,
                        locationCity, locationProvince, regionZone,
                        is2FAEnabled, twoFactorSecret, commissionRateMultiplier,
                        convertedStepsToday
                    )
                    SELECT
                        id, name, email, phone, tier, balance, points, referralCode,
                        0,
                        checkInStreak,
                        CAST(COALESCE(lastCheckInDate, '') AS TEXT),
                        todaySteps,
                        pendingBalance, totalPaidOut, unclaimedSteps, dailyStepGoal,
                        isLocationTrackingAllowed, latitude, longitude,
                        locationCity, locationProvince, regionZone,
                        is2FAEnabled, twoFactorSecret, commissionRateMultiplier,
                        convertedStepsToday
                    FROM users_old
                """.trimIndent())

                // 4. Drop table lama
                db.execSQL("DROP TABLE users_old")
            }
        }

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "royaltree.db"
                )
                    .addMigrations(MIGRATION_5_6, MIGRATION_6_7, MIGRATION_7_8)
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        // No-op — semua data di-load dari backend (Firestore).
        // Tidak ada data dummy di Room DB.
    }
}
