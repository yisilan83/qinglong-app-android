package com.qinglong.core.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.qinglong.core.data.remote.TokenProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "ql_auth")

/**
 * 认证相关的本地持久化存储
 */
@Singleton
class AuthLocalDataSource @Inject constructor(
    @ApplicationContext private val context: Context
) : TokenProvider {

    companion object {
        private val KEY_HOST = stringPreferencesKey("host")
        private val KEY_USERNAME = stringPreferencesKey("username")
        private val KEY_PASSWORD = stringPreferencesKey("password")
        private val KEY_TOKEN = stringPreferencesKey("token")
        private val KEY_ALIAS = stringPreferencesKey("alias")
        private val KEY_REMEMBER = booleanPreferencesKey("remember_password")
    }

    val host: Flow<String?> = context.dataStore.data.map { it[KEY_HOST] }
    val username: Flow<String?> = context.dataStore.data.map { it[KEY_USERNAME] }
    val password: Flow<String?> = context.dataStore.data.map { it[KEY_PASSWORD] }
    val token: Flow<String?> = context.dataStore.data.map { it[KEY_TOKEN] }
    val alias: Flow<String?> = context.dataStore.data.map { it[KEY_ALIAS] }
    val rememberPassword: Flow<Boolean> = context.dataStore.data.map { it[KEY_REMEMBER] ?: false }

    override fun getTokenSync(): String? {
        // 从 DataStore 同步获取（谨慎使用）
        return kotlinx.coroutines.runBlocking {
            context.dataStore.data.first()[KEY_TOKEN]
        }
    }

    suspend fun saveCredentials(
        host: String,
        username: String,
        password: String,
        token: String,
        alias: String? = null,
        remember: Boolean = false
    ) {
        context.dataStore.edit { prefs ->
            prefs[KEY_HOST] = host
            prefs[KEY_USERNAME] = username
            if (remember) {
                prefs[KEY_PASSWORD] = password
            } else {
                prefs.remove(KEY_PASSWORD)
            }
            prefs[KEY_TOKEN] = token
            if (alias != null) {
                prefs[KEY_ALIAS] = alias
            }
            prefs[KEY_REMEMBER] = remember
        }
    }

    suspend fun clearCredentials() {
        context.dataStore.edit { prefs ->
            prefs.remove(KEY_TOKEN)
            prefs.remove(KEY_PASSWORD)
        }
    }

    suspend fun clearAll() {
        context.dataStore.edit { it.clear() }
    }
}
