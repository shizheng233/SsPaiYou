package com.shihcheeng.sspaiyou.paging.base

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.shihcheeng.sspaiyou.utils.extra.printStackOnlyDebug
import kotlinx.coroutines.CancellationException

abstract class BaseMatrixPaging<T : Any> : PagingSource<Int, T>() {
    private val createdAt = System.currentTimeMillis() / 1000

    // Both Matrix feeds refresh from the beginning with a new snapshot.
    final override fun getRefreshKey(state: PagingState<Int, T>): Int = 0

    final override suspend fun load(params: LoadParams<Int>): LoadResult<Int, T> {
        val offset = params.key ?: 0
        return try {
            val page = loadPage(limit = params.loadSize, offset = offset, createdAt = createdAt)
            val nextOffset = offset + page.data.size
            val endReached = page.data.isEmpty() || page.total?.let { nextOffset >= it } == true
            LoadResult.Page(
                data = page.data,
                prevKey = null,
                // Short pages may be server-capped; only a total or an empty page ends the list.
                nextKey = if (endReached) null else nextOffset
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            e.printStackOnlyDebug()
            LoadResult.Error(e)
        }
    }

    // Implementations validate their API response before returning items and pagination metadata.
    protected abstract suspend fun loadPage(limit: Int, offset: Int, createdAt: Long): PageData<T>

    protected data class PageData<T : Any>(val data: List<T>, val total: Int?)
}
