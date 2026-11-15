package com.shihcheeng.sspaiyou.user

import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.shihcheeng.sspaiyou.api.data.column.ColumnArticle
import com.shihcheeng.sspaiyou.api.data.user.UserinfoDataModel
import com.shihcheeng.sspaiyou.api.data.user.activity.UserActivityDataModel
import com.shihcheeng.sspaiyou.api.data.user.favourite.UserFavouriteDataModel
import com.shihcheeng.sspaiyou.api.data.user.follow.FollowMessageModel
import com.shihcheeng.sspaiyou.api.data.user.follow.FollowUserRequest
import com.shihcheeng.sspaiyou.api.data.user.follow.UserFollowedDataModel
import com.shihcheeng.sspaiyou.api.data.user.third.ThirdUserDataModel
import com.shihcheeng.sspaiyou.api.service.UserService
import com.shihcheeng.sspaiyou.paging.UserFavouritePaging
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

class UserFavouriteDataLayerTest {
    @Test
    fun thirtyArticlesLoadAtOffsetsZeroTenTwentyUsingOneTimestampAndAllTypes() = runBlocking {
        val sample = Json { ignoreUnknownKeys = true }.decodeFromString<UserFavouriteDataModel>(
            requireNotNull(javaClass.getResource("/user_favourite.json")).readText(Charsets.UTF_8)
        )
        val articles = sample.data + List(20) { sample.data[it % 10].copy(id = 100000 + it) }
        val service = FakeUserService()
        val before = System.currentTimeMillis() / 1000
        val paging = UserFavouritePaging(service, "test-user")
        val after = System.currentTimeMillis() / 1000
        val pages = mutableListOf<PagingSource.LoadResult.Page<Int, ColumnArticle>>()
        var key: Int? = null
        repeat(3) { index ->
            service.response = sample.copy(data = articles.drop(index * 10).take(10))
            val params = if (index == 0) {
                PagingSource.LoadParams.Refresh(key, 10, false)
            } else {
                PagingSource.LoadParams.Append(requireNotNull(key), 10, false)
            }
            val page = paging.load(params) as PagingSource.LoadResult.Page
            assertEquals(articles.drop(index * 10).take(10), page.data)
            assertNull(page.prevKey)
            assertEquals(if (index == 2) null else (index + 1) * 10, page.nextKey)
            pages += page
            key = page.nextKey
        }

        val loaded = pages.flatMap { it.data }
        assertEquals(30, loaded.size)
        assertEquals(30, loaded.map { it.id }.distinct().size)
        assertEquals(articles, loaded)
        assertEquals(listOf(0, 10, 20), service.requests.map { it.offset })
        assertTrue(service.requests.all { it.limit == 10 && it.slug == "test-user" && it.type == "all" })
        val timestamp = service.requests.first().createdAt
        assertTrue(timestamp in before..after)
        assertTrue(service.requests.all { it.createdAt == timestamp })
        assertEquals(0, paging.getRefreshKey(PagingState(pages, 15, PagingConfig(10), 0)))
    }

    @Test
    fun serverCappedShortPagesContinueUntilKnownTotalUsingActualCounts() = runBlocking {
        val service = FakeUserService()
        val paging = UserFavouritePaging(service, "short-pages")
        service.response = UserFavouriteDataModel(List(2) { ColumnArticle(id = it + 1) }, 0, total = 3)
        val first = paging.load(PagingSource.LoadParams.Refresh(null, 30, false))
            as PagingSource.LoadResult.Page
        assertEquals(listOf(1, 2), first.data.map { it.id })
        assertEquals(2, first.nextKey)
        assertNull(first.prevKey)

        service.response = UserFavouriteDataModel(listOf(ColumnArticle(id = 3)), 0, total = 3)
        val last = paging.load(PagingSource.LoadParams.Append(requireNotNull(first.nextKey), 10, false))
            as PagingSource.LoadResult.Page
        assertEquals(listOf(3), last.data.map { it.id })
        assertNull(last.nextKey)
        assertNull(last.prevKey)
        assertEquals(listOf(0, 2), service.requests.map { it.offset })
        assertEquals(listOf(30, 10), service.requests.map { it.limit })
    }

    @Test
    fun withoutTotalEvenShortPagesContinueUntilAnEmptyPage() = runBlocking {
        val service = FakeUserService()
        val paging = UserFavouritePaging(service, "no-total")
        service.response = UserFavouriteDataModel(List(2) { ColumnArticle(id = it + 1) }, 0)
        val first = paging.load(PagingSource.LoadParams.Refresh(null, 10, false))
            as PagingSource.LoadResult.Page
        assertEquals(2, first.nextKey)

        service.response = UserFavouriteDataModel(listOf(ColumnArticle(id = 3)), 0)
        val second = paging.load(PagingSource.LoadParams.Append(requireNotNull(first.nextKey), 10, false))
            as PagingSource.LoadResult.Page
        assertEquals(3, second.nextKey)

        service.response = UserFavouriteDataModel(error = 0)
        val end = paging.load(PagingSource.LoadParams.Append(requireNotNull(second.nextKey), 10, false))
            as PagingSource.LoadResult.Page
        assertEquals(listOf(1, 2, 3), (first.data + second.data).map { it.id })
        assertTrue(end.data.isEmpty())
        assertNull(end.prevKey)
        assertNull(end.nextKey)
        assertEquals(listOf(0, 2, 3), service.requests.map { it.offset })
    }

    @Test
    fun defaultEmptyResponseEndsImmediatelyAndRefreshAlwaysStartsAtZero() = runBlocking {
        val service = FakeUserService()
        service.response = Json.decodeFromString<UserFavouriteDataModel>("""{"error":0}""")
        val paging = UserFavouritePaging(service, "empty")
        val page = paging.load(PagingSource.LoadParams.Refresh(null, 10, false))
            as PagingSource.LoadResult.Page
        assertTrue(page.data.isEmpty())
        assertNull(page.prevKey)
        assertNull(page.nextKey)
        assertEquals(listOf(0), service.requests.map { it.offset })
        assertEquals(0, paging.getRefreshKey(PagingState(emptyList(), null, PagingConfig(10), 0)))
    }

    @Test
    fun emptyPageEndsEvenWhenReportedTotalHasNotBeenReached() = runBlocking {
        val service = FakeUserService()
        service.response = UserFavouriteDataModel(error = 0, total = 30)
        val page = UserFavouritePaging(service, "stale-total")
            .load(PagingSource.LoadParams.Append(10, 10, false)) as PagingSource.LoadResult.Page
        assertTrue(page.data.isEmpty())
        assertNull(page.prevKey)
        assertNull(page.nextKey)
    }

    @Test
    fun refreshHonoursExplicitOffsetAndStopsWhenPagePassesTotal() = runBlocking {
        val service = FakeUserService()
        service.response = UserFavouriteDataModel(List(2) { ColumnArticle(id = it + 1) }, 0, total = 21)
        val paging = UserFavouritePaging(service, "explicit-key")
        val page = paging.load(PagingSource.LoadParams.Refresh(20, 7, false))
            as PagingSource.LoadResult.Page
        assertEquals(listOf(1, 2), page.data.map { it.id })
        assertNull(page.prevKey)
        assertNull(page.nextKey)
        assertEquals(20, service.requests.single().offset)
        assertEquals(7, service.requests.single().limit)
        assertEquals(0, paging.getRefreshKey(PagingState(listOf(page), 1, PagingConfig(10), 20)))
    }

    @Test
    fun businessErrorsPreserveServerMessageOrCodeAndCanBeRetriedAtTheSameOffset() = runBlocking {
        val service = FakeUserService()
        val paging = UserFavouritePaging(service, "private-user")
        val params = PagingSource.LoadParams.Append(7, 10, false)
        // An error envelope must never expose even a non-empty payload as a successful page.
        service.response = UserFavouriteDataModel(listOf(ColumnArticle(id = 99)), 403, "Access denied")
        val denied = paging.load(params) as PagingSource.LoadResult.Error
        assertEquals("Access denied", denied.throwable.message)

        service.response = UserFavouriteDataModel(error = 503)
        val missingMessage = paging.load(params) as PagingSource.LoadResult.Error
        assertTrue(missingMessage.throwable.message!!.contains("503"))
        service.response = UserFavouriteDataModel(error = 429, msg = " \t ")
        val blankMessage = paging.load(params) as PagingSource.LoadResult.Error
        assertTrue(blankMessage.throwable.message!!.contains("429"))

        service.response = UserFavouriteDataModel(listOf(ColumnArticle(id = 8)), 0, total = 8)
        val retried = paging.load(params) as PagingSource.LoadResult.Page
        assertEquals(8, retried.data.single().id)
        assertNull(retried.nextKey)
        assertEquals(4, service.requests.size)
        assertTrue(service.requests.all { it == service.requests.first() })
    }

    @Test
    fun networkFailureIsReturnedUnchangedAndRetryKeepsOffsetAndSnapshot() = runBlocking {
        val service = FakeUserService()
        val paging = UserFavouritePaging(service, "offline-user")
        val params = PagingSource.LoadParams.Append(10, 10, false)
        val network = IOException("offline")
        service.failure = network
        val failed = paging.load(params) as PagingSource.LoadResult.Error
        assertSame(network, failed.throwable)

        service.failure = null
        service.response = UserFavouriteDataModel(listOf(ColumnArticle(id = 11)), 0)
        val retried = paging.load(params) as PagingSource.LoadResult.Page
        assertEquals(11, retried.data.single().id)
        assertEquals(11, retried.nextKey)
        assertNull(retried.prevKey)
        assertEquals(listOf(10, 10), service.requests.map { it.offset })
        assertEquals(service.requests.first(), service.requests.last())
    }

    @Test
    fun cancellationPropagatesInsteadOfBecomingARetryableLoadError() {
        val service = FakeUserService()
        val cancellation = CancellationException("cancelled")
        service.failure = cancellation
        val paging = UserFavouritePaging(service, "cancelled-user")
        val thrown = assertThrows(CancellationException::class.java) {
            runBlocking { paging.load(PagingSource.LoadParams.Refresh(null, 10, false)) }
        }
        assertSame(cancellation, thrown)
        assertEquals(1, service.requests.size)
    }

    private class FakeUserService : UserService {
        var response = UserFavouriteDataModel(error = 0)
        var failure: Exception? = null
        val requests = mutableListOf<FavouriteRequest>()

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
        ): UserActivityDataModel = error("unused")

        override suspend fun getUserFavourite(
            limit: Int,
            offset: Int,
            createdAt: Long,
            slug: String,
            type: String
        ): UserFavouriteDataModel {
            requests += FavouriteRequest(limit, offset, createdAt, slug, type)
            failure?.let { throw it }
            return response
        }
    }

    private data class FavouriteRequest(
        val limit: Int,
        val offset: Int,
        val createdAt: Long,
        val slug: String,
        val type: String
    )
}
