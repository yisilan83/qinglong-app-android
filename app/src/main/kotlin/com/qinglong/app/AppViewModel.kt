package com.qinglong.app

import androidx.lifecycle.ViewModel
import com.qinglong.core.data.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/**
 * App 级别 ViewModel，管理全局登录状态。
 */
@HiltViewModel
class AppViewModel @Inject constructor(
    sessionManager: SessionManager
) : ViewModel() {

    val isLoggedIn: StateFlow<Boolean?> = sessionManager.tokenFlow
        .map { token ->
            // null 表示首次加载还未完成
            if (token == null) {
                sessionManager.hostFlow.value?.let { false } // 有 host 但无 token → 确实未登录
            } else {
                true // 有 token → 已登录
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
}
