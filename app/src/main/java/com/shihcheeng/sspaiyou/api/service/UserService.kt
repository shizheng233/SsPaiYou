package com.shihcheeng.sspaiyou.api.service

import com.shihcheeng.sspaiyou.api.data.user.UserinfoDataModel
import com.shihcheeng.sspaiyou.api.data.user.activity.UserActivityDataModel
import com.shihcheeng.sspaiyou.api.data.user.third.ThirdUserDataModel
import com.shihcheeng.sspaiyou.utils.Constants
import com.shihcheeng.sspaiyou.utils.SSPaiUrl
import com.shihcheeng.sspaiyou.utils.extra.createAtFor
import retrofit2.http.GET
import retrofit2.http.Query

interface UserService {

    @GET(SSPaiUrl.USER_INFO)
    suspend fun getUserInfo(): UserinfoDataModel

    @GET(SSPaiUrl.USER_SLUG_INFO)
    suspend fun getUserInfo(
        @Query("slug") slug: String
    ): ThirdUserDataModel


    @GET(SSPaiUrl.USER_ACTIVITY)
    suspend fun getUserActivity(
        @Query("limit") limit: Int = Constants.NORMAL_LIMIT,
        @Query("offset") offset: Int,
        @Query("created_at") createdAt: Long = createAtFor(offset),
        @Query("slug") slug: String
    ): UserActivityDataModel

}