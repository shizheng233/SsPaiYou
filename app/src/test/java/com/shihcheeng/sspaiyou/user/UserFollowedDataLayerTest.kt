package com.shihcheeng.sspaiyou.user

import androidx.paging.PagingSource
import com.shihcheeng.sspaiyou.api.data.column.ColumnUser
import com.shihcheeng.sspaiyou.api.data.user.follow.FollowMessageModel
import com.shihcheeng.sspaiyou.api.data.user.follow.FollowUserRequest
import com.shihcheeng.sspaiyou.api.data.user.follow.UserFollowedDataModel
import com.shihcheeng.sspaiyou.paging.UserFollowedPaging
import com.shihcheeng.sspaiyou.repository.UserRepository
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
import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Assert.*
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class UserFollowedDataLayerTest {
    @Test
    fun suppliedJsonPreservesAllTenUsersWithRedactedIdsAndBadgeFields() {
        val response = Json.decodeFromString<UserFollowedDataModel>(
            requireNotNull(javaClass.getResource("/user_followed_list.json")).readText(Charsets.UTF_8)
        )
        assertEquals(0, response.error)
        assertEquals(12, response.total)
        assertEquals(10, response.data.size)
        assertEquals(10, response.data.map { it.slug }.distinct().size)
        assertTrue(response.data.all { it.id == 0 && it.followed })
        val first = response.data.first()
        assertEquals("利兹与青鸟", first.nickname)
        assertEquals("d6sd0vwe", first.slug)
        assertEquals(76, first.fansCount)
        assertEquals("writer", first.userFlags.single().key)
        assertEquals("缺一抹奇幻色彩。", response.data.first { it.slug == "clyde" }.bio)
    }

    @Test
    fun requestUsesSlugAndFlagAndResponsesMustContainAnErrorCode() {
        assertEquals(
            """{"slug":"samwanng","flag":2}""",
            Json.encodeToString(FollowUserRequest("samwanng", 2))
        )
        assertThrows(SerializationException::class.java) { Json.decodeFromString<FollowMessageModel>("{}") }
        assertThrows(SerializationException::class.java) { Json.decodeFromString<UserFollowedDataModel>("{}") }
    }

    @Test
    fun pagingDeduplicatesBySlugAndAdvancesRawOffsetAcrossDuplicateOnlyPages() = runTest {
        val service = FollowUserTestService()
        val paging = UserFollowedPaging(service, "owner")
        service.users = users("a", "a", "b", "")
        val first = paging.load(PagingSource.LoadParams.Refresh(null, 30, false)) as PagingSource.LoadResult.Page
        assertEquals(listOf("a", "b"), first.data.map { it.slug })
        assertEquals(4, first.nextKey)
        service.users = users("a", "b")
        val duplicates = paging.load(PagingSource.LoadParams.Append(4, 10, false)) as PagingSource.LoadResult.Page
        assertTrue(duplicates.data.isEmpty())
        assertEquals(6, duplicates.nextKey)
        service.users = users("b", "c").copy(total = 8)
        val last = paging.load(PagingSource.LoadParams.Append(6, 10, false)) as PagingSource.LoadResult.Page
        assertEquals(listOf("c"), last.data.map { it.slug })
        assertNull(last.nextKey)
        assertNull(last.prevKey)
        assertEquals(listOf(Triple(30, 0, "owner"), Triple(10, 4, "owner"), Triple(10, 6, "owner")), service.listRequests)
    }

    @Test
    fun freshPagingSourceRestoresPreviouslyLoadedUsersAndEmptyPageEndsWithoutTotal() = runTest {
        val service = FollowUserTestService()
        service.users = users("a")
        val params = PagingSource.LoadParams.Refresh<Int>(null, 10, false)
        repeat(2) {
            val paging = UserFollowedPaging(service, "owner")
            val page = paging.load(params) as PagingSource.LoadResult.Page
            assertEquals(listOf("a"), page.data.map { it.slug })
            assertEquals(1, page.nextKey)
        }
        service.users = users().copy(total = 50)
        val page = UserFollowedPaging(service, "owner").load(params) as PagingSource.LoadResult.Page
        assertTrue(page.data.isEmpty())
        assertNull(page.nextKey)
    }

    @Test
    fun failedPageCanRetryWithoutLosingUsersAndCancellationPropagates() = runTest {
        val service = FollowUserTestService()
        val paging = UserFollowedPaging(service, "owner")
        val params = PagingSource.LoadParams.Append(10, 10, false)
        service.users = users("a").copy(error = 403, msg = "Access denied")
        assertEquals("Access denied", (paging.load(params) as PagingSource.LoadResult.Error).throwable.message)
        val offline = IOException("offline")
        service.failure = offline
        assertSame(offline, (paging.load(params) as PagingSource.LoadResult.Error).throwable)
        service.failure = CancellationException("cancelled")
        assertThrows(CancellationException::class.java) { runBlocking { paging.load(params) } }
        service.failure = null
        service.users = users("a")
        assertEquals(listOf("a"), (paging.load(params) as PagingSource.LoadResult.Page).data.map { it.slug })
        assertTrue(service.listRequests.all { it == Triple(10, 10, "owner") })
    }

    @Test
    fun followAndUnfollowPublishConfirmedChangesUsingCorrectFlags() = runTest {
        val service = FollowUserTestService()
        val repository = UserRepository(service) { "session" }
        val changes = collectChanges(repository)
        repository.setFollowed("target", true)
        runCurrent()
        repository.setFollowed("target", false)
        runCurrent()
        assertEquals(listOf(FollowUserRequest("target", 1), FollowUserRequest("target", 2)), service.followRequests)
        assertEquals(listOf("target" to true, "target" to false), changes)
        assertTrue(repository.followPending.value.isEmpty())
    }

    @Test
    fun pendingRequestSuppressesDuplicatesAndCancellationAllowsRetry() = runTest {
        val service = FollowUserTestService()
        val repository = UserRepository(service) { "session" }
        val changes = collectChanges(repository)
        service.followGate = CompletableDeferred()
        val first = launch { repository.setFollowed("target", true) }
        runCurrent()
        assertEquals(setOf("target"), repository.followPending.value)
        repository.setFollowed("target", false)
        assertEquals(1, service.followRequests.size)
        first.cancelAndJoin()
        assertTrue(repository.followPending.value.isEmpty())
        assertTrue(changes.isEmpty())
        service.followGate = null
        repository.setFollowed("target", true)
        runCurrent()
        assertEquals(listOf("target" to true), changes)
    }

    @Test
    fun failuresDoNotPublishSuccessAndAlwaysClearPending() = runTest {
        val service = FollowUserTestService()
        val repository = UserRepository(service) { "session" }
        val changes = collectChanges(repository)
        service.followResponse = FollowMessageModel(403, "Access denied")
        val businessError = assertThrows(IllegalStateException::class.java) {
            runBlocking { repository.setFollowed("target", true) }
        }
        assertEquals("Access denied", businessError.message)
        assertTrue(repository.followPending.value.isEmpty())
        service.failure = IOException("offline")
        assertThrows(IOException::class.java) { runBlocking { repository.setFollowed("target", true) } }
        runCurrent()
        assertTrue(changes.isEmpty())
        assertTrue(repository.followPending.value.isEmpty())
    }

    @Test
    fun anonymousAndStaleSessionRequestsCannotPublishFollowChanges() = runTest {
        val service = FollowUserTestService()
        var token: String? = null
        val repository = UserRepository(service) { token }
        val changes = collectChanges(repository)
        repository.setFollowed("target", true)
        token = " "
        repository.setFollowed("target", true)
        assertTrue(service.followRequests.isEmpty())
        token = "first-session"
        val gate = CompletableDeferred<Unit>()
        service.followGate = gate
        val pending = launch { repository.setFollowed("target", true) }
        runCurrent()
        token = "other-session"
        gate.complete(Unit)
        pending.join()
        runCurrent()
        assertTrue(changes.isEmpty())
        assertTrue(repository.followPending.value.isEmpty())
    }

    private fun TestScope.collectChanges(repository: UserRepository): List<Pair<String, Boolean>> {
        val changes = mutableListOf<Pair<String, Boolean>>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { repository.followChanges.toList(changes) }
        return changes
    }

    private fun users(vararg slugs: String) = UserFollowedDataModel(
        data = slugs.map { ColumnUser(slug = it, nickname = it) }, error = 0
    )
}
