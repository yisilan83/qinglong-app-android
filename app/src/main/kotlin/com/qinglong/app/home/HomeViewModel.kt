package com.qinglong.app.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qinglong.core.domain.LogRepository
import com.qinglong.core.domain.TaskRepository
import com.qinglong.core.model.ScriptFile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val runningCount: Int = 0,
    val idleCount: Int = 0,
    val logs: List<ScriptFile> = emptyList(),
    val isLoading: Boolean = false
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val taskRepo: TaskRepository,
    private val logRepo: LogRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init { refresh() }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            // tasks
            taskRepo.getTasks("", 1, 200)
                .onSuccess { (list, _) ->
                    val running = list.count { it.statusCode == 0 }
                    val idle = list.count { it.statusCode == 2 }
                    _uiState.update { it.copy(runningCount = running, idleCount = idle) }
                }
            // logs
            logRepo.getLogFiles()
                .onSuccess { logs ->
                    _uiState.update { it.copy(logs = logs.sortedByDescending { l -> l.title }) }
                }
            _uiState.update { it.copy(isLoading = false) }
        }
    }
}
