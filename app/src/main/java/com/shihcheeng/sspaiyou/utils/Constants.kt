package com.shihcheeng.sspaiyou.utils

import androidx.core.net.toUri

object Constants {
    const val TYPE_HOME_SUB = "home_sub"
    const val TYPE_HOME_MAIN = "home_main"
    const val IMAGE_HOST_URL = "https://cdnfile.sspai.com/"
    const val MATRIX = "https://sspai.com/matrix"
    const val COLUMN = "https://sspai.com/columns"
    const val MAIN = "https://sspai.com/"
    const val NORMAL_LIMIT = 10
    const val PREFIX_AUTHORIZATION = "Bearer"
    const val SSPAI_AWT_JSON = "sspai_jwt_token"
    const val URL_HOT = "热门文章"
    const val APPLICATION_RECOMMEND = "应用推荐"
    const val STYLE_OF_LIFE = "生活方式"
    const val EFFECTIVE = "效率技巧"
}

fun imageUrlFor(path: String): String {
    return if (path.toUri().scheme == "https") path else "${Constants.IMAGE_HOST_URL}$path"
}

fun imageUrlForNullable(path: String?): String? {
    if (path == null) return null
    val imageUrl: String = if (path.toUri().scheme == "https") {
        path
    } else {
        "${Constants.IMAGE_HOST_URL}$path"
    }
    return imageUrl
}