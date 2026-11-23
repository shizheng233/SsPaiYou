package com.shihcheeng.sspaiyou.discover

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.lifecycle.viewModelScope
import com.shihcheeng.sspaiyou.api.data.column.ColumnArticle
import com.shihcheeng.sspaiyou.api.data.column.ColumnUser
import com.shihcheeng.sspaiyou.api.data.discover.DiscoverPage
import com.shihcheeng.sspaiyou.api.data.discover.FollowedUserActivity
import com.shihcheeng.sspaiyou.api.service.DiscoverService
import com.shihcheeng.sspaiyou.repository.DiscoverRepository
import com.shihcheeng.sspaiyou.ui.screen.main.discover.hot.HotScreen
import com.shihcheeng.sspaiyou.ui.screen.main.discover.hot.HotViewModel
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

class HotScreenTest {
    @get:Rule
    val compose = createComposeRule()

    private val service = FakeDiscoverService()
    private val viewModel = HotViewModel(
        discoverRepository = DiscoverRepository(discoverService = service),
    )

    @After
    fun tearDown() {
        viewModel.viewModelScope.cancel()
    }

    @Test
    fun retriesAnErrorThenRefreshesAnEmptyFeedAndOpensRepeatedArticles() {
        var selectedArticle: String? = null
        var selectedAuthor: String? = null
        compose.setContent {
            MaterialTheme {
                HotScreen(
                    viewModel = viewModel,
                    onArticleClick = { selectedArticle = it },
                    onUserClick = { selectedAuthor = it },
                )
            }
        }

        service.respond(page = DiscoverPage(error = 503, msg = "热门文章加载失败"))
        waitForText(text = "热门文章加载失败")
        compose.onNodeWithText("重试").performClick()

        service.respond(page = DiscoverPage(error = 0, data = emptyList()))
        waitForText(text = "暂无热门文章")
        compose.onNodeWithText("刷新").performClick()

        val article = ColumnArticle(
            id = 119438,
            title = "重复的热门文章",
            slug = "hot-article-slug",
            author = ColumnUser(nickname = "热门文章作者", slug = "hot-author"),
            free = false,
        )
        service.respond(page = DiscoverPage(error = 0, data = listOf(article, article), total = 2))
        waitForText(text = article.title)
        compose.onAllNodesWithText(article.title).assertCountEquals(2)
        compose.onAllNodesWithText(article.author.nickname).onFirst().performClick()
        compose.runOnIdle {
            assertEquals(article.author.slug, selectedAuthor)
            assertNull(selectedArticle)
        }
        compose.onAllNodesWithText(article.title).onFirst().performClick()
        compose.runOnIdle { assertEquals(article.slug, selectedArticle) }
    }

    private fun waitForText(text: String) {
        compose.waitUntil(timeoutMillis = 5_000) {
            compose.onAllNodesWithText(text).fetchSemanticsNodes().isNotEmpty()
        }
    }

    private class FakeDiscoverService : DiscoverService {
        private val responses = Channel<DiscoverPage<ColumnArticle>>(capacity = Channel.UNLIMITED)

        fun respond(page: DiscoverPage<ColumnArticle>) {
            check(responses.trySend(page).isSuccess)
        }

        override suspend fun getHotPage(
            offset: Int,
            limit: Int,
            createdAt: Long,
        ): DiscoverPage<ColumnArticle> = responses.receive()

        override suspend fun getUserActivityUserPage(
            offset: Int,
            limit: Int,
            createdAt: Long,
        ): DiscoverPage<FollowedUserActivity> = error("测试只加载热门文章")
    }
}
