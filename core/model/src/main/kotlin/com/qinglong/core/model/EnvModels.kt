package com.qinglong.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

object EnvStatus {
    const val ENABLED = 0
    const val DISABLED = 1
}

@Serializable
data class EnvInfo(
    @SerialName("_id") val id: String? = null,
    val name: String? = null,
    val value: String? = null,
    val remarks: String? = null,
    val status: Int? = null,       // 0=enabled, 1=disabled
    val position: Double? = null,
    @SerialName("createdAt") val createdAt: String? = null,
    @SerialName("updatedAt") val updatedAt: String? = null
) {
    val statusText: String
        get() = if (status == EnvStatus.ENABLED) "已启用" else "已禁用"
}

/** 用于请求体的环境变量 */
@Serializable
data class EnvCreateRequest(
    val name: String,
    val value: String,
    val remarks: String? = null
)

@Serializable
data class EnvUpdateRequest(
    @SerialName("_id") val id: String? = null,
    val name: String,
    val value: String,
    val remarks: String? = null
)
