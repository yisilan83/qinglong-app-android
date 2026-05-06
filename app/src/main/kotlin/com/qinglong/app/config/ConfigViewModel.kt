package com.qinglong.app.config

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qinglong.core.data.remote.QLApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConfigViewModel @Inject constructor(
    private val api: QLApiService
) : ViewModel() {

    private val _content = MutableStateFlow<String?>(null)
    val content = _content.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    init { loadConfig() }

    fun loadConfig(name: String = "config.sh") {
        viewModelScope.launch {
            _loading.value = true
            try {
                val r = api.getConfigContent(name)
                _content.value = if (r.code == 200) r.data else "加载失败"
            } catch (_: Exception) { _content.value = "加载失败" }
            _loading.value = false
        }
    }

    fun saveContent(name: String, content: String) {
        viewModelScope.launch {
            try {
                api.saveConfig(mapOf("name" to name, "content" to content))
            } catch (_: Exception) {}
            loadConfig(name)
        }
    }

    fun clearContent() { _content.value = null }
}
