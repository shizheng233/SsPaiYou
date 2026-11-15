package com.shihcheeng.sspaiyou.column

import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.shihcheeng.sspaiyou.api.data.column.ColumnArticle
import com.shihcheeng.sspaiyou.api.data.column.ColumnArticleDataModel
import com.shihcheeng.sspaiyou.api.data.column.ColumnDataModel
import com.shihcheeng.sspaiyou.api.data.column.ColumnFollowRequest
import com.shihcheeng.sspaiyou.api.data.column.ColumnFollowResponse
import com.shihcheeng.sspaiyou.api.data.column.ColumnInfo
import com.shihcheeng.sspaiyou.api.data.column.ColumnSingleDataModel
import com.shihcheeng.sspaiyou.api.service.ColumnService
import com.shihcheeng.sspaiyou.paging.ColumnArticlePaging
import com.shihcheeng.sspaiyou.repository.ColumnRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class ColumnDataLayerTest {
    @Test
    fun shortPagesContinueUsingActualOffsetsAndOneTimestampUntilEmpty() = runBlocking {
        val service = FakeColumnService()
        val before = System.currentTimeMillis() / 1000
        val paging = ColumnArticlePaging(service, 245)
        service.articles = ColumnArticleDataModel(List(2) { ColumnArticle(id = it + 1) }, 0)
        val first = paging.load(PagingSource.LoadParams.Refresh(null, 10, false))
                as PagingSource.LoadResult.Page
        assertEquals(listOf(1, 2), first.data.map { it.id })
        assertEquals(2, first.nextKey)
        assertNull(first.prevKey)

        service.articles = ColumnArticleDataModel(listOf(ColumnArticle(id = 3)), 0)
        val second = paging.load(PagingSource.LoadParams.Append(2, 10, false))
                as PagingSource.LoadResult.Page
        assertEquals(3, second.nextKey)

        service.articles = ColumnArticleDataModel(error = 0)
        val end = paging.load(PagingSource.LoadParams.Append(3, 10, false))
                as PagingSource.LoadResult.Page
        assertTrue(end.data.isEmpty())
        assertNull(end.nextKey)
        assertEquals(listOf(0, 2, 3), service.requests.map { it.offset })
        assertTrue(service.requests.all { it.limit == 10 && it.columnId == 245 && it.view == "second" })
        val timestamp = service.requests.first().createdAt
        assertTrue(timestamp in before..(System.currentTimeMillis() / 1000))
        assertTrue(service.requests.all { it.createdAt == timestamp })
        assertEquals(
            0,
            paging.getRefreshKey(PagingState(listOf(first, second, end), 2, PagingConfig(10), 0))
        )
    }

    @Test
    fun knownTotalStopsAtLastArticleWithoutRequestingAnEmptyPage() = runBlocking {
        val service = FakeColumnService()
        val paging = ColumnArticlePaging(service, 245)
        service.articles =
            ColumnArticleDataModel(List(2) { ColumnArticle(id = it + 1) }, 0, total = 3)
        val first = paging.load(PagingSource.LoadParams.Refresh(null, 10, false))
                as PagingSource.LoadResult.Page
        assertEquals(2, first.nextKey)

        service.articles = ColumnArticleDataModel(listOf(ColumnArticle(id = 3)), 0, total = 3)
        val last = paging.load(PagingSource.LoadParams.Append(2, 10, false))
                as PagingSource.LoadResult.Page
        assertEquals(listOf(3), last.data.map { it.id })
        assertNull(last.nextKey)
    }

    @Test
    fun initiallyEmptyColumnHasNoNextPage() = runBlocking {
        val page = ColumnArticlePaging(FakeColumnService(), 245)
            .load(PagingSource.LoadParams.Refresh(null, 10, false)) as PagingSource.LoadResult.Page
        assertTrue(page.data.isEmpty())
        assertNull(page.prevKey)
        assertNull(page.nextKey)
    }

    @Test
    fun apiAndNetworkErrorsAreRetryableAndCancellationPropagates() = runBlocking {
        val service = FakeColumnService()
        val paging = ColumnArticlePaging(service, 245)
        service.articles = ColumnArticleDataModel(error = 403, msg = "Access denied")
        val api = paging.load(PagingSource.LoadParams.Refresh(null, 10, false))
                as PagingSource.LoadResult.Error
        assertEquals("Access denied", api.throwable.message)
        service.articles = ColumnArticleDataModel(error = 503)
        val fallback = paging.load(PagingSource.LoadParams.Refresh(null, 10, false))
                as PagingSource.LoadResult.Error
        assertTrue(fallback.throwable.message!!.contains("503"))

        val network = IOException("offline")
        service.failure = network
        val failed = paging.load(PagingSource.LoadParams.Refresh(null, 10, false))
                as PagingSource.LoadResult.Error
        assertSame(network, failed.throwable)
        service.failure = null
        service.articles = ColumnArticleDataModel(listOf(ColumnArticle(id = 99595)), 0)
        val retried = paging.load(PagingSource.LoadParams.Refresh(null, 10, false))
                as PagingSource.LoadResult.Page
        assertEquals(99595, retried.data.single().id)

        val cancellation = CancellationException("cancelled")
        service.failure = cancellation
        assertSame(cancellation, assertThrows(CancellationException::class.java) {
            runBlocking { paging.load(PagingSource.LoadParams.Refresh(null, 10, false)) }
        })
    }

    @Test
    fun repositoryValidatesDetailAndUsesServiceDefaults() {
        val service = FakeColumnService()
        val repository = ColumnRepository(service, readToken = { null })
        val detail = ColumnInfo(id = 245, title = "科研利器")
        service.detail = ColumnSingleDataModel(detail, 0)
        assertSame(detail, runBlocking { repository.getColumn(245) })
        assertEquals(245, service.detailId)
        assertEquals(4, service.detailLimit)
        assertEquals("second", service.detailView)

        service.detail = ColumnSingleDataModel(detail, 403, "Access denied")
        val denied = assertThrows(IllegalStateException::class.java) {
            runBlocking { repository.getColumn(245) }
        }
        assertEquals("Access denied", denied.message)
        service.detail = ColumnSingleDataModel(error = 503)
        val fallback = assertThrows(IllegalStateException::class.java) {
            runBlocking { repository.getColumn(245) }
        }
        assertEquals("Column API error: 503", fallback.message)
        service.detail = ColumnSingleDataModel(error = 0)
        val missing = assertThrows(IllegalStateException::class.java) {
            runBlocking { repository.getColumn(245) }
        }
        assertEquals("Column data is missing", missing.message)

        val network = IOException("offline")
        service.failure = network
        assertSame(network, assertThrows(IOException::class.java) {
            runBlocking { repository.getColumn(245) }
        })
        val cancellation = CancellationException("cancelled")
        service.failure = cancellation
        assertSame(cancellation, assertThrows(CancellationException::class.java) {
            runBlocking { repository.getColumn(245) }
        })
    }

    @Test
    fun repositoryMapsFollowFlagsAndPublishesSuccessfulChanges() = runTest {
        val service = FakeColumnService()
        val tokens = FakeTokenProvider()
        val repository = ColumnRepository(service, tokens::getToken)
        val changes = collectFollowChanges(repository)

        repository.setFollowed(245, true)
        repository.setFollowed(245, false)
        runCurrent()

        assertEquals(listOf(ColumnFollowRequest(245, 1), ColumnFollowRequest(245, 2)), service.followRequests)
        assertEquals(listOf(245 to true, 245 to false), changes)
        assertTrue(repository.followPending.value.isEmpty())
    }

    @Test
    fun repositorySkipsFollowWhenTokenIsMissingOrBlank() = runTest {
        val service = FakeColumnService()
        val tokens = FakeTokenProvider()
        val repository = ColumnRepository(service, tokens::getToken)
        val changes = collectFollowChanges(repository)

        for (token in listOf(null, "", " \t\n ")) {
            tokens.currentToken = token
            repository.setFollowed(245, true)
            repository.setFollowed(245, false)
            assertTrue(repository.followPending.value.isEmpty())
        }
        runCurrent()

        assertTrue(service.followRequests.isEmpty())
        assertTrue(changes.isEmpty())
    }

    @Test
    fun repositoryRejectsInvalidFollowIdsBeforeReadingTokenOrCallingService() = runTest {
        val service = FakeColumnService()
        val repository = ColumnRepository(service, readToken = { error("Token must not be read") })
        val changes = collectFollowChanges(repository)

        for (id in listOf(0, -1)) {
            val failure = assertThrows(IllegalArgumentException::class.java) {
                runBlocking { repository.setFollowed(id, true) }
            }
            assertEquals("Invalid column ID", failure.message)
        }
        runCurrent()

        assertTrue(service.followRequests.isEmpty())
        assertTrue(changes.isEmpty())
        assertTrue(repository.followPending.value.isEmpty())
    }

    @Test
    fun followApiErrorsClearPendingWithoutPublishingAndAllowRetry() = runTest {
        val service = FakeColumnService()
        val tokens = FakeTokenProvider()
        val repository = ColumnRepository(service, tokens::getToken)
        val changes = collectFollowChanges(repository)

        for ((response, message) in listOf(
            ColumnFollowResponse(403, "Access denied") to "Access denied",
            ColumnFollowResponse(503) to "Column follow API error: 503"
        )) {
            service.followResponse = response
            val failure = assertThrows(IllegalStateException::class.java) {
                runBlocking { repository.setFollowed(245, true) }
            }
            assertEquals(message, failure.message)
            runCurrent()
            assertTrue(changes.isEmpty())
            assertTrue(repository.followPending.value.isEmpty())
        }

        service.followResponse = ColumnFollowResponse(0)
        repository.setFollowed(245, true)
        runCurrent()
        assertEquals(List(3) { ColumnFollowRequest(245, 1) }, service.followRequests)
        assertEquals(listOf(245 to true), changes)
        assertTrue(repository.followPending.value.isEmpty())
    }

    @Test
    fun followNetworkAndCancellationErrorsPropagateAndAllowRetry() = runTest {
        val service = FakeColumnService()
        val tokens = FakeTokenProvider()
        val repository = ColumnRepository(service, tokens::getToken)
        val changes = collectFollowChanges(repository)

        for (failure in listOf(IOException("offline"), CancellationException("cancelled"))) {
            service.failure = failure
            assertSame(failure, assertThrows(Exception::class.java) {
                runBlocking { repository.setFollowed(245, true) }
            })
            runCurrent()
            assertTrue(changes.isEmpty())
            assertTrue(repository.followPending.value.isEmpty())
        }

        service.failure = null
        repository.setFollowed(245, true)
        runCurrent()
        assertEquals(List(3) { ColumnFollowRequest(245, 1) }, service.followRequests)
        assertEquals(listOf(245 to true), changes)
        assertTrue(repository.followPending.value.isEmpty())
    }

    @Test
    fun pendingFollowSuppressesSameColumnDuplicatesButAllowsDifferentColumns() = runTest {
        val service = FakeColumnService()
        val tokens = FakeTokenProvider()
        val repository = ColumnRepository(service, tokens::getToken)
        val changes = collectFollowChanges(repository)
        val gate = CompletableDeferred<Unit>()
        service.followGate = gate

        val first = launch { repository.setFollowed(245, true) }
        runCurrent()
        assertEquals(setOf(245), repository.followPending.value)
        repository.setFollowed(245, false)
        assertEquals(setOf(245), repository.followPending.value)
        assertEquals(listOf(ColumnFollowRequest(245, 1)), service.followRequests)

        val second = launch { repository.setFollowed(246, true) }
        runCurrent()
        assertEquals(setOf(245, 246), repository.followPending.value)
        assertEquals(listOf(ColumnFollowRequest(245, 1), ColumnFollowRequest(246, 1)), service.followRequests)
        assertTrue(changes.isEmpty())

        gate.complete(Unit)
        first.join()
        second.join()
        runCurrent()
        assertEquals(setOf(245 to true, 246 to true), changes.toSet())
        assertEquals(2, changes.size)
        assertTrue(repository.followPending.value.isEmpty())

        repository.setFollowed(245, false)
        runCurrent()
        assertEquals(ColumnFollowRequest(245, 2), service.followRequests.last())
        assertEquals(3, service.followRequests.size)
        assertEquals(245 to false, changes.last())
        assertTrue(repository.followPending.value.isEmpty())
    }

    @Test
    fun cancellingSuspendedFollowClearsPendingAndAllowsRetry() = runTest {
        val service = FakeColumnService()
        val tokens = FakeTokenProvider()
        val repository = ColumnRepository(service, tokens::getToken)
        val changes = collectFollowChanges(repository)
        service.followGate = CompletableDeferred()

        val pending = launch { repository.setFollowed(245, true) }
        runCurrent()
        assertEquals(setOf(245), repository.followPending.value)
        pending.cancelAndJoin()
        runCurrent()
        assertTrue(changes.isEmpty())
        assertTrue(repository.followPending.value.isEmpty())

        service.followGate = null
        repository.setFollowed(245, true)
        runCurrent()
        assertEquals(List(2) { ColumnFollowRequest(245, 1) }, service.followRequests)
        assertEquals(listOf(245 to true), changes)
        assertTrue(repository.followPending.value.isEmpty())
    }

    @Test
    fun sessionChangesDuringFollowDiscardStaleResultsAndAllowANewRequest() = runTest {
        val service = FakeColumnService()
        val tokens = FakeTokenProvider()
        val repository = ColumnRepository(service, tokens::getToken)
        val changes = collectFollowChanges(repository)

        for (newToken in listOf(null, " ", "other-session")) {
            tokens.currentToken = "original-session"
            val gate = CompletableDeferred<Unit>()
            service.followGate = gate
            val pending = launch { repository.setFollowed(245, true) }
            runCurrent()
            assertEquals(setOf(245), repository.followPending.value)

            tokens.currentToken = newToken
            gate.complete(Unit)
            pending.join()
            runCurrent()
            assertTrue(changes.isEmpty())
            assertTrue(repository.followPending.value.isEmpty())
        }

        service.followGate = null
        tokens.currentToken = "new-session"
        repository.setFollowed(245, true)
        runCurrent()
        assertEquals(List(4) { ColumnFollowRequest(245, 1) }, service.followRequests)
        assertEquals(listOf(245 to true), changes)
        assertTrue(repository.followPending.value.isEmpty())
    }

    private fun TestScope.collectFollowChanges(repository: ColumnRepository): List<Pair<Int, Boolean>> {
        val changes = mutableListOf<Pair<Int, Boolean>>()
        // SharedFlow has no replay: subscribe before invoking the operation under test.
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            repository.followChanges.toList(changes)
        }
        return changes
    }

    /** In-memory session state for JVM tests; null models logout, another value models account switching. */
    private class FakeTokenProvider(var currentToken: String? = "test-session") {
        fun getToken(): String? = currentToken
    }

    private class FakeColumnService : ColumnService {
        var detail = ColumnSingleDataModel(error = 0)
        var articles = ColumnArticleDataModel(error = 0)
        var followResponse = ColumnFollowResponse(0, "成功")
        var followGate: CompletableDeferred<Unit>? = null
        var failure: Exception? = null
        var detailId = 0
        var detailLimit = 0
        var detailView = ""
        val requests = mutableListOf<ArticleRequest>()
        val followRequests = mutableListOf<ColumnFollowRequest>()

        override suspend fun getColumn(limit: Int, offset: Int, createdAt: Long): ColumnDataModel =
            error("Column directory is not used by detail requests")

        override suspend fun getColumnSingle(
            id: Int,
            limit: Int,
            view: String
        ): ColumnSingleDataModel {
            failure?.let { throw it }
            detailId = id
            detailLimit = limit
            detailView = view
            return detail
        }

        override suspend fun getColumnSingleArticle(
            specialColumnId: Int,
            limit: Int,
            offset: Int,
            createdAt: Long,
            view: String
        ): ColumnArticleDataModel {
            requests += ArticleRequest(specialColumnId, limit, offset, createdAt, view)
            failure?.let { throw it }
            return articles
        }

        override suspend fun followColumn(request: ColumnFollowRequest): ColumnFollowResponse {
            followRequests += request
            followGate?.await()
            failure?.let { throw it }
            return followResponse
        }
    }

    private data class ArticleRequest(
        val columnId: Int,
        val limit: Int,
        val offset: Int,
        val createdAt: Long,
        val view: String
    )
}
