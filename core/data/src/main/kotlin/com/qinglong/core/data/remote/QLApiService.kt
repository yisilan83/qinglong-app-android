package com.qinglong.core.data.remote

import com.qinglong.core.model.ApiResponse
import com.qinglong.core.model.LoginData
import com.qinglong.core.model.LoginRequest
import com.qinglong.core.model.ScriptFile
import com.qinglong.core.model.SystemInfo
import com.qinglong.core.model.TaskInfo
import com.qinglong.core.model.TwoFactorRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface QLApiService {

    @POST("api/user/login")
    suspend fun login(@Body request: LoginRequest): ApiResponse<LoginData>

    @PUT("api/user/two-factor/login")
    suspend fun loginTwoFactor(@Body request: TwoFactorRequest): ApiResponse<LoginData>

    @GET("api/system")
    suspend fun getSystemInfo(): ApiResponse<SystemInfo>

    @GET("api/crons")
    suspend fun getTasks(@Query("searchValue") search: String = ""): ApiResponse<List<TaskInfo>>

    @PUT("api/crons/run")
    suspend fun runTasks(@Body ids: List<String>): ApiResponse<Unit>

    @PUT("api/crons/stop")
    suspend fun stopTasks(@Body ids: List<String>): ApiResponse<Unit>

    @GET("api/crons/{id}/log")
    suspend fun getTaskLog(@Path("id") id: String): ApiResponse<String>

    @GET("api/scripts")
    suspend fun getScripts(): ApiResponse<List<ScriptFile>>

    @GET("api/scripts/{filename}")
    suspend fun getScriptContent(
        @Path("filename") filename: String,
        @Query("path") path: String? = null
    ): ApiResponse<String>
}
