package com.qinglong.core.data.repository

import com.qinglong.core.data.local.AuthLocalDataSource
import com.qinglong.core.data.remote.QLApiService
import com.qinglong.core.data.remote.QLRetrofitClient
import com.qinglong.core.domain.AuthRepository
import com.qinglong.core.model.LoginRequest
import com.qinglong.core.model.LoginResponse
import com.qinglong.core.model.TwoFactorRequest
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val retrofitClient: QLRetrofitClient,
    private val localDataSource: AuthLocalDataSource
) : AuthRepository {

    private var apiService: QLApiService? = null

    private suspend fun getApiService(): QLApiService {
        if (apiService == null) {
            val host = localDataSource.host.first() ?: throw IllegalStateException("Host not set")
            apiService = retrofitClient.createApiService(host)
        }
        return apiService!!
    }

    fun initApiService(host: String): QLApiService {
        val service = retrofitClient.createApiService(host)
        apiService = service
        return service
    }

    override suspend fun login(request: LoginRequest): Result<LoginResponse> {
        return runCatching {
            val host = localDataSource.host.first() ?: throw IllegalStateException("Host not set")
            val service = retrofitClient.createApiService(host)
            val response = service.login(request)

            if (response.code == 200 && response.data != null) {
                response.data
            } else if (response.code == 420) {
                // 需要两步验证，但仍返回 response 的信息
                LoginResponse(
                    code = 420,
                    message = response.message ?: "需要两步验证"
                )
            } else {
                throw Exception(response.message ?: "登录失败 (${response.code})")
            }
        }
    }

    override suspend fun loginTwoFactor(request: TwoFactorRequest): Result<LoginResponse> {
        return runCatching {
            val host = localDataSource.host.first() ?: throw IllegalStateException("Host not set")
            val service = retrofitClient.createApiService(host)
            val response = service.loginTwoFactor(request)

            if (response.code == 200 && response.data != null) {
                response.data
            } else {
                throw Exception(response.message ?: "两步验证失败 (${response.code})")
            }
        }
    }

    override suspend fun saveCredentials(
        host: String,
        username: String,
        password: String,
        token: String,
        alias: String?
    ) {
        localDataSource.saveCredentials(
            host = host,
            username = username,
            password = password,
            token = token,
            alias = alias,
            remember = true
        )
    }

    override suspend fun getToken(): String? {
        return localDataSource.token.first()
    }

    override suspend fun getHost(): String? {
        return localDataSource.host.first()
    }

    override suspend fun clearCredentials() {
        localDataSource.clearCredentials()
    }
}
