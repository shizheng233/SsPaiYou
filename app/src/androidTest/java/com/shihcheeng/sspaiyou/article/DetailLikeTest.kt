package com.shihcheeng.sspaiyou.article

import android.content.Context
import android.content.ContextWrapper
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.test.platform.app.InstrumentationRegistry
import com.shihcheeng.sspaiyou.activities.detail.DetailViewModel
import com.shihcheeng.sspaiyou.api.data.actrcle.ArticleCount
import com.shihcheeng.sspaiyou.api.data.actrcle.ArticleData
import com.shihcheeng.sspaiyou.api.data.actrcle.ArticleDataModel
import com.shihcheeng.sspaiyou.api.data.actrcle.ArticleFavouriteRequest
import com.shihcheeng.sspaiyou.api.data.actrcle.ArticleFavouriteResponse
import com.shihcheeng.sspaiyou.api.data.actrcle.ArticleLikeRequest
import com.shihcheeng.sspaiyou.api.data.actrcle.ArticleLikeResponse
import com.shihcheeng.sspaiyou.api.data.actrcle.comment.ArticleCommentDataModel
import com.shihcheeng.sspaiyou.api.service.ArticleService
import com.shihcheeng.sspaiyou.repository.DetailRepository
import com.shihcheeng.sspaiyou.ui.screen.detail.DetailBottomBar
import com.shihcheeng.sspaiyou.ui.screen.detail.DetailMarkDownParser
import com.shihcheeng.sspaiyou.ui.screen.detail.DetailScreen
import com.shihcheeng.sspaiyou.utils.LocalComposeSetting
import com.shihcheeng.sspaiyou.utils.LocalTokenManager
import com.shihcheeng.sspaiyou.utils.NetState
import dev.expressive.settings.DataStoreSettingsRepository
import dev.expressive.settings.expressiveSettingsDataStore
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.IOException
import java.util.UUID
import java.util.concurrent.CopyOnWriteArrayList

class DetailLikeTest {
    @get:Rule
    val compose = createComposeRule()

    private val store = ViewModelStore()
    private lateinit var testContext: Context
    private lateinit var tokenManager: LocalTokenManager
    private lateinit var viewModel: DetailViewModel
    private lateinit var service: FakeArticleService

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val preferencesName = "detail-like-test-${UUID.randomUUID()}"
        testContext = object : ContextWrapper(context) {
            override fun getSharedPreferences(name: String, mode: Int) =
                super.getSharedPreferences(preferencesName, mode)
        }
        tokenManager = LocalTokenManager(testContext)
        service = FakeArticleService()
    }

    @After
    fun tearDown() {
        compose.runOnIdle { store.clear() }
        testContext.getSharedPreferences("TOKEN_MANAGER", Context.MODE_PRIVATE).edit().clear().commit()
    }

    @Test
    fun loggedOutClickShowsLoginMessageWithoutSendingRequest() {
        createViewModel(token = null)
        showScreen()
        compose.onNodeWithContentDescription("喜欢").assertIsEnabled().assertIsOff().performClick()
        compose.onNodeWithText("没有登录").assertIsDisplayed()
        assertTrue(service.requests.isEmpty())
        assertFalse(article().liked)
        assertEquals(12, article().articleCount.likeCount)
        assertFalse(viewModel.isLikeUpdating.value)
    }

    @Test
    fun blankTokenAlsoRejectsDirectAction() {
        createViewModel(token = " ")
        compose.runOnIdle { viewModel.toggleLike() }
        assertEquals(DetailViewModel.LikeError.NOT_LOGGED_IN, viewModel.likeError.value)
        assertTrue(service.requests.isEmpty())
        assertFalse(viewModel.isLikeUpdating.value)
        compose.runOnIdle { viewModel.clearLikeError() }
        assertNull(viewModel.likeError.value)
    }

    @Test
    fun buttonLikesAndUnlikesUpdatingStateAndCount() {
        createViewModel()
        showBottomBar()
        compose.onNodeWithContentDescription("喜欢").assertIsOff().performClick()
        compose.waitUntil { article().liked && !viewModel.isLikeUpdating.value }
        compose.onNodeWithContentDescription("取消喜欢").assertIsOn()
        compose.onNodeWithText("(13)").assertIsDisplayed()
        compose.onNodeWithContentDescription("取消喜欢").performClick()
        compose.waitUntil { !article().liked && !viewModel.isLikeUpdating.value }
        compose.onNodeWithContentDescription("喜欢").assertIsOff().assertIsEnabled()
        compose.onNodeWithText("(12)").assertIsDisplayed()
        assertEquals(listOf(ArticleLikeRequest(115074, 1), ArticleLikeRequest(115074, 2)), service.requests)
    }

    @Test
    fun initiallyLikedArticleCanBeUnlikedWithoutNegativeCount() {
        service.article = service.article.copy(liked = true, articleCount = ArticleCount(likeCount = 0))
        createViewModel()
        showBottomBar()
        compose.onNodeWithContentDescription("取消喜欢").assertIsOn().performClick()
        compose.waitUntil { !article().liked && !viewModel.isLikeUpdating.value }
        assertEquals(0, article().articleCount.likeCount)
        assertEquals(ArticleLikeRequest(115074, 2), service.requests.single())
    }

    @Test
    fun pendingRequestDisablesButtonAndRejectsDuplicatesWithoutOptimisticUpdate() {
        val pending = CompletableDeferred<ArticleLikeResponse>()
        service.pending = pending
        createViewModel()
        showBottomBar()
        compose.runOnIdle {
            viewModel.toggleLike()
            viewModel.toggleLike()
            assertTrue(viewModel.isLikeUpdating.value)
            assertFalse(article().liked)
            assertEquals(12, article().articleCount.likeCount)
        }
        compose.onNodeWithContentDescription("喜欢").assertIsNotEnabled().performClick()
        assertEquals(1, service.requests.size)
        pending.complete(ArticleLikeResponse(0))
        compose.waitUntil { article().liked && !viewModel.isLikeUpdating.value }
        compose.onNodeWithContentDescription("取消喜欢").assertIsOn().assertIsEnabled()
        assertEquals(13, article().articleCount.likeCount)
    }

    @Test
    fun businessErrorKeepsLikeAndCountAndAllowsRetry() {
        service.article = service.article.copy(liked = true)
        service.response = ArticleLikeResponse(403)
        createViewModel()
        compose.runOnIdle { viewModel.toggleLike() }
        compose.waitUntil { viewModel.likeError.value == DetailViewModel.LikeError.REQUEST_FAILED }
        assertTrue(article().liked)
        assertEquals(12, article().articleCount.likeCount)
        assertFalse(viewModel.isLikeUpdating.value)
        compose.runOnIdle {
            service.response = ArticleLikeResponse(0)
            viewModel.toggleLike()
        }
        compose.waitUntil { !article().liked && !viewModel.isLikeUpdating.value }
        assertNull(viewModel.likeError.value)
        assertEquals(11, article().articleCount.likeCount)
        assertEquals(listOf(2, 2), service.requests.map { it.flag })
    }

    @Test
    fun networkErrorShowsFailureMessageAndKeepsState() {
        service.failure = IOException("offline")
        createViewModel()
        showScreen()
        compose.onNodeWithContentDescription("喜欢").performClick()
        compose.onNodeWithText("喜欢操作失败，请重试").assertIsDisplayed()
        compose.onNodeWithContentDescription("喜欢").assertIsOff().assertIsEnabled()
        assertFalse(article().liked)
        assertEquals(12, article().articleCount.likeCount)
    }

    @Test
    fun cancellationResetsPendingStateWithoutShowingFailure() {
        service.failure = CancellationException("cancelled")
        createViewModel()
        compose.runOnIdle { viewModel.toggleLike() }
        compose.waitUntil { service.requests.isNotEmpty() && !viewModel.isLikeUpdating.value }
        assertFalse(article().liked)
        assertEquals(12, article().articleCount.likeCount)
        assertNull(viewModel.likeError.value)
    }

    @Test
    fun memberArticleUsesResolvedNumericIdRatherThanRouteSlug() {
        createViewModel(route = "member-article")
        compose.runOnIdle { viewModel.toggleLike() }
        compose.waitUntil { article().liked }
        assertEquals("member-article", service.requestedSlug)
        assertEquals(ArticleLikeRequest(115074, 1), service.requests.single())
    }

    @Test
    fun invalidLoadedArticleIdCannotBeLiked() {
        service.article = service.article.copy(id = 0)
        createViewModel()
        compose.runOnIdle { viewModel.toggleLike() }
        assertTrue(service.requests.isEmpty())
        assertFalse(viewModel.isLikeUpdating.value)
    }

    @Test
    fun responseFromOldLoginSessionDoesNotChangeCurrentState() {
        val pending = CompletableDeferred<ArticleLikeResponse>()
        service.pending = pending
        createViewModel()
        compose.runOnIdle { viewModel.toggleLike() }
        compose.waitUntil { service.requests.isNotEmpty() }
        runBlocking { tokenManager.saveToken("another-session") }
        pending.complete(ArticleLikeResponse(0))
        compose.waitUntil { !viewModel.isLikeUpdating.value }
        assertFalse(article().liked)
        assertEquals(12, article().articleCount.likeCount)
        assertNull(viewModel.likeError.value)
    }

    @Test
    fun failureAfterLogoutDoesNotShowStaleError() {
        val pending = CompletableDeferred<ArticleLikeResponse>()
        service.pending = pending
        createViewModel()
        compose.runOnIdle { viewModel.toggleLike() }
        compose.waitUntil { service.requests.isNotEmpty() }
        runBlocking { tokenManager.saveToken("") }
        pending.complete(ArticleLikeResponse(403))
        compose.waitUntil { !viewModel.isLikeUpdating.value }
        assertFalse(article().liked)
        assertNull(viewModel.likeError.value)
    }

    @Test
    fun concurrentFavouriteAndLikeRequestsPreserveBothUpdates() {
        val pending = CompletableDeferred<ArticleLikeResponse>()
        service.pending = pending
        createViewModel()
        compose.runOnIdle {
            viewModel.toggleLike()
            viewModel.toggleFavourite()
        }
        compose.waitUntil { article().favorited }
        pending.complete(ArticleLikeResponse(0))
        compose.waitUntil { article().liked && !viewModel.isLikeUpdating.value }
        assertTrue(article().favorited)
        assertEquals(13, article().articleCount.likeCount)
        assertEquals(1, service.requests.size)
    }

    private fun createViewModel(token: String? = "test-session", route: String = "115074") {
        if (token != null) runBlocking { tokenManager.saveToken(token) }
        val factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T = DetailViewModel(
                SavedStateHandle(mapOf("id" to route)),
                DetailRepository(service),
                DetailMarkDownParser(),
                tokenManager
            ) as T
        }
        compose.runOnIdle {
            viewModel = ViewModelProvider(store, factory)[DetailViewModel::class.java]
        }
        compose.waitUntil(10_000) { viewModel.data.value is NetState.Success }
    }

    private fun article() = (viewModel.data.value as NetState.Success).content

    private fun showScreen() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val settings = DataStoreSettingsRepository(context.expressiveSettingsDataStore)
        compose.setContent {
            CompositionLocalProvider(LocalComposeSetting provides settings) {
                MaterialTheme {
                    DetailScreen(viewModel = viewModel, onAvatarClick = {}, onLinkClick = {}, onNavBack = {})
                }
            }
        }
    }

    private fun showBottomBar() {
        compose.setContent {
            val data by viewModel.data.collectAsStateWithLifecycle()
            val pending by viewModel.isLikeUpdating.collectAsStateWithLifecycle()
            val article = (data as NetState.Success).content
            MaterialTheme {
                DetailBottomBar(
                    commentCount = 0,
                    likeCount = article.articleCount.likeCount,
                    onShareCommit = {},
                    onBookmarkClick = viewModel::toggleFavourite,
                    onTocClick = {},
                    onViewComments = {},
                    onLikeClick = viewModel::toggleLike,
                    isLike = article.liked,
                    likeEnabled = !pending,
                    isBookmarked = article.favorited
                )
            }
        }
    }

    private class FakeArticleService : ArticleService {
        var article = ArticleData(
            id = 115074,
            bodyExtends = emptyList(),
            articleCount = ArticleCount(likeCount = 12)
        )
        var response = ArticleLikeResponse(0)
        var failure: Exception? = null
        var pending: CompletableDeferred<ArticleLikeResponse>? = null
        var requestedSlug: String? = null
        val requests = CopyOnWriteArrayList<ArticleLikeRequest>()

        override suspend fun getArticleDetail(id: String, view: String, supportWebp: Boolean) =
            ArticleDataModel(data = article)

        override suspend fun getMemberArticleDetail(slug: String, view: String, supportWebp: Boolean): ArticleDataModel {
            requestedSlug = slug
            return ArticleDataModel(data = article)
        }

        override suspend fun getArticleComments(limit: Int, offset: Int, createdAt: Long, articleId: Int) =
            ArticleCommentDataModel()

        override suspend fun favouriteOrNot(request: ArticleFavouriteRequest) = ArticleFavouriteResponse(0)

        override suspend fun likeItOrNot(request: ArticleLikeRequest): ArticleLikeResponse {
            requests += request
            failure?.let { throw it }
            return pending?.await() ?: response
        }
    }
}
