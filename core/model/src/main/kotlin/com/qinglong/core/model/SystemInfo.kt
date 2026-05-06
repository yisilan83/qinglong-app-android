package com.qinglong.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SystemInfo(
    val version: String? = null,
    @SerialName("os_type") val osType: String? = null,
    @SerialName("os_version") val osVersion: String? = null,
    @SerialName("cpu_usage") val cpuUsage: Double? = null,
    @SerialName("mem_total") val memTotal: String? = null,
    @SerialName("mem_usage") val memUsage: String? = null,
    @SerialName("disk_total") val diskTotal: String? = null,
    @SerialName("disk_usage") val diskUsage: String? = null,
    @SerialName("node_version") val nodeVersion: String? = null,
    @SerialName("npm_version") val npmVersion: String? = null
)
