package com.shihcheeng.sspaiyou.ui.compoents.tristate

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import com.shihcheeng.sspaiyou.utils.NetState


fun <T> LazyListScope.triStateItem(
    triState: NetState<T>,
    onLoading: @Composable () -> Unit,
    onError: @Composable (Throwable) -> Unit,
    onSuccess: (T) -> Unit
) {
    when (triState) {
        is NetState.Error -> item {
            onError(triState.throwable)
        }

        NetState.Loading -> item {
            onLoading()
        }

        is NetState.Success -> onSuccess(triState.content)
    }
}