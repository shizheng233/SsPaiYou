package com.shihcheeng.sspaiyou.discover

import android.content.Context
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.lifecycle.viewModelScope
import androidx.test.platform.app.InstrumentationRegistry
import com.shihcheeng.sspaiyou.api.data.column.ColumnArticle
import com.shihcheeng.sspaiyou.api.data.column.ColumnUser
import com.shihcheeng.sspaiyou.api.data.discover.DiscoverPage
import com.shihcheeng.sspaiyou.api.data.discover.FollowedUserActivity
import com.shihcheeng.sspaiyou.api.service.DiscoverService
import com.shihcheeng.sspaiyou.repository.DiscoverRepository
import com.shihcheeng.sspaiyou.ui.screen.main.discover.followactivity.FollowActivityScreen
import com.shihcheeng.sspaiyou.ui.screen.main.discover.followactivity.FollowActivityViewModel
import com.shihcheeng.sspaiyou.utils.CookieCipher
import com.shihcheeng.sspaiyou.utils.LocalTokenManager
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import java.util.UUID

class FollowActivityScreenTest {
    @get:Rule
    val compose = createComposeRule()

    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private val preferencesName = "follow-activity-test-${UUID.randomUUID()}"
    private val cookieCipher = CookieCipher(keyAlias = preferencesName)
    private lateinit var viewModel: FollowActivityViewModel

    @After
    fun tearDown() {
        if (::viewModel.isInitialized) viewModel.viewModelScope.cancel()
        context.deleteSharedPreferences(preferencesName)
        cookieCipher.deleteKey()
    }

    @Test
    fun retriesAnErrorThenRefreshesAnEmptyFeedAndOpensTheArticle() {
        val service = FakeDiscoverService()
        val tokenManager = LocalTokenManager(
            tokenPreferences = context.getSharedPreferences(preferencesName, Context.MODE_PRIVATE),
            cookieCipher = cookieCipher,
        )
        runBlocking { tokenManager.saveToken(string = "test-session") }
        viewModel = FollowActivityViewModel(
            discoverRepository = DiscoverRepository(discoverService = service),
            tokenManager = tokenManager,
        )
        var openedArticleId: String? = null
        compose.setContent {
            MaterialTheme {
                FollowActivityScreen(
                    viewModel = viewModel,
                    onArticleId = { openedArticleId = it },
                )
            }
        }
        compose.onNode(SemanticsMatcher.keyIsDefined(SemanticsProperties.ProgressBarRangeInfo))
            .assertIsDisplayed()

        check(service.responses.trySend(DiscoverPage(error = 503, msg = "暂时无法加载")).isSuccess)
        waitForText("暂时无法加载")
        compose.onNodeWithText("重试").performClick()

        check(service.responses.trySend(DiscoverPage(error = 0, data = emptyList())).isSuccess)
        waitForText("暂无关注动态")
        compose.onNodeWithText("刷新").performClick()

        val article = ColumnArticle(id = 119438, title = "同一篇文章")
        val activity = FollowedUserActivity(
            key = "release_article",
            author = ColumnUser(id = 12),
            article = article,
            createdAt = 1795428509L,
            action = "发布了文章",
        )
        val activities = listOf(activity, activity.copy(author = ColumnUser(id = 56)))
        check(service.responses.trySend(
            DiscoverPage(error = 0, data = activities, total = activities.size),
        ).isSuccess)
        waitForText(article.title)
        compose.onAllNodesWithText(article.title).assertCountEquals(2).onFirst().performClick()
        compose.runOnIdle { assertEquals(article.id.toString(), openedArticleId) }
    }

    private fun waitForText(text: String) {
        compose.waitUntil(timeoutMillis = 5_000) {
            compose.onAllNodesWithText(text).fetchSemanticsNodes().isNotEmpty()
        }
        compose.onAllNodesWithText(text).onFirst().assertIsDisplayed()
    }

    private class FakeDiscoverService : DiscoverService {
        val responses = Channel<DiscoverPage<FollowedUserActivity>>(capacity = Channel.UNLIMITED)

        override suspend fun getHotPage(
            offset: Int,
            limit: Int,
            createdAt: Long,
        ): DiscoverPage<ColumnArticle> = error("测试只加载关注动态")

        override suspend fun getUserActivityUserPage(
            offset: Int,
            limit: Int,
            createdAt: Long,
        ): DiscoverPage<FollowedUserActivity> = responses.receive()
    }
}
