package com.shihcheeng.sspaiyou.api.data.matrix.look


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import androidx.annotation.Keep

@Keep
@Serializable
data class Author(
    @SerialName("avatar")
    val avatar: String = "",
    @SerialName("id")
    val id: Int = 0,
    @SerialName("nickname")
    val nickname: String = "",
    @SerialName("slug")
    val slug: String = ""
)