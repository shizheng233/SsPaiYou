package com.shihcheeng.sspaiyou.paging

import com.shihcheeng.sspaiyou.api.data.matrix.host.HotMatrixData
import com.shihcheeng.sspaiyou.api.service.MatrixService
import com.shihcheeng.sspaiyou.paging.base.BaseMatrixPaging
import com.shihcheeng.sspaiyou.utils.Constants

class MatrixHotPaging(
    private val service: MatrixService
) : BaseMatrixPaging<HotMatrixData>() {
    override suspend fun getData(offset: Int): List<HotMatrixData> {
        return service.getMatrixHost(offset = offset).data
    }

    override val loadOffset: Int
        get() = Constants.NORMAL_LIMIT

}