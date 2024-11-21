package com.shihcheeng.sspaiyou.api.data.actrcle


import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class ArticleDataModel(
    @SerialName("data")
    val `data`: Data = Data(),
    @SerialName("error")
    val error: Int = 0
)