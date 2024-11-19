package com.shihcheeng.sspaiyou.api.service

import com.shihcheeng.sspaiyou.api.data.matrix.host.MatrixHostDataModel
import com.shihcheeng.sspaiyou.api.data.matrix.look.MatrixLookDataModel
import com.shihcheeng.sspaiyou.api.data.matrix.normal.MatrixNormalDataModel
import com.shihcheeng.sspaiyou.utils.SSPaiUrl
import com.shihcheeng.sspaiyou.utils.extra.createAtFor
import retrofit2.http.GET
import retrofit2.http.Query

interface MatrixService {

    @GET(SSPaiUrl.MATRIX_URL)
    suspend fun getMatrix(
        @Query("limit") limit: Int = 10,
        @Query("offset") offset: Int,
        @Query("created_at") createdAt: Long = createAtFor(offset)
    ): MatrixNormalDataModel

    @GET(SSPaiUrl.MATRIX_URL_HOT)
    suspend fun getMatrixHost(
        @Query("limit") limit: Int = 10,
        @Query("offset") offset: Int,
        @Query("created_at") createdAt: Long = createAtFor(offset)
    ): MatrixHostDataModel

    @GET(SSPaiUrl.MATRIX_URL_LOOK)
    suspend fun getMatrixLook(): MatrixLookDataModel

}