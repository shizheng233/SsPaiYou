package com.shihcheeng.sspaiyou.ui.screen.main.discover.followactivity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.shihcheeng.sspaiyou.repository.DiscoverRepository
import com.shihcheeng.sspaiyou.utils.LocalTokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FollowActivityViewModel @Inject constructor(
    discoverRepository: DiscoverRepository,
    tokenManager: LocalTokenManager,
) : ViewModel() {

    val items = discoverRepository.getFollowedActivities()
        .cachedIn(viewModelScope)

    val token = tokenManager.tokenFlow(viewModelScope)

}