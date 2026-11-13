package com.shihcheeng.sspaiyou.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.shihcheeng.sspaiyou.api.data.column.ColumnArticle
import com.shihcheeng.sspaiyou.api.service.SearchService
import com.shihcheeng.sspaiyou.utils.extra.getSystemSecond
import kotlinx.coroutines.CancellationException

class SearchArticlePaging(
    private val service: SearchService,
    private val title: String
) : PagingSource<Int, ColumnArticle>() {
    // Keep the search time range stable while loading subsequent pages.
    private val endTime = getSystemSecond()

    override fun getRefreshKey(state: PagingState<Int, ColumnArticle>): Int = 0

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ColumnArticle> {
        val offset = params.key ?: 0
        return try {
            val response = service.searchArticle(
                title = title,
                etime = endTime,
                offset = offset,
                limit = params.loadSize
            )
            check(response.error == 0) {
                response.msg.ifBlank { "Search articles API error: ${response.error}" }
            }
            val nextOffset = offset + response.data.size
            val endReached = response.data.isEmpty() || response.total?.let { nextOffset >= it } == true
            LoadResult.Page(
                data = response.data,
                prevKey = null,
                nextKey = if (endReached) null else nextOffset
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}
