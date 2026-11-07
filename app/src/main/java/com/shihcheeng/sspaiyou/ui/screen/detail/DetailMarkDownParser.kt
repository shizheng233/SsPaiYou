package com.shihcheeng.sspaiyou.ui.screen.detail

import com.shihcheeng.sspaiyou.utils.ImageResolveConverter
import com.vladsch.flexmark.ast.Heading
import com.vladsch.flexmark.util.ast.TextCollectingVisitor
import com.vladsch.flexmark.html2md.converter.FlexmarkHtmlConverter
import com.vladsch.flexmark.html2md.converter.ExtensionConversion
import com.vladsch.flexmark.html2md.converter.HtmlConverterCoreNodeRendererFactory
import com.vladsch.flexmark.html2md.converter.HtmlNodeRenderer
import com.vladsch.flexmark.html2md.converter.HtmlNodeRendererHandler
import com.vladsch.flexmark.parser.Parser
import com.vladsch.flexmark.util.data.MutableDataSet
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import javax.inject.Inject

/**
 * 将文章 HTML 正文先转换为 Markdown，再切分为 [DetailContent] 列表：
 * 文本段落归为 [DetailContent.Markdown]，图片归为 [DetailContent.Image]。
 *
 * 图片来源有两种形态，都需要识别：
 * 1. `<figure>` 经 [ImageResolveConverter] 转换后输出的原生 `<img>` 标签；
 * 2. 行内 `<img>` 经 flexmark 默认转换后输出的标准 `![](url)` 语法。
 */
class DetailMarkDownParser @Inject constructor() {

    private val markdownParser = Parser.builder().build()

    private val converter = FlexmarkHtmlConverter.builder(
        MutableDataSet().set(Parser.EXTENSIONS, listOf(ImageResolveConverter))
    ).htmlNodeRendererFactory { options ->
        val handlers = HtmlConverterCoreNodeRendererFactory().apply(options)
            .htmlNodeRendererHandlers
            .filter { it.tagName == "strong" || it.tagName == "b" }
            .map { original ->
                HtmlNodeRendererHandler(original.tagName, Element::class.java) { element, context, out ->
                    val addBoundaries = context.htmlConverterOptions.extInlineStrong == ExtensionConversion.MARKDOWN &&
                        !out.isPreFormatted && !context.isInlineCode && element.hasText()
                    // Comments delimit CJK punctuation without adding characters to rendered text.
                    // Never start a paragraph/list item with a comment: it would become an HTML block.
                    if (addBoundaries && element.previousSibling() != null && out.column() > 0) {
                        out.append("<!-- -->")
                    }
                    original.render(element, context, out)
                    if (addBoundaries) out.append("<!-- -->")
                }
            }.toSet()
        HtmlNodeRenderer { handlers }
    }.build()

    fun parse(html: String): List<DetailContent> {
        val imageRatios = Jsoup.parseBodyFragment(html).select("img").associate { image ->
            val url = image.attr("data-original").ifBlank { image.attr("src") }
            val width = image.attr("width").toFloatOrNull()
            val height = image.attr("height").toFloatOrNull()
            val ratio = if (width != null && height != null && width > 0 && height > 0) {
                (width / height).takeIf { it.isFinite() }
            } else null
            url to ratio
        }
        val markdown = converter.convert(html)
        val blocks = mutableListOf<DetailContent>()
        var lastIndex = 0
        imageTokenRegex.findAll(markdown).forEach { match ->
            addMarkdownBlock(blocks, markdown.substring(lastIndex, match.range.first))
            resolveImage(match.value)?.let { image ->
                blocks += image.copy(aspectRatio = imageRatios[image.url])
            }
            lastIndex = match.range.last + 1
        }
        addMarkdownBlock(blocks, markdown.substring(lastIndex))
        return blocks
    }

    private fun addMarkdownBlock(blocks: MutableList<DetailContent>, raw: String) {
        // 清理 figure 转换时夹带的 <br/>，避免残留为可见文本或产生空白块
        // Preserve leading indentation: stripping it can turn a code line into a heading.
        val text = raw.replace(brTagRegex, "").trimEnd().trimStart('\r', '\n')
        if (text.isBlank()) return
        // Only top-level AST headings qualify; hashes inside code, lists and quotes do not.
        val headings = markdownParser.parse(text).children
            .filterIsInstance<Heading>()
            .filter { it.level in 1..3 }
            .toList()
        if (headings.isEmpty()) {
            blocks += DetailContent.Markdown(text)
            return
        }
        text.substring(0, headings.first().startOffset).trimEnd().takeIf(String::isNotBlank)?.let {
            blocks += DetailContent.Markdown(it)
        }
        headings.forEachIndexed { index, heading ->
            val end = headings.getOrNull(index + 1)?.startOffset ?: text.length
            blocks += DetailContent.Markdown(
                text = text.substring(heading.startOffset, end).trim(),
                heading = DetailHeading(
                    title = TextCollectingVisitor().collectAndGetText(heading).trim(),
                    level = heading.level
                )
            )
        }
    }

    private fun resolveImage(token: String): DetailContent.Image? {
        return if (token.startsWith("<img")) {
            val image = Jsoup.parseBodyFragment(token).selectFirst("img") ?: return null
            val url = image.attr("src")
            val caption = image.attr("alt").takeIf(String::isNotBlank)
            if (url.isBlank()) null else DetailContent.Image(url, caption)
        } else {
            val match = mdImageRegex.matchEntire(token) ?: return null
            val caption = match.groupValues[1].takeIf(String::isNotBlank)
            val url = match.groupValues[2]
            if (url.isBlank()) null else DetailContent.Image(url, caption)
        }
    }

    private companion object {
        val imageTokenRegex = Regex("<img\\b[^>]*?/?>|!\\[[^]]*]\\([^)]+\\)")
        val mdImageRegex = Regex("!\\[([^]]*)]\\(([^)\\s]+)[^)]*\\)")
        val brTagRegex = Regex("<br\\s*/?>", RegexOption.IGNORE_CASE)
    }
}
