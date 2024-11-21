package com.shihcheeng.sspaiyou.api.data.column


import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class ColumnDataModel(
    @SerialName("data")
    val `data`: List<Data> = listOf(),
    @SerialName("error")
    val error: Int = 0,
    @SerialName("msg")
    val msg: String = "",
    @SerialName("total")
    val total: Int = 0
)