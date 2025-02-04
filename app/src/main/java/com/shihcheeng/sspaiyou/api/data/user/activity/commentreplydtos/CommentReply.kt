package com.shihcheeng.sspaiyou.api.data.user.activity.commentreplydtos


import androidx.annotation.Keep
import com.shihcheeng.sspaiyou.api.data.user.activity.Author
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class CommentReply(
    @SerialName("author")
    val author: Author? = Author(),
    @SerialName("blocked")
    val blocked: Boolean = false,
    @SerialName("body")
    val body: String = "",
    @SerialName("chosen")
    val chosen: Boolean = false,
    @SerialName("created_at")
    val createdAt: Int = 0,
    @SerialName("hates_count")
    val hatesCount: Int = 0,
    @SerialName("id_hash")
    val idHash: String = "",
    @SerialName("likes_count")
    val likesCount: Int = 0,
    @SerialName("status")
    val status: Int = 0,
    @SerialName("top")
    val top: Boolean = false
)