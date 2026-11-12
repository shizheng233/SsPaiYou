package com.shihcheeng.sspaiyou.ui.screen.detail

import android.content.Intent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import com.shihcheeng.sspaiyou.R
import com.shihcheeng.sspaiyou.activities.detail.DetailViewModel
import com.shihcheeng.sspaiyou.ui.compoents.Error
import com.shihcheeng.sspaiyou.ui.compoents.tristate.TriStateScreen
import com.shihcheeng.sspaiyou.utils.LocalComposeSetting
import com.shihcheeng.sspaiyou.utils.NetState
import com.shihcheeng.sspaiyou.utils.settingdata.SettingData
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
@OptIn(FlowPreview::class)
fun DetailScreen(
    viewModel: DetailViewModel = hiltViewModel<DetailViewModel>(),
    onAvatarClick: (String) -> Unit,
    onLinkClick: (String) -> Unit,
    onTagClick: (String) -> Unit = {},
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
    var selectedImageUrl by rememberSaveable(viewModel.id) { mutableStateOf<String?>(null) }
    val article = (data as? NetState.Success)?.content
    var restoredArticleId by remember(article?.id) { mutableIntStateOf(-1) }
    val context = LocalContext.current
    val setting = LocalComposeSetting.current
    val shareTitle = stringResource(R.string.share_text)
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val favouriteErrorMessage = stringResource(R.string.article_favourite_error)

    LaunchedEffect(article?.id, contentBlocks, bodyExtendsBodys) {
        val currentArticle = article ?: return@LaunchedEffect
        if (currentArticle.id <= 0 || restoredArticleId == currentArticle.id) {
            restoredArticleId = currentArticle.id
            return@LaunchedEffect
        }

        val itemCount = 2 + bodyExtendsBodys.sumOf { 1 + it.body.size } + contentBlocks.size
        snapshotFlow { state.layoutInfo.totalItemsCount }
            .first { it >= itemCount && it > 0 }

        val savedPosition = viewModel.savedReadingPosition(currentArticle.id)
        val targetIndex = savedPosition?.resumeItemIndex?.coerceIn(0, itemCount - 1) ?: 0
        val targetOffset = savedPosition?.resumeItemOffsetPx?.coerceAtLeast(0) ?: 0
        state.scrollToItem(targetIndex, targetOffset)
        restoredArticleId = currentArticle.id
    }

    LaunchedEffect(article?.id, restoredArticleId) {
        val currentArticle = article ?: return@LaunchedEffect
        if (currentArticle.id <= 0 || restoredArticleId != currentArticle.id) {
            return@LaunchedEffect
        }

        viewModel.saveReadingProgress(
            article = currentArticle,
            progress = state.readingProgress(),
            itemIndex = state.firstVisibleItemIndex,
            itemOffsetPx = state.firstVisibleItemScrollOffset
        )

        snapshotFlow {
            Triple(
                state.firstVisibleItemIndex,
                state.firstVisibleItemScrollOffset,
                state.readingProgress()
            )
        }
            .distinctUntilChanged()
            .debounce(700)
            .collect { (itemIndex, itemOffsetPx, progress) ->
                viewModel.saveReadingProgress(
                    article = currentArticle,
                    progress = progress,
                    itemIndex = itemIndex,
                    itemOffsetPx = itemOffsetPx
                )
            }
    }

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
    val innerOpen by setting.observe(SettingData.urlIsOpenInner)
        .collectAsStateWithLifecycle(initialValue = true)
    val urlHandler = customUriHandler(urlIsInnerOpen = innerOpen, onInnerOpen = onLinkClick)

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
                val showTitle by remember(state) {
                    derivedStateOf { state.firstVisibleItemIndex > 0 }
                }
                DetailReadingToolBar(
                    title = article?.title,
                    titleShowable = showTitle,
                    // Includes scrolling within the first item, and becomes false again at the top.
                    showProgressBar = state.canScrollBackward,
                    // Defer the per-pixel state read to the progress indicator.
                    progress = { state.readingProgress() },
                    scrollBehavior = scrollBehavior,
                    onNavBack = onNavBack
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
                                putExtra(
                                    Intent.EXTRA_TEXT,
                                    detailArticleUrl(viewModel.id, article.id)
                                )
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
                val publishedDate = remember(it.releasedTime, it.pubDate) {
                    if (it.releasedTime > 0) {
                        DateTimeFormatter.ofPattern("MM月dd日", Locale.CHINESE)
                            .withZone(ZoneId.systemDefault())
                            .format(Instant.ofEpochSecond(it.releasedTime.toLong()))
                    } else {
                        it.pubDate
                    }
                }
                val tags = remember(it.tags) { it.tags.orEmpty().map { tag -> tag.title } }
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    CompositionLocalProvider(LocalUriHandler provides urlHandler) {
                        DetailScreenContent(
                            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                            state = state,
                            title = it.title,
                            author = it.author,
                            publishedDate = publishedDate,
                            tags = tags,
                            blocks = contentBlocks,
                            extendsBodys = bodyExtendsBodys,
                            bottomPadding = padding.calculateBottomPadding(),
                            onAvatarClick = onAvatarClick,
                            onTagClick = onTagClick,
                            onImageClick = { selectedImageUrl = it }
                        )
                    }
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
            },
            onImageClick = { selectedImageUrl = it }
        )
    }
    selectedImageUrl?.let { url ->
        key(url) {
            ArticleImageDialog(url = url, onDismiss = { selectedImageUrl = null })
        }
    }
}
