package com.shihcheeng.sspaiyou.ui.screen.detail

import org.intellij.markdown.MarkdownElementTypes
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.flavours.gfm.GFMElementTypes
import org.intellij.markdown.flavours.gfm.GFMFlavourDescriptor
import org.intellij.markdown.flavours.gfm.GFMTokenTypes
import org.intellij.markdown.parser.MarkdownParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DetailArticleTableParserTest {
    private val parser = DetailMarkDownParser()

    @Test
    fun allThComparisonTableRendersEveryRowInsteadOfOnlyTheLastHeader() {
        // Minimized from .opencode_temp/error_render.html, retaining all 14 table rows.
        val html = requireNotNull(javaClass.getResource("/all_th_table.html")).readText(Charsets.UTF_8)
        val markdown = markdownOf(parser.parse(html))
        val table = tablesIn(markdown).single()
        val header = table.children.single { it.type == GFMElementTypes.HEADER }
        assertTrue(markdown, markdown.substring(header.startOffset, header.endOffset).contains("核心功能"))
        val rows = table.children.filter { it.type == GFMElementTypes.ROW }
        assertEquals(markdown, 13, rows.size)
        assertTrue(rows.all { it.cells().size == 3 })
        assertTrue(markdown.substring(rows.first().startOffset, rows.first().endOffset).contains("剪贴板历史与内容预览"))
        assertTrue(markdown.substring(rows.last().startOffset, rows.last().endOffset).contains("联网链接预览"))
        assertTrue(markdown.substring(table.endOffset).contains("Pasto 另支持"))
    }

    @Test
    fun multipleTheadRowsBecomeOneGfmHeaderWithoutLosingBoldContentOrLinks() {
        val markdown = markdownOf(parser.parse(
            """<table><thead><tr><th>项目</th><th>说明</th></tr>
                <tr><th>第二层</th><th><a href="https://example.com">链接</a></th></tr></thead>
                <tbody><tr><th>行标题</th><td>正文</td></tr></tbody></table>"""
        ))
        val table = tablesIn(markdown).single()
        assertEquals(markdown, 2, table.children.count { it.type == GFMElementTypes.ROW })
        val header = table.children.single { it.type == GFMElementTypes.HEADER }
        assertTrue(markdown.substring(header.startOffset, header.endOffset).contains("项目"))
        val rows = table.children.filter { it.type == GFMElementTypes.ROW }
        assertTrue(rows.first().descendants().any { it.type == MarkdownElementTypes.INLINE_LINK })
        assertTrue(rows.last().descendants().any { it.type == MarkdownElementTypes.STRONG })
    }

    @Test
    fun htmlTableKeepsHeaderRowsAndInlineFormattingForTheRenderer() {
        val markdown = markdownOf(parser.parse(
            """<table><thead><tr><th>按键</th><th>用途</th></tr></thead><tbody>
                <tr><td><strong>确认</strong></td><td><code>Enter</code></td></tr>
                <tr><td>链接</td><td><a href="https://example.com">说明</a></td></tr>
            </tbody></table>"""
        ))
        val table = tablesIn(markdown).single()

        assertEquals(2, table.children.single { it.type == GFMElementTypes.HEADER }.cells().size)
        assertEquals(2, table.children.count { it.type == GFMElementTypes.ROW })
        val types = table.descendants().map { it.type }.toList()
        assertTrue(types.contains(MarkdownElementTypes.STRONG))
        assertTrue(types.contains(MarkdownElementTypes.CODE_SPAN))
        assertTrue(types.contains(MarkdownElementTypes.INLINE_LINK))
    }

    @Test
    fun figureWrappedTableKeepsItsPositionAndHeadingAnchors() {
        val blocks = parser.parse(
            """<h2>之前</h2><figure class="table"><table>
                <tr><th>按键</th><th>用途</th></tr><tr><td>OK</td><td>确认</td></tr>
            </table></figure><h2>之后</h2><p>结尾</p>"""
        )

        assertEquals(1, tablesIn(markdownOf(blocks)).size)
        assertEquals(listOf("之前", "之后"), blocks.filterIsInstance<DetailContent.Markdown>().mapNotNull { it.heading?.title })
        assertEquals(
            listOf(DetailTocEntry("之前", 2, 2), DetailTocEntry("之后", 2, 3)),
            buildDetailToc("", blocks, emptyList())
        )
        assertTrue((blocks.last() as DetailContent.Markdown).text.contains("结尾"))
    }

    @Test
    fun tableWithoutThKeepsAllDataRows() {
        val markdown = markdownOf(parser.parse(
            """<table><tbody><tr><td>第一行</td><td>A</td></tr><tr><td>第二行</td><td>B</td></tr></tbody></table>"""
        ))
        val table = tablesIn(markdown).single()
        val text = markdown.substring(table.startOffset, table.endOffset)

        assertTrue(text.contains("第一行"))
        assertTrue(text.contains("第二行"))
    }

    @Test
    fun imagesInsideTableStayInCellsWhileArticleImagesRemainSeparate() {
        val blocks = parser.parse(
            """<figure class="table"><table><tr><th>图片</th><th>说明</th></tr>
                <tr><td><img src="cell.png" alt="单元格图片"></td><td>示意</td></tr>
            </table></figure><figure><img src="outside.png" alt="正文图片"></figure>"""
        )
        val markdown = markdownOf(blocks)
        val table = tablesIn(markdown).single()

        assertEquals(listOf("outside.png"), blocks.filterIsInstance<DetailContent.Image>().map { it.url })
        assertEquals(1, table.descendants().count { it.type == MarkdownElementTypes.IMAGE })
        assertTrue(markdown.substring(table.startOffset, table.endOffset).contains("cell.png"))
    }

    @Test
    fun multipleTablesKeepEmptyCellsAndEscapedPipes() {
        val markdown = markdownOf(parser.parse(
            """<table><tr><th>符号</th><th>空值</th></tr><tr><td>A | B</td><td></td></tr></table>
                <p>中间正文</p>
                <figure class="table"><table><tr><th>名称</th></tr><tr><td>第二张表</td></tr></table></figure>"""
        ))
        val tables = tablesIn(markdown)

        assertEquals(2, tables.size)
        assertEquals(2, tables.first().children.single { it.type == GFMElementTypes.ROW }.cells().size)
        assertTrue(markdown.contains("中间正文"))
        assertTrue(markdown.contains("第二张表"))
    }

    @Test
    fun footerAndBodyRowHeadersDoNotLoseData() {
        val markdown = markdownOf(parser.parse(
            """<table><thead><tr><th>项目</th><th>数值</th></tr></thead>
                <tbody><tr><td>A</td><td>1</td></tr><tr><th>B</th><td>2</td></tr></tbody>
                <tfoot><tr><th>合计</th><td>3</td></tr></tfoot></table>"""
        ))
        val table = tablesIn(markdown).single()

        assertEquals(3, table.children.count { it.type == GFMElementTypes.ROW })
        assertTrue(markdown.substring(table.startOffset, table.endOffset).contains("合计"))
    }

    private fun markdownOf(blocks: List<DetailContent>) =
        blocks.filterIsInstance<DetailContent.Markdown>().joinToString("\n\n") { it.text }

    private fun tablesIn(markdown: String): List<ASTNode> =
        MarkdownParser(GFMFlavourDescriptor()).buildMarkdownTreeFromString(markdown)
            .descendants().filter { it.type == GFMElementTypes.TABLE }.toList()

    private fun ASTNode.cells() = children.filter { it.type == GFMTokenTypes.CELL }

    private fun ASTNode.descendants(): Sequence<ASTNode> = sequence {
        yield(this@descendants)
        children.forEach { yieldAll(it.descendants()) }
    }
}
