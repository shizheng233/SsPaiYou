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
        service.getArticleDetail(id = id).data
    }

}