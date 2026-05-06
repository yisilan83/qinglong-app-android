package com.qinglong.feature.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qinglong.core.data.session.SessionManager
import com.qinglong.core.domain.LoginTwoFactorUseCase
import com.qinglong.core.domain.LoginUseCase
import com.qinglong.core.domain.SaveCredentialsUseCase
import com.qinglong.core.model.LoginResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val loginTwoFactorUseCase: LoginTwoFactorUseCase,
    private val saveCredentialsUseCase: SaveCredentialsUseCase,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState = _uiState.asStateFlow()

    private val _host = MutableStateFlow("")
    val host = _host.asStateFlow()

    private val _username = MutableStateFlow("")
    val username = _username.asStateFlow()

    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()

    private val _alias = MutableStateFlow("")
    val alias = _alias.asStateFlow()

    private val _rememberPassword = MutableStateFlow(false)
    val rememberPassword = _rememberPassword.asStateFlow()

    private val _twoFactorCode = MutableStateFlow("")
    val twoFactorCode = _twoFactorCode.asStateFlow()

    private val _twoFactorError = MutableStateFlow<String?>(null)
    val twoFactorError = _twoFactorError.asStateFlow()

    fun onHostChanged(value: String) { _host.value = value }
    fun onUsernameChanged(value: String) { _username.value = value }
    fun onPasswordChanged(value: String) { _password.value = value }
    fun onAliasChanged(value: String) { _alias.value = value }
    fun onRememberPasswordChanged(value: Boolean) { _rememberPassword.value = value }
    fun onTwoFactorCodeChanged(value: String) {
        _twoFactorCode.value = value
        _twoFactorError.value = null
    }

    fun canLogin(): Boolean {
        if (_uiState.value is LoginUiState.Loading) return false
        return _host.value.isNotBlank() &&
                _username.value.isNotBlank() &&
                _password.value.isNotBlank()
    }

    fun login() {
        val host = _host.value.trim()
        if (!host.startsWith("http://") && !host.startsWith("https://")) {
            _uiState.update { LoginUiState.Error("服务器地址必须以 http:// 或 https:// 开头") }
            return
        }

        _uiState.update { LoginUiState.Loading }

        viewModelScope.launch {
            sessionManager.setHost(host)
            when (val result = loginUseCase(_username.value, _password.value)) {
                is LoginResult.Success -> onLoginSuccess(host, result)
                is LoginResult.NeedTwoFactor -> _uiState.update {
                    LoginUiState.NeedTwoFactor(_username.value, _password.value)
                }
                is LoginResult.Error -> _uiState.update { LoginUiState.Error(result.message) }
            }
        }
    }

    fun submitTwoFactor() {
        val code = _twoFactorCode.value.trim()
        if (code.isEmpty()) {
            _twoFactorError.value = "请输入验证码"
            return
        }
        val state = _uiState.value as? LoginUiState.NeedTwoFactor ?: return

        _uiState.update { LoginUiState.Loading }

        viewModelScope.launch {
            when (val result = loginTwoFactorUseCase(state.username, state.password, code)) {
                is LoginResult.Success -> onLoginSuccess(_host.value.trim(), result)
                is LoginResult.NeedTwoFactor -> {
                    _twoFactorError.value = "仍需验证"
                    _uiState.update { LoginUiState.NeedTwoFactor(state.username, state.password) }
                }
                is LoginResult.Error -> {
                    _twoFactorError.value = result.message
                    _uiState.update { LoginUiState.NeedTwoFactor(state.username, state.password) }
                }
            }
        }
    }

    private suspend fun onLoginSuccess(host: String, result: LoginResult.Success) {
        saveCredentialsUseCase(
            host = host,
            username = _username.value,
            password = _password.value,
            token = result.data.token ?: "",
            alias = _alias.value.ifBlank { null }
        )
        _uiState.update { LoginUiState.Success }
    }

    fun backToPasswordLogin() {
        _twoFactorCode.value = ""
        _twoFactorError.value = null
        _uiState.update { LoginUiState.Idle }
    }

    fun clearError() {
        _uiState.update { LoginUiState.Idle }
    }
}
