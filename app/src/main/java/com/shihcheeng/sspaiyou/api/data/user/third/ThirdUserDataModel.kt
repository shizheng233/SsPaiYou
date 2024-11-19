package com.shihcheeng.sspaiyou.api.data.user.third


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import androidx.annotation.Keep

@Keep
@Serializable
data class ThirdUserDataModel(
    @SerialName("data")
    val `data`: Data = Data(),
    @SerialName("error")
    val error: Int = 0
)