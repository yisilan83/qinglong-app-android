package com.qinglong.core.data.repository

import com.qinglong.core.data.remote.QLApiService
import com.qinglong.core.domain.ConfigRepository
import com.qinglong.core.model.SystemConfig
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConfigRepositoryImpl @Inject constructor(
    private val api: QLApiService
) : ConfigRepository {

    override suspend fun getConfigContent(name: String): Result<String> {
        return try {
            val res = api.getConfigContent(name)
            if (res.code == 200 && res.data != null) Result.success(res.data)
            else Result.failure(Exception(res.message ?: "获取配置内容失败"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun saveConfig(name: String, content: String): Result<Unit> {
        return try {
            val res = api.saveConfig(mapOf("name" to name, "content" to content))
            if (res.code == 200) Result.success(Unit)
            else Result.failure(Exception(res.message ?: "保存配置失败"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getSystemConfig(): Result<SystemConfig> {
        return try {
            val res = api.getSystemConfig()
            if (res.code == 200 && res.data?.info != null) Result.success(res.data.info)
            else Result.failure(Exception(res.message ?: "获取系统配置失败"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateSystemConfig(config: SystemConfig): Result<Unit> {
        return try {
            val body = mutableMapOf<String, Int>()
            config.logRemoveFrequency?.let { body["logRemoveFrequency"] = it }
            config.cronConcurrency?.let { body["cronConcurrency"] = it }
            val res = api.updateSystemConfig(body)
            if (res.code == 200) Result.success(Unit)
            else Result.failure(Exception(res.message ?: "更新系统配置失败"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
