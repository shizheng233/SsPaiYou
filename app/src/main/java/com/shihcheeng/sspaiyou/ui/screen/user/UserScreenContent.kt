package com.shihcheeng.sspaiyou.ui.screen.user

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.materialkolor.material3.rememberDynamicColorScheme
import com.shihcheeng.sspaiyou.R
import com.shihcheeng.sspaiyou.api.data.column.ColumnArticle
import com.shihcheeng.sspaiyou.api.data.user.activity.datadtos.DataActivityArticle
import com.shihcheeng.sspaiyou.api.data.user.activity.datadtos.DataActivityAuthors
import com.shihcheeng.sspaiyou.api.data.user.activity.datadtos.DataActivityColumns
import com.shihcheeng.sspaiyou.api.data.user.activity.datadtos.DataActivityComment
import com.shihcheeng.sspaiyou.api.data.user.activity.datadtos.DataActivityCommentAndReply
import com.shihcheeng.sspaiyou.api.data.user.activity.datadtos.DataActivityPrimeArticle
import com.shihcheeng.sspaiyou.api.data.user.activity.datadtos.DataActivityTopicComment
import com.shihcheeng.sspaiyou.api.data.user.activity.datadtos.IBaseData
import com.shihcheeng.sspaiyou.api.data.user.third.ThirdUserFlag
import com.shihcheeng.sspaiyou.api.data.user.third.UserBadge
import com.shihcheeng.sspaiyou.api.data.user.third.UserRewardBadge
import com.shihcheeng.sspaiyou.ui.compoents.ErrorItem
import com.shihcheeng.sspaiyou.ui.compoents.LoadingItem
import com.shihcheeng.sspaiyou.ui.compoents.tristate.pagingTriState
import com.shihcheeng.sspaiyou.ui.screen.favourite.favouriteItems
import com.shihcheeng.sspaiyou.ui.screen.user.activityitems.UserActionComment
import com.shihcheeng.sspaiyou.ui.screen.user.activityitems.UserActionFollowUser
import com.shihcheeng.sspaiyou.ui.screen.user.activityitems.UserActionTopicComment
import com.shihcheeng.sspaiyou.ui.screen.user.activityitems.UserActivityItem
import com.shihcheeng.sspaiyou.ui.screen.user.activityitems.UserActivityNormalItem
import com.shihcheeng.sspaiyou.utils.extra.formatWithUnit
import com.shihcheeng.sspaiyou.utils.extra.untilToDay
import com.shihcheeng.sspaiyou.utils.icons.BoxesLinked
import com.shihcheeng.sspaiyou.utils.icons.ListVideo
import com.shihcheeng.sspaiyou.utils.icons.SSPaiPack

@Composable
fun UserScreenContent(
    contentPaddingValues: PaddingValues,
    listState: LazyListState,
    data: LazyPagingItems<IBaseData>,
    favourites: LazyPagingItems<ColumnArticle>?,
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    followed: Boolean,
    color: String?,
    onFollowedChange: (Boolean) -> Unit,
    username: String,
    bio: String,
    followingCount: Int,
    beFollowedCount: Int,
    imageUrl: String,
    rewardBadge: List<UserRewardBadge>,
    badge: List<UserBadge>,
    userFlag: List<ThirdUserFlag>,
    releasedArticleCount: Int,
    articleViewCount: Int,
    likeCount: Int,
    haveBeenSSPai: Int,
    isMine: Boolean,
    onArticleClick: (String) -> Unit,
    onUserClick: (String) -> Unit
) {

    val scheme = MaterialTheme.colorScheme
    val seed = remember(color) {
        runCatching {
            color?.takeIf(String::isNotBlank)?.toColorInt()?.let { Color(it) }
        }.getOrNull()
    }
    val primary = seed?.let {
        rememberDynamicColorScheme(
            isDark = scheme.surface.luminance() < scheme.onSurface.luminance(),
            seedColor = it,
            isAmoled = false,
        )
    } ?: MaterialTheme.colorScheme
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = contentPaddingValues,
        state = listState,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        item(key = "profile", contentType = "profile") {
            HeaderOfUserDetail(
                followed = followed,
                primary = primary,
                onFollowedChange = onFollowedChange,
                username = username,
                bio = bio,
                followingCount = followingCount,
                beFollowedCount = beFollowedCount,
                imageUrl = imageUrl,
                userFlag = userFlag,
                isMine = isMine
            )
        }
        item(key = "statistics", contentType = "statistics") {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .horizontalScroll(state = rememberScrollState())
            ) {
                UserTwoColumnItem(
                    leadingImage = rewardBadge.firstOrNull()?.icon ?: badge.firstOrNull()?.icon,
                    topText = stringResource(R.string.user_reward_badges),
                    bottomText = (rewardBadge.size + badge.size).toString()
                )
                if (releasedArticleCount > 0) {
                    UserTwoColumnItem(
                        topText = stringResource(R.string.released_article_count),
                        bottomText = releasedArticleCount.toString()
                    )
                }
                if (articleViewCount > 0) {
                    UserTwoColumnItem(
                        topText = stringResource(R.string.article_view_count),
                        bottomText = articleViewCount.toLong().formatWithUnit()
                    )
                }
                if (likeCount > 0) {
                    UserTwoColumnItem(
                        topText = stringResource(R.string.liked_count),
                        bottomText = likeCount.toString()
                    )
                }
                UserTwoColumnItem(
                    topText = stringResource(R.string.had_been_sspai_spc),
                    bottomText = stringResource(
                        R.string.some_day,
                        haveBeenSSPai.toLong().untilToDay()
                    )
                )
            }
        }
        item(key = "user_content_tabs", contentType = "tabs") {
            PrimaryTabRow(
                selectedTabIndex = selectedTab,
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { onTabSelected(0) },
                    text = { Text(stringResource(R.string.activity_of_user)) },
                    icon = {
                        Icon(
                            imageVector = SSPaiPack.ListVideo,
                            contentDescription = stringResource(R.string.activity_of_user)
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { onTabSelected(1) },
                    text = { Text(stringResource(R.string.favourite_of_user)) },
                    icon = {
                        Icon(
                            imageVector = SSPaiPack.BoxesLinked,
                            contentDescription = stringResource(R.string.favourite_of_user)
                        )
                    }
                )
            }
        }
        item(
            key = "spacer_support_1"
        ) {
            Spacer(Modifier.height(8.dp))
        }
        if (selectedTab == 1) {
            favourites?.let {
                favouriteItems(it, onArticleClick, onUserClick)
            }
            return@LazyColumn
        }
        if (data.itemCount == 0) {
            pagingTriState(
                paging = data.loadState.refresh,
                onLoading = { LoadingItem() },
                onError = { ErrorItem(error = it.localizedMessage, onClick = data::retry) }
            ) {
                item(key = "empty_activity", contentType = "empty") {
                    Surface(
                        shape = MaterialTheme.shapes.extraLarge,
                        color = MaterialTheme.colorScheme.surfaceContainerLow,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            stringResource(R.string.user_activity_empty),
                            modifier = Modifier.padding(24.dp),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
        items(
            count = data.itemCount,
            key = data.itemKey { item ->
                val id = when (item) {
                    is DataActivityArticle -> item.id
                    is DataActivityAuthors -> item.id
                    is DataActivityColumns -> item.id
                    is DataActivityComment -> item.id
                    is DataActivityPrimeArticle -> item.id
                    is DataActivityCommentAndReply -> item.id
                    is DataActivityTopicComment -> item.id
                }
                "${item.javaClass.simpleName}:$id"
            },
            contentType = data.itemContentType { it.javaClass.name }
        ) { innerIndex ->
            val outer = MaterialTheme.shapes.extraLarge
            val inner = MaterialTheme.shapes.extraSmall
            val first = innerIndex == 0
            val last = innerIndex == data.itemCount - 1
            val shape = RoundedCornerShape(
                topStart = if (first) outer.topStart else inner.topStart,
                topEnd = if (first) outer.topEnd else inner.topEnd,
                bottomStart = if (last) outer.bottomStart else inner.bottomStart,
                bottomEnd = if (last) outer.bottomEnd else inner.bottomEnd
            )
            data[innerIndex]?.let { data ->
                Surface(
                    shape = shape,
                    color = MaterialTheme.colorScheme.surfaceContainerLow,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(Modifier.padding(16.dp)) {
                        when (data) {
                            is DataActivityArticle -> {
                                UserActivityItem(
                                    imageUrl = data.author.avatar,
                                    actionString = data.action
                                ) {
                                    UserActivityNormalItem(
                                        title = data.data.title,
                                        banner = data.data.banner.takeIf { x -> x.isNotEmpty() }
                                    ) {
                                        onArticleClick(data.data.id.toString())
                                    }
                                }
                            }

                            is DataActivityAuthors -> {
                                UserActivityItem(
                                    imageUrl = data.author.avatar,
                                    actionString = data.action
                                ) {
                                    data.data.forEach {
                                        UserActionFollowUser(
                                            otherUserAvatarUrl = it.avatar,
                                            name = it.nickname,
                                            description = it.bio
                                        )
                                    }
                                }
                            }

                            is DataActivityColumns -> {
                                UserActivityItem(
                                    imageUrl = data.author.avatar,
                                    actionString = data.action
                                ) {
                                    data.data.forEach {
                                        UserActionFollowUser(
                                            otherUserAvatarUrl = it.banner,
                                            name = it.title,
                                            description = it.intro
                                        )
                                    }
                                }
                            }

                            is DataActivityComment -> {
                                UserActionComment(
                                    avatarUrl = data.author.avatar,
                                    actionString = data.action,
                                    commentString = data.data.comment,
                                    title = data.data.articleTitle,
                                    banner = data.data.artcileBanner,
                                ) { onArticleClick(data.data.articleId.toString()) }
                            }

                            is DataActivityPrimeArticle -> UserActivityItem(
                                imageUrl = data.author.avatar,
                                actionString = data.action
                            ) {
                                Text(
                                    data.data.topic.title,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                if (data.data.topic.summary.isNotBlank()) {
                                    Text(
                                        data.data.topic.summary,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }

                            is DataActivityCommentAndReply -> UserActionTopicComment(
                                avatarUrl = data.author.avatar,
                                actionString = data.action,
                                commentString = data.data.commentReply.body,
                                replyToString = data.data.commentReplyTo.body.ifBlank {
                                    data.data.comment.body
                                },
                                topicTitle = data.data.topic.title,
                                statusTitle = data.data.status.title
                            )

                            is DataActivityTopicComment -> UserActionTopicComment(
                                avatarUrl = data.author.avatar,
                                actionString = data.action,
                                commentString = data.data.comment.body,
                                topicTitle = data.data.topic.title,
                                statusTitle = data.data.status.title
                            )
                        }
                    }
                }
            }
        }
        if (data.itemCount > 0) {
            pagingTriState(
                paging = data.loadState.append,
                onLoading = { LoadingItem() },
                onError = { ErrorItem(error = it.localizedMessage, onClick = data::retry) }
            ) {}
        }
    }
}

