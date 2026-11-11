package com.shihcheeng.sspaiyou.ui.screen.detail

import androidx.compose.foundation.lazy.LazyListState

/**
 * Current reading position in 0..1, including the article header and extension blocks.
 * LazyColumn only measures nearby items, so Compose estimates the scroll range for mixed heights.
 * Use the actual scroll boundaries for the endpoints, including short/empty articles at the top.
 */
internal fun LazyListState.readingProgress(): Float {
    if (layoutInfo.totalItemsCount == 0 || !canScrollBackward) return 0f
    if (!canScrollForward) return 1f

    val indicator = scrollIndicatorState ?: return 0f
    val offset = indicator.scrollOffset
    val contentSize = indicator.contentSize
    val viewportSize = indicator.viewportSize
    if (offset == Int.MAX_VALUE || contentSize == Int.MAX_VALUE || viewportSize == Int.MAX_VALUE) {
        return 0f
    }
    val scrollRange = contentSize - viewportSize
    if (scrollRange <= 0) return 0f

    return (offset.toFloat() / scrollRange).coerceIn(0f, 1f)
}
