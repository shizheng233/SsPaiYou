package com.shihcheeng.sspaiyou.column

import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.shihcheeng.sspaiyou.api.data.column.ColumnArticle
import com.shihcheeng.sspaiyou.api.data.column.ColumnArticleDataModel
import com.shihcheeng.sspaiyou.api.data.column.ColumnDataModel
import com.shihcheeng.sspaiyou.api.data.column.ColumnInfo
import com.shihcheeng.sspaiyou.api.data.column.ColumnSingleDataModel
import com.shihcheeng.sspaiyou.api.service.ColumnService
import com.shihcheeng.sspaiyou.paging.ColumnArticlePaging
import com.shihcheeng.sspaiyou.repository.ColumnRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException

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
        assertEquals(0, paging.getRefreshKey(PagingState(listOf(first, second, end), 2, PagingConfig(10), 0)))
    }

    @Test
    fun knownTotalStopsAtLastArticleWithoutRequestingAnEmptyPage() = runBlocking {
        val service = FakeColumnService()
        val paging = ColumnArticlePaging(service, 245)
        service.articles = ColumnArticleDataModel(List(2) { ColumnArticle(id = it + 1) }, 0, total = 3)
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
        val repository = ColumnRepository(service)
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
        service.detail = ColumnSingleDataModel(error = 0)
        assertThrows(IllegalStateException::class.java) { runBlocking { repository.getColumn(245) } }
        val cancellation = CancellationException("cancelled")
        service.failure = cancellation
        assertSame(cancellation, assertThrows(CancellationException::class.java) {
            runBlocking { repository.getColumn(245) }
        })
    }

    private class FakeColumnService : ColumnService {
        var detail = ColumnSingleDataModel(error = 0)
        var articles = ColumnArticleDataModel(error = 0)
        var failure: Exception? = null
        var detailId = 0
        var detailLimit = 0
        var detailView = ""
        val requests = mutableListOf<ArticleRequest>()

        override suspend fun getColumn(limit: Int, offset: Int, createdAt: Long): ColumnDataModel =
            error("Column directory is not used by detail requests")

        override suspend fun getColumnSingle(id: Int, limit: Int, view: String): ColumnSingleDataModel {
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
    }

    private data class ArticleRequest(
        val columnId: Int,
        val limit: Int,
        val offset: Int,
        val createdAt: Long,
        val view: String
    )
}
