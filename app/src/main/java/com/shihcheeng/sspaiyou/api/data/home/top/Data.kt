package com.shihcheeng.sspaiyou.api.data.home.top


import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class Data(
    @SerialName("ad_api")
    val adApi: String = "",
    @SerialName("ad_type")
    val adType: Int = 0,
    @SerialName("code")
    val code: String = "",
    @SerialName("created_at")
    val createdAt: Int = 0,
    @SerialName("id")
    val id: Int = 0,
    @SerialName("image")
    val image: String = "",
    @SerialName("memo")
    val memo: String = "",
    @SerialName("redirect_short_url")
    val redirectShortUrl: String = "",
    @SerialName("title")
    val title: String = "",
    @SerialName("title_sub")
    val titleSub: String = "",
    @SerialName("type")
    val type: Int = 0,
    @SerialName("url")
    val url: String = "",
    @SerialName("url_scheme")
    val urlScheme: String = "",
    @SerialName("weight")
    val weight: Int = 0
)

typealias HeaderModel = Data