package com.shihcheeng.sspaiyou.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.shihcheeng.sspaiyou.api.data.user.activity.datadtos.IBaseData
import com.shihcheeng.sspaiyou.api.service.UserService
import kotlinx.coroutines.CancellationException

class ActivityPaging(
    private val service: UserService,
    private val slug: String
) : PagingSource<Int, IBaseData>() {
    private val createdAt = System.currentTimeMillis() / 1000

    // Refresh a timeline from the newest activity with a new PagingSource snapshot.
    override fun getRefreshKey(state: PagingState<Int, IBaseData>): Int = 0

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, IBaseData> {
        val offset = params.key ?: 0
        return try {
            val response = service.getUserActivity(
                limit = params.loadSize,
                offset = offset,
                createdAt = createdAt,
                slug = slug
            )
            check(response.error == 0) {
                response.msg.ifBlank { "User activity API error: ${response.error}" }
            }
            val activities = response.data.orEmpty()
            val nextOffset = offset + activities.size
            val endReached = activities.isEmpty() || response.total?.let { nextOffset >= it } == true
            LoadResult.Page(
                data = activities,
                prevKey = null,
                // Short pages may be server-capped; only a total or an empty page ends the list.
                nextKey = if (endReached) null else nextOffset
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}
