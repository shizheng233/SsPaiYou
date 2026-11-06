package com.shihcheeng.sspaiyou.api.data.bullet

import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class BulletVote(
    val id: Int = 0,
    val type: Int = 0,
    val title: String = "",
    val banner: String = "",
    @SerialName("banner_id")
    val bannerId: Int = 0,
    val status: Int = 0,
    @SerialName("created_at")
    val createdAt: Long = 0L,
    @SerialName("updated_at")
    val updatedAt: Long = 0L,
    @SerialName("vote_count")
    val voteCount: Int = 0,
    @SerialName("is_voted")
    val isVoted: Boolean = false
)
