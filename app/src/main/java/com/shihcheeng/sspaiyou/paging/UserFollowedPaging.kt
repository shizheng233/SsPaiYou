package com.shihcheeng.sspaiyou.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.shihcheeng.sspaiyou.api.data.column.ColumnUser
import com.shihcheeng.sspaiyou.api.service.UserService
import kotlinx.coroutines.CancellationException

class UserFollowedPaging(
    private val service: UserService,
    private val slug: String
) : PagingSource<Int, ColumnUser>() {
    // The API redacts numeric IDs to zero; slug is the user's identity and navigation key.
    private val loadedSlugs = mutableSetOf<String>()

    override fun getRefreshKey(state: PagingState<Int, ColumnUser>): Int = 0

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ColumnUser> {
        val offset = params.key ?: 0
        return try {
            require(slug.isNotBlank()) { "Invalid user slug" }
            val response = service.getUserList(params.loadSize, offset, slug)
            check(response.error == 0) {
                response.msg.ifBlank { "Followed users API error: ${response.error}" }
            }
            // Advance through raw results even if all entries are duplicates or lack a slug.
            val nextOffset = offset + response.data.size
            val endReached = response.data.isEmpty() || response.total?.let { nextOffset >= it } == true
            val users = response.data.filter { it.slug.isNotBlank() && loadedSlugs.add(it.slug) }
            LoadResult.Page(
                data = users,
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
