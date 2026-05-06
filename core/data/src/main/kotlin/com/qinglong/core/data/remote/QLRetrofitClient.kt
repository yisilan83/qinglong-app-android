package com.qinglong.core.data.remote

import com.qinglong.core.data.session.SessionManager
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Retrofit 客户端工厂。
 * 使用默认 baseUrl 占位，实际 host 通过 @Url 动态指定或由调用方在拦截器中切换。
 */
@Singleton
class QLRetrofitClient @Inject constructor(
    private val okHttpClient: OkHttpClient,
    private val json: Json,
    private val sessionManager: SessionManager
) {
    /**
     * 获取指向当前 Host 的 API 服务。
     * Host 变化时需调用 [rebuildForCurrentHost] 重建。
     */
    val apiService: QLApiService by lazy { buildService(sessionManager.host ?: "http://localhost:5700/") }

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
