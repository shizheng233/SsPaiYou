package com.shihcheeng.sspaiyou.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.shihcheeng.sspaiyou.api.service.MatrixService
import com.shihcheeng.sspaiyou.paging.MatrixHotPaging
import com.shihcheeng.sspaiyou.paging.MatrixPaging
import com.shihcheeng.sspaiyou.paging.BulletPaging
import com.shihcheeng.sspaiyou.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MatrixRepository @Inject constructor(
    private val service: MatrixService
) {

    val normalData
        get() = Pager(
            config = PagingConfig(
                pageSize = Constants.NORMAL_LIMIT,
                initialLoadSize = Constants.NORMAL_LIMIT,
                enablePlaceholders = false
            )
        ) {
            MatrixPaging(service)
        }.flow

    val hostData
        get() = Pager(
            config = PagingConfig(
                pageSize = Constants.NORMAL_LIMIT,
                initialLoadSize = Constants.NORMAL_LIMIT,
                enablePlaceholders = false
            )
        ) {
            MatrixHotPaging(service)
        }.flow

    val bulletData
        get() = Pager(
            config = PagingConfig(pageSize = 10, initialLoadSize = 10, enablePlaceholders = false)
        ) {
            BulletPaging(service)
        }.flow

    suspend fun getLookData() = withContext(Dispatchers.Default) {
        service.getMatrixLook().data
    }

}
