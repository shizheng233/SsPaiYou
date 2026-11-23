package com.shihcheeng.sspaiyou.api.service

import com.shihcheeng.sspaiyou.api.data.actrcle.ArticleDataModel
import com.shihcheeng.sspaiyou.api.data.actrcle.ArticleFavouriteRequest
import com.shihcheeng.sspaiyou.api.data.actrcle.ArticleFavouriteResponse
import com.shihcheeng.sspaiyou.api.data.actrcle.ArticleLikeRequest
import com.shihcheeng.sspaiyou.api.data.actrcle.ArticleLikeResponse
import com.shihcheeng.sspaiyou.api.data.actrcle.comment.ArticleCommentDataModel
import com.shihcheeng.sspaiyou.api.data.actrcle.comment.CommentPostRequest
import com.shihcheeng.sspaiyou.api.data.actrcle.comment.CommentPostResponse
import com.shihcheeng.sspaiyou.utils.SSPaiUrl
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.Url

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

    @GET(SSPaiUrl.BASE_URL_V2 + SSPaiUrl.ARTICLE_COMMENT)
    suspend fun getArticleBlockIDComments(
        @Query("limit") limit: Int = 10,
        @Query("offset") offset: Int,
        @Query("created_at") createdAt: Long,
        @Query("article_id") articleId: Int,
        @Query("article_body_extend_id") articleBodyExtendId: Int,
    ): ArticleCommentDataModel

    @POST(SSPaiUrl.FAVOURITE_ON_OFF)
    suspend fun favouriteOrNot(
        @Body request: ArticleFavouriteRequest
    ): ArticleFavouriteResponse

    @POST(SSPaiUrl.LIKE_ON_OFF)
    suspend fun likeItOrNot(
        @Body request: ArticleLikeRequest
    ): ArticleLikeResponse

    @POST
    suspend fun postComment(
        @Url url: String = SSPaiUrl.INSERT_COMMENT,
        @Body request: CommentPostRequest
    ): CommentPostResponse
}
