package com.shihcheeng.sspaiyou.api.data.user.activity

import androidx.annotation.Keep
import com.shihcheeng.sspaiyou.api.data.user.activity.commentreplydtos.Comment
import com.shihcheeng.sspaiyou.api.data.user.activity.commentreplydtos.Status
import com.shihcheeng.sspaiyou.api.data.user.activity.commentreplydtos.Topic
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class TopicCommentDataModel(
    val comment: Comment,
    val status: Status,
    val topic: Topic
)
