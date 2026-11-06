package com.shihcheeng.sspaiyou.ui.screen.detail

import androidx.compose.ui.text.style.TextDecoration
import com.shihcheeng.sspaiyou.ui.theme.AppTypography
import com.shihcheeng.sspaiyou.ui.theme.sspaiReaderTypoMedium
import com.shihcheeng.sspaiyou.ui.theme.toMarkdownTypo
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ReaderTypographyTest {
    @Test
    fun linkStylesInheritBoldAndItalicFromTheirContent() {
        listOf(sspaiReaderTypoMedium(), AppTypography.toMarkdownTypo()).forEach { typography ->
            val linkStyle = typography.textLink.style!!
            assertNull(linkStyle.fontWeight)
            assertNull(linkStyle.fontStyle)
            assertEquals(TextDecoration.Underline, linkStyle.textDecoration)
        }
    }
}
