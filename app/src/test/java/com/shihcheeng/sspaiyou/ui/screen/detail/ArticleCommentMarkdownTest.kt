package com.shihcheeng.sspaiyou.ui.screen.detail

import com.shihcheeng.sspaiyou.api.data.actrcle.comment.ArticleCommentDataModel
import kotlinx.serialization.json.Json
import org.intellij.markdown.MarkdownElementTypes
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.flavours.gfm.GFMFlavourDescriptor
import org.intellij.markdown.parser.MarkdownParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ArticleCommentMarkdownTest {
    @Test
    fun suppliedHtmlLinksBecomeMarkdownLinksAndSpacerSeparatesParagraphs() {
        val response = Json.decodeFromString<ArticleCommentDataModel>(
            requireNotNull(javaClass.getResource("/comment_has_reply.json")).readText()
        )
        listOf(433902, 433901).forEach { id ->
            val markdown = articleCommentMarkdown(response.data.single { it.id == id }.comment)
            assertFalse(markdown.contains("<a "))
            assertFalse(markdown.contains("<p"))
            val tree = MarkdownParser(GFMFlavourDescriptor()).buildMarkdownTreeFromString(markdown)
            assertTrue(tree.descendants().any {
                it.type == MarkdownElementTypes.INLINE_LINK || it.type == MarkdownElementTypes.AUTOLINK
            })
        }
        val paragraphs = articleCommentMarkdown(response.data.single { it.id == 433901 }.comment)
        assertTrue(paragraphs.contains("扫雷。\n"))
        assertTrue(paragraphs.contains("而且它对于二选一问题有所优化"))
        val reply = response.data.last().reply.single()
        assertEquals(reply.comment, articleCommentMarkdown(reply.comment))
    }

    @Test
    fun htmlLinkKeepsLabelDestinationAndSurroundingLineBreaks() {
        val markdown = articleCommentMarkdown(
            "第一行\n<a href=\"https://example.com/?a=1&amp;b=2\">阅读指南</a><br>最后一行"
        )
        assertTrue(markdown.contains("[阅读指南](https://example.com/?a=1&b=2)"))
        assertTrue(markdown.indexOf('\n') in 3 until markdown.indexOf("[阅读指南]"))
        assertTrue(markdown.substringAfter(")").contains('\n'))
        assertFalse(markdown.contains("<br"))
    }

    @Test
    fun plainTextMarkdownEmojiAndAutolinksAreNotEscapedOrCollapsed() {
        listOf(
            "第一行😆\n第二行",
            "**重点** [链接](https://example.com)\n`code`",
            "<https://example.com>",
            "```\nval value = 1\n```",
            ""
        ).forEach { source ->
            assertEquals(source, articleCommentMarkdown(source))
        }
    }

    private fun ASTNode.descendants(): Sequence<ASTNode> = sequence {
        yield(this@descendants)
        children.forEach { yieldAll(it.descendants()) }
    }
}
