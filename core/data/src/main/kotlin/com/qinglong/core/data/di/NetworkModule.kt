package com.qinglong.core.data.di

import android.util.Log
import com.qinglong.core.data.remote.QLApiService
import com.qinglong.core.data.remote.QLRetrofitClient
import com.qinglong.core.data.session.SessionManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

private const val TAG = "QL-Network"

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        isLenient = true
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(sessionManager: SessionManager): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                val original = chain.request()
                val token = sessionManager.token
                Log.d(TAG, "→ ${original.method} ${original.url} | token=${if (token != null) "${token.take(20)}..." else "NULL"}")
                val request = if (!token.isNullOrBlank()) {
                    original.newBuilder()
                        .header("Authorization", "Bearer $token")
                        .build()
                } else {
                    Log.w(TAG, "⚠ 请求缺少 Authorization: ${original.url}")
                    original
                }
                chain.proceed(request)
            }
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.HEADERS
            })
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .hostnameVerifier { _, _ -> true }
            .build()
    }

    @Provides
    @Singleton
    fun provideQLRetrofitClient(
        okHttpClient: OkHttpClient,
        json: Json,
        sessionManager: SessionManager
    ): QLRetrofitClient {
        return QLRetrofitClient(okHttpClient, json, sessionManager)
    }

    @Provides
    @Singleton
    fun provideQLApiService(client: QLRetrofitClient): QLApiService {
        Log.d(TAG, "初始化 QLApiService, host=${client.sessionManager.host}")
        return client.apiService
    }
}
