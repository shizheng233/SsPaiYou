package com.shihcheeng.sspaiyou.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.shihcheeng.sspaiyou.api.service.UserService
import com.shihcheeng.sspaiyou.paging.ActivityPaging
import com.shihcheeng.sspaiyou.paging.UserFavouritePaging
import com.shihcheeng.sspaiyou.utils.Constants
import com.shihcheeng.sspaiyou.utils.extra.IGNORE_PAGE_SIZE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val service: UserService
) {

    suspend fun getUserInfo() = withContext(Dispatchers.Default) {
        service.getUserInfo()
    }

    suspend fun getUserInfo(slug: String) = withContext(Dispatchers.Default) {
        service.getUserInfo(slug)
    }

    fun getData(slug: String) = Pager(
        config = PagingConfig(pageSize = PagingConfig.IGNORE_PAGE_SIZE)
    ) {
        ActivityPaging(service, slug)
    }.flow

    fun getFavourites(slug: String) = Pager(
        config = PagingConfig(
            pageSize = Constants.NORMAL_LIMIT,
            initialLoadSize = Constants.NORMAL_LIMIT,
            enablePlaceholders = false
        )
    ) {
        UserFavouritePaging(service, slug)
    }.flow
}
