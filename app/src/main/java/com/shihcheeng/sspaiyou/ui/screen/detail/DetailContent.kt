package com.shihcheeng.sspaiyou.ui.screen.detail

/**
 * 详情页正文被切分后的内容块。
 * 文本块交给 Markdown 组件渲染，图片块交给 AsyncImage 渲染，
 * 解决 Markdown 组件无法渲染 flexmark 输出的原生 HTML <img> 标签的问题。
 */
sealed interface DetailContent {

    data class Markdown(val text: String, val heading: DetailHeading? = null) : DetailContent

    data class Image(
        val url: String,
        val caption: String? = null,
        val aspectRatio: Float? = null
    ) : DetailContent
}
