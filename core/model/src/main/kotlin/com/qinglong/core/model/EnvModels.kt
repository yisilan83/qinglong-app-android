package com.qinglong.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

object EnvStatus {
    const val ENABLED = 0
    const val DISABLED = 1
}

@Serializable
data class EnvInfo(
    @SerialName("_id") val idRaw: JsonElement? = null,
    val name: String? = null,
    val value: String? = null,
    val remarks: String? = null,
    val status: Int? = null,
    val position: Double? = null,
    @SerialName("createdAt") val createdAt: String? = null,
    @SerialName("updatedAt") val updatedAt: String? = null
) {
    /** 兼容 _id 为纯字符串或 { "$oid": "..." } 两种格式 */
    val id: String?
        get() = when (idRaw) {
            is JsonPrimitive -> idRaw.jsonPrimitive.content
            is JsonObject -> idRaw.jsonObject["\$oid"]?.jsonPrimitive?.content
            else -> idRaw?.toString()
        }

    val statusText: String
        get() = if (status == EnvStatus.ENABLED) "已启用" else "已禁用"
}

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
