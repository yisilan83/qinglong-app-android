    @PUT("api/dependencies")
    suspend fun updateDependency(@Body body: Map<String, String>): ApiResponse<Unit>

    @PUT("api/dependencies/cancel")
    suspend fun cancelDependency(@Body ids: List<String>): ApiResponse<Unit>

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

    @GET
    suspend fun getLogContent(@Url url: String): ApiResponse<String>

    @HTTP(method = "DELETE", path = "api/logs", hasBody = true)
    suspend fun deleteLogs(@Body ids: List<String>): ApiResponse<Unit>

    @POST("api/logs/download")
    suspend fun downloadLog(@Body ids: List<String>): ApiResponse<Unit>