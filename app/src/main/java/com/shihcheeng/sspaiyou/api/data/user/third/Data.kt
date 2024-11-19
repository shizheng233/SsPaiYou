package com.shihcheeng.sspaiyou.api.data.user.third


import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class Data(
    @SerialName("article_view_count")
    val articleViewCount: Int = 0,
    @SerialName("articles_word_count")
    val articlesWordCount: Int = 0,
    @SerialName("avatar")
    val avatar: String = "",
    @SerialName("avatar_id")
    val avatarId: Int = 0,
    @SerialName("banned_until")
    val bannedUntil: Int = 0,
    @SerialName("bio")
    val bio: String = "",
    @SerialName("created_at")
    val createdAt: Int = 0,
    @SerialName("followed")
    val followed: Boolean = false,
    @SerialName("followed_count")
    val followedCount: Int = 0,
    @SerialName("following_count")
    val followingCount: Int = 0,
    @SerialName("id")
    val id: Int = 0,
    @SerialName("id_hash")
    val idHash: String = "",
    @SerialName("key")
    val key: String = "",
    @SerialName("liked_count")
    val likedCount: Int = 0,
    @SerialName("matrix_article_count")
    val matrixArticleCount: Int = 0,
    @SerialName("nickname")
    val nickname: String = "",
    @SerialName("power_plus_flag")
    val powerPlusFlag: Int = 0,
    @SerialName("recommend_article_count")
    val recommendArticleCount: Int = 0,
    @SerialName("recommend_to_home_article_count")
    val recommendToHomeArticleCount: Int = 0,
    @SerialName("released_article_count")
    val releasedArticleCount: Int = 0,
    @SerialName("slug")
    val slug: String = "",
    @SerialName("social_networks")
    val socialNetworks: List<SocialNetwork> = listOf(),
    @SerialName("special_column_count")
    val specialColumnCount: Int = 0,
    @SerialName("special_column_writer")
    val specialColumnWriter: Boolean = false,
    @SerialName("user_badges")
    val userBadges: List<UserBadge> = listOf(),
    @SerialName("user_flags")
    val userFlags: List<UserFlag> = listOf(),
    @SerialName("user_plan_flags")
    val userPlanFlags: List<UserPlanFlag> = listOf(),
    @SerialName("user_reward_badges")
    val userRewardBadges: List<UserRewardBadge> = listOf()
)

val ThirdUSerData = Data