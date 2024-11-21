package com.shihcheeng.sspaiyou.api.data.actrcle


import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class ArticleCount(
    @SerialName("comment_count")
    val commentCount: Int = 0,
    @SerialName("like_count")
    val likeCount: Int = 0,
    @SerialName("show_views_count")
    val showViewsCount: Boolean = false,
    @SerialName("views_count")
    val viewsCount: Int = 0
)