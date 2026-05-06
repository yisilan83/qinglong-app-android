package com.qinglong.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ScriptFile(
    val title: String? = null,
    val key: String? = null,
    val type: String? = null,   // "directory" or "file"
    val children: List<ScriptFile>? = null,
    @SerialName("isLeaf") val isLeaf: Boolean? = null,
    val size: Long? = null,
    @SerialName("modify_time") val modifyTime: Long? = null
)
