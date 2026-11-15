package com.shihcheeng.sspaiyou.api.data.user.follow

import androidx.annotation.Keep
import com.shihcheeng.sspaiyou.api.data.column.ColumnUser
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class UserFollowedDataModel(
    // Followed users share the public user payload used by column authors/followers.
    @SerialName("data") val data: List<ColumnUser> = emptyList(),
    @SerialName("error") val error: Int,
    @SerialName("msg") val msg: String = "",
    @SerialName("total") val total: Int? = null
)
