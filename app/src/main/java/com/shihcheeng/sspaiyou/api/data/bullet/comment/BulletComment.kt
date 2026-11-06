package com.shihcheeng.sspaiyou.api.data.bullet.comment

import androidx.annotation.Keep
import com.shihcheeng.sspaiyou.api.data.bullet.BulletUser
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Keep
@Serializable
data class BulletComment(
    @SerialName("id_hash")
    val idHash: String = "",
    val body: String = "",
    val status: Int = 0,
    val blocked: Boolean = false,
    val top: Boolean = false,
    @SerialName("top_at")
    val topAt: Long = 0L,
    val chosen: Boolean = false,
    @SerialName("chosen_at")
    val chosenAt: Long = 0L,
    @SerialName("views_count_web")
    val viewsCountWeb: Int = 0,
    @SerialName("views_count_ios")
    val viewsCountIos: Int = 0,
    @SerialName("created_at")
    val createdAt: Long = 0L,
    @SerialName("client_admin_id")
    val clientAdminId: Int = 0,
    @SerialName("thread_flag")
    val threadFlag: Boolean = false,
    val threads: JsonElement? = null,
    val author: BulletUser = BulletUser(),
    @SerialName("comments_count")
    val commentsCount: Int = 0,
    @SerialName("likes_count")
    val likesCount: Int = 0,
    @SerialName("hates_count")
    val hatesCount: Int = 0,
    @SerialName("like_status")
    val likeStatus: Int = 0,
    val files: List<BulletCommentFile> = emptyList(),
    val flags: List<JsonElement> = emptyList(),
    @SerialName("bullet_created_at")
    val bulletCreatedAt: Long = 0L,
    val comments: JsonElement? = null,
    @SerialName("views_count")
    val viewsCount: Int = 0
)
