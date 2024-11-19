package com.shihcheeng.sspaiyou.api.data.home.sub

import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class HomeSubDataModel(
    @SerialName("data")
    val `data`: List<Data> = listOf(),
    @SerialName("error")
    val error: Int = 0,
    @SerialName("total")
    val total: Int = 0
)