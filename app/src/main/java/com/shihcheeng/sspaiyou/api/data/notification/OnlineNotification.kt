package com.shihcheeng.sspaiyou.api.data.notification

import androidx.annotation.Keep
import com.shihcheeng.sspaiyou.api.data.column.ColumnUser
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class OnlineNotification(
    val icon: String = "",
    val title: String,
    @SerialName("is_read") val isRead: Boolean,
    val type: Int,
    val user: ColumnUser? = null,
    @SerialName("created_at") val createdAt: Long,
    @SerialName("article_title") val articleTitle: String = "",
    @SerialName("article_banner") val articleBanner: String = "",
    @SerialName("article_id") val articleId: Int = 0,
    @SerialName("article_belong_to_member") val articleBelongToMember: Boolean = false,
    @SerialName("article_slug") val articleSlug: String = "",
    val comment: String = "",
    @SerialName("comment_id") val commentId: Int = 0,
    @SerialName("comment_reply_id") val commentReplyId: Int = 0,
    @SerialName("community_topic_title") val communityTopicTitle: String = "",
    @SerialName("community_topic_id_hash") val communityTopicIdHash: String = "",
    @SerialName("topic_summary") val topicSummary: String = "",
    @SerialName("topic_comment_id_hash") val topicCommentIdHash: String = "",
    @SerialName("topic_comment_reply_id_hash") val topicCommentReplyIdHash: String = "",
    @SerialName("bullet_created_at") val bulletCreatedAt: Long = 0,
    @SerialName("bullet_title") val bulletTitle: String = "",
    @SerialName("bullet_banner") val bulletBanner: String = "",
    @SerialName("thread_id_hash") val threadIdHash: String = "",
    @SerialName("thread_comment_id_hash") val threadCommentIdHash: String = "",
    @SerialName("thread_comment_reply_id_hash") val threadCommentReplyIdHash: String = "",
    @SerialName("article_body_extend_id") val articleBodyExtendId: Int = 0,
    @SerialName("article_body_extend_title") val articleBodyExtendTitle: String = "",
    @SerialName("allow_delete") val allowDelete: Boolean = false
)
