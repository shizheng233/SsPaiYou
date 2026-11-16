package com.shihcheeng.sspaiyou.api.data.actrcle.comment

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * @param commentFiles 没有需求，故而不知道请求体是什么
 */
@Serializable
data class CommentPostRequest(
    @SerialName("article_id") val articleId: Int,
    @SerialName("comment") val comment: String,
    @SerialName("comment_files") val commentFiles: List<String> = emptyList(),
) {
}