package com.shihcheeng.sspaiyou.api.data.user.activity.commentreplydtos


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import androidx.annotation.Keep

@Keep
@Serializable
data class STCommentReplyDto(
    @SerialName("comment")
    val comment: Comment = Comment(),
    @SerialName("comment_reply")
    val commentReply: CommentReply = CommentReply(),
    @SerialName("comment_reply_to")
    val commentReplyTo: CommentReplyTo = CommentReplyTo(),
    @SerialName("status")
    val status: Status = Status(),
    @SerialName("topic")
    val topic: Topic = Topic()
)