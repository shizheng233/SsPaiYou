package com.shihcheeng.sspaiyou.api.data.user.activity


import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class TopicActivityModel(
    @SerialName("blocked")
    val blocked: Boolean = false,
    @SerialName("body")
    val body: String = "",
    @SerialName("chosen")
    val chosen: Boolean = false,
    @SerialName("comment_notice_on")
    val commentNoticeOn: Boolean = false,
    @SerialName("comment_total")
    val commentTotal: Int = 0,
    @SerialName("community_top")
    val communityTop: Boolean = false,
    @SerialName("created_at")
    val createdAt: Int = 0,
    @SerialName("favorited")
    val favorited: Boolean = false,
    @SerialName("id_hash")
    val idHash: String = "",
    @SerialName("login_user_is_author")
    val loginUserIsAuthor: Boolean = false,
    @SerialName("share_on")
    val shareOn: Boolean = false,
    @SerialName("status")
    val status: Int = 0,
    @SerialName("subscribed")
    val subscribed: Boolean = false,
    @SerialName("summary")
    val summary: String = "",
    @SerialName("title")
    val title: String = "",
    @SerialName("top")
    val top: Boolean = false,
    @SerialName("view_count")
    val viewCount: Int = 0
)