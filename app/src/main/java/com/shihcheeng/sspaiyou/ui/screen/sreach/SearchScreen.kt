package com.shihcheeng.sspaiyou.ui.screen.sreach

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.AppBarWithSearch
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExpandedFullScreenSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarDefaults.appBarWithSearchColors
import androidx.compose.material3.SearchBarValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.shihcheeng.sspaiyou.R
import com.shihcheeng.sspaiyou.api.data.column.ColumnArticle
import com.shihcheeng.sspaiyou.database.datamodel.LocalSearchRecordDataModel
import com.shihcheeng.sspaiyou.ui.compoents.Error
import com.shihcheeng.sspaiyou.ui.compoents.ErrorItem
import com.shihcheeng.sspaiyou.ui.compoents.LoadingItem
import com.shihcheeng.sspaiyou.ui.compoents.WaitingSearchImage
import com.shihcheeng.sspaiyou.ui.screen.main.matrix.MatrixItem
import com.shihcheeng.sspaiyou.utils.icons.ArrowBackIos
import com.shihcheeng.sspaiyou.utils.icons.ArrowLeftUpLine
import com.shihcheeng.sspaiyou.utils.icons.SSPaiPack
import com.shihcheeng.sspaiyou.utils.icons.SavedSearch
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = hiltViewModel(),
    onClick: (String) -> Unit,
    onBack: () -> Unit
) {
    val data = viewModel.data.collectAsLazyPagingItems()
    val request by viewModel.searchRequest.collectAsStateWithLifecycle()
    val history by viewModel.searchHistory.collectAsStateWithLifecycle()
    val textFieldState = rememberTextFieldState(initialText = request.title)
    val state = rememberSearchBarState()
    val scrollBehavior = SearchBarDefaults.enterAlwaysSearchBarScrollBehavior()
    val scope = rememberCoroutineScope()
    val colors = appBarWithSearchColors()
    val inputField =
        @Composable {
            SearchBarDefaults.InputField(
                textFieldState = textFieldState,
                searchBarState = state,
                colors = colors.searchBarColors.inputFieldColors,
                onSearch = {
                    viewModel.search(it)
                    scope.launch { state.animateToCollapsed() }
                },
                placeholder = {
                    Text(stringResource(R.string.search_articles))
                },
                leadingIcon = {
                    if (state.currentValue == SearchBarValue.Expanded) {
                        IconButton(onClick = { scope.launch { state.animateToCollapsed() } }) {
                            Icon(
                                imageVector = SSPaiPack.ArrowBackIos,
                                contentDescription = stringResource(R.string.search_collapse)
                            )
                        }
                    }
                },
                trailingIcon = {
                    if (textFieldState.text.isNotEmpty()) {
                        IconButton(onClick = {
                            textFieldState.clearText()
                            viewModel.search("")
                        }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = stringResource(R.string.search_clear)
                            )
                        }
                    }
                }
            )
        }
    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            AppBarWithSearch(
                state = state,
                inputField = inputField,
                scrollBehavior = scrollBehavior,
                colors = colors,
                navigationIcon = {
                    IconButton(
                        onClick = onBack
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_arrow_back_24),
                            contentDescription = stringResource(R.string.back_to_up)
                        )
                    }
                }
            )
        }
    ) { padding ->
        key(request.id) {
            SearchResults(
                hasSearch = request.title.isNotEmpty(),
                articles = data,
                onClick = onClick,
                paddingValues = padding,
                modifier = Modifier
                    .fillMaxSize()
            )
        }
    }

    ExpandedFullScreenSearchBar(
        state = state,
        inputField = inputField,
        colors = colors.searchBarColors
    ) {
        key(request.id) {
            SearchHistory(
                history = history,
                onClick = { keyword ->
                    textFieldState.setTextAndPlaceCursorAtEnd(keyword)
                    viewModel.search(keyword)
                    scope.launch { state.animateToCollapsed() }
                }
            )
        }
    }
}

@Composable
private fun SearchHistory(
    history: List<LocalSearchRecordDataModel>,
    modifier: Modifier = Modifier,
    onClick: (String) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .then(modifier),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        if (history.isEmpty()) {
            item {
                ListItem(
                    content = {
                        Text(stringResource(R.string.search_history_empty))
                    },
                )
            }
        } else {
            items(
                items = history,
                key = { it.text }
            ) { record ->
                ListItem(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onClick(record.text) },
                    leadingContent = {
                        Icon(
                            imageVector = SSPaiPack.SavedSearch,
                            contentDescription = stringResource(R.string.search_history_record)
                        )
                    },
                    trailingContent = {
                        IconButton(onClick = { onClick(record.text) }) {
                            Icon(
                                imageVector = SSPaiPack.ArrowLeftUpLine,
                                contentDescription = stringResource(R.string.search_history_put)
                            )
                        }
                    },
                    colors = ListItemDefaults.colors(),
                    content = {
                        Text(record.text)
                    },
                )
            }
        }
    }
}

@Composable
private fun SearchResults(
    hasSearch: Boolean,
    articles: LazyPagingItems<ColumnArticle>,
    paddingValues: PaddingValues,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val refresh = articles.loadState.refresh
    Box(
        modifier = modifier.padding(paddingValues),
        contentAlignment = Alignment.Center
    ) {
        when {
            !hasSearch -> Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                WaitingSearchImage(
                    modifier = Modifier
                        .widthIn(max = 320.dp)
                        .fillMaxWidth()
                        .heightIn(max = 240.dp)
                )
                Text(
                    text = stringResource(R.string.search_waiting),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }

            articles.itemCount == 0 && refresh is LoadState.Loading ->
                CircularProgressIndicator()

            articles.itemCount == 0 && refresh is LoadState.Error ->
                Error(error = stringResource(R.string.search_error), onClick = articles::retry)

            articles.itemCount == 0 -> Text(
                text = stringResource(R.string.search_empty),
                modifier = Modifier.padding(24.dp),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            else -> LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                when (refresh) {
                    is LoadState.Loading -> item(key = "refresh_loading", contentType = "loading") {
                        LoadingItem(Modifier.padding(16.dp))
                    }

                    is LoadState.Error -> item(key = "refresh_error", contentType = "error") {
                        ErrorItem(
                            error = stringResource(R.string.search_error),
                            onClick = articles::retry
                        )
                    }

                    is LoadState.NotLoading -> Unit
                }
                items(
                    count = articles.itemCount,
                    key = articles.itemKey { "search_article_${it.id}" },
                    contentType = { "search_article" }
                ) { index ->
                    articles[index]?.let { article ->
                        MatrixItem(
                            title = article.title,
                            imageUrl = article.banner,
                            avatarUrl = article.author.avatar,
                            author = article.author.nickname,
                            flash = article.likeCount.toString(),
                            message = article.commentCount.toString()
                        ) {
                            onClick(
                                if (!article.free && article.slug.isNotBlank()) article.slug
                                else article.id.toString()
                            )
                        }
                    }
                }
                when (articles.loadState.append) {
                    is LoadState.Loading -> item(key = "append_loading", contentType = "loading") {
                        LoadingItem(Modifier.padding(16.dp))
                    }

                    is LoadState.Error -> item(key = "append_error", contentType = "error") {
                        ErrorItem(
                            error = stringResource(R.string.search_error),
                            onClick = articles::retry
                        )
                    }

                    is LoadState.NotLoading -> Unit
                }
            }
        }
    }
}
