package com.shihcheeng.sspaiyou.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.shihcheeng.sspaiyou.api.data.actrcle.comment.ArticleComment
import com.shihcheeng.sspaiyou.api.service.ArticleService
import kotlinx.coroutines.CancellationException

class ArticleCommentPaging(
    private val service: ArticleService,
    private val articleId: Int
) : PagingSource<Int, ArticleComment>() {
    // Keep the same snapshot timestamp across pages so new comments do not shift offsets.
    private val createdAt = System.currentTimeMillis() / 1000

    override fun getRefreshKey(state: PagingState<Int, ArticleComment>): Int = 0

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ArticleComment> {
        val offset = params.key ?: 0
        return try {
            val response = service.getArticleComments(
                limit = params.loadSize,
                offset = offset,
                createdAt = createdAt,
                articleId = articleId
            )
            check(response.error == 0) {
                response.msg.ifBlank { "评论加载失败：${response.error}" }
            }
            val nextOffset = offset + response.data.size
            LoadResult.Page(
                data = response.data,
                prevKey = null,
                nextKey = if (response.data.isEmpty() || nextOffset >= response.total) {
                    null
                } else {
                    nextOffset
                }
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}
