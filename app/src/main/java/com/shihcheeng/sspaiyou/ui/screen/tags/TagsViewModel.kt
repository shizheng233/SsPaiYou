package com.shihcheeng.sspaiyou.ui.screen.tags

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.shihcheeng.sspaiyou.api.data.column.ColumnArticle
import com.shihcheeng.sspaiyou.repository.TagsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

@HiltViewModel
class TagsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    repository: TagsRepository
) : ViewModel() {
    private val tagName = savedStateHandle.get<String>("tag_name").orEmpty()
    val articles = (
        tagName.takeIf { it.isNotBlank() }
            ?.let(repository::articles)
            ?: flowOf(PagingData.empty<ColumnArticle>())
        ).cachedIn(viewModelScope)
}
