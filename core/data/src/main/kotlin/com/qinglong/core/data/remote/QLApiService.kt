package com.qinglong.core.data.remote

import com.qinglong.core.model.*
import retrofit2.http.*

interface QLApiService {

    @POST("api/user/login")
    suspend fun login(@Body request: LoginRequest): ApiResponse<LoginData>

    @PUT("api/user/two-factor/login")
    suspend fun loginTwoFactor(@Body request: TwoFactorRequest): ApiResponse<LoginData>

    @GET("api/system")
    suspend fun getSystemInfo(): ApiResponse<SystemInfo>

    @GET("api/crons")
    suspend fun getTasks(@Query("searchValue") search: String = ""): ApiResponse<List<TaskInfo>>

    @PUT("api/crons/run") suspend fun runTasks(@Body ids: List<String>): ApiResponse<Unit>
    @PUT("api/crons/stop") suspend fun stopTasks(@Body ids: List<String>): ApiResponse<Unit>
    @GET("api/crons/{id}/log") suspend fun getTaskLog(@Path("id") id: String): ApiResponse<String>

    @GET("api/scripts") suspend fun getScripts(): ApiResponse<List<ScriptFile>>
    @GET("api/scripts/{filename}") suspend fun getScriptContent(@Path("filename") f: String, @Query("path") p: String? = null): ApiResponse<String>

    @GET("api/envs") suspend fun getEnvs(@Query("searchValue") s: String = ""): ApiResponse<List<EnvInfo>>
    @POST("api/envs") suspend fun addEnv(@Body d: List<Map<String, String>>): ApiResponse<Unit>
    @PUT("api/envs") suspend fun updateEnv(@Body d: Map<String, String>): ApiResponse<Unit>
    @PUT("api/envs/enable") suspend fun enableEnvs(@Body ids: List<String>): ApiResponse<Unit>
    @PUT("api/envs/disable") suspend fun disableEnvs(@Body ids: List<String>): ApiResponse<Unit>
    @HTTP(method = "DELETE", path = "api/envs", hasBody = true) suspend fun deleteEnvs(@Body ids: List<String>): ApiResponse<Unit>

    @GET("api/configs/files") suspend fun getConfigFiles(): ApiResponse<List<ConfigFile>>
    @GET("api/configs/{name}") suspend fun getConfigContent(@Path("name") n: String): ApiResponse<String>
    @POST("api/configs/save") suspend fun saveConfig(@Body d: Map<String, String>): ApiResponse<Unit>

    @GET("api/dependencies") suspend fun getDependencies(@Query("type") t: String = ""): ApiResponse<List<DependencyInfo>>

    @GET("api/subscriptions") suspend fun getSubscriptions(@Query("searchValue") s: String = ""): ApiResponse<List<SubscribeInfo>>
    @PUT("api/subscriptions/run") suspend fun runSubscriptions(@Body ids: List<Int>): ApiResponse<Unit>
    @PUT("api/subscriptions/stop") suspend fun stopSubscriptions(@Body ids: List<Int>): ApiResponse<Unit>
}
