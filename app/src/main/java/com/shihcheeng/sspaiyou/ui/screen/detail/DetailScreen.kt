package com.shihcheeng.sspaiyou.ui.screen.detail

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import com.shihcheeng.sspaiyou.R
import com.shihcheeng.sspaiyou.activities.detail.DetailViewModel
import com.shihcheeng.sspaiyou.ui.compoents.Error
import com.shihcheeng.sspaiyou.ui.compoents.tristate.TriStateScreen
import com.shihcheeng.sspaiyou.utils.NetState
import kotlinx.coroutines.launch

@Composable
fun DetailScreen(
    viewModel: DetailViewModel = androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel<DetailViewModel>(),
    onAvatarClick: (String) -> Unit,
    onNavBack: () -> Unit
) {
    val data by viewModel.data.collectAsStateWithLifecycle()
    val token by viewModel.token.collectAsStateWithLifecycle()
    val isFavouriteUpdating by viewModel.isFavouriteUpdating.collectAsStateWithLifecycle()
    val favouriteError by viewModel.favouriteError.collectAsStateWithLifecycle()
    val isLikeUpdating by viewModel.isLikeUpdating.collectAsStateWithLifecycle()
    val likeError by viewModel.likeError.collectAsStateWithLifecycle()
    val contentBlocks by viewModel.contentBlocks.collectAsStateWithLifecycle()
    val bodyExtendsBodys by viewModel.bodyExtendsBlocks.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val state = rememberLazyListState()
    var showComments by rememberSaveable { mutableStateOf(false) }
    val article = (data as? NetState.Success)?.content
    val context = LocalContext.current
    val shareTitle = stringResource(R.string.share_text)
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val favouriteErrorMessage = stringResource(R.string.article_favourite_error)
    LaunchedEffect(favouriteError) {
        if (favouriteError) {
            snackbarHostState.showSnackbar(favouriteErrorMessage)
            viewModel.clearFavouriteError()
        }
    }
    val likeErrorMessage = when (likeError) {
        DetailViewModel.LikeError.NOT_LOGGED_IN -> stringResource(R.string.no_login)
        DetailViewModel.LikeError.REQUEST_FAILED -> stringResource(R.string.article_like_error)
        null -> null
    }
    LaunchedEffect(likeError) {
        if (likeErrorMessage != null) {
            snackbarHostState.showSnackbar(likeErrorMessage)
            viewModel.clearLikeError()
        }
    }
    val tocEntries = remember(article?.title, contentBlocks, bodyExtendsBodys) {
        buildDetailToc(article?.title.orEmpty(), contentBlocks, bodyExtendsBodys)
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = drawerState.isOpen,
        drawerContent = {
            ModalDrawerSheet(
                drawerState = drawerState,
                drawerContainerColor = MaterialTheme.colorScheme.surfaceContainerLow
            ) {
                DetailTocDrawer(
                    entries = tocEntries,
                    articleListState = state,
                    onEntryClick = { entry ->
                        scope.launch {
                            drawerState.close()
                            state.scrollToItem(entry.itemIndex)
                        }
                    }
                )
            }
        }
    ) {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                TopAppBar(
                    title = {
                        val showTitle by remember { derivedStateOf { state.firstVisibleItemIndex > 0 } }
                        AnimatedVisibility(
                            visible = showTitle && data is NetState.Success,
                            enter = fadeIn(),
                            exit = fadeOut()
                        ) {
                            Text(
                                text = (data as NetState.Success).content.title,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    },
                    scrollBehavior = scrollBehavior,
                    navigationIcon = {
                        IconButton(
                            onClick = onNavBack
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.baseline_arrow_back_24),
                                contentDescription = stringResource(R.string.back_to_up)
                            )
                        }
                    }
                )
            },
            bottomBar = {
                if (article != null && article.id > 0) {
                    DetailBottomBar(
                        commentCount = article.articleCount.commentCount,
                        onViewComments = { showComments = true },
                        modifier = Modifier.fillMaxWidth(),
                        onTocClick = { scope.launch { drawerState.open() } },
                        onShareCommit = {
                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, detailArticleUrl(viewModel.id, article.id))
                            }
                            context.startActivity(Intent.createChooser(intent, shareTitle))
                        },
                        onBookmarkClick = viewModel::toggleFavourite,
                        isBookmarked = article.favorited,
                        bookmarkEnabled = !token.isNullOrBlank() && !isFavouriteUpdating,
                        likeCount = article.articleCount.likeCount,
                        onLikeClick = viewModel::toggleLike,
                        isLike = article.liked,
                        likeEnabled = !isLikeUpdating
                    )
                }
            }
        ) { padding ->
            TriStateScreen(
                triState = data,
                padding = PaddingValues(
                    top = padding.calculateTopPadding(),
                    bottom = 0.dp
                ),
                onError = {
                    Error(error = it.localizedMessage, onClick = viewModel::load)
                }
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    DetailScreenContent(
                        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                        state = state,
                        title = it.title,
                        author = it.author,
                        blocks = contentBlocks,
                        extendsBodys = bodyExtendsBodys,
                        onAvatarClick = onAvatarClick,
                        bottomPadding = padding.calculateBottomPadding()
                    )
                }
            }
        }
    }
    if (showComments && article != null && article.id > 0) {
        val comments = viewModel.comments.collectAsLazyPagingItems()
        ArticleCommentsSheet(
            comments = comments,
            commentCount = article.articleCount.commentCount,
            onDismiss = { showComments = false },
            onAvatarClick = {
                showComments = false
                onAvatarClick(it)
            }
        )
    }
}
