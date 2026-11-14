package com.shihcheeng.sspaiyou.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.shihcheeng.sspaiyou.api.data.home.main.MainData
import com.shihcheeng.sspaiyou.api.service.HomeService
import com.shihcheeng.sspaiyou.utils.extra.printStackOnlyDebug
import kotlinx.coroutines.CancellationException

class HomePaging(
    private val service: HomeService
) : PagingSource<Int, MainData>() {
    private val createdAt = System.currentTimeMillis() / 1000

    // Refresh the feed from the beginning with a new PagingSource snapshot.
    override fun getRefreshKey(state: PagingState<Int, MainData>): Int = 0

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MainData> {
        val offset = params.key ?: 0
        return try {
            val response = service.getHome(
                limit = params.loadSize,
                offset = offset,
                createdAt = createdAt
            )
            check(response.error == 0) {
                response.msg.ifBlank { "Home feed API error: ${response.error}" }
            }
            val nextOffset = offset + response.data.size
            val endReached = response.data.isEmpty() || response.total?.let { nextOffset >= it } == true
            LoadResult.Page(
                data = response.data,
                prevKey = null,
                // The server may return a different size than requested; use the actual count.
                nextKey = if (endReached) null else nextOffset
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            e.printStackOnlyDebug()
            LoadResult.Error(e)
        }
    }
}
