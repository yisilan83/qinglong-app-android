package com.qinglong.core.domain

import com.qinglong.core.model.ScriptFile

interface ScriptRepository {
    suspend fun getScripts(): Result<List<ScriptFile>>
    suspend fun getScriptContent(filename: String, path: String): Result<String>
    suspend fun addScript(filename: String, path: String, content: String): Result<Unit>
    suspend fun updateScript(filename: String, path: String, content: String): Result<Unit>
    suspend fun deleteScript(filename: String, path: String, isDir: Boolean): Result<Unit>
}
