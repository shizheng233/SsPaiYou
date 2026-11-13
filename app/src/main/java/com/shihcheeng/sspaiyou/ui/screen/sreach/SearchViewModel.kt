package com.shihcheeng.sspaiyou.ui.screen.sreach

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.shihcheeng.sspaiyou.api.data.column.ColumnArticle
import com.shihcheeng.sspaiyou.database.datamodel.LocalSearchRecordDataModel
import com.shihcheeng.sspaiyou.repository.SearchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: SearchRepository
) : ViewModel() {

    private val _searchRequest = MutableStateFlow(SearchRequest())
    val searchRequest = _searchRequest.asStateFlow()

    val searchHistory = repository.getLocalKeywords()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
            initialValue = emptyList<LocalSearchRecordDataModel>()
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val data = _searchRequest
        .flatMapLatest { request ->
            if (request.title.isEmpty()) {
                flowOf(PagingData.empty<ColumnArticle>())
            } else {
                repository.searchArticle(request.title).onStart {
                    // Clear the previous query's results without briefly showing an empty state.
                    emit(
                        PagingData.empty(
                            sourceLoadStates = LoadStates(
                                refresh = LoadState.Loading,
                                prepend = LoadState.NotLoading(true),
                                append = LoadState.NotLoading(true)
                            )
                        )
                    )
                }
            }
        }.cachedIn(viewModelScope)

    fun search(title: String) {
        val keyword = title.trim()
        _searchRequest.update {
            SearchRequest(title = keyword, id = it.id + 1)
        }
        if (keyword.isNotEmpty()) {
            viewModelScope.launch {
                repository.updateKeyword(keyword)
            }
        }
    }
}

// Each explicit submission starts a new search, including the same keyword after a failure.
data class SearchRequest(val title: String = "", val id: Long = 0)
