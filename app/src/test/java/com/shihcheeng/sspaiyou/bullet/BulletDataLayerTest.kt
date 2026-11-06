package com.shihcheeng.sspaiyou.bullet

import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.PagingConfig
import com.shihcheeng.sspaiyou.api.data.bullet.BulletData
import com.shihcheeng.sspaiyou.api.data.bullet.comment.BulletComment
import com.shihcheeng.sspaiyou.api.data.bullet.comment.BulletCommentDataModel
import com.shihcheeng.sspaiyou.api.data.bullet.single.BulletSingleDataModel
import com.shihcheeng.sspaiyou.api.service.BulletService
import com.shihcheeng.sspaiyou.paging.BulletCommentPaging
import com.shihcheeng.sspaiyou.repository.BulletRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException

class BulletDataLayerTest {
    @Test
    fun pagingUsesActualResultSizeAndFixedTimestamp() = runBlocking {
        val service = FakeBulletService()
        val before = System.currentTimeMillis() / 1000
        val paging = BulletCommentPaging(service, 1793254303L, "hot")
        service.comments = BulletCommentDataModel(List(2) { BulletComment(idHash = "$it") }, 0, 3)
        val first = paging.load(PagingSource.LoadParams.Refresh(null, 10, false))
            as PagingSource.LoadResult.Page
        assertEquals(2, first.nextKey)
        assertNull(first.prevKey)
        service.comments = BulletCommentDataModel(listOf(BulletComment(idHash = "last")), 0, 3)
        val last = paging.load(PagingSource.LoadParams.Append(2, 10, false))
            as PagingSource.LoadResult.Page
        assertNull(last.nextKey)
        assertEquals(listOf(0, 2), service.requests.map { it.offset })
        assertTrue(service.requests.all { it.limit == 10 && it.bulletCreatedAt == 1793254303L })
        assertTrue(service.requests.all { it.sortWay == "hot" && it.flagModel == 1 })
        assertEquals(service.requests[0].createdAt, service.requests[1].createdAt)
        assertTrue(service.requests[0].createdAt in before..(System.currentTimeMillis() / 1000))
        assertEquals(0, paging.getRefreshKey(PagingState(emptyList(), null, PagingConfig(10), 0)))
    }

    @Test
    fun emptyPageStopsEvenWhenTotalHasMoreItems() = runBlocking {
        val service = FakeBulletService()
        service.comments = BulletCommentDataModel(error = 0, total = 17)
        val page = BulletCommentPaging(service, 1L)
            .load(PagingSource.LoadParams.Refresh(null, 10, false)) as PagingSource.LoadResult.Page
        assertTrue(page.data.isEmpty())
        assertNull(page.nextKey)
        assertEquals("new", service.requests.single().sortWay)
    }

    @Test
    fun apiAndNetworkErrorsBecomeLoadErrorsButCancellationPropagates() = runBlocking {
        val service = FakeBulletService()
        val paging = BulletCommentPaging(service, 1L)
        service.comments = BulletCommentDataModel(error = 403)
        val api = paging.load(PagingSource.LoadParams.Refresh(null, 10, false))
            as PagingSource.LoadResult.Error
        assertTrue(api.throwable.message!!.contains("403"))
        val network = IOException("offline")
        service.failure = network
        val failed = paging.load(PagingSource.LoadParams.Refresh(null, 10, false))
            as PagingSource.LoadResult.Error
        assertSame(network, failed.throwable)
        val cancellation = CancellationException("cancelled")
        service.failure = cancellation
        val thrown = assertThrows(CancellationException::class.java) {
            runBlocking { paging.load(PagingSource.LoadParams.Refresh(null, 10, false)) }
        }
        assertSame(cancellation, thrown)
    }

    @Test
    fun repositoryChecksErrorsAndMissingDataAndPassesDetailDefaults() {
        val service = FakeBulletService()
        val repository = BulletRepository(service)
        val detail = BulletData(title = "Discussion", createdAt = 1792994679L)
        service.detail = BulletSingleDataModel(detail, 0)
        assertSame(detail, runBlocking { repository.getBullet(1792994679L) })
        assertEquals(1792994679L, service.detailCreatedAt)
        assertEquals("second", service.detailView)
        assertTrue(service.detailSupportWebp)
        service.detail = BulletSingleDataModel(detail, 403)
        assertThrows(IllegalStateException::class.java) { runBlocking { repository.getBullet(1L) } }
        service.detail = BulletSingleDataModel(error = 0)
        assertThrows(IllegalStateException::class.java) { runBlocking { repository.getBullet(1L) } }
        val cancellation = CancellationException("cancelled")
        service.failure = cancellation
        assertSame(cancellation, assertThrows(CancellationException::class.java) {
            runBlocking { repository.getBullet(1L) }
        })
    }

    private class FakeBulletService : BulletService {
        var detail = BulletSingleDataModel(error = 0)
        var comments = BulletCommentDataModel(error = 0)
        var failure: Exception? = null
        var detailCreatedAt = 0L
        var detailView = ""
        var detailSupportWebp = false
        val requests = mutableListOf<CommentRequest>()

        override suspend fun getBulletInfo(createdAt: Long, view: String, supportWebp: Boolean): BulletSingleDataModel {
            failure?.let { throw it }
            detailCreatedAt = createdAt
            detailView = view
            detailSupportWebp = supportWebp
            return detail
        }

        override suspend fun getBulletComments(
            limit: Int,
            offset: Int,
            bulletCreatedAt: Long,
            flagModel: Int,
            sortWay: String,
            createdAt: Long
        ): BulletCommentDataModel {
            requests += CommentRequest(limit, offset, bulletCreatedAt, flagModel, sortWay, createdAt)
            failure?.let { throw it }
            return comments
        }
    }

    private data class CommentRequest(
        val limit: Int,
        val offset: Int,
        val bulletCreatedAt: Long,
        val flagModel: Int,
        val sortWay: String,
        val createdAt: Long
    )
}
