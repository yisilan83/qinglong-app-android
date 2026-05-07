package com.qinglong.core.model

import kotlinx.serialization.Serializable

@Serializable
data class SubscriptionInfo(
    val _id: String? = null,
    val name: String? = null,
    val url: String? = null,
    val type: String? = null,
    val branch: String? = null,
    val status: Int? = null,
    val isDisabled: Int? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
)

@Serializable
data class SubscriptionListResponse(
    val code: Int = 0,
    val message: String? = null,
    val data: List<SubscriptionInfo>? = null
)
