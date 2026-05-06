package com.qinglong.core.domain

import com.qinglong.core.model.EnvInfo

interface EnvRepository {
    suspend fun getEnvs(search: String = ""): Result<List<EnvInfo>>
    suspend fun addEnvs(envs: List<Triple<String, String, String?>>): Result<Unit>
    suspend fun updateEnv(id: String, name: String, value: String, remarks: String?): Result<Unit>
    suspend fun deleteEnvs(ids: List<String>): Result<Unit>
    suspend fun enableEnvs(ids: List<String>): Result<Unit>
    suspend fun disableEnvs(ids: List<String>): Result<Unit>
}
