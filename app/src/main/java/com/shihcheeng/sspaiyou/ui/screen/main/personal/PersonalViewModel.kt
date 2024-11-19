package com.shihcheeng.sspaiyou.ui.screen.main.personal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shihcheeng.sspaiyou.api.data.user.UserinfoDataModel
import com.shihcheeng.sspaiyou.repository.UserRepository
import com.shihcheeng.sspaiyou.utils.LocalTokenManager
import com.shihcheeng.sspaiyou.utils.NetState
import com.shihcheeng.sspaiyou.utils.extra.mutableNetStateOf
import com.shihcheeng.sspaiyou.utils.extra.printStackOnlyDebug
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PersonalViewModel @Inject constructor(
    localTokenManager: LocalTokenManager,
    private val repository: UserRepository
) : ViewModel() {

    private val _info = mutableNetStateOf<UserinfoDataModel>()
    val info = _info.asStateFlow()

    val token = localTokenManager.tokenFlow(viewModelScope)

    init {
        viewModelScope.launch {
            token.collectLatest {
                if (it != null) {
                    load()
                }
            }
        }
    }

    fun load() = viewModelScope.launch {
        _info.emit(NetState.Loading)
        try {
            repository.getUserInfo().let {
                _info.emit(NetState.Success(it))
            }
        } catch (e: Exception) {
            e.printStackOnlyDebug()
            _info.emit(NetState.Error(e))
        }
    }

}