package com.qinglong.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 登录请求体
 */
@Serializable
data class LoginRequest(
    val username: String,
    val password: String
)

/**
 * 两步验证请求体
 */
@Serializable
data class TwoFactorRequest(
    val username: String,
    val password: String,
    val code: String
)

/**
 * 登录响应
 */
@Serializable
data class LoginResponse(
    val token: String? = null,
    @SerialName("lastip") val lastIp: String? = null,
    @SerialName("lastaddr") val lastAddr: String? = null,
    @SerialName("lastlogon") val lastLogon: Long? = null,
    val retries: Int? = null,
    val platform: String? = null,
    val code: Int? = null,
    val message: String? = null
)
