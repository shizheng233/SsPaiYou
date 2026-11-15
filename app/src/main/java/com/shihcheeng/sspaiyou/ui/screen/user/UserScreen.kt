package com.shihcheeng.sspaiyou.ui.screen.user

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import com.shihcheeng.sspaiyou.R
import com.shihcheeng.sspaiyou.ui.compoents.Error
import com.shihcheeng.sspaiyou.ui.compoents.WithTooltipBox
import com.shihcheeng.sspaiyou.ui.compoents.tristate.TriStateScreen
import soup.compose.material.motion.animation.materialFadeIn
import soup.compose.material.motion.animation.materialFadeOut

@Composable
fun UserScreen(
    viewModel: UserViewModel = androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel(),
    onNavUp: () -> Unit,
    onArticleClick: (String) -> Unit,
    onUserClick: (String) -> Unit
) {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    val data = viewModel.data.collectAsLazyPagingItems()
    // Do not request favourites until their tab is opened; cachedIn retains loaded pages.
    val favourites = if (selectedTab == 1) viewModel.favourites.collectAsLazyPagingItems() else null
    val user by viewModel.dataOfUser.collectAsStateWithLifecycle()
    val currentUserSlug by viewModel.currentUserSlug.collectAsStateWithLifecycle()
    val token by viewModel.token.collectAsStateWithLifecycle()
    val followPending by viewModel.followPending.collectAsStateWithLifecycle()
    val followError by viewModel.followError.collectAsStateWithLifecycle()
    val context = LocalContext.current
    LaunchedEffect(followError) {
        if (followError) {
            Toast.makeText(context, R.string.user_follow_error, Toast.LENGTH_SHORT).show()
            viewModel.clearFollowError()
        }
    }
    val activityList = rememberLazyListState()
    val favouriteList = rememberLazyListState()
    val list = if (selectedTab == 0) activityList else favouriteList
    val title by viewModel.title.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    val showTitle by remember(list) { derivedStateOf { list.firstVisibleItemIndex > 0 } }
                    AnimatedVisibility(
                        visible = showTitle,
                        enter = materialFadeIn(),
                        exit = materialFadeOut()
                    ) {
                        Text(text = title, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                },
                navigationIcon = {
                    WithTooltipBox(
                        tooltipText = stringResource(R.string.back_to_up)
                    ) {
                        IconButton(
                            onClick = onNavUp
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.baseline_arrow_back_24),
                                contentDescription = stringResource(R.string.back_to_up)
                            )
                        }
                    }
                }
            )
        }
    ) { padding ->
        TriStateScreen(
            padding = PaddingValues(
                top = padding.calculateTopPadding(),
                bottom = 0.dp
            ),
            onError = {
                Error(error = it.localizedMessage, onClick = viewModel::load)
            },
            triState = user,
        ) {
            UserScreenContent(
                contentPaddingValues = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    top = 8.dp,
                    bottom = 24.dp + padding.calculateBottomPadding()
                ),
                listState = list,
                data = data,
                favourites = favourites,
                selectedTab = selectedTab,
                onTabSelected = { x ->
                    selectedTab = x
                },
                followed = it.data.followed,
                color = it.data.userBadges
                    .takeIf { x ->
                        x.isNotEmpty()
                    }?.get(0)?.color
                    ?.takeIf { x ->
                        x.isNotEmpty()
                    },
                onFollowedChange = viewModel::setFollowed,
                followEnabled = !token.isNullOrBlank() && it.data.slug !in followPending,
                username = it.data.nickname,
                bio = it.data.bio,
                followingCount = it.data.followingCount,
                beFollowedCount = it.data.followedCount,
                imageUrl = it.data.avatar,
                rewardBadge = it.data.userRewardBadges,
                badge = it.data.userBadges,
                userFlag = it.data.userFlags,
                releasedArticleCount = it.data.releasedArticleCount,
                articleViewCount = it.data.articleViewCount,
                likeCount = it.data.likedCount,
                haveBeenSSPai = it.data.createdAt,
                isMine = !currentUserSlug.isNullOrBlank() && currentUserSlug == it.data.slug,
                onArticleClick = onArticleClick,
                onUserClick = onUserClick
            )
        }
    }

}
