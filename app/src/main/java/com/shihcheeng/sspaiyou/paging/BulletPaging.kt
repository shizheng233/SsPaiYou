package com.shihcheeng.sspaiyou.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.shihcheeng.sspaiyou.api.data.bullet.BulletData
import com.shihcheeng.sspaiyou.api.service.MatrixService
import kotlinx.coroutines.CancellationException

class BulletPaging(private val service: MatrixService) : PagingSource<Int, BulletData>() {
    private val createdAt = System.currentTimeMillis() / 1000

    override fun getRefreshKey(state: PagingState<Int, BulletData>): Int = 0

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, BulletData> {
        val offset = params.key ?: 0
        return try {
            val response = service.getBullet(
                limit = params.loadSize,
                offset = offset,
                createdAt = createdAt
            )
            check(response.error == 0) { "Bullet API error: ${response.error}" }
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
