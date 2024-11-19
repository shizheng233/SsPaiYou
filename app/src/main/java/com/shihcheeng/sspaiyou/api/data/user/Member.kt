package com.shihcheeng.sspaiyou.api.data.user


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import androidx.annotation.Keep

@Keep
@Serializable
data class Member(
    @SerialName("banner")
    val banner: String = "",
    @SerialName("end_time")
    val endTime: Int = 0,
    @SerialName("is_expire")
    val isExpire: Boolean = false,
    @SerialName("is_pay")
    val isPay: Boolean = false
)