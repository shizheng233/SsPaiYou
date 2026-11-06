package com.shihcheeng.sspaiyou.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.shihcheeng.sspaiyou.api.data.column.ColumnInfo
import com.shihcheeng.sspaiyou.api.service.ColumnService
import com.shihcheeng.sspaiyou.paging.ColumnArticlePaging
import com.shihcheeng.sspaiyou.paging.ColumnPaging
import com.shihcheeng.sspaiyou.utils.Constants
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

    suspend fun getColumn(id: Int): ColumnInfo {
        val response = service.getColumnSingle(id)
        check(response.error == 0) {
            response.msg.ifBlank { "Column API error: ${response.error}" }
        }
        return checkNotNull(response.data) { "Column data is missing" }
    }

    fun articles(columnId: Int) = Pager(
        config = PagingConfig(
            pageSize = Constants.NORMAL_LIMIT,
            initialLoadSize = Constants.NORMAL_LIMIT,
            enablePlaceholders = false
        )
    ) {
        ColumnArticlePaging(service, columnId)
    }.flow

}
