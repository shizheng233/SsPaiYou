package com.shihcheeng.sspaiyou.ui.screen.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shihcheeng.sspaiyou.utils.LocalTokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val localTokenManager: LocalTokenManager
) : ViewModel() {

    fun logOut() = viewModelScope.launch(Dispatchers.Default) {
        localTokenManager.logout()
    }

}