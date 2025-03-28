package com.shihcheeng.sspaiyou.ui.screen.detail

import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.window.Popup
import coil.compose.AsyncImage
import com.mikepenz.markdown.annotator.annotatorSettings
import com.mikepenz.markdown.annotator.buildMarkdownAnnotatedString
import com.mikepenz.markdown.compose.LocalImageTransformer
import com.mikepenz.markdown.compose.LocalMarkdownAnimations
import com.mikepenz.markdown.compose.LocalMarkdownColors
import com.mikepenz.markdown.compose.LocalMarkdownExtendedSpans
import com.mikepenz.markdown.compose.LocalMarkdownTypography
import com.mikepenz.markdown.compose.elements.material.MarkdownBasicText
import com.mikepenz.markdown.compose.extendedspans.ExtendedSpans
import com.mikepenz.markdown.compose.extendedspans.drawBehind
import com.mikepenz.markdown.model.MarkdownImageState
import com.mikepenz.markdown.model.PlaceholderConfig
import com.mikepenz.markdown.utils.MARKDOWN_TAG_IMAGE_URL
import com.shihcheeng.sspaiyou.ui.compoents.rememberMarkdownImageState
import org.intellij.markdown.IElementType
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.ast.findChildOfType


@Composable
fun MarkdownText(
    content: String,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalMarkdownTypography.current.text,
) {
    MarkdownText(AnnotatedString(content), modifier, style)
}

@Composable
fun MarkdownText(
    content: String,
    node: ASTNode,
    style: TextStyle,
    modifier: Modifier = Modifier,
    contentChildType: IElementType? = null,
) {
    val annotatorSettings = annotatorSettings()
    val childNode = contentChildType?.run(node::findChildOfType) ?: node

    val styledText = buildAnnotatedString {
        pushStyle(style.toSpanStyle())
        buildMarkdownAnnotatedString(
            content = content,
            node = childNode,
            annotatorSettings = annotatorSettings
        )
        pop()
    }

    MarkdownText(styledText, modifier = modifier, style = style)
}

@Composable
fun MarkdownText(
    content: AnnotatedString,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalMarkdownTypography.current.text,
    extendedSpans: ExtendedSpans? = LocalMarkdownExtendedSpans.current.extendedSpans?.invoke(),
) {
    val extendedStyledText = if (extendedSpans != null) {
        remember(content) {
            extendedSpans.extend(content)
        }
    } else {
        content
    }

    val onTextLayout: ((TextLayoutResult, Color?) -> Unit)? = if (extendedSpans != null) {
        { layoutResult, color ->
            extendedSpans.onTextLayout(layoutResult, color)
        }
    } else {
        null
    }

    val extendedModifier = if (extendedSpans != null) {
        modifier.drawBehind(extendedSpans)
    } else modifier

    MarkdownText(extendedStyledText, extendedModifier, style, onTextLayout)
}

@Composable
fun MarkdownText(
    content: AnnotatedString,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalMarkdownTypography.current.text,
    onTextLayout: ((TextLayoutResult, Color?) -> Unit)?,
) {
    val baseColor = LocalMarkdownColors.current.text
    val animations = LocalMarkdownAnimations.current
    val transformer = LocalImageTransformer.current

    val layoutResult: MutableState<TextLayoutResult?> = remember { mutableStateOf(null) }
    val imageState = rememberMarkdownImageState()

    val placeholderState by remember(imageState) {
        derivedStateOf {
            transformer.placeholderConfig(
                imageState.density,
                imageState.containerSize,
                imageState.intrinsicImageSize
            )
        }
    }

    MarkdownBasicText(
        text = content,
        modifier = modifier
            .onPlaced {
                it.parentLayoutCoordinates?.also { coordinates ->
                    imageState.updateContainerSize(coordinates.size.toSize())
                }
            }
            .let {
                // for backwards compatibility still check the `animate` property
                @Suppress("DEPRECATION")
                if (placeholderState.animate) animations.animateTextSize(it) else it
            },
        style = style,
        inlineContent = mapOf(
            MARKDOWN_TAG_IMAGE_URL to createImageInlineTextContent(placeholderState, imageState)
        ),
        onTextLayout = {
            layoutResult.value = it
            onTextLayout?.invoke(it, baseColor)
        }
    )
}


//todo 坏主意：使用不同的图片控件来达到正常显示效果
fun createImageInlineTextContent(
    placeholderState: PlaceholderConfig,
    imageState: MarkdownImageState
): InlineTextContent {
    return InlineTextContent(
        Placeholder(
            width = placeholderState.size.width.sp,
            height = placeholderState.size.height.sp,
            placeholderVerticalAlign = PlaceholderVerticalAlign.Center
        )
    ) { link ->
        var showPopup by remember { mutableStateOf(false) }
        var size by remember {
            mutableStateOf(DpSize.Zero)
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .pointerInput(
                    key1 = link,
                    block = {
                        detectTapGestures(
                            onLongPress = {
                                showPopup = true
                            }
                        )
                    }
                ),
        ) {
            AsyncImage(
                model = link,
                contentDescription = "",
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.medium)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.tertiary,
                        shape = MaterialTheme.shapes.medium
                    )
                    .then(
                        if (showPopup) Modifier.blur(6.dp) else Modifier
                    ),
                alignment = Alignment.Center,
                contentScale = ContentScale.Crop,
                placeholder = ColorPainter(MaterialTheme.colorScheme.secondaryContainer),
                onSuccess = { success ->
                    size = with(imageState.density) {
                        DpSize(
                            width = success.painter.intrinsicSize.width.toDp(),
                            height = success.painter.intrinsicSize.height.toDp()
                        )
                    }
                }
            )
            if (showPopup) {
                Popup(
                    onDismissRequest = {
                        showPopup = false
                    },
                ) {
                    Box(
                        modifier = Modifier
                            .size(size = size)
                            .fillMaxWidth()
                    ) {
                        AsyncImage(
                            model = link,
                            contentDescription = "",
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .clip(MaterialTheme.shapes.medium)
                                .border(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.tertiary,
                                    shape = MaterialTheme.shapes.medium
                                ),
                            alignment = Alignment.Center,
                            contentScale = ContentScale.Crop,
                            placeholder = ColorPainter(MaterialTheme.colorScheme.secondaryContainer)
                        )
                    }
                }
            }
        }
    }
}
