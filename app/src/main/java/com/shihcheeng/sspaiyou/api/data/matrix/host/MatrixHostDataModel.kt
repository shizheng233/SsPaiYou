package com.shihcheeng.sspaiyou.api.data.matrix.host


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import androidx.annotation.Keep

@Keep
@Serializable
data class MatrixHostDataModel(
    @SerialName("data")
    val `data`: List<Data> = listOf(),
    @SerialName("error")
    val error: Int = 0,
    @SerialName("msg")
    val msg: String = "",
    @SerialName("total")
    val total: Int = 0
)