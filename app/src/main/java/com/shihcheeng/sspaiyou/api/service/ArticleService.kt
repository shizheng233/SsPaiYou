package com.shihcheeng.sspaiyou.api.service

import com.shihcheeng.sspaiyou.api.data.actrcle.ArticleDataModel
import com.shihcheeng.sspaiyou.api.data.actrcle.comment.ArticleCommentDataModel
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

    @GET(SSPaiUrl.ARTICLE_MEMBER)
    suspend fun getMemberArticleDetail(
        @Query("slug") slug: String,
        @Query("view") view: String = "second",
        @Query("support_webp") supportWebp: Boolean = true
    ): ArticleDataModel

    @GET(SSPaiUrl.BASE_URL_V2 + SSPaiUrl.ARTICLE_COMMENT)
    suspend fun getArticleComments(
        @Query("limit") limit: Int,
        @Query("offset") offset: Int,
        @Query("created_at") createdAt: Long,
        @Query("article_id") articleId: Int
    ): ArticleCommentDataModel
}
