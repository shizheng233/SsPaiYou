package com.shihcheeng.sspaiyou.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.shihcheeng.sspaiyou.api.data.column.ColumnArticle
import com.shihcheeng.sspaiyou.api.service.UserService
import kotlinx.coroutines.CancellationException

class UserFavouritePaging(
    private val service: UserService,
    private val slug: String
) : PagingSource<Int, ColumnArticle>() {
    private val createdAt = System.currentTimeMillis() / 1000

    override fun getRefreshKey(state: PagingState<Int, ColumnArticle>): Int = 0

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ColumnArticle> {
        val offset = params.key ?: 0
        return try {
            val response = service.getUserFavourite(
                limit = params.loadSize,
                offset = offset,
                createdAt = createdAt,
                slug = slug
            )
            check(response.error == 0) {
                response.msg.ifBlank { "User favourites API error: ${response.error}" }
            }
            val nextOffset = offset + response.data.size
            val endReached = response.data.isEmpty() || response.total?.let { nextOffset >= it } == true
            LoadResult.Page(
                data = response.data,
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
