package com.shihcheeng.sspaiyou.ui.screen.detail

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class DetailMarkDownParserTest {
    private val parser = DetailMarkDownParser()

    @Test
    fun figureWithSrcPreservesUrlCaptionAndDimensions() {
        val image = parser.parse(
            """<figure><img src="https://example.com/photo?a=1&amp;b=2" alt="A &amp; B" width="800" height="400"></figure>"""
        ).filterIsInstance<DetailContent.Image>().single()

        assertEquals("https://example.com/photo?a=1&b=2", image.url)
        assertEquals("A & B", image.caption)
        assertEquals(2f, image.aspectRatio!!, 0.001f)
    }

    @Test
    fun lazyImageUsesOriginalUrlAndKeepsSurroundingText() {
        val blocks = parser.parse(
            """<p>Before</p><figure><img src="preview.jpg" data-original="full.jpg" width="400" height="800"></figure><p>After</p>"""
        )
        val image = blocks.filterIsInstance<DetailContent.Image>().single()

        assertEquals("full.jpg", image.url)
        assertEquals(0.5f, image.aspectRatio!!, 0.001f)
        assertEquals(listOf("Before", "After"), blocks.filterIsInstance<DetailContent.Markdown>().map { it.text })
    }

    @Test
    fun percentageOrInvalidDimensionsDoNotBecomeAspectRatios() {
        val images = parser.parse(
            """<figure><img src="first.jpg" width="100%" height="400"><img src="second.jpg" width="400" height="0"></figure>"""
        ).filterIsInstance<DetailContent.Image>()

        assertEquals(2, images.size)
        images.forEach { assertNull(it.aspectRatio) }
    }
}
