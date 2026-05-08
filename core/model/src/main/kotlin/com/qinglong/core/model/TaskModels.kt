package com.qinglong.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

/** 任务状态码 */
object TaskStatus {
    const val RUNNING = 0
    const val WAITING = 1
    const val IDLE = 2
    const val DISABLED = 3
    const val UNKNOWN = 4
}

@Serializable
data class TaskInfo(
    @SerialName("_id") val idRaw: JsonElement? = null,
    val name: String? = null,
    val command: String? = null,
    val schedule: String? = null,
    val status: Double? = null,
    @SerialName("isDisabled") val isDisabled: Int? = null,
    @SerialName("isPinned") val isPinned: Int? = null,
    @SerialName("isSystem") val isSystem: Int? = null,
    val pid: String? = null,
    @SerialName("log_path") val logPath: String? = null,
    @SerialName("last_running_time") val lastRunningTime: Long? = null,
    @SerialName("last_execution_time") val lastExecutionTime: Long? = null,
    val saved: String? = null,
    val timestamp: String? = null,
    @SerialName("createdAt") val createdAt: String? = null,
    @SerialName("updatedAt") val updatedAt: String? = null
) {
    /** 兼容 _id 为纯字符串或 { "$oid": "..." } 两种 MongoDB 格式 */
    val id: String?
        get() = when (idRaw) {
            is JsonPrimitive -> idRaw.jsonPrimitive.content
            is JsonObject -> idRaw.jsonObject["\$oid"]?.jsonPrimitive?.content
            else -> idRaw?.toString()
        }

    val statusCode: Int
        get() = when {
            isDisabled == 1 -> TaskStatus.DISABLED
            status != null && status <= 0.0 -> TaskStatus.RUNNING
            status != null && status <= 0.5 -> TaskStatus.WAITING
            else -> TaskStatus.IDLE
        }

    val statusText: String
        get() = when (statusCode) {
            TaskStatus.RUNNING -> "运行中"
            TaskStatus.WAITING -> "队列中"
            TaskStatus.DISABLED -> "已禁用"
            else -> "空闲中"
        }

    val pinned: Boolean get() = isPinned == 1
}

@Serializable
data class TaskListData(
    val data: List<TaskInfo>? = null,
    val total: Int? = null
)

@Serializable
data class TaskListResponse(
    val code: Int = 0,
    val message: String? = null,
    val data: TaskListData? = null
)
