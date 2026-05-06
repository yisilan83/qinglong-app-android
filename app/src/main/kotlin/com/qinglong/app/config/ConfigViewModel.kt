package com.qinglong.app.config

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qinglong.core.data.remote.QLApiService
import com.qinglong.core.model.ConfigFile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConfigViewModel @Inject constructor(
    private val api: QLApiService
) : ViewModel() {

    private val _files = MutableStateFlow<List<ConfigFile>>(emptyList())
    val files = _files.asStateFlow()

    private val _content = MutableStateFlow<String?>(null)
    val content = _content.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    init { loadFiles() }

    fun loadFiles() {
        viewModelScope.launch {
            _loading.value = true
            try {
                val r = api.getConfigFiles()
                if (r.code == 200 && r.data != null) _files.value = r.data
            } catch (_: Exception) {}
            _loading.value = false
        }
    }

    fun loadContent(file: ConfigFile) {
        val name = file.name ?: file.title ?: return
        viewModelScope.launch {
            try {
                val r = api.getConfigContent(name)
                _content.value = if (r.code == 200) r.data else "加载失败"
            } catch (_: Exception) { _content.value = "加载失败" }
        }
    }

    fun saveContent(name: String, content: String) {
        viewModelScope.launch {
            try { api.saveConfig(mapOf("name" to name, "content" to content)) } catch (_: Exception) {}
            loadFiles()
        }
    }

    fun clearContent() { _content.value = null }
}
