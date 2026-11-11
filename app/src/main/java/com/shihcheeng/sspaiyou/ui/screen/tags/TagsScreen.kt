package com.shihcheeng.sspaiyou.ui.screen.tags

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.shihcheeng.sspaiyou.R
import com.shihcheeng.sspaiyou.api.data.column.ColumnArticle
import com.shihcheeng.sspaiyou.ui.compoents.Error
import com.shihcheeng.sspaiyou.ui.compoents.ErrorItem

@Composable
fun TagsScreen(
    tagName: String,
    viewModel: TagsViewModel = hiltViewModel(),
    onArticleClick: (String) -> Unit,
    onNavUp: () -> Unit
) {
    val articles = viewModel.articles.collectAsLazyPagingItems()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = tagName,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavUp) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_arrow_back_24),
                            contentDescription = stringResource(R.string.back_to_up)
                        )
                    }
                }
            )
        }
    ) { padding ->
        TagArticleList(
            articles = articles,
            onArticleClick = onArticleClick,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        )
    }
}

@Composable
private fun TagArticleList(
    articles: LazyPagingItems<ColumnArticle>,
    onArticleClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val refresh = articles.loadState.refresh
    Box(modifier) {
        when {
            articles.itemCount == 0 && refresh is LoadState.Loading ->
                CircularProgressIndicator(Modifier.align(Alignment.Center))

            articles.itemCount == 0 && refresh is LoadState.Error ->
                Error(error = refresh.error.localizedMessage, onClick = articles::retry)

            articles.itemCount == 0 ->
                Text(
                    text = "该标签下暂无文章",
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(24.dp),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

            else -> LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (refresh is LoadState.Error) {
                    item(key = "refresh_error", contentType = "error") {
                        ErrorItem(error = refresh.error.localizedMessage, onClick = articles::retry)
                    }
                }
                items(
                    count = articles.itemCount,
                    key = articles.itemKey { "tag_article_${it.id}" },
                    contentType = { "tag_article" }
                ) { index ->
                    articles[index]?.let { article ->
                        TagArticleItem(
                            article = article,
                            onClick = {
                                onArticleClick(
                                    if (!article.free && article.slug.isNotBlank()) article.slug
                                    else article.id.toString()
                                )
                            }
                        )
                    }
                }
                when (val append = articles.loadState.append) {
                    is LoadState.Loading -> item(key = "append_loading", contentType = "loading") {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    is LoadState.Error -> item(key = "append_error", contentType = "error") {
                        ErrorItem(error = append.error.localizedMessage, onClick = articles::retry)
                    }

                    is LoadState.NotLoading -> Unit
                }
            }
        }
    }
}
