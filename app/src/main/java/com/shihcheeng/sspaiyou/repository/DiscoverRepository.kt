package com.shihcheeng.sspaiyou.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.shihcheeng.sspaiyou.api.data.column.ColumnArticle
import com.shihcheeng.sspaiyou.api.data.discover.FollowedUserActivity
import com.shihcheeng.sspaiyou.api.service.DiscoverService
import com.shihcheeng.sspaiyou.paging.DiscoverPaging
import com.shihcheeng.sspaiyou.utils.Constants
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DiscoverRepository @Inject constructor(
    private val discoverService: DiscoverService,
) {
    private val pagingConfig = PagingConfig(
        pageSize = Constants.NORMAL_LIMIT,
        initialLoadSize = Constants.NORMAL_LIMIT,
        enablePlaceholders = false,
    )

    fun getHotArticles(): Flow<PagingData<ColumnArticle>> = Pager(config = pagingConfig) {
        DiscoverPaging(fetchPage = discoverService::getHotPage)
    }.flow

    fun getFollowedActivities(): Flow<PagingData<FollowedUserActivity>> = Pager(config = pagingConfig) {
        DiscoverPaging(fetchPage = discoverService::getUserActivityUserPage)
    }.flow
}
