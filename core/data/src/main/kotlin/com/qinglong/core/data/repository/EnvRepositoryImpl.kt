package com.qinglong.core.data.repository

import com.qinglong.core.data.remote.QLApiService
import com.qinglong.core.domain.EnvRepository
import com.qinglong.core.model.EnvCreateRequest
import com.qinglong.core.model.EnvInfo
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EnvRepositoryImpl @Inject constructor(
    private val api: QLApiService
) : EnvRepository {

    override suspend fun getEnvs(search: String): Result<List<EnvInfo>> {
        return try {
            val res = api.getEnvs(search)
            if (res.code == 200) Result.success(res.data.orEmpty())
            else Result.failure(Exception(res.message ?: "获取环境变量失败"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addEnvs(envs: List<Triple<String, String, String?>>): Result<Unit> {
        return try {
            val body = envs.map { EnvCreateRequest(it.first, it.second, it.third) }
            val res = api.addEnvs(body)
            if (res.code == 200) Result.success(Unit)
            else Result.failure(Exception(res.message ?: "添加失败"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateEnv(id: String, name: String, value: String, remarks: String?): Result<Unit> {
        return try {
            val body = mutableMapOf("_id" to id, "name" to name, "value" to value)
            remarks?.let { body["remarks"] = it }
            val res = api.updateEnv(body)
            if (res.code == 200) Result.success(Unit)
            else Result.failure(Exception(res.message ?: "更新失败"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteEnvs(ids: List<String>) = apiCall { api.deleteEnvs(ids) }
    override suspend fun enableEnvs(ids: List<String>) = apiCall { api.enableEnvs(ids) }
    override suspend fun disableEnvs(ids: List<String>) = apiCall { api.disableEnvs(ids) }

    private suspend fun apiCall(call: suspend () -> com.qinglong.core.model.ApiResponse<Unit>): Result<Unit> {
        return try {
            val res = call()
            if (res.code == 200) Result.success(Unit)
            else Result.failure(Exception(res.message ?: "操作失败"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
