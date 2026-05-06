package com.qinglong.app.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.RssFeed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onLogout: () -> Unit,
    onNavigateToConfig: (() -> Unit)? = null
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("设置") }) }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            if (onNavigateToConfig != null) {
                ListItem(
                    headlineContent = { Text("配置文件") },
                    supportingContent = { Text("查看和管理 config.sh 等") },
                    leadingContent = { Icon(Icons.Default.Description, null) },
                    trailingContent = { Icon(Icons.Default.ChevronRight, null) },
                    modifier = Modifier.clickable { onNavigateToConfig() }
                )
                ListItem(
                    headlineContent = { Text("环境变量") },
                    supportingContent = { Text("在首页环境标签中管理") },
                    leadingContent = { Icon(Icons.Default.Extension, null) }
                )
                ListItem(
                    headlineContent = { Text("依赖管理") },
                    supportingContent = { Text("NodeJS / Python3 / Linux 依赖") },
                    leadingContent = { Icon(Icons.Default.Menu, null) }
                )
                ListItem(
                    headlineContent = { Text("订阅管理") },
                    supportingContent = { Text("在任务标签页中查看订阅") },
                    leadingContent = { Icon(Icons.Default.RssFeed, null) }
                )
                HorizontalDivider(Modifier.padding(horizontal = 16.dp))
                Spacer(Modifier.height(8.dp))
            }

            Spacer(Modifier.weight(1f))

            Button(
                onClick = onLogout,
                modifier = Modifier.fillMaxWidth().height(52.dp).padding(horizontal = 16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("退出登录", color = MaterialTheme.colorScheme.onError)
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}
