package com.shihcheeng.sspaiyou.ui.screen.main.matrix.hot

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.compose.collectAsLazyPagingItems
import com.shihcheeng.sspaiyou.ui.compoents.ErrorItem
import com.shihcheeng.sspaiyou.ui.compoents.LoadingItem
import com.shihcheeng.sspaiyou.ui.compoents.tristate.TriStateScreen
import com.shihcheeng.sspaiyou.ui.compoents.tristate.pagingTriState
import com.shihcheeng.sspaiyou.ui.screen.main.matrix.MatrixItem
import com.shihcheeng.sspaiyou.ui.compoents.Error as ErrorScreen

@Composable
fun HotScreen(
    viewModel: MatrixHotViewModel = androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel(),
    onClick: (String) -> Unit
) {
    val data = viewModel.data.collectAsLazyPagingItems()

    TriStateScreen(
        triState = data.loadState.refresh,
        onError = {
            ErrorScreen(
                error = it.message
            ) {
                data.refresh()
            }
        },
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(all = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(data.itemCount) { ii ->
                data[ii]?.let {
                    MatrixItem(
                        title = it.title,
                        author = it.author.nickname,
                        avatarUrl = it.author.avatar,
                        flash = it.likeCount.toString(),
                        message = it.commentCount.toString(),
                        imageUrl = it.banner
                    ) {
                        onClick(it.id.toString())
                    }
                }
            }
            pagingTriState(
                paging = data.loadState.append,
                onError = {
                    ErrorItem(error = it.localizedMessage) {
                        data.retry()
                    }
                },
                onLoading = {
                    LoadingItem()
                }
            ) {

            }
        }
    }


}