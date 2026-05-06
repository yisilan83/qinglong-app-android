package com.qinglong.app

import androidx.lifecycle.ViewModel
import com.qinglong.core.data.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    sessionManager: SessionManager
) : ViewModel() {

    /**
     * 登录状态。
     * - null = DataStore 首次加载中
     * - false = 未登录
     * - true = 已登录
     */
    val isLoggedIn: StateFlow<Boolean?> = sessionManager.tokenFlow
        .map { it != null }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
}
