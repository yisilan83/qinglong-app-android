package com.qinglong.core.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

object EnvStatus {
    const val ENABLED = 0
    const val DISABLED = 1
}

/** 兼容 MongoDB ObjectId 的纯字符串和 { $oid: "..." } 两种格式 */
object ObjectIdSerializer : KSerializer<String?> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("ObjectId", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: String?) {
        encoder.encodeString(value ?: "")
    }

    override fun deserialize(decoder: Decoder): String? {
        return try {
            val jsonDecoder = decoder as JsonDecoder
            when (val el = jsonDecoder.decodeJsonElement()) {
                is JsonPrimitive -> el.jsonPrimitive.content
                is JsonObject -> el.jsonObject["\$oid"]?.jsonPrimitive?.content
                else -> el.toString()
            }
        } catch (_: Exception) {
            try { decoder.decodeString() } catch (_: Exception) { null }
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
