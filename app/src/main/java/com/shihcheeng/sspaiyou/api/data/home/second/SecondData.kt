package com.shihcheeng.sspaiyou.api.data.home.second

import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class SecondData(
    @SerialName("mains")
    val mains: List<MainsItem>
)
