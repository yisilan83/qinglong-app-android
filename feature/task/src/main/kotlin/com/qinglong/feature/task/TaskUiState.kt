package com.qinglong.feature.task

import com.qinglong.core.model.TaskInfo

data class TaskUiState(
    val tasks: List<TaskInfo> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val isLoadingMore: Boolean = false,
    val searchQuery: String = "",
    val currentPage: Int = 1,
    val hasMore: Boolean = false,
    val isBatchMode: Boolean = false,
    val selectedIds: Set<String> = emptySet(),
    val editingTask: TaskInfo? = null,
    val showEditDialog: Boolean = false,
    val logContent: String? = null,
    val showLogSheet: Boolean = false,
    val isLivePolling: Boolean = false,
    val error: String? = null,
    // 去重确认
    val duplicateTask: TaskInfo? = null,
    val showDuplicateDialog: Boolean = false,
    // 操作结果提示
    val successMessage: String? = null
)
