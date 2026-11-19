package com.shihcheeng.sspaiyou.home

import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.shihcheeng.sspaiyou.api.data.home.main.HomeMainDataModel
import com.shihcheeng.sspaiyou.api.data.home.main.MainData
import com.shihcheeng.sspaiyou.api.data.home.second.SecondHomeDataModel
import com.shihcheeng.sspaiyou.api.data.home.sub.HomeSubDataModel
import com.shihcheeng.sspaiyou.api.data.home.top.HeaderBannerDataModel
import com.shihcheeng.sspaiyou.api.data.notification.InformationMineResponse
import com.shihcheeng.sspaiyou.api.service.HomeService
import com.shihcheeng.sspaiyou.paging.HomePaging
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException

class HomePagingTest {
    @Test
    fun serverCappedPagesLoadWithoutGapsUsingOneTimestampUntilTotalIsReached() = runBlocking {
        val articles = List(45) { MainData(id = it + 1) }
        val service = FakeHomeService()
        val before = System.currentTimeMillis() / 1000
        val paging = HomePaging(service)
        val after = System.currentTimeMillis() / 1000
        val pages = mutableListOf<PagingSource.LoadResult.Page<Int, MainData>>()
        var key: Int? = null
        repeat(5) { index ->
            service.response = HomeMainDataModel(articles.drop(index * 10).take(10), total = 45)
            val params = if (index == 0) {
                PagingSource.LoadParams.Refresh(key, 30, false)
            } else {
                PagingSource.LoadParams.Append(requireNotNull(key), 10, false)
            }
            val page = paging.load(params) as PagingSource.LoadResult.Page
            assertNull(page.prevKey)
            assertEquals(if (index == 4) null else (index + 1) * 10, page.nextKey)
            pages += page
            key = page.nextKey
        }
        assertEquals(articles, pages.flatMap { it.data })
        assertEquals(listOf(0, 10, 20, 30, 40), service.requests.map { it.offset })
        assertEquals(listOf(30, 10, 10, 10, 10), service.requests.map { it.limit })
        val timestamp = service.requests.first().createdAt
        assertTrue(timestamp in before..after)
        assertTrue(service.requests.all { it.createdAt == timestamp })
        assertEquals(0, paging.getRefreshKey(PagingState(pages, 25, PagingConfig(10), 0)))
    }

    @Test
    fun serverMayReturnMoreItemsThanRequestedWithoutRepeatingTheNextPage() = runBlocking {
        val service = FakeHomeService()
        val paging = HomePaging(service)
        service.response = HomeMainDataModel(List(10) { MainData(id = it + 1) }, total = 11)
        val first = paging.load(PagingSource.LoadParams.Refresh(null, 2, false))
            as PagingSource.LoadResult.Page
        assertEquals(10, first.nextKey)

        service.response = HomeMainDataModel(listOf(MainData(id = 11)), total = 11)
        val last = paging.load(PagingSource.LoadParams.Append(requireNotNull(first.nextKey), 2, false))
            as PagingSource.LoadResult.Page
        assertNull(last.nextKey)
        assertEquals((1..11).toList(), (first.data + last.data).map { it.id })
        assertEquals(listOf(0, 10), service.requests.map { it.offset })
        assertEquals(listOf(2, 2), service.requests.map { it.limit })
    }

    @Test
    fun absentOrNullTotalAllowsShortPagesUntilAnEmptyPage() = runBlocking {
        val service = FakeHomeService()
        val paging = HomePaging(service)
        service.response = Json.decodeFromString<HomeMainDataModel>("""{"error":0,"data":[{"id":1}]}""")
        assertNull(service.response.total)
        val first = paging.load(PagingSource.LoadParams.Refresh(null, 10, false))
            as PagingSource.LoadResult.Page
        assertEquals(1, first.nextKey)

        service.response = Json.decodeFromString<HomeMainDataModel>(
            """{"error":0,"total":null,"data":[{"id":2}]}"""
        )
        val second = paging.load(PagingSource.LoadParams.Append(requireNotNull(first.nextKey), 10, false))
            as PagingSource.LoadResult.Page
        assertEquals(2, second.nextKey)

        service.response = HomeMainDataModel()
        val end = paging.load(PagingSource.LoadParams.Append(requireNotNull(second.nextKey), 10, false))
            as PagingSource.LoadResult.Page
        assertTrue(end.data.isEmpty())
        assertNull(end.nextKey)
        assertEquals(listOf(0, 1, 2), service.requests.map { it.offset })
    }

    @Test
    fun emptyPageEndsImmediatelyEvenWhenTotalHasNotBeenReached() = runBlocking {
        val service = FakeHomeService()
        val paging = HomePaging(service)
        for (total in listOf(null, 0, 77)) {
            service.response = HomeMainDataModel(total = total)
            val page = paging.load(PagingSource.LoadParams.Refresh(null, 10, false))
                as PagingSource.LoadResult.Page
            assertTrue(page.data.isEmpty())
            assertNull(page.prevKey)
            assertNull(page.nextKey)
        }
        assertEquals(0, paging.getRefreshKey(PagingState(emptyList(), null, PagingConfig(10), 0)))
    }

    @Test
    fun explicitRefreshOffsetIsHonouredAndPageCanPassReportedTotal() = runBlocking {
        val service = FakeHomeService()
        service.response = HomeMainDataModel(List(2) { MainData(id = it + 21) }, total = 21)
        val paging = HomePaging(service)
        val page = paging.load(PagingSource.LoadParams.Refresh(20, 7, false))
            as PagingSource.LoadResult.Page
        assertEquals(service.response.data, page.data)
        assertNull(page.prevKey)
        assertNull(page.nextKey)
        assertEquals(20, service.requests.single().offset)
        assertEquals(7, service.requests.single().limit)
        assertEquals(0, paging.getRefreshKey(PagingState(listOf(page), 1, PagingConfig(10), 20)))
    }

    @Test
    fun businessErrorsPreserveMessageOrCodeAndRetryTheSameRequest() = runBlocking {
        val service = FakeHomeService()
        val paging = HomePaging(service)
        val params = PagingSource.LoadParams.Append(7, 10, false)
        service.response = HomeMainDataModel(listOf(MainData(id = 99)), error = 403, msg = "Access denied")
        val denied = paging.load(params) as PagingSource.LoadResult.Error
        assertEquals("Access denied", denied.throwable.message)

        for (message in listOf("", " \t ")) {
            service.response = HomeMainDataModel(error = 503, msg = message)
            val failed = paging.load(params) as PagingSource.LoadResult.Error
            assertTrue(failed.throwable.message!!.contains("503"))
        }
        service.response = HomeMainDataModel(listOf(MainData(id = 8)), total = 8)
        val retried = paging.load(params) as PagingSource.LoadResult.Page
        assertEquals(service.response.data, retried.data)
        assertNull(retried.nextKey)
        assertEquals(4, service.requests.size)
        assertTrue(service.requests.all { it == service.requests.first() })
    }

    @Test
    fun networkFailureIsReturnedUnchangedAndRetryKeepsOffsetAndSnapshot() = runBlocking {
        val service = FakeHomeService()
        val paging = HomePaging(service)
        val params = PagingSource.LoadParams.Append(10, 10, false)
        val network = IOException("offline")
        service.failure = network
        val failed = paging.load(params) as PagingSource.LoadResult.Error
        assertSame(network, failed.throwable)

        service.failure = null
        service.response = HomeMainDataModel(listOf(MainData(id = 11)))
        val retried = paging.load(params) as PagingSource.LoadResult.Page
        assertEquals(service.response.data, retried.data)
        assertEquals(11, retried.nextKey)
        assertEquals(listOf(10, 10), service.requests.map { it.offset })
        assertEquals(service.requests.first(), service.requests.last())
    }

    @Test
    fun cancellationPropagatesInsteadOfBecomingARetryableLoadError() {
        val service = FakeHomeService()
        val cancellation = CancellationException("cancelled")
        service.failure = cancellation
        val paging = HomePaging(service)
        val thrown = assertThrows(CancellationException::class.java) {
            runBlocking { paging.load(PagingSource.LoadParams.Refresh(null, 10, false)) }
        }
        assertSame(cancellation, thrown)
    }

    private class FakeHomeService : HomeService {
        var response = HomeMainDataModel()
        var failure: Exception? = null
        val requests = mutableListOf<HomeRequest>()

        override suspend fun getHome(limit: Int, offset: Int, createdAt: Long): HomeMainDataModel {
            requests += HomeRequest(limit, offset, createdAt)
            failure?.let { throw it }
            return response
        }

        override suspend fun getHomeSub(limit: Int, offset: Int, type: String): HomeSubDataModel =
            error("unused")

        override suspend fun getHomeSecond(view: String): SecondHomeDataModel = error("unused")

        override suspend fun getHomeMain(limit: Int, offset: Int, type: String): HeaderBannerDataModel =
            error("unused")

        override suspend fun getInformation(
            lastMessageFlag: Boolean, community: Boolean, hasBullet: Boolean,
            hasArticleBodyExtendStatus: Boolean, hasArticleCommentStatus: Boolean,
            hasArticleCommentReplyStatus: Boolean
        ): InformationMineResponse = error("unused")
    }

    private data class HomeRequest(val limit: Int, val offset: Int, val createdAt: Long)
}
