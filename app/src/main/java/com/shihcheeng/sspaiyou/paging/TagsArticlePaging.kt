package com.shihcheeng.sspaiyou.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.shihcheeng.sspaiyou.api.data.column.ColumnArticle
import com.shihcheeng.sspaiyou.api.service.TagsService
import kotlinx.coroutines.CancellationException

class TagsArticlePaging(
    private val service: TagsService,
    private val tagName: String
) : PagingSource<Int, ColumnArticle>() {

    override fun getRefreshKey(state: PagingState<Int, ColumnArticle>): Int = 0

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ColumnArticle> {
        val offset = params.key ?: 0
        return try {
            val response = service.getArticleTagsList(
                limit = params.loadSize,
                offset = offset,
                tagName = tagName
            )
            check(response.error == 0) {
                response.msg.ifBlank { "Tag articles API error: ${response.error}" }
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
