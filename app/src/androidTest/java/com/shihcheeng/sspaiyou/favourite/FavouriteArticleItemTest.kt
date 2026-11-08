package com.shihcheeng.sspaiyou.favourite

import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.test.platform.app.InstrumentationRegistry
import com.shihcheeng.sspaiyou.R
import com.shihcheeng.sspaiyou.api.data.column.ColumnArticle
import com.shihcheeng.sspaiyou.api.data.column.ColumnUser
import com.shihcheeng.sspaiyou.ui.screen.favourite.FavouriteArticleItem
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class FavouriteArticleItemTest {
    @get:Rule
    val compose = createComposeRule()

    private val article = ColumnArticle(
        title = "这是一篇标题较长的收藏文章，用来验证紧凑横向卡片中的标题限行与封面位置",
        banner = "cover.png",
        free = true,
        author = ColumnUser(nickname = "收藏作者", slug = "favourite-author")
    )

    @Test
    fun cardClickOpensArticle() {
        var clicks = 0
        showCard(onClick = { clicks++ })
        compose.onNodeWithTag("favourite").performClick()
        compose.runOnIdle { assertEquals(1, clicks) }
    }

    @Test
    fun authorClickDoesNotOpenArticle() {
        var articleClicks = 0
        var selectedAuthor: String? = null
        showCard(onClick = { articleClicks++ }, onUserClick = { selectedAuthor = it })
        compose.onNodeWithText(article.author.nickname).performClick()
        compose.runOnIdle {
            assertEquals("favourite-author", selectedAuthor)
            assertEquals(0, articleClicks)
        }
    }

    @Test
    fun compactCardShowsTypeAboveTitleAndReservesCoverSpaceAtRight() {
        showCard()
        val card = compose.onNodeWithTag("favourite").fetchSemanticsNode().boundsInRoot
        val title = compose.onNodeWithText(article.title, useUnmergedTree = true)
            .fetchSemanticsNode().boundsInRoot
        val typeLabel = InstrumentationRegistry.getInstrumentation().targetContext
            .getString(R.string.type_obj_1)
        val type = compose.onNodeWithText(typeLabel, useUnmergedTree = true)
            .fetchSemanticsNode().boundsInRoot
        val density = compose.density
        assertTrue(type.bottom <= title.top)
        assertTrue(card.right - title.right >= with(density) { 104.dp.toPx() })
        assertTrue(card.height <= with(density) { 156.dp.toPx() })
    }

    @Test
    fun coverFollowsEndInRtlLayout() {
        showCard(direction = LayoutDirection.Rtl)
        val card = compose.onNodeWithTag("favourite").fetchSemanticsNode().boundsInRoot
        val title = compose.onNodeWithText(article.title, useUnmergedTree = true)
            .fetchSemanticsNode().boundsInRoot
        assertTrue(title.left - card.left >= with(compose.density) { 104.dp.toPx() })
    }

    @Test
    fun blankCoverDoesNotReserveImageSpace() {
        showCard(article.copy(banner = ""))
        val title = compose.onNodeWithText(article.title, useUnmergedTree = true)
            .fetchSemanticsNode().boundsInRoot
        assertTrue(title.width >= with(compose.density) { 320.dp.toPx() })
    }

    private fun showCard(
        item: ColumnArticle = article,
        direction: LayoutDirection = LayoutDirection.Ltr,
        onClick: () -> Unit = {},
        onUserClick: (String) -> Unit = {}
    ) {
        compose.setContent {
            CompositionLocalProvider(LocalLayoutDirection provides direction) {
                MaterialTheme {
                    FavouriteArticleItem(
                        article = item,
                        onClick = onClick,
                        onUserClick = onUserClick,
                        modifier = Modifier.width(360.dp).testTag("favourite")
                    )
                }
            }
        }
    }
}
