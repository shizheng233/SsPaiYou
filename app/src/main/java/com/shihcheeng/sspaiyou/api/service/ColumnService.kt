package com.shihcheeng.sspaiyou.api.service

import com.shihcheeng.sspaiyou.api.data.column.ColumnDataModel
import com.shihcheeng.sspaiyou.api.data.column.ColumnArticleDataModel
import com.shihcheeng.sspaiyou.api.data.column.ColumnSingleDataModel
import com.shihcheeng.sspaiyou.api.data.column.ColumnFollowRequest
import com.shihcheeng.sspaiyou.api.data.column.ColumnFollowResponse
import com.shihcheeng.sspaiyou.utils.Constants
import com.shihcheeng.sspaiyou.utils.SSPaiUrl
import com.shihcheeng.sspaiyou.utils.extra.createAtFor
import retrofit2.http.GET
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface ColumnService {

    @GET(SSPaiUrl.COLUMN_URL)
    suspend fun getColumn(
        @Query("limit") limit: Int = Constants.NORMAL_LIMIT,
        @Query("offset") offset: Int,
        @Query("created_at") createdAt: Long = createAtFor(offset)
    ): ColumnDataModel

    @GET(SSPaiUrl.COLUMN_SINGLE)
    suspend fun getColumnSingle(
        @Query("id") id: Int,
        @Query("limit") limit: Int = 4,
        @Query("view") view: String = "second"
    ): ColumnSingleDataModel

    @GET(SSPaiUrl.COLUMN_SINGLE_ARTICLE)
    suspend fun getColumnSingleArticle(
        @Query("special_column_id") specialColumnId: Int,
        @Query("limit") limit: Int,
        @Query("offset") offset: Int,
        @Query("created_at") createdAt: Long,
        @Query("view") view: String = "second"
    ): ColumnArticleDataModel

    @POST(SSPaiUrl.FOLLOW_COLUMN)
    suspend fun followColumn(@Body request: ColumnFollowRequest): ColumnFollowResponse

}
