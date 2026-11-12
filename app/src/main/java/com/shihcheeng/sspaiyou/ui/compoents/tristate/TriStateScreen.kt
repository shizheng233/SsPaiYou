package com.shihcheeng.sspaiyou.ui.compoents.tristate

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import com.shihcheeng.sspaiyou.ui.compoents.Loading
import com.shihcheeng.sspaiyou.utils.NetState

/**
 * 这是对于自建的[NetState]状态屏幕，主要服务于三状态，也就是加载、成功、失败。
 * 注意到Tri是个词根。
 *
 * @param modifier The modifier to be applied to the layout.
 * @param padding Padding 数值，但是注意不要当做contentPadding使用，建议传入的时候看看会不会导致系统导航栏也被padding了。
 * @param triState 传入的[NetState]
 * @param onError 加载错误屏幕
 * @param onLoading 加载中屏幕
 * @param onSuccess 加载成功屏幕
 */
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

/**
 * 这是对于Paging的[LoadState]状态屏幕，主要服务于三状态，也就是加载、成功、失败。
 * 注意到Tri是个词根。
 *
 * @param modifier The modifier to be applied to the layout.
 * @param padding Padding 数值，但是注意不要当做contentPadding使用，建议传入的时候看看会不会导致系统导航栏也被padding了。
 * @param triState 传入的[LoadState]
 * @param onError 加载错误屏幕
 * @param onLoading 加载中屏幕
 * @param onSuccess 加载成功屏幕
 */
@Composable
fun TriStateScreen(
    modifier: Modifier = Modifier,
    padding: PaddingValues = PaddingValues(0.dp),
    triState: LoadState,
    onError: @Composable (Throwable) -> Unit,
    onLoading: @Composable () -> Unit = { Loading() },
    onSuccess: @Composable () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .then(modifier),
    ) {
        when (triState) {
            is LoadState.Error -> onError(triState.error)
            LoadState.Loading -> onLoading()
            is LoadState.NotLoading -> onSuccess()
        }
    }
}