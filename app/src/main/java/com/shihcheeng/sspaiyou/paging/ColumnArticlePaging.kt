package com.shihcheeng.sspaiyou.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.shihcheeng.sspaiyou.api.data.column.ColumnArticle
import com.shihcheeng.sspaiyou.api.service.ColumnService
import kotlinx.coroutines.CancellationException

class ColumnArticlePaging(
    private val service: ColumnService,
    private val columnId: Int
) : PagingSource<Int, ColumnArticle>() {
    private val createdAt = System.currentTimeMillis() / 1000

    override fun getRefreshKey(state: PagingState<Int, ColumnArticle>): Int = 0

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ColumnArticle> {
        val offset = params.key ?: 0
        return try {
            val response = service.getColumnSingleArticle(
                specialColumnId = columnId,
                limit = params.loadSize,
                offset = offset,
                createdAt = createdAt
            )
            check(response.error == 0) {
                response.msg.ifBlank { "Column articles API error: ${response.error}" }
            }
            val nextOffset = offset + response.data.size
            val endReached = response.data.isEmpty() || response.total?.let { nextOffset >= it } == true
            LoadResult.Page(
                data = response.data,
                prevKey = null,
                // A short page may be server-capped; use the total or wait for an empty page.
                nextKey = if (endReached) null else nextOffset
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}
