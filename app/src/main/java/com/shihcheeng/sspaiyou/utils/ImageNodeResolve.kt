package com.shihcheeng.sspaiyou.utils

import android.util.Log
import com.vladsch.flexmark.ast.Reference
import com.vladsch.flexmark.ext.emoji.internal.EmojiShortcuts
import com.vladsch.flexmark.html.renderer.LinkType
import com.vladsch.flexmark.html2md.converter.FlexmarkHtmlConverter
import com.vladsch.flexmark.html2md.converter.HtmlConverterOptions
import com.vladsch.flexmark.html2md.converter.HtmlMarkdownWriter
import com.vladsch.flexmark.html2md.converter.HtmlNodeConverterContext
import com.vladsch.flexmark.html2md.converter.HtmlNodeRenderer
import com.vladsch.flexmark.html2md.converter.HtmlNodeRendererFactory
import com.vladsch.flexmark.html2md.converter.HtmlNodeRendererHandler
import com.vladsch.flexmark.html2md.converter.LinkConversion
import com.vladsch.flexmark.html2md.converter.internal.HtmlConverterCoreNodeRenderer.EMOJI_ALT_PREFIX
import com.vladsch.flexmark.util.data.DataHolder
import org.jsoup.nodes.Element
import java.net.URLDecoder


class ImageNodeResolve(options: DataHolder?) : HtmlNodeRenderer {

    private val _htmlConverterOptions = HtmlConverterOptions(options)

    override fun getHtmlNodeRendererHandlers(): MutableSet<HtmlNodeRendererHandler<*>> {
        Log.d("a", "getHtmlNodeRendererHandlers: ")
        return mutableSetOf(
            HtmlNodeRendererHandler(
                FlexmarkHtmlConverter.IMG_NODE,
                Element::class.java,
                this::processImg
            ),
        )
    }

    private fun processImg(
        element: Element,
        context: HtmlNodeConverterContext,
        out: HtmlMarkdownWriter
    ) {
        // see if it is an anchor or a link
        out.append("\n\n")
        if (element.hasAttr("src")) {
            val src = element.attr("src")
            var emoji = EmojiShortcuts.getEmojiFromURI(src)

            // see if this is an emoji from Apple mail from pasted Markdown Navigator HTML mime
            if (emoji == null && element.hasAttr("figcaption")) {
                val emojiAlt = element.attr("figcaption")
                if (emojiAlt.startsWith(EMOJI_ALT_PREFIX)) {
                    // see if the full attribute is emoji
                    val pos = emojiAlt.indexOf(":", EMOJI_ALT_PREFIX.length)
                    if (pos > 0) {
                        // NOTE: category is not significant since it can change, so it is ignored.
                        // String category = emojiAlt.substring(EMOJI_ALT_PREFIX.length(), pos);
                        val shortcut = emojiAlt.substring(pos + 1)
                        emoji = EmojiShortcuts.getEmojiFromShortcut(shortcut)
                    }
                }
            }

            if (emoji?.shortcut != null) {
                out.append(':').append(emoji.shortcut).append(':')
            } else {
                val conv: LinkConversion = _htmlConverterOptions.extInlineImage
                if (conv.isSuppressed) return

                if (conv.isParsed) {
                    var alt = if (!element.hasAttr("figcaption"))
                        null
                    else element.attr("figcaption")
                        .trim { it <= ' ' }
                        .replace("[", "\\[")
                        .replace("]", "\\]")

                    if (alt != null && alt.isEmpty()) alt = null

                    var title = if (!element.hasAttr("title"))
                        null
                    else element.attr("title")
                        .replace("\n", _htmlConverterOptions.eolInTitleAttribute)
                        .replace("\"", "\\\"")
                    if (title != null && title.isEmpty()) title = null

                    if (!conv.isTextOnly) {
                        val resolvedLink = context.resolveLink(LinkType.IMAGE, src, false)
                        val useSrc = resolvedLink.url
                        val pos = useSrc.indexOf('?')
                        val eol = if (pos < 0) pos else useSrc.indexOf("%0A", pos)
                        val isMultiLineUrl = pos > 0 && eol > 0
                        var handled = false

                        if (conv.isReference && !isMultiLineUrl) {
                            val reference: Reference? = context.getOrCreateReference(
                                useSrc,
                                alt ?: "image", title
                            )
                            if (reference != null) {
                                handled = true

                                if (alt == null || reference.reference.equals(alt)) {
                                    // use reference as is
                                    out.append("![").append(reference.reference).append("][]")
                                } else {
                                    out.append("![").append(alt).append("][")
                                        .append(reference.reference).append("]")
                                }
                            }
                        }

                        if (!handled) {
                            out.append("![")
                            if (alt != null) out.append(alt)
                            out.append(']').append('(')

                            if (isMultiLineUrl) {
                                out.append(useSrc, 0, pos + 1)
                                val decoded: String = URLDecoder.decode(
                                    useSrc.substring(pos + 1).replace("+", "%2B"),
                                    "UTF8"
                                )
                                out.line().append(decoded)
                            } else {
                                out.append(useSrc)
                            }

                            if (title != null) out.append(" \"").append(title).append('"')
                            out.append(")")
                        }
                    } else {
                        if (alt != null) out.append(alt)
                        else if (title != null) out.append(title)
                    }
                } else if (!conv.isSuppressed) {
                    context.processWrapped(element, null, false)
                }
            }
        }
        out.append("\n\n")
    }


    companion object {
        object Factory : HtmlNodeRendererFactory {
            override fun apply(options: DataHolder?): HtmlNodeRenderer {
                return ImageNodeResolve(options)
            }
        }
    }

}