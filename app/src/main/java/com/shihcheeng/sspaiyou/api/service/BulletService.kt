package com.shihcheeng.sspaiyou.api.service

import com.shihcheeng.sspaiyou.api.data.bullet.single.BulletSingleDataModel
import com.shihcheeng.sspaiyou.api.data.bullet.comment.BulletCommentDataModel
import com.shihcheeng.sspaiyou.utils.SSPaiUrl
import retrofit2.http.GET
import retrofit2.http.Query

interface BulletService {

    @GET(SSPaiUrl.BULLET_SINGE)
    suspend fun getBulletInfo(
        @Query("created_at") createdAt: Long,
        @Query("view") view: String = "second",
        @Query("support_webp") supportWebp: Boolean = true
    ): BulletSingleDataModel

    @GET(SSPaiUrl.BULLET_COMMENT)
    suspend fun getBulletComments(
        @Query("limit") limit: Int,
        @Query("offset") offset: Int,
        @Query("bullet_created_at") bulletCreatedAt: Long,
        @Query("flag_model") flagModel: Int = 1,
        @Query("sort_way") sortWay: String = "new",
        @Query("created_at") createdAt: Long
    ): BulletCommentDataModel

}
