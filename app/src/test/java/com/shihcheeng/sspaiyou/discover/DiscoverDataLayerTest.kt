package com.shihcheeng.sspaiyou.discover

import androidx.paging.LoadState
import androidx.paging.PagingDataEvent
import androidx.paging.PagingDataPresenter
import com.shihcheeng.sspaiyou.api.data.column.ColumnArticle
import com.shihcheeng.sspaiyou.api.data.column.ColumnUser
import com.shihcheeng.sspaiyou.api.data.discover.DiscoverPage
import com.shihcheeng.sspaiyou.api.data.discover.FollowedUserActivity
import com.shihcheeng.sspaiyou.api.service.DiscoverService
import com.shihcheeng.sspaiyou.modula.NetworkModula
import com.shihcheeng.sspaiyou.modula.ServiceModula
import com.shihcheeng.sspaiyou.repository.DiscoverRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DiscoverDataLayerTest {
    @Test
    fun retrofitDecodesBothDiscoverResponsesAndSendsPaginationParameters() = runBlocking {
        val hotPath = "/api/v1/article/hot/page/get"
        val activityPath = "/api/v1/information/user/activity/user/page/get"
        val responses = mapOf(
            hotPath to fixture("hot_page.json"),
            activityPath to fixture("user_activity_page_user.json"),
        )
        val requests = mutableListOf<Request>()
        val mediaType = "application/json".toMediaType()
        val client = OkHttpClient.Builder().addInterceptor { chain ->
            val request = chain.request()
            requests += request
            Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .message("OK")
                .body(responses.getValue(request.url.encodedPath).toResponseBody(mediaType))
                .build()
        }.build()
        try {
            val service = ServiceModula.provideDiscoverService(
                retrofit = NetworkModula.provideRetrofit(
                    okHttpClient = client,
                    json = NetworkModula.provideJson(),
                ),
            )
            val hotPage = service.getHotPage(offset = 0, createdAt = 2_200_000_000L)
            val articles = requireNotNull(hotPage.data)
            assertEquals(0, hotPage.error)
            assertEquals(46497, hotPage.total)
            assertEquals(listOf(103701, 119337), articles.map { it.id })
            assertEquals(12, articles.last().bodyExtendTitles.size)

            val activityPage = service.getUserActivityUserPage(
                offset = 8,
                limit = 4,
                createdAt = 2_200_000_000L,
            )
            val activities = requireNotNull(activityPage.data)
            assertEquals(0, activityPage.error)
            assertEquals("", activityPage.msg)
            assertEquals(33142, activityPage.total)
            assertEquals(listOf(119438, 119438, 119418), activities.map { it.article.id })
            assertEquals(
                listOf("release_article", "release_article", "like_article"),
                activities.map { it.key },
            )
            val like = activities.last()
            assertEquals("喜欢了文章", like.action)
            assertEquals(1795412484L, like.createdAt)
            assertTrue(like.memo.isNotBlank())
            assertNotEquals(like.author.slug, like.article.author.slug)

            assertEquals(listOf(hotPath, activityPath), requests.map { it.url.encodedPath })
            assertEquals(listOf("0", "8"), requests.map { it.url.queryParameter("offset") })
            assertEquals(listOf("10", "4"), requests.map { it.url.queryParameter("limit") })
            assertTrue(requests.all {
                it.method == "GET" && it.url.queryParameter("created_at") == "2200000000"
            })
        } finally {
            client.dispatcher.executorService.shutdown()
            client.connectionPool.evictAll()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun repositoryAppendsAfterAShortPageAndRetriesActivityApiErrors() = runTest {
        val service = FakeDiscoverService()
        val repository = DiscoverRepository(discoverService = service)
        val dispatcher = UnconfinedTestDispatcher(testScheduler)
        val hotPresenter = presenter<ColumnArticle>(dispatcher = dispatcher)
        val activityPresenter = presenter<FollowedUserActivity>(dispatcher = dispatcher)
        backgroundScope.launch(dispatcher) {
            repository.getHotArticles().collectLatest { hotPresenter.collectFrom(it) }
        }
        backgroundScope.launch(dispatcher) {
            repository.getFollowedActivities().collectLatest { activityPresenter.collectFrom(it) }
        }
        runCurrent()

        assertEquals(listOf(1, 2), hotPresenter.snapshot().items.map { it.id })
        hotPresenter[1]
        runCurrent()
        assertEquals(listOf(1, 2, 3), hotPresenter.snapshot().items.map { it.id })
        assertTrue(hotPresenter.loadStateFlow.value?.append?.endOfPaginationReached == true)
        assertEquals(listOf(0, 2), service.hotRequests.map { it.offset })
        assertTrue(service.hotRequests.all {
            it.limit == 10 && it.createdAt == service.hotRequests.first().createdAt
        })

        val error = activityPresenter.loadStateFlow.value?.refresh as LoadState.Error
        assertEquals("请先登录", error.error.message)
        val release = FollowedUserActivity(
            key = "release_article",
            author = ColumnUser(slug = "writer"),
            article = ColumnArticle(id = 1),
            createdAt = 2L,
        )
        val like = release.copy(
            key = "like_article",
            author = ColumnUser(slug = "reader"),
            createdAt = 1L,
        )
        service.activities = DiscoverPage(error = 0, data = listOf(release, like), total = 2)
        activityPresenter.retry()
        runCurrent()
        assertEquals(listOf(release, like), activityPresenter.snapshot().items)
        assertEquals(2, service.activityRequests.size)
        assertEquals(service.activityRequests.first(), service.activityRequests.last())
        assertEquals(0, service.activityRequests.first().offset)
        assertEquals(10, service.activityRequests.first().limit)
    }

    private fun fixture(name: String): String =
        requireNotNull(javaClass.getResource("/discover/$name")).readText(Charsets.UTF_8)

    private fun <T : Any> presenter(dispatcher: CoroutineDispatcher) =
        object : PagingDataPresenter<T>(dispatcher) {
            override suspend fun presentPagingDataEvent(event: PagingDataEvent<T>) = Unit
        }

    private class FakeDiscoverService : DiscoverService {
        val hotRequests = mutableListOf<PageRequest>()
        val activityRequests = mutableListOf<PageRequest>()
        var activities = DiscoverPage<FollowedUserActivity>(error = 401, msg = "请先登录")

        override suspend fun getHotPage(
            offset: Int,
            limit: Int,
            createdAt: Long,
        ): DiscoverPage<ColumnArticle> {
            hotRequests += PageRequest(offset = offset, limit = limit, createdAt = createdAt)
            val articles = if (offset == 0) {
                listOf(ColumnArticle(id = 1), ColumnArticle(id = 2))
            } else {
                listOf(ColumnArticle(id = 3))
            }
            return DiscoverPage(error = 0, data = articles, total = 3)
        }

        override suspend fun getUserActivityUserPage(
            offset: Int,
            limit: Int,
            createdAt: Long,
        ): DiscoverPage<FollowedUserActivity> {
            activityRequests += PageRequest(offset = offset, limit = limit, createdAt = createdAt)
            return activities
        }
    }

    private data class PageRequest(
        val offset: Int,
        val limit: Int,
        val createdAt: Long,
    )
}
