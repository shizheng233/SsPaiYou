package com.shihcheeng.sspaiyou.ui.screen.main.discover.hot

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.shihcheeng.sspaiyou.R
import com.shihcheeng.sspaiyou.ui.compoents.ErrorItem
import com.shihcheeng.sspaiyou.ui.compoents.LoadingItem
import com.shihcheeng.sspaiyou.ui.compoents.tristate.TriStateScreen
import com.shihcheeng.sspaiyou.ui.compoents.tristate.pagingTriState
import com.shihcheeng.sspaiyou.ui.screen.favourite.FavouriteArticleItem
import com.shihcheeng.sspaiyou.ui.compoents.Error as ErrorScreen

@Composable
fun HotScreen(
    modifier: Modifier = Modifier,
    viewModel: HotViewModel = hiltViewModel(),
    onArticleClick: (String) -> Unit,
    onUserClick: (String) -> Unit,
) {
    val articles = viewModel.items.collectAsLazyPagingItems()
    val loadState = articles.loadState

    if (articles.itemCount == 0) {
        Box(modifier = modifier.fillMaxSize()) {
            TriStateScreen(
                triState = loadState.refresh,
                onError = { error ->
                    ErrorScreen(
                        error = error.localizedMessage,
                        onClick = articles::retry,
                    )
                },
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(all = 24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = stringResource(R.string.hot_articles_empty),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                    )
                    TextButton(onClick = articles::refresh) {
                        Text(text = stringResource(R.string.refresh))
                    }
                }
            }
        }
        return
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 16.dp,
            top = 16.dp,
            end = 16.dp,
            bottom = 52.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(space = 16.dp),
    ) {
        pagingTriState(
            paging = loadState.refresh,
            onLoading = { LinearProgressIndicator(modifier = Modifier.fillMaxWidth()) },
            onError = { error ->
                ErrorItem(error = error.localizedMessage, onClick = articles::retry)
            },
            onNotLoading = {},
        )
        // 同一篇文章跨页重复出现时，用列表位置区分两行。
        items(
            count = articles.itemCount,
            key = { index -> "hot_article_${articles.peek(index)?.id}_$index" },
            contentType = { "hot_article" },
        ) { index ->
            articles[index]?.let { article ->
                FavouriteArticleItem(
                    article = article,
                    onClick = {
                        val detailId = if (!article.free && article.slug.isNotBlank()) {
                            article.slug
                        } else {
                            article.id.toString()
                        }
                        onArticleClick(detailId)
                    },
                    onUserClick = onUserClick,
                )
            }
        }
        pagingTriState(
            paging = loadState.append,
            onLoading = { LoadingItem() },
            onError = { error ->
                ErrorItem(error = error.localizedMessage, onClick = articles::retry)
            },
            onNotLoading = {},
        )
    }
}
