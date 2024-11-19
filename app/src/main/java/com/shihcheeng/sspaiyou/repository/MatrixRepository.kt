package com.shihcheeng.sspaiyou.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.shihcheeng.sspaiyou.api.service.MatrixService
import com.shihcheeng.sspaiyou.paging.MatrixHotPaging
import com.shihcheeng.sspaiyou.paging.MatrixPaging
import com.shihcheeng.sspaiyou.utils.extra.IGNORE_PAGE_SIZE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MatrixRepository @Inject constructor(
    private val service: MatrixService
) {

    val normalData
        get() = Pager(config = PagingConfig(pageSize = PagingConfig.IGNORE_PAGE_SIZE)) {
            MatrixPaging(service)
        }.flow

    val hostData
        get() = Pager(config = PagingConfig(pageSize = PagingConfig.IGNORE_PAGE_SIZE)) {
            MatrixHotPaging(service)
        }.flow

    suspend fun getLookData() = withContext(Dispatchers.Default) {
        service.getMatrixLook().data
    }

}