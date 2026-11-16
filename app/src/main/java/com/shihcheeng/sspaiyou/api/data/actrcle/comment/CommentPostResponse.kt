package com.shihcheeng.sspaiyou.api.data.actrcle.comment

import kotlinx.serialization.Serializable

@Serializable
data class CommentPostResponse(
    val data: CommentPostResponseData,
    val error: Int,
) {

    @Serializable
    data class CommentPostResponseData(
        val id: Int,
        val keywords: List<String> = emptyList(),
        val status: Int,
        val success: Boolean
    )

}