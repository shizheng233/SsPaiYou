package com.shihcheeng.sspaiyou.api.service

import com.shihcheeng.sspaiyou.api.data.actrcle.ArticleDataModel
import com.shihcheeng.sspaiyou.utils.SSPaiUrl
import retrofit2.http.GET
import retrofit2.http.Query

interface ArticleService {

    @GET(SSPaiUrl.ARTICLE_DETAIL)
    suspend fun getArticleDetail(
        @Query("id") id: String,
        @Query("view") view: String = "second",
        @Query("support_webp") supportWebp: Boolean = true
    ): ArticleDataModel

}