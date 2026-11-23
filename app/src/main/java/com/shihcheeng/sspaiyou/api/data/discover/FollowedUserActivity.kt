package com.shihcheeng.sspaiyou.api.data.discover

import androidx.annotation.Keep
import com.shihcheeng.sspaiyou.api.data.column.ColumnArticle
import com.shihcheeng.sspaiyou.api.data.column.ColumnUser
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class FollowedUserActivity(
    val key: String,
    val author: ColumnUser,
    @SerialName("data") val article: ColumnArticle,
    @SerialName("created_at") val createdAt: Long,
    val action: String = "",
    val memo: String = "",
)
