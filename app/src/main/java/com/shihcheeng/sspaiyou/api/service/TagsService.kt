package com.shihcheeng.sspaiyou.api.service

import com.shihcheeng.sspaiyou.api.data.column.ColumnArticleDataModel
import com.shihcheeng.sspaiyou.utils.SSPaiUrl
import retrofit2.http.GET
import retrofit2.http.Query

interface TagsService {
    @GET(SSPaiUrl.ARTICLE_TAGS)
    suspend fun getArticleTagsList(
        @Query("limit") limit: Int,
        @Query("offset") offset: Int,
        @Query("tag") tagName: String
    ): ColumnArticleDataModel
}
