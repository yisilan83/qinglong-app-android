package com.qinglong.core.data.repository

import com.qinglong.core.data.remote.QLApiService
import com.qinglong.core.domain.EnvRepository
import com.qinglong.core.model.EnvCreateRequest
import com.qinglong.core.model.EnvInfo
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
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
            val arr = buildJsonArray {
                envs.forEach { (name, value, remarks) ->
                    add(buildJsonObject {
                        put("name", name)
                        put("value", value)
                        remarks?.let { put("remarks", it) }
                    })
                }
            }
            val body = arr.toString().toRequestBody("application/json".toMediaType())
            val res = api.addEnvs(body)
            if (res.code == 200) Result.success(Unit)
            else Result.failure(Exception(res.message ?: "添加失败"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateEnv(id: String, name: String, value: String, remarks: String?): Result<Unit> {
        return try {
            val json = buildJsonObject {
                put("_id", id)
                put("name", name)
                put("value", value)
                if (remarks != null) put("remarks", remarks)
            }
            val body = json.toString().toRequestBody("application/json".toMediaType())
            val res = api.updateEnv(body)
            if (res.code == 200) Result.success(Unit)
            else Result.failure(Exception(res.message ?: "更新失败 (${res.code})"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteEnvs(ids: List<String>): Result<Unit> {
        return try {
            val arr = buildJsonArray { ids.forEach { add(JsonPrimitive(it)) } }
            val body = arr.toString().toRequestBody("application/json".toMediaType())
            val res = api.deleteEnvs(body)
            if (res.code == 200) Result.success(Unit)
            else Result.failure(Exception(res.message ?: "删除失败"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun enableEnvs(ids: List<String>): Result<Unit> {
        return listOp(ids) { api.enableEnvs(it) }
    }

    override suspend fun disableEnvs(ids: List<String>): Result<Unit> {
        return listOp(ids) { api.disableEnvs(it) }
    }

    private suspend fun listOp(ids: List<String>, call: suspend (RequestBody) -> com.qinglong.core.model.ApiResponse<Unit>): Result<Unit> {
        return try {
            val arr = buildJsonArray { ids.forEach { add(JsonPrimitive(it)) } }
            val body = arr.toString().toRequestBody("application/json".toMediaType())
            val res = call(body)
            if (res.code == 200) Result.success(Unit)
            else Result.failure(Exception(res.message ?: "操作失败"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
