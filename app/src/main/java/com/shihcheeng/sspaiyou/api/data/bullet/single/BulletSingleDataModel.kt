package com.shihcheeng.sspaiyou.api.data.bullet.single

import androidx.annotation.Keep
import com.shihcheeng.sspaiyou.api.data.bullet.BulletData
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class BulletSingleDataModel(
    val data: BulletData? = null,
    val error: Int
)
