package com.shihcheeng.sspaiyou.ui.screen.main.matrix.hot

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.shihcheeng.sspaiyou.ui.screen.main.matrix.MatrixItem

@Composable
fun HotScreen(
    viewModel: MatrixHotViewModel = hiltViewModel(),
    onClick: (String) -> Unit
) {
    val data = viewModel.data.collectAsLazyPagingItems()

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
    }
}