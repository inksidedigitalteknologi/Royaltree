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
    version = 8,  // Bump: force destructive rebuild (schema mismatch from MIGRATION_6_7)
    exportSchema = false  // No-op, sudah ada migration manual
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appDao(): AppDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null


        // ============ MIGRATION 2 → 3 ============
        // UserEntity.points: Int -> Double
        // Tukar table users: CAST points Int -> REAL

        val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE game_miner_items ADD COLUMN powerHs INTEGER NOT NULL DEFAULT 0")
                db.execSQL("UPDATE game_miner_items SET powerHs = CAST(ROUND(powerGhs * 1e9) AS INTEGER)")
            }
        }

        val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // GameMinerItemEntity: +4 field
                db.execSQL("ALTER TABLE game_miner_items ADD COLUMN basePowerHs INTEGER NOT NULL DEFAULT 0")
                db.execSQL("UPDATE game_miner_items SET basePowerHs = powerHs WHERE basePowerHs = 0")
                db.execSQL("ALTER TABLE game_miner_items ADD COLUMN efficiency REAL NOT NULL DEFAULT 1.0")
                db.execSQL("ALTER TABLE game_miner_items ADD COLUMN currentPowerHs INTEGER NOT NULL DEFAULT 0")
                db.execSQL("UPDATE game_miner_items SET currentPowerHs = powerHs WHERE currentPowerHs = 0")
                db.execSQL("ALTER TABLE game_miner_items ADD COLUMN signature TEXT NOT NULL DEFAULT ''")

                // GameRoomStateEntity: +6 field
                db.execSQL("ALTER TABLE game_room_state ADD COLUMN weightedBreakdownJson TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE game_room_state ADD COLUMN pingMs INTEGER NOT NULL DEFAULT 100")
                db.execSQL("ALTER TABLE game_room_state ADD COLUMN deviceScore INTEGER NOT NULL DEFAULT 70")
                db.execSQL("ALTER TABLE game_room_state ADD COLUMN tempC REAL NOT NULL DEFAULT 35.0")
                db.execSQL("ALTER TABLE game_room_state ADD COLUMN batteryLevel INTEGER NOT NULL DEFAULT 85")
                db.execSQL("ALTER TABLE game_room_state ADD COLUMN isCharging INTEGER NOT NULL DEFAULT 0")
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS users_new (
                        id TEXT NOT NULL PRIMARY KEY,
                        name TEXT NOT NULL DEFAULT '',
                        email TEXT NOT NULL DEFAULT '',
                        phone TEXT NOT NULL DEFAULT '',
                        tier TEXT NOT NULL DEFAULT 'FREE',
                        balance REAL NOT NULL DEFAULT 0.0,
                        points REAL NOT NULL DEFAULT 0.0,
                        referralCode TEXT NOT NULL DEFAULT '',
                        referredCount INTEGER NOT NULL DEFAULT 0,
                        checkInStreak INTEGER NOT NULL DEFAULT 0,
                        lastCheckInDate TEXT NOT NULL DEFAULT '',
                        todaySteps INTEGER NOT NULL DEFAULT 0,
                        pendingBalance REAL NOT NULL DEFAULT 0.0,
                        totalPaidOut REAL NOT NULL DEFAULT 0.0,
                        dailyStepGoal INTEGER NOT NULL DEFAULT 5000,
                        unclaimedSteps INTEGER NOT NULL DEFAULT 0,
                        isLocationTrackingAllowed INTEGER NOT NULL DEFAULT 1,
                        latitude REAL NOT NULL DEFAULT 0.0,
                        longitude REAL NOT NULL DEFAULT 0.0,
                        locationCity TEXT NOT NULL DEFAULT '',
                        locationProvince TEXT NOT NULL DEFAULT '',
                        regionZone TEXT NOT NULL DEFAULT 'ID',
                        is2FAEnabled INTEGER NOT NULL DEFAULT 0,
                        twoFactorSecret TEXT NOT NULL DEFAULT '',
                        commissionRateMultiplier REAL NOT NULL DEFAULT 1.0,
                        convertedStepsToday INTEGER NOT NULL DEFAULT 0,
                        affiliateBalance REAL NOT NULL DEFAULT 0.0,
                        affiliateBalanceAvailable REAL NOT NULL DEFAULT 0.0,
                        affiliateBalancePending REAL NOT NULL DEFAULT 0.0,
                        affiliateTotalEarned REAL NOT NULL DEFAULT 0.0,
                        affiliateTotalWithdrawn REAL NOT NULL DEFAULT 0.0
                    )
                    """.trimIndent()
                )

                db.execSQL(
                    """
                    INSERT OR IGNORE INTO users_new
                    SELECT id, name, email, phone, tier, balance,
                           CAST(points AS REAL),
                           referralCode, referredCount, checkInStreak, lastCheckInDate,
                           todaySteps, pendingBalance, totalPaidOut, dailyStepGoal,
                           unclaimedSteps, isLocationTrackingAllowed, latitude, longitude,
                           locationCity, locationProvince, regionZone, is2FAEnabled,
                           twoFactorSecret, commissionRateMultiplier, convertedStepsToday,
                           affiliateBalance, affiliateBalanceAvailable, affiliateBalancePending,
                           affiliateTotalEarned, affiliateTotalWithdrawn
                    FROM users
                    """.trimIndent()
                )

                db.execSQL("DROP TABLE IF EXISTS users")
                db.execSQL("ALTER TABLE users_new RENAME TO users")
            }
        }

        // ============ MIGRATION 3 → 4 ============
        // - GameMinerItemEntity: pricePoints → tokenCost + tambah catalogId
        // - UserEntity: tambah minerTokens
        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // 1. game_miner_items — rename pricePoints → tokenCost
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS game_miner_items_new (
                        id TEXT NOT NULL PRIMARY KEY,
                        catalogId TEXT NOT NULL DEFAULT '',
                        name TEXT NOT NULL DEFAULT '',
                        tier TEXT NOT NULL DEFAULT 'COMMON',
                        iconEmoji TEXT NOT NULL DEFAULT '',
                        tokenCost INTEGER NOT NULL DEFAULT 0,
                        powerGhs REAL NOT NULL DEFAULT 0.0,
                        pointsPerMinute REAL NOT NULL DEFAULT 0.0,
                        isOwned INTEGER NOT NULL DEFAULT 0,
                        isPlacedInRoom INTEGER NOT NULL DEFAULT 0,
                        placedSlotIndex INTEGER NOT NULL DEFAULT -1,
                        description TEXT NOT NULL DEFAULT ''
                    )
                """)
                // Copy data — pricePoints → tokenCost, catalogId = id
                db.execSQL("""
                    INSERT INTO game_miner_items_new (id, catalogId, name, tier, iconEmoji, tokenCost, powerGhs, pointsPerMinute, isOwned, isPlacedInRoom, placedSlotIndex, description)
                    SELECT id, id, name, tier, iconEmoji, pricePoints, powerGhs, pointsPerMinute, isOwned, isPlacedInRoom, placedSlotIndex, description
                    FROM game_miner_items
                """)
                db.execSQL("DROP TABLE IF EXISTS game_miner_items")
                db.execSQL("ALTER TABLE game_miner_items_new RENAME TO game_miner_items")

                // 2. users — tambah minerTokens
                db.execSQL("ALTER TABLE users ADD COLUMN minerTokens INTEGER NOT NULL DEFAULT 0")
            }
        }

        // ============ MIGRATION 4 → 5 ============
        // UserEntity: tambah photoUrl (untuk Google profile picture)
        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // 1. Fix users table (tambah photoUrl)
                db.execSQL("ALTER TABLE users ADD COLUMN photoUrl TEXT")

                // 2. Fix game_miner_items table (pastikan skema sesuai Kotlin terbaru)
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS game_miner_items_new (
                        id TEXT NOT NULL PRIMARY KEY,
                        catalogId TEXT NOT NULL DEFAULT '',
                        name TEXT NOT NULL DEFAULT '',
                        tier TEXT NOT NULL DEFAULT 'COMMON',
                        iconEmoji TEXT NOT NULL DEFAULT '',
                        tokenCost INTEGER NOT NULL DEFAULT 0,
                        powerGhs REAL NOT NULL DEFAULT 0.0,
                        pointsPerMinute REAL NOT NULL DEFAULT 0.0,
                        isOwned INTEGER NOT NULL DEFAULT 0,
                        isPlacedInRoom INTEGER NOT NULL DEFAULT 0,
                        placedSlotIndex INTEGER NOT NULL DEFAULT -1,
                        description TEXT NOT NULL DEFAULT ''
                    )
                """)
                db.execSQL("""
                    INSERT INTO game_miner_items_new (id, catalogId, name, tier, iconEmoji, tokenCost, powerGhs, pointsPerMinute, isOwned, isPlacedInRoom, placedSlotIndex, description)
                    SELECT id, catalogId, name, tier, iconEmoji, tokenCost, powerGhs, pointsPerMinute, isOwned, isPlacedInRoom, placedSlotIndex, description
                    FROM game_miner_items
                """)
                db.execSQL("DROP TABLE IF EXISTS game_miner_items")
                db.execSQL("ALTER TABLE game_miner_items_new RENAME TO game_miner_items")
            }
        }

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "royaltree.db"
                )
                    .addMigrations(MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6, MIGRATION_6_7)  // MIGRATION PROPER
                    .addCallback(DatabaseCallback(scope))
                    .fallbackToDestructiveMigration(dropAllTables = true)  // ✅ Aktifkan — handle schema mismatch
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
