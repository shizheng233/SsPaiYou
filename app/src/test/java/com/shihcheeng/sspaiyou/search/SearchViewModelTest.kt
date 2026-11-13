package com.shihcheeng.sspaiyou.search

import androidx.lifecycle.ViewModelStore
import androidx.paging.LoadState
import androidx.paging.PagingDataEvent
import androidx.paging.PagingDataPresenter
import com.shihcheeng.sspaiyou.api.data.column.ColumnArticle
import com.shihcheeng.sspaiyou.api.data.search.SearchArticleDataModel
import com.shihcheeng.sspaiyou.api.service.SearchService
import com.shihcheeng.sspaiyou.repository.SearchRepository
import com.shihcheeng.sspaiyou.ui.screen.sreach.SearchViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val store = ViewModelStore()
    private val service = FakeSearchService()
    private lateinit var viewModel: SearchViewModel
    private val presenter = object : PagingDataPresenter<ColumnArticle>(dispatcher) {
        override suspend fun presentPagingDataEvent(event: PagingDataEvent<ColumnArticle>) = Unit
    }

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        viewModel = SearchViewModel(SearchRepository(service))
        store.put("search", viewModel)
    }

    @After
    fun tearDown() {
        store.clear()
        Dispatchers.resetMain()
    }

    @Test
    fun idleAndWhitespaceDoNotRequestArticles() = runTest {
        collectResults()
        runCurrent()
        assertTrue(viewModel.searchRequest.value.title.isEmpty())
        assertTrue(service.queries.isEmpty())

        viewModel.search(" \t\n ")
        runCurrent()
        assertTrue(viewModel.searchRequest.value.title.isEmpty())
        assertTrue(service.queries.isEmpty())
        assertTrue(presenter.snapshot().isEmpty())
    }

    @Test
    fun trimsKeywordsAndAllowsExplicitlyRepeatingTheSameSearch() = runTest {
        collectResults()
        viewModel.search("  游戏  ")
        runCurrent()
        assertEquals(listOf("游戏"), service.queries)
        assertEquals("游戏", presenter.snapshot().items.single().title)
        val firstRequest = viewModel.searchRequest.value.id

        viewModel.search("游戏")
        runCurrent()
        assertEquals(listOf("游戏", "游戏"), service.queries)
        assertTrue(viewModel.searchRequest.value.id > firstRequest)
    }

    @Test
    fun aNewSearchClearsOldResultsAndCancelsThePreviousRequest() = runTest {
        collectResults()
        viewModel.search("已完成")
        runCurrent()
        assertEquals("已完成", presenter.snapshot().items.single().title)

        val oldResponse = CompletableDeferred<SearchArticleDataModel>()
        val newResponse = CompletableDeferred<SearchArticleDataModel>()
        service.responses["旧关键词"] = oldResponse
        service.responses["新关键词"] = newResponse
        viewModel.search("旧关键词")
        runCurrent()
        assertTrue(presenter.snapshot().isEmpty())
        assertTrue(presenter.loadStateFlow.value?.refresh is LoadState.Loading)

        viewModel.search("新关键词")
        runCurrent()
        assertEquals(listOf("旧关键词"), service.cancelled)
        oldResponse.complete(result("过时结果"))
        newResponse.complete(result("新结果"))
        runCurrent()
        assertEquals(listOf("新结果"), presenter.snapshot().items.map { it.title })
    }

    @Test
    fun clearingSearchCancelsLoadingAndRemovesResults() = runTest {
        collectResults()
        val pending = CompletableDeferred<SearchArticleDataModel>()
        service.responses["游戏"] = pending
        viewModel.search("游戏")
        runCurrent()
        viewModel.search("")
        runCurrent()

        assertTrue(viewModel.searchRequest.value.title.isEmpty())
        assertEquals(listOf("游戏"), service.queries)
        assertEquals(listOf("游戏"), service.cancelled)
        assertTrue(presenter.snapshot().isEmpty())
        pending.complete(result("过时结果"))
        runCurrent()
        assertTrue(presenter.snapshot().isEmpty())
    }

    @Test
    fun collectingAgainReusesCachedResults() = runTest {
        val firstCollection = collectResults()
        viewModel.search("游戏")
        runCurrent()
        firstCollection.cancelAndJoin()
        collectResults()
        runCurrent()

        assertEquals(listOf("游戏"), service.queries)
        assertEquals("游戏", presenter.snapshot().items.single().title)
    }

    private fun TestScope.collectResults() =
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.data.collectLatest { presenter.collectFrom(it) }
        }

    private class FakeSearchService : SearchService {
        val queries = mutableListOf<String>()
        val cancelled = mutableListOf<String>()
        val responses = mutableMapOf<String, CompletableDeferred<SearchArticleDataModel>>()

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
            return try {
                responses[title]?.await() ?: result(title)
            } catch (e: CancellationException) {
                cancelled += title
                throw e
            }
        }
    }

    companion object {
        private fun result(title: String) = SearchArticleDataModel(
            data = listOf(ColumnArticle(id = 1, title = title)),
            error = 0,
            total = 1
        )
    }
}
