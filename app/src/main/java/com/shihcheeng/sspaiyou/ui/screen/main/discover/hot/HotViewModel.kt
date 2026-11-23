package com.shihcheeng.sspaiyou.ui.screen.main.discover.hot

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.shihcheeng.sspaiyou.repository.DiscoverRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HotViewModel @Inject constructor(
    discoverRepository: DiscoverRepository
) : ViewModel() {

    val items = discoverRepository.getHotArticles().cachedIn(viewModelScope)

}