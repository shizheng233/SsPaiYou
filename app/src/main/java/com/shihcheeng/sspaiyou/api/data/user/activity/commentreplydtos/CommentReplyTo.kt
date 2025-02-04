package com.shihcheeng.sspaiyou.api.data.user.activity.commentreplydtos


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import androidx.annotation.Keep

@Keep
@Serializable
data class CommentReplyTo(
    @SerialName("admin_id")
    val adminId: Int = 0,
    @SerialName("audited_at")
    val auditedAt: Int = 0,
    @SerialName("blocked")
    val blocked: Boolean = false,
    @SerialName("blocked_at")
    val blockedAt: Int = 0,
    @SerialName("body")
    val body: String = "",
    @SerialName("body_type")
    val bodyType: Int = 0,
    @SerialName("channel_id")
    val channelId: Int = 0,
    @SerialName("chosen")
    val chosen: Boolean = false,
    @SerialName("chosen_at")
    val chosenAt: Int = 0,
    @SerialName("client_admin_id")
    val clientAdminId: Int = 0,
    @SerialName("comment_id")
    val commentId: Int = 0,
    @SerialName("comment_id_hash")
    val commentIdHash: String = "",
    @SerialName("created_at")
    val createdAt: Int = 0,
    @SerialName("id")
    val id: Int = 0,
    @SerialName("id_hash")
    val idHash: String = "",
    @SerialName("reply_comment_reply_id_hash")
    val replyCommentReplyIdHash: String = "",
    @SerialName("reply_id")
    val replyId: Int = 0,
    @SerialName("reply_user_id")
    val replyUserId: Int = 0,
    @SerialName("status")
    val status: Int = 0,
    @SerialName("top")
    val top: Boolean = false,
    @SerialName("top_at")
    val topAt: Int = 0,
    @SerialName("topic_id")
    val topicId: Int = 0,
    @SerialName("updated_at")
    val updatedAt: Int = 0,
    @SerialName("user_id")
    val userId: Int = 0
)