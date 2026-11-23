package com.shihcheeng.sspaiyou.favourite

import android.content.Context
import android.content.ContextWrapper
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.test.platform.app.InstrumentationRegistry
import com.shihcheeng.sspaiyou.activities.detail.DetailViewModel
import com.shihcheeng.sspaiyou.api.data.actrcle.ArticleData
import com.shihcheeng.sspaiyou.api.data.actrcle.ArticleDataModel
import com.shihcheeng.sspaiyou.api.data.actrcle.ArticleFavouriteRequest
import com.shihcheeng.sspaiyou.api.data.actrcle.ArticleFavouriteResponse
import com.shihcheeng.sspaiyou.api.data.actrcle.ArticleLikeRequest
import com.shihcheeng.sspaiyou.api.data.actrcle.ArticleLikeResponse
import com.shihcheeng.sspaiyou.api.data.actrcle.comment.ArticleCommentDataModel
import com.shihcheeng.sspaiyou.api.data.actrcle.comment.CommentPostRequest
import com.shihcheeng.sspaiyou.api.data.actrcle.comment.CommentPostResponse
import com.shihcheeng.sspaiyou.api.service.ArticleService
import com.shihcheeng.sspaiyou.repository.DetailRepository
import com.shihcheeng.sspaiyou.repository.FakeHistoryDAO
import com.shihcheeng.sspaiyou.repository.HistoryRepository
import com.shihcheeng.sspaiyou.ui.screen.detail.DetailBottomBar
import com.shihcheeng.sspaiyou.ui.screen.detail.DetailMarkDownParser
import com.shihcheeng.sspaiyou.utils.LocalTokenManager
import com.shihcheeng.sspaiyou.utils.NetState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.IOException
import java.util.UUID
import java.util.concurrent.CopyOnWriteArrayList

class DetailFavouriteTest {
    @get:Rule
    val compose = createComposeRule()

    private val store = ViewModelStore()
    private lateinit var testContext: Context
    private lateinit var tokenManager: LocalTokenManager
    private lateinit var viewModel: DetailViewModel
    private lateinit var service: FakeArticleService
    private lateinit var historyRepository: HistoryRepository

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val preferencesName = "detail-favourite-test-${UUID.randomUUID()}"
        // Never read or change the app's real login session during tests.
        testContext = object : ContextWrapper(context) {
            override fun getSharedPreferences(name: String, mode: Int) =
                super.getSharedPreferences(preferencesName, mode)
        }
        tokenManager = LocalTokenManager(testContext)
        service = FakeArticleService()
        historyRepository = HistoryRepository(FakeHistoryDAO())
    }

    @After
    fun tearDown() {
        compose.runOnIdle { store.clear() }
        testContext.getSharedPreferences("TOKEN_MANAGER", Context.MODE_PRIVATE).edit().clear().commit()
    }

    @Test
    fun loggedOutDisablesBookmarkAndRejectsDirectAction() {
        createViewModel(token = null)
        showBottomBar()
        compose.onNodeWithText("收藏").assertIsNotEnabled().performClick()
        compose.runOnIdle {
            viewModel.toggleFavourite()
            assertTrue(service.requests.isEmpty())
            assertFalse(viewModel.isFavouriteUpdating.value)
        }
    }

    @Test
    fun tokenChangesEnableButtonButBlankTokenDisablesItAgain() {
        createViewModel(token = null)
        showBottomBar()
        compose.onNodeWithText("收藏").assertIsNotEnabled()
        runBlocking { tokenManager.saveToken("test-session") }
        compose.waitUntil { viewModel.token.value == "test-session" }
        compose.onNodeWithText("收藏").assertIsEnabled()
        runBlocking { tokenManager.saveToken(" ") }
        compose.waitUntil { viewModel.token.value == " " }
        compose.onNodeWithText("收藏").assertIsNotEnabled()
        compose.runOnIdle { viewModel.toggleFavourite() }
        assertTrue(service.requests.isEmpty())
    }

    @Test
    fun loadedFavouriteCanBeCancelledAndAddedAgainThroughButton() {
        service.article = service.article.copy(favorited = true)
        createViewModel()
        showBottomBar()
        compose.onNodeWithText("已收藏").assertIsOn().performClick()
        compose.waitUntil { !article().favorited && !viewModel.isFavouriteUpdating.value }
        compose.onNodeWithText("收藏").assertIsOff().performClick()
        compose.waitUntil { article().favorited && !viewModel.isFavouriteUpdating.value }
        compose.onNodeWithText("已收藏").assertIsOn().assertIsEnabled()
        assertEquals(listOf(ArticleFavouriteRequest(115074, 2), ArticleFavouriteRequest(115074, 1)), service.requests)
    }

    @Test
    fun pendingRequestDisablesButtonAndRejectsDuplicatesWithoutOptimisticUpdate() {
        val pending = CompletableDeferred<ArticleFavouriteResponse>()
        service.pending = pending
        createViewModel()
        showBottomBar()
        compose.runOnIdle {
            viewModel.toggleFavourite()
            viewModel.toggleFavourite()
            assertTrue(viewModel.isFavouriteUpdating.value)
            assertFalse(article().favorited)
        }
        compose.onNodeWithText("收藏").assertIsNotEnabled().performClick()
        assertEquals(1, service.requests.size)
        pending.complete(ArticleFavouriteResponse(0))
        compose.waitUntil { article().favorited && !viewModel.isFavouriteUpdating.value }
        compose.onNodeWithText("已收藏").assertIsOn().assertIsEnabled()
    }

    @Test
    fun businessErrorKeepsFavouriteAndAllowsRetry() {
        service.article = service.article.copy(favorited = true)
        service.response = ArticleFavouriteResponse(403)
        createViewModel()
        compose.runOnIdle { viewModel.toggleFavourite() }
        compose.waitUntil { viewModel.favouriteError.value }
        assertTrue(article().favorited)
        assertFalse(viewModel.isFavouriteUpdating.value)
        compose.runOnIdle {
            viewModel.clearFavouriteError()
            service.response = ArticleFavouriteResponse(0)
            viewModel.toggleFavourite()
        }
        compose.waitUntil { !article().favorited && !viewModel.isFavouriteUpdating.value }
        assertFalse(viewModel.favouriteError.value)
        assertEquals(listOf(2, 2), service.requests.map { it.flag })
    }

    @Test
    fun networkErrorDoesNotChangeFavourite() {
        service.failure = IOException("offline")
        createViewModel()
        compose.runOnIdle { viewModel.toggleFavourite() }
        compose.waitUntil { viewModel.favouriteError.value }
        assertFalse(article().favorited)
        assertFalse(viewModel.isFavouriteUpdating.value)
    }

    @Test
    fun cancellationResetsPendingStateWithoutShowingFailure() {
        service.failure = CancellationException("cancelled")
        createViewModel()
        compose.runOnIdle { viewModel.toggleFavourite() }
        compose.waitUntil { service.requests.isNotEmpty() && !viewModel.isFavouriteUpdating.value }
        assertFalse(article().favorited)
        assertFalse(viewModel.favouriteError.value)
    }

    @Test
    fun memberArticleUsesResolvedNumericIdRatherThanRouteSlug() {
        createViewModel(route = "member-article")
        compose.runOnIdle { viewModel.toggleFavourite() }
        compose.waitUntil { article().favorited }
        assertEquals("member-article", service.requestedSlug)
        assertEquals(ArticleFavouriteRequest(115074, 1), service.requests.single())
    }

    @Test
    fun invalidLoadedArticleIdCannotBeFavourited() {
        service.article = service.article.copy(id = 0)
        createViewModel()
        compose.runOnIdle { viewModel.toggleFavourite() }
        assertTrue(service.requests.isEmpty())
        assertFalse(viewModel.isFavouriteUpdating.value)
    }

    @Test
    fun responseFromOldLoginSessionDoesNotChangeCurrentState() {
        val pending = CompletableDeferred<ArticleFavouriteResponse>()
        service.pending = pending
        createViewModel()
        compose.runOnIdle { viewModel.toggleFavourite() }
        compose.waitUntil { service.requests.isNotEmpty() }
        runBlocking { tokenManager.saveToken("another-session") }
        pending.complete(ArticleFavouriteResponse(0))
        compose.waitUntil { !viewModel.isFavouriteUpdating.value }
        assertFalse(article().favorited)
        assertFalse(viewModel.favouriteError.value)
    }

    private fun createViewModel(token: String? = "test-session", route: String = "115074") {
        if (token != null) runBlocking { tokenManager.saveToken(token) }
        val factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T = DetailViewModel(
                SavedStateHandle(mapOf("id" to route)),
                DetailRepository(service),
                DetailMarkDownParser(),
                tokenManager,
                historyRepository
            ) as T
        }
        compose.runOnIdle {
            viewModel = ViewModelProvider(store, factory)[DetailViewModel::class.java]
        }
        compose.waitUntil(10_000) { viewModel.data.value is NetState.Success }
    }

    private fun article() = (viewModel.data.value as NetState.Success).content

    private fun showBottomBar() {
        compose.setContent {
            val data by viewModel.data.collectAsStateWithLifecycle()
            val token by viewModel.token.collectAsStateWithLifecycle()
            val pending by viewModel.isFavouriteUpdating.collectAsStateWithLifecycle()
            MaterialTheme {
                DetailBottomBar(
                    bottomAppBarScrollBehavior = BottomAppBarDefaults.exitAlwaysScrollBehavior(),
                    commentCount = 0,
                    likeCount = 0,
                    onShareCommit = {},
                    onBookmarkClick = viewModel::toggleFavourite,
                    onTocClick = {},
                    onViewComments = {},
                    onLikeClick = viewModel::toggleLike,
                    isLike = (data as NetState.Success).content.liked,
                    isBookmarked = (data as NetState.Success).content.favorited,
                    bookmarkEnabled = !token.isNullOrBlank() && !pending,
                    onPostCommentClick = {}
                )
            }
        }
    }

    private class FakeArticleService : ArticleService {
        var article = ArticleData(id = 115074, bodyExtends = emptyList())
        var response = ArticleFavouriteResponse(0)
        var failure: Exception? = null
        var pending: CompletableDeferred<ArticleFavouriteResponse>? = null
        var requestedSlug: String? = null
        val requests = CopyOnWriteArrayList<ArticleFavouriteRequest>()

        override suspend fun getArticleDetail(id: String, view: String, supportWebp: Boolean) =
            ArticleDataModel(data = article)

        override suspend fun getMemberArticleDetail(slug: String, view: String, supportWebp: Boolean): ArticleDataModel {
            requestedSlug = slug
            return ArticleDataModel(data = article)
        }

        override suspend fun getArticleComments(limit: Int, offset: Int, createdAt: Long, articleId: Int) =
            ArticleCommentDataModel()

        override suspend fun getArticleBlockIDComments(
            limit: Int,
            offset: Int,
            createdAt: Long,
            articleId: Int,
            articleBodyExtendId: Int,
        ): ArticleCommentDataModel = error("Unexpected block comments request")

        override suspend fun favouriteOrNot(request: ArticleFavouriteRequest): ArticleFavouriteResponse {
            requests += request
            failure?.let { throw it }
            return pending?.await() ?: response
        }

        override suspend fun likeItOrNot(request: ArticleLikeRequest): ArticleLikeResponse =
            error("Unexpected like request")

        override suspend fun postComment(
            url: String,
            request: CommentPostRequest
        ): CommentPostResponse {
            return CommentPostResponse(
                data = CommentPostResponse.CommentPostResponseData(
                    id = article.id,
                    status = 3,
                    success = true
                ),
                error = 0
            )
        }
    }
}
