package com.shihcheeng.sspaiyou.api.service

import com.shihcheeng.sspaiyou.api.data.user.UserinfoDataModel
import com.shihcheeng.sspaiyou.api.data.user.activity.UserActivityDataModel
import com.shihcheeng.sspaiyou.api.data.user.favourite.UserFavouriteDataModel
import com.shihcheeng.sspaiyou.api.data.user.follow.FollowMessageModel
import com.shihcheeng.sspaiyou.api.data.user.follow.FollowUserRequest
import com.shihcheeng.sspaiyou.api.data.user.follow.UserFollowedDataModel
import com.shihcheeng.sspaiyou.api.data.user.third.ThirdUserDataModel
import com.shihcheeng.sspaiyou.utils.Constants
import com.shihcheeng.sspaiyou.utils.SSPaiUrl
import com.shihcheeng.sspaiyou.utils.extra.createAtFor
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
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

    @GET(SSPaiUrl.USER_FAVOURE)
    suspend fun getUserFavourite(
        @Query("limit") limit: Int,
        @Query("offset") offset: Int,
        @Query("created_at") createdAt: Long,
        @Query("slug") slug: String,
        @Query("type") type: String = "all"
    ): UserFavouriteDataModel

    @POST(SSPaiUrl.FOLLOW_USER)
    suspend fun followUser(
        @Body request: FollowUserRequest
    ): FollowMessageModel

    /**
     * 获取用户的关注列表。
     *
     * @param slug 用户的slug。
     */
    @GET(SSPaiUrl.FOLLOW_USER_LIST)
    suspend fun getUserList(
        @Query("limit") limit: Int,
        @Query("offset") offset: Int,
        @Query("slug") slug: String
    ): UserFollowedDataModel
}
