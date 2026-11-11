package com.shihcheeng.sspaiyou.utils

import com.vladsch.flexmark.html2md.converter.CustomHtmlNodeRenderer
import com.vladsch.flexmark.html2md.converter.HtmlMarkdownWriter
import com.vladsch.flexmark.html2md.converter.HtmlNodeConverterContext
import com.vladsch.flexmark.html2md.converter.HtmlNodeRenderer
import com.vladsch.flexmark.html2md.converter.HtmlNodeRendererFactory
import com.vladsch.flexmark.html2md.converter.HtmlNodeRendererHandler
import org.jsoup.nodes.Element
import kotlin.math.roundToInt


class ImageNodeResolve : HtmlNodeRenderer {

    override fun getHtmlNodeRendererHandlers(): MutableSet<HtmlNodeRendererHandler<*>> {
        return mutableSetOf(
            HtmlNodeRendererHandler(
                /* tagName = */ "figure",
                /* aClass = */ Element::class.java,
                /* adapter = */ FigureProcessor
            ),
        )
    }

    //<figure class=\"image ss-img-wrapper\">
    // <img src=\"https://cdnfile.sspai.com/2026/11/03/25d27bb07be96a78bb3bc0430fcef2a5.jpg?imageView2/2/w/1120/q/90/interlace/1/ignore-error/1/format/webp\" data-original=\"https://cdnfile.sspai.com/2026/11/03/25d27bb07be96a78bb3bc0430fcef2a5.jpg?imageView2/2/format/webp\"/>
    // <figcaption>熊掌记 1.0</figcaption>
    // </figure>

    private object FigureProcessor : CustomHtmlNodeRenderer<Element> {
        override fun render(
            node: Element,
            context: HtmlNodeConverterContext,
            markdown: HtmlMarkdownWriter
        ) {
            // Article editors also wrap tables in <figure class="table">.
            // Let the HTML converter reach the table instead of treating it as an image.
            if (node.selectFirst("table") != null || node.selectFirst("img") == null) {
                context.processUnwrapped(node)
                return
            }
            val images = node.getElementsByTag("img")
            val figcaption = node.getElementsByTag("figcaption")
            if (images.size == 1) {
                val imageUrl = images.first()?.let {
                    it.attr("data-original").ifBlank {
                        it.attr("src")
                    }
                }
                val caption = node.attr("figcaption")
                    .takeIf { x ->
                        x.isNotEmpty() && x.isNotBlank()
                    } ?: figcaption.first()?.text()
                ?: images.first()?.attr("alt")
                markdown.appendLine()
                markdown.append(
                    Element("img")
                        .attr("src", imageUrl.orEmpty())
                        .attr("alt", caption.orEmpty())
                        .outerHtml()
                )
                markdown.append("<br/>")
                markdown.appendLine()
            } else {
                val ratio = (100f / images.size).roundToInt()
                markdown.appendLine()
                images.forEach {
                    val imageUrl = it.attr("data-original").ifBlank { it.attr("src") }
                    val caption = it.attr("alt")
                    markdown.append(
                        Element("img").attr("width", "$ratio%").attr("src", imageUrl)
                            .attr("alt", caption).outerHtml()
                    )
                }
                markdown.append("<br/>")
            }
        }

    }


    companion object {

        fun create(): HtmlNodeRendererFactory = HtmlNodeRendererFactory {
            ImageNodeResolve()
        }
    }

}
