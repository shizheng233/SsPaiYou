package com.shihcheeng.sspaiyou.api.data.home.second

import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class MainsItem(
    @SerialName("id")
    val id: Int,
    val image: String,
    val title: String,
    val url: String,
    val code: String,
    @SerialName("only_image")
    val onlyImage: Boolean
)
