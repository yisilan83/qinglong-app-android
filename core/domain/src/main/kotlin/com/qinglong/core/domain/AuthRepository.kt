package com.qinglong.core.domain

import com.qinglong.core.model.LoginRequest
import com.qinglong.core.model.LoginResponse
import com.qinglong.core.model.TwoFactorRequest

/**
 * 认证仓库接口
 */
interface AuthRepository {

    /**
     * 用户名密码登录。
     * 成功返回 [LoginResponse]，code=420 时需要两步验证。
     */
    suspend fun login(request: LoginRequest): Result<LoginResponse>

    /**
     * 两步验证登录
     */
    suspend fun loginTwoFactor(request: TwoFactorRequest): Result<LoginResponse>

    /**
     * 保存登录凭证
     */
    suspend fun saveCredentials(
        host: String,
        username: String,
        password: String,
        token: String,
        alias: String? = null
    )

    /**
     * 获取当前 Token
     */
    suspend fun getToken(): String?

    /**
     * 获取当前 Host
     */
    suspend fun getHost(): String?

    /**
     * 清除凭证（登出）
     */
    suspend fun clearCredentials()
}
