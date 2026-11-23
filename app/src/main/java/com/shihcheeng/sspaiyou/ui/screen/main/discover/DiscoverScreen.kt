package com.shihcheeng.sspaiyou.ui.screen.main.discover

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.shihcheeng.sspaiyou.ui.screen.main.discover.followactivity.FollowActivityScreen
import com.shihcheeng.sspaiyou.ui.screen.main.discover.hot.HotScreen
import kotlinx.coroutines.launch

@Composable
fun DiscoverScreen(
    onArticleClick: (String) -> Unit,
    onUserClick: (String) -> Unit
) {

    val items = DiscoverTabItem.getDiscoverTabItems()

    val state = rememberPagerState(pageCount = items::size)
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        PrimaryScrollableTabRow(
            selectedTabIndex = state.currentPage,
            modifier = Modifier.fillMaxWidth(),
            edgePadding = 4.dp
        ) {
            items.forEachIndexed { index, item ->
                Tab(
                    selected = index == state.currentPage,
                    onClick = {
                        scope.launch {
                            state.animateScrollToPage(page = index)
                        }
                    },
                    text = {
                        Text(text = stringResource(item.stringID))
                    }
                )
            }
        }
        HorizontalPager(
            state = state,
            modifier = Modifier
                .fillMaxWidth()
                .weight(weight = 1f),
        ) { index ->
            when (items[index].discoverItemType) {
                DiscoverItemType.HOT -> HotScreen(
                    onArticleClick = onArticleClick,
                    onUserClick = onUserClick,
                )

                DiscoverItemType.FOLLOW -> FollowActivityScreen(
                    onArticleId = onArticleClick,
                    onUserClick = onUserClick
                )
            }
        }
    }
}
