package com.qinglong.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TaskInfo(
    @SerialName("_id") val id: String? = null,
    val name: String? = null,
    val command: String? = null,
    val schedule: String? = null,
    val status: Int? = null,  // 0=running, 1=idle, 2=disabled
    @SerialName("isPinned") val isPinned: Int? = null,
    @SerialName("last_running_time") val lastRunningTime: Long? = null,
    @SerialName("last_execution_time") val lastExecutionTime: Long? = null,
    @SerialName("isDisabled") val isDisabled: Int? = null
)

@Serializable
data class TaskListResponse(
    val data: List<TaskInfo>? = null,
    val total: Int? = null
)
