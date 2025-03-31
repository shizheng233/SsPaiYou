package com.shihcheeng.sspaiyou.repository

import com.shihcheeng.sspaiyou.api.service.ArticleService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DetailRepository @Inject constructor(
    private val service: ArticleService
) {

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