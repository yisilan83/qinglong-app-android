package com.qinglong.feature.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qinglong.core.domain.GetHostUseCase
import com.qinglong.core.domain.GetTokenUseCase
import com.qinglong.core.domain.LoginTwoFactorUseCase
import com.qinglong.core.domain.LoginUseCase
import com.qinglong.core.domain.SaveCredentialsUseCase
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
    private val getTokenUseCase: GetTokenUseCase,
    private val getHostUseCase: GetHostUseCase
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

    /** 2FA 验证码 */
    private val _twoFactorCode = MutableStateFlow("")
    val twoFactorCode = _twoFactorCode.asStateFlow()

    /** 2FA 错误信息 */
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

    /** 是否可以点击登录按钮 */
    fun canLogin(): Boolean {
        if (_uiState.value is LoginUiState.Loading) return false
        return _host.value.isNotBlank() &&
                _username.value.isNotBlank() &&
                _password.value.isNotBlank()
    }

    /** 执行登录 */
    fun login() {
        val host = _host.value.trim()
        if (!host.startsWith("http://") && !host.startsWith("https://")) {
            _uiState.update { LoginUiState.Error("服务器地址必须以 http:// 或 https:// 开头") }
            return
        }

        _uiState.update { LoginUiState.Loading }

        viewModelScope.launch {
            // 先保存 host
            saveCredentialsUseCase(host, "", "", "")

            loginUseCase(_username.value, _password.value)
                .onSuccess { response ->
                    when (response.code) {
                        420 -> {
                            // 需要两步验证
                            _uiState.update {
                                LoginUiState.NeedTwoFactor(
                                    username = _username.value,
                                    password = _password.value
                                )
                            }
                        }
                        else -> {
                            // 登录成功
                            response.token?.let { token ->
                                saveCredentialsUseCase(
                                    host = host,
                                    username = _username.value,
                                    password = _password.value,
                                    token = token,
                                    alias = _alias.value.ifBlank { null }
                                )
                            }
                            _uiState.update { LoginUiState.Success }
                        }
                    }
                }
                .onFailure { e ->
                    _uiState.update { LoginUiState.Error(e.message ?: "登录失败") }
                }
        }
    }

    /** 提交两步验证码 */
    fun submitTwoFactor() {
        val code = _twoFactorCode.value.trim()
        if (code.isEmpty()) {
            _twoFactorError.value = "请输入验证码"
            return
        }

        val state = _uiState.value
        if (state !is LoginUiState.NeedTwoFactor) return

        _uiState.update { LoginUiState.Loading }

        viewModelScope.launch {
            loginTwoFactorUseCase(state.username, state.password, code)
                .onSuccess { response ->
                    response.token?.let { token ->
                        saveCredentialsUseCase(
                            host = _host.value.trim(),
                            username = state.username,
                            password = state.password,
                            token = token,
                            alias = _alias.value.ifBlank { null }
                        )
                    }
                    _uiState.update { LoginUiState.Success }
                }
                .onFailure { e ->
                    _twoFactorError.value = e.message ?: "验证失败"
                    _uiState.update {
                        LoginUiState.NeedTwoFactor(state.username, state.password)
                    }
                }
        }
    }

    /** 返回密码登录（取消 2FA） */
    fun backToPasswordLogin() {
        _twoFactorCode.value = ""
        _twoFactorError.value = null
        _uiState.update { LoginUiState.Idle }
    }

    /** 清除错误 */
    fun clearError() {
        _uiState.update { LoginUiState.Idle }
    }
}
