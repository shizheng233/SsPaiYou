package com.shihcheeng.sspaiyou.api.data.user.activity


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import androidx.annotation.Keep

@Keep
@Serializable
data class AuthorX(
    @SerialName("avatar")
    val avatar: String = "",
    @SerialName("id")
    val id: Int = 0,
    @SerialName("nickname")
    val nickname: String = "",
    @SerialName("slug")
    val slug: String = ""
)