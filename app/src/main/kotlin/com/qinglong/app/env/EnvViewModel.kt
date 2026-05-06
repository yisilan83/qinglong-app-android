package com.qinglong.app.env

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qinglong.core.data.remote.QLApiService
import com.qinglong.core.model.EnvInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EnvViewModel @Inject constructor(
    private val api: QLApiService
) : ViewModel() {

    private val _envs = MutableStateFlow<List<EnvInfo>>(emptyList())
    val envs = _envs.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    private val _search = MutableStateFlow("")
    val search = _search.asStateFlow()

    init { loadEnvs() }

    fun onSearchChanged(value: String) {
        _search.value = value
        loadEnvs()
    }

    fun loadEnvs() {
        viewModelScope.launch {
            _loading.value = true
            try {
                val r = api.getEnvs(_search.value)
                if (r.code == 200 && r.data != null) _envs.value = r.data
            } catch (_: Exception) {}
            _loading.value = false
        }
    }

    fun toggleEnv(env: EnvInfo) {
        val id = env.id ?: return
        viewModelScope.launch {
            try {
                if (env.status == 1) api.disableEnvs(listOf(id))
                else api.enableEnvs(listOf(id))
            } catch (_: Exception) {}
            loadEnvs()
        }
    }

    fun addEnv(name: String, value: String, remarks: String) {
        viewModelScope.launch {
            try {
                api.addEnv(listOf(mapOf("name" to name, "value" to value, "remarks" to remarks)))
            } catch (_: Exception) {}
            loadEnvs()
        }
    }

    fun deleteEnv(env: EnvInfo) {
        val id = env.id ?: return
        viewModelScope.launch {
            try { api.deleteEnvs(listOf(id)) } catch (_: Exception) {}
            loadEnvs()
        }
    }
}
