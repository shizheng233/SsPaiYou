package com.shihcheeng.sspaiyou.ui.screen.detail

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.intellij.markdown.MarkdownElementTypes
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.flavours.gfm.GFMFlavourDescriptor
import org.intellij.markdown.parser.MarkdownParser

class DetailMarkDownParserTest {
    private val parser = DetailMarkDownParser()

    @Test
    fun chineseQuotesInsideStrongRemainBoldWhenTouchingChineseProse() {
        val html = """<p style="margin-left:0px;">Apple 在 2015 年推出了 SF 系列西文字体（一般使用 SF Pro）和苹方系列中文字体。然而打开 Apple 简体中文官网，可以发现字体栈中第一个是一款名为<strong>「SF Pro SC」</strong>的字体。</p>"""
        val markdown = parser.parse(html).filterIsInstance<DetailContent.Markdown>().single().text
        val tree = MarkdownParser(GFMFlavourDescriptor()).buildMarkdownTreeFromString(markdown)
        val strong = tree.descendants().filter { it.type == MarkdownElementTypes.STRONG }.single()

        assertEquals("**「SF Pro SC」**", markdown.substring(strong.startOffset, strong.endOffset))
    }

    @Test
    fun quotedStrongAtParagraphAndListStartsDoesNotBecomeHtmlBlock() {
        val html = """<p><strong>「段首」</strong>的字体。</p><ul><li><b>「列表」</b>的字体。</li></ul>"""
        val markdown = parser.parse(html).filterIsInstance<DetailContent.Markdown>().joinToString("\n\n") { it.text }
        val nodes = MarkdownParser(GFMFlavourDescriptor()).buildMarkdownTreeFromString(markdown).descendants().toList()

        assertEquals(2, nodes.count { it.type == MarkdownElementTypes.STRONG })
        assertTrue(nodes.none { it.type == MarkdownElementTypes.HTML_BLOCK })
    }

    @Test
    fun strongBoundaryWorkaroundDoesNotChangeCodeOrLiteralAsterisks() {
        val html = """<p><code>**literal**</code></p><pre><strong>**code**</strong></pre><p>**literal**</p>"""
        val markdown = parser.parse(html).filterIsInstance<DetailContent.Markdown>().joinToString("\n\n") { it.text }
        val nodes = MarkdownParser(GFMFlavourDescriptor()).buildMarkdownTreeFromString(markdown).descendants().toList()

        assertTrue(!markdown.contains("<!-- -->"))
        assertTrue(nodes.none { it.type == MarkdownElementTypes.STRONG })
    }

    @Test
    fun htmlStrongRemainsMarkdownStrongIncludingChineseAndLinks() {
        val blocks = parser.parse(
            """<p>Before <strong>bold</strong> after.</p><p><strong>中文粗体</strong></p><p><a href="https://example.com"><strong>Bold link</strong></a></p>"""
        ).filterIsInstance<DetailContent.Markdown>()
        val markdown = blocks.joinToString("\n\n") { it.text }
        val tree = MarkdownParser(GFMFlavourDescriptor()).buildMarkdownTreeFromString(markdown)
        val strongNodes = tree.descendants().filter { it.type == MarkdownElementTypes.STRONG }.toList()

        assertEquals(3, strongNodes.size)
        assertTrue(strongNodes.any { markdown.substring(it.startOffset, it.endOffset).contains("中文粗体") })
        assertTrue(strongNodes.any { markdown.substring(it.startOffset, it.endOffset).contains("Bold link") })
    }

    private fun ASTNode.descendants(): Sequence<ASTNode> = sequence {
        yield(this@descendants)
        children.forEach { yieldAll(it.descendants()) }
    }

    @Test
    fun figureWithSrcPreservesUrlCaptionAndDimensions() {
        val image = parser.parse(
            """<figure><img src="https://example.com/photo?a=1&amp;b=2" alt="A &amp; B" width="800" height="400"></figure>"""
        ).filterIsInstance<DetailContent.Image>().single()

        assertEquals("https://example.com/photo?a=1&b=2", image.url)
        assertEquals("A & B", image.caption)
        assertEquals(2f, image.aspectRatio!!, 0.001f)
    }

    @Test
    fun lazyImageUsesOriginalUrlAndKeepsSurroundingText() {
        val blocks = parser.parse(
            """<p>Before</p><figure><img src="preview.jpg" data-original="full.jpg" width="400" height="800"></figure><p>After</p>"""
        )
        val image = blocks.filterIsInstance<DetailContent.Image>().single()

        assertEquals("full.jpg", image.url)
        assertEquals(0.5f, image.aspectRatio!!, 0.001f)
        assertEquals(listOf("Before", "After"), blocks.filterIsInstance<DetailContent.Markdown>().map { it.text })
    }

    @Test
    fun percentageOrInvalidDimensionsDoNotBecomeAspectRatios() {
        val images = parser.parse(
            """<figure><img src="first.jpg" width="100%" height="400"><img src="second.jpg" width="400" height="0"></figure>"""
        ).filterIsInstance<DetailContent.Image>()

        assertEquals(2, images.size)
        images.forEach { assertNull(it.aspectRatio) }
    }
}
