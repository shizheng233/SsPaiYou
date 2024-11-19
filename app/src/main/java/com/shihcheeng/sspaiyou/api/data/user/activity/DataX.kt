package com.shihcheeng.sspaiyou.api.data.user.activity


import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonTransformingSerializer

@Keep
@Serializable
data class DataX(
    @SerialName("advertisement_url")
    val advertisementUrl: String = "",
    @SerialName("article_limit_free")
    val articleLimitFree: Boolean = false,
    @SerialName("article_limit_free_etime")
    val articleLimitFreeEtime: Int = 0,
    @SerialName("article_limit_free_stime")
    val articleLimitFreeStime: Int = 0,
    @SerialName("author")
    val author: AuthorX = AuthorX(),
    @SerialName("banner")
    val banner: String = "",
    @SerialName("belong_to_member")
    val belongToMember: Boolean = false,
    @SerialName("comment_count")
    val commentCount: Int = 0,
    @SerialName("corner")
    val corner: Corner = Corner(),
    @SerialName("created_time")
    val createdTime: Int = 0,
    @SerialName("free")
    val free: Boolean = false,
    @SerialName("id")
    val id: Int = 0,
    @SerialName("id_hash")
    val idHash: String = "",
    @SerialName("important")
    val important: Int = 0,
    @SerialName("is_matrix")
    val isMatrix: Boolean = false,
    @SerialName("is_pre_recommend_to_home")
    val isPreRecommendToHome: Boolean = false,
    @SerialName("is_recommend_to_home")
    val isRecommendToHome: Boolean = false,
    @SerialName("issue")
    val issue: String = "",
    @SerialName("like_count")
    val likeCount: Int = 0,
    @SerialName("modify_time")
    val modifyTime: Int = 0,
    @SerialName("object_type")
    val objectType: Int = 0,
    @SerialName("podcast_duration")
    val podcastDuration: Int = 0,
    @SerialName("post_type")
    val postType: Int = 0,
    @SerialName("recommend_to_home_at")
    val recommendToHomeAt: Int = 0,
    @SerialName("released_time")
    val releasedTime: Int = 0,
    @SerialName("slug")
    val slug: String = "",
    @SerialName("status")
    val status: Int = 0,
    @SerialName("summary")
    val summary: String = "",
    @SerialName("tags")
    val tags: List<Tag>? = listOf(),
    @SerialName("title")
    val title: String = "",
    @SerialName("user_member_card_show_on")
    val userMemberCardShowOn: Boolean = false,
    @SerialName("view_count")
    val viewCount: Int = 0
)


data object DataXListChanger : JsonTransformingSerializer<DataX>(DataX.serializer()) {
    override fun transformDeserialize(element: JsonElement): JsonElement {
        return when (element) {
            is JsonArray -> element.first()
            else -> element
        }
    }
}