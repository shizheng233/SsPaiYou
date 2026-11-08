package com.shihcheeng.sspaiyou.api.data.actrcle

import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class ArticleLikeRequest(
    @SerialName("target_id")
    val targetId: Int,
    @SerialName("flag")
    val flag: Int
)
