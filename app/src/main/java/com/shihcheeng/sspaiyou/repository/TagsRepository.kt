package com.shihcheeng.sspaiyou.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.shihcheeng.sspaiyou.api.service.TagsService
import com.shihcheeng.sspaiyou.paging.TagsArticlePaging
import com.shihcheeng.sspaiyou.utils.Constants
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TagsRepository @Inject constructor(
    private val service: TagsService
) {
    fun articles(tagName: String) = Pager(
        config = PagingConfig(
            pageSize = Constants.NORMAL_LIMIT,
            initialLoadSize = Constants.NORMAL_LIMIT,
            enablePlaceholders = false
        )
    ) {
        TagsArticlePaging(service, tagName)
    }.flow
}
