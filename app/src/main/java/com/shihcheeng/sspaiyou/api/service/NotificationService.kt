package com.shihcheeng.sspaiyou.api.service

import com.shihcheeng.sspaiyou.api.data.notification.CommentNotification
import com.shihcheeng.sspaiyou.api.data.notification.NotificationPage
import com.shihcheeng.sspaiyou.api.data.notification.OnlineNotification
import com.shihcheeng.sspaiyou.utils.SSPaiUrl
import retrofit2.http.GET
import retrofit2.http.Query

interface NotificationService {

    /**
     * 获取回应等等
     *
     * @param limit 推荐为4
     */
    @GET(SSPaiUrl.ONLINE_PAGE_GET)
    suspend fun getOnlinePageGet(
        @Query("offset") offset: Int,
        @Query("limit") limit: Int,
        @Query("community") community: Boolean = true,
        @Query("created_at") createdAt: Long,
        @Query("has_bullet") hasBullet: Boolean = true,
        @Query("has_article_body_extend_status")
        hasArticleBodyExtendStatus: Boolean = true,
        @Query("has_article_comment_status") hasArticleCommentStatus: Boolean = true,
        @Query("has_article_comment_reply_status")
        hasArticleCommentReplyStatus: Boolean = true
    ): NotificationPage<OnlineNotification>

    /**
     * 获取用户获得的评论
     *
     * @param limit 推荐为4
     */
    @GET(SSPaiUrl.INFORMATION_PAGE_GET)
    suspend fun getInfoCommentGet(
        @Query("offset") offset: Int,
        @Query("limit") limit: Int,
        @Query("community") community: Boolean = true,
        @Query("created_at") createdAt: Long,
        @Query("has_bullet") hasBullet: Boolean = true,
        @Query("has_article_body_extend_status")
        hasArticleBodyExtendStatus: Boolean = true,
        @Query("has_article_comment_status") hasArticleCommentStatus: Boolean = true,
        @Query("has_article_comment_reply_status")
        hasArticleCommentReplyStatus: Boolean = true
    ): NotificationPage<CommentNotification>

}
