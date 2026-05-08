package com.qinglong.core.data.repository

import com.qinglong.core.data.remote.QLApiService
import com.qinglong.core.domain.TaskRepository
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskRepositoryImpl @Inject constructor(
    private val api: QLApiService
) : TaskRepository {

    override suspend fun getTasks(search: String, page: Int, size: Int): Result<Pair<List<com.qinglong.core.model.TaskInfo>, Int>> {
        return try {
            val res = api.getTasks(search, page, size)
            if (res.code == 200) {
                val listData = res.data
                if (listData != null) {
                    Result.success(Pair(listData.data.orEmpty(), listData.total ?: 0))
                } else {
                    Result.success(Pair(emptyList(), 0))
                }
            } else {
                Result.failure(Exception(res.message ?: "获取任务列表失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addTask(name: String, command: String, schedule: String): Result<Unit> {
        val json = buildJsonObject {
            put("name", name)
            put("command", command)
            put("schedule", schedule)
        }
        return apiCall(json.toJsonBody()) { api.addTask(it) }
    }

    override suspend fun updateTask(id: String, name: String, command: String, schedule: String): Result<Unit> {
        val json = buildJsonObject {
            put("_id", id)
            put("name", name)
            put("command", command)
            put("schedule", schedule)
        }
        return apiCall(json.toJsonBody()) { api.updateTask(it) }
    }

    override suspend fun deleteTasks(ids: List<String>) = listOp(ids) { api.deleteTasks(it) }
    override suspend fun runTasks(ids: List<String>) = listOp(ids) { api.runTasks(it) }
    override suspend fun stopTasks(ids: List<String>) = listOp(ids) { api.stopTasks(it) }
    override suspend fun enableTasks(ids: List<String>) = listOp(ids) { api.enableTasks(it) }
    override suspend fun disableTasks(ids: List<String>) = listOp(ids) { api.disableTasks(it) }
    override suspend fun pinTasks(ids: List<String>) = listOp(ids) { api.pinTasks(it) }
    override suspend fun unpinTasks(ids: List<String>) = listOp(ids) { api.unpinTasks(it) }

    override suspend fun getTaskLog(id: String): Result<String> {
        return try {
            val res = api.getTaskLog(id)
            if (res.code == 200) {
                Result.success(res.data ?: "")
            } else {
                Result.failure(Exception(res.message ?: "加载日志失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun listOp(ids: List<String>, call: suspend (RequestBody) -> com.qinglong.core.model.ApiResponse<Unit>): Result<Unit> {
        val arr = buildJsonArray { ids.forEach { add(JsonPrimitive(it)) } }
        return apiCall(arr.toJsonBody(), call)
    }

    private suspend fun apiCall(
        body: RequestBody,
        call: suspend (RequestBody) -> com.qinglong.core.model.ApiResponse<Unit>
    ): Result<Unit> {
        return try {
            val res = call(body)
            if (res.code == 200) Result.success(Unit)
            else Result.failure(Exception(res.message ?: "操作失败"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

private fun kotlinx.serialization.json.JsonObject.toJsonBody(): RequestBody =
    toString().toRequestBody("application/json".toMediaType())

private fun kotlinx.serialization.json.JsonArray.toJsonBody(): RequestBody =
    toString().toRequestBody("application/json".toMediaType())
