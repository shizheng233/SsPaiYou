package com.shihcheeng.sspaiyou.utils

import com.vladsch.flexmark.html2md.converter.CustomHtmlNodeRenderer
import com.vladsch.flexmark.html2md.converter.HtmlMarkdownWriter
import com.vladsch.flexmark.html2md.converter.HtmlNodeConverterContext
import com.vladsch.flexmark.html2md.converter.HtmlNodeRenderer
import com.vladsch.flexmark.html2md.converter.HtmlNodeRendererFactory
import com.vladsch.flexmark.html2md.converter.HtmlNodeRendererHandler
import org.jsoup.nodes.Element


class ImageNodeResolve : HtmlNodeRenderer {

    override fun getHtmlNodeRendererHandlers(): MutableSet<HtmlNodeRendererHandler<*>> {
        //Log.d("a", "getHtmlNodeRendererHandlers: ")
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
            val imageUrl = node.getElementsByTag("img")
                .first()?.attr("src")
            val caption = node.attr("figcaption")
            markdown.appendLine()
            markdown.append("![${caption.takeIf { x -> x.isNotEmpty() } ?: "图片"}]")
            markdown.append("($imageUrl)")
            markdown.appendLine()
        }


    }


    companion object {

        fun create(): HtmlNodeRendererFactory = HtmlNodeRendererFactory {
            ImageNodeResolve()
        }
    }

}