package com.shihcheeng.sspaiyou.paging

import com.shihcheeng.sspaiyou.api.data.discover.DiscoverPage
import com.shihcheeng.sspaiyou.paging.base.BaseMatrixPaging

internal class DiscoverPaging<T : Any>(
    private val fetchPage: suspend (offset: Int, limit: Int, createdAt: Long) -> DiscoverPage<T>,
) : BaseMatrixPaging<T>() {
    override suspend fun loadPage(
        limit: Int,
        offset: Int,
        createdAt: Long,
    ): PageData<T> {
        val response = fetchPage(offset, limit, createdAt)
        check(response.error == 0) {
            response.msg.ifBlank { "发现页请求失败（错误码：${response.error}）" }
        }
        return PageData(
            data = response.data.orEmpty(),
            total = response.total,
        )
    }
}
