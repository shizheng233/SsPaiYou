package com.shihcheeng.sspaiyou.api.data.actrcle

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class ArticleColumn(
    val id: Int = 0,
    val title: String = "",
    val banner: String = "",
    val intro: String = "",
    val followed: Boolean = false
)
