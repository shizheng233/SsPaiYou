package com.shihcheeng.sspaiyou.ui.screen.userfollowed

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.shihcheeng.sspaiyou.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class UserFollowedViewModel @Inject constructor(
    repository: UserRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val users = repository.getFollowedUsers(
        slug = savedStateHandle.get<String>("user_slug").orEmpty()
    ).cachedIn(viewModelScope)

}
