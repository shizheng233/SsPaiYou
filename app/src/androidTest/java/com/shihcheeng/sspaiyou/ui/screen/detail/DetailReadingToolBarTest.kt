package com.shihcheeng.sspaiyou.ui.screen.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class DetailReadingToolBarTest {
    @get:Rule
    val compose = createComposeRule()

    private lateinit var state: LazyListState
    private lateinit var scope: CoroutineScope
    private val title = mutableStateOf<String?>("测试文章")
    private var backClicks = 0

    @Test
    fun scrollingWithinFirstItemShowsProgressAndReturningToTopHidesIt() {
        showReader(listOf(1600.dp, 80.dp, 1200.dp))
        progressNode().assertDoesNotExist()
        compose.onNodeWithText("测试文章").assertDoesNotExist()
        compose.onNodeWithContentDescription("返回上一层级").performClick()
        assertEquals(1, backClicks)

        scrollTo(0, 120)
        compose.runOnIdle { assertEquals(0, state.firstVisibleItemIndex) }
        progressNode().assertIsDisplayed()
        val firstProgress = progress()
        assertTrue(firstProgress > 0f && firstProgress < 1f)
        compose.onNodeWithText("测试文章").assertDoesNotExist()

        scrollTo(0, 240)
        assertTrue(progress() > firstProgress)

        scrollTo(0)
        progressNode().assertDoesNotExist()
        compose.runOnIdle { assertEquals(0f, state.readingProgress(), 0f) }
    }

    @Test
    fun mixedHeightArticleReachesFullProgressAndDirectoryJumpUpdatesPosition() {
        showReader(listOf(1600.dp, 80.dp, 1200.dp, 100.dp, 900.dp))
        scrollTo(1)
        compose.onNodeWithText("测试文章").assertIsDisplayed()
        assertTrue(progress() in 0f..1f)

        scrollTo(4, 100_000)
        compose.runOnIdle { assertFalse(state.canScrollForward) }
        assertEquals(1f, progress(), 0f)

        scrollTo(0, 120)
        assertTrue(progress() > 0f && progress() < 1f)
    }

    @Test
    fun singleTallBlockTracksPixelOffsetAndCanReachTheEnd() {
        showReader(listOf(2400.dp))
        scrollTo(0, 200)
        assertTrue(progress() > 0f && progress() < 1f)
        scrollTo(0, 100_000)
        assertEquals(1f, progress(), 0f)
    }

    @Test
    fun articleThatFitsTheViewportKeepsProgressHidden() {
        showReader(listOf(24.dp, 24.dp))
        progressNode().assertDoesNotExist()
        compose.runOnIdle {
            assertFalse(state.canScrollBackward)
            assertFalse(state.canScrollForward)
            assertEquals(0f, state.readingProgress(), 0f)
        }
    }

    @Test
    fun restoredPositionShowsProgressWithoutWaitingForAnotherGesture() {
        showReader(listOf(1600.dp, 1200.dp, 900.dp), initialIndex = 1)
        progressNode().assertIsDisplayed()
        compose.onNodeWithText("测试文章").assertIsDisplayed()
        assertTrue(progress() > 0f && progress() < 1f)
    }

    @Test
    fun leavingLoadedStateWhileTitleIsVisibleSafelyHidesReadingUi() {
        showReader(listOf(1600.dp, 1200.dp, 900.dp))
        scrollTo(1)
        compose.onNodeWithText("测试文章").assertIsDisplayed()

        compose.runOnIdle { title.value = null }
        progressNode().assertDoesNotExist()
        compose.onNodeWithText("测试文章").assertDoesNotExist()
        compose.onNodeWithContentDescription("返回上一层级").assertIsDisplayed()
    }

    private fun showReader(heights: List<Dp>, initialIndex: Int = 0) {
        compose.setContent {
            state = rememberLazyListState(initialFirstVisibleItemIndex = initialIndex)
            scope = rememberCoroutineScope()
            val currentTitle by title
            MaterialTheme {
                Column(Modifier.fillMaxSize()) {
                    DetailReadingToolBar(
                        scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
                        titleShowable = state.firstVisibleItemIndex > 0,
                        showProgressBar = state.canScrollBackward,
                        title = currentTitle,
                        progress = { state.readingProgress() },
                        onNavBack = { backClicks++ }
                    )
                    LazyColumn(
                        state = state,
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(bottom = 96.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(heights.size) { index ->
                            Box(Modifier.fillMaxWidth().height(heights[index]))
                        }
                    }
                }
            }
        }
    }

    private fun scrollTo(index: Int, offset: Int = 0) {
        compose.runOnIdle { scope.launch { state.scrollToItem(index, offset) } }
        compose.waitForIdle()
    }

    private fun progressNode() = compose.onNodeWithContentDescription("阅读进度")

    private fun progress(): Float = progressNode().fetchSemanticsNode()
        .config[SemanticsProperties.ProgressBarRangeInfo].current
}
