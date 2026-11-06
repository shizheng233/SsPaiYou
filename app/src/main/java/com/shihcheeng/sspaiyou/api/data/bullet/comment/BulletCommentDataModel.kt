package com.shihcheeng.sspaiyou.api.data.bullet.comment

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class BulletCommentDataModel(
    val data: List<BulletComment> = emptyList(),
    val error: Int,
    val total: Int = 0
)
