package com.qinglong.app.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qinglong.core.data.remote.QLApiService
import com.qinglong.core.model.TaskInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TasksViewModel @Inject constructor(
    private val api: QLApiService
) : ViewModel() {

    private val _tasks = MutableStateFlow<List<TaskInfo>>(emptyList())
    val tasks = _tasks.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    private val _currentPage = MutableStateFlow(1)
    private val _hasMore = MutableStateFlow(false)

    private val _taskLog = MutableStateFlow<String?>(null)
    val taskLog = _taskLog.asStateFlow()

    init { loadTasks() }

    fun loadTasks(page: Int = 1) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val r = api.getTasks(page = page, size = 50)
                if (r.code == 200 && r.data != null) {
                    val list = r.data.data.orEmpty()
                    val total = r.data.total ?: 0
                    if (page == 1) {
                        _tasks.value = list
                    } else {
                        _tasks.value = _tasks.value + list
                    }
                    _currentPage.value = page
                    _hasMore.value = list.size >= 50
                }
            } catch (_: Exception) {}
            _loading.value = false
        }
    }

    fun loadMore() {
        if (!_loading.value && _hasMore.value) loadTasks(_currentPage.value + 1)
    }

    fun runTask(task: TaskInfo) {
        val id = task.id ?: return
        viewModelScope.launch {
            try { api.runTasks(listOf(id)) } catch (_: Exception) {}
            loadTasks()
        }
    }

    fun stopTask(task: TaskInfo) {
        val id = task.id ?: return
        viewModelScope.launch {
            try { api.stopTasks(listOf(id)) } catch (_: Exception) {}
            loadTasks()
        }
    }

    fun loadLog(task: TaskInfo) {
        val id = task.id ?: return
        viewModelScope.launch {
            try {
                val r = api.getTaskLog(id)
                _taskLog.value = if (r.code == 200) r.data else "加载失败"
            } catch (_: Exception) { _taskLog.value = "加载失败" }
        }
    }

    fun clearLog() { _taskLog.value = null }
}
