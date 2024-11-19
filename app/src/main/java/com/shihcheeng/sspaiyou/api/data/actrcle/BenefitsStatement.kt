package com.shihcheeng.sspaiyou.api.data.actrcle


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import androidx.annotation.Keep

@Keep
@Serializable
data class BenefitsStatement(
    @SerialName("admin_id")
    val adminId: Int = 0,
    @SerialName("created_at")
    val createdAt: Int = 0,
    @SerialName("id")
    val id: Int = 0,
    @SerialName("status")
    val status: Int = 0,
    @SerialName("title")
    val title: String = "",
    @SerialName("updated_at")
    val updatedAt: Int = 0,
    @SerialName("usable")
    val usable: Boolean = false
)