package com.shihcheeng.sspaiyou.ui.screen.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import coil.compose.AsyncImage
import com.shihcheeng.sspaiyou.ui.theme.ReaderBodyMedium
import com.shihcheeng.sspaiyou.ui.theme.ReaderH1Title
import com.shihcheeng.sspaiyou.ui.theme.ReaderH2Title
import com.shihcheeng.sspaiyou.ui.theme.ReaderH3Title
import com.shihcheeng.sspaiyou.ui.theme.colorForMode
import com.shihcheeng.sspaiyou.utils.imageUrlForNullable
import org.jsoup.nodes.Element
import org.jsoup.nodes.Node
import org.jsoup.nodes.TextNode

fun LazyListScope.parserBodyContent(
    darkMode: Boolean,
    list: List<Node>
) {

    val parserInner = DetailBodyText { builder ->
        item {
            val text = builder.toAnnotatedString()
            Text(
                text = text,
                style = ReaderBodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 1.8.em,
            )
        }
    }
    parserInner.parserTags(darkMode, this, list)
    parserInner.terminateCurrentBuilder()
}


fun DetailBodyText.parserTags(
    darkMode: Boolean,
    scoped: LazyListScope,
    list: List<Node>
) {
    var node = list.firstOrNull()
    while (node != null) {
        when (node) {
            is TextNode -> {
                append(node.text().trim())
            }

            is Element -> {
                val element = node
                when (element.tagName()) {
                    "p" -> {
                        if (element.text().isNotEmpty() && element.text().isNotBlank()) {
                            withParagraph {
                                parserTags(darkMode, scoped, element.childNodes())
                            }
                        }
                    }

                    "img" -> {
                        val mediaUrl = element.attr("src")
                        appendImage {
                            scoped.item {
                                AsyncImage(
                                    model = imageUrlForNullable(mediaUrl),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .wrapContentHeight()
                                        .clip(MaterialTheme.shapes.medium),
                                    contentDescription = null,
                                )
                            }
                        }
                    }

                    "hr" -> {
                        appendCompose {
                            scoped.item {
                                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                            }
                        }
                    }

                    "figure" -> {
                        val imageUrl = element.getElementsByTag("img")
                            .first()?.attr("src")
                        val caption = element.attr("figcaption")
                        appendImage {
                            scoped.item {
                                Column(
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    AsyncImage(
                                        model = imageUrlForNullable(imageUrl),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .wrapContentHeight()
                                            .clip(MaterialTheme.shapes.medium),
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop
                                    )
                                    Text(
                                        text = caption,
                                        textAlign = TextAlign.Center,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    "blockquote" -> {
                        withParagraph(
                            paragraphStyle = ParagraphStyle(textIndent = TextIndent(2.em, 2.em))
                        ) {
                            withStyle(
                                SpanStyle(
                                    color = colorForMode(darkMode).copy(0.74f),
                                    fontStyle = FontStyle.Italic
                                )
                            ) {
                                parserTags(darkMode, scoped, element.childNodes())
                            }
                        }
                    }

                    "h1" -> {
                        withParagraph {
                            withStyle(ReaderH1Title.toSpanStyle()) {
                                parserTags(darkMode, scoped, element.childNodes())
                            }
                        }
                    }

                    "h2" -> {
                        withParagraph {
                            withStyle(ReaderH2Title.toSpanStyle()) {
                                parserTags(darkMode, scoped, element.childNodes())
                            }
                        }
                    }

                    "h3" -> {
                        withParagraph {
                            withStyle(ReaderH3Title.toSpanStyle()) {
                                parserTags(darkMode, scoped, element.childNodes())
                            }
                        }
                    }

                    "ul" -> {
                        element.children()
                            .filter { x ->
                                x.tagName() == "li"
                            }.forEach { item ->

                                // append("\n")
                                append('\u2022')
                                append(" ")
                                parserTags(darkMode, scoped, item.childNodes())
                                append('\n')
                            }
                    }

                    "strong" -> {
                        withStyle(
                            SpanStyle(fontWeight = FontWeight.W700)
                        ) {
                            parserTags(darkMode, scoped, element.childNodes())
                        }
                    }

                    "a" -> {
                        withAnnotation(tag = "link", annotation = element.text()) {
                            parserTags(darkMode, scoped, element.childNodes())
                        }
                    }

                    "i" -> {
                        withStyle(SpanStyle(fontStyle = FontStyle.Italic)) {
                            parserTags(darkMode, scoped, element.childNodes())
                        }
                    }
                }
            }
        }
        node = node.nextSibling()
    }
}