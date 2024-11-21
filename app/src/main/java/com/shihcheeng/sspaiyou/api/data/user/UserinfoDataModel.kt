package com.shihcheeng.sspaiyou.api.data.user


import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class UserinfoDataModel(
    @SerialName("data")
    val `data`: Data = Data(),
    @SerialName("error")
    val error: Int = 0
)