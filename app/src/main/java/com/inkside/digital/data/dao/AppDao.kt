package com.inkside.digital.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.inkside.digital.data.model.AffiliateLinkEntity
import com.inkside.digital.data.model.AppDownloadAdEntity
import com.inkside.digital.data.model.CampaignEntity
import com.inkside.digital.data.model.InvestmentCouponEntity
import com.inkside.digital.data.model.NotificationEntity
import com.inkside.digital.data.model.SystemSettingsEntity
import com.inkside.digital.data.model.TaskMissionEntity
import com.inkside.digital.data.model.TransactionEntity
import com.inkside.digital.data.model.UserEntity
import com.inkside.digital.data.model.UserLocationLogEntity
import com.inkside.digital.data.model.WithdrawalEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    // User
    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    fun getUser(id: String = "user_001"): Flow<UserEntity?>

    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getUserSync(id: String = "user_001"): UserEntity?

    @Query("SELECT * FROM users")
    suspend fun getAllUsersSync(): List<UserEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Update
    suspend fun updateUser(user: UserEntity)

    // Campaigns
    @Query("SELECT * FROM campaigns ORDER BY totalEarned DESC")
    fun getAllCampaigns(): Flow<List<CampaignEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCampaigns(campaigns: List<CampaignEntity>)

    @Update
    suspend fun updateCampaign(campaign: CampaignEntity)

    // Affiliate Links
    @Query("SELECT * FROM affiliate_links ORDER BY createdAt DESC")
    fun getAllLinks(): Flow<List<AffiliateLinkEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLink(link: AffiliateLinkEntity)

    @Update
    suspend fun updateLink(link: AffiliateLinkEntity)

    // Withdrawals
    @Query("SELECT * FROM withdrawals ORDER BY requestedAt DESC")
    fun getAllWithdrawals(): Flow<List<WithdrawalEntity>>

    @Query("SELECT * FROM withdrawals WHERE status = 'PENDING' ORDER BY requestedAt ASC")
    fun getPendingWithdrawals(): Flow<List<WithdrawalEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWithdrawal(withdrawal: WithdrawalEntity)

    @Update
    suspend fun updateWithdrawal(withdrawal: WithdrawalEntity)

    // Transactions
    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity)

    // Investment Coupons
    @Query("SELECT * FROM investment_coupons")
    fun getAllInvestmentCoupons(): Flow<List<InvestmentCouponEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInvestmentCoupons(coupons: List<InvestmentCouponEntity>)

    @Update
    suspend fun updateInvestmentCoupon(coupon: InvestmentCouponEntity)

    // Notifications
    @Query("SELECT * FROM notifications ORDER BY timestamp DESC")
    fun getAllNotifications(): Flow<List<NotificationEntity>>

    @Query("SELECT COUNT(*) FROM notifications WHERE isRead = 0")
    fun getUnreadNotificationCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity)

    @Query("UPDATE notifications SET isRead = 1")
    suspend fun markAllNotificationsAsRead()

    // Task Missions
    @Query("SELECT * FROM task_missions ORDER BY isClaimed ASC, isCompleted DESC, rtpReward DESC")
    fun getAllMissions(): Flow<List<com.inkside.digital.data.model.TaskMissionEntity>>

    @Query("SELECT * FROM task_missions WHERE id = :id LIMIT 1")
    suspend fun getMissionById(id: String): com.inkside.digital.data.model.TaskMissionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMissions(missions: List<TaskMissionEntity>)

    @Update
    suspend fun updateMission(mission: TaskMissionEntity)

    // User Locations Tracking
    @Query("SELECT * FROM user_locations ORDER BY timestamp DESC")
    fun getAllLocationLogs(): Flow<List<UserLocationLogEntity>>

    @Query("SELECT * FROM user_locations ORDER BY timestamp DESC LIMIT 50")
    suspend fun getRecentLocationLogsSync(): List<UserLocationLogEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocationLog(log: UserLocationLogEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocationLogs(logs: List<UserLocationLogEntity>)

    // System Settings
    @Query("SELECT * FROM system_settings WHERE id = :id LIMIT 1")
    fun getSystemSettings(id: String = "default_config"): Flow<SystemSettingsEntity?>

    @Query("SELECT * FROM system_settings WHERE id = :id LIMIT 1")
    suspend fun getSystemSettingsSync(id: String = "default_config"): SystemSettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSystemSettings(settings: SystemSettingsEntity)

    @Update
    suspend fun updateSystemSettings(settings: SystemSettingsEntity)

    // App Download Ads (Offers)
    @Query("SELECT * FROM app_download_ads ORDER BY isRewardClaimed ASC, rewardCoins DESC")
    fun getAllAppDownloadAds(): Flow<List<AppDownloadAdEntity>>

    @Query("SELECT * FROM app_download_ads WHERE id = :id LIMIT 1")
    suspend fun getAppDownloadAdById(id: String): AppDownloadAdEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAppDownloadAds(ads: List<AppDownloadAdEntity>)

    @Update
    suspend fun updateAppDownloadAd(ad: AppDownloadAdEntity)

    // Game Miner Items (RollerCoin-style items)
    @Query("SELECT * FROM game_miner_items")
    fun getAllGameMinerItems(): Flow<List<com.inkside.digital.data.model.GameMinerItemEntity>>

    @Query("SELECT * FROM game_miner_items")
    suspend fun getAllGameMinerItemsSync(): List<com.inkside.digital.data.model.GameMinerItemEntity>

    @Query("SELECT * FROM game_miner_items WHERE isPlacedInRoom = 1 ORDER BY placedSlotIndex ASC")
    fun getPlacedMinerItems(): Flow<List<com.inkside.digital.data.model.GameMinerItemEntity>>

    @Query("SELECT * FROM game_miner_items WHERE isPlacedInRoom = 1 ORDER BY placedSlotIndex ASC")
    suspend fun getPlacedMinerItemsSync(): List<com.inkside.digital.data.model.GameMinerItemEntity>

    @Query("SELECT * FROM game_miner_items WHERE id = :id LIMIT 1")
    suspend fun getGameMinerItemById(id: String): com.inkside.digital.data.model.GameMinerItemEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGameMinerItems(items: List<com.inkside.digital.data.model.GameMinerItemEntity>)

    @Update
    suspend fun updateGameMinerItem(item: com.inkside.digital.data.model.GameMinerItemEntity)

    // Game Room State
    @Query("SELECT * FROM game_room_state WHERE id = :id LIMIT 1")
    fun getGameRoomState(id: String = "default_room"): Flow<com.inkside.digital.data.model.GameRoomStateEntity?>

    @Query("SELECT * FROM game_room_state WHERE id = :id LIMIT 1")
    suspend fun getGameRoomStateSync(id: String = "default_room"): com.inkside.digital.data.model.GameRoomStateEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGameRoomState(state: com.inkside.digital.data.model.GameRoomStateEntity)

    @Update
    suspend fun updateGameRoomState(state: com.inkside.digital.data.model.GameRoomStateEntity)
}
