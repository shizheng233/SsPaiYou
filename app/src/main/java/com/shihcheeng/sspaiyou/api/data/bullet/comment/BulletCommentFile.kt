package com.shihcheeng.sspaiyou.api.data.bullet.comment

import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class BulletCommentFile(
    val id: Int = 0,
    @SerialName("file_path")
    val filePath: String = ""
)
