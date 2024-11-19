package com.shihcheeng.sspaiyou.api.data.actrcle


import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class Author(
    @SerialName("avatar")
    val avatar: String = "",
    @SerialName("bio")
    val bio: String = "",
    @SerialName("followed")
    val followed: Boolean = false,
    @SerialName("id")
    val id: Int = 0,
    @SerialName("nickname")
    val nickname: String = "",
    @SerialName("slug")
    val slug: String = "",
    @SerialName("user_flags")
    val userFlags: List<UserFlag>? = null,
    @SerialName("user_plan_flags")
    val userPlanFlags: List<UserPlanFlag>? = null
)

typealias ArticleAuthor = Author