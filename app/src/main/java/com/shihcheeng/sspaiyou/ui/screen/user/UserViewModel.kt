package com.shihcheeng.sspaiyou.ui.screen.user

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.shihcheeng.sspaiyou.api.data.user.third.ThirdUserDataModel
import com.shihcheeng.sspaiyou.repository.UserRepository
import com.shihcheeng.sspaiyou.utils.NetState
import com.shihcheeng.sspaiyou.utils.extra.mutableNetStateOf
import com.shihcheeng.sspaiyou.utils.extra.printStackOnlyDebug
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val repository: UserRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _slug = savedStateHandle.get<String>("slug")
    val slug = MutableStateFlow(_slug)
        .filter { x -> x?.isNotEmpty() == true }
        .filterNotNull()
        .stateIn(scope = viewModelScope, started = SharingStarted.Eagerly, initialValue = "")

    @OptIn(ExperimentalCoroutinesApi::class)
    val data = slug.flatMapLatest {
        repository.getData(it)
    }.cachedIn(viewModelScope)

    private val _data = mutableNetStateOf<ThirdUserDataModel>()
    val dataOfUser = _data.asStateFlow()

    private val _title = MutableStateFlow("")
    val title = _title.asStateFlow()

    init {
        load()
    }

    fun load() = viewModelScope.launch {
        _data.emit(NetState.Loading)
        try {
            _slug?.let {
                val data = repository.getUserInfo(slug = it)
                _data.emit(NetState.Success(data))
                _title.emit(data.data.nickname)
            }
        } catch (e: Exception) {
            e.printStackOnlyDebug()
            _data.emit(NetState.Error(e))
        }
    }

}