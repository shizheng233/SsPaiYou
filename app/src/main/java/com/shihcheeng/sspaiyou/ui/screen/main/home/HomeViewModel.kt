package com.shihcheeng.sspaiyou.ui.screen.main.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.shihcheeng.sspaiyou.api.data.home.second.MainsItem
import com.shihcheeng.sspaiyou.repository.HomeRepository
import com.shihcheeng.sspaiyou.utils.NetState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: HomeRepository
) : ViewModel() {

    val data = repository.getHome().cachedIn(viewModelScope)

    private val _banner = MutableStateFlow<NetState<List<MainsItem>>>(NetState.Loading)
    val banner = _banner.asStateFlow()

    init {
        loadBanner()
    }

    fun loadBanner() = viewModelScope.launch {
        _banner.tryEmit(NetState.Loading)
        try {
            _banner.tryEmit(NetState.Success(repository.getHomeBanner()))
        } catch (e: Exception) {
            e.printStackTrace()
            _banner.tryEmit(NetState.Error(e))
        }
    }

}