package com.qinglong.core.domain

import com.qinglong.core.model.LoginRequest
import com.qinglong.core.model.LoginResult
import com.qinglong.core.model.TwoFactorRequest

/**
 * 认证仓库接口
 */
interface AuthRepository {
    suspend fun login(request: LoginRequest): LoginResult
    suspend fun loginTwoFactor(request: TwoFactorRequest): LoginResult
    suspend fun saveCredentials(host: String, username: String, password: String, token: String, alias: String? = null)
    suspend fun getToken(): String?
    suspend fun getHost(): String?
    suspend fun clearCredentials()
}
