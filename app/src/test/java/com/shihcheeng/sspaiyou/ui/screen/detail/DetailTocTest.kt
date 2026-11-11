package com.shihcheeng.sspaiyou.ui.screen.detail

import org.junit.Assert.assertEquals
import org.junit.Test

class DetailTocTest {
    @Test
    fun tocIndicesIncludeArticleHeaderAuthorImagesAndAllExtensionHeaders() {
        val parser = DetailMarkDownParser()
        val extensions = listOf(
            extension(1, "扩展章节", parser.parse("<p>导语</p><h3>扩展标题</h3>")),
            extension(2, "", listOf(DetailContent.Image("photo.jpg")))
        )
        val blocks = parser.parse("<p>正文导语</p><h2>重复标题</h2><h2>重复标题</h2>")

        assertEquals(
            listOf(
                DetailTocEntry("文章标题", 1, 0),
                DetailTocEntry("扩展章节", 2, 2),
                DetailTocEntry("扩展标题", 3, 4),
                DetailTocEntry("重复标题", 2, 8),
                DetailTocEntry("重复标题", 2, 9)
            ),
            buildDetailToc("文章标题", blocks, extensions)
        )
    }

    @Test
    fun articleWithoutBodyHeadingsStillHasTitleAnchor() {
        assertEquals(
            listOf(DetailTocEntry("标题", 1, 0)),
            buildDetailToc("标题", listOf(DetailContent.Markdown("正文")), emptyList())
        )
    }

    @Test
    fun shareUrlsUsePublicRoutesForNumericIdsAndMemberSlugs() {
        assertEquals("https://sspai.com/post/12345", detailArticleUrl("12345", 12345))
        assertEquals("https://sspai.com/prime/story/member-story", detailArticleUrl("member-story", 12345))
        assertEquals("https://sspai.com/post/12345", detailArticleUrl(null, 12345))
    }

    private fun extension(id: Int, title: String, body: List<DetailContent>) = DetailBodyExtends(
        id = id,
        title = title,
        body = body,
        commentOn = false,
        commentShowOn = false,
        statusOn = false,
        statusShowOn = false,
        commentCount = 0,
        comments = emptyList()
    )
}
