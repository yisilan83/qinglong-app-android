package com.qinglong.core.domain

import com.qinglong.core.model.LoginLogEntry
import com.qinglong.core.model.ScriptFile

interface LogRepository {
    suspend fun getLogFiles(): Result<List<ScriptFile>>
    suspend fun getLogContent(path: String): Result<String>
    suspend fun getTaskLog(taskId: String): Result<String>
    suspend fun getLoginLogs(): Result<List<LoginLogEntry>>
}
