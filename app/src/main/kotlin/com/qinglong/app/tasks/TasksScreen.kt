package com.qinglong.app.tasks

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.qinglong.core.model.TaskInfo
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksScreen(viewModel: TasksViewModel = hiltViewModel()) {
    val tasks by viewModel.tasks.collectAsStateWithLifecycle()
    val loading by viewModel.loading.collectAsStateWithLifecycle()
    val log by viewModel.taskLog.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()
    var showLog by remember { mutableStateOf(false) }

    if (log != null) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.clearLog(); showLog = false },
            sheetState = sheetState
        ) {
            Column(Modifier.fillMaxWidth().padding(16.dp)) {
                Text("任务日志", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                Text(log ?: "", fontFamily = FontFamily.Monospace, style = MaterialTheme.typography.bodySmall)
                Spacer(Modifier.height(32.dp))
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("定时任务") },
                actions = {
                    IconButton(onClick = viewModel::loadTasks) {
                        Icon(Icons.Default.Refresh, "刷新")
                    }
                }
            )
        }
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = loading,
            onRefresh = viewModel::loadTasks,
            modifier = Modifier.padding(padding)
        ) {
            if (tasks.isEmpty() && !loading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("暂无任务", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(tasks, key = { it.id ?: it.hashCode().toString() }) { task ->
                    TaskItem(
                        task = task,
                        onRun = { viewModel.runTask(task) },
                        onStop = { viewModel.stopTask(task) },
                        onLog = { scope.launch { sheetState.show(); viewModel.loadLog(task) } }
                    )
                }
            }
        }
    }
}

@Composable
private fun TaskItem(
    task: TaskInfo,
    onRun: () -> Unit,
    onStop: () -> Unit,
    onLog: () -> Unit
) {
    val isRunning = task.status == 0
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(Modifier.padding(12.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(task.name ?: "未命名", style = MaterialTheme.typography.titleSmall, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
                Row {
                    if (isRunning) {
                        IconButton(onClick = onStop, modifier = Modifier.size(36.dp)) {
                            Icon(Icons.Default.Stop, "停止", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(20.dp))
                        }
                    } else {
                        IconButton(onClick = onRun, modifier = Modifier.size(36.dp)) {
                            Icon(Icons.Default.PlayArrow, "运行", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                        }
                    }
                    IconButton(onClick = onLog, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Default.Description, "日志", modifier = Modifier.size(20.dp))
                    }
                }
            }
            Text(task.schedule ?: "", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(task.command ?: "", style = MaterialTheme.typography.bodySmall, maxLines = 2, overflow = TextOverflow.Ellipsis)
        }
    }
}
