package com.shihcheeng.sspaiyou.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.shihcheeng.sspaiyou.api.data.column.ColumnInfo
import com.shihcheeng.sspaiyou.api.data.column.ColumnFollowRequest
import com.shihcheeng.sspaiyou.api.service.ColumnService
import com.shihcheeng.sspaiyou.paging.ColumnArticlePaging
import com.shihcheeng.sspaiyou.paging.ColumnPaging
import com.shihcheeng.sspaiyou.utils.Constants
import com.shihcheeng.sspaiyou.utils.LocalTokenManager
import com.shihcheeng.sspaiyou.utils.extra.IGNORE_PAGE_SIZE
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ColumnRepository internal constructor(
    private val service: ColumnService,
    private val readToken: () -> String?
) {
    // Resolve the token on every read so in-flight requests can detect session changes.
    @Inject
    constructor(service: ColumnService, tokenManager: LocalTokenManager) :
        this(service, tokenManager::getToken)

    private val _followChanges = MutableSharedFlow<Pair<Int, Boolean>>(extraBufferCapacity = 16)
    val followChanges = _followChanges.asSharedFlow()
    private val _followPending = MutableStateFlow<Set<Int>>(emptySet())
    val followPending = _followPending.asStateFlow()
    private val followLock = Any()

    suspend fun setFollowed(id: Int, followed: Boolean) {
        require(id > 0) { "Invalid column ID" }
        synchronized(followLock) {
            if (id in _followPending.value) return
            _followPending.value = _followPending.value + id
        }
        try {
            val token = readToken()?.takeIf { it.isNotBlank() } ?: return
            val response = service.followColumn(ColumnFollowRequest(id, if (followed) 1 else 2))
            if (token != readToken()) return
            check(response.error == 0) {
                response.msg.ifBlank { "Column follow API error: ${response.error}" }
            }
            _followChanges.emit(id to followed)
        } finally {
            synchronized(followLock) {
                _followPending.value = _followPending.value - id
            }
        }
    }

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
