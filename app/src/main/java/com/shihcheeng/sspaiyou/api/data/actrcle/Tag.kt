package com.shihcheeng.sspaiyou.api.data.actrcle


import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class Tag(
    @SerialName("color")
    val color: String = "",
    @SerialName("icon")
    val icon: String = "",
    @SerialName("id")
    val id: Int = 0,
    @SerialName("title")
    val title: String = ""
)