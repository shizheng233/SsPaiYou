package com.shihcheeng.sspaiyou.ui.screen.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shihcheeng.sspaiyou.utils.LocalTokenManager
import com.shihcheeng.sspaiyou.utils.extra.printStackOnlyDebug
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WebLoginViewModel @Inject constructor(
    private val tokenManager: LocalTokenManager
) : ViewModel() {

    val token = tokenManager.tokenFlow(viewModelScope)
    private val _saveAccomplish = MutableStateFlow(false)
    val saveAccomplish = _saveAccomplish.asStateFlow()

    fun saveToken(token: String) = viewModelScope.launch {
        try {
            tokenManager.saveToken(token)
            _saveAccomplish.emit(true)
        } catch (e: Exception) {
            e.printStackOnlyDebug()
            _saveAccomplish.emit(false)
        }
    }

}