package com.shihcheeng.sspaiyou.api.data.discover

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class DiscoverPage<T>(
    val error: Int,
    val data: List<T>? = null,
    val total: Int? = null,
    val msg: String = "",
)
