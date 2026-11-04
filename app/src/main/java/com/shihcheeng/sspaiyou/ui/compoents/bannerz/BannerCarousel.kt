package com.shihcheeng.sspaiyou.ui.compoents.bannerz

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.time.Duration.Companion.milliseconds

/**
 * 基于 [HorizontalPager] 的全宽 Banner 轮播组件，右上角点状指示器，支持自动轮播。
 *
 * 内部通过超大 pageCount 实现无限循环，自动轮播到末尾时不会出现回滚动画。
 *
 * @param items 轮播数据
 * @param autoPlay 是否自动轮播（用户拖动时暂停当前这一拍）
 * @param autoPlayIntervalMillis 自动轮播间隔
 * @param showIndicator 是否显示右上角点状指示器（单页时自动隐藏）
 * @param itemContent 每一页的内容，通常使用 BannerItem
 */
@Composable
fun <T> BannerCarousel(
    items: List<T>,
    modifier: Modifier = Modifier,
    autoPlay: Boolean = true,
    autoPlayIntervalMillis: Long = 4000L,
    showIndicator: Boolean = true,
    itemContent: @Composable (T) -> Unit
) {
    if (items.isEmpty()) return

    val loop = items.size > 1
    // 起始页对齐到 items.size 的整数倍，保证 currentPage % size 始终映射到正确的数据下标
    val startPage = if (loop) Int.MAX_VALUE / 2 - (Int.MAX_VALUE / 2) % items.size else 0
    val pagerState = rememberPagerState(
        initialPage = startPage,
        pageCount = { if (loop) Int.MAX_VALUE else 1 }
    )

    if (autoPlay && loop) {
        LaunchedEffect(items.size, autoPlayIntervalMillis) {
            while (isActive) {
                delay(autoPlayIntervalMillis.milliseconds)
                if (!pagerState.isScrollInProgress) {
                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                }
            }
        }
    }

    Box(modifier = modifier.fillMaxWidth()) {
        HorizontalPager(state = pagerState) { page ->
            itemContent(items[page % items.size])
        }

        if (showIndicator && loop) {
            BannerDotsIndicator(
                pageCount = items.size,
                currentPage = pagerState.currentPage % items.size,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 12.dp, end = 16.dp)
            )
        }
    }
}

/**
 * 点状页码指示器，选中点为加长胶囊，未选中为半透明圆点。
 */
@Composable
fun BannerDotsIndicator(
    pageCount: Int,
    currentPage: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(pageCount) { index ->
            val selected = index == currentPage
            val width by animateDpAsState(
                targetValue = if (selected) 16.dp else 6.dp,
                animationSpec = tween(durationMillis = 300),
                label = "banner_dot_width"
            )
            Box(
                modifier = Modifier
                    .width(width)
                    .height(6.dp)
                    .clip(CircleShape)
                    .background(
                        if (selected) Color.White
                        else Color.White.copy(alpha = 0.4f)
                    )
            )
        }
    }
}
