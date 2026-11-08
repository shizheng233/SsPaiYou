package com.shihcheeng.sspaiyou.api.data.actrcle

import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class ArticleFavouriteResponse(
    @SerialName("error")
    val error: Int
)
