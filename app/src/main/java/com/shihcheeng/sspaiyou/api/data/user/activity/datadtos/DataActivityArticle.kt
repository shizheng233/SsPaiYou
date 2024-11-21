package com.shihcheeng.sspaiyou.api.data.user.activity.datadtos

import androidx.annotation.Keep
import com.shihcheeng.sspaiyou.api.data.user.activity.Author
import com.shihcheeng.sspaiyou.api.data.user.activity.DataX
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class DataActivityArticle(
    val data: DataX = DataX(),
    @SerialName("action")
    val action: String = "",
    @SerialName("author")
    val author: Author = Author(),
    @SerialName("created_at")
    val createdAt: Int = 0,
    @SerialName("delete_status")
    val deleteStatus: Boolean = false,
    @SerialName("id")
    val id: Int = 0,
    @SerialName("key")
    val key: String = "",
    @SerialName("memo")
    val memo: String = "",
    @SerialName("object_type")
    val objectType: Int = 0,
    @SerialName("read")
    val read: Boolean = false,
) : IBaseData

