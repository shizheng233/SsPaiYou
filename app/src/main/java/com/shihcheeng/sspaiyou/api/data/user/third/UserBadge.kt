package com.shihcheeng.sspaiyou.api.data.user.third


import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class UserBadge(
    @SerialName("color")
    val color: String = "",
    @SerialName("description")
    val description: String = "",
    @SerialName("icon")
    val icon: String = "",
    @SerialName("id")
    val id: Int = 0,
    @SerialName("key")
    val key: String = "",
    @SerialName("name")
    val name: String = "",
    @SerialName("status")
    val status: Int = 0,
    @SerialName("url")
    val url: String = ""
)