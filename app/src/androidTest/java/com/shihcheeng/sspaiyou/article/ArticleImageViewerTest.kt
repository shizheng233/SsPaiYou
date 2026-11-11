package com.shihcheeng.sspaiyou.article

import android.graphics.Bitmap
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.doubleClick
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.pinch
import androidx.compose.ui.unit.dp
import androidx.test.espresso.Espresso
import androidx.test.platform.app.InstrumentationRegistry
import com.github.panpf.zoomimage.CoilZoomState
import com.github.panpf.zoomimage.rememberCoilZoomState
import com.shihcheeng.sspaiyou.ui.screen.detail.ArticleImageDialog
import com.shihcheeng.sspaiyou.ui.screen.detail.ArticleZoomableImage
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.io.File

class ArticleImageViewerTest {
    @get:Rule
    val compose = createComposeRule()

    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private val files = mutableListOf<File>()
    private val showContent = mutableStateOf(true)

    @After
    fun cleanUp() {
        compose.mainClock.autoAdvance = true
        compose.runOnIdle { showContent.value = false }
        compose.waitForIdle()
        files.forEach { it.delete() }
    }

    @Test
    fun localImageHasVisibleBaseAndSubsamplingAndZoomGesturesDoNotTap() {
        val file = newImageFile().apply { writeImage(this) }
        lateinit var state: CoilZoomState
        var taps = 0
        compose.setContent {
            if (showContent.value) {
                MaterialTheme {
                    state = rememberCoilZoomState()
                    ArticleZoomableImage(
                        url = file.toURI().toString(),
                        modifier = Modifier.size(240.dp).testTag("zoom"),
                        zoomState = state,
                        onTap = { taps++ }
                    )
                }
            }
        }
        compose.waitUntil(10_000) { state.subsampling.ready }
        assertEquals(1200, state.zoomable.contentOriginSize.width)
        assertEquals(2400, state.zoomable.contentOriginSize.height)
        val image = compose.onNodeWithTag("zoom")
        val pixels = image.captureToImage().toPixelMap()
        val centerPixel = pixels[pixels.width / 2, pixels.height / 2]
        assertTrue("Base image must be drawn", centerPixel.red > 0.9f && centerPixel.green < 0.1f)

        image.performTouchInput { doubleClick(center) }
        compose.waitUntil(5_000) { state.zoomable.userTransform.scale.scaleX > 1.5f }
        compose.waitForIdle()
        val beforePinch = state.zoomable.userTransform.scale.scaleX
        image.performTouchInput {
            pinch(
                start0 = center.copy(x = center.x - 50f),
                end0 = center.copy(x = center.x - 100f),
                start1 = center.copy(x = center.x + 50f),
                end1 = center.copy(x = center.x + 100f)
            )
        }
        compose.waitUntil(5_000) { state.zoomable.userTransform.scale.scaleX > beforePinch }
        assertEquals(0, taps)
    }

    @Test
    fun failedFileCanBeRetriedAfterItBecomesAvailable() {
        val file = newImageFile().apply { delete() }
        lateinit var state: CoilZoomState
        compose.setContent {
            if (showContent.value) {
                MaterialTheme {
                    state = rememberCoilZoomState()
                    ArticleZoomableImage(file.toURI().toString(), Modifier.size(240.dp), state)
                }
            }
        }
        compose.waitUntil(5_000) {
            compose.onAllNodesWithText("图片加载失败，请重试").fetchSemanticsNodes().isNotEmpty()
        }
        writeImage(file)
        compose.onNodeWithText("重试").performClick()
        compose.waitUntil(10_000) { state.subsampling.ready }
        compose.onNodeWithText("图片加载失败，请重试").assertDoesNotExist()
    }

    @Test
    fun dialogFinishesExitBeforeDismissAndCanReopenAndCloseWithBack() {
        val file = newImageFile().apply { writeImage(this) }
        val showDialog = mutableStateOf(true)
        var dismissCount = 0
        compose.setContent {
            if (showContent.value) {
                MaterialTheme {
                    if (showDialog.value) {
                        ArticleImageDialog(file.toURI().toString(), onDismiss = {
                            dismissCount++
                            showDialog.value = false
                        })
                    }
                }
            }
        }
        compose.onNodeWithContentDescription("关闭大图").assertIsDisplayed()
        compose.mainClock.autoAdvance = false
        compose.onNodeWithContentDescription("关闭大图").performClick()
        assertEquals("Exit animation must retain the dialog", 0, dismissCount)
        compose.mainClock.advanceTimeBy(300)
        compose.mainClock.autoAdvance = true
        compose.waitUntil(5_000) { !showDialog.value }
        assertEquals(1, dismissCount)

        compose.runOnIdle { showDialog.value = true }
        compose.onNodeWithContentDescription("关闭大图").assertIsDisplayed()
        Espresso.pressBack()
        compose.waitUntil(5_000) { !showDialog.value }
        assertFalse(showDialog.value)
        assertEquals(2, dismissCount)
    }

    private fun newImageFile(): File = File.createTempFile("article_zoom_", ".png", context.cacheDir)
        .also { files += it }

    private fun writeImage(file: File) {
        val bitmap = Bitmap.createBitmap(1200, 2400, Bitmap.Config.ARGB_8888)
        try {
            bitmap.eraseColor(android.graphics.Color.RED)
            file.outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
        } finally {
            bitmap.recycle()
        }
    }
}
