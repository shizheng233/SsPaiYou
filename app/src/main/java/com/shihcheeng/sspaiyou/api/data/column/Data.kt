package com.shihcheeng.sspaiyou.api.data.column


import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class Data(
    @SerialName("article_count")
    val articleCount: Int = 0,
    @SerialName("author")
    val author: Author = Author(),
    @SerialName("banner")
    val banner: String = "",
    @SerialName("follow_count")
    val followCount: Int = 0,
    @SerialName("followed")
    val followed: Boolean = false,
    @SerialName("id")
    val id: Int = 0,
    @SerialName("intro")
    val intro: String = "",
    @SerialName("released_article_count")
    val releasedArticleCount: Int = 0,
    @SerialName("title")
    val title: String = ""
)

typealias ColumnItem = Data