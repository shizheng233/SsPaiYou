package com.shihcheeng.sspaiyou.api.data.user.activity

import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class InnerData(
    @SerialName("status")
    val status: StatusActivityModel = StatusActivityModel(),
    @SerialName("topic")
    val topic: TopicActivityModel = TopicActivityModel()
)