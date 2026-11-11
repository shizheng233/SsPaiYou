package com.shihcheeng.sspaiyou.ui.compoents

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import androidx.test.platform.app.InstrumentationRegistry
import coil.ImageLoader
import coil.decode.DataSource
import coil.intercept.Interceptor
import coil.request.SuccessResult
import kotlinx.coroutines.CompletableDeferred
import org.junit.Assert.assertTrue
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class ReaderImageTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun delayedImageIsDrawnWithoutScrollingAndUpdatesItsAspectRatio() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val releaseImage = CompletableDeferred<Unit>()
        var openedImage: String? = null
        val bitmap = Bitmap.createBitmap(400, 200, Bitmap.Config.ARGB_8888).apply {
            eraseColor(android.graphics.Color.RED)
        }
        val loader = ImageLoader.Builder(context)
            .components {
                add(Interceptor { chain ->
                    releaseImage.await()
                    SuccessResult(
                        drawable = BitmapDrawable(context.resources, bitmap),
                        request = chain.request,
                        dataSource = DataSource.NETWORK
                    )
                })
            }
            .build()
        try {
            compose.setContent {
                MaterialTheme {
                    LazyColumn(Modifier.width(200.dp)) {
                        item {
                            ReaderImage(
                                url = "https://example.com/delayed.png",
                                imageLoader = loader,
                                modifier = Modifier.testTag("reader-image"),
                                onImageClick = { openedImage = it }
                            )
                        }
                    }
                }
            }
            val image = compose.onNodeWithTag("reader-image")
            val loadingBounds = image.fetchSemanticsNode().boundsInRoot
            assertTrue("Loading image must reserve space", loadingBounds.height > 0f)

            releaseImage.complete(Unit)
            compose.waitUntil(5_000) {
                val bounds = image.fetchSemanticsNode().boundsInRoot
                kotlin.math.abs(bounds.width / bounds.height - 2f) < 0.02f
            }
            compose.waitForIdle()
            val pixels = image.captureToImage().toPixelMap()
            val center = pixels[pixels.width / 2, pixels.height / 2]
            assertTrue("Loaded image must be visible without a scroll", center.red > 0.9f && center.green < 0.1f)
            image.performClick()
            assertEquals("https://example.com/delayed.png", openedImage)
        } finally {
            loader.shutdown()
        }
    }
}
