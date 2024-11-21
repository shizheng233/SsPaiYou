package com.shihcheeng.sspaiyou.api.data.user


import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class Address(
    @SerialName("created_at")
    val createdAt: Int = 0,
    @SerialName("id")
    val id: Int = 0,
    @SerialName("is_used")
    val isUsed: Boolean = false,
    @SerialName("status")
    val status: Int = 0,
    @SerialName("updated_at")
    val updatedAt: Int = 0,
    @SerialName("user_id")
    val userId: Int = 0
)