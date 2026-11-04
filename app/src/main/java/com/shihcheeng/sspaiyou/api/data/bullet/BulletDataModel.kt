package com.shihcheeng.sspaiyou.api.data.bullet

import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class BulletDataModel(
    @SerialName("data")
    val data: List<BulletData> = emptyList(),
    @SerialName("error")
    val error: Int = 0,
    @SerialName("total")
    val total: Int = 0
)
