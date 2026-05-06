package com.qinglong.core.data.repository

import com.qinglong.core.data.local.AuthLocalDataSource
import com.qinglong.core.data.remote.QLRetrofitClient
import com.qinglong.core.domain.AuthRepository
import com.qinglong.core.model.LoginRequest
import com.qinglong.core.model.LoginResult
import com.qinglong.core.model.TwoFactorRequest
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val retrofitClient: QLRetrofitClient,
    private val localDataSource: AuthLocalDataSource
) : AuthRepository {

    override suspend fun login(request: LoginRequest): LoginResult {
        return try {
            val host = localDataSource.host.first()
                ?: return LoginResult.Error("服务器地址未设置")
            val service = retrofitClient.createApiService(host)
            val response = service.login(request)

            when (response.code) {
                200 -> {
                    val data = response.data
                    if (data != null && data.token != null) {
                        LoginResult.Success(data)
                    } else {
                        LoginResult.Error("登录响应缺少 token")
                    }
                }
                420 -> LoginResult.NeedTwoFactor(
                    response.message ?: "需要两步验证"
                )
                else -> LoginResult.Error(
                    response.message ?: "登录失败 (${response.code})"
                )
            }
        } catch (e: Exception) {
            LoginResult.Error(e.message ?: "网络请求失败")
        }
    }

    override suspend fun loginTwoFactor(request: TwoFactorRequest): LoginResult {
        return try {
            val host = localDataSource.host.first()
                ?: return LoginResult.Error("服务器地址未设置")
            val service = retrofitClient.createApiService(host)
            val response = service.loginTwoFactor(request)

            when (response.code) {
                200 -> {
                    val data = response.data
                    if (data != null && data.token != null) {
                        LoginResult.Success(data)
                    } else {
                        LoginResult.Error("验证成功但缺少 token")
                    }
                }
                else -> LoginResult.Error(
                    response.message ?: "两步验证失败 (${response.code})"
                )
            }
        } catch (e: Exception) {
            LoginResult.Error(e.message ?: "网络请求失败")
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

    override suspend fun getToken(): String? = localDataSource.token.first()
    override suspend fun getHost(): String? = localDataSource.host.first()

    override suspend fun clearCredentials() {
        localDataSource.clearCredentials()
    }
}
