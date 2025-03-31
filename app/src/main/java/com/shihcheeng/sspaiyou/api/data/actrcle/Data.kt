package com.shihcheeng.sspaiyou.api.data.actrcle


import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class Data(
    @SerialName("allow_comment")
    val allowComment: Boolean = false,
    @SerialName("article_advertisement")
    val articleAdvertisement: String? = null,
    @SerialName("article_column")
    val articleColumn: String? = null,
    @SerialName("article_count")
    val articleCount: ArticleCount = ArticleCount(),
    @SerialName("article_follow_up_admin")
    val articleFollowUpAdmin: ArticleFollowUpAdmin? = ArticleFollowUpAdmin(),
    @SerialName("article_limit_free")
    val articleLimitFree: Boolean = false,
    @SerialName("article_limit_free_etime")
    val articleLimitFreeEtime: Int = 0,
    @SerialName("article_limit_free_stime")
    val articleLimitFreeStime: Int = 0,
    @SerialName("article_type")
    val articleType: Int = 0,
    @SerialName("author")
    val author: Author = Author(),
    @SerialName("author_seconds")
    val authorSeconds: List<Author>? = null,
    @SerialName("author_series_article_manage_on")
    val authorSeriesArticleManageOn: Boolean = false,
    @SerialName("banner")
    val banner: String = "",
    @SerialName("benefits_statement")
    val benefitsStatement: BenefitsStatement = BenefitsStatement(),
    @SerialName("body")
    val body: String = "",
    @SerialName("column_author")
    val columnAuthor: Boolean = false,
    @SerialName("comment_file_on")
    val commentFileOn: Boolean = false,
    @SerialName("comment_manage_permission")
    val commentManagePermission: Boolean = false,
    @SerialName("comments")
    val comments: String? = null,
    @SerialName("copyright")
    val copyright: Boolean = false,
    @SerialName("corner")
    val corner: CornerModel? = null,
    @SerialName("favorited")
    val favorited: Boolean = false,
    @SerialName("id")
    val id: Int = 0,
    @SerialName("important")
    val important: Int = 0,
    @SerialName("is_contributed_by_signed_writer")
    val isContributedBySignedWriter: Boolean = false,
    @SerialName("is_expire")
    val isExpire: Boolean = false,
    @SerialName("is_free")
    val isFree: Boolean = false,
    @SerialName("is_pay")
    val isPay: Boolean = false,
    @SerialName("is_preview")
    val isPreview: Boolean = false,
    @SerialName("issue")
    val issue: String = "",
    @SerialName("keywords")
    val keywords: String = "",
    @SerialName("liked")
    val liked: Boolean = false,
    @SerialName("next_article_banner")
    val nextArticleBanner: String = "",
    @SerialName("next_article_slug")
    val nextArticleSlug: String = "",
    @SerialName("next_article_title")
    val nextArticleTitle: String = "",
    @SerialName("next_artitle_id")
    val nextArtitleId: Int = 0,
    @SerialName("podcast_url")
    val podcastUrl: String = "",
    @SerialName("post_type")
    val postType: Int = 0,
    @SerialName("pre_article_banner")
    val preArticleBanner: String = "",
    @SerialName("pre_article_slug")
    val preArticleSlug: String = "",
    @SerialName("pre_article_title")
    val preArticleTitle: String = "",
    @SerialName("pre_artitle_id")
    val preArtitleId: Int = 0,
    @SerialName("probation")
    val probation: Boolean = false,
    @SerialName("promote_image")
    val promoteImage: String = "",
    @SerialName("promote_title")
    val promoteTitle: String = "",
    @SerialName("pub_date")
    val pubDate: String = "",
    @SerialName("released_time")
    val releasedTime: Int = 0,
    @SerialName("rtime")
    val rtime: String = "",
    @SerialName("series_use_end_time")
    val seriesUseEndTime: Int = 0,
    @SerialName("series_use_start_time")
    val seriesUseStartTime: Int = 0,
    @SerialName("show_content_table")
    val showContentTable: Boolean = false,
    @SerialName("sign_contract_request_fields")
    val signContractRequestFields: String = "",
    @SerialName("summary")
    val summary: String = "",
    @SerialName("summary_weibo")
    val summaryWeibo: String = "",
    @SerialName("tags")
    val tags: List<Tag>? = listOf(),
    @SerialName("thread")
    val thread: Boolean = false,
    @SerialName("title")
    val title: String = "",
    @SerialName("title_prefix")
    val titlePrefix: String = "",
    @SerialName("user_is_pay")
    val userIsPay: Boolean = false,
    @SerialName("video_code")
    val videoCode: String = "",
    @SerialName("video_info")
    val videoInfo: String = "",
    @SerialName("video_platform_type")
    val videoPlatformType: Int = 0,
    @SerialName("words")
    val words: Int = 0
) : IActData

typealias ArticleData = Data