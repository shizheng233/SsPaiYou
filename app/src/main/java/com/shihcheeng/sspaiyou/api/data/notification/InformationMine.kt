package com.shihcheeng.sspaiyou.api.data.notification

import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class InformationMineResponse(
    val data: InformationMine? = null,
    val error: Int,
    val msg: String = ""
)

@Keep
@Serializable
data class InformationMine(
    @SerialName("comment_count") val commentCount: Int = 0,
    @SerialName("community_comment_count") val communityCommentCount: Int = 0,
    @SerialName("community_subscribe_count") val communitySubscribeCount: Int = 0,
    @SerialName("is_series_author") val isSeriesAuthor: Boolean = false,
    @SerialName("last_message_community_comment") val lastMessageCommunityComment: String = "",
    @SerialName("last_message_community_subscribe") val lastMessageCommunitySubscribe: String = "",
    @SerialName("last_message_online") val lastMessageOnline: String = "",
    @SerialName("last_message_series_title") val lastMessageSeriesTitle: String = "",
    @SerialName("last_message_system") val lastMessageSystem: String = "",
    @SerialName("message_count") val messageCount: Int = 0,
    @SerialName("online_count") val onlineCount: Int = 0,
    @SerialName("series_comment_count") val seriesCommentCount: Int = 0,
    @SerialName("system_count") val systemCount: Int = 0
) {
    val hasNotifications: Boolean
        get() = commentCount > 0 || communityCommentCount > 0 || communitySubscribeCount > 0 ||
            messageCount > 0 || onlineCount > 0 || seriesCommentCount > 0 || systemCount > 0
}
