package com.shihcheeng.sspaiyou.api.data.notification

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class NotificationPage<T>(
    val data: List<T>? = null,
    val error: Int,
    val total: Int? = null,
    val msg: String = ""
)
