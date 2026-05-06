package com.qinglong.core.data.remote

import com.qinglong.core.model.ApiResponse
import com.qinglong.core.model.LoginData
import com.qinglong.core.model.LoginRequest
import com.qinglong.core.model.TwoFactorRequest
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT

/**
 * 青龙面板 API 接口定义
 */
interface QLApiService {

    /**
     * 用户名/密码登录
     * POST /api/user/login
     * 成功：code=200, data={token, ...}
     * 需两步验证：code=420
     */
    @POST("api/user/login")
    suspend fun login(@Body request: LoginRequest): ApiResponse<LoginData>

    /**
     * 两步验证登录
     * PUT /api/user/two-factor/login
     */
    @PUT("api/user/two-factor/login")
    suspend fun loginTwoFactor(@Body request: TwoFactorRequest): ApiResponse<LoginData>
}
