package com.shihcheeng.sspaiyou.api.data.matrix.normal


import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class Corner(
    @SerialName("color")
    val color: String = "",
    @SerialName("icon")
    val icon: String = "",
    @SerialName("id")
    val id: Int = 0,
    @SerialName("memo")
    val memo: String = "",
    @SerialName("name")
    val name: String = "",
    @SerialName("url")
    val url: String = ""
)