package com.shihcheeng.sspaiyou.utils

import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle

sealed class Span

data class SpanStyle(val style: SpanStyle) : Span()
data class SpanAnnotation(val tag: String, val annotation: String) : Span()
data class Paragraph(val style: ParagraphStyle) : Span()