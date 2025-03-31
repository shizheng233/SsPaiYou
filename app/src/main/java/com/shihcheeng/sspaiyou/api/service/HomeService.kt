package com.shihcheeng.sspaiyou.api.service

import com.shihcheeng.sspaiyou.api.data.home.main.HomeMainDataModel
import com.shihcheeng.sspaiyou.api.data.home.sub.HomeSubDataModel
import com.shihcheeng.sspaiyou.api.data.home.top.HeaderBannerDataModel
import com.shihcheeng.sspaiyou.utils.Constants
import com.shihcheeng.sspaiyou.utils.SSPaiUrl
import com.shihcheeng.sspaiyou.utils.extra.getSystemSecond
import retrofit2.http.GET
import retrofit2.http.Query

interface HomeService {

    @GET(SSPaiUrl.HOME_SUB_URL)
    suspend fun getHomeSub(
        @Query("limit") limit: Int = 4,
        @Query("offset") offset: Int = 0,
        @Query("type") type: String = Constants.TYPE_HOME_SUB
    ): HomeSubDataModel

    @GET(SSPaiUrl.HOME_MAIN_URL)
    suspend fun getHome(
        @Query("limit") limit: Int = 10,
        @Query("offset") offset: Int,
        @Query("created_at") createdAt: Long = if (offset == 0) 0 else getSystemSecond()
    ): HomeMainDataModel

    @GET(SSPaiUrl.HOME_SUB_URL)
    suspend fun getHomeMain(
        @Query("limit") limit: Int = 5,
        @Query("offset") offset: Int = 0,
        @Query("type") type: String = Constants.TYPE_HOME_MAIN
    ): HeaderBannerDataModel

}