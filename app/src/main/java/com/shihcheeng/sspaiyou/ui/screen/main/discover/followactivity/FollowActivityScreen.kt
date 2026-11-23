package com.shihcheeng.sspaiyou.ui.screen.main.discover.followactivity

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import com.shihcheeng.sspaiyou.R
import com.shihcheeng.sspaiyou.ui.compoents.ErrorItem
import com.shihcheeng.sspaiyou.ui.compoents.LoadingItem
import com.shihcheeng.sspaiyou.ui.compoents.tristate.TriStateScreen
import com.shihcheeng.sspaiyou.ui.compoents.tristate.pagingTriState
import com.shihcheeng.sspaiyou.ui.screen.user.activityitems.UserActivityItem
import com.shihcheeng.sspaiyou.ui.screen.user.activityitems.UserActivityNormalItem
import com.shihcheeng.sspaiyou.utils.extra.click
import com.shihcheeng.sspaiyou.ui.compoents.Error as ErrorScreen

@Composable
fun FollowActivityScreen(
    viewModel: FollowActivityViewModel = hiltViewModel(),
    onArticleId: (String) -> Unit,
    onUserClick: (String) -> Unit
) {
    val items = viewModel.items.collectAsLazyPagingItems()
    val token by viewModel.token.collectAsStateWithLifecycle()
    val loadState = items.loadState

    if (token == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.no_login),
                style = MaterialTheme.typography.bodyMedium
            )
        }
        return
    }

    if (items.itemCount == 0) {
        TriStateScreen(
            triState = loadState.refresh,
            onError = { error ->
                ErrorScreen(error = error.localizedMessage, onClick = items::retry)
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
                    text = stringResource(R.string.follow_activity_empty),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
                TextButton(onClick = items::refresh) {
                    Text(text = stringResource(R.string.refresh))
                }
            }
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            bottom = 52.dp,
            top = 16.dp,
            start = 16.dp,
            end = 16.dp
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        pagingTriState(
            paging = loadState.refresh,
            onLoading = { LoadingItem() },
            onError = { error ->
                ErrorItem(error = error.localizedMessage, onClick = items::retry)
            },
            onNotLoading = {},
        )
        // 接口没有动态 ID，同一篇文章可以对应多条同类型动态。
        items(
            count = items.itemCount,
            contentType = items.itemContentType { item -> item.key },
        ) { index ->
            items[index]?.let { item ->
                UserActivityItem(
                    imageUrl = item.author.avatar,
                    actionString = item.action,
                    modifier = Modifier.click(
                        onClick = {
                            onUserClick(item.author.slug)
                        }
                    )
                ) {
                    UserActivityNormalItem(
                        title = item.article.title,
                        banner = item.article.banner,
                    ) {
                        onArticleId(item.article.id.toString())
                    }
                }
            }
        }
        pagingTriState(
            paging = loadState.append,
            onLoading = { LoadingItem() },
            onError = { error ->
                ErrorItem(error = error.localizedMessage, onClick = items::retry)
            },
            onNotLoading = {},
        )
    }
}
