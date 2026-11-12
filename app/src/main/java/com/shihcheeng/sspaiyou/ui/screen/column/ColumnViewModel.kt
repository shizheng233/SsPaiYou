package com.shihcheeng.sspaiyou.ui.screen.column

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.shihcheeng.sspaiyou.api.data.column.ColumnInfo
import com.shihcheeng.sspaiyou.repository.ColumnRepository
import com.shihcheeng.sspaiyou.utils.NetState
import com.shihcheeng.sspaiyou.utils.LocalTokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ColumnViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: ColumnRepository,
    tokenManager: LocalTokenManager
) : ViewModel() {
    private val columnId = savedStateHandle.get<String>("columnId")?.toIntOrNull()
        ?.takeIf { it > 0 }
    private val _info = MutableStateFlow<NetState<ColumnInfo>>(NetState.Loading)
    val info = _info.asStateFlow()
    val articles = (columnId?.let { repository.articles(it) }
        ?: flowOf(PagingData.empty())).cachedIn(viewModelScope)
    private var loadJob: Job? = null
    val followPending = repository.followPending
    val token = tokenManager.tokenFlow(viewModelScope)
    private val _followError = MutableStateFlow(false)
    val followError = _followError.asStateFlow()
    private var followVersion = 0
    private var latestFollowed: Boolean? = null

    init {
        viewModelScope.launch {
            token.drop(1).collect { load() }
        }
        viewModelScope.launch {
            repository.followChanges.collect { (id, followed) ->
                if (id == columnId) {
                    followVersion++
                    latestFollowed = followed
                    val current = _info.value
                    if (current is NetState.Success) {
                        _info.value = NetState.Success(current.content.withFollowed(followed))
                    }
                }
            }
        }
        load()
    }

    fun load() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            val versionAtStart = followVersion
            _info.value = NetState.Loading
            try {
                val loaded = repository.getColumn(requireNotNull(columnId) { "Invalid column ID" })
                _info.value = NetState.Success(if (followVersion != versionAtStart) {
                    loaded.withFollowed(requireNotNull(latestFollowed))
                } else loaded)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _info.value = NetState.Error(e)
            }
        }
    }

    fun setFollowed(followed: Boolean) {
        val id = columnId ?: return
        if (id in followPending.value) return
        viewModelScope.launch {
            try {
                repository.setFollowed(id, followed)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _followError.value = true
            }
        }
    }

    fun clearFollowError() { _followError.value = false }
}

private fun ColumnInfo.withFollowed(value: Boolean): ColumnInfo = copy(
    followed = value,
    followCount = (followCount + if (followed == value) 0 else if (value) 1 else -1).coerceAtLeast(0)
)
