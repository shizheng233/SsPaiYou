package com.shihcheeng.sspaiyou.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.shihcheeng.sspaiyou.api.data.column.ColumnItem
import com.shihcheeng.sspaiyou.api.service.ColumnService
import com.shihcheeng.sspaiyou.utils.extra.printStackOnlyDebug

class ColumnPaging(private val service: ColumnService) : PagingSource<Int, ColumnItem>() {

    override fun getRefreshKey(state: PagingState<Int, ColumnItem>): Int {
        return 0
    }

    override suspend fun load(
        params: LoadParams<Int>
    ): LoadResult<Int, ColumnItem> {
        val offset = params.key ?: 0
        return try {
            val data = service.getColumn(offset = offset)
            return LoadResult.Page(
                data = data.data,
                prevKey = null,
                nextKey = if (offset > data.total) null else offset + 10
            )
        } catch (e: Exception) {
            e.printStackOnlyDebug()
            LoadResult.Error(e)
        }
    }
}