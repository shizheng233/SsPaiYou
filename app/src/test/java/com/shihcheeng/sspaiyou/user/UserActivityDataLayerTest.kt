package com.shihcheeng.sspaiyou.user

import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.shihcheeng.sspaiyou.api.data.user.UserinfoDataModel
import com.shihcheeng.sspaiyou.api.data.user.activity.UserActivityDataModel
import com.shihcheeng.sspaiyou.api.data.user.activity.datadtos.DataActivityArticle
import com.shihcheeng.sspaiyou.api.data.user.activity.datadtos.IBaseData
import com.shihcheeng.sspaiyou.api.data.user.favourite.UserFavouriteDataModel
import com.shihcheeng.sspaiyou.api.data.user.follow.FollowMessageModel
import com.shihcheeng.sspaiyou.api.data.user.follow.FollowUserRequest
import com.shihcheeng.sspaiyou.api.data.user.follow.UserFollowedDataModel
import com.shihcheeng.sspaiyou.api.data.user.third.ThirdUserDataModel
import com.shihcheeng.sspaiyou.api.service.UserService
import com.shihcheeng.sspaiyou.paging.ActivityPaging
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

class UserActivityDataLayerTest {
    @Test
    fun loadsBeyondThreePagesWithoutGapsUsingOneSnapshotUntilTotalIsReached() = runBlocking {
        val activities = List(45) { DataActivityArticle(id = it + 1) }
        val service = FakeUserService()
        val before = System.currentTimeMillis() / 1000
        val paging = ActivityPaging(service, "test-user")
        val after = System.currentTimeMillis() / 1000
        val pages = mutableListOf<PagingSource.LoadResult.Page<Int, IBaseData>>()
        var key: Int? = null
        repeat(5) { index ->
            service.response = UserActivityDataModel(activities.drop(index * 10).take(10), total = 45)
            val params = if (index == 0) {
                PagingSource.LoadParams.Refresh(key, 10, false)
            } else {
                PagingSource.LoadParams.Append(requireNotNull(key), 10, false)
            }
            val page = paging.load(params) as PagingSource.LoadResult.Page
            assertNull(page.prevKey)
            assertEquals(if (index == 4) null else (index + 1) * 10, page.nextKey)
            pages += page
            key = page.nextKey
        }
        assertEquals(activities, pages.flatMap { it.data })
        assertEquals(listOf(0, 10, 20, 30, 40), service.requests.map { it.offset })
        assertTrue(service.requests.all { it.limit == 10 && it.slug == "test-user" })
        val timestamp = service.requests.first().createdAt
        assertTrue(timestamp in before..after)
        assertTrue(service.requests.all { it.createdAt == timestamp })
        assertEquals(0, paging.getRefreshKey(PagingState(pages, 25, PagingConfig(10), 0)))
    }

    @Test
    fun serverCappedPagesAdvanceByActualCountAndHonourRequestedLoadSize() = runBlocking {
        val service = FakeUserService()
        val paging = ActivityPaging(service, "short-pages")
        service.response = UserActivityDataModel(List(2) { DataActivityArticle(id = it + 1) }, total = 3)
        val first = paging.load(PagingSource.LoadParams.Refresh(null, 30, false))
            as PagingSource.LoadResult.Page
        assertEquals(2, first.nextKey)

        service.response = UserActivityDataModel(listOf(DataActivityArticle(id = 3)), total = 3)
        val last = paging.load(PagingSource.LoadParams.Append(requireNotNull(first.nextKey), 7, false))
            as PagingSource.LoadResult.Page
        assertEquals(List(3) { DataActivityArticle(id = it + 1) }, first.data + last.data)
        assertNull(last.nextKey)
        assertEquals(listOf(0, 2), service.requests.map { it.offset })
        assertEquals(listOf(30, 7), service.requests.map { it.limit })
    }

    @Test
    fun missingOrNullTotalDoesNotStopNonemptyPages() = runBlocking {
        val service = FakeUserService()
        val paging = ActivityPaging(service, "no-total")
        service.response = Json.decodeFromString<UserActivityDataModel>(
            """{"error":0,"data":[{"key":"release_article","id":1}]}"""
        )
        assertNull(service.response.total)
        val first = paging.load(PagingSource.LoadParams.Refresh(null, 10, false))
            as PagingSource.LoadResult.Page
        assertEquals(1, first.nextKey)

        service.response = Json.decodeFromString<UserActivityDataModel>(
            """{"error":0,"total":null,"data":[{"key":"release_article","id":2}]}"""
        )
        val second = paging.load(PagingSource.LoadParams.Append(requireNotNull(first.nextKey), 10, false))
            as PagingSource.LoadResult.Page
        assertEquals(2, second.nextKey)

        service.response = Json.decodeFromString<UserActivityDataModel>("""{"error":0}""")
        val end = paging.load(PagingSource.LoadParams.Append(requireNotNull(second.nextKey), 10, false))
            as PagingSource.LoadResult.Page
        assertTrue(end.data.isEmpty())
        assertNull(end.nextKey)
        assertEquals(listOf(0, 1, 2), service.requests.map { it.offset })
    }

    @Test
    fun emptyAndNullDataEndImmediatelyEvenWithAnUnreachedTotal() = runBlocking {
        val service = FakeUserService()
        val paging = ActivityPaging(service, "empty")
        for (payload in listOf(
            """{"error":0,"total":0,"data":[]}""",
            """{"error":0,"total":77,"data":[]}""",
            """{"error":0,"total":77,"data":null}"""
        )) {
            service.response = Json.decodeFromString<UserActivityDataModel>(payload)
            val page = paging.load(PagingSource.LoadParams.Refresh(null, 10, false))
                as PagingSource.LoadResult.Page
            assertTrue(page.data.isEmpty())
            assertNull(page.prevKey)
            assertNull(page.nextKey)
        }
        assertEquals(0, paging.getRefreshKey(PagingState(emptyList(), null, PagingConfig(10), 0)))
    }

    @Test
    fun explicitRefreshOffsetIsHonouredAndPageMayPassReportedTotal() = runBlocking {
        val service = FakeUserService()
        service.response = UserActivityDataModel(List(2) { DataActivityArticle(id = it + 21) }, total = 21)
        val page = ActivityPaging(service, "explicit-key")
            .load(PagingSource.LoadParams.Refresh(20, 7, false)) as PagingSource.LoadResult.Page
        assertEquals(service.response.data, page.data)
        assertNull(page.nextKey)
        assertNull(page.prevKey)
        assertEquals(20, service.requests.single().offset)
        assertEquals(7, service.requests.single().limit)
    }

    @Test
    fun businessErrorsPreserveMessageOrCodeAndRetryTheSameRequest() = runBlocking {
        val service = FakeUserService()
        val paging = ActivityPaging(service, "private-user")
        val params = PagingSource.LoadParams.Append(7, 10, false)
        service.response = Json.decodeFromString<UserActivityDataModel>(
            """{"error":403,"msg":"Access denied","data":[{"key":"release_article","id":99}]}"""
        )
        val denied = paging.load(params) as PagingSource.LoadResult.Error
        assertEquals("Access denied", denied.throwable.message)

        for (message in listOf("", " \t ")) {
            service.response = UserActivityDataModel(error = 503, msg = message)
            val failed = paging.load(params) as PagingSource.LoadResult.Error
            assertTrue(failed.throwable.message!!.contains("503"))
        }
        service.response = UserActivityDataModel(listOf(DataActivityArticle(id = 8)), total = 8)
        val retried = paging.load(params) as PagingSource.LoadResult.Page
        assertEquals(service.response.data, retried.data)
        assertNull(retried.nextKey)
        assertEquals(4, service.requests.size)
        assertTrue(service.requests.all { it == service.requests.first() })
    }

    @Test
    fun networkFailureIsReturnedUnchangedAndRetryKeepsOffsetAndSnapshot() = runBlocking {
        val service = FakeUserService()
        val paging = ActivityPaging(service, "offline-user")
        val params = PagingSource.LoadParams.Append(10, 10, false)
        val network = IOException("offline")
        service.failure = network
        val failed = paging.load(params) as PagingSource.LoadResult.Error
        assertSame(network, failed.throwable)

        service.failure = null
        service.response = UserActivityDataModel(listOf(DataActivityArticle(id = 11)))
        val retried = paging.load(params) as PagingSource.LoadResult.Page
        assertEquals(service.response.data, retried.data)
        assertEquals(11, retried.nextKey)
        assertEquals(listOf(10, 10), service.requests.map { it.offset })
        assertEquals(service.requests.first(), service.requests.last())
    }

    @Test
    fun cancellationPropagatesInsteadOfBecomingARetryableLoadError() {
        val service = FakeUserService()
        val cancellation = CancellationException("cancelled")
        service.failure = cancellation
        val paging = ActivityPaging(service, "cancelled-user")
        val thrown = assertThrows(CancellationException::class.java) {
            runBlocking { paging.load(PagingSource.LoadParams.Refresh(null, 10, false)) }
        }
        assertSame(cancellation, thrown)
    }

    private class FakeUserService : UserService {
        var response = UserActivityDataModel()
        var failure: Exception? = null
        val requests = mutableListOf<ActivityRequest>()

        override suspend fun getUserInfo(): UserinfoDataModel = error("unused")

        override suspend fun getUserInfo(slug: String): ThirdUserDataModel = error("unused")

        override suspend fun followUser(request: FollowUserRequest): FollowMessageModel = error("unused")

        override suspend fun getUserList(limit: Int, offset: Int, slug: String): UserFollowedDataModel =
            error("unused")

        override suspend fun getUserActivity(
            limit: Int,
            offset: Int,
            createdAt: Long,
            slug: String
        ): UserActivityDataModel {
            requests += ActivityRequest(limit, offset, createdAt, slug)
            failure?.let { throw it }
            return response
        }

        override suspend fun getUserFavourite(
            limit: Int,
            offset: Int,
            createdAt: Long,
            slug: String,
            type: String
        ): UserFavouriteDataModel = error("unused")
    }

    private data class ActivityRequest(
        val limit: Int,
        val offset: Int,
        val createdAt: Long,
        val slug: String
    )
}
