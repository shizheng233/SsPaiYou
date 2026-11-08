package com.shihcheeng.sspaiyou.ui.screen.detail

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
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
    fun headingsBecomeSeparateAnchorsWithPlainTextAndOnlyThreeLevels() {
        val blocks = parser.parse(
            """<p>导语</p><h1>主标题</h1><p>正文一</p><h2><strong>二级</strong> <a href="https://example.com">链接</a></h2><p>正文二</p><h3>三级</h3><h4>四级</h4><p>结尾</p>"""
        ).filterIsInstance<DetailContent.Markdown>()

        assertEquals(4, blocks.size)
        assertNull(blocks.first().heading)
        assertEquals(
            listOf(DetailHeading("主标题", 1), DetailHeading("二级 链接", 2), DetailHeading("三级", 3)),
            blocks.mapNotNull { it.heading }
        )
        assertTrue(blocks[1].text.contains("正文一"))
        assertTrue(blocks.last().text.contains("四级"))
        assertTrue(blocks.last().text.contains("结尾"))
    }

    @Test
    fun jsonEscapedNumberedHeadingsKeepTheirLevelsInTocAndRenderer() {
        val json = """{"body":"\u003ch1\u003e1. 总览\u003c/h1\u003e\u003ch2\u003e0. 为什么想给 AI 编程配个手持遥控器？\u003c/h2\u003e\u003cp\u003e正文\u003c/p\u003e\u003ch2\u003e1. 硬件细节：为什么选 RC003？\u003c/h2\u003e\u003ch2\u003e2. 软件配置：打通系统级语音输入\u003c/h2\u003e\u003ch3\u003e2.1 macOS 平台配置流程\u003c/h3\u003e"}"""
        val body = Json.parseToJsonElement(json).jsonObject.getValue("body").jsonPrimitive.content
        val blocks = parser.parse(body).filterIsInstance<DetailContent.Markdown>()
        val expected = listOf(
            DetailHeading("1. 总览", 1),
            DetailHeading("0. 为什么想给 AI 编程配个手持遥控器？", 2),
            DetailHeading("1. 硬件细节：为什么选 RC003？", 2),
            DetailHeading("2. 软件配置：打通系统级语音输入", 2),
            DetailHeading("2.1 macOS 平台配置流程", 3)
        )

        assertEquals(expected, blocks.mapNotNull { it.heading })
        blocks.zip(expected).forEach { (block, heading) ->
            val tree = MarkdownParser(GFMFlavourDescriptor()).buildMarkdownTreeFromString(block.text)
            val expectedType = when (heading.level) {
                1 -> MarkdownElementTypes.ATX_1
                2 -> MarkdownElementTypes.ATX_2
                else -> MarkdownElementTypes.ATX_3
            }
            assertEquals(expectedType, tree.children.first().type)
            assertTrue(tree.descendants().none { it.type == MarkdownElementTypes.ORDERED_LIST })
        }
        assertTrue(blocks[1].text.contains("正文"))
        assertEquals(
            expected.mapIndexed { index, heading -> DetailTocEntry(heading.title, heading.level, index + 2) },
            buildDetailToc("", blocks, emptyList())
        )
    }

    @Test
    fun codeAndQuotedHeadingsDoNotCreateTocAnchors() {
        val blocks = parser.parse(
            """<pre><code>## 不是标题
### 仍然是代码</code></pre><blockquote><h2>引用内标题</h2></blockquote><h2>真正标题</h2>"""
        ).filterIsInstance<DetailContent.Markdown>()

        assertEquals(listOf(DetailHeading("真正标题", 2)), blocks.mapNotNull { it.heading })
        assertTrue(blocks.first().text.contains("不是标题"))
        assertTrue(blocks.first().text.contains("引用内标题"))
    }

    @Test
    fun imagesBetweenHeadingsKeepTheirOwnBlocksAndAnchors() {
        val blocks = parser.parse(
            """<h2>重复标题</h2><p>之前</p><figure><img src="photo.jpg"></figure><p>之后</p><h2>重复标题</h2><p>第二节</p>"""
        )

        assertEquals(listOf(0, 3), blocks.indices.filter {
            (blocks[it] as? DetailContent.Markdown)?.heading != null
        })
        assertTrue(blocks[1] is DetailContent.Image)
        assertTrue((blocks[2] as DetailContent.Markdown).text.contains("之后"))
    }

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
