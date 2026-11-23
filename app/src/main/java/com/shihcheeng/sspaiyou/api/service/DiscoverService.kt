package com.shihcheeng.sspaiyou.api.service

import com.shihcheeng.sspaiyou.api.data.column.ColumnArticle
import com.shihcheeng.sspaiyou.api.data.discover.DiscoverPage
import com.shihcheeng.sspaiyou.api.data.discover.FollowedUserActivity
import com.shihcheeng.sspaiyou.utils.Constants
import com.shihcheeng.sspaiyou.utils.SSPaiUrl
import retrofit2.http.GET
import retrofit2.http.Query

interface DiscoverService {

    @GET(SSPaiUrl.HOT_PAGE)
    suspend fun getHotPage(
        @Query("offset") offset: Int,
        @Query("limit") limit: Int = Constants.NORMAL_LIMIT,
        @Query("created_at") createdAt: Long,
    ): DiscoverPage<ColumnArticle>

    @GET(SSPaiUrl.USER_ACTIVITY_USER_PAGE)
    suspend fun getUserActivityUserPage(
        @Query("offset") offset: Int,
        @Query("limit") limit: Int,
        @Query("created_at") createdAt: Long,
    ): DiscoverPage<FollowedUserActivity>
}
