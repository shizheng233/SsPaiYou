package com.shihcheeng.sspaiyou.paging.base

import androidx.paging.PagingSource
import androidx.paging.PagingState

abstract class BaseMatrixPaging<T : Any> : PagingSource<Int, T>() {

    override fun getRefreshKey(state: PagingState<Int, T>): Int {
        return 0
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, T> {
        val key = params.key ?: 0
        return try {
            val data = getData(key)
            LoadResult.Page(
                data = data,
                prevKey = null,
                nextKey = key + loadOffset
            )
        } catch (e: Exception) {
            e.printStackTrace()
            LoadResult.Error(e)
        }
    }

    abstract suspend fun getData(offset: Int): List<T>

    abstract val loadOffset: Int

}