package com.qinglong.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ConfigFile(
    val title: String? = null,
    val name: String? = null,
    @SerialName("isDir") val isDir: Boolean? = null
)

@Serializable
data class DependencyInfo(
    @SerialName("_id") val id: String? = null,
    val name: String? = null,
    val type: Int? = null,
    val status: Int? = null,
    val remark: String? = null,
    @SerialName("created_at") val createdAt: Long? = null
)

@Serializable
data class SubscribeInfo(
    @SerialName("_id") val id: Int? = null,
    val name: String? = null,
    val url: String? = null,
    val type: Int? = null,
    val schedule: String? = null,
    val status: Int? = null,
    @SerialName("whitelist") val whitelist: String? = null,
    @SerialName("blacklist") val blacklist: String? = null
)
