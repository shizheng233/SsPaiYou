package com.shihcheeng.sspaiyou.search

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onLast
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performImeAction
import androidx.compose.ui.test.performTextReplacement
import androidx.lifecycle.ViewModelStore
import com.shihcheeng.sspaiyou.api.data.column.ColumnArticle
import com.shihcheeng.sspaiyou.api.data.search.SearchArticleDataModel
import com.shihcheeng.sspaiyou.api.service.SearchService
import com.shihcheeng.sspaiyou.repository.SearchRepository
import com.shihcheeng.sspaiyou.ui.screen.sreach.SearchScreen
import com.shihcheeng.sspaiyou.ui.screen.sreach.SearchViewModel
import kotlinx.coroutines.CompletableDeferred
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.IOException
import java.util.concurrent.CopyOnWriteArrayList

class SearchScreenTest {
    @get:Rule
    val compose = createComposeRule()

    private val store = ViewModelStore()
    private val service = FakeSearchService()
    private lateinit var viewModel: SearchViewModel
    private var openedArticle: String? = null
    private var backCount = 0

    @Before
    fun setUp() {
        viewModel = SearchViewModel(SearchRepository(service))
        store.put("search", viewModel)
        compose.setContent {
            MaterialTheme {
                SearchScreen(
                    viewModel = viewModel,
                    onClick = { openedArticle = it },
                    onBack = { backCount++ }
                )
            }
        }
    }

    @After
    fun tearDown() {
        compose.runOnIdle { store.clear() }
    }

    @Test
    fun initiallyWaitsWithoutRequestingAndBackStillWorks() {
        compose.onNodeWithText(WAITING).assertIsDisplayed()
        compose.onNodeWithText(EMPTY).assertDoesNotExist()
        compose.runOnIdle { assertTrue(service.queries.isEmpty()) }
        compose.onNodeWithContentDescription("返回上一层级").performClick()
        compose.runOnIdle { assertEquals(1, backCount) }
    }

    @Test
    fun loadingDoesNotShowEmptyAndAnEmptyResponseShowsNoResults() {
        val response = CompletableDeferred<SearchArticleDataModel>()
        service.load = { response.await() }
        submit("  没有匹配的文章  ")
        compose.waitUntil { service.queries.isNotEmpty() }
        compose.onNode(SemanticsMatcher.keyIsDefined(SemanticsProperties.ProgressBarRangeInfo))
            .assertIsDisplayed()
        compose.onNodeWithText(EMPTY).assertDoesNotExist()
        compose.onNodeWithText(WAITING).assertDoesNotExist()

        response.complete(SearchArticleDataModel(error = 0, total = 0))
        waitForText(EMPTY)
        compose.onNodeWithText(EMPTY).assertIsDisplayed()
        compose.runOnIdle { assertEquals(listOf("没有匹配的文章"), service.queries) }
    }

    @Test
    fun failureCanRetryAndOpenTheResult() {
        service.load = {
            if (service.queries.size == 1) throw IOException("offline")
            result("找到的文章")
        }
        submit("游戏")
        waitForText("搜索失败，请重试")
        compose.onNodeWithText(EMPTY).assertDoesNotExist()
        compose.onNodeWithText("重试").performClick()
        waitForText("找到的文章")
        compose.onNodeWithText("找到的文章").performClick()
        compose.runOnIdle {
            assertEquals("42", openedArticle)
            assertEquals(listOf("游戏", "游戏"), service.queries)
        }
    }

    @Test
    fun repeatedSubmissionSearchesAgainAndClearReturnsToWaiting() {
        service.load = { result("找到的文章") }
        submit("游戏")
        waitForText("找到的文章")
        submit("游戏")
        compose.waitUntil { service.queries.size == 2 }
        waitForText("找到的文章")
        compose.onNodeWithContentDescription("清空搜索").performClick()
        compose.onNodeWithText(WAITING).assertIsDisplayed()
        compose.onNodeWithText("找到的文章").assertDoesNotExist()
        compose.onNodeWithText(EMPTY).assertDoesNotExist()
        compose.runOnIdle {
            assertEquals(2, service.queries.size)
            assertTrue(viewModel.searchRequest.value.title.isEmpty())
        }
    }

    private fun submit(query: String) {
        compose.onAllNodes(hasSetTextAction()).onFirst().performClick()
        compose.waitForIdle()
        compose.onAllNodes(hasSetTextAction()).onLast().performTextReplacement(query)
        compose.onAllNodes(hasSetTextAction()).onLast().performImeAction()
        compose.waitForIdle()
    }

    private fun waitForText(text: String) {
        compose.waitUntil(timeoutMillis = 5000) {
            compose.onAllNodesWithText(text).fetchSemanticsNodes().isNotEmpty()
        }
    }

    private class FakeSearchService : SearchService {
        val queries = CopyOnWriteArrayList<String>()
        var load: suspend () -> SearchArticleDataModel = { SearchArticleDataModel(error = 0, total = 0) }

        override suspend fun searchArticle(
            free: Int,
            title: String,
            searchType: String,
            stime: Long,
            etime: Long,
            offset: Int,
            limit: Int
        ): SearchArticleDataModel {
            queries += title
            return load()
        }
    }

    companion object {
        private const val WAITING = "输入关键词，搜索感兴趣的文章"
        private const val EMPTY = "没有搜索结果，换个关键词试试"

        private fun result(title: String) = SearchArticleDataModel(
            data = listOf(ColumnArticle(id = 42, title = title, free = true)),
            error = 0,
            total = 1
        )
    }
}
