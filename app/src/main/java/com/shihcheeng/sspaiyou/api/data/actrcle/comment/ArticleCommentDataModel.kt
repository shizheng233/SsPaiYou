package com.shihcheeng.sspaiyou.api.data.actrcle.comment

import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Keep
@Serializable
data class ArticleCommentDataModel(
    val error: Int = 0,
    val msg: String = "",
    val data: List<ArticleComment> = emptyList(),
    val total: Int = 0
)

@Keep
@Serializable
data class ArticleComment(
    val id: Int = 0,
    val comment: String = "",
    val blocked: Boolean = false,
    @SerialName("created_at") val createdAt: Long = 0L,
    val liked: Boolean = false,
    @SerialName("likes_count") val likesCount: Int = 0,
    val chosen: Boolean = false,
    val hide: Boolean = false,
    @SerialName("liked_status") val likedStatus: Int = 0,
    @SerialName("unlikes_count") val unlikesCount: Int = 0,
    // The supplied response only contains empty arrays for these fields.
    @SerialName("comment_files") val commentFiles: List<JsonElement> = emptyList(),
    val user: ArticleCommentUser = ArticleCommentUser(),
    val reply: List<JsonElement> = emptyList(),
    @SerialName("author_blocked") val authorBlocked: Boolean = false,
    val article: ArticleCommentArticle = ArticleCommentArticle(),
    val series: ArticleCommentSeries = ArticleCommentSeries(),
    val top: Boolean = false,
    @SerialName("is_author") val isAuthor: Boolean = false,
    val flags: List<JsonElement> = emptyList(),
    val status: Int = 0,
    @SerialName("reply_show") val replyShow: Boolean = false,
    val statuses: List<JsonElement> = emptyList(),
    @SerialName("article_body_extend_id") val articleBodyExtendId: Int = 0
)

@Keep
@Serializable
data class ArticleCommentUser(
    val id: Int = 0,
    val slug: String = "",
    val bio: String = "",
    val avatar: String = "",
    val nickname: String = "",
    @SerialName("power_plus_flag") val powerPlusFlag: Int = 0,
    val deleted: Boolean = false,
    @SerialName("user_flags") val userFlags: List<JsonElement> = emptyList(),
    @SerialName("user_badges") val userBadges: List<JsonElement> = emptyList(),
    @SerialName("user_plan_flags") val userPlanFlags: List<JsonElement> = emptyList(),
    @SerialName("user_id_verify_flags") val userIdVerifyFlags: List<JsonElement> = emptyList(),
    @SerialName("user_community_flags") val userCommunityFlags: List<JsonElement> = emptyList()
)

@Keep
@Serializable
data class ArticleCommentArticle(
    val id: Int = 0,
    val slug: String = "",
    val title: String = "",
    @SerialName("is_free") val isFree: Int = 0,
    val deleted: Boolean = false,
    val blocked: Boolean = false,
    @SerialName("comment_file_on") val commentFileOn: Boolean = false,
    @SerialName("allow_comment") val allowComment: Boolean = false,
    @SerialName("author_series_article_manage_on") val authorSeriesArticleManageOn: Boolean = false,
    val author: ArticleCommentUser = ArticleCommentUser(),
    @SerialName("belong_to_member") val belongToMember: Boolean = false,
    @SerialName("comment_manage_permission") val commentManagePermission: Boolean = false,
    @SerialName("member_series_slug") val memberSeriesSlug: String = ""
)

@Keep
@Serializable
data class ArticleCommentSeries(
    val id: Int = 0,
    val title: String = "",
    @SerialName("is_author") val isAuthor: Boolean = false,
    val author: ArticleCommentUser = ArticleCommentUser()
)
