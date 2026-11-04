package com.shihcheeng.sspaiyou.api.data.actrcle

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class BodyExtendsComment(
    val id: Int,
    val comment: String,
    val user: InnerUser
) {
    @Keep
    @Serializable
    data class InnerUser(
        val nickname: String,
        val avatar: String
    ) {

    }
}
