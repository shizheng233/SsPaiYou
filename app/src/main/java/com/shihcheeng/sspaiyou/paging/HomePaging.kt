package com.shihcheeng.sspaiyou.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.shihcheeng.sspaiyou.api.data.home.main.MainData
import com.shihcheeng.sspaiyou.api.service.HomeService

class HomePaging(
    private val service: HomeService
) : PagingSource<Int, MainData>() {

    override fun getRefreshKey(state: PagingState<Int, MainData>): Int {
        return 0
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MainData> {
        val offset: Int = params.key ?: 0
        return try {
            val data = service.getHome(offset = offset)
            LoadResult.Page(data = data.data, prevKey = null, nextKey = offset + 10)
        } catch (e: Exception) {
            e.printStackTrace()
            LoadResult.Error(e)
        }
    }
}