package com.shihcheeng.sspaiyou.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.shihcheeng.sspaiyou.api.data.user.activity.datadtos.IBaseData
import com.shihcheeng.sspaiyou.api.service.UserService
import com.shihcheeng.sspaiyou.utils.Constants
import com.shihcheeng.sspaiyou.utils.extra.printStackOnlyDebug

class ActivityPaging(
    private val service: UserService,
    private val slug: String
) : PagingSource<Int, IBaseData>() {

    override fun getRefreshKey(state: PagingState<Int, IBaseData>): Int {
        return 0
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, IBaseData> {
        val offset = params.key ?: 0
        return try {
            val data = service.getUserActivity(offset = offset, slug = slug)
            LoadResult.Page(
                data = data.data ?: emptyList(),
                nextKey = if (offset > Constants.NORMAL_LIMIT) {
                    null
                } else {
                    offset + Constants.NORMAL_LIMIT
                },
                prevKey = null
            )
        } catch (e: Exception) {
            e.printStackOnlyDebug()
            LoadResult.Error(e)
        }
    }
}