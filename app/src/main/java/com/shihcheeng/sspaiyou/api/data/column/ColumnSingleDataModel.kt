package com.shihcheeng.sspaiyou.api.data.column

import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class ColumnSingleDataModel(
    @SerialName("data") val data: ColumnInfo? = null,
    @SerialName("error") val error: Int,
    @SerialName("msg") val msg: String = ""
)

@Keep
@Serializable
data class ColumnInfo(
    @SerialName("id") val id: Int = 0,
    @SerialName("title") val title: String = "",
    @SerialName("banner") val banner: String = "",
    @SerialName("intro") val intro: String = "",
    @SerialName("chief_editor") val chiefEditor: Author = Author(),
    @SerialName("article_count") val articleCount: Int = 0,
    @SerialName("article_audit_count") val articleAuditCount: Int = 0,
    @SerialName("author_count") val authorCount: Int = 0,
    @SerialName("editor_count") val editorCount: Int = 0,
    @SerialName("follow_count") val followCount: Int = 0,
    @SerialName("follow_users") val followUsers: List<ColumnUser> = emptyList(),
    @SerialName("followed") val followed: Boolean = false,
    @SerialName("accept_article") val acceptArticle: Boolean = false,
    @SerialName("manage_permission") val managePermission: Boolean = false
)
