package com.shihcheeng.sspaiyou.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.unit.sp

private val DefaultTextStyle = TextStyle.Default

val ReaderBodyMedium = DefaultTextStyle.copy(
    fontFamily = ReaderDefTypeToken.BodyMediumFont,
    fontWeight = ReaderDefTypeToken.BodyMediumWeight,
    fontSize = 17.sp,
    lineHeight = ReaderDefTypeToken.BodyMediumLineHeight,
    letterSpacing = ReaderDefTypeToken.BodyMediumTracking
)

val ReaderBodySmall = DefaultTextStyle.copy(
    fontFamily = ReaderDefTypeToken.BodySmallFont,
    fontWeight = ReaderDefTypeToken.BodySmallWeight,
    fontSize = ReaderDefTypeToken.BodySmallSize,
    lineHeight = ReaderDefTypeToken.BodySmallLineHeight,
    letterSpacing = ReaderDefTypeToken.BodySmallTracking,
)

val ReaderH1Title = DefaultTextStyle.copy(
    fontFamily = ReaderDefTypeToken.TitleLargeFont,
    fontWeight = FontWeight.Bold,
    fontSize = 22.sp,
    lineHeight = ReaderDefTypeToken.TitleLargeLineHeight,
    letterSpacing = ReaderDefTypeToken.TitleLargeTracking
)

val ReaderH2Title = DefaultTextStyle.copy(
    fontFamily = ReaderDefTypeToken.TitleMediumFont,
    fontWeight = FontWeight.Bold,
    fontSize = 20.sp,
    lineHeight = ReaderDefTypeToken.TitleMediumLineHeight,
    letterSpacing = ReaderDefTypeToken.TitleMediumTracking
)

val ReaderH3Title = DefaultTextStyle.copy(
    fontFamily = ReaderDefTypeToken.TitleSmallFont,
    fontWeight = FontWeight.Bold,
    fontSize = 18.sp,
    lineHeight = ReaderDefTypeToken.TitleSmallLineHeight,
    letterSpacing = ReaderDefTypeToken.TitleSmallTracking
)

val ReaderBlockquoteSmall = DefaultTextStyle.copy(
    fontFamily = ReaderDefTypeToken.BodySmallFont,
    fontWeight = ReaderDefTypeToken.BodySmallWeight,
    fontSize = ReaderDefTypeToken.BodySmallSize,
    lineHeight = ReaderDefTypeToken.BodySmallLineHeight,
    letterSpacing = ReaderDefTypeToken.BodySmallTracking,
    textIndent = TextIndent(
        firstLine = ReaderBodySmall.fontSize * 2,
        restLine = ReaderBodySmall.fontSize * 2
    )
)

fun colorForMode(isDarkMode: Boolean): Color = if (isDarkMode) Color.White else Color.Black