package com.shihcheeng.sspaiyou.ui.screen.main.personal

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.shihcheeng.sspaiyou.R

sealed interface PersonalItem {
    @get:StringRes
    val resId: Int

    @get:DrawableRes
    val drawable: Int
    val tag: String

    companion object {
        @Composable
        fun createListInCompose() = remember {
            listOf(
                PersonalItemFav,
                PersonalItemSetting
            )
        }
    }
}

data object PersonalItemFav : PersonalItem {
    override val resId: Int
        get() = R.string.fav
    override val drawable: Int
        get() = R.drawable.baseline_bookmark_border_24
    override val tag: String
        get() = "TAG_FAVOURITE_ITEM"
}

data object PersonalItemSetting : PersonalItem {
    override val resId: Int
        get() = R.string.setting
    override val drawable: Int
        get() = R.drawable.outline_settings_24
    override val tag: String
        get() = "TAG_SETTING_ITEM"
}

data object PersonalItemLogOut : PersonalItem {
    override val resId: Int
        get() = R.string.log_out
    override val drawable: Int
        get() = R.drawable.baseline_logout_24
    override val tag: String
        get() = "TAG_LOGOUT_ITEM"
}