package com.shihcheeng.sspaiyou.api.data.actrcle


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import androidx.annotation.Keep

@Keep
@Serializable
data class ArticleDataModel(
    @SerialName("data")
    val `data`: Data = Data(),
    @SerialName("error")
    val error: Int = 0
)