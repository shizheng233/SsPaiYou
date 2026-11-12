package com.shihcheeng.sspaiyou.api.data.column

import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class ColumnFollowRequest(
    @SerialName("target_id") val targetId: Int,
    @SerialName("flag") val flag: Int
)

@Keep
@Serializable
data class ColumnFollowResponse(
    @SerialName("error") val error: Int,
    @SerialName("msg") val msg: String = ""
)
