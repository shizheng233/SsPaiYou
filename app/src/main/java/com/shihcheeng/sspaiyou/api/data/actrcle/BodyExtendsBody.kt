package com.shihcheeng.sspaiyou.api.data.actrcle

import androidx.annotation.Keep
import com.shihcheeng.sspaiyou.ui.screen.detail.DetailBodyExtends
import com.shihcheeng.sspaiyou.ui.screen.detail.DetailMarkDownParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

//TODO: Add the statuses later.
@Keep
@Serializable
data class BodyExtendsBody(
    val id: Int,
    val title: String = "",
    val body: String,
    @SerialName("comment_on")
    val commentOn: Boolean,
    @SerialName("comment_show_on")
    val commentShowOn: Boolean,
    @SerialName("status_on")
    val statusOn: Boolean,
    @SerialName("status_show_on")
    val statusShowOn: Boolean,
    @SerialName("comment_count")
    val commentCount: Int,
    val comments: List<BodyExtendsComment>,
    //val statuses: List<Body>
) {
    suspend fun hyperTransformToMD(
        parser: DetailMarkDownParser
    ): DetailBodyExtends = DetailBodyExtends(
        id = id,
        title = title,
        body = withContext(Dispatchers.Default) {
            parser.parse(body)
        },
        commentOn = commentOn,
        commentShowOn = commentShowOn,
        statusOn = statusOn,
        statusShowOn = statusShowOn,
        commentCount = commentCount,
        comments = comments
    )
}