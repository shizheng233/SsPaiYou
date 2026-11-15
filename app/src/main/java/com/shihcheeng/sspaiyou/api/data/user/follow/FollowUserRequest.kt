package com.shihcheeng.sspaiyou.api.data.user.follow

import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class FollowUserRequest(
    @SerialName("slug") val slug: String,
    // 1 follows the user; 2 unfollows them.
    @SerialName("flag") val flag: Int
)
