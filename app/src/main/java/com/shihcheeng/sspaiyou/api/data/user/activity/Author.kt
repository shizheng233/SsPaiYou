package com.shihcheeng.sspaiyou.api.data.user.activity


import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class Author(
    @SerialName("article_count")
    val articleCount: Int = 0,
    @SerialName("avatar")
    val avatar: String = "",
    @SerialName("bio")
    val bio: String = "",
    @SerialName("fans_count")
    val fansCount: Int = 0,
    @SerialName("follow_count")
    val followCount: Int = 0,
    @SerialName("followed")
    val followed: Boolean = false,
    @SerialName("id")
    val id: Int = 0,
    @SerialName("id_hash")
    val idHash: String = "",
    @SerialName("intro")
    val intro: String = "",
    @SerialName("nickname")
    val nickname: String = "",
    @SerialName("role")
    val role: String = "",
    @SerialName("slug")
    val slug: String = "",
    @SerialName("url")
    val url: String = "",
    @SerialName("user_flags")
    val userFlags: List<UserFlag> = listOf(),
    @SerialName("user_plan_flags")
    val userPlanFlags: List<UserPlanFlag> = listOf()
)