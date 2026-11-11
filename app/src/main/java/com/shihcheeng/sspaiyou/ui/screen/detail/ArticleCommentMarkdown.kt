package com.shihcheeng.sspaiyou.ui.screen.detail

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.mikepenz.markdown.compose.Markdown
import com.mikepenz.markdown.model.DefaultMarkdownTypography
import com.mikepenz.markdown.model.markdownAnnotator
import com.mikepenz.markdown.model.markdownAnnotatorConfig
import com.mikepenz.markdown.model.markdownPadding
import com.shihcheeng.sspaiyou.ui.theme.toMarkDownColors
import com.vladsch.flexmark.html2md.converter.FlexmarkHtmlConverter
import com.vladsch.flexmark.util.data.MutableDataSet
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.nodes.TextNode

@Composable
internal fun ArticleCommentMarkdown(content: String, modifier: Modifier = Modifier) {
    val markdown = remember(content) { articleCommentMarkdown(content) }
    val typography = MaterialTheme.typography
    val body = typography.bodyMedium
    Markdown(
        content = markdown,
        modifier = modifier,
        colors = MaterialTheme.colorScheme.toMarkDownColors(),
        typography = DefaultMarkdownTypography(
            h1 = typography.titleLarge,
            h2 = typography.titleMedium,
            h3 = typography.titleSmall,
            h4 = typography.titleSmall,
            h5 = typography.titleSmall,
            h6 = typography.titleSmall,
            text = body,
            code = body.copy(fontFamily = FontFamily.Monospace),
            inlineCode = body.copy(fontFamily = FontFamily.Monospace),
            quote = body,
            paragraph = body,
            ordered = body,
            bullet = body,
            list = body,
            textLink = TextLinkStyles(
                SpanStyle(
                    color = MaterialTheme.colorScheme.primary,
                    textDecoration = TextDecoration.Underline
                )
            ),
            table = body
        ),
        padding = markdownPadding(block = 4.dp),
        annotator = markdownAnnotator(config = markdownAnnotatorConfig(eolAsNewLine = true))
    )
}

/** API comments mix plain text/newlines with HTML anchors and paragraph spacers. */
internal fun articleCommentMarkdown(content: String): String {
    val text = content.replace("\r\n", "\n").trim()
    val document = Jsoup.parseBodyFragment(text)
    // Preserve already-authored Markdown (including autolinks) rather than HTML-escaping it.
    if (document.select("a, p, br, div, span, strong, b, em, i, blockquote, pre, code, ul, ol, li, img").isEmpty()) {
        return text
    }
    document.outputSettings().prettyPrint(false)
    document.body().getAllElements().forEach { element ->
        if (element.closest("pre, code") != null) return@forEach
        element.textNodes().toList().forEach { node ->
            if ('\n' in node.wholeText) {
                node.wholeText.split('\n').forEachIndexed { index, line ->
                    if (index > 0) node.before(Element("br"))
                    node.before(TextNode(line))
                }
                node.remove()
            }
        }
    }
    return commentHtmlConverter.convert(document.body().html()).trim()
}

private val commentHtmlConverter: FlexmarkHtmlConverter by lazy {
    FlexmarkHtmlConverter.builder(
        MutableDataSet()
            .set(FlexmarkHtmlConverter.SKIP_CHAR_ESCAPE, true)
            .set(FlexmarkHtmlConverter.BR_AS_PARA_BREAKS, false)
            .set(FlexmarkHtmlConverter.BR_AS_EXTRA_BLANK_LINES, false)
    ).build()
}
