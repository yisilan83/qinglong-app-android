package com.qinglong.core.data.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType

/**
 * Retrofit 客户端工厂。
 * 支持动态 baseUrl（多账户场景）。
 */
@Singleton
class QLRetrofitClient @Inject constructor(
    private val tokenProvider: TokenProvider
) {
    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        isLenient = true
    }

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor { chain ->
                val original = chain.request()
                val token = tokenProvider.getTokenSync()
                val request = if (token != null) {
                    original.newBuilder()
                        .header("Authorization", "Bearer $token")
                        .build()
                } else {
                    original
                }
                chain.proceed(request)
            }
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                }
            )
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            // 信任自签名证书
            .hostnameVerifier { _, _ -> true }
            .build()
    }

    /**
     * 为指定 host 创建 API 服务
     */
    fun createApiService(host: String): QLApiService {
        val baseUrl = if (host.endsWith("/")) host else "$host/"
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
        return retrofit.create(QLApiService::class.java)
    }
}

/**
 * Token 提供者接口（由 DataStore 实现）
 */
interface TokenProvider {
    fun getTokenSync(): String?
}
