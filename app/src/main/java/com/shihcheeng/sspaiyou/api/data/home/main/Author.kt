package com.shihcheeng.sspaiyou.api.data.home.main


import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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