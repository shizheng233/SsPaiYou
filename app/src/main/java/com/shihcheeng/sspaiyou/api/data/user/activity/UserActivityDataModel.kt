package com.shihcheeng.sspaiyou.api.data.user.activity


import androidx.annotation.Keep
import com.shihcheeng.sspaiyou.api.data.user.activity.datadtos.IBaseData
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class UserActivityDataModel(
    @SerialName("data")
    val `data`: List<IBaseData>? = emptyList(),
    @SerialName("error")
    val error: Int = 0,
    @SerialName("total")
    val total: Int? = null,
    @SerialName("msg")
    val msg: String = ""
)

