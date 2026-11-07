package com.shihcheeng.sspaiyou.ui.screen.detail

fun detailArticleUrl(routeId: String?, articleId: Int): String =
    if (!routeId.isNullOrBlank() && routeId.toIntOrNull() == null) {
        "https://sspai.com/prime/story/$routeId"
    } else {
        "https://sspai.com/post/$articleId"
    }
