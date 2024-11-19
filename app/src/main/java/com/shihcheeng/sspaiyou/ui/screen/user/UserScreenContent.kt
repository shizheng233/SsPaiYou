package com.shihcheeng.sspaiyou.ui.screen.user

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import com.materialkolor.rememberDynamicColorScheme
import com.shihcheeng.sspaiyou.R
import com.shihcheeng.sspaiyou.api.data.user.activity.ActivityData
import android.graphics.Color as xmlColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserScreenContent(
    contentPaddingValues: PaddingValues,
    listState: LazyListState,
    data: LazyPagingItems<ActivityData /* = Data */>,
    followed: Boolean,
    color: String?,
    onFollowedChange: (Boolean) -> Unit,
    username: String,
    bio: String,
    followingCount: Int,
    beFollowedCount: Int
) {

    val primary = color?.let {
        rememberDynamicColorScheme(
            isDark = isSystemInDarkTheme(),
            seedColor = Color(xmlColor.parseColor(color)),
            primary = MaterialTheme.colorScheme.primary,
            isAmoled = false,
        )
    } ?: MaterialTheme.colorScheme
    val selectItems = UserRowItem.createItemOfUser()
    var selectedItem by rememberSaveable { mutableIntStateOf(0) }

    LazyColumn(
        contentPadding = contentPaddingValues,
        state = listState
    ) {
        item {
            HeaderOfUserDetail(
                followed = followed,
                primary = primary,
                onFollowedChange = onFollowedChange,
                username = username,
                bio = bio,
                followingCount = followingCount,
                beFollowedCount = beFollowedCount
            )
        }
        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .horizontalScroll(state = rememberScrollState())
            ) {
                UserTwoColumnItem(
                    leadingImage = "",
                    end = false,
                    topText = stringResource(R.string.user_reward_badges),
                    bottomText = 1.toString()
                )
                UserTwoColumnItem(
                    end = false,
                    topText = stringResource(R.string.released_article_count),
                    bottomText = "2344"
                )
                UserTwoColumnItem(
                    end = false,
                    topText = stringResource(R.string.article_view_count),
                    bottomText = "123"
                )
                UserTwoColumnItem(
                    end = false,
                    topText = stringResource(R.string.liked_count),
                    bottomText = "2344"
                )
                UserTwoColumnItem(
                    end = true,
                    topText = stringResource(R.string.had_been_sspai_spc),
                    bottomText = "1234天"
                )
            }
        }
        item {
            HorizontalDivider()
        }
        item {
            PrimaryTabRow(
                selectedTabIndex = 0,
                divider = { }
            ) {
                selectItems.forEach {
                    Tab(
                        selected = false,
                        onClick = {
                            selectedItem = it.index.toInt()
                        },
                        text = {
                            Text(text = stringResource(it.item))
                        }
                    )
                }
            }
        }
        items(data.itemCount) { innerIndex ->
            data[innerIndex]?.let { data ->
                when (data.objectType) {
                    1 -> {
                        UserActivityItem(
                            modifier = Modifier.padding(vertical = 16.dp),
                            imageUrl = data.author.avatar,
                            actionString = data.action,
                            type = stringResource(R.string.type_obj_1),
                            title = data.data.title,
                            onCardClick = {

                            },
                            banner = data.data.banner.takeIf { x -> x.isNotEmpty() && x.isNotBlank() }
                        )
                    }
                }
            }
        }
    }
}

