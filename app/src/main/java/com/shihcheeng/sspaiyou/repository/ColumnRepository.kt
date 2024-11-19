package com.shihcheeng.sspaiyou.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.shihcheeng.sspaiyou.api.service.ColumnService
import com.shihcheeng.sspaiyou.paging.ColumnPaging
import com.shihcheeng.sspaiyou.utils.extra.IGNORE_PAGE_SIZE
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ColumnRepository @Inject constructor(
    private val service: ColumnService
) {

    val columns
        get() = Pager(config = PagingConfig(pageSize = PagingConfig.IGNORE_PAGE_SIZE)) {
            ColumnPaging(service)
        }.flow

}