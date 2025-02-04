package com.shihcheeng.sspaiyou.api.data.user.activity

import com.shihcheeng.sspaiyou.api.data.user.activity.commentreplydtos.Comment
import com.shihcheeng.sspaiyou.api.data.user.activity.commentreplydtos.CommentReply
import com.shihcheeng.sspaiyou.api.data.user.activity.commentreplydtos.CommentReplyTo
import com.shihcheeng.sspaiyou.api.data.user.activity.commentreplydtos.Status
import com.shihcheeng.sspaiyou.api.data.user.activity.commentreplydtos.Topic
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CommentReplyDataModel(
    val comment: Comment = Comment(),
    @SerialName("comment_reply")
    val commentReply: CommentReply,
    @SerialName("comment_reply_to")
    val commentReplyTo: CommentReplyTo,
    @SerialName("status")
    val status: Status,
    @SerialName("topic")
    val topic: Topic
)