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
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PersonalViewModel @Inject constructor(
    private val localTokenManager: LocalTokenManager,
    private val repository: UserRepository
) : ViewModel() {

    private val _info = mutableNetStateOf<UserinfoDataModel>()
    val info = _info.asStateFlow()

    val token = localTokenManager.tokenFlow(viewModelScope)
    private val refresh = MutableStateFlow(0)

    init {
        viewModelScope.launch {
            token.combine(refresh) { currentToken, _ -> currentToken }.collectLatest {
                _info.value = NetState.Loading
                if (it != null) {
                    loadUserInfo(it)
                }
            }
        }
    }

    fun load() {
        refresh.update { it + 1 }
    }

    private suspend fun loadUserInfo(expectedToken: String) {
        try {
            repository.getUserInfo().let {
                if (localTokenManager.getToken() != expectedToken) return
                if (it.error == 0 && it.data.slug.isNotBlank()) {
                    if (!localTokenManager.saveUserSlug(it.data.slug, expectedToken)) return
                }
                _info.emit(NetState.Success(it))
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            if (localTokenManager.getToken() != expectedToken) return
            e.printStackOnlyDebug()
            _info.emit(NetState.Error(e))
        }
    }

}
