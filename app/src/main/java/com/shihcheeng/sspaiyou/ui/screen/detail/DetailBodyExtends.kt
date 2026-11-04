package com.shihcheeng.sspaiyou.ui.screen.detail

import com.shihcheeng.sspaiyou.api.data.actrcle.BodyExtendsComment

data class DetailBodyExtends(
    val id: Int,
    val title: String = "",
    val body: List<DetailContent>,
    val commentOn: Boolean,
    val commentShowOn: Boolean,
    val statusOn: Boolean,
    val statusShowOn: Boolean,
    val commentCount: Int,
    val comments: List<BodyExtendsComment>,
)
