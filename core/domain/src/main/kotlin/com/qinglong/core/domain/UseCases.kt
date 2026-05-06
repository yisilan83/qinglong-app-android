package com.qinglong.core.domain

import com.qinglong.core.model.LoginRequest
import com.qinglong.core.model.LoginResponse
import com.qinglong.core.model.TwoFactorRequest
import javax.inject.Inject

/**
 * 登录用例
 */
class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(username: String, password: String): Result<LoginResponse> {
        return authRepository.login(LoginRequest(username, password))
    }
}

/**
 * 两步验证登录用例
 */
class LoginTwoFactorUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        username: String,
        password: String,
        code: String
    ): Result<LoginResponse> {
        return authRepository.loginTwoFactor(TwoFactorRequest(username, password, code))
    }
}

/**
 * 保存凭证用例
 */
class SaveCredentialsUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        host: String,
        username: String,
        password: String,
        token: String,
        alias: String? = null
    ) {
        authRepository.saveCredentials(host, username, password, token, alias)
    }
}

/**
 * 获取当前 Token 用例
 */
class GetTokenUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): String? = authRepository.getToken()
}

/**
 * 获取当前 Host 用例
 */
class GetHostUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): String? = authRepository.getHost()
}

/**
 * 登出用例
 */
class LogoutUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke() {
        authRepository.clearCredentials()
    }
}
