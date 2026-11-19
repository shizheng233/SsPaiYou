package com.shihcheeng.sspaiyou.ui.screen.notity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.shihcheeng.sspaiyou.api.data.notification.CommentNotification
import com.shihcheeng.sspaiyou.api.data.notification.OnlineNotification
import com.shihcheeng.sspaiyou.repository.NotificationRepository
import com.shihcheeng.sspaiyou.utils.LocalTokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotifyViewModel @Inject constructor(
    private val repository: NotificationRepository,
    tokenManager: LocalTokenManager
) : ViewModel() {
    val token = tokenManager.tokenFlow(viewModelScope)

    @OptIn(ExperimentalCoroutinesApi::class)
    val online = token.map { it?.takeIf(String::isNotBlank) }.distinctUntilChanged()
        .flatMapLatest {
            if (it == null) flowOf(PagingData.empty())
            else repository.online(it)
        }
        .cachedIn(viewModelScope)

    @OptIn(ExperimentalCoroutinesApi::class)
    val comments = token.map { it?.takeIf(String::isNotBlank) }.distinctUntilChanged()
        .flatMapLatest {
            if (it == null) flowOf(PagingData.empty())
            else repository.comments(it)
        }
        .cachedIn(viewModelScope)

    fun markRead(notification: OnlineNotification, onRead: () -> Unit = {}) {
        val currentToken = token.value?.takeIf(String::isNotBlank) ?: return
        viewModelScope.launch {
            repository.markOnlineRead(currentToken, notification)
            onRead()
        }
    }

    fun markRead(notification: CommentNotification, onRead: () -> Unit = {}) {
        val currentToken = token.value?.takeIf(String::isNotBlank) ?: return
        viewModelScope.launch {
            repository.markCommentRead(currentToken, notification)
            onRead()
        }
    }
}
