package com.shihcheeng.sspaiyou.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.shihcheeng.sspaiyou.api.data.column.ColumnArticle
import com.shihcheeng.sspaiyou.api.service.SearchService
import com.shihcheeng.sspaiyou.database.dao.SearchKeywordDAO
import com.shihcheeng.sspaiyou.database.datamodel.LocalSearchRecordDataModel
import com.shihcheeng.sspaiyou.paging.SearchArticlePaging
import com.shihcheeng.sspaiyou.utils.extra.getSystemSecond
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchRepository @Inject constructor(
    private val service: SearchService,
    private val keywordDAO: SearchKeywordDAO
) {
    /**
     * Keeps existing service-only callers useful (for example, paging unit tests).
     * Hilt uses the injected constructor above, so production still receives Room.
     */
    constructor(service: SearchService) : this(service, EmptySearchKeywordDAO)

    fun searchArticle(title: String): Flow<PagingData<ColumnArticle>> = Pager(
        config = PagingConfig(
            pageSize = 8,
            initialLoadSize = 8,
            enablePlaceholders = false,
            prefetchDistance = 1
        )
    ) {
        SearchArticlePaging(service, title)
    }.flow

    suspend fun updateKeyword(keyWord: String) {
        val keyword = keyWord.trim()
        if (keyword.isNotEmpty()) {
            keywordDAO.updateKeywords(
                recordDataModel = LocalSearchRecordDataModel(
                    text = keyword,
                    time = getSystemSecond()
                )
            )
        }
    }

    fun getLocalKeywords() = keywordDAO.getKeywords()

    private object EmptySearchKeywordDAO : SearchKeywordDAO {
        override fun getKeywords(): Flow<List<LocalSearchRecordDataModel>> = emptyFlow()

        override suspend fun updateKeywords(recordDataModel: LocalSearchRecordDataModel) = Unit
    }
}
