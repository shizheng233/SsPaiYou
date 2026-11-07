package com.shihcheeng.sspaiyou.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.shihcheeng.sspaiyou.api.data.actrcle.comment.ArticleComment
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

    fun comments(articleId: Int): Flow<PagingData<ArticleComment>> = Pager(
        config = PagingConfig(pageSize = 20, initialLoadSize = 20, enablePlaceholders = false)
    ) {
        ArticleCommentPaging(service, articleId)
    }.flow

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

}
