package com.qinglong.feature.task

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qinglong.core.data.remote.QLApiService
import com.qinglong.core.model.TaskInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val api: QLApiService
) : ViewModel() {

    private val _uiState = MutableStateFlow(TaskUiState())
    val uiState: StateFlow<TaskUiState> = _uiState.asStateFlow()

    init { loadTasks() }

    // ── 列表加载 ──

    fun loadTasks(page: Int = 1) {
        viewModelScope.launch {
            _uiState.update {
                if (page == 1) it.copy(isRefreshing = true, isLoading = true)
                else it.copy(isLoadingMore = true)
            }
            try {
                val r = api.getTasks(search = _uiState.value.searchQuery, page = page, size = 50)
                if (r.code == 200) {
                    val listData = r.data
                    if (listData != null) {
                        val list = listData.data.orEmpty()
                        _uiState.update {
                            it.copy(
                                tasks = if (page == 1) list else it.tasks + list,
                                currentPage = page,
                                hasMore = list.size >= 50,
                                isRefreshing = false,
                                isLoading = false,
                                isLoadingMore = false
                            )
                        }
                    } else {
                        _uiState.update { it.copy(isRefreshing = false, isLoading = false, isLoadingMore = false) }
                    }
                } else {
                    _uiState.update {
                        it.copy(isRefreshing = false, isLoading = false, isLoadingMore = false, error = r.message)
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isRefreshing = false, isLoading = false, isLoadingMore = false, error = e.message)
                }
            }
        }
    }

    fun loadMore() {
        val s = _uiState.value
        if (!s.isLoadingMore && s.hasMore) loadTasks(s.currentPage + 1)
    }

    fun refresh() = loadTasks(1)

    fun onSearch(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        loadTasks(1)
    }

    fun clearError() { _uiState.update { it.copy(error = null) } }

    // ── 批量模式 ──

    fun toggleBatchMode() {
        _uiState.update {
            if (it.isBatchMode) it.copy(isBatchMode = false, selectedIds = emptySet())
            else it.copy(isBatchMode = true, selectedIds = emptySet())
        }
    }

    fun toggleSelection(id: String) {
        _uiState.update {
            val new = it.selectedIds.toMutableSet()
            if (new.contains(id)) new.remove(id) else new.add(id)
            it.copy(selectedIds = new)
        }
    }

    fun selectAll() {
        _uiState.update {
            if (it.selectedIds.size == it.tasks.size) it.copy(selectedIds = emptySet())
            else it.copy(selectedIds = it.tasks.mapNotNull { t -> t.id }.toSet())
        }
    }

    // ── 单项操作 ──

    fun runTask(task: TaskInfo) {
        task.id?.let { batchRun(listOf(it)) }
    }

    fun stopTask(task: TaskInfo) {
        task.id?.let { batchStop(listOf(it)) }
    }

    // ── 批量操作 ──

    fun batchRun(ids: List<String>) = batchOp(ids, "执行") { api.runTasks(it) }
    fun batchStop(ids: List<String>) = batchOp(ids, "停止") { api.stopTasks(it) }
    fun batchEnable(ids: List<String>) = batchOp(ids, "启用") { api.enableTasks(it) }
    fun batchDisable(ids: List<String>) = batchOp(ids, "禁用") { api.disableTasks(it) }
    fun batchPin(ids: List<String>) = batchOp(ids, "置顶") { api.pinTasks(it) }
    fun batchUnpin(ids: List<String>) = batchOp(ids, "取消置顶") { api.unpinTasks(it) }
    fun batchDelete(ids: List<String>) = batchOp(ids, "删除") { api.deleteTasks(it) }

    fun batchRunSelected() = batchRun(_uiState.value.selectedIds.toList())
    fun batchStopSelected() = batchStop(_uiState.value.selectedIds.toList())
    fun batchEnableSelected() = batchEnable(_uiState.value.selectedIds.toList())
    fun batchDisableSelected() = batchDisable(_uiState.value.selectedIds.toList())
    fun batchPinSelected() = batchPin(_uiState.value.selectedIds.toList())
    fun batchUnpinSelected() = batchUnpin(_uiState.value.selectedIds.toList())
    fun batchDeleteSelected() = batchDelete(_uiState.value.selectedIds.toList())

    private fun batchOp(ids: List<String>, name: String, op: suspend (List<String>) -> com.qinglong.core.model.ApiResponse<Unit>) {
        if (ids.isEmpty()) return
        viewModelScope.launch {
            try {
                val r = op(ids)
                if (r.code != 200) _uiState.update { it.copy(error = "${name}失败: ${r.message}") }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "${name}失败: ${e.message}") }
            }
            _uiState.update { it.copy(isBatchMode = false, selectedIds = emptySet()) }
            loadTasks(1)
        }
    }

    // ── 编辑 ──

    fun showEditDialog(task: TaskInfo? = null) {
        _uiState.update { it.copy(editingTask = task, showEditDialog = true) }
    }

    fun dismissEditDialog() {
        _uiState.update { it.copy(editingTask = null, showEditDialog = false) }
    }

    fun submitEdit(name: String, command: String, schedule: String) {
        val existing = _uiState.value.editingTask
        viewModelScope.launch {
            try {
                val r = if (existing != null && existing.id != null) {
                    api.updateTask(mapOf<String, String>("_id" to existing.id, "name" to name, "command" to command, "schedule" to schedule))
                } else {
                    api.addTask(mapOf<String, String>("name" to name, "command" to command, "schedule" to schedule))
                }
                if (r.code == 200) {
                    _uiState.update { it.copy(editingTask = null, showEditDialog = false) }
                    loadTasks(1)
                } else {
                    _uiState.update { it.copy(error = r.message ?: "操作失败") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    // ── 日志 ──

    fun showLog(task: TaskInfo) {
        task.id?.let { id ->
            viewModelScope.launch {
                try {
                    val r = api.getTaskLog(id)
                    _uiState.update {
                        it.copy(
                            logContent = if (r.code == 200) r.data else "加载失败: ${r.message}",
                            showLogSheet = true
                        )
                    }
                } catch (e: Exception) {
                    _uiState.update { it.copy(logContent = "加载失败: ${e.message}", showLogSheet = true) }
                }
            }
        }
    }

    fun dismissLog() {
        _uiState.update { it.copy(logContent = null, showLogSheet = false) }
    }
}
