package com.shihcheeng.sspaiyou.ui.screen.main.matrix.column

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.shihcheeng.sspaiyou.repository.ColumnRepository
import com.shihcheeng.sspaiyou.utils.LocalTokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ColumnViewModel @Inject constructor(
    private val repository: ColumnRepository,
    tokenManager: LocalTokenManager
) : ViewModel() {

    val data = repository.columns.cachedIn(viewModelScope)
    val token = tokenManager.tokenFlow(viewModelScope)
    val followPending = repository.followPending
    private val _followed = MutableStateFlow<Map<Int, Boolean>>(emptyMap())
    val followed = _followed.asStateFlow()
    private val _followError = MutableStateFlow(false)
    val followError = _followError.asStateFlow()

    init {
        viewModelScope.launch {
            repository.followChanges.collect { (id, followed) ->
                _followed.update { it + (id to followed) }
            }
        }
    }

    fun setFollowed(id: Int, followed: Boolean) {
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
    fun clearFollowedOverrides() { _followed.value = emptyMap() }

}
