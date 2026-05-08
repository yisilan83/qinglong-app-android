package com.qinglong.core.data.remote

import com.qinglong.core.data.session.SessionManager
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QLRetrofitClient @Inject constructor(
    private val okHttpClient: OkHttpClient,
    private val json: Json,
    val sessionManager: SessionManager  // 改为 val 以便诊断
) {
    val apiService: QLApiService
        get() = buildService(sessionManager.host ?: error("Host not set"))

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
