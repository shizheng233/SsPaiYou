package com.shihcheeng.sspaiyou.ui.screen.column

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.shihcheeng.sspaiyou.api.data.column.ColumnInfo
import com.shihcheeng.sspaiyou.repository.ColumnRepository
import com.shihcheeng.sspaiyou.utils.NetState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ColumnViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: ColumnRepository
) : ViewModel() {
    private val columnId = savedStateHandle.get<String>("columnId")?.toIntOrNull()
        ?.takeIf { it > 0 }
    private val _info = MutableStateFlow<NetState<ColumnInfo>>(NetState.Loading)
    val info = _info.asStateFlow()
    val articles = (columnId?.let { repository.articles(it) }
        ?: flowOf(PagingData.empty())).cachedIn(viewModelScope)
    private var loadJob: Job? = null

    init {
        load()
    }

    fun load() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _info.value = NetState.Loading
            try {
                _info.value = NetState.Success(
                    repository.getColumn(requireNotNull(columnId) { "Invalid column ID" })
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _info.value = NetState.Error(e)
            }
        }
    }
}
