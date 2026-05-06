package com.qinglong.core.data.remote

import com.qinglong.core.data.session.SessionManager
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Retrofit 客户端工厂。
 * [createApiService] 为每次登录/请求按需创建 Retrofit 实例（支持多 Host）。
 */
@Singleton
class QLRetrofitClient @Inject constructor(
    private val okHttpClient: OkHttpClient,
    private val json: Json,
    private val sessionManager: SessionManager
) {
    /**
     * 获取当前会话的 API 服务（从已持久化的 Host 构建，用于登录后请求）。
     */
    val apiService: QLApiService
        get() = buildService(sessionManager.host ?: error("Host not set"))

    /**
     * 为指定 host 创建 API 服务（登录前使用）。
     */
    fun createApiService(host: String): QLApiService = buildService(host)

    private fun buildService(host: String): QLApiService {
        val baseUrl = if (host.endsWith("/")) host else "$host/"
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(QLApiService::class.java)
    }
}
