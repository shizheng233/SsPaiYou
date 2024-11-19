package com.shihcheeng.sspaiyou.api.data.user.activity


import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class Tag(
    @SerialName("articles_count")
    val articlesCount: Int = 0,
    @SerialName("color")
    val color: String = "",
    @SerialName("created_at")
    val createdAt: Int = 0,
    @SerialName("custom_url")
    val customUrl: String = "",
    @SerialName("icon")
    val icon: String = "",
    @SerialName("icon_id")
    val iconId: Int = 0,
    @SerialName("id")
    val id: Int = 0,
    @SerialName("intro")
    val intro: String = "",
    @SerialName("modify_at")
    val modifyAt: Int = 0,
    @SerialName("pai_read_recommend_on")
    val paiReadRecommendOn: Boolean = false,
    @SerialName("recommend")
    val recommend: Boolean = false,
    @SerialName("released_at")
    val releasedAt: Int = 0,
    @SerialName("synonyms")
    val synonyms: String = "",
    @SerialName("title")
    val title: String = "",
    @SerialName("usable_member")
    val usableMember: Boolean = false,
    @SerialName("usable_user")
    val usableUser: Boolean = false,
    @SerialName("views_count")
    val viewsCount: Int = 0,
    @SerialName("weight")
    val weight: Int = 0
)