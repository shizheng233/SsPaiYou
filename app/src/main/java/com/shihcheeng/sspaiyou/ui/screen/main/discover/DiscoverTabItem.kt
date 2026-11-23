package com.shihcheeng.sspaiyou.ui.screen.main.discover

import androidx.annotation.StringRes
import com.shihcheeng.sspaiyou.R

data class DiscoverTabItem(
    @StringRes
    val stringID: Int,
    val discoverItemType: DiscoverItemType
) {

    companion object {
        fun getDiscoverTabItems() = listOf(
            DiscoverTabItem(
                stringID = R.string.hot,
                discoverItemType = DiscoverItemType.HOT
            ),
            DiscoverTabItem(
                stringID = R.string.followed_user,
                discoverItemType = DiscoverItemType.FOLLOW
            )
        )
    }

}

enum class DiscoverItemType {
    HOT, FOLLOW
}