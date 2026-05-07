package com.qinglong.feature.log

import com.qinglong.core.model.ScriptFile

data class LogUiState(
    val logs: List<ScriptFile> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    // 日志内容
    val logContent: String? = null,
    val logFileName: String = "",
    val showLogSheet: Boolean = false,
    val isLoadingContent: Boolean = false,
    val error: String? = null
)
