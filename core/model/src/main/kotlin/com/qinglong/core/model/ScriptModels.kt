package com.qinglong.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ScriptFile(
    val title: String? = null,
    @SerialName("isLeaf") val isLeaf: Boolean? = null,
    @SerialName("isDir") val isDir: Boolean? = null,
    val children: List<ScriptFile>? = null,
    val size: Long? = null,
    @SerialName("mtime") val mtime: Double? = null,
    val key: String? = null,
    val type: String? = null
) {
    val isDirectory: Boolean get() = isDir == true || isLeaf == false
}

@Serializable
data class ScriptUpdateRequest(
    val filename: String,
    val path: String = "",
    val content: String
)

@Serializable
data class ScriptDeleteRequest(
    val filename: String,
    val path: String = "",
    val type: String = "file"   // "file" or "directory"
)

@Serializable
data class ScriptAddRequest(
    val filename: String,
    val path: String = "",
    val content: String = ""
)
