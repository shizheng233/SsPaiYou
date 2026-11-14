package com.shihcheeng.sspaiyou.paging

import com.shihcheeng.sspaiyou.api.data.matrix.normal.NormalMatrixData
import com.shihcheeng.sspaiyou.api.service.MatrixService
import com.shihcheeng.sspaiyou.paging.base.BaseMatrixPaging

class MatrixPaging(
    private val service: MatrixService
) : BaseMatrixPaging<NormalMatrixData>() {

    override suspend fun loadPage(limit: Int, offset: Int, createdAt: Long): PageData<NormalMatrixData> {
        val response = service.getMatrix(limit = limit, offset = offset, createdAt = createdAt)
        check(response.error == 0) {
            response.msg.ifBlank { "Matrix feed API error: ${response.error}" }
        }
        return PageData(response.data, response.total)
    }
}
