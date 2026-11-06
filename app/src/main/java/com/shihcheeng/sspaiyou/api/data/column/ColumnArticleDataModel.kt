package com.shihcheeng.sspaiyou.api.data.column

import androidx.annotation.Keep
import com.shihcheeng.sspaiyou.api.data.home.main.Corner
import com.shihcheeng.sspaiyou.api.data.home.main.SeriesDataModel
import com.shihcheeng.sspaiyou.api.data.home.main.Tag
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Keep
@Serializable
data class ColumnArticleDataModel(
    @SerialName("data") val data: List<ColumnArticle> = emptyList(),
    @SerialName("error") val error: Int,
    @SerialName("msg") val msg: String = "",
    @SerialName("total") val total: Int? = null
)

@Keep
@Serializable
data class ColumnArticle(
    @SerialName("id") val id: Int = 0,
    @SerialName("title") val title: String = "",
    @SerialName("banner") val banner: String = "",
    @SerialName("summary") val summary: String = "",
    @SerialName("comment_count") val commentCount: Int = 0,
    @SerialName("like_count") val likeCount: Int = 0,
    @SerialName("view_count") val viewCount: Int = 0,
    @SerialName("free") val free: Boolean = false,
    @SerialName("post_type") val postType: Int = 0,
    @SerialName("important") val important: Int = 0,
    @SerialName("released_time") val releasedTime: Long = 0L,
    @SerialName("morning_paper_title") val morningPaperTitle: List<String> = emptyList(),
    @SerialName("advertisement_url") val advertisementUrl: String = "",
    @SerialName("series") val series: List<SeriesDataModel> = emptyList(),
    @SerialName("author") val author: ColumnUser = ColumnUser(),
    @SerialName("corner") val corner: Corner = Corner(),
    @SerialName("special_columns") val specialColumns: List<ColumnReference> = emptyList(),
    @SerialName("status") val status: Int = 0,
    @SerialName("created_time") val createdTime: Long = 0L,
    @SerialName("modify_time") val modifyTime: Long = 0L,
    @SerialName("is_matrix") val isMatrix: Boolean = false,
    @SerialName("is_recommend_to_home") val isRecommendToHome: Boolean = false,
    @SerialName("slug") val slug: String = "",
    @SerialName("belong_to_member") val belongToMember: Boolean = false,
    @SerialName("issue") val issue: String = "",
    @SerialName("tags") val tags: List<Tag> = emptyList(),
    @SerialName("podcast_duration") val podcastDuration: Int = 0,
    @SerialName("article_limit_free") val articleLimitFree: Boolean = false,
    @SerialName("article_limit_free_stime") val articleLimitFreeStime: Long = 0L,
    @SerialName("article_limit_free_etime") val articleLimitFreeEtime: Long = 0L,
    @SerialName("user_member_card_show_on") val userMemberCardShowOn: Boolean = false,
    @SerialName("object_type") val objectType: Int = 0,
    @SerialName("id_hash") val idHash: String = "",
    @SerialName("recommend_to_home_at") val recommendToHomeAt: Long = 0L,
    @SerialName("is_pre_recommend_to_home") val isPreRecommendToHome: Boolean = false,
    @SerialName("body_extend_titles") val bodyExtendTitles: List<JsonElement> = emptyList(),
    @SerialName("body_extend") val bodyExtend: ColumnArticleBodyExtend = ColumnArticleBodyExtend(),
    @SerialName("statuses") val statuses: List<JsonElement> = emptyList(),
    @SerialName("theme_color") val themeColor: String = "",
    @SerialName("banner_for_desktop") val bannerForDesktop: String = ""
)

@Keep
@Serializable
data class ColumnReference(
    @SerialName("id") val id: Int = 0,
    @SerialName("banner") val banner: String = "",
    @SerialName("title") val title: String = ""
)

@Keep
@Serializable
data class ColumnArticleBodyExtend(
    @SerialName("id") val id: Int = 0,
    @SerialName("title") val title: String = "",
    @SerialName("body") val body: String = "",
    @SerialName("comment_on") val commentOn: Boolean = false,
    @SerialName("comment_show_on") val commentShowOn: Boolean = false,
    @SerialName("status_on") val statusOn: Boolean = false,
    @SerialName("status_show_on") val statusShowOn: Boolean = false,
    @SerialName("comment_count") val commentCount: Int = 0,
    @SerialName("comments") val comments: List<JsonElement>? = null,
    @SerialName("statuses") val statuses: List<JsonElement>? = null
)
