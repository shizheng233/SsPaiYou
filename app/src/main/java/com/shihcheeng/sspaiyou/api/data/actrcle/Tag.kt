package com.shihcheeng.sspaiyou.api.data.actrcle


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import androidx.annotation.Keep

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