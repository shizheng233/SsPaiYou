package com.shihcheeng.sspaiyou.ui.screen.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shihcheeng.sspaiyou.api.data.actrcle.ArticleData
import com.shihcheeng.sspaiyou.repository.DetailRepository
import com.shihcheeng.sspaiyou.utils.NetState
import com.shihcheeng.sspaiyou.utils.extra.printStackOnlyDebug
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: DetailRepository
) : ViewModel() {

    val id = savedStateHandle.get<String>("id")

    private val _data = MutableStateFlow<NetState<ArticleData>>(NetState.Loading)
    val data = _data.asStateFlow()

    init {
        load()
    }

    fun load() = viewModelScope.launch {
        _data.emit(NetState.Loading)
        try {
            id?.let {
                val dataI = repository.getArticleDetail(it)
                _data.emit(NetState.Success(dataI))
            }
        } catch (e: Exception) {
            e.printStackOnlyDebug()
            _data.emit(NetState.Error(e))
        }
    }

}