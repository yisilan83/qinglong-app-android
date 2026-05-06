package com.qinglong.app.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qinglong.core.data.remote.QLApiService
import com.qinglong.core.model.SystemInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val apiService: QLApiService
) : ViewModel() {

    private val _systemInfo = MutableStateFlow<SystemInfo?>(null)
    val systemInfo = _systemInfo.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    init { refresh() }

    fun refresh() {
        viewModelScope.launch {
            _loading.value = true
            try {
                val response = apiService.getSystemInfo()
                if (response.code == 200) _systemInfo.value = response.data
            } catch (_: Exception) { /* silently ignore */ }
            _loading.value = false
        }
    }
}
