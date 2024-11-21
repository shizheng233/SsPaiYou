package com.shihcheeng.sspaiyou.api.data.user.activity


import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class StatusActivityModel(
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
    val total: Int = 0
)