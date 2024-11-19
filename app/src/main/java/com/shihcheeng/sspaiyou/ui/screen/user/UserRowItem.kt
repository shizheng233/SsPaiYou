package com.shihcheeng.sspaiyou.ui.screen.user

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.shihcheeng.sspaiyou.R

sealed interface UserRowItem {
    @get:StringRes
    val item: Int
    val index: UInt

    companion object {

        @Composable
        fun createItemOfUser() = remember {
            listOf(
                ActivityItem,
                CreatorItem,
                FavouriteItem
            )
        }

    }
}

data object ActivityItem : UserRowItem {
    override val item: Int
        get() = R.string.activity_of_user
    override val index: UInt
        get() = 0u
}

data object CreatorItem : UserRowItem {
    override val item: Int
        get() = R.string.created_of_user
    override val index: UInt
        get() = 1u
}

data object FavouriteItem : UserRowItem {
    override val item: Int
        get() = R.string.favourite_of_user
    override val index: UInt
        get() = 2u
}