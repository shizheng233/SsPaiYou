package com.shihcheeng.sspaiyou.ui.screen.history

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shihcheeng.sspaiyou.database.datamodel.LocalRecordDataModel
import com.shihcheeng.sspaiyou.repository.HistoryRepository
import com.shihcheeng.sspaiyou.utils.NetState
import com.shihcheeng.sspaiyou.utils.extra.printStackOnlyDebug
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val repository: HistoryRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val ownerKey = savedStateHandle.get<String>("owner_key")

    private val _state = MutableStateFlow<NetState<List<LocalRecordDataModel>>>(
        NetState.Loading
    )
    val state = _state.asStateFlow()

    private var historyJob: Job? = null

    init {
        loadingHistory()
    }

    fun loadingHistory() {
        historyJob?.cancel()
        historyJob = viewModelScope.launch {
            val key = ownerKey?.takeIf { it.isNotBlank() }
            if (key == null) {
                _state.value = NetState.Error(IllegalArgumentException("History owner key is missing"))
                return@launch
            }

            _state.value = NetState.Loading
            try {
                repository.observeHistory(key).collect { records ->
                    _state.value = NetState.Success(records)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _state.value = NetState.Error(e)
                e.printStackOnlyDebug()
            }
        }
    }

}
