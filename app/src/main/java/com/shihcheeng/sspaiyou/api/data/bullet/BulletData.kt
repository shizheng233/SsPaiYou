package com.shihcheeng.sspaiyou.api.data.bullet

import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Keep
@Serializable
data class BulletData(
    @SerialName("title")
    val title: String = "",
    @SerialName("banner")
    val banner: String = "",
    @SerialName("banner_id")
    val bannerId: Int = 0,
    @SerialName("type")
    val type: Int = 0,
    @SerialName("vol")
    val vol: String = "",
    @SerialName("body")
    val body: String = "",
    @SerialName("status")
    val status: Int = 0,
    @SerialName("style_type")
    val styleType: Int = 0,
    @SerialName("vote_title")
    val voteTitle: String = "",
    @SerialName("vote_type")
    val voteType: Int = 0,
    @SerialName("vote_start_time")
    val voteStartTime: Long = 0L,
    @SerialName("vote_end_time")
    val voteEndTime: Long = 0L,
    @SerialName("words_count")
    val wordsCount: Int = 0,
    @SerialName("comment_file_on")
    val commentFileOn: Boolean = false,
    @SerialName("promote_title")
    val promoteTitle: String = "",
    @SerialName("promote_description")
    val promoteDescription: String = "",
    @SerialName("promote_keywords")
    val promoteKeywords: List<JsonElement> = emptyList(),
    @SerialName("promote_banner")
    val promoteBanner: String = "",
    @SerialName("promote_banner_id")
    val promoteBannerId: Int = 0,
    @SerialName("corner_id")
    val cornerId: Int = 0,
    @SerialName("recommend_position_type")
    val recommendPositionType: Int = 0,
    @SerialName("show_ios")
    val showIos: Boolean = false,
    @SerialName("created_at")
    val createdAt: Long = 0L,
    @SerialName("updated_at")
    val updatedAt: Long = 0L,
    @SerialName("released_at")
    val releasedAt: Long = 0L,
    @SerialName("share_banner_id")
    val shareBannerId: Int = 0,
    @SerialName("share_banner")
    val shareBanner: String = "",
    @SerialName("token_created_at")
    val tokenCreatedAt: Long = 0L,
    @SerialName("token")
    val token: String = "",
    @SerialName("co_creation_id")
    val coCreationId: Int = 0,
    @SerialName("list_show")
    val listShow: Boolean = false,
    @SerialName("default_thread_sort_way")
    val defaultThreadSortWay: Int = 0,
    @SerialName("authors")
    val authors: List<BulletUser> = emptyList(),
    @SerialName("tags")
    val tags: List<JsonElement> = emptyList(),
    @SerialName("votes")
    val votes: List<BulletVote> = emptyList(),
    @SerialName("participation_users")
    val participationUsers: List<BulletUser> = emptyList(),
    @SerialName("participations_count")
    val participationsCount: Int = 0,
    @SerialName("threads_count")
    val threadsCount: Int = 0,
    @SerialName("likes_count")
    val likesCount: Int = 0,
    @SerialName("bullets")
    val bullets: List<JsonElement> = emptyList(),
    @SerialName("voted")
    val voted: Boolean = false,
    @SerialName("like_status")
    val likeStatus: Int = 0,
    @SerialName("views_count")
    val viewsCount: Int = 0,
    @SerialName("corner")
    val corner: JsonElement? = null,
    @SerialName("thread_flag")
    val threadFlag: Boolean = false,
    @SerialName("co_creation")
    val coCreation: JsonElement? = null,
    @SerialName("favorited")
    val favorited: Boolean = false
)
