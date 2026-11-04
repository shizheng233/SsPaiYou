package com.shihcheeng.sspaiyou.api.data.user


import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class SocialNetwork(
    @SerialName("id")
    val id: Int = 0,
    @SerialName("nickname")
    val nickname: String = "",
    @SerialName("type")
    val type: String = "",
    @SerialName("uid")
    val uid: String = "",
    @SerialName("user_slug")
    val userSlug: String = ""
)