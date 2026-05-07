package com.qinglong.core.data.repository

import com.qinglong.core.data.remote.QLApiService
import com.qinglong.core.domain.TaskRepository
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
        return apiCall {
            api.addTask(mapOf("name" to name, "command" to command, "schedule" to schedule))
        }
    }

    override suspend fun updateTask(id: String, name: String, command: String, schedule: String): Result<Unit> {
        return apiCall {
            api.updateTask(mapOf("_id" to id, "name" to name, "command" to command, "schedule" to schedule))
        }
    }

    override suspend fun deleteTasks(ids: List<String>) = apiCall { api.deleteTasks(ids) }
    override suspend fun runTasks(ids: List<String>) = apiCall { api.runTasks(ids) }
    override suspend fun stopTasks(ids: List<String>) = apiCall { api.stopTasks(ids) }
    override suspend fun enableTasks(ids: List<String>) = apiCall { api.enableTasks(ids) }
    override suspend fun disableTasks(ids: List<String>) = apiCall { api.disableTasks(ids) }
    override suspend fun pinTasks(ids: List<String>) = apiCall { api.pinTasks(ids) }
    override suspend fun unpinTasks(ids: List<String>) = apiCall { api.unpinTasks(ids) }

    override suspend fun getTaskLog(id: String): Result<String> {
        return try {
            val res = api.getTaskLog(id)
            if (res.code == 200 && res.data != null) {
                Result.success(res.data)
            } else {
                Result.failure(Exception(res.message ?: "加载日志失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

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
