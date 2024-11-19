package com.shihcheeng.sspaiyou.utils

import androidx.compose.runtime.Stable

sealed class NetState<out T> {
    @Stable
    data class Success<T>(val content: T) : NetState<T>()

    @Stable
    data class Error(val throwable: Throwable) : NetState<Nothing>()

    @Stable
    data object Loading : NetState<Nothing>()
}