package com.shihcheeng.sspaiyou.api.data.user.activity


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import androidx.annotation.Keep

@Keep
@Serializable
data class UserActivityDataModel(
    @SerialName("data")
    val `data`: List<Data>? = listOf(),
    @SerialName("error")
    val error: Int = 0,
    @SerialName("total")
    val total: Int = 0
)