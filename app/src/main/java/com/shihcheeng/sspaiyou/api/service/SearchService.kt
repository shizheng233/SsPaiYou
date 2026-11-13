package com.shihcheeng.sspaiyou.api.service

import com.shihcheeng.sspaiyou.api.data.search.SearchArticleDataModel
import com.shihcheeng.sspaiyou.utils.SSPaiUrl
import com.shihcheeng.sspaiyou.utils.extra.getSystemSecond
import retrofit2.http.GET
import retrofit2.http.Query

interface SearchService {

    /**
     * 搜索文章，默认仅查询免费文章。
     *
     * @param free 1 为免费文章，2 会包含会员文章。
     * @param searchType 1 为文章。
     * @param stime 搜索开始时间，单位为秒，0 表示不限制开始时间。
     * @param etime 搜索结束时间，单位为秒，默认当前时间。
     * @param offset 已加载的文章数量。
     * @param limit 本次请求的文章数量。
     */
    @GET(SSPaiUrl.SEARCH_ARTICLE)
    suspend fun searchArticle(
        @Query("free") free: Int = 1,
        @Query("title") title: String,
        @Query("search_types") searchType: String = "1",
        @Query("stime") stime: Long = 0L,
        @Query("etime") etime: Long = getSystemSecond(),
        @Query("offset") offset: Int = 0,
        @Query("limit") limit: Int = 8
    ): SearchArticleDataModel

}
