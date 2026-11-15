package com.shihcheeng.sspaiyou.api.data.user.follow

import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class FollowMessageModel(
    @SerialName("error") val error: Int,
    @SerialName("msg") val msg: String = ""
)
