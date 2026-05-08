package com.qinglong.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.JsonTransformingSerializer
import kotlinx.serialization.json.jsonPrimitive

object EnvStatus {
    const val ENABLED = 0
    const val DISABLED = 1
}

object ObjectIdSerializer : JsonTransformingSerializer<String>(String.serializer()) {
    override fun transformDeserialize(element: JsonElement): JsonElement {
        return when {
            element is JsonPrimitive && element.isString -> element
            else -> JsonPrimitive(element.toString())
        }
    }
}

@Serializable
data class EnvInfo(
    @Serializable(with = ObjectIdSerializer::class)
    @SerialName("_id") val id: String? = null,
    val name: String? = null,
    val value: String? = null,
    val remarks: String? = null,
    val status: Int? = null,
    val position: Double? = null,
    @SerialName("createdAt") val createdAt: String? = null,
    @SerialName("updatedAt") val updatedAt: String? = null
) {
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
