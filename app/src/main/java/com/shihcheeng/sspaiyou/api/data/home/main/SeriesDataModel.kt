package com.shihcheeng.sspaiyou.api.data.home.main


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import androidx.annotation.Keep

@Keep
@Serializable
data class SeriesDataModel(
    @SerialName("banner")
    val banner: String = "",
    @SerialName("id")
    val id: Int = 0,
    @SerialName("title")
    val title: String = ""
)