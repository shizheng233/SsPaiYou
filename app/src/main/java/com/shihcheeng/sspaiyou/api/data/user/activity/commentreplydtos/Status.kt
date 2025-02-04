package com.shihcheeng.sspaiyou.api.data.user.activity.commentreplydtos


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import androidx.annotation.Keep

@Keep
@Serializable
data class Status(
    @SerialName("banner")
    val banner: String = "",
    @SerialName("id")
    val id: Int = 0,
    @SerialName("like_on")
    val likeOn: Boolean = false,
    @SerialName("login_user_on")
    val loginUserOn: Boolean = false,
    @SerialName("title")
    val title: String = "",
    @SerialName("total")
    val total: Int = 0,
)