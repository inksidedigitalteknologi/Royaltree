package com.inkside.digital.data.network

import com.inkside.digital.data.network.model.ApiConfigResponse
import com.inkside.digital.data.network.model.ApiGenericResponse
import com.inkside.digital.data.network.model.ApiHealthResponse
import com.inkside.digital.data.network.model.ApiUserSyncRequest
import com.inkside.digital.data.network.model.ApiUserSyncResponse
import com.inkside.digital.data.network.model.StepSyncRequest
import com.inkside.digital.data.network.model.ApiHistoryResponse
import com.inkside.digital.data.network.model.ApiNotificationListResponse
import com.inkside.digital.data.network.model.ApiUnreadResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import com.inkside.digital.data.network.model.ApiAnalyticsSummaryResponse
import com.inkside.digital.data.network.model.SpinRequest
import com.inkside.digital.data.network.model.ApiSpinResponse
import com.inkside.digital.data.network.model.ApiProfileResponse
import com.inkside.digital.data.network.model.ApiWithdrawalListResponse
import com.inkside.digital.data.network.model.ApiWithdrawalRequest
import com.inkside.digital.data.network.model.ApiCampaignListResponse
import com.inkside.digital.data.network.model.ApiMissionListResponse
import okhttp3.RequestBody
import okhttp3.ResponseBody

interface RoyaltreeApiService {

    @GET("health")
    suspend fun checkHealth(): ApiHealthResponse

    @GET("config")
    suspend fun getSystemConfig(): ApiConfigResponse

    @POST("users/sync")
    suspend fun syncUser(
        @Header("Authorization") token: String,
        @Body request: ApiUserSyncRequest
    ): ApiUserSyncResponse

    @POST("steps/sync")
    suspend fun syncSteps(
        @Header("Authorization") token: String,
        @Body request: StepSyncRequest
    ): ApiGenericResponse

    @PUT("withdrawals/{id}/status")
    suspend fun updateWithdrawalStatus(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Body body: Map<String, String>
    ): ApiGenericResponse

    @GET("history/{userId}")
    suspend fun getHistory(
        @Header("Authorization") token: String,
        @Path("userId") userId: String
    ): ApiHistoryResponse

    @GET("notifications/{userId}")
    suspend fun getNotifications(
        @Header("Authorization") token: String,
        @Path("userId") userId: String
    ): ApiNotificationListResponse

    @GET("notifications/{userId}/unread")
    suspend fun getUnreadCount(
        @Header("Authorization") token: String,
        @Path("userId") userId: String
    ): ApiUnreadResponse

    // Analytics
    @GET("analytics/{userId}/summary")
    suspend fun getAnalyticsSummary(
        @Header("Authorization") token: String,
        @Path("userId") userId: String
    ): ApiAnalyticsSummaryResponse

    // Games
    @POST("games/spin")
    suspend fun spinWheel(
        @Header("Authorization") token: String,
        @Body request: SpinRequest
    ): ApiSpinResponse

    // Profile
    @GET("profile/{userId}")
    suspend fun getProfile(
        @Header("Authorization") token: String,
        @Path("userId") userId: String
    ): ApiProfileResponse

    @PUT("profile/{userId}/update")
    suspend fun updateProfile(
        @Header("Authorization") token: String,
        @Path("userId") userId: String,
        @Body body: Map<String, String>
    ): ApiGenericResponse

    @PUT("profile/{userId}/change-pin")
    suspend fun changePin(
        @Header("Authorization") token: String,
        @Path("userId") userId: String,
        @Body body: Map<String, String>
    ): ApiGenericResponse

    // Withdrawals
    @GET("withdrawals")
    suspend fun getWithdrawals(
        @Header("Authorization") token: String
    ): ApiWithdrawalListResponse

    @POST("withdrawals")
    suspend fun createWithdrawal(
        @Header("Authorization") token: String,
        @Body body: ApiWithdrawalRequest
    ): ApiGenericResponse

    @GET("campaigns")
    suspend fun getCampaigns(
        @Header("Authorization") token: String
    ): ApiCampaignListResponse

    @GET("missions")
    suspend fun getMissions(
        @Header("Authorization") token: String
    ): ApiMissionListResponse

    // ============ AUTH (Firebase) ============
    @POST("auth/sync")
    suspend fun syncFirebaseUser(
        @Body body: RequestBody
    ): ResponseBody

    @GET("auth/me")
    suspend fun getMyProfile(): ResponseBody

    @POST("auth/link-referral")
    suspend fun linkReferral(
        @Body body: RequestBody
    ): ResponseBody

    // ============ DAILY CHECK-IN ============
    @GET("daily/status")
    suspend fun getDailyStatus(): ResponseBody

    @POST("daily/check-in")
    suspend fun dailyCheckIn(): ResponseBody

    @POST("daily/recover-day")
    suspend fun recoverDay(@Body body: RequestBody): ResponseBody

    // ============ ADS ============
    @GET("ads/status")
    suspend fun getAdStatus(): ResponseBody

    @POST("ads/reward")
    suspend fun rewardAd(@Body body: RequestBody): ResponseBody

    // ============ MISSIONS ============
    @POST("missions/{id}/complete")
    suspend fun completeMission(
        @Path("id") id: String,
        @Body body: RequestBody
    ): ResponseBody

    @POST("missions/{id}/claim")
    suspend fun claimMission(
        @Path("id") id: String,
        @Body body: RequestBody
    ): ResponseBody
}


