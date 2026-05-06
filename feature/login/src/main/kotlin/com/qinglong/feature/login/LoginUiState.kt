package com.qinglong.feature.login

/**
 * 登录 UI 状态
 */
sealed interface LoginUiState {
    /** 初始 / 空闲 */
    data object Idle : LoginUiState

    /** 正在登录中 */
    data object Loading : LoginUiState

    /** 需要两步验证 */
    data class NeedTwoFactor(
        val username: String,
        val password: String
    ) : LoginUiState

    /** 登录成功 */
    data object Success : LoginUiState

    /** 登录失败 */
    data class Error(val message: String) : LoginUiState
}
