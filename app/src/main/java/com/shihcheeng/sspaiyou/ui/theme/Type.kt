package com.shihcheeng.sspaiyou.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.unit.sp
import com.mikepenz.markdown.model.DefaultMarkdownTypography
import com.mikepenz.markdown.model.MarkdownTypography

val AppTypography = Typography()

fun Typography.toMarkdownTypo(): MarkdownTypography {
    return DefaultMarkdownTypography(
        h1 = headlineLarge,
        h2 = headlineMedium,
        h3 = headlineSmall,
        h4 = titleLarge,
        h5 = titleMedium,
        h6 = titleSmall,
        text = ReaderBodyMedium,
        code = ReaderCode,
        inlineCode = ReaderCodeMedium,
        quote = ReaderBodyMedium,
        paragraph = ReaderBodyMedium,
        ordered = ReaderBodyMedium,
        bullet = ReaderBodyMedium,
        list = ReaderBodyMedium,
        textLink = TextLinkStyles(
            style = ReaderBodyMedium.toSpanStyle().copy(textDecoration = TextDecoration.Underline)
        ),
        table = bodyMedium
    )
}

private fun TextStyle.scaledBy(scale: Float): TextStyle =
    if (scale == 1f) this else copy(fontSize = fontSize * scale, lineHeight = lineHeight * scale)

fun sspaiReaderTypo(fontScale: Float = 1f): MarkdownTypography {
    val body = ReaderBodyMedium.scaledBy(fontScale)
    val bodySmall = ReaderBodySmall.scaledBy(fontScale)
    return object : MarkdownTypography {
        override val text: TextStyle
            get() = body
        override val code: TextStyle
            get() = ReaderCode.scaledBy(fontScale)
        override val inlineCode: TextStyle
            get() = ReaderCodeMedium.scaledBy(fontScale)
        override val h1: TextStyle
            get() = ReaderH1Title.scaledBy(fontScale)
        override val h2: TextStyle
            get() = ReaderH2Title.scaledBy(fontScale)
        override val h3: TextStyle
            get() = ReaderH3Title.scaledBy(fontScale)
        override val h4: TextStyle
            get() = ReaderH3Title.copy(fontSize = 16.sp * fontScale, fontWeight = FontWeight.Bold)
        override val h5: TextStyle
            get() = body.copy(fontWeight = FontWeight.Bold)
        override val h6: TextStyle
            get() = bodySmall.copy(fontWeight = FontWeight.Bold)
        override val quote: TextStyle
            get() = bodySmall.copy(
                textIndent = TextIndent(
                    firstLine = bodySmall.fontSize * 2,
                    restLine = bodySmall.fontSize * 2
                )
            )
        override val paragraph: TextStyle
            get() = body
        override val ordered: TextStyle
            get() = body
        override val bullet: TextStyle
            get() = body
        override val list: TextStyle
            get() = body
        override val textLink: TextLinkStyles
            get() = TextLinkStyles(
                style = body.toSpanStyle().copy(textDecoration = TextDecoration.Underline)
            )
        override val table: TextStyle
            get() = body
        override val alertTitle: TextStyle
            get() = body.copy(fontWeight = FontWeight.Bold)
    }
}

fun sspaiReaderTypoMedium(): MarkdownTypography = sspaiReaderTypo(fontScale = 1.15f)

fun sspaiReaderTypoLarge(): MarkdownTypography = sspaiReaderTypo(fontScale = 1.3f)

