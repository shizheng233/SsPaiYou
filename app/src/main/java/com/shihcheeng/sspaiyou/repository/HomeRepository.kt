package com.shihcheeng.sspaiyou.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.shihcheeng.sspaiyou.api.data.home.sub.BannerData
import com.shihcheeng.sspaiyou.api.service.HomeService
import com.shihcheeng.sspaiyou.paging.HomePaging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HomeRepository @Inject constructor(
    private val homeService: HomeService
) {

    fun getHome() = Pager(config = PagingConfig(1)) {
        HomePaging(homeService)
    }.flow

    suspend fun getHomeBanner(): List<BannerData> = withContext(Dispatchers.Default) {
        homeService.getHomeSub().data
    }

}