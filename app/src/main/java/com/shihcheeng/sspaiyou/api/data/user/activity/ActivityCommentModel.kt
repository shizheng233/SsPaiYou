package com.shihcheeng.sspaiyou.api.data.user.activity


import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class ActivityCommentModel(
    @SerialName("artcile_banner")
    val artcileBanner: String = "",
    @SerialName("article_author_id")
    val articleAuthorId: Int = 0,
    @SerialName("article_author_nickname")
    val articleAuthorNickname: String = "",
    @SerialName("article_author_slug")
    val articleAuthorSlug: String = "",
    @SerialName("article_belong_to_member")
    val articleBelongToMember: Boolean = false,
    @SerialName("article_detele")
    val articleDetele: Boolean = false,
    @SerialName("article_id")
    val articleId: Int = 0,
    @SerialName("article_slug")
    val articleSlug: String = "",
    @SerialName("article_title")
    val articleTitle: String = "",
    @SerialName("comment")
    val comment: String = "",
    @SerialName("id")
    val id: Int = 0,
    @SerialName("series_id")
    val seriesId: Int = 0,
    @SerialName("series_title")
    val seriesTitle: String = ""
)