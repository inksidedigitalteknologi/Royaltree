package com.example.data.network

import com.example.data.network.model.ApiConfigResponse
import com.example.data.network.model.ApiGenericResponse
import com.example.data.network.model.ApiHealthResponse
import com.example.data.network.model.ApiUserSyncRequest
import com.example.data.network.model.ApiUserSyncResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

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

    @PUT("withdrawals/{id}/status")
    suspend fun updateWithdrawalStatus(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Body body: Map<String, String>
    ): ApiGenericResponse
}
