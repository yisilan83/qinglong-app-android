package com.qinglong.core.data.remote

import com.qinglong.core.model.*
import okhttp3.RequestBody
import retrofit2.http.*

interface QLApiService {

    // ── Auth ──
    @POST("api/user/login")
    suspend fun login(@Body request: LoginRequest): ApiResponse<LoginData>

    @POST("api/user/logout")
    suspend fun logout(): ApiResponse<Unit>

    @PUT("api/user/two-factor/login")
    suspend fun loginTwoFactor(@Body request: TwoFactorRequest): ApiResponse<LoginData>

    // ── Health ──
    @GET("api/health")
    suspend fun healthCheck(): ApiResponse<Unit>

    // ── System ──
    @GET("api/system")
    suspend fun getSystemInfo(): ApiResponse<SystemInfo>

    @GET("api/system/config")
    suspend fun getSystemConfig(): ApiResponse<SystemConfigData>

    @PUT("api/system/config/log-remove-frequency")
    suspend fun updateLogRemoveFrequency(@Body body: Map<String, Int>): ApiResponse<Unit>

    @PUT("api/system/config/cron-concurrency")
    suspend fun updateCronConcurrency(@Body body: Map<String, Int>): ApiResponse<Unit>

    @GET("api/user/login-log")
    suspend fun getLoginLogs(): ApiResponse<List<LoginLogEntry>>

    @PUT("api/user")
    suspend fun updateAccount(@Body body: Map<String, String>): ApiResponse<Unit>

    // ── Tasks ──
    @GET("api/crons")
    suspend fun getTasks(
        @Query("searchValue") search: String = "",
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 50
    ): ApiResponse<TaskListData>

    @POST("api/crons")
    suspend fun addTask(@Body body: Map<String, String>): ApiResponse<Unit>

    @PUT("api/crons")
    suspend fun updateTask(@Body body: Map<String, String>): ApiResponse<Unit>

    @HTTP(method = "DELETE", path = "api/crons", hasBody = true)
    suspend fun deleteTasks(@Body ids: List<String>): ApiResponse<Unit>

    @PUT("api/crons/run")
    suspend fun runTasks(@Body ids: List<String>): ApiResponse<Unit>

    @PUT("api/crons/stop")
    suspend fun stopTasks(@Body ids: List<String>): ApiResponse<Unit>

    @PUT("api/crons/enable")
    suspend fun enableTasks(@Body ids: List<String>): ApiResponse<Unit>

    @PUT("api/crons/disable")
    suspend fun disableTasks(@Body ids: List<String>): ApiResponse<Unit>

    @PUT("api/crons/pin")
    suspend fun pinTasks(@Body ids: List<String>): ApiResponse<Unit>

    @PUT("api/crons/unpin")
    suspend fun unpinTasks(@Body ids: List<String>): ApiResponse<Unit>

    @GET("api/crons/{id}/log")
    suspend fun getTaskLog(@Path("id") id: String): ApiResponse<String>

    // ── Environments ──
    @GET("api/envs")
    suspend fun getEnvs(
        @Query("searchValue") search: String = ""
    ): ApiResponse<List<EnvInfo>>

    @POST("api/envs")
    suspend fun addEnvs(@Body body: List<EnvCreateRequest>): ApiResponse<Unit>

    @PUT("api/envs")
    suspend fun updateEnv(@Body body: EnvUpdateRequest): ApiResponse<Unit>

    @HTTP(method = "DELETE", path = "api/envs", hasBody = true)
    suspend fun deleteEnvs(@Body ids: List<String>): ApiResponse<Unit>

    @PUT("api/envs/enable")
    suspend fun enableEnvs(@Body ids: List<String>): ApiResponse<Unit>

    @PUT("api/envs/disable")
    suspend fun disableEnvs(@Body ids: List<String>): ApiResponse<Unit>

    // ── Scripts ──
    @GET("api/scripts")
    suspend fun getScripts(): ApiResponse<List<ScriptFile>>

    @POST("api/scripts")
    suspend fun addScript(@Body body: ScriptAddRequest): ApiResponse<Unit>

    @PUT("api/scripts")
    suspend fun updateScript(@Body body: ScriptUpdateRequest): ApiResponse<Unit>

    @HTTP(method = "DELETE", path = "api/scripts", hasBody = true)
    suspend fun deleteScript(@Body body: ScriptDeleteRequest): ApiResponse<Unit>

    @GET("api/scripts/{filename}")
    suspend fun getScriptContent(
        @Path("filename") filename: String,
        @Query("path") path: String = ""
    ): ApiResponse<String>

    // ── Dependencies ──
    @GET("api/dependencies")
    suspend fun getDependencies(
        @Query("searchValue") search: String = "",
        @Query("type") type: String = ""
    ): ApiResponse<List<DependencyInfo>>

    @POST("api/dependencies")
    suspend fun addDependencies(@Body body: List<DependencyCreateRequest>): ApiResponse<Unit>

    @PUT("api/dependencies/reinstall")
    suspend fun reinstallDependencies(@Body ids: List<String>): ApiResponse<Unit>

    @HTTP(method = "DELETE", path = "api/dependencies/force", hasBody = true)
    suspend fun deleteDependencies(@Body ids: List<String>): ApiResponse<Unit>

    @GET("api/dependencies/{id}")
    suspend fun getDependenceLog(@Path("id") id: String): ApiResponse<DependenceLogEntry>

    // ── Config ──
    @POST("api/configs/save")
    suspend fun saveConfig(@Body body: Map<String, String>): ApiResponse<Unit>

    @GET("api/configs/{name}")
    suspend fun getConfigContent(@Path("name") name: String): ApiResponse<String>

    // ── Logs ──
    @GET("api/logs")
    suspend fun getLogFiles(): ApiResponse<List<ScriptFile>>

    @GET("api/logs/detail")
    suspend fun getLogDetail(): ApiResponse<List<ScriptFile>>

    @GET
    suspend fun getLogContent(@Url url: String): ApiResponse<String>

    @HTTP(method = "DELETE", path = "api/logs", hasBody = true)
    suspend fun deleteLogs(@Body ids: List<String>): ApiResponse<Unit>

    @POST("api/logs/download")
    suspend fun downloadLogs(@Body body: Map<String, String>): ApiResponse<Unit>
}
