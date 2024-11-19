package com.shihcheeng.sspaiyou.api.data.user


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import androidx.annotation.Keep

@Keep
@Serializable
data class SocialNetwork(
    @SerialName("id")
    val id: Int = 0,
    @SerialName("nickname")
    val nickname: String = "",
    @SerialName("type")
    val type: Int = 0,
    @SerialName("uid")
    val uid: String = "",
    @SerialName("user_slug")
    val userSlug: String = ""
)