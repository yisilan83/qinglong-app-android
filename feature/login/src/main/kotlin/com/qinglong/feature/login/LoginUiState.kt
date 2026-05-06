package com.qinglong.feature.login

import com.qinglong.core.data.session.StoredAccount

sealed interface LoginUiState {
    data object Idle : LoginUiState
    data object Loading : LoginUiState
    data class Ready(val accounts: List<StoredAccount>) : LoginUiState
    data class NeedTwoFactor(val username: String, val password: String) : LoginUiState
    data object Success : LoginUiState
    data class Error(val message: String) : LoginUiState
}
