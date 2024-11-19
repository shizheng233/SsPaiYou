package com.shihcheeng.sspaiyou.api.service

import com.shihcheeng.sspaiyou.api.data.column.ColumnDataModel
import com.shihcheeng.sspaiyou.utils.Constants
import com.shihcheeng.sspaiyou.utils.SSPaiUrl
import com.shihcheeng.sspaiyou.utils.extra.createAtFor
import retrofit2.http.GET
import retrofit2.http.Query

interface ColumnService {

    @GET(SSPaiUrl.COLUMN_URL)
    suspend fun getColumn(
        @Query("limit") limit: Int = Constants.NORMAL_LIMIT,
        @Query("offset") offset: Int,
        @Query("created_at") createdAt: Long = createAtFor(offset)
    ): ColumnDataModel


}