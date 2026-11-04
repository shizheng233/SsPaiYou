package com.shihcheeng.sspaiyou.ui.screen.main.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import androidx.paging.compose.itemContentType
import com.shihcheeng.sspaiyou.ui.compoents.ErrorItem
import com.shihcheeng.sspaiyou.ui.compoents.Loading
import com.shihcheeng.sspaiyou.ui.compoents.LoadingItem
import com.shihcheeng.sspaiyou.ui.compoents.bannerz.BannerCarousel
import com.shihcheeng.sspaiyou.ui.compoents.tristate.pagingTriState
import com.shihcheeng.sspaiyou.ui.compoents.tristate.triStateItem

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel(),
    onClick: (String) -> Unit
) {
    val data = viewModel.data.collectAsLazyPagingItems()
    val banner by viewModel.banner.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        triStateItem(
            triState = banner,
            onLoading = {
                Loading(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(360f / 274f)
                )
            },
            onError = {
                ErrorItem(
                    error = it.localizedMessage,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(360f / 274f)
                ) {
                    viewModel.loadBanner()
                }
            },
            onSuccess = {
                item(
                    key = HomeScreenKey.BANNER,
                    contentType = HomeScreenKey.BANNER
                ) {
                    BannerCarousel(items = it) { banner ->
                        BannerItem(
                            image = banner.image,
                            title = banner.title,
                            onlyImage = banner.onlyImage
                        ) {
                            banner.url.detectiveUrlTypeOp { bannerType, s ->
                                if (bannerType == BannerType.POST) {
                                    onClick(s)
                                }
                            }
                        }
                    }
                }
            }
        )
        pagingTriState(
            paging = data.loadState.source.refresh,
            onLoading = {
                LoadingItem()
            },
            onError = {
                ErrorItem(error = it.localizedMessage) {
                    data.refresh()
                }
            },
            onNotLoading = {
                items(
                    count = data.itemCount,
                    key = data.itemKey { it.id },
                    contentType = data.itemContentType {
                        when {
                            it.important == 2 -> HomeScreenKey.SUPER_ITEM
                            it.morningPaperTitle.isNotEmpty() -> HomeScreenKey.PAPER_ITEM
                            else -> HomeScreenKey.ITEM
                        }
                    }
                ) { ii ->
                    data[ii]?.let {
                        when (it.important) {
                            2 -> {
                                HomeItemLarge(
                                    image = it.banner,
                                    timeColor = it.themeColor,
                                    author = it.author.nickname,
                                    avatar = it.author.avatar,
                                    title = it.title,
                                    summary = it.summary,
                                    timeStamp = it.releasedTime.toLong(),
                                    commentCount = it.commentCount,
                                    thunderCount = it.likeCount
                                ) {
                                    if (!it.free && it.slug.isNotEmpty() && it.slug.isNotBlank()) {
                                        onClick(it.slug)
                                    } else {
                                        onClick(it.id.toString())
                                    }
                                }
                            }

                            else -> {
                                if (it.morningPaperTitle.isNotEmpty()) {
                                    HomePaperItem(
                                        timeSource = it.releasedTime.toLong(),
                                        list = it.morningPaperTitle,
                                        onClick = {
                                            onClick(it.id.toString())
                                        }
                                    )
                                } else {
                                    HomeItemAlt(
                                        image = it.banner,
                                        title = it.title,
                                        timeStamp = it.releasedTime.toLong()
                                    ) {
                                        if (!it.free && it.slug.isNotEmpty() && it.slug.isNotBlank()) {
                                            onClick(it.slug)
                                        } else {
                                            onClick(it.id.toString())
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        )
        pagingTriState(
            paging = data.loadState.append,
            onError = {
                ErrorItem(error = it.localizedMessage) {
                    data.retry()
                }
            },
            onLoading = {
                LoadingItem()
            }
        ) {

        }
    }

}

private fun String.detectiveUrlTypeOp(
    onEach: (BannerType, String) -> Unit
) {
    val ii = this.split("/")
    val typeString = ii[ii.lastIndex - 1]
    onEach(
        when (typeString) {
            "post" -> BannerType.POST
            "topic" -> BannerType.TOPIC
            "series" -> BannerType.SERIES
            else -> BannerType.OTHER
        },
        ii[ii.lastIndex]
    )
}
