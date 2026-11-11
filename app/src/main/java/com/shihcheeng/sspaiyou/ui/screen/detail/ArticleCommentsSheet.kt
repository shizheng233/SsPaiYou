package com.shihcheeng.sspaiyou.ui.screen.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import com.shihcheeng.sspaiyou.R
import com.shihcheeng.sspaiyou.api.data.actrcle.comment.ArticleComment
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ArticleCommentsSheet(
    comments: LazyPagingItems<ArticleComment>,
    commentCount: Int,
    onDismiss: () -> Unit,
    onAvatarClick: (String) -> Unit,
    onImageClick: ((String) -> Unit)? = null
) {
    val sheetState = rememberBottomSheetState(
        initialValue = SheetValue.Hidden,
        enabledValues = setOf(SheetValue.Hidden, SheetValue.Expanded)
    )
    val scope = rememberCoroutineScope()
    val dismiss: () -> Unit = {
        scope.launch {
            sheetState.hide()
            onDismiss()
        }
    }
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surfaceContainer
    ) {
        Column(Modifier.fillMaxWidth().fillMaxHeight(0.8f)) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(start = 20.dp, end = 8.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.article_comments_title, commentCount),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = comments::refresh, enabled = comments.loadState.refresh !is LoadState.Loading) {
                    Icon(
                        painterResource(R.drawable.column_refresh_24),
                        contentDescription = stringResource(R.string.article_comments_refresh)
                    )
                }
                IconButton(onClick = dismiss) {
                    Icon(
                        painterResource(R.drawable.column_close_24),
                        contentDescription = stringResource(R.string.article_comments_close)
                    )
                }
            }
            val refresh = comments.loadState.refresh
            when {
                comments.itemCount == 0 && refresh is LoadState.Loading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { LoadingIndicator() }
                }
                comments.itemCount == 0 && refresh is LoadState.Error -> {
                    CommentLoadError(onRetry = comments::retry, modifier = Modifier.fillMaxSize())
                }
                comments.itemCount == 0 -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            stringResource(R.string.article_comments_empty),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
                else -> LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (refresh is LoadState.Error) {
                        item(key = "refresh_error", contentType = "status") {
                            CommentLoadError(onRetry = comments::retry)
                        }
                    }
                    items(
                        count = comments.itemCount,
                        key = comments.itemKey { it.id },
                        contentType = { "comment" }
                    ) { index ->
                        comments[index]?.let { comment ->
                            ArticleCommentItem(
                                comment = comment,
                                onAvatarClick = onAvatarClick,
                                onImageClick = onImageClick
                            )
                        }
                    }
                    when (comments.loadState.append) {
                        is LoadState.Loading -> item(key = "append_loading", contentType = "status") {
                            Box(Modifier.fillMaxWidth().padding(8.dp), contentAlignment = Alignment.Center) {
                                LoadingIndicator()
                            }
                        }
                        is LoadState.Error -> item(key = "append_error", contentType = "status") {
                            CommentLoadError(onRetry = comments::retry)
                        }
                        else -> Unit
                    }
                }
            }
        }
    }
}

@Composable
private fun CommentLoadError(onRetry: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically)
    ) {
        Text(
            text = stringResource(R.string.article_comments_error),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        TextButton(onClick = onRetry) { Text(stringResource(R.string.retry)) }
    }
}
