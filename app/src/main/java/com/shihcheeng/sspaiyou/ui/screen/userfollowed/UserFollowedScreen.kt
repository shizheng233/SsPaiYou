package com.shihcheeng.sspaiyou.ui.screen.userfollowed

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.shihcheeng.sspaiyou.R
import com.shihcheeng.sspaiyou.api.data.column.ColumnUser
import com.shihcheeng.sspaiyou.ui.compoents.Error
import com.shihcheeng.sspaiyou.ui.compoents.ErrorItem
import com.shihcheeng.sspaiyou.ui.compoents.Loading
import com.shihcheeng.sspaiyou.ui.compoents.LoadingItem

@Composable
fun UserFollowedScreen(
    viewModel: UserFollowedViewModel = hiltViewModel(),
    onUserClick: (String) -> Unit,
    onBack: () -> Unit
) {
    val users = viewModel.users.collectAsLazyPagingItems()
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(R.string.followed_user))
                },
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
        UserFollowedList(
            users = users,
            onUserClick = onUserClick,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .consumeWindowInsets(padding)
        )
    }
}

@Composable
internal fun UserFollowedList(
    users: LazyPagingItems<ColumnUser>,
    onUserClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val refresh = users.loadState.refresh
    Box(modifier) {
        when (users.itemCount) {
            0 if refresh is LoadState.Loading -> Loading()
            0 if refresh is LoadState.Error -> Error(
                error = refresh.error.localizedMessage,
                onClick = users::retry
            )
            0 -> Text(
                text = stringResource(R.string.user_followed_empty),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.Center).padding(24.dp)
            )
            else -> LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp)
            ) {
                when (refresh) {
                    is LoadState.Loading -> item(key = "refresh_loading", contentType = "loading") {
                        LoadingItem(Modifier.padding(16.dp))
                    }

                    is LoadState.Error -> item(key = "refresh_error", contentType = "error") {
                        ErrorItem(error = refresh.error.localizedMessage, onClick = users::retry)
                    }

                    is LoadState.NotLoading -> Unit
                }
                items(
                    count = users.itemCount,
                    key = users.itemKey { "followed_user_${it.slug}" },
                    contentType = { "followed_user" }
                ) { index ->
                    users[index]?.let { user ->
                        FollowedUserListItem(
                            user = user,
                            first = index == 0,
                            last = index == users.itemCount - 1,
                            onClick = { onUserClick(user.slug) },
                            modifier = Modifier.padding(
                                bottom = if (index == users.itemCount - 1) 0.dp else 2.dp
                            )
                        )
                    }
                }
                when (val append = users.loadState.append) {
                    is LoadState.Loading -> item(key = "append_loading", contentType = "loading") {
                        LoadingItem(Modifier.padding(16.dp))
                    }

                    is LoadState.Error -> item(key = "append_error", contentType = "error") {
                        ErrorItem(
                            error = append.error.localizedMessage,
                            onClick = users::retry,
                            modifier = Modifier.padding(16.dp)
                        )
                    }

                    is LoadState.NotLoading -> Unit
                }
            }
        }
    }
}
