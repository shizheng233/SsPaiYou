package com.shihcheeng.sspaiyou.paging

import com.shihcheeng.sspaiyou.api.data.matrix.normal.NormalMatrixData
import com.shihcheeng.sspaiyou.api.service.MatrixService
import com.shihcheeng.sspaiyou.paging.base.BaseMatrixPaging

class MatrixPaging(
    private val service: MatrixService
) : BaseMatrixPaging<NormalMatrixData>() {

    override suspend fun getData(offset: Int): List<NormalMatrixData> {
        return service.getMatrix(offset = offset).data
    }

    override val loadOffset: Int
        get() = 10
}