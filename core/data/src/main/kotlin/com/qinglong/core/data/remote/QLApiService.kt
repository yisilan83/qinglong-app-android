package com.qinglong.core.data.remote

import com.qinglong.core.model.ApiResponse
import com.qinglong.core.model.LoginData
import com.qinglong.core.model.LoginRequest
import com.qinglong.core.model.SystemInfo
import com.qinglong.core.model.TwoFactorRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT

interface QLApiService {

    @POST("api/user/login")
    suspend fun login(@Body request: LoginRequest): ApiResponse<LoginData>

    @PUT("api/user/two-factor/login")
    suspend fun loginTwoFactor(@Body request: TwoFactorRequest): ApiResponse<LoginData>

    @GET("api/system")
    suspend fun getSystemInfo(): ApiResponse<SystemInfo>
}
