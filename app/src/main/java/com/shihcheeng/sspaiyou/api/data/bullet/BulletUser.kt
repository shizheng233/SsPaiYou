package com.shihcheeng.sspaiyou.api.data.bullet

import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Keep
@Serializable
data class BulletUser(
    @SerialName("id")
    val id: Int = 0,
    @SerialName("id_hash")
    val idHash: String = "",
    @SerialName("slug")
    val slug: String = "",
    @SerialName("avatar")
    val avatar: String = "",
    @SerialName("bio")
    val bio: String = "",
    @SerialName("nickname")
    val nickname: String = "",
    @SerialName("role")
    val role: String = "",
    @SerialName("followed")
    val followed: Boolean = false,
    @SerialName("follow_count")
    val followCount: Int = 0,
    @SerialName("fans_count")
    val fansCount: Int = 0,
    @SerialName("article_count")
    val articleCount: Int = 0,
    @SerialName("user_flags")
    val userFlags: List<BulletBadge> = emptyList(),
    @SerialName("user_badges")
    val userBadges: List<BulletBadge> = emptyList(),
    @SerialName("user_plan_flags")
    val userPlanFlags: List<BulletBadge> = emptyList(),
    @SerialName("user_id_verify_flags")
    val userIdVerifyFlags: List<JsonElement> = emptyList(),
    @SerialName("intro")
    val intro: String = "",
    @SerialName("url")
    val url: String = "",
    @SerialName("recommend_to_home_flag")
    val recommendToHomeFlag: BulletBadge = BulletBadge()
)
