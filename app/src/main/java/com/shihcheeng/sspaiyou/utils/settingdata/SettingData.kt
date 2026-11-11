package com.shihcheeng.sspaiyou.utils.settingdata

import dev.expressive.settings.booleanSetting
import dev.expressive.settings.enumSetting
import dev.expressive.settings.floatSetting

object SettingData {
    val urlIsOpenInner = booleanSetting(
        id = SettingKeys.KEY_OF_URL_IS_OPEN_INNER,
        default = true
    )
    val dynamicColor = booleanSetting(id = SettingKeys.DYNAMIC_COLOR, true)
    val themeMode = enumSetting(id = SettingKeys.THEME_MODE, SettingKeys.ThemeMode.System)
    val readerScale = floatSetting(id = SettingKeys.KEY_OF_READER_SCALE, 1.15f)
    val imageCacheSize = floatSetting(
        id = SettingKeys.KEY_OF_IMAGE_CACHE_SIZE,
        default = 256f
    )
}
