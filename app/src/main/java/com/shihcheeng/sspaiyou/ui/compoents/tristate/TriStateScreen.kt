package com.shihcheeng.sspaiyou.ui.compoents.tristate

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.shihcheeng.sspaiyou.ui.compoents.Loading
import com.shihcheeng.sspaiyou.utils.NetState

@Composable
fun <T> TriStateScreen(
    modifier: Modifier = Modifier,
    padding: PaddingValues = PaddingValues(0.dp),
    triState: NetState<T>,
    onError: @Composable (Throwable) -> Unit,
    onLoading: @Composable () -> Unit = { Loading() },
    onSuccess: @Composable (T) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .then(modifier),
    ) {
        when (triState) {
            is NetState.Error -> onError(triState.throwable)
            NetState.Loading -> onLoading()
            is NetState.Success -> onSuccess(triState.content)
        }
    }
}