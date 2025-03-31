package com.shihcheeng.sspaiyou.ui.compoents.tristate

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.paging.LoadState

@Composable
fun PagingTriState(
    modifier: Modifier = Modifier,
    paging: LoadState,
    onLoading: @Composable () -> Unit,
    onError: @Composable () -> Unit,
    onNotLoading: @Composable () -> Unit
) {
    Box(modifier = Modifier.then(modifier)) {
        when (paging) {
            is LoadState.Error -> onError()
            LoadState.Loading -> onLoading()
            is LoadState.NotLoading -> onNotLoading()
        }
    }
}


fun LazyListScope.pagingTriState(
    paging: LoadState,
    onLoading: @Composable () -> Unit,
    onError: @Composable (Throwable) -> Unit,
    onNotLoading: () -> Unit
) {

    when (paging) {
        is LoadState.Error -> item {
            onError(paging.error)
        }

        LoadState.Loading -> item {
            onLoading()
        }

        is LoadState.NotLoading -> onNotLoading()
    }
}

fun LazyGridScope.pagingTriState(
    paging: LoadState,
    onLoading: @Composable () -> Unit,
    onError: @Composable (Throwable) -> Unit,
    onNotLoading: () -> Unit
) {

    when (paging) {
        is LoadState.Error -> item(
            span = { GridItemSpan(1) }
        ) {
            onError(paging.error)
        }

        LoadState.Loading -> item(
            span = { GridItemSpan(1) }
        ) {
            onLoading()
        }

        is LoadState.NotLoading -> onNotLoading()
    }
}