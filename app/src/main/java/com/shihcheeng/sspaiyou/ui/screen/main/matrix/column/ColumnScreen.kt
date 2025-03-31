package com.shihcheeng.sspaiyou.ui.screen.main.matrix.column

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.shihcheeng.sspaiyou.ui.compoents.Error
import com.shihcheeng.sspaiyou.ui.compoents.ErrorItem
import com.shihcheeng.sspaiyou.ui.compoents.LoadingItem
import com.shihcheeng.sspaiyou.ui.compoents.tristate.TriStateScreen
import com.shihcheeng.sspaiyou.ui.compoents.tristate.pagingTriState

@Composable
fun ColumnScreen(
    viewModel: ColumnViewModel = hiltViewModel()
) {

    val data = viewModel.data.collectAsLazyPagingItems()

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
                        followCount = it.followCount,
                        onCheckedChange = {},
                        checkValue = it.followed
                    ) {
                        it.id
                    }
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
            ) {

            }
        }
    }

}