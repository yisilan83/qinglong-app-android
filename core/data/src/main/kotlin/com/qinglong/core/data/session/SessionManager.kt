package com.qinglong.core.data.session

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

private val Context.sessionDataStore: DataStore<Preferences> by preferencesDataStore(name = "ql_session")

/**
 * 全局会话管理器。
 * 负责：
 * - 持久化 Host / Token / 账户信息
 * - 提供同步/异步两种 Token 访问（OkHttp 拦截器用同步，其他用 Flow）
 * - 登录状态判断
 */
@Singleton
class SessionManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private val KEY_HOST = stringPreferencesKey("host")
        private val KEY_USERNAME = stringPreferencesKey("username")
        private val KEY_PASSWORD = stringPreferencesKey("password")
        private val KEY_TOKEN = stringPreferencesKey("token")
        private val KEY_ALIAS = stringPreferencesKey("alias")
        private val KEY_REMEMBER = booleanPreferencesKey("remember_password")
    }

    // ── Flow 方式（UI 层推荐） ──

    val hostFlow: Flow<String?> = context.sessionDataStore.data.map { it[KEY_HOST] }
    val usernameFlow: Flow<String?> = context.sessionDataStore.data.map { it[KEY_USERNAME] }
    val tokenFlow: Flow<String?> = context.sessionDataStore.data.map { it[KEY_TOKEN] }
    val isLoggedInFlow: Flow<Boolean> = tokenFlow.map { it != null }

    // ── 同步方式（OkHttp 拦截器用） ──

    val host: String?
        get() = runBlocking { context.sessionDataStore.data.first()[KEY_HOST] }

    val token: String?
        get() = runBlocking { context.sessionDataStore.data.first()[KEY_TOKEN] }

    val isLoggedIn: Boolean
        get() = token != null

    // ── 写入 ──

    suspend fun saveSession(
        host: String,
        username: String,
        password: String,
        token: String,
        alias: String? = null,
        remember: Boolean = false
    ) {
        context.sessionDataStore.edit { prefs ->
            prefs[KEY_HOST] = host
            prefs[KEY_USERNAME] = username
            prefs[KEY_TOKEN] = token
            if (alias != null) prefs[KEY_ALIAS] = alias
            if (remember) prefs[KEY_PASSWORD] = password
            else prefs.remove(KEY_PASSWORD)
            prefs[KEY_REMEMBER] = remember
        }
    }

    /** 仅更新 Host（登录前保存服务器地址） */
    suspend fun setHost(host: String) {
        context.sessionDataStore.edit { prefs ->
            prefs[KEY_HOST] = host
        }
    }

    /** 清除登录态 */
    suspend fun clearSession() {
        context.sessionDataStore.edit { prefs ->
            prefs.remove(KEY_TOKEN)
            prefs.remove(KEY_PASSWORD)
        }
    }

    /** 清除全部数据 */
    suspend fun clearAll() {
        context.sessionDataStore.edit { it.clear() }
    }
}
