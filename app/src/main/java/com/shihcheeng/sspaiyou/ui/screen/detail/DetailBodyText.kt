package com.shihcheeng.sspaiyou.ui.screen.detail

import androidx.compose.ui.text.ParagraphStyle
import com.shihcheeng.sspaiyou.utils.Paragraph
import com.shihcheeng.sspaiyou.utils.Span
import com.shihcheeng.sspaiyou.utils.SpanAnnotation
import com.shihcheeng.sspaiyou.utils.SpanStyle
import androidx.compose.ui.text.SpanStyle as UiTextSpanStyle

class DetailBodyText(
    val emitter: (DetailAnnotationStringBuilder) -> Unit
) {

    val spanStack: MutableList<Span> = mutableListOf()

    private var builder: DetailAnnotationStringBuilder = DetailAnnotationStringBuilder()

    fun terminateCurrentBuilder() {
        if (builder.isEmpty()) {
            return
        }
        emitter(builder)
        builder = DetailAnnotationStringBuilder()
        for (span in spanStack) {
            when (span) {
                is SpanStyle -> builder.pushStyle(span.style)
                is SpanAnnotation -> builder.pushStringAnnotation(span.tag, span.annotation)
                is Paragraph -> builder.pushParagraphStyle(span.style)
            }
        }
    }

    fun endWithWhiteSpace() = builder.endWithWhitespace

    fun appendLine() = builder.ensureSingleNewline()

    fun append(text: String) {
        builder.append(text)
    }

    fun append(char: Char) = builder.append(char)

    fun <R> appendImage(
        block: () -> R
    ): R {
        terminateCurrentBuilder()
        return block()
    }

    fun <R> appendCompose(
        block: () -> R
    ): R {
        terminateCurrentBuilder()
        return block()
    }

    fun pop(index: Int) = builder.pop(index)

    fun pushStack(style: UiTextSpanStyle) = builder.pushStyle(style)

    fun pushStringAnnotation(
        tag: String,
        annotation: String
    ): Int = builder.pushStringAnnotation(
        tag = tag,
        annotation = annotation
    )

    fun pushParagraphStyle(style: ParagraphStyle) = builder.pushParagraphStyle(style)

    inline fun withParagraph(
        crossinline block: DetailBodyText.() -> Unit
    ) {
        runCatching {
            append('\n')
            block(this)
            append('\n')
        }
    }

    inline fun withParagraph(
        paragraphStyle: ParagraphStyle = ParagraphStyle(),
        crossinline block: DetailBodyText.() -> Unit
    ) {
        spanStack.add(Paragraph(paragraphStyle))
        val index = pushParagraphStyle(paragraphStyle)
        return try {
            appendLine()
            block(this)
        } finally {
            pop(index)
            spanStack.removeAt(spanStack.lastIndex)
        }

    }

    inline fun <R : Any> withStyle(
        style: UiTextSpanStyle,
        crossinline block: DetailBodyText.() -> R,
    ): R {
        spanStack.add(SpanStyle(style))
        val index = pushStack(style)
        return try {
            block()
        } finally {
            pop(index)
            spanStack.removeAt(spanStack.lastIndex)
        }
    }

    inline fun <R : Any> withAnnotation(
        tag: String,
        annotation: String,
        crossinline block: DetailBodyText.() -> R,
    ): R {
        spanStack.add(SpanAnnotation(tag = tag, annotation = annotation))
        val index = pushStringAnnotation(tag = tag, annotation = annotation)
        return try {
            block()
        } finally {
            pop(index)
            spanStack.remove(spanStack.last())
        }
    }


}