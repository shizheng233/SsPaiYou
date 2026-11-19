package com.shihcheeng.sspaiyou.ui.screen.column

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.shihcheeng.sspaiyou.R
import com.shihcheeng.sspaiyou.api.data.column.ColumnArticle
import com.shihcheeng.sspaiyou.ui.compoents.Error
import com.shihcheeng.sspaiyou.ui.compoents.ErrorItem
import com.shihcheeng.sspaiyou.utils.NetState
import kotlinx.coroutines.launch

@Composable
fun ColumnScreen(
    viewModel: ColumnViewModel = hiltViewModel(),
    onArticleClick: (String) -> Unit,
    onUserClick: (String) -> Unit,
    onNavUp: () -> Unit
) {
    val info by viewModel.info.collectAsStateWithLifecycle()
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
    val articles = viewModel.articles.collectAsLazyPagingItems()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = drawerState.isOpen,
        drawerContent = {
            ModalDrawerSheet(
                drawerState = drawerState,
                drawerContainerColor = MaterialTheme.colorScheme.surfaceContainerLow
            ) {
                ColumnInfoDrawer(
                    info = info,
                    followPending = (info as? NetState.Success)?.content?.id in pending,
                    followEnabled = !token.isNullOrBlank(),
                    onFollowChange = viewModel::setFollowed,
                    onRetry = viewModel::load,
                    onClose = { scope.launch { drawerState.close() } },
                    onUserClick = { slug ->
                        scope.launch {
                            drawerState.close()
                            onUserClick(slug)
                        }
                    }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = (info as? NetState.Success)?.content?.title
                                ?.takeIf { it.isNotBlank() } ?: stringResource(R.string.column),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavUp) {
                            Icon(
                                painterResource(R.drawable.baseline_arrow_back_24),
                                contentDescription = stringResource(R.string.back_to_up)
                            )
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = {
                                articles.refresh()
                                viewModel.load()
                            },
                            enabled = articles.loadState.refresh !is LoadState.Loading
                        ) {
                            Icon(
                                painterResource(R.drawable.column_refresh_24),
                                contentDescription = stringResource(R.string.column_refresh)
                            )
                        }
                        FilledTonalIconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(
                                painterResource(R.drawable.column_info_24),
                                contentDescription = stringResource(R.string.column_info)
                            )
                        }
                    }
                )
            }
        ) { padding ->
            ColumnArticleList(
                articles = articles,
                onArticleClick = onArticleClick,
                onUserClick = onUserClick,
                contentPadding = padding,
                modifier = Modifier
                    .fillMaxSize()
            )
        }
    }
}

@Composable
private fun ColumnArticleList(
    articles: LazyPagingItems<ColumnArticle>,
    contentPadding: PaddingValues,
    onArticleClick: (String) -> Unit,
    onUserClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val refresh = articles.loadState.refresh
    Box(modifier) {
        when (articles.itemCount) {
            0 if refresh is LoadState.Loading -> ColumnLoading(Modifier.fillMaxSize())
            0 if refresh is LoadState.Error -> Error(
                error = refresh.error.localizedMessage,
                onClick = articles::retry
            )

            0 -> Text(
                text = stringResource(R.string.column_articles_empty),
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(24.dp),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            else -> LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    top = contentPadding.calculateTopPadding() + 16.dp,
                    bottom = contentPadding.calculateBottomPadding() + 32.dp
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (refresh is LoadState.Loading) {
                    item(key = "refresh_loading", contentType = "loading") { ColumnLoading() }
                } else if (refresh is LoadState.Error) {
                    item(key = "refresh_error", contentType = "error") {
                        ErrorItem(error = refresh.error.localizedMessage, onClick = articles::retry)
                    }
                }
                items(
                    count = articles.itemCount,
                    key = articles.itemKey { "article_${it.id}" },
                    contentType = { "article" }
                ) { index ->
                    articles[index]?.let { article ->
                        ColumnArticleItem(
                            article = article,
                            onClick = {
                                onArticleClick(
                                    if (!article.free && article.slug.isNotBlank()) article.slug
                                    else article.id.toString()
                                )
                            },
                            onUserClick = onUserClick
                        )
                    }
                }
                when (val append = articles.loadState.append) {
                    is LoadState.Loading -> item(key = "append_loading", contentType = "loading") {
                        ColumnLoading()
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

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun ColumnLoading(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        LoadingIndicator()
    }
}
