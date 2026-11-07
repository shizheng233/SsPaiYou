package com.shihcheeng.sspaiyou.ui.screen.detail

data class DetailHeading(val title: String, val level: Int)

data class DetailTocEntry(val title: String, val level: Int, val itemIndex: Int)

/** Indices follow DetailScreenContent: article title, author, extensions, then body. */
fun buildDetailToc(
    title: String,
    blocks: List<DetailContent>,
    extendsBodys: List<DetailBodyExtends>
): List<DetailTocEntry> = buildList {
    if (title.isNotBlank()) add(DetailTocEntry(title, 1, 0))
    var itemIndex = 2
    fun addHeadings(body: List<DetailContent>) {
        body.forEach { block ->
            val heading = (block as? DetailContent.Markdown)?.heading
            if (heading != null && heading.level in 1..3 && heading.title.isNotBlank()) {
                add(DetailTocEntry(heading.title, heading.level, itemIndex))
            }
            itemIndex++
        }
    }
    extendsBodys.forEach { extension ->
        if (extension.title.isNotBlank()) {
            add(DetailTocEntry(extension.title, 2, itemIndex))
        }
        itemIndex++
        addHeadings(extension.body)
    }
    addHeadings(blocks)
}
