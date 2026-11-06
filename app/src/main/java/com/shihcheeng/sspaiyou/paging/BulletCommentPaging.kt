package com.shihcheeng.sspaiyou.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.shihcheeng.sspaiyou.api.data.bullet.comment.BulletComment
import com.shihcheeng.sspaiyou.api.service.BulletService
import kotlinx.coroutines.CancellationException

class BulletCommentPaging(
    private val service: BulletService,
    private val bulletCreatedAt: Long,
    private val sortWay: String = "new"
) : PagingSource<Int, BulletComment>() {
    private val createdAt = System.currentTimeMillis() / 1000

    override fun getRefreshKey(state: PagingState<Int, BulletComment>): Int = 0

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, BulletComment> {
        val offset = params.key ?: 0
        return try {
            val response = service.getBulletComments(
                limit = params.loadSize,
                offset = offset,
                bulletCreatedAt = bulletCreatedAt,
                sortWay = sortWay,
                createdAt = createdAt
            )
            check(response.error == 0) { "Bullet comments API error: ${response.error}" }
            val nextOffset = offset + response.data.size
            LoadResult.Page(
                data = response.data,
                prevKey = null,
                nextKey = if (response.data.isEmpty() || nextOffset >= response.total) {
                    null
                } else {
                    nextOffset
                }
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}
