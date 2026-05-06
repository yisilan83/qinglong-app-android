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

    private val _taskLog = MutableStateFlow<String?>(null)
    val taskLog = _taskLog.asStateFlow()

    init { loadTasks() }

    fun loadTasks() {
        viewModelScope.launch {
            _loading.value = true
            try {
                val r = api.getTasks()
                if (r.code == 200 && r.data != null) _tasks.value = r.data
            } catch (_: Exception) {}
            _loading.value = false
        }
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
