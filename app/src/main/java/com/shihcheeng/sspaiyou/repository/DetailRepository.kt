package com.shihcheeng.sspaiyou.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.shihcheeng.sspaiyou.api.data.actrcle.ArticleFavouriteRequest
import com.shihcheeng.sspaiyou.api.data.actrcle.ArticleLikeRequest
import com.shihcheeng.sspaiyou.api.data.actrcle.comment.ArticleComment
import com.shihcheeng.sspaiyou.api.data.actrcle.comment.CommentPostRequest
import com.shihcheeng.sspaiyou.api.service.ArticleService
import com.shihcheeng.sspaiyou.paging.ArticleCommentPaging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DetailRepository @Inject constructor(
    private val service: ArticleService
) {

    fun comments(
        articleId: Int,
        articleBodyExtendId: Int? = null,
    ): Flow<PagingData<ArticleComment>> {
        val pageSize = if (articleBodyExtendId == null) 20 else 10
        return Pager(
            config = PagingConfig(
                pageSize = pageSize,
                initialLoadSize = pageSize,
                enablePlaceholders = false,
            ),
        ) {
            ArticleCommentPaging(
                service = service,
                articleId = articleId,
                articleBodyExtendId = articleBodyExtendId,
            )
        }.flow
    }

    suspend fun setFavourite(articleId: Int, favourite: Boolean) {
        require(articleId > 0) { "Invalid article ID" }
        val response = service.favouriteOrNot(
            ArticleFavouriteRequest(targetId = articleId, flag = if (favourite) 1 else 2)
        )
        check(response.error == 0) { "收藏操作失败：${response.error}" }
    }

    suspend fun setLike(articleId: Int, liked: Boolean) {
        require(articleId > 0) { "Invalid article ID" }
        val response = service.likeItOrNot(
            ArticleLikeRequest(targetId = articleId, flag = if (liked) 1 else 2)
        )
        check(response.error == 0) { "喜欢操作失败：${response.error}" }
    }

    suspend fun getArticleDetail(id: String) = withContext(Dispatchers.Default) {
        runCatching {
            if (id.toIntOrNull() == null) {
                service.getMemberArticleDetail(slug = id)
            } else {
                service.getArticleDetail(id = id)
            }
        }.onSuccess {
            if (it.error != 0) {
                throw Exception("错误：${it.error}")
            }
        }.getOrThrow().data
    }

    suspend fun postComment(
        articleID: Int,
        comment: String
    ) = runCatching {
        service.postComment(
            request = CommentPostRequest(
                articleId = articleID,
                comment = comment
            )
        )
    }.onSuccess {
        if (it.error != 0) {
            throw Exception(it.error.toString())
        } else if (!it.data.success) {
            throw Exception("错误：success=${it.data.success} 且 status=${it.data.status}")
        }
    }.getOrThrow().data


}
