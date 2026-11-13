package com.shihcheeng.sspaiyou.search

import androidx.paging.PagingConfig
import androidx.paging.PagingDataEvent
import androidx.paging.PagingDataPresenter
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.shihcheeng.sspaiyou.api.data.column.ColumnArticle
import com.shihcheeng.sspaiyou.api.data.search.SearchArticleDataModel
import com.shihcheeng.sspaiyou.api.service.SearchService
import com.shihcheeng.sspaiyou.modula.ServiceModula
import com.shihcheeng.sspaiyou.paging.SearchArticlePaging
import com.shihcheeng.sspaiyou.repository.SearchRepository
import com.shihcheeng.sspaiyou.utils.SSPaiUrl
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.io.IOException

class SearchDataLayerTest {
    @Test
    fun retrofitEncodesKeywordAndDecodesSourceFixtureWithDefaultFilters() {
        val requests = mutableListOf<Request>()
        val before = System.currentTimeMillis() / 1000
        withService(
            response = requireNotNull(javaClass.getResource("/search_article.json"))
                .readText(Charsets.UTF_8),
            onRequest = { requests += it }
        ) { service ->
            val response = service.searchArticle(title = "游戏 & Android+1")
            assertEquals(0, response.error)
            assertEquals(1000, response.total)
            assertEquals("", response.msg)
            assertEquals(
                listOf(30056, 112962, 23911, 11614, 27487, 46873, 22094, 23380),
                response.data.map { it.id }
            )
            val first = response.data.first()
            assertEquals("小清新外壳，却暗含血腥暴力的游戏~", first.title)
            assertEquals("65p6vfgd", first.author.slug)
            assertEquals(1433401688L, first.releasedTime)
            assertTrue(first.free)
            assertNull(first.bodyExtend.comments)
            assertNull(first.bodyExtend.statuses)
            val second = response.data[1]
            assertEquals("writer", second.author.userFlags.single().key)
            assertEquals("matrix_plan_member", second.author.userPlanFlags.single().key)
            assertEquals(listOf("热门文章", "资讯", "游戏"), second.tags.map { it.title })
        }
        val request = requests.single()
        assertEquals("GET", request.method)
        assertEquals("/api/v1/search/article/page/get", request.url.encodedPath)
        assertEquals("游戏 & Android+1", request.url.queryParameter("title"))
        assertEquals("1", request.url.queryParameter("free"))
        assertEquals("1", request.url.queryParameter("search_types"))
        assertEquals("0", request.url.queryParameter("stime"))
        assertEquals("0", request.url.queryParameter("offset"))
        assertEquals("8", request.url.queryParameter("limit"))
        val endTime = requireNotNull(request.url.queryParameter("etime")).toLong()
        assertTrue(endTime in before..(System.currentTimeMillis() / 1000))
    }

    @Test
    fun retrofitPreservesExplicitTimeRangeAndPaginationParameters() {
        val requests = mutableListOf<Request>()
        withService(onRequest = { requests += it }) { service ->
            service.searchArticle(
                title = "游戏",
                stime = 2200000000L,
                etime = 2300000000L,
                offset = 16,
                limit = 24
            )
        }
        val url = requests.single().url
        assertEquals("2200000000", url.queryParameter("stime"))
        assertEquals("2300000000", url.queryParameter("etime"))
        assertEquals("16", url.queryParameter("offset"))
        assertEquals("24", url.queryParameter("limit"))
    }

    @Test
    fun shortPagesAdvanceByActualCountsAndStopAtTotal() = runBlocking {
        val service = FakeSearchService()
        val before = System.currentTimeMillis() / 1000
        val paging = SearchArticlePaging(service, "游戏")
        service.response = SearchArticleDataModel(List(2) { ColumnArticle(id = it + 1) }, 0, total = 3)
        val first = paging.load(PagingSource.LoadParams.Refresh(null, 24, false))
            as PagingSource.LoadResult.Page
        assertEquals(listOf(1, 2), first.data.map { it.id })
        assertEquals(2, first.nextKey)
        assertNull(first.prevKey)

        service.response = SearchArticleDataModel(listOf(ColumnArticle(id = 3)), 0, total = 3)
        val last = paging.load(PagingSource.LoadParams.Append(requireNotNull(first.nextKey), 8, false))
            as PagingSource.LoadResult.Page
        assertEquals(listOf(3), last.data.map { it.id })
        assertNull(last.nextKey)
        assertNull(last.prevKey)
        assertEquals(listOf(0, 2), service.requests.map { it.offset })
        assertEquals(listOf(24, 8), service.requests.map { it.limit })
        assertTrue(service.requests.all {
            it.title == "游戏" && it.free == 1 && it.searchType == "1" && it.stime == 0L
        })
        val endTime = service.requests.first().etime
        assertTrue(endTime in before..(System.currentTimeMillis() / 1000))
        assertTrue(service.requests.all { it.etime == endTime })
        assertEquals(0, paging.getRefreshKey(PagingState(listOf(first, last), 2, PagingConfig(8), 0)))
    }

    @Test
    fun missingTotalContinuesUntilEmptyAndEmptyFirstPageAlsoStops() = runBlocking {
        val service = FakeSearchService()
        val paging = SearchArticlePaging(service, "游戏")
        service.response = SearchArticleDataModel(listOf(ColumnArticle(id = 1)), 0)
        val first = paging.load(PagingSource.LoadParams.Refresh(null, 8, false))
            as PagingSource.LoadResult.Page
        assertEquals(1, first.nextKey)

        service.response = SearchArticleDataModel(error = 0)
        val last = paging.load(PagingSource.LoadParams.Append(requireNotNull(first.nextKey), 8, false))
            as PagingSource.LoadResult.Page
        assertTrue(last.data.isEmpty())
        assertNull(last.nextKey)

        service.response = SearchArticleDataModel(error = 0, total = 1000)
        val empty = SearchArticlePaging(service, "没有结果")
            .load(PagingSource.LoadParams.Refresh(null, 8, false)) as PagingSource.LoadResult.Page
        assertTrue(empty.data.isEmpty())
        assertNull(empty.prevKey)
        assertNull(empty.nextKey)
    }

    @Test
    fun apiAndNetworkErrorsAreRetryableAndCancellationPropagates() = runBlocking {
        val service = FakeSearchService()
        val paging = SearchArticlePaging(service, "游戏")
        service.response = SearchArticleDataModel(error = 403, msg = "Access denied")
        val api = paging.load(PagingSource.LoadParams.Refresh(null, 8, false))
            as PagingSource.LoadResult.Error
        assertEquals("Access denied", api.throwable.message)

        service.response = SearchArticleDataModel(error = 503)
        val fallback = paging.load(PagingSource.LoadParams.Refresh(null, 8, false))
            as PagingSource.LoadResult.Error
        assertTrue(fallback.throwable.message.orEmpty().contains("503"))

        val network = IOException("offline")
        service.failure = network
        val failed = paging.load(PagingSource.LoadParams.Refresh(null, 8, false))
            as PagingSource.LoadResult.Error
        assertSame(network, failed.throwable)

        service.failure = null
        service.response = SearchArticleDataModel(listOf(ColumnArticle(id = 30056)), 0)
        val retried = paging.load(PagingSource.LoadParams.Refresh(null, 8, false))
            as PagingSource.LoadResult.Page
        assertEquals(30056, retried.data.single().id)

        val cancellation = CancellationException("cancelled")
        service.failure = cancellation
        assertSame(cancellation, assertThrows(CancellationException::class.java) {
            runBlocking { paging.load(PagingSource.LoadParams.Refresh(null, 8, false)) }
        })
    }

    @Test
    fun responseRequiresErrorCodeAndAllowsMissingOptionalFields() {
        val response = Json.decodeFromString<SearchArticleDataModel>("""{"error":403}""")
        assertTrue(response.data.isEmpty())
        assertEquals("", response.msg)
        assertNull(response.total)
        assertThrows(SerializationException::class.java) {
            Json.decodeFromString<SearchArticleDataModel>("""{"data":[],"total":0}""")
        }
    }

    @Test
    fun repositoryLoadsIndependentSearchesWithEightArticlePages() = runBlocking {
        val service = FakeSearchService()
        val repository = SearchRepository(service)
        for ((index, title) in listOf("游戏", "Android").withIndex()) {
            val article = ColumnArticle(id = index + 1, title = title)
            service.response = SearchArticleDataModel(listOf(article), 0, total = 1)
            val loaded = CompletableDeferred<List<ColumnArticle>>()
            val presenter = object : PagingDataPresenter<ColumnArticle>(Dispatchers.Unconfined) {
                override suspend fun presentPagingDataEvent(event: PagingDataEvent<ColumnArticle>) {
                    loaded.complete(snapshot().items)
                }
            }
            val job = launch {
                repository.searchArticle(title).collectLatest { presenter.collectFrom(it) }
            }
            try {
                assertEquals(listOf(article), withTimeout(5000) { loaded.await() })
            } finally {
                job.cancelAndJoin()
            }
        }
        assertEquals(listOf("游戏", "Android"), service.requests.map { it.title })
        assertTrue(service.requests.all { it.offset == 0 && it.limit == 8 })
    }

    private fun withService(
        response: String = """{"data":[],"error":0,"total":0}""",
        onRequest: (Request) -> Unit = {},
        block: suspend (SearchService) -> Unit
    ) = runBlocking {
        val mediaType = "application/json".toMediaType()
        val client = OkHttpClient.Builder().addInterceptor { chain ->
            onRequest(chain.request())
            Response.Builder()
                .request(chain.request())
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .message("test response")
                .body(response.toResponseBody(mediaType))
                .build()
        }.build()
        try {
            val retrofit = Retrofit.Builder()
                .baseUrl(SSPaiUrl.BASE_URL)
                .client(client)
                .addConverterFactory(Json { ignoreUnknownKeys = true }.asConverterFactory(mediaType))
                .build()
            block(ServiceModula.provideSearchService(retrofit))
        } finally {
            client.dispatcher.executorService.shutdown()
            client.connectionPool.evictAll()
        }
    }

    private class FakeSearchService : SearchService {
        var response = SearchArticleDataModel(error = 0)
        var failure: Exception? = null
        val requests = mutableListOf<SearchRequest>()

        override suspend fun searchArticle(
            free: Int,
            title: String,
            searchType: String,
            stime: Long,
            etime: Long,
            offset: Int,
            limit: Int
        ): SearchArticleDataModel {
            requests += SearchRequest(free, title, searchType, stime, etime, offset, limit)
            failure?.let { throw it }
            return response
        }
    }

    private data class SearchRequest(
        val free: Int,
        val title: String,
        val searchType: String,
        val stime: Long,
        val etime: Long,
        val offset: Int,
        val limit: Int
    )
}
