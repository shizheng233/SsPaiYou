package com.shihcheeng.sspaiyou.ui.screen.notity

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.shihcheeng.sspaiyou.R
import com.shihcheeng.sspaiyou.ui.compoents.Error
import com.shihcheeng.sspaiyou.ui.compoents.ErrorItem

@Composable
fun NotifyScreen(
    viewModel: NotifyViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onArticleClick: (String) -> Unit
) {
    val token by viewModel.token.collectAsStateWithLifecycle()
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
                ),
                title = {
                    Text(stringResource(R.string.notification_title))
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_arrow_back_24),
                            contentDescription = stringResource(R.string.back_to_up)
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .consumeWindowInsets(padding)
        ) {
            if (token.isNullOrBlank()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(stringResource(R.string.no_login))
                }
            } else {
                PrimaryTabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
                ) {
                    listOf(
                        R.string.notification_online,
                        R.string.notification_comment
                    ).forEachIndexed { index, label ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = { Text(stringResource(label)) })
                    }
                }
                if (selectedTab == 0) {
                    val items = viewModel.online.collectAsLazyPagingItems()
                    NotificationList(
                        items = items
                    ) { notification ->
                        NotificationItem(
                            icon = notification.icon,
                            avatar = notification.user?.avatar.orEmpty(),
                            nickname = notification.user?.nickname.orEmpty(),
                            title = notification.title,
                            body = notification.comment,
                            target = notification.articleTitle.ifBlank {
                                notification.communityTopicTitle.ifBlank {
                                    notification.bulletTitle.ifBlank { notification.articleBodyExtendTitle }
                                }
                            },
                            targetImage = when {
                                notification.articleTitle.isNotBlank() -> notification.articleBanner
                                notification.bulletTitle.isNotBlank() -> notification.bulletBanner
                                else -> ""
                            },
                            createdAt = notification.createdAt,
                            isRead = notification.isRead,
                            onClick = {
                                viewModel.markRead(notification) {
                                    articleTarget(
                                        notification.articleId,
                                        notification.articleSlug,
                                        notification.articleBelongToMember
                                    )?.let(onArticleClick)
                                }
                            }
                        )
                    }
                } else {
                    val items = viewModel.comments.collectAsLazyPagingItems()
                    NotificationList(
                        itemKey = { it.id },
                        items = items
                    ) { notification ->
                        val detail = notification.data
                        val title = notification.title.replace("{{data.nickname}}", detail.nickname)
                            .replace("{{data.slug}}", detail.slug)
                        NotificationItem(
                            icon = notification.icon,
                            avatar = detail.avatar,
                            nickname = detail.nickname,
                            title = title.ifBlank { notification.action },
                            body = detail.commentReplyReply.ifBlank { detail.commentReply.ifBlank { detail.comment } },
                            target = detail.articleTitle.ifBlank { detail.communityTopicTitle.ifBlank { detail.bulletTitle } },
                            targetImage = when {
                                detail.articleTitle.isNotBlank() -> detail.articleBanner
                                detail.bulletTitle.isNotBlank() -> detail.bulletBanner
                                else -> ""
                            },
                            createdAt = notification.createdAt,
                            isRead = notification.isRead,
                            onClick = {
                                viewModel.markRead(notification) {
                                    articleTarget(
                                        detail.articleId,
                                        detail.articleSlug,
                                        notification.articleBelongToMember
                                    )?.let(onArticleClick)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

private fun articleTarget(id: Int, slug: String, member: Boolean): String? =
    if (member) slug.takeIf { it.isNotBlank() } else id.takeIf { it > 0 }?.toString()

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun <T : Any> NotificationList(
    items: LazyPagingItems<T>,
    itemKey: ((T) -> Any)? = null,
    row: @Composable (T) -> Unit
) {
    val refresh = items.loadState.refresh
    Box(Modifier.fillMaxSize()) {
        when (items.itemCount) {
            0 if refresh is LoadState.Loading -> LoadingIndicator(Modifier.align(Alignment.Center))
            0 if refresh is LoadState.Error -> Error(
                error = refresh.error.localizedMessage,
                onClick = items::retry
            )

            0 -> Text(
                text = stringResource(R.string.notification_empty),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.Center)
            )

            else -> LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (refresh is LoadState.Error) {
                    item {
                        ErrorItem(
                            error = refresh.error.localizedMessage,
                            onClick = items::retry
                        )
                    }
                }
                items(
                    count = items.itemCount,
                    key = itemKey?.let { items.itemKey(it) },
                    contentType = { "notification" }
                ) { index ->
                    items[index]?.let { row(it) }
                }
                when (val append = items.loadState.append) {
                    is LoadState.Loading -> item {
                        Box(Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                            LoadingIndicator()
                        }
                    }
                    is LoadState.Error -> item {
                        ErrorItem(
                            error = append.error.localizedMessage,
                            onClick = items::retry
                        )
                    }

                    is LoadState.NotLoading -> Unit
                }
            }
        }
    }
}
