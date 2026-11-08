package com.shihcheeng.sspaiyou.utils.settingdata

import com.shihcheeng.sspaiyou.R

data object SettingKeys {
    const val DYNAMIC_COLOR = "DYNAMIC_COLOR"
    const val THEME_MODE = "THEME_MODE"
    const val KEY_OF_READER_SCALE = "KEY_OF_READER_SCALE"

    enum class ThemeMode(val label: Int) {
        System(R.string.follow_system),
        Light(R.string.light),
        Dark(R.string.dark),
    }
}