package com.shihcheeng.sspaiyou.paging

import com.shihcheeng.sspaiyou.api.data.matrix.host.HotMatrixData
import com.shihcheeng.sspaiyou.api.service.MatrixService
import com.shihcheeng.sspaiyou.paging.base.BaseMatrixPaging

class MatrixHotPaging(
    private val service: MatrixService
) : BaseMatrixPaging<HotMatrixData>() {
    override suspend fun loadPage(limit: Int, offset: Int, createdAt: Long): PageData<HotMatrixData> {
        val response = service.getMatrixHost(limit = limit, offset = offset, createdAt = createdAt)
        check(response.error == 0) {
            response.msg.ifBlank { "Hot Matrix feed API error: ${response.error}" }
        }
        return PageData(response.data, response.total)
    }
}
