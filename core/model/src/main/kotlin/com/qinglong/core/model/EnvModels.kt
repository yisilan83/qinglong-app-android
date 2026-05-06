package com.qinglong.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EnvInfo(
    @SerialName("_id") val id: String? = null,
    val name: String? = null,
    val value: String? = null,
    val remarks: String? = null,
    val status: Int? = null,    // 0=disabled, 1=enabled
    val position: Long? = null,
    @SerialName("created_at") val createdAt: Long? = null,
    @SerialName("updated_at") val updatedAt: Long? = null
)
