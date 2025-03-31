package com.shihcheeng.sspaiyou.utils

import androidx.compose.runtime.Stable
import androidx.lifecycle.LifecycleOwner
import com.shihcheeng.sspaiyou.utils.extra.observe
import kotlinx.coroutines.flow.StateFlow

sealed class NetState<out T> {
    @Stable
    data class Success<T>(val content: T) : NetState<T>()

    @Stable
    data class Error(val throwable: Throwable) : NetState<Nothing>()

    @Stable
    data object Loading : NetState<Nothing>()

}

interface NetStateListener<T> {
    fun loading()
    fun error(throwable: Throwable)
    fun success(content: T)
}

fun <T> StateFlow<NetState<T>>.collectWithState(
    owner: LifecycleOwner,
    netStateListener: NetStateListener<T>
) {
    this.observe(owner) {
        when (it) {
            is NetState.Error -> netStateListener.error(it.throwable)
            NetState.Loading -> netStateListener.loading()
            is NetState.Success -> netStateListener.success(it.content)
        }
    }
}