package com.shihcheeng.sspaiyou.matrix

import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.shihcheeng.sspaiyou.api.data.bullet.BulletDataModel
import com.shihcheeng.sspaiyou.api.data.matrix.host.HotMatrixData
import com.shihcheeng.sspaiyou.api.data.matrix.host.MatrixHostDataModel
import com.shihcheeng.sspaiyou.api.data.matrix.look.MatrixLookDataModel
import com.shihcheeng.sspaiyou.api.data.matrix.normal.MatrixNormalDataModel
import com.shihcheeng.sspaiyou.api.data.matrix.normal.NormalMatrixData
import com.shihcheeng.sspaiyou.api.service.MatrixService
import com.shihcheeng.sspaiyou.paging.MatrixHotPaging
import com.shihcheeng.sspaiyou.paging.MatrixPaging
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException

class MatrixPagingTest : MatrixPagingContractTest<NormalMatrixData>() {
    override val endpoint = "normal"
    override fun createPaging(service: FakeMatrixService) = MatrixPaging(service)
    override fun ids(data: List<NormalMatrixData>) = data.map { it.id }
}

class MatrixHotPagingTest : MatrixPagingContractTest<HotMatrixData>() {
    override val endpoint = "hot"
    override fun createPaging(service: FakeMatrixService) = MatrixHotPaging(service)
    override fun ids(data: List<HotMatrixData>) = data.map { it.id }
}

// Exercise the shared paging contract through both real Matrix implementations.
abstract class MatrixPagingContractTest<T : Any> {
    protected abstract val endpoint: String
    protected abstract fun createPaging(service: FakeMatrixService): PagingSource<Int, T>
    protected abstract fun ids(data: List<T>): List<Int>

    @Test
    fun loadsEveryPageAtTheCorrectEndpointUsingOneTimestampUntilTotalIsReached() = runBlocking {
        val service = FakeMatrixService()
        service.total = 50
        val before = System.currentTimeMillis() / 1000
        val paging = createPaging(service)
        val after = System.currentTimeMillis() / 1000
        val pages = mutableListOf<PagingSource.LoadResult.Page<Int, T>>()
        var key: Int? = null
        repeat(5) { index ->
            service.responseIds = (index * 10 + 1..(index + 1) * 10).toList()
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
        assertEquals((1..50).toList(), ids(pages.flatMap { it.data }))
        assertEquals(listOf(0, 10, 20, 30, 40), service.requests.map { it.offset })
        assertTrue(service.requests.all { it.limit == 10 && it.endpoint == endpoint })
        val timestamp = service.requests.first().createdAt
        assertTrue(timestamp in before..after)
        assertTrue(service.requests.all { it.createdAt == timestamp })
        assertEquals(0, paging.getRefreshKey(PagingState(pages, 25, PagingConfig(10), 0)))
    }

    @Test
    fun serverCappedPagesAdvanceByActualCountAndHonourRequestedLoadSize() = runBlocking {
        val service = FakeMatrixService()
        val paging = createPaging(service)
        service.total = 3
        service.responseIds = listOf(1, 2)
        val first = paging.load(PagingSource.LoadParams.Refresh(null, 30, false))
            as PagingSource.LoadResult.Page
        assertEquals(2, first.nextKey)

        service.responseIds = listOf(3)
        val last = paging.load(PagingSource.LoadParams.Append(requireNotNull(first.nextKey), 7, false))
            as PagingSource.LoadResult.Page
        assertEquals(listOf(1, 2, 3), ids(first.data + last.data))
        assertNull(last.nextKey)
        assertEquals(listOf(0, 2), service.requests.map { it.offset })
        assertEquals(listOf(30, 7), service.requests.map { it.limit })
    }

    @Test
    fun unknownTotalAllowsShortPagesUntilAnEmptyPage() = runBlocking {
        val service = FakeMatrixService()
        val paging = createPaging(service)
        service.responseIds = listOf(1, 2)
        val first = paging.load(PagingSource.LoadParams.Refresh(null, 10, false))
            as PagingSource.LoadResult.Page
        assertEquals(2, first.nextKey)

        service.responseIds = listOf(3)
        val second = paging.load(PagingSource.LoadParams.Append(requireNotNull(first.nextKey), 10, false))
            as PagingSource.LoadResult.Page
        assertEquals(3, second.nextKey)

        service.responseIds = emptyList()
        val end = paging.load(PagingSource.LoadParams.Append(requireNotNull(second.nextKey), 10, false))
            as PagingSource.LoadResult.Page
        assertTrue(end.data.isEmpty())
        assertNull(end.nextKey)
        assertEquals(listOf(1, 2, 3), ids(first.data + second.data))
        assertEquals(listOf(0, 2, 3), service.requests.map { it.offset })
    }

    @Test
    fun emptyPageEndsImmediatelyEvenWhenTotalHasNotBeenReached() = runBlocking {
        val service = FakeMatrixService()
        val paging = createPaging(service)
        for (total in listOf(null, 0, 77)) {
            service.total = total
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
        val service = FakeMatrixService()
        service.responseIds = listOf(21, 22)
        service.total = 21
        val paging = createPaging(service)
        val page = paging.load(PagingSource.LoadParams.Refresh(20, 7, false))
            as PagingSource.LoadResult.Page
        assertEquals(service.responseIds, ids(page.data))
        assertNull(page.prevKey)
        assertNull(page.nextKey)
        assertEquals(20, service.requests.single().offset)
        assertEquals(7, service.requests.single().limit)
        assertEquals(0, paging.getRefreshKey(PagingState(listOf(page), 1, PagingConfig(10), 20)))
    }

    @Test
    fun businessErrorsPreserveMessageOrCodeAndRetryTheSameRequest() = runBlocking {
        val service = FakeMatrixService()
        val paging = createPaging(service)
        val params = PagingSource.LoadParams.Append(7, 10, false)
        service.responseIds = listOf(99)
        service.error = 403
        service.msg = "Access denied"
        val denied = paging.load(params) as PagingSource.LoadResult.Error
        assertEquals("Access denied", denied.throwable.message)

        for (message in listOf("", " \t ")) {
            service.error = 503
            service.msg = message
            val failed = paging.load(params) as PagingSource.LoadResult.Error
            assertTrue(failed.throwable.message!!.contains("503"))
        }
        service.error = 0
        service.responseIds = listOf(8)
        service.total = 8
        val retried = paging.load(params) as PagingSource.LoadResult.Page
        assertEquals(service.responseIds, ids(retried.data))
        assertNull(retried.nextKey)
        assertEquals(4, service.requests.size)
        assertTrue(service.requests.all { it == service.requests.first() })
    }

    @Test
    fun networkFailureIsReturnedUnchangedAndRetryKeepsOffsetAndSnapshot() = runBlocking {
        val service = FakeMatrixService()
        val paging = createPaging(service)
        val params = PagingSource.LoadParams.Append(10, 10, false)
        val network = IOException("offline")
        service.failure = network
        val failed = paging.load(params) as PagingSource.LoadResult.Error
        assertSame(network, failed.throwable)

        service.failure = null
        service.responseIds = listOf(11)
        val retried = paging.load(params) as PagingSource.LoadResult.Page
        assertEquals(service.responseIds, ids(retried.data))
        assertEquals(11, retried.nextKey)
        assertEquals(listOf(10, 10), service.requests.map { it.offset })
        assertEquals(service.requests.first(), service.requests.last())
    }

    @Test
    fun cancellationPropagatesInsteadOfBecomingARetryableLoadError() {
        val service = FakeMatrixService()
        val cancellation = CancellationException("cancelled")
        service.failure = cancellation
        val paging = createPaging(service)
        val thrown = assertThrows(CancellationException::class.java) {
            runBlocking { paging.load(PagingSource.LoadParams.Refresh(null, 10, false)) }
        }
        assertSame(cancellation, thrown)
    }

    protected class FakeMatrixService : MatrixService {
        var responseIds: List<Int> = emptyList()
        var total: Int? = null
        var error = 0
        var msg = ""
        var failure: Exception? = null
        val requests = mutableListOf<MatrixRequest>()

        override suspend fun getMatrix(limit: Int, offset: Int, createdAt: Long): MatrixNormalDataModel {
            requests += MatrixRequest("normal", limit, offset, createdAt)
            failure?.let { throw it }
            return MatrixNormalDataModel(responseIds.map { NormalMatrixData(id = it) }, error, msg, total)
        }

        override suspend fun getMatrixHost(limit: Int, offset: Int, createdAt: Long): MatrixHostDataModel {
            requests += MatrixRequest("hot", limit, offset, createdAt)
            failure?.let { throw it }
            return MatrixHostDataModel(responseIds.map { HotMatrixData(id = it) }, error, msg, total)
        }

        override suspend fun getMatrixLook(): MatrixLookDataModel = error("unused")

        override suspend fun getBullet(limit: Int, offset: Int, createdAt: Long): BulletDataModel =
            error("unused")
    }

    protected data class MatrixRequest(
        val endpoint: String,
        val limit: Int,
        val offset: Int,
        val createdAt: Long
    )
}
