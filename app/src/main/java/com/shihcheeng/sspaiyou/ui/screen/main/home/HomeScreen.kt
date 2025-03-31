package com.shihcheeng.sspaiyou.ui.screen.main.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.carousel.CarouselDefaults
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import com.shihcheeng.sspaiyou.ui.compoents.ErrorItem
import com.shihcheeng.sspaiyou.ui.compoents.Loading
import com.shihcheeng.sspaiyou.ui.compoents.LoadingItem
import com.shihcheeng.sspaiyou.ui.compoents.tristate.pagingTriState
import com.shihcheeng.sspaiyou.ui.compoents.tristate.triStateItem

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onClick: (String) -> Unit
) {
    val data = viewModel.data.collectAsLazyPagingItems()
    val banner by viewModel.banner.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        triStateItem(
            triState = banner,
            onLoading = {
                Loading(
                    modifier = Modifier
                        .width(CarouselWidth)
                        .aspectRatio(11f / 6f)
                )
            },
            onError = {
                ErrorItem(
                    error = it.localizedMessage,
                    modifier = Modifier
                        .width(CarouselWidth)
                        .aspectRatio(11f / 6f)
                ) {
                    viewModel.loadBanner()
                }
            },
            onSuccess = {
                item(key = HomeScreenKey.BANNER) {
                    val state = rememberCarouselState { it.size }
                    HorizontalMultiBrowseCarousel(
                        state = state,
                        modifier = Modifier,
                        preferredItemWidth = CarouselWidth,
                        itemSpacing = 8.dp,
                        flingBehavior = CarouselDefaults.singleAdvanceFlingBehavior(state = state)
                    ) { index ->
                        BannerItem(
                            image = it[index].image,
                            title = it[index].title,
                            shape = rememberMaskShape(MaterialTheme.shapes.medium),
                        ) {
                            it[index].url.detectiveUrlTypeOp { bannerType, s ->
                                if (bannerType == BannerType.POST) {
                                    onClick(s)
                                }
                            }
                        }
                    }
                }
            }
        )
        stickyHeader(
            key = "KEY_THE_ROW_"
        ) {
            PrimaryScrollableTabRow(
                selectedTabIndex = 0
            ) {
                listOf("a", "b", "c").forEach {
                    Tab(
                        selected = false,
                        text = {
                            Text(text = it)
                        },
                        onClick = {

                        }
                    )
                }
            }
        }
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
                items(data.itemCount) { ii ->
                    data[ii]?.let {
                        when (it.important) {
                            2 -> {
                                HomeItemSpc(
                                    image = it.banner,
                                    author = it.author.nickname,
                                    avatar = it.author.avatar,
                                    title = it.title,
                                    summary = it.summary,
                                    timeStamp = it.releasedTime.toLong()
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
                                    HomeItem(
                                        image = it.banner,
                                        title = it.title,
                                        author = it.author.nickname,
                                        avatar = it.author.avatar,
                                        summary = it.summary,
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

val CarouselWidth = 340.dp

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