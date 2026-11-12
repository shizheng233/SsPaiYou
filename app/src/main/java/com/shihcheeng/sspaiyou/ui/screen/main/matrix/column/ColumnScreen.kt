package com.shihcheeng.sspaiyou.ui.screen.main.matrix.column

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.shihcheeng.sspaiyou.R
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import com.shihcheeng.sspaiyou.ui.compoents.Error
import com.shihcheeng.sspaiyou.ui.compoents.ErrorItem
import com.shihcheeng.sspaiyou.ui.compoents.LoadingItem
import com.shihcheeng.sspaiyou.ui.compoents.tristate.TriStateScreen
import com.shihcheeng.sspaiyou.ui.compoents.tristate.pagingTriState

@Composable
fun ColumnScreen(
    viewModel: ColumnViewModel = androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel(),
    onColumnClick: (Int) -> Unit,
) {

    val data = viewModel.data.collectAsLazyPagingItems()
    val followed by viewModel.followed.collectAsStateWithLifecycle()
    val pending by viewModel.followPending.collectAsStateWithLifecycle()
    val token by viewModel.token.collectAsStateWithLifecycle()
    val followError by viewModel.followError.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(followError) {
        if (followError) {
            Toast.makeText(context, R.string.column_follow_error, Toast.LENGTH_SHORT).show()
            viewModel.clearFollowError()
        }
    }
    LaunchedEffect(data) {
        snapshotFlow { data.loadState.refresh is LoadState.Loading }
            .distinctUntilChanged().collect { if (it) viewModel.clearFollowedOverrides() }
    }
    LaunchedEffect(viewModel, data) {
        viewModel.token.drop(1).collect {
            viewModel.clearFollowedOverrides()
            data.refresh()
        }
    }

    TriStateScreen(
        triState = data.loadState.refresh,
        onError = {
            Error(
                error = it.message
            ) {
                data.refresh()
            }
        }
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(data.itemCount) { index ->
                data[index]?.let {
                    ColumnItem(
                        title = it.title,
                        imageUrl = it.banner,
                        followCount = (it.followCount + when (followed[it.id]) {
                            null, it.followed -> 0
                            true -> 1
                            false -> -1
                        }).coerceAtLeast(0),
                        onCheckedChange = { checked -> viewModel.setFollowed(it.id, checked) },
                        checkValue = followed[it.id] ?: it.followed,
                        followEnabled = !token.isNullOrBlank() && it.id !in pending,
                        onClick = {
                            onColumnClick(it.id)
                        }
                    )
                }
            }
            pagingTriState(
                paging = data.loadState.append,
                onLoading = {
                    LoadingItem()
                },
                onError = {
                    ErrorItem(
                        error = it.localizedMessage
                    ) {
                        data.retry()
                    }
                },
            ) {}
        }
    }

}
