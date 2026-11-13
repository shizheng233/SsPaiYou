package com.shihcheeng.sspaiyou.api.data.search

import androidx.annotation.Keep
import com.shihcheeng.sspaiyou.api.data.column.ColumnArticle
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class SearchArticleDataModel(
    // Search results use the same article payload as column article lists.
    @SerialName("data") val data: List<ColumnArticle> = emptyList(),
    @SerialName("error") val error: Int,
    @SerialName("msg") val msg: String = "",
    @SerialName("total") val total: Int? = null
)
