package com.shihcheeng.sspaiyou.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.style.TextDecoration
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
        link = ReaderBodyMedium,
        textLink = TextLinkStyles(
            style = ReaderBodyMedium.toSpanStyle().copy(textDecoration = TextDecoration.Underline)
        ),
        table = bodyMedium
    )
}


