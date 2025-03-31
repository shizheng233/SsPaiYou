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


    private object FigureProcessor : CustomHtmlNodeRenderer<Element> {
        override fun render(
            node: Element,
            context: HtmlNodeConverterContext,
            markdown: HtmlMarkdownWriter
        ) {
            val images = node.getElementsByTag("img")
            if (images.size == 1) {
                val imageUrl = images.first()?.attr("data-original")
                val caption = node.attr("figcaption").takeIf { x ->
                    x.isNotEmpty() && x.isNotBlank()
                } ?: images.first()?.attr("alt")
                markdown.appendLine()
                markdown.append("<img width=\"100%\" src=\"${imageUrl}\" alt=\"${caption}\"/><br/>")
                markdown.appendLine()
            } else {
                val ratio = (100f / images.size).roundToInt()
                markdown.appendLine()
                images.forEach {
                    val imageUrl = it.attr("data-original")
                    val caption = it.attr("alt")
                    markdown.append("<img width=\"${ratio}%\" src=\"${imageUrl}\" alt=\"${caption}\"/>")
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